/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------
 * BarChartDemo.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BarChartDemo.java,v 1.16 2004/04/29 10:06:34 mungady Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 09-Oct-2002 : Added frame centering (DG);
 * 18-Nov-2002 : Changed from DefaultCategoryDataset to DefaultTableDataset (DG);
 * 28-Oct-2003 : Changed to display gradient paint (DG);
 * 10-Nov-2003 : Renamed BarChartDemo.java (DG);
 *
 */

import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * A simple demonstration application showing how to create a bar chart.
 *
 */
public class Chart extends ApplicationFrame {
	public ChartPanel chartPanel;
	
    /**
     * Creates a chart instance.
     *
     * @param title  the frame title.
     */
    public Chart(Color[] colorsArray, float[][] coinCapitalArray) {
        super("");

        final XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries[] series = new XYSeries[colorsArray.length];
        
        for (int i=0; i<colorsArray.length; i++){
        	Color datasetColor = colorsArray[i];
        	series[i] = createSeries(i, coinCapitalArray[i]);
        	
        	dataset.addSeries(series[i]);
        }
        
        final JFreeChart chart = createChart(colorsArray, dataset);
        
        chart.setBackgroundPaint(null);     
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(620, 165));
        chartPanel.setOpaque(false);
        
        setContentPane(chartPanel);
    }

    /**
     * Returns a series (collection of datapoints).
     * 
     * @return The series.
     */
    private XYSeries createSeries(int seriesCount, float[] capitalPerTick) {
    	XYSeries series = new XYSeries(seriesCount); // chart dataset
    	
		for (int i=0; i<capitalPerTick.length; i++){			
			series.add(i+0.5, capitalPerTick[i]);
		}
        
        return series;
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(Color[] plotColors, final XYSeriesCollection data) {    	
    	final JFreeChart chart = ChartFactory.createXYLineChart("",
                "", "", data, PlotOrientation.VERTICAL, false, false, false);
    	
            XYPlot plot = (XYPlot) chart.getPlot();
            
            plot.setBackgroundPaint(Color.black);
            
            for (int i=0; i<plotColors.length; i++)
            	plot.getRenderer().setSeriesPaint(i, plotColors[i]);
            
            final NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            
            xAxis.setTickUnit(new NumberTickUnit(1)); 
            xAxis.setInverted(true);
            
        return chart;
        
    }
    
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

}