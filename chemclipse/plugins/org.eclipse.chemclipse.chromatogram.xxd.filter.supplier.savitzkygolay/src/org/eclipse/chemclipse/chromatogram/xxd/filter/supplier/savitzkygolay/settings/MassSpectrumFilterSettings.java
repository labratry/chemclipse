/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MassSpectrumFilterSettings extends AbstractMassSpectrumFilterSettings {

	private static final Logger logger = Logger.getLogger(MassSpectrumFilterSettings.class);
	//
	@JsonProperty(value = "Order", defaultValue = "2")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_ORDER, maxValue = PreferenceSupplier.MAX_ORDER)
	private int order = 2;
	@JsonProperty(value = "Width", defaultValue = "5")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_WIDTH, maxValue = PreferenceSupplier.MAX_WIDTH)
	private int width = 5;

	public int getDerivative() {

		return 0;
	}

	public void setDerivative(int derivative) {

		if(derivative != 0) {
			logger.warn("Derivative is not supported");
		}
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
