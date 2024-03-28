/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;

public class DynamicProcessSupplier extends AbstractProcessSupplier<DynamicProcessSettings> {

	public DynamicProcessSupplier(String id, String name, String description) {

		super(id, name, description, DynamicProcessSettings.class, null, DataCategory.chromatographyCategories());
	}

	@Override
	public String getCategory() {

		return "Dynamic Methods";
	}
}