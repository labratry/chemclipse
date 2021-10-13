/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

import org.eclipse.chemclipse.support.text.ILabel;

public enum ModelPeakOption implements ILabel {

	ALL("MPx (All)", 0), //
	MP1("MP1", 1), //
	MP2("MP2", 2), //
	MP3("MP3", 3);

	private String label = "";
	private int value = 0;

	private ModelPeakOption(String label, int value) {

		this.label = label;
		this.value = value;
	}

	@Override
	public String label() {

		return label;
	}

	public int value() {

		return value;
	}

	public static String[][] getItems() {

		return new String[][]{//
				{ALL.label(), ALL.name()}, //
				{MP1.label(), MP1.name()}, //
				{MP2.label(), MP2.name()}, //
				{MP3.label(), MP3.name()} //
		};
	}
}
