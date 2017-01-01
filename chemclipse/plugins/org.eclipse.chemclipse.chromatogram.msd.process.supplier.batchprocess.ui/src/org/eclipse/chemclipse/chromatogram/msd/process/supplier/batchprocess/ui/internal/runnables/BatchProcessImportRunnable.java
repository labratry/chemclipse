/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.BatchProcessJobReader;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessImportRunnable implements IRunnableWithProgress {

	private File file;
	private IBatchProcessJob batchProcessJob = null;

	public BatchProcessImportRunnable(File file) {
		this.file = file;
	}

	/**
	 * Returns the batch process job, it could be null.
	 * Execute run first using a e.g. ProgressMonitorDialog.
	 * 
	 * @return {@link IBatchProcessJob}
	 */
	public IBatchProcessJob getBatchProcessJob() {

		return batchProcessJob;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		BatchProcessJobReader reader = new BatchProcessJobReader();
		try {
			batchProcessJob = reader.read(file, monitor);
		} catch(FileNotFoundException e) {
			throw new InterruptedException("The file " + file.getPath() + " couldn't be found.");
		} catch(FileIsNotReadableException e) {
			throw new InterruptedException("The file " + file.getPath() + " is not readable.");
		} catch(FileIsEmptyException e) {
			throw new InterruptedException("The file " + file.getPath() + " is empty.");
		} catch(IOException e) {
			throw new InterruptedException("An I/O error has occured using the file " + file.getPath());
		}
	}
}
