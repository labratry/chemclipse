/*******************************************************************************
 * Copyright (c) 2012, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.processing;

import java.io.File;

import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public abstract class AbstractChromatogramReportProcessingInfo extends AbstractProcessingInfo implements IChromatogramReportProcessingInfo {

	@Override
	public File getFile() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof File) {
			return (File)object;
		} else {
			throw createTypeCastException("Report Generator", object.getClass(), File.class);
		}
	}

	@Override
	public void setFile(File file) {

		setProcessingResult(file);
	}
}
