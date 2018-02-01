/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public abstract class AbstractPcaCalculator implements IPcaCalculator {

	protected DenseMatrix64F loadings;
	protected double mean[];
	protected int numComps;
	protected DenseMatrix64F sampleData = new DenseMatrix64F(1, 1);
	protected int sampleIndex;

	@Override
	public void addObservation(double[] obsData) {

		for(int i = 0; i < obsData.length; i++) {
			sampleData.set(sampleIndex, i, obsData[i]);
		}
		sampleIndex++;
	}

	public double[] applyLoadings(double[] obs) {

		DenseMatrix64F mean = DenseMatrix64F.wrap(sampleData.getNumCols(), 1, this.mean);
		DenseMatrix64F sample = new DenseMatrix64F(sampleData.getNumCols(), 1, true, obs);
		DenseMatrix64F rotated = new DenseMatrix64F(numComps, 1);
		CommonOps.subtract(sample, mean, sample);
		CommonOps.mult(loadings, sample, rotated);
		return rotated.data;
	}

	@Override
	public double getErrorMetric(double[] obs) {

		double[] eig = applyLoadings(obs);
		double[] reproj = reproject(eig);
		double total = 0;
		for(int i = 0; i < reproj.length; i++) {
			double d = obs[i] - reproj[i];
			total += d * d;
		}
		return Math.sqrt(total);
	}

	@Override
	public double[] getLoadingVector(int var) {

		if(var < 0 || var >= numComps) {
			throw new IllegalArgumentException("Invalid component");
		}
		DenseMatrix64F loadingVector = new DenseMatrix64F(1, sampleData.numCols);
		CommonOps.extract(loadings, var, var + 1, 0, sampleData.numCols, loadingVector, 0, 0);
		return loadingVector.data;
	}

	@Override
	public double[] getScoreVector(int obs) {

		DenseMatrix64F scores = new DenseMatrix64F(1, sampleData.numCols);
		CommonOps.extract(loadings, obs, obs + 1, 0, sampleData.numCols, scores, 0, 0);
		return scores.data;
	}

	@Override
	public void initialize(int numObs, int numVars) {

		sampleData.reshape(numObs, numVars, false);
		mean = new double[numObs];
		numComps = -1;
	}

	public double[] reproject(double[] scoreVector) {

		DenseMatrix64F sample = new DenseMatrix64F(sampleData.getNumCols(), 1);
		DenseMatrix64F rotated = DenseMatrix64F.wrap(numComps, 1, scoreVector);
		CommonOps.multTransA(loadings, rotated, sample);
		DenseMatrix64F mean = DenseMatrix64F.wrap(sampleData.getNumCols(), 1, this.mean);
		CommonOps.add(sample, mean, sample);
		return sample.data;
	}
}
