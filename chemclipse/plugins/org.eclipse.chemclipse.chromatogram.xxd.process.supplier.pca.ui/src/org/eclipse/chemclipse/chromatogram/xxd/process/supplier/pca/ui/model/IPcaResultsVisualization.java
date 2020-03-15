/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;

import javafx.collections.ObservableList;

public interface IPcaResultsVisualization extends IResultsPCA<IPcaResultVisualization, IVariableVisualization> {

	IPcaVisualization getPcaVisualization();

	@Override
	ObservableList<IVariableVisualization> getExtractedVariables();

	@Override
	ObservableList<IPcaResultVisualization> getPcaResultList();
}
