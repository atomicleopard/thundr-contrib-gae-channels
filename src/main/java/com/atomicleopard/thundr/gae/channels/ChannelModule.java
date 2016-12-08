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

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.ObjectifyService;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.route.Router;
import com.threewks.thundr.user.User;

/**
 * Sets up a thundr abstraction on <a href="https://cloud.google.com/appengine/docs/java/channel/">channels</a>
 * using thundr-user and thundr-gae-user.
 * 
 * Channels can be established by invoking <code>/channel/create</code>, after which payloads can be sent using the {@link ChannelService} to a specific {@link User}.
 *
 */
public class ChannelModule extends BaseModule {
	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		super.configure(injectionContext);
		injectionContext.inject(ChannelServiceFactory.getChannelService()).as(com.google.appengine.api.channel.ChannelService.class);
		injectionContext.inject(ChannelService.class).as(ChannelService.class);
		injectionContext.inject(DatastoreChannelTokenStore.class).as(ChannelTokenStore.class);
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {
		super.start(injectionContext);
		Router router = injectionContext.get(Router.class);
		router.post("/_ah/channel/connected/", ChannelController.class, "connected");
		router.post("/_ah/channel/disconnected/", ChannelController.class, "disconnected");
		router.post("/channel/create/", ChannelController.class, "createToken");

		ObjectifyService.register(ChannelToken.class);
	}

}
