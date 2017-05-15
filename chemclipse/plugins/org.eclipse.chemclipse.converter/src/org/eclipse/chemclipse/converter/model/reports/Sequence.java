/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

import java.util.ArrayList;
import java.util.List;

public class Sequence<T> extends AbstractFileAttributes implements ISequence<T> {

	private String info = "";
	private String method = "";
	private String data = "";
	private List<T> sequenceRecords;

	public Sequence() {
		sequenceRecords = new ArrayList<T>();
	}

	@Override
	public String getInfo() {

		return info;
	}

	@Override
	public void setInfo(String info) {

		this.info = info;
	}

	@Override
	public String getMethod() {

		return method;
	}

	@Override
	public void setMethod(String method) {

		this.method = method;
	}

	@Override
	public String getData() {

		return data;
	}

	@Override
	public void setData(String data) {

		this.data = data;
	}

	@Override
	public List<T> getSequenceRecords() {

		return sequenceRecords;
	}
}
