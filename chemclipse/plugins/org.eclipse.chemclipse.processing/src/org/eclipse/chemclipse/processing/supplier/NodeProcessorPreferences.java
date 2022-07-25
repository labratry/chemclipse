/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvements
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public final class NodeProcessorPreferences<T> implements IProcessorPreferences<T> {

	private static final Logger logger = Logger.getLogger(NodeProcessorPreferences.class);
	//
	private static final String KEY_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
	private static final String KEY_USER_SETTINGS = "userSettings";
	private static final String KEY_ASK_FOR_SETTINGS = "askForSettings";
	//
	private Preferences node;
	private IProcessSupplier<T> supplier;

	public NodeProcessorPreferences(IProcessSupplier<T> supplier, Preferences node) {

		this.supplier = supplier;
		this.node = node;
	}

	@Override
	public DialogBehavior getDialogBehaviour() {

		if(supplier.getSettingsClass() == null) {
			return DialogBehavior.NONE;
		}
		//
		trySync();
		boolean askForSettings = node.getBoolean(KEY_ASK_FOR_SETTINGS, true);
		if(askForSettings) {
			return DialogBehavior.SHOW;
		} else {
			return DialogBehavior.SAVED_DEFAULTS;
		}
	}

	@Override
	public void setAskForSettings(boolean askForSettings) {

		node.putBoolean(KEY_ASK_FOR_SETTINGS, askForSettings);
		tryFlush();
	}

	@Override
	public void setUserSettings(String settings) {

		node.put(KEY_USER_SETTINGS, settings);
		tryFlush();
	}

	@Override
	public void reset() {

		try {
			node.clear();
			tryFlush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	@Override
	public boolean isUseSystemDefaults() {

		if(supplier.getSettingsClass() == null) {
			return true;
		}
		//
		trySync();
		return node.getBoolean(KEY_USE_SYSTEM_DEFAULTS, true);
	}

	@Override
	public void setUseSystemDefaults(boolean useSystemDefaults) {

		node.putBoolean(KEY_USE_SYSTEM_DEFAULTS, useSystemDefaults);
		tryFlush();
	}

	@Override
	public String getUserSettingsAsString() {

		trySync();
		return node.get(KEY_USER_SETTINGS, "");
	}

	@Override
	public IProcessSupplier<T> getSupplier() {

		return supplier;
	}

	public void trySync() {

		try {
			node.sync();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	private void tryFlush() {

		try {
			node.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}
