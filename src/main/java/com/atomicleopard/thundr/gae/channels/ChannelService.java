/*
 * This file is a component of thundr-contrib-gae-channels, a software
 * library from Atomic Leopard.
 * Copyright (C) 2016 Atomic Leopard, <admin@atomicleopard.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.gae.channels;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.expressive.transform.CollectionTransformer;
import com.google.appengine.api.channel.ChannelMessage;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.request.InMemoryRequest;
import com.threewks.thundr.request.InMemoryResponse;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.session.SessionService;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.user.User;
import com.threewks.thundr.view.View;
import com.threewks.thundr.view.ViewResolverRegistry;
import com.threewks.thundr.view.json.JsonView;

/**
 * The ChannelService allows content to be sent to specific users connected
 * via a <a href="https://cloud.google.com/appengine/docs/java/channel/">GAE channel</a>.
 * 
 * A channel must be created for a user session by calling {@link #createChannel(User, Session)} or {@link #createChannel(User, Session, int)}.
 * 
 * After one or more channels exist, a user will receive the payload rendered by supplying a view to {@link #send(View, List)}.
 * Typically this would be a {@link JsonView}, but any other view type supported by your application will work.
 *
 * The {@link ChannelModule} will handle creation and removal of channel session for you by wiring up the {@link ChannelController}.
 */
public class ChannelService {
	private com.google.appengine.api.channel.ChannelService service;
	private ViewResolverRegistry viewResolverRegistry;
	private ChannelTokenStore channelTokenStore;
	private TransformerManager transformerManager;
	@SuppressWarnings("rawtypes")
	private SessionService sessionService;

	@SuppressWarnings("rawtypes")
	public ChannelService(com.google.appengine.api.channel.ChannelService service, ViewResolverRegistry viewResolverRegistry, ChannelTokenStore channelTokenStore,
			TransformerManager transformerManager, SessionService sessionService) {
		super();
		this.service = service;
		this.viewResolverRegistry = viewResolverRegistry;
		this.channelTokenStore = channelTokenStore;
		this.transformerManager = transformerManager;
		this.sessionService = sessionService;
	}

	public int send(View view, User... users) {
		return send(view, Arrays.asList(users));
	}

	public int send(View view, List<? extends User> users) {
		InMemoryRequest req = new InMemoryRequest();
		InMemoryResponse resp = new InMemoryResponse(transformerManager);
		viewResolverRegistry.resolve(req, resp, view);
		String output = resp.getBodyAsString();
		int count = 0;

		List<Session> sessions = findSessions(users);
		for (Session session : sessions) {
			String sessionId = session.getId().toString();
			try {
				service.sendMessage(new ChannelMessage(sessionId, output));
				count++;
			} catch (Exception e) {
				// do nothing
				e.printStackTrace();
				// TODO - v3 - How to clean up dead connections?
				// channelTokenStore.clear(sessionId);
			}
		}
		return count;
	}

	public <U extends User> String createChannel(U user, Session session) {
		UUID id = session.getId();
		return service.createChannel(id.toString());
	}

	public <U extends User> String createChannel(U user, Session session, int durationMinutes) {
		UUID id = session.getId();
		return service.createChannel(id.toString(), durationMinutes);
	}

	@SuppressWarnings("unchecked")
	protected <U extends User> List<Session> findSessions(List<U> users) {
		Map<U, List<Session>> sessions = sessionService.listSessions(users);
		return Expressive.flatten(sessions.values());
	}

	public List<String> listClientIds(List<String> usernames) {
		List<String> clientIds = ToClientIds.from(usernames);
		Map<String, List<String>> all = channelTokenStore.list(clientIds);
		return Expressive.flatten(all.values());
	}

	public List<String> listClientIds(String user) {
		return channelTokenStore.list(user);
	}

	private static final String limitTo64Bytes(String clientId) {
		try {
			byte[] bytes = clientId.getBytes("UTF-8");
			byte[] newBytes = Arrays.copyOfRange(bytes, Math.max(0, bytes.length - 64), bytes.length);
			return new String(newBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.warn("Unsupported encoding exception while determining clientId, making a best guess and continuing: %s", e.getMessage());
			return StringUtils.reverse(StringUtils.reverse(clientId).substring(0, 64));
		}
	}

	private static final CollectionTransformer<String, String> ToClientIds = Expressive.Transformers.transformAllUsing(new ETransformer<String, String>() {
		@Override
		public String from(String from) {
			return limitTo64Bytes(from);
		}
	});

	public void removeChannel(String clientId) {
		channelTokenStore.clear(clientId);

	}

}
