/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.fieldeditors;

import org.eclipse.chemclipse.ux.extension.xxd.ui.ranges.TimeRangesSettingsEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TimeRangesFieldEditor extends FieldEditor {

	private TimeRangesSettingsEditor editor;

	public TimeRangesFieldEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		editor = new TimeRangesSettingsEditor(parent, null, null);
		editor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		editor.load(entries);
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		editor.load(entries);
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), editor.getValues());
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		Control control = editor.getControl();
		GridData gridData = (GridData)control.getLayoutData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = (numColumns >= 2) ? numColumns - 1 : 1;
	}
}
