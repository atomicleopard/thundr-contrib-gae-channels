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

import com.threewks.thundr.view.json.JsonView;

/**
 * {@link ChannelEvent} is a structured object to help normalise
 * content sent via channel to the client. It simply is a categorised payload,
 * and will be serialised if wrapped in a {@link JsonView} (or other data view)
 * when {@link ChannelService#send(com.threewks.thundr.view.View, com.threewks.thundr.user.User...)} is called.
 */
public class ChannelEvent {
	protected String type;
	protected Object content;

	public ChannelEvent(String type, Object content) {
		super();
		this.type = type;
		this.content = content;
	}

}
