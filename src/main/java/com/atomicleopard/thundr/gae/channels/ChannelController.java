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

import static com.atomicleopard.expressive.Expressive.map;

import javax.inject.Inject;

import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.session.Session;
import com.threewks.thundr.user.controller.Authenticated;
import com.threewks.thundr.user.gae.UserGae;
import com.threewks.thundr.view.Model;
import com.threewks.thundr.view.json.JsonView;

public class ChannelController {
	protected ChannelService channelService;
	protected boolean logChannelActivity = false;

	public ChannelController(ChannelService channelService) {
		super();
		this.channelService = channelService;
	}

	public void setLogChannelActivity(boolean logChannelActivity) {
		this.logChannelActivity = logChannelActivity;
	}

	public JsonView connected(String from) {
		if (logChannelActivity) {
			Logger.info("Channel connected to %s", from);
		}
		return new JsonView(new Model("success", true));
	}

	public JsonView disconnected(String from) {
		if (logChannelActivity) {
			Logger.info("Channel disconnected from %s", from);
		}
		channelService.removeChannel(from);
		return new JsonView(new Model("success", true));
	}

	@Authenticated
	public JsonView createToken(Session session, UserGae user) {
		String token = channelService.createChannel(user, session);
		if (logChannelActivity) {
			Logger.info("Channel token created for %s: %s", user.getUsername(), token);
		}
		return new JsonView(map("success", true, "token", token));
	}
}
