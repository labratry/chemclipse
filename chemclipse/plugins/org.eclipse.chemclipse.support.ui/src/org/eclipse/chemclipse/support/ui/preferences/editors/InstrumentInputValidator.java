/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.List;

public class InstrumentInputValidator implements IInputValidator {

	private String[] items;

	public InstrumentInputValidator() {
		items = new String[]{};
	}

	public InstrumentInputValidator(List list) {
		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String newInstrument) {

		if(newInstrument.equals("")) {
			return "Please type in an instrument name.";
		} else {
			for(String item : items) {
				if(item.equals(newInstrument)) {
					return "The instrument exists already.";
				}
			}
		}
		return null;
	}
}
