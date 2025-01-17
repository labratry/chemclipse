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
package org.eclipse.chemclipse.model.core;

import java.util.TreeMap;

public class Peak_2_Test extends AbstractPeakTestCase {

	private IPeak peak = createPeak();

	public void test1() {

		assertNotNull(peak);
	}

	public void test2() {

		/*
		 * 1.0177305 - strictModel: true
		 * 1.0165975 - strictModel: false
		 */
		assertEquals(1.0177305f, peak.getPeakModel().getTailing());
	}

	private IPeak createPeak() {

		float totalSignal = 239;
		float startBackgroundAbundance = 0;
		float stopBackgroundAbundance = 0;
		//
		TreeMap<Integer, Float> retentionTimeIntensityMap = new TreeMap<>();
		retentionTimeIntensityMap.put(3032172, 0.0f);
		retentionTimeIntensityMap.put(3032883, 61.0f);
		retentionTimeIntensityMap.put(3033595, 118.0f);
		retentionTimeIntensityMap.put(3034306, 60.0f);
		retentionTimeIntensityMap.put(3035017, 0.0f);
		//
		return createPeak(totalSignal, retentionTimeIntensityMap, startBackgroundAbundance, stopBackgroundAbundance, true);
	}
}