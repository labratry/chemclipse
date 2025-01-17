/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.system;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class FilterRetentionIndexQC extends AbstractSystemProcessSettings {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.filter.system.retentionIndexQualityControl"; //$NON-NLS-1$

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractSystemProcessSupplier<SettingsRetentionIndexQC> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, Messages.qcRetentionIndex, Messages.qcRetentionIndexDescription, SettingsRetentionIndexQC.class, parent);
		}

		@Override
		public void executeUserSettings(ISystemProcessSettings settings, ProcessExecutionContext context) throws Exception {

			if(settings instanceof SettingsRetentionIndexQC processSettings) {
				org.eclipse.chemclipse.model.preferences.PreferenceSupplier.setUseRetentionIndexQC(processSettings.isUseRetentionIndexQC());
			}
		}
	}
}
