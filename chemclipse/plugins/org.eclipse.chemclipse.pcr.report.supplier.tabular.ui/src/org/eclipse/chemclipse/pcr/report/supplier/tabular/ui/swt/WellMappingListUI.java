/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.swt;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider.WellMappingLabelProvider;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider.WellMappingTableComparator;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.swt.widgets.Composite;

public class WellMappingListUI extends ExtendedTableViewer {

	private IUpdateListener updateListener;

	public WellMappingListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		refresh();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(WellMappingLabelProvider.TITLES, WellMappingLabelProvider.BOUNDS);
		setLabelProvider(new WellMappingLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new WellMappingTableComparator());
	}
}