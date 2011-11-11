/**
 * Copyright 2010, 2011 Jingjing Li.
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
package org.jplot2d.tex;

import java.io.StringReader;

import org.jplot2d.javacc.ParseException;
import org.jplot2d.javacc.TeXMathParser;
import org.jplot2d.javacc.TokenMgrError;

/**
 * Provide a static parse method to use thread local parser.
 * 
 * @author Jingjing Li
 * 
 */
public class TeXMathUtils {

	private static final ThreadLocal<TeXMathParser> parserTL = new ThreadLocal<TeXMathParser>() {
		@Override
		protected TeXMathParser initialValue() {
			return new TeXMathParser(new StringReader(""));
		}
	};

	/**
	 * Parse the given text into a math model. If the given text is null, then
	 * null is returned. if the given text does not represent a math model, such
	 * as "", or "$$", then a MathElement.NULL is returned.
	 * 
	 * @param text
	 * @return
	 */
	public static MathElement parseText(String text) {
		if (text == null) {
			return null;
		} else {
			TeXMathParser parser = parserTL.get();
			parser.ReInit(new StringReader(text));

			MathElement model;
			try {
				model = parser.parse();
			} catch (ParseException e) {
				throw new IllegalArgumentException(
						"the syntax of text is not correct", e);
			} catch (TokenMgrError e) {
				throw new IllegalArgumentException(
						"the syntax of text is not correct", e);
			}

			return model;
		}
	}

	/**
	 * Returns the TeX string to represent the given MathElement. Unlike
	 * String.valueOf(), this method returns null when the given MathElement is
	 * null.
	 * 
	 * @param me
	 *            the math model
	 * @return the TeX string
	 */
	public static String toString(MathElement me) {
		if (me == null) {
			return null;
		}
		return me.toString();
	}
}
