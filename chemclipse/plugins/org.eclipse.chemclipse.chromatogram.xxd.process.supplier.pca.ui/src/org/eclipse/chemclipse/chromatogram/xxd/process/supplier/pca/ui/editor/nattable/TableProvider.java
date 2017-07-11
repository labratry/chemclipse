/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class TableProvider {

	public static final int COLUMN_INDEX_PEAKS_NAMES = 1;
	public static final int COLUMN_INDEX_RETENTION_TIMES = 0;
	public static final String COLUMN_LABEL_GROUP_DATA = "COLUMN_GROUP_DATA";
	public static final String COLUMN_LABEL_PEAKS_NAMES = "COLUMN_PEAKS_NAMES";
	public static final String COLUMN_LABEL_RETENTION_TIMES = "COLUMN_RETENTION_TIMES";
	public static final String COLUMN_LABEL_SAMPLE_DATA = "COLUMN_SAMPLE_DATA";
	public static final String NORMALIZATION_COLUMN = "column normalization";
	public static final String NORMALIZATION_NONE = "none normalization";
	public static final String NORMALIZATION_ROW = "row normalization";
	/**
	 * number of column which is used to describe sample data
	 */
	final public static int NUMER_OF_DESCRIPTION_COLUMN = 2;
	private TableData dataTable;
	private String normalization;

	public TableProvider(TableData dataTable) {
		this.dataTable = dataTable;
		this.normalization = NORMALIZATION_NONE;
	}

	public int getColumnCount() {

		return dataTable.getSamples().size() + NUMER_OF_DESCRIPTION_COLUMN;
	}

	public String getColumnLable(int columnIndex) {

		if(columnIndex == COLUMN_INDEX_RETENTION_TIMES) {
			return COLUMN_LABEL_RETENTION_TIMES;
		} else if(columnIndex == COLUMN_INDEX_PEAKS_NAMES) {
			return COLUMN_LABEL_PEAKS_NAMES;
		} else {
			ISample sample = getDataTable().getSamples().get(columnIndex - NUMER_OF_DESCRIPTION_COLUMN);
			if(sample instanceof IGroup) {
				return COLUMN_LABEL_GROUP_DATA;
			} else {
				return COLUMN_LABEL_SAMPLE_DATA;
			}
		}
	}

	public TableData getDataTable() {

		return dataTable;
	}

	public String getNormalizationData() {

		return normalization;
	}

	public int getRowCount() {

		List<ISample> samples = dataTable.getSamples();
		if(!samples.isEmpty()) {
			return samples.get(0).getPcaResult().getSampleData().length;
		}
		return 0;
	}

	public void setNormalizationData(String normalizationData) {

		if(normalizationData.equals(NORMALIZATION_NONE) //
				|| normalizationData.equals(NORMALIZATION_COLUMN) //
				|| normalizationData.equals(NORMALIZATION_ROW)) {
			this.normalization = normalizationData;
		} else {
			throw new RuntimeException("Undefine format cell");
		}
	}
}
