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
package org.eclipse.chemclipse.xxd.classification.ui.provider;

import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ClassificationRuleFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		if(element instanceof ClassificationRule rule) {
			String searchExpression = rule.getSearchExpression();
			String classification = rule.getClassification();
			String reference = rule.getReference().label();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				searchExpression = searchExpression.toLowerCase();
				classification = classification.toLowerCase();
				reference = reference.toLowerCase();
			}
			//
			if(searchExpression.contains(searchText)) {
				return true;
			}
			//
			if(classification.contains(searchText)) {
				return true;
			}
			//
			if(reference.contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
