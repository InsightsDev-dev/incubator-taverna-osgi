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
package org.apache.taverna.configuration.app.impl;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Find and create an application's user directory according to operating system
 * standards.
 * <p>
 * For example for the application "MyApp" this class will be able to find
 * <code>$HOME/.myapp</code> on Linux or
 * <code>C:\Document and settings\MyUsername\Application Data\MyApplication</code>
 * on Windows XP.
 *
 * @author Stian Soiland-Reyes
 * @author Stuart Owen
 *
 */
public class ApplicationUserHome {

	private final String defaultApplicationHome;

	private final String applicationName;

	private File homeDir;

	private static Logger logger = Logger.getLogger(ApplicationUserHome.class);

	/**
	 * Initialise with the name of the application.
	 *
	 * @param applicationName
	 *            This name will be used as a template for creating the
	 *            application home directory (but might be transcribed, for
	 *            instance to lowercase). It is generally recommended, but not
	 *            required - that this name does not contain spaces or any
	 *            special international/Unicode characters.
	 */
	public ApplicationUserHome(String applicationName) {
		this(applicationName, null);
	}

	/**
	 * Initialise with the name of the application and a default application
	 * home.
	 *
	 * @param applicationName
	 *            This name will be used as a template for creating the
	 *            application home directory (but might be transcribed, for
	 *            instance to lowercase). It is generally recommended, but not
	 *            required - that this name does not contain spaces or any
	 *            special international/Unicode characters.
	 * @param defaultApplicationHome
	 *            The full path to the default home directory. If this string is
	 *            not <code>null</code>, then a {@link File} based on this
	 *            directory will always be returned by
	 *            {@link #getDefaultApplicationHome()} - otherwise the normal
	 *            operating system logic is used to determine the application's
	 *            home directory.
	 */
	public ApplicationUserHome(String applicationName,
			String defaultApplicationHome) {
		this.applicationName = applicationName;
		this.defaultApplicationHome = defaultApplicationHome;
	}

	/**
	 * Find (and if necessary create) the user's application directory,
	 * according to operating system standards. The resolved directory is then
	 * returned as a {@link File} object.
	 * <p>
	 * The application's name as defined by {@link #getApplicationName()} is
	 * used as a basis for naming the directory of the application's user
	 * directory, but the directory name might for instance be transformed to
	 * lowercase.
	 * <p>
	 * If {@link #getDefaultApplicationHome()} returns a non-null value, the
	 * directory specified by that path will be used instead of the operation
	 * system specific directory. The directory will be created if needed.
	 * <p>
	 * If any exception occurs (such as out of diskspace), <code>null</code>
	 * will be returned.
	 *
	 * <p>
	 * On Windows XP, this will typically be something like:
	 *
	 * <pre>
	 *      	C:\Document and settings\MyUsername\Application Data\MyApplication
	 * </pre>
	 *
	 * and on Windows Vista it would be something like:
	 *
	 * <pre>
	 *          C:\Users\MyUsername\Application Data\MyApplication
	 * </pre>
	 *
	 * while on Mac OS X it will be something like:
	 *
	 * <pre>
	 *      	/Users/MyUsername/Library/Application Support/MyApplication
	 * </pre>
	 *
	 * All other OS'es are assumed to be UNIX-alike, returning something like:
	 *
	 * <pre>
	 *      	/user/myusername/.myapplication
	 * </pre>
	 *
	 * <p>
	 * If the directory does not already exist, it will be created.
	 * </p>
	 *
	 * @return An {@link File} referring to an existing directory for
	 *         user-specific configuration etc. for the given application.
	 */
	public synchronized File getAppUserHome() {
		if (homeDir != null) {
			return homeDir;
		}
		File appHome;
		String applicationHome = getDefaultApplicationHome();
		if (applicationHome != null) {
			appHome = new File(applicationHome);
		} else {
			if (getApplicationName() == null) {
				logger.warn("Unknown application name");
				return null;
			}
			File home = new File(System.getProperty("user.home"));
			if (!home.isDirectory()) {
				logger.error("User home not a valid directory: " + home);
				return null;
			}
			String os = System.getProperty("os.name");
			// logger.debug("OS is " + os);
			if (os.equals("Mac OS X")) {
				File libDir = new File(home, "Library/Application Support");
				libDir.mkdirs();
				appHome = new File(libDir, getApplicationName());
			} else if (os.startsWith("Windows")) {
				String APPDATA = System.getenv("APPDATA");
				File appData = null;
				if (APPDATA != null) {					
					appData = new File(APPDATA);
				}
				if (appData != null && appData.isDirectory()) {
					appHome = new File(appData, getApplicationName());
				} else {
					logger.warn("Could not find %APPDATA%: " + APPDATA);
					appHome = new File(home, getApplicationName());
				}
			} else {
				// We'll assume UNIX style is OK
				appHome = new File(home, "."
						+ getApplicationName().toLowerCase().replace(' ', '-'));
			}
		}
		if (!appHome.exists()) {
			if (appHome.mkdir()) {
				logger.info("Created " + appHome);
			} else {
				logger.error("Could not create " + appHome);
				return null;
			}
		}
		if (!appHome.isDirectory()) {
			logger.error("User home not a valid directory: " + appHome);
			return null;
		}
		this.homeDir = appHome.getAbsoluteFile();
		return this.homeDir;
	}

	/**
	 * The application's name. This name will be used as a template for creating
	 * the application home directory (but might be transcribed, for instance to
	 * lowercase). It is generally recommended, but not required - that this
	 * name does not contain spaces or any special international/Unicode
	 * characters.
	 *
	 * @return The application's name.
	 *
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * The full path to the default home directory. If this string is not
	 * <code>null</code>, then a {@link File} based on this directory will
	 * always be returned by {@link #getDefaultApplicationHome()} - otherwise
	 * the normal operating system logic is used to determine the application's
	 * home directory.
	 *
	 * @return The full path to the application's home directory, or
	 *         <code>null</code> if the operation system specific logic is to
	 *         be used.
	 */
	public String getDefaultApplicationHome() {
		return defaultApplicationHome;
	}

}
