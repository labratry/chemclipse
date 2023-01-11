/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.swt.dialogs.WindowsFileDialog;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

public class TimeRangesSettingsEditor implements SettingsUIProvider.SettingsUIControl, IExtendedPartUI {

	private Composite control;
	//
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	//
	private TimeRanges settings = new TimeRanges();
	private TimeRangeLabels timeRangeLabels = new TimeRangeLabels();
	//
	private TimeRangesListUI listUI;
	//
	private List<Listener> listeners = new ArrayList<>();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IProcessorPreferences<TimeRanges> preferences = null;

	public TimeRangesSettingsEditor(Composite parent, IProcessorPreferences<TimeRanges> preferences, TimeRanges timeRanges, TimeRangeLabels timeRangeLabels) {

		/*
		 * Populate the settings on demand.
		 */
		this.preferences = preferences;
		if(timeRanges != null) {
			this.settings.load(timeRanges.save());
		}
		//
		if(timeRangeLabels != null) {
			this.timeRangeLabels = timeRangeLabels;
		}
		//
		control = createControl(parent);
	}

	@Override
	public void setEnabled(boolean enabled) {

		listUI.getControl().setEnabled(enabled);
	}

	@Override
	public IStatus validate() {

		return ValidationStatus.ok();
	}

	@Override
	public String getSettings() throws IOException {

		if(preferences != null) {
			TimeRanges settingz = new TimeRanges();
			settingz.load(settings.save());
			return preferences.getSerialization().toString(settingz);
		}
		return "";
	}

	@Override
	public void addChangeListener(Listener listener) {

		listeners.add(listener);
	}

	@Override
	public Control getControl() {

		return control;
	}

	public void load(String entries) {

		settings.load(entries);
		setTableViewerInput();
	}

	public String getValues() {

		return settings.save();
	}

	private Composite createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		//
		createButtonSection(composite);
		createToolbarSearch(composite);
		createTableSection(composite);
		//
		initialize();
		//
		return composite;
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
	}

	private void createButtonSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));
		//
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		createButtonAdd(composite);
		createButtonEdit(composite);
		createButtonRemove(composite);
		createButtonRemoveAll(composite);
		createButtonImport(composite);
		createButtonExport(composite);
		createButtonSave(composite);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				listUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		listUI = new TimeRangesListUI(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		setTableViewerInput();
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getCreateMessage(), timeRangeLabels.getCreateInitialValue(), new TimeRangeInputValidator(settings.keySet(), timeRangeLabels));
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue();
					TimeRange timeRange = settings.extractTimeRange(item);
					if(timeRange != null) {
						settings.add(timeRange);
						setTableViewerInput();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Edit");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof TimeRange) {
					Set<String> keySetEdit = new HashSet<>();
					keySetEdit.addAll(settings.keySet());
					TimeRange timeRange = (TimeRange)object;
					keySetEdit.remove(timeRange.getIdentifier());
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getEditMessage(), settings.extractTimeRange(timeRange), new TimeRangeInputValidator(keySetEdit, timeRangeLabels));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						TimeRange timeRangeNew = settings.extractTimeRange(item);
						if(timeRangeNew != null) {
							settings.remove(timeRange);
							settings.add(timeRangeNew);
							setTableViewerInput();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove Selected");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getDeleteMessage())) {
					IStructuredSelection structuredSelection = (IStructuredSelection)listUI.getSelection();
					for(Object object : structuredSelection.toArray()) {
						if(object instanceof TimeRange) {
							settings.remove(((TimeRange)object).getIdentifier());
						}
					}
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove All");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getClearMessage())) {
					settings.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(OperatingSystemUtils.isWindows()) {
					WindowsFileDialog.ClearInitialDirectoryWorkaround();
				}
				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(timeRangeLabels.getTitle());
				fileDialog.setFilterExtensions(new String[]{TimeRanges.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{TimeRanges.FILTER_NAME});
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.setValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
					settings.importItems(file);
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Export");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				if(OperatingSystemUtils.isWindows()) {
					WindowsFileDialog.ClearInitialDirectoryWorkaround();
				}
				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(timeRangeLabels.getTitle());
				fileDialog.setFilterExtensions(new String[]{TimeRanges.FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{TimeRanges.FILTER_NAME});
				fileDialog.setFileName(TimeRanges.FILE_NAME);
				fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER));
				String pathname = fileDialog.open();
				if(pathname != null) {
					File file = new File(pathname);
					String path = file.getParentFile().getAbsolutePath();
					preferenceStore.setValue(PreferenceConstants.P_TIME_RANGE_TEMPLATE_FOLDER, path);
					if(settings.exportItems(file)) {
						MessageDialog.openInformation(button.getShell(), timeRangeLabels.getTitle(), "Exported Successful");
					} else {
						MessageDialog.openWarning(button.getShell(), timeRangeLabels.getTitle(), "Export Failed");
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonSave(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Save");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				settings.save();
			}
		});
		//
		return button;
	}

	private void setTableViewerInput() {

		listUI.setInput(settings.values());
	}
}