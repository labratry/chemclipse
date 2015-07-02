/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.preferences;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceSupplier {

	public static final String P_ORGANIC_COMPOUND_HYDROCARBONS = "organicCompoundHydrocarbons";
	public static final String P_ORGANIC_COMPOUND_FATTY_ACIDS = "organicCompoundFattyAcids";
	public static final String P_ORGANIC_COMPOUND_FAME = "organicCompoundFattyAcidsAsMethylEsters";
	//
	private static IMarkedIons compoundIonsEmpty = new MarkedIons();
	private static IMarkedIons compoundIonsHydrocarbons = null;
	private static IMarkedIons compoundIonsFattyAcids = null;
	private static IMarkedIons compoundIonsFame = null;

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {

	}

	public static String[][] getOrganicCompoundPresets() {

		String[][] organicCompoundPresets = new String[3][2];
		organicCompoundPresets[0] = new String[]{"Hydrocarbons", P_ORGANIC_COMPOUND_HYDROCARBONS};
		organicCompoundPresets[1] = new String[]{"Fatty Acids", P_ORGANIC_COMPOUND_FATTY_ACIDS};
		organicCompoundPresets[2] = new String[]{"FAME", P_ORGANIC_COMPOUND_FAME};
		return organicCompoundPresets;
	}

	public static IMarkedIons getOrganicCompoundIons() {

		IMarkedIons compoundIons;
		//
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String selectedOrganicCompound = store.getString(PreferenceConstants.P_SELECTED_ORGANIC_COMPOUND);
		if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_HYDROCARBONS)) {
			/*
			 * Hydrocarbons
			 */
			if(compoundIonsHydrocarbons == null) {
				compoundIonsHydrocarbons = new MarkedIons();
				compoundIonsHydrocarbons.add(new MarkedIon(57));
				compoundIonsHydrocarbons.add(new MarkedIon(71));
				compoundIonsHydrocarbons.add(new MarkedIon(85));
			}
			compoundIons = compoundIonsHydrocarbons;
		} else if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_FATTY_ACIDS)) {
			/*
			 * Fatty acids
			 */
			if(compoundIonsFattyAcids == null) {
				compoundIonsFattyAcids = new MarkedIons();
				compoundIonsFattyAcids.add(new MarkedIon(74));
				compoundIonsFattyAcids.add(new MarkedIon(87));
			}
			compoundIons = compoundIonsFattyAcids;
		} else if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_FAME)) {
			/*
			 * FAME
			 */
			if(compoundIonsFame == null) {
				compoundIonsFame = new MarkedIons();
				compoundIonsFame.add(new MarkedIon(79));
				compoundIonsFame.add(new MarkedIon(81));
			}
			compoundIons = compoundIonsFame;
		} else {
			/*
			 * No ions.
			 */
			compoundIons = compoundIonsEmpty;
		}
		return compoundIons;
	}

	/**
	 * Returns the x offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayXOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_X_OFFSET);
	}

	/**
	 * Returns the y offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayYOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_Y_OFFSET);
	}
}
