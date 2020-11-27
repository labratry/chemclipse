/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui;

import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractActivatorUI {

	private static Activator plugin;
	private static ServiceTracker<ProcessSupplierContext, ProcessSupplierContext> serviceTracker;

	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		serviceTracker = new ServiceTracker<>(context, ProcessSupplierContext.class, null);
		serviceTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
		serviceTracker.close();
		serviceTracker = null;
	}

	public static AbstractActivatorUI getDefault() {

		return plugin;
	}

	public static ProcessSupplierContext getProcessSupplierContext() {

		if(serviceTracker != null) {
			return serviceTracker.getService();
		} else {
			return null;
		}
	}
}
