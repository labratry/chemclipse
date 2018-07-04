/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Backfolding Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding";

	public FilterModifier(IChromatogramSelectionMSD chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * The filter settings.
			 */
			ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
			/*
			 * Set the values.
			 */
			chromatogramFilterSettings.setMaximumRetentionTimeShift(PreferenceSupplier.getMaxRetentionTimeShift());
			chromatogramFilterSettings.setNumberOfBackfoldingRuns(PreferenceSupplier.getBackfoldingRuns());
			/*
			 * Apply the filter.
			 */
			final IProcessingInfo processingInfo = ChromatogramFilterMSD.applyFilter((IChromatogramSelectionMSD)chromatogramSelection, chromatogramFilterSettings, FILTER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
		}
	}

	@Override
	public String getDescription() {

		return DESCRIPTION;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("Backfolding Filter", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
