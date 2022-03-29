/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.BaselineSelectionPaintListener;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ICustomSelectionHandler;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class TimeRangesPeakChart extends ChromatogramPeakChart {

	private Cursor defaultCursor;
	private BaselineSelectionPaintListener baselineSelectionPaintListener;
	//
	private TimeRangeAdjustmentHandler timeRangeAdjustmentHandler = new TimeRangeAdjustmentHandler();
	private TimeRangeSelectionHandler timeRangeSelectionHandler = new TimeRangeSelectionHandler();
	//
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	//
	private TimeRangesUI timeRangesUI;
	private TimeRanges timeRanges;
	private TimeRangeMarker timeRangeMarker;
	//
	private IUpdateListener updateListener;

	public TimeRangesPeakChart(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void update(TimeRange timeRange) {

		TimeRangeSelector.updateTimeRangeUI(timeRangesUI, timeRange, getBaseChart());
	}

	public void update(TimeRanges timeRanges) {

		if(timeRangesUI != null) {
			timeRangesUI.setInput(timeRanges);
		}
		update(timeRangesUI, timeRanges);
	}

	public void update(TimeRangesUI timeRangesUI, TimeRanges timeRanges) {

		this.timeRangesUI = timeRangesUI;
		this.timeRanges = timeRanges;
		timeRangeAdjustmentHandler.update(this.timeRangesUI, this.timeRanges);
		timeRangeSelectionHandler.update(this.timeRangesUI, this.timeRanges);
		updateTimeRangeMarker();
	}

	public void updateTimeRangeMarker() {

		timeRangeMarker.getTimeRanges().clear();
		if(timeRanges != null) {
			/*
			 * Show the time range composite.
			 */
			setTimeRangesVisible(true);
			timeRangeMarker.getTimeRanges().addAll(timeRanges.values());
		} else {
			/*
			 * Hide the time range composite.
			 */
			setTimeRangesVisible(false);
		}
		getBaseChart().redraw();
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		super.handleMouseDownEvent(event);
		if(isControlKeyPressed(event)) {
			startBaselineSelection(event.x, event.y);
			setCursor(SWT.CURSOR_CROSS);
		}
	}

	@Override
	public void handleMouseMoveEvent(Event event) {

		super.handleMouseMoveEvent(event);
		if(isControlKeyPressed(event)) {
			if(xStart > 0 && yStart > 0) {
				trackBaselineSelection(event.x, event.y);
			}
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		super.handleMouseUpEvent(event);
		if(isControlKeyPressed(event)) {
			stopBaselineSelection(event.x, event.y);
			adjustTimeRange(event);
			setCursorDefault();
			resetSelectedRange();
		}
	}

	private void createControl() {

		defaultCursor = getBaseChart().getCursor();
		timeRangeMarker = addTimeRangeMarker(this);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.addHandledEventProcessor(timeRangeAdjustmentHandler);
		chartSettings.addHandledEventProcessor(timeRangeSelectionHandler);
		applySettings(chartSettings);
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		//
		getBaseChart().addCustomRangeSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				assignCurrentRangeSelection();
			}
		});
	}

	private TimeRangeMarker addTimeRangeMarker(ScrollableChart scrollableChart) {

		BaseChart baseChart = scrollableChart.getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		TimeRangeMarker timeRangeMarker = new TimeRangeMarker(baseChart);
		plotArea.addCustomPaintListener(timeRangeMarker);
		return timeRangeMarker;
	}

	private void adjustTimeRange(Event event) {

		TimeRange timeRange = TimeRangeSelector.selectRange(getBaseChart(), event, xStart, xStop, timeRanges);
		if(timeRange != null) {
			updateTimeRange(timeRange); // Update
		} else {
			/*
			 * Add a new TimeRange
			 */
			if(timeRanges != null) {
				InputDialog inputDialog = new InputDialog(event.display.getActiveShell(), "Time Range", "Add a new time range.", "", new IInputValidator() {

					@Override
					public String isValid(String newText) {

						if(newText == null || newText.isEmpty() || newText.isBlank()) {
							return "Please define a new time range ID.";
						} else {
							for(TimeRange timeRangeX : timeRanges.values()) {
								if(timeRangeX.getIdentifier().equals(newText)) {
									return "The time range ID exists already.";
								}
							}
						}
						return null;
					}
				});
				/*
				 * Add a new time range.
				 */
				if(inputDialog.open() == Window.OK) {
					String identifier = inputDialog.getValue().trim();
					TimeRange timeRangeAdd = new TimeRange(identifier, 0, 0);
					timeRanges.add(timeRangeAdd);
					timeRangesUI.setInput(timeRanges);
					updateTimeRange(timeRangeAdd);
				}
			}
		}
	}

	private void updateTimeRange(TimeRange timeRange) {

		BaseChart baseChart = getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		Point rectangle = baseChart.getPlotArea().getSize();
		int width = rectangle.x;
		//
		if(width != 0) {
			/*
			 * Selected Width
			 */
			double factorWidth = 100.0d / width;
			double percentageStartWidth = (factorWidth * xStart) / 100.0d;
			double percentageStopWidth = (factorWidth * xStop) / 100.0d;
			/*
			 * Retention Time
			 */
			IAxis retentionTime = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			Range millisecondsRange = retentionTime.getRange();
			double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
			int startRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
			int stopRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
			timeRange.update(startRetentionTime, stopRetentionTime);
			TimeRangeSelector.updateTimeRangeUI(timeRangesUI, timeRange, baseChart);
			fireTimeRangeUpdate();
		}
	}

	private boolean isControlKeyPressed(Event event) {

		return (event.stateMask & SWT.MOD1) == SWT.MOD1;
	}

	private void startBaselineSelection(int x, int y) {

		xStart = x;
		yStart = y;
		/*
		 * Set the start point.
		 */
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
	}

	private void trackBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
		//
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
		baselineSelectionPaintListener.setX2(xStop);
		baselineSelectionPaintListener.setY2(yStop);
		//
		redrawChart();
	}

	private void stopBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
	}

	private void resetSelectedRange() {

		baselineSelectionPaintListener.reset();
		//
		xStart = 0;
		yStart = 0;
		xStop = 0;
		yStop = 0;
		//
		redrawChart();
	}

	private void setCursor(int cursorId) {

		getBaseChart().setCursor(getBaseChart().getDisplay().getSystemCursor(cursorId));
	}

	private void setCursorDefault() {

		getBaseChart().setCursor(defaultCursor);
	}

	private void redrawChart() {

		getBaseChart().redraw();
	}

	private void setTimeRangesVisible(boolean visible) {

		if(timeRangesUI != null) {
			timeRangesUI.setVisible(visible);
			Object layoutData = timeRangesUI.getLayoutData();
			if(layoutData instanceof GridData) {
				GridData gridData = (GridData)layoutData;
				gridData.exclude = !visible;
			}
			Composite parent = timeRangesUI.getParent();
			parent.layout(true);
			parent.redraw();
		}
	}

	private void fireTimeRangeUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}