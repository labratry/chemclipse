/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.IPeakFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class PeakFilter {

	private static final Logger logger = Logger.getLogger(PeakFilter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.wsd.filter.peakFilterSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILTER = "filter";
	//
	private static final String PROCESSING_DESCRIPTION = "Peak Filter";
	private static final String NO_PEAK_FILTER_AVAILABLE = "There is no peak filter available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private PeakFilter() {

	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IPeakFilterSettings} on the {@link IPeak} .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.
	 * 
	 * @param peak
	 * @param peakFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(IPeakWSD peak, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(peak, peakFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter, but retrieves the IPeakFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IPeakMSD peak, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param peak
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(IPeakWSD peak, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(peak, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IPeakFilterSettings} on the peak list .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.
	 * 
	 * @param List
	 *            <IPeakMSD> peaks
	 * @param peakFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(List<IPeakWSD> peaks, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(peaks, peakFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter, but retrieves the IPeakFilterSettings dynamically.<br/>
	 * See also method: applyFilter(List<IPeakMSD> peaks, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param List
	 *            <IPeakMSD> peaks
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(List<IPeakWSD> peaks, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(peaks, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IPeakFilterSettings} on the peaks stored in the chromatogram selection.<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(IChromatogramSelectionWSD chromatogramSelection, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(chromatogramSelection, peakFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter, but retrieves the IPeakFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IChromatogramSelectionMSD chromatogramSelection, IPeakFilterSettings peakFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param chromatogramSelection
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IPeakFilterResult> applyFilter(IChromatogramSelectionWSD chromatogramSelection, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IPeakFilterResult> processingInfo;
		IPeakFilter peakFilter = getPeakFilter(filterId);
		if(peakFilter != null) {
			processingInfo = peakFilter.applyFilter(chromatogramSelection, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_PEAK_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Returns the peak filter support instance.
	 * 
	 * @return {@link IPeakFilterSupport}
	 */
	public static IPeakFilterSupport getPeakFilterSupport() {

		PeakFilterSupplier supplier;
		PeakFilterSupport filterSupport = new PeakFilterSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new PeakFilterSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IPeakFilter} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IPeakFilter getPeakFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IPeakFilter instance = null;
		if(element != null) {
			try {
				instance = (IPeakFilter)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IPeakFilter} instance or null if none is
	 * available.
	 * 
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if("".equals(filterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}
	// --------------------------------------------private methods
}
