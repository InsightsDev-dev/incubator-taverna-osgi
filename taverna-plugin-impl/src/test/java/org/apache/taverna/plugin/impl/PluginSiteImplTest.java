/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.taverna.plugin.impl;

import static org.junit.Assert.*;

import org.apache.taverna.plugin.PluginSite.PluginSiteType;
import org.apache.taverna.plugin.impl.PluginSiteImpl;
import org.junit.Before;
import org.junit.Test;

public class PluginSiteImplTest {

	PluginSiteImpl pluginSiteImpl;
	String name, url;
	PluginSiteType type;

	@Before
	public void setUp() throws Exception {
		name = "test name";
		url = "test url";
		type = PluginSiteType.SYSTEM;
		pluginSiteImpl = new PluginSiteImpl(name, url, type);
	}

	@Test
	public void testPluginSiteImpl() {
		pluginSiteImpl = new PluginSiteImpl();
	}

	@Test
	public void testPluginSiteImplStringStringPluginSiteType() {
		pluginSiteImpl = new PluginSiteImpl(null, null, null);
		pluginSiteImpl = new PluginSiteImpl("", "", PluginSiteType.USER);
	}

	@Test
	public void testGetName() {
		assertEquals(name, pluginSiteImpl.getName());
		assertEquals(name, pluginSiteImpl.getName());
	}

	@Test
	public void testSetName() {
		pluginSiteImpl.setName("name");
		assertEquals("name", pluginSiteImpl.getName());
	}

	@Test
	public void testGetUrl() {
		assertEquals(url, pluginSiteImpl.getUrl());
		assertEquals(url, pluginSiteImpl.getUrl());
	}

	@Test
	public void testSetUrl() {
		pluginSiteImpl.setName("http://www.example.com/");
		assertEquals("http://www.example.com/", pluginSiteImpl.getName());
	}

	@Test
	public void testGetType() {
		assertEquals(type, pluginSiteImpl.getType());
		assertEquals(type, pluginSiteImpl.getType());
	}

	@Test
	public void testSetType() {
		pluginSiteImpl.setType(PluginSiteType.USER);
		assertEquals(PluginSiteType.USER, pluginSiteImpl.getType());

	}

}
