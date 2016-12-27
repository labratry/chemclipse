/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.quantitation.AbstractQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;

public class QuantitationEntryMSD extends AbstractQuantitationEntryMSD implements IQuantitationEntryMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 5095807391290598331L;

	public QuantitationEntryMSD(String name, double concentration, String concentrationUnit, double area, double ion) {
		super(name, concentration, concentrationUnit, area, ion);
	}
}
