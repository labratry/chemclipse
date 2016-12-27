/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

public interface ISelectedPositionMarker {

	/**
	 * Sets the actual retention time in milliseconds and abundance.
	 * 
	 * @param retentionTime
	 * @param abundance
	 */
	void setActualPosition(int retentionTimeInMilliseconds, double abundance);
}
