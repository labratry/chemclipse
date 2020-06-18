/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - Ion-wise savitzky-golay on msd data
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ChromatogramFilterSettings extends AbstractChromatogramFilterSettings {

	@JsonProperty(value = "Order", defaultValue = "2")
	@JsonPropertyDescription(value = "Order p of the polynomial to be fitted: Integer in the range from 2 to 5")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_ORDER, maxValue = PreferenceSupplier.MAX_ORDER)
	private int order = 2;
	@JsonProperty(value = "Width", defaultValue = "5")
	@JsonPropertyDescription(value = "Filter width, uneven integer in the range from 5 to 51")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WIDTH, maxValue = PreferenceSupplier.MAX_WIDTH)
	private int width = 5;
	@JsonIgnore
	private int derivative = 0;

	public int getDerivative() {

		return derivative;
	}

	public void setDerivative(int derivative) {

		this.derivative = derivative;
	}

	public int getOrder() {

		return order;
	}

	public void setOrder(int order) {

		this.order = order;
	}

	public int getWidth() {

		return width;
	}

	public void setWidth(int width) {

		this.width = width;
	}
}
