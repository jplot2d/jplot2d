/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.axtick;

import java.util.Locale;
import java.util.TimeZone;

/**
 * A Concrete Creator for Linear TickCalculator.
 * 
 * @author Jingjing Li
 * 
 */
public class DateTickAlgorithm extends TickAlgorithm {

	private static final Locale en_US = Locale.US;

	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private static final DateTickAlgorithm DEFAULT = new DateTickAlgorithm(GMT,
			en_US);

	private TimeZone _zone;

	private Locale _locale;

	private DateTickAlgorithm(TimeZone zone, Locale aLocale) {
		_zone = zone;
		_locale = aLocale;
	}

	public static DateTickAlgorithm getInstance() {
		return DEFAULT;
	}

	public static DateTickAlgorithm getInstance(TimeZone zone, Locale aLocale) {
		if (zone.equals(GMT) && aLocale.equals(en_US)) {
			return DEFAULT;
		}
		return new DateTickAlgorithm(zone, aLocale);
	}

	public DateTickCalculator createCalculator() {
		return new DateTickCalculator(_zone, _locale);
	}

}
