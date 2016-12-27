/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

/**
 * All extension points which support a mass spectrum comparator must implement
 * this interface.
 * 
 * @author eselmeister
 */
public interface IMassSpectrumComparator {

	/**
	 * This methods compares two mass spectra and returns an instance of a {@link IMassSpectrumComparatorProcessingInfo} object.<br/>
	 * If something has gone wrong, null will be returned.<br/>
	 * The mass spectra will be left as they are.
	 * 
	 * @param unknown
	 * @param reference
	 * @return {@link IMassSpectrumComparatorProcessingInfo}
	 */
	IMassSpectrumComparatorProcessingInfo compare(IScanMSD unknown, IScanMSD reference);

	/**
	 * Validates the unknown, reference mass spectrum and the ion range.
	 * 
	 * @param unknown
	 * @param reference
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IScanMSD unknown, IScanMSD reference);
}
