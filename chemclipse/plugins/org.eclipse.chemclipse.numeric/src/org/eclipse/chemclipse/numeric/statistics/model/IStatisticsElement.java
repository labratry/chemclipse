/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics.model;

import java.util.List;

public interface IStatisticsElement<T> {

	Object getIdentifier();

	void setIdentifier(Object identifier);

	List<IStatisticsSourceObject<T>> getSourceElements();

	void setSourceElements(List<IStatisticsSourceObject<T>> sourceElements);

	boolean isContentStatistics();

	IStatistics getStatisticsContent();

	void setStatisticsContent(IStatistics content);

	List<StatisticsElement<T>> getStatisticsElements();

	void setStatisticsElements(List<StatisticsElement<T>> content);
}