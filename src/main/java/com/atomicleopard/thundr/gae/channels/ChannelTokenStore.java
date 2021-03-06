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

import java.util.List;
import java.util.Map;

/**
 * This interface defines the API for managing channel tokens for users.
 * 
 * Read more about clientId and token <a href="https://cloud.google.com/appengine/docs/java/channel/">here</a>
 *
 */
public interface ChannelTokenStore {
	public void store(String username, String clientId);

	public List<String> list(String username);

	public Map<String, List<String>> list(List<String> usernames);

	public void clearAllFor(String username);

	public void clear(String clientId);
}
