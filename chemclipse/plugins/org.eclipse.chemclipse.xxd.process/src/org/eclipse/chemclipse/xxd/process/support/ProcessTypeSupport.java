/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - improve logging output, propagate processor messages, enhance for usage in editors, preference support for processors
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.filter.FilterFactory;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryComparator;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplierWSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramReportTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ClassifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.CombinedIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.MassspectrumProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierCSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakQuantitationTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessTypeSupport {

	private static final String KEY_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
	private static final String KEY_USER_SETTINGS = "userSettings";
	private static final String KEY_ASK_FOR_SETTINGS = "askForSettings";
	private static final CategoryComparator CATEGORY_COMPARATOR = new CategoryComparator();
	private static final Logger logger = Logger.getLogger(ProcessTypeSupport.class);
	//
	private Map<String, IProcessTypeSupplier<?>> processSupplierMap = new HashMap<>();
	private IEclipsePreferences preferences;

	public ProcessTypeSupport() {
		this(null);
	}

	public ProcessTypeSupport(FilterFactory filterFactory) {
		/*
		 * Add all available process supplier here.
		 * TODO: Test native settings composite via extension point resolution
		 */
		addProcessSupplier(new BaselineDetectorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramIdentifierTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new ChromatogramIntegratorTypeSupplier()); // OK
		addProcessSupplier(new ClassifierTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new CombinedIntegratorTypeSupplier()); // OK - Nested Settings
		addProcessSupplier(new ChromatogramFilterTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakDetectorTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakIdentifierTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakDetectorTypeSupplierCSD()); // OK
		addProcessSupplier(new PeakIdentifierTypeSupplierCSD()); // OK
		addProcessSupplier(new PeakIntegratorTypeSupplier()); // OK - Improve settings
		addProcessSupplier(new PeakQuantitationTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramCalculatorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramReportTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramExportTypeSupplierMSD()); // OK - Improve settings
		addProcessSupplier(new ChromatogramExportTypeSupplierCSD()); // OK - Improve settings
		addProcessSupplier(new ChromatogramExportTypeSupplierWSD()); // OK - Improve settings
		addProcessSupplier(MassspectrumProcessTypeSupplier.createPeakFilterSupplier());
		addProcessSupplier(MassspectrumProcessTypeSupplier.createScanFilterSupplier());
		// NoiseCalculator?
		if(filterFactory != null) {
			// TODO
		}
	}

	/**
	 * 
	 * @return all active preferences for this {@link ProcessTypeSupport}
	 */
	public Map<ProcessorSupplier, ProcessorPreferences> getAllPreferences() {

		HashMap<ProcessorSupplier, ProcessorPreferences> map = new HashMap<>();
		try {
			IEclipsePreferences storage = getStorage();
			String[] childrenNames = storage.childrenNames();
			for(String name : childrenNames) {
				Preferences node = storage.node(name);
				if(node.keys().length == 0) {
					// empty default node
					continue;
				}
				IProcessTypeSupplier<?> supplier = getSupplier(name);
				if(supplier != null) {
					IProcessTypeSupplier<?> typeSupplier = getSupplier(name);
					if(typeSupplier == null) {
						// not valid for this type support
						continue;
					}
					ProcessorSupplier processorSupplier = typeSupplier.getProcessorSupplier(name);
					if(processorSupplier != null) {
						map.put(processorSupplier, new NodeProcessorPreferences(node));
					}
				}
			}
		} catch(BackingStoreException e) {
			// can't load it then
		}
		return map;
	}

	public IProcessTypeSupplier<?> getSupplier(String id) {

		return processSupplierMap.get(id);
	}

	/**
	 * 
	 * @param processorId
	 * @return the preferences for this processor id
	 */
	public ProcessorPreferences getPreferences(ProcessorSupplier supplier) {

		return new NodeProcessorPreferences(getStorage().node(supplier.getId()));
	}

	private IEclipsePreferences getStorage() {

		if(preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(ProcessTypeSupport.class.getName());
		}
		return preferences;
	}

	/**
	 * Adds the given {@link IProcessTypeSupplier} to this {@link ProcessTypeSupport} this allows for adding non standard supplier
	 * 
	 * @param processTypeSupplier
	 */
	public void addProcessSupplier(IProcessTypeSupplier<?> processTypeSupplier) {

		try {
			for(String processorId : processTypeSupplier.getProcessorIds()) {
				IProcessTypeSupplier<?> typeSupplier = getSupplier(processorId);
				if(typeSupplier != null) {
					logger.warn("The processor id " + processorId + " is already defined by " + typeSupplier.getClass().getSimpleName() + " and is ignored for redefining supplier " + processTypeSupplier.getClass().getSimpleName());
				} else {
					processSupplierMap.put(processorId, processTypeSupplier);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	/**
	 * 
	 * @param dataTypes
	 * @return the matching {@link IProcessTypeSupplier} order by category name
	 */
	public List<IProcessTypeSupplier<?>> getProcessorTypeSuppliers(Collection<DataType> dataTypes) {

		List<IProcessTypeSupplier<?>> supplier = new ArrayList<>();
		for(IProcessTypeSupplier<?> processTypeSupplier : processSupplierMap.values()) {
			List<DataType> types = processTypeSupplier.getSupportedDataTypes();
			for(DataType dataType : dataTypes) {
				if(types.contains(dataType)) {
					if(!supplier.contains(processTypeSupplier)) {
						supplier.add(processTypeSupplier);
						break;
					}
				}
			}
		}
		Collections.sort(supplier, CATEGORY_COMPARATOR);
		return supplier;
	}

	public <T> IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<>();
		if(chromatogramSelection != null) {
			chromatogramSelections.add(chromatogramSelection);
		}
		return applyProcessor(chromatogramSelections, processMethod, monitor);
	}

	public <T> IProcessingInfo<T> applyProcessor(List<? extends IChromatogramSelection<?, ?>> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		for(IChromatogramSelection<?, ?> chromatogramSelection : chromatogramSelections) {
			/*
			 * Process referenced chromatograms?
			 * Will make things more complex. Discussion needed.
			 * Think of RI calculation. Are the RIs of the master taken or the RIs of the selected reference?
			 * What about the column?
			 */
			for(IProcessEntry processEntry : processMethod) {
				String processorId = processEntry.getProcessorId();
				IProcessTypeSupplier<?> processTypeSupplier = getSupplier(processorId);
				if(processTypeSupplier != null) {
					/*
					 * If processEntry.getJsonSettings() == {} (IProcessEntry.EMPTY_JSON_SETTINGS),
					 * the processSettings class will be null.
					 * The applyProcessor method manages how to handle this situation.
					 * By default, the system settings of the plugin shall be used instead.
					 */
					IProcessSettings processSettings = getProcessSettings(processEntry);
					IProcessingInfo<?> processorResult = processTypeSupplier.applyProcessor(chromatogramSelection, processorId, processSettings, monitor);
					processingInfo.addMessages(processorResult);
					if(processorResult.hasErrorMessages()) {
						break;
					}
				}
			}
		}
		return processingInfo;
	}

	public IProcessSettings getProcessSettings(IProcessEntry processEntry) {

		IProcessSettings processSettings = null;
		if(processEntry != null) {
			Class<? extends IProcessSettings> clazz = processEntry.getProcessSettingsClass();
			if(clazz != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				try {
					String content = processEntry.getJsonSettings();
					if(IProcessEntry.EMPTY_JSON_SETTINGS.equals(content)) {
						logger.info("Process settings are empty. Default system settings are used instead.");
					} else {
						processSettings = objectMapper.readValue(content, clazz);
					}
				} catch(JsonParseException e) {
					logger.warn(e);
				} catch(JsonMappingException e) {
					logger.warn(e);
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
		//
		return processSettings;
	}

	public int validate(IProcessEntry processEntry) {

		if(processEntry != null) {
			if(processSupplierMap.containsKey(processEntry.getProcessorId())) {
				if(processEntry.getJsonSettings().equals(IProcessEntry.EMPTY_JSON_SETTINGS)) {
					return IStatus.INFO;
				} else {
					if(processEntry.getProcessSettingsClass() == null) {
						return IStatus.WARNING;
					} else {
						return IStatus.OK;
					}
				}
			} else {
				return IStatus.ERROR;
			}
		}
		/*
		 * Error if other checks failed.
		 */
		return IStatus.ERROR;
	}

	private static final class NodeProcessorPreferences implements ProcessorPreferences {

		private Preferences node;

		public NodeProcessorPreferences(Preferences node) {
			this.node = node;
		}

		@Override
		public boolean isAskForSettings() {

			trySync();
			return node.getBoolean(KEY_ASK_FOR_SETTINGS, true);
		}

		public void trySync() {

			try {
				node.sync();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public void setAskForSettings(boolean askForSettings) {

			node.putBoolean(KEY_ASK_FOR_SETTINGS, askForSettings);
			tryFlush();
		}

		private void tryFlush() {

			try {
				node.flush();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public String getUserSettings() {

			trySync();
			return node.get(KEY_USER_SETTINGS, ProcessEntry.EMPTY_JSON_SETTINGS);
		}

		@Override
		public void setUserSettings(String settings) {

			node.put(KEY_USER_SETTINGS, settings);
			tryFlush();
		}

		@Override
		public void reset() {

			try {
				node.clear();
				tryFlush();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public boolean isUseSystemDefaults() {

			trySync();
			return node.getBoolean(KEY_USE_SYSTEM_DEFAULTS, true);
		}

		@Override
		public void setUseSystemDefaults(boolean useSystemDefaults) {

			node.putBoolean(KEY_USE_SYSTEM_DEFAULTS, useSystemDefaults);
			tryFlush();
		}
	}
}
