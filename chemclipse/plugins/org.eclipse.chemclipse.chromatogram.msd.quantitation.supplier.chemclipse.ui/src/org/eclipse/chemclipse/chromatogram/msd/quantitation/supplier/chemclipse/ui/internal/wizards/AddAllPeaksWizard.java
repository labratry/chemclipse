/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.QuantitationDatabase;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.jface.wizard.Wizard;

public class AddAllPeaksWizard extends Wizard {

	private static final Logger logger = Logger.getLogger(AddAllPeaksWizard.class);
	private AddAllPeakPage page;
	private List<IChromatogramPeakMSD> peaks;

	public AddAllPeaksWizard(List<IChromatogramPeakMSD> peaks) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Add All Peak to the Quantitation Table");
		this.peaks = peaks;
	}

	@Override
	public void addPages() {

		page = new AddAllPeakPage("Do you like to add the peaks to the quantitation table?\r\nThey will be identified by their best target.\r\nIf a target already exists in the table, it will be merged.");
		addPage(page);
	}

	@Override
	public boolean performFinish() {

		removeErrorMessage();
		double concentration = page.getConcentration();
		String concentrationUnit = page.getConcentrationUnit();
		if(peaks == null || peaks.size() == 0) {
			showErrorMessage("There is no peak list available.");
			return false;
		} else if(concentrationUnit == null || concentrationUnit.equals("")) {
			showErrorMessage("Please select a concentration unit for compounds that will be created.");
			return false;
		} else if(concentration <= 0) {
			showErrorMessage("Please select a valid concentration.");
			return false;
		} else {
			/*
			 * Library Info
			 */
			System.out.println("Load QuantDB");
			IQuantitationDatabase quantitationDatabase = new QuantitationDatabase();
			if(quantitationDatabase != null && quantitationDatabase.size() > 0) {
				//
				for(IChromatogramPeakMSD peak : peaks) {
					//
					String name = getPeakTargetName(peak);
					if(name == null) {
						logger.warn("The peak has no target. It has been not added to the quantitation table: " + peak);
					} else {
						/*
						 * Name is not null.
						 */
						if(quantitationDatabase.containsQuantitationCompund(name)) {
							/*
							 * Merge the quantitation peak
							 */
							IQuantitationCompound quantitationCompoundMSD = quantitationDatabase.getQuantitationCompound(name);
							if(quantitationCompoundMSD != null) {
								IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD(peak, concentration, quantitationCompoundMSD.getConcentrationUnit());
								quantitationCompoundMSD.getQuantitationPeaks().add(quantitationPeakMSD);
							}
						} else {
							/*
							 * Add a new peak
							 */
							int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
							String chemicalClass = page.getChemicalClass();
							//
							IQuantitationCompound quantitationCompoundMSD = new QuantitationCompoundMSD(name, concentrationUnit, retentionTime);
							quantitationCompoundMSD.setChemicalClass(chemicalClass);
							quantitationCompoundMSD.getRetentionTimeWindow().setAllowedNegativeDeviation(1500); // Default
							quantitationCompoundMSD.getRetentionTimeWindow().setAllowedPositiveDeviation(1500); // Default
							//
							IQuantitationPeak quantitationPeakMSD = new QuantitationPeakMSD(peak, concentration, concentrationUnit);
							//
							quantitationDatabase.add(quantitationCompoundMSD);
						}
					}
				}
				return true;
			} else {
				showErrorMessage("Please select a quantitation table previously.");
				return false;
			}
		}
	}

	/**
	 * Returns the best matching peak target name or null if there is none.
	 * 
	 * @param chromatogramPeakMSD
	 * @return String
	 */
	private String getPeakTargetName(IChromatogramPeakMSD chromatogramPeakMSD) {

		ILibraryInformation libraryInformation = null;
		float bestMatchFactor = 0.0f;
		//
		if(chromatogramPeakMSD != null) {
			//
			Set<IIdentificationTarget> peakTargets = chromatogramPeakMSD.getTargets();
			if(peakTargets.size() > 0) {
				/*
				 * Get the name of the stored identification entry.
				 */
				for(IIdentificationTarget peakTarget : peakTargets) {
					if(peakTarget instanceof IIdentificationTarget) {
						IIdentificationTarget peakIdentificationEntry = (IIdentificationTarget)peakTarget;
						float actualMatchFactor = peakIdentificationEntry.getComparisonResult().getMatchFactor();
						if(actualMatchFactor > bestMatchFactor) {
							/*
							 * Best match.
							 */
							bestMatchFactor = actualMatchFactor;
							libraryInformation = peakIdentificationEntry.getLibraryInformation();
						}
					}
				}
			}
		}
		//
		if(libraryInformation == null) {
			return null;
		} else {
			return libraryInformation.getName();
		}
	}

	private void showErrorMessage(String message) {

		page.setErrorMessage(message);
	}

	private void removeErrorMessage() {

		page.setErrorMessage(null);
	}
}
