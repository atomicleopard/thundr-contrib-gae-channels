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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.expressive.transform.CollectionTransformer;

public class DatastoreChannelTokenStore implements ChannelTokenStore {

	@Override
	public void store(String username, String clientId) {
		ofy().save().entity(new ChannelToken(clientId, username)).now();
	}

	@Override
	public List<String> list(String username) {
		List<ChannelToken> all = ofy().load().type(ChannelToken.class).filter("username", username).list();
		return ToId.from(all);
	}

	@Override
	public Map<String, List<String>> list(List<String> usernames) {
		List<ChannelToken> all = ofy().load().type(ChannelToken.class).filter("username in", usernames).list();

		Map<String, List<ChannelToken>> lookup = ToUsernameLookup.from(all);
		Map<String, List<String>> result = new LinkedHashMap<>(lookup.size());
		for (Map.Entry<String, List<ChannelToken>> entry : lookup.entrySet()) {
			result.put(entry.getKey(), ToId.from(entry.getValue()));
		}
		return result;
	}

	@Override
	public void clearAllFor(String username) {
		List<ChannelToken> all = ofy().load().type(ChannelToken.class).filter("username", username).list();
		ofy().delete().entities(all).now();
	}

	@Override
	public void clear(String clientId) {
		ofy().delete().type(ChannelToken.class).id(clientId).now();
	}

	private static final CollectionTransformer<ChannelToken, String> ToUsername = Expressive.Transformers.transformAllUsing(Expressive.Transformers.<ChannelToken, String> toProperty("username",
			ChannelToken.class));
	private static final CollectionTransformer<ChannelToken, String> ToId = Expressive.Transformers.transformAllUsing(Expressive.Transformers.<ChannelToken, String> toProperty("id",
			ChannelToken.class));
	private static final ETransformer<Collection<ChannelToken>, Map<String, List<ChannelToken>>> ToUsernameLookup = Expressive.Transformers.toBeanLookup("username", ChannelToken.class);
}
