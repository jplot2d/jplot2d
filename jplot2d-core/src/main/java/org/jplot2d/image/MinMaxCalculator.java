/**
 * Copyright 2010-2013 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.image;

import org.jplot2d.data.ImageData;
import org.jplot2d.data.ImageDataBuffer;
import org.jplot2d.data.SingleBandImageData;

/**
 * The limits calculator to produce the upper and lower limits to the min and max values.
 * 
 * @author Jingjing Li
 * 
 */
public class MinMaxCalculator implements LimitsCalculator {

	public MinMaxCalculator() {

	}

	public double[] calcLimits(ImageData[] dataArray) {
		double min = Double.NaN;
		double max = Double.NaN;
		for (ImageData data : dataArray) {
			double[] minmax = null;
			if (data instanceof SingleBandImageData) {
				ImageDataBuffer dbuf = ((SingleBandImageData) data).getDataBuffer();
				minmax = dbuf.calcMinMax(data.getWidth(), data.getHeight());
			}

			if (minmax != null) {
				double dmin = minmax[0];
				double dmax = minmax[1];
				if (Double.isNaN(min) || Double.isNaN(max)) {
					min = dmin;
					max = dmax;
				} else {
					if (min > dmin) {
						min = dmin;
					}
					if (max < dmax) {
						max = dmax;
					}
				}
			}
		}
		if (Double.isNaN(min) || Double.isNaN(max)) {
			return null;
		} else {
			return new double[] { min, max };
		}
	}
}
