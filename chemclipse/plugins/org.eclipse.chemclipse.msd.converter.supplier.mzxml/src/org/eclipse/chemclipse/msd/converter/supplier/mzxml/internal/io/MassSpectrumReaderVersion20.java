/*******************************************************************************
 * Copyright (c) 2015, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model.DataProcessing;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model.MsRun;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model.Peaks;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model.Scan;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.IVendorMassSpectra;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorMassSpectra;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MassSpectrumReaderVersion20 extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MassSpectrumReaderVersion20.class);
	private String contextPath;

	public MassSpectrumReaderVersion20(String contextPath) {

		this.contextPath = contextPath;
	}

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorMassSpectrum massSpectrum = null;
		//
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(IConstants.NODE_MS_RUN);
			//
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			MsRun msrun = (MsRun)unmarshaller.unmarshal(nodeList.item(0));
			//
			massSpectrum = new VendorMassSpectrum();
			massSpectrum.setFile(file);
			massSpectrum.setIdentifier(file.getName());
			for(DataProcessing dataProcessing : msrun.getDataProcessing()) {
				massSpectrum.setMassSpectrumType((short)(dataProcessing.isCentroided() ? 0 : 1));
			}
			//
			for(Scan scan : msrun.getScan()) {
				/*
				 * Get the ions.
				 */
				Peaks peaks = scan.getPeaks();
				ByteBuffer byteBuffer = ByteBuffer.wrap(peaks.getValue());
				/*
				 * Byte Order
				 */
				String byteOrder = peaks.getByteOrder();
				if(byteOrder != null && byteOrder.equals("network")) {
					byteBuffer.order(ByteOrder.BIG_ENDIAN);
				} else {
					byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				}
				/*
				 * Precision
				 */
				double[] values;
				BigInteger precision = peaks.getPrecision();
				if(precision != null && precision.intValue() == 64) {
					DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
					values = new double[doubleBuffer.capacity()];
					for(int index = 0; index < doubleBuffer.capacity(); index++) {
						values[index] = new Double(doubleBuffer.get(index));
					}
				} else {
					FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
					values = new double[floatBuffer.capacity()];
					for(int index = 0; index < floatBuffer.capacity(); index++) {
						values[index] = new Double(floatBuffer.get(index));
					}
				}
				//
				for(int peakIndex = 0; peakIndex < values.length - 1; peakIndex += 2) {
					/*
					 * Get m/z and intensity (m/z-int)
					 */
					double mz = AbstractIon.getIon(values[peakIndex]);
					float intensity = (float)values[peakIndex + 1];
					try {
						if(intensity >= VendorIon.MIN_ABUNDANCE && intensity <= VendorIon.MAX_ABUNDANCE) {
							IVendorIon ion = new VendorIon(mz, intensity);
							massSpectrum.addIon(ion);
						}
					} catch(AbundanceLimitExceededException e) {
						logger.warn(e);
					} catch(IonLimitExceededException e) {
						logger.warn(e);
					}
				}
			}
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		}
		//
		IVendorMassSpectra massSpectra = new VendorMassSpectra();
		massSpectra.setName(file.getName());
		massSpectra.addMassSpectrum(massSpectrum);
		return massSpectra;
	}
}