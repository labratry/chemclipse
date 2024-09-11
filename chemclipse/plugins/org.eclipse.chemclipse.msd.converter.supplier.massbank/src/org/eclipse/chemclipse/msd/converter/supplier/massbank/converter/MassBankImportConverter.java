/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend for massspectrum database useage
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.l10n.ConverterMessages;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.massbank.io.MassBankReader;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

public class MassBankImportConverter implements IDatabaseImportConverter, IMassSpectrumImportConverter {

	private static final Logger logger = Logger.getLogger(MassBankImportConverter.class);
	private static final String DESCRIPTION = "MassBank MassSpectrum Import";

	@Override
	public IProcessingInfo<IMassSpectra> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		try {
			IMassSpectraReader massSpectraReader = new MassBankReader();
			IMassSpectra massSpectra = massSpectraReader.read(file, monitor);
			if(massSpectra != null && !massSpectra.isEmpty()) {
				processingInfo.setProcessingResult(massSpectra);
			} else {
				processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.noMassSpectraStored, file.getAbsolutePath()));
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.fileNotFound, file.getAbsolutePath()));
		} catch(FileIsNotReadableException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.fileNotReadable, file.getAbsolutePath()));
		} catch(FileIsEmptyException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.emptyFile, file.getAbsolutePath()));
		} catch(IOException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(DESCRIPTION, NLS.bind(ConverterMessages.failedToReadFile, file.getAbsolutePath()));
		}
		return processingInfo;
	}
}
