/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.model.ScanSelectorOption;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.LabelFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.StringRegexFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FilterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FilterPreferencePage() {

		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTitle("Chromatogram Scan Filter");
		setDescription("");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		addField(new LabelFieldEditor("Scan Remover Pattern (" + PreferenceSupplier.PRESERVE_SIGN.toString() + "=preserve " + PreferenceSupplier.REMOVE_SIGN + "=remove)", getFieldEditorParent()));
		StringRegexFieldEditor stringPattern = new StringRegexFieldEditor(PreferenceSupplier.P_REMOVER_PATTERN, "Pattern", getFieldEditorParent());
		stringPattern.setRegEx(PreferenceSupplier.CHECK_REMOVER_PATTERM);
		addField(stringPattern);
		//
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new LabelFieldEditor("Scan Selector", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_SCAN_SELECTOR_OPTION, "Option", ScanSelectorOption.getOptions(), getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_SCAN_SELECTOR_VALUE, "Value", PreferenceSupplier.MIN_SCAN_SELECTOR_VALUE, PreferenceSupplier.MAX_SCAN_SELECTOR_VALUE, getFieldEditorParent()));
	}
}