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
/*
 * $Id: TeXMathParserTest.java,v 1.8 2010/08/11 09:36:23 jli Exp $
 */
package org.jplot2d.util;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jingjing Li
 * 
 */
public class TeXMathParserTest {

	private static TeXMathParser parser;

	@BeforeClass
	public static void setUpBeforeClass() {
		parser = new TeXMathParser(new StringReader(""));
	}

	private void checkParse(String src, String roundTrip, String mathml)
			throws ParseException {
		parser.ReInit(new StringReader(src));
		MathElement me = parser.parse();
		assertEquals(me.toString(), roundTrip);
		assertEquals(me.toMathML(), mathml);
	}

	private void checkEmpty(String src) throws ParseException {
		parser.ReInit(new StringReader(src));
		MathElement me = parser.parse();
		assertSame(me, MathElement.EMPTY);
	}

	private void checkException(String src) throws ParseException {
		parser.ReInit(new StringReader(src));
		try {
			parser.parse();
			fail("ParseException should be thrown");
		} catch (ParseException e) {
		}
	}

	@Test
	public void testEmpty() throws ParseException {
		checkEmpty("");
		checkEmpty("$$");
		checkEmpty("$$$$");
		checkEmpty("$$\\n$$");
		checkEmpty("$$\n$$");
		checkEmpty("\\n");
		checkEmpty("\n");
	}

	@Test
	public void testCR() throws ParseException {
		checkParse("a\\n", "a", "<mtext>a</mtext>");
		checkParse("a\n", "a", "<mtext>a</mtext>");
		checkParse("\\na", "a", "<mtext>a</mtext>");
		checkParse("\na", "a", "<mtext>a</mtext>");

		checkParse("ab\\noncommand{}", "ab\\noncommand",
				"<xlines><mtext>ab</mtext><mtext>oncommand</mtext></xlines>");
		checkParse("abc\\nd", "abc\\nd",
				"<xlines><mtext>abc</mtext><mtext>d</mtext></xlines>");
		checkParse("abc\nd", "abc\\nd",
				"<xlines><mtext>abc</mtext><mtext>d</mtext></xlines>");
		checkParse("abc\\n$d$\\ne", "abc\\n$d$\\ne",
				"<xlines><mtext>abc</mtext><mi>d</mi><mtext>e</mtext></xlines>");
		checkParse("abc\n$d$\ne", "abc\\n$d$\\ne",
				"<xlines><mtext>abc</mtext><mi>d</mi><mtext>e</mtext></xlines>");
	}

	@Test
	public void testText() throws ParseException {
		checkParse(" ", " ", "<mtext> </mtext>");
		checkParse("abc", "abc", "<mtext>abc</mtext>");
		checkParse("abc d", "abc d", "<mtext>abc d</mtext>");
		checkParse("abc\\$d", "abc\\$d", "<mtext>abc$d</mtext>");
		checkParse("abc\\{de", "abc\\{de", "<mtext>abc{de</mtext>");
		checkParse("abc\\}de", "abc\\}de", "<mtext>abc}de</mtext>");
		checkParse("abc\\\\de", "abc\\\\de", "<mtext>abc\\de</mtext>");

		// group
		checkParse("ab{cde}", "abcde",
				"<mrow><mtext>ab</mtext><mtext>cde</mtext></mrow>");
		checkParse("{abc}de", "abcde",
				"<mrow><mtext>abc</mtext><mtext>de</mtext></mrow>");
		// nested group
		checkParse(
				"ab{c{d}e}",
				"abcde",
				"<mrow><mtext>ab</mtext><mtext>c</mtext><mtext>d</mtext><mtext>e</mtext></mrow>");

		checkParse("abc$$", "abc", "<mtext>abc</mtext>");
		checkParse("abc$d$", "abc$d$",
				"<mrow><mtext>abc</mtext><mi>d</mi></mrow>");
		checkParse("abc$de$", "abc$de$",
				"<mrow><mtext>abc</mtext><mi>d</mi><mi>e</mi></mrow>");
		checkParse("$$abc", "abc", "<mtext>abc</mtext>");
		checkParse("$abc$d", "$abc$d",
				"<mrow><mi>a</mi><mi>b</mi><mi>c</mi><mtext>d</mtext></mrow>");
		checkParse("$$a$$b", "ab",
				"<mrow><mtext>a</mtext><mtext>b</mtext></mrow>");
		checkParse("$$a$$$$b", "ab",
				"<mrow><mtext>a</mtext><mtext>b</mtext></mrow>");
		checkParse("$a$$c$d$e$f", "$ac$d$e$f",
				"<mrow><mi>a</mi><mi>c</mi><mtext>d</mtext><mi>e</mi><mtext>f</mtext></mrow>");

		checkException("abc\\de");

	}

	@Test
	public void testFont() throws ParseException {
		checkParse(
				"ab\\textbf{cde}",
				"ab\\textbf{cde}",
				"<mrow><mtext>ab</mtext><mstyle mathvariant=BOLD><mtext>cde</mtext></mstyle></mrow>");
		checkParse(
				"ab\\textbf {cde}",
				"ab\\textbf{cde}",
				"<mrow><mtext>ab</mtext><mstyle mathvariant=BOLD><mtext>cde</mtext></mstyle></mrow>");

		checkException("ab\\textbf cde");
	}

	@Test
	public void testUnicode() throws ParseException {
		checkParse("\u2299", "\u2299", "<mtext>\u2299</mtext>");
		checkParse("\\u2299", "\u2299", "<mtext>\u2299</mtext>");
		checkParse("$\u2299$", "$\u2299$", "<mi>\u2299</mi>");
		checkParse("$\\u2299$", "$\u2299$", "<mi>\u2299</mi>");
	}

	@Test
	public void testMathToken() throws ParseException {
		checkParse("$a$", "$a$", "<mi>a</mi>");
		checkParse("$ab$", "$ab$", "<mrow><mi>a</mi><mi>b</mi></mrow>");
		checkParse("$a b$", "$ab$", "<mrow><mi>a</mi><mi>b</mi></mrow>");
		checkParse("$a\\$b$", "$a\\$b$",
				"<mrow><mi>a</mi><mi>$</mi><mi>b</mi></mrow>");
		checkParse("$a\\\\b$", "$a\\\\b$",
				"<mrow><mi>a</mi><mi>\\</mi><mi>b</mi></mrow>");
		checkParse("$(ab)$", "$({ab})$",
				"<mrow><mo>(</mo><mrow><mi>a</mi><mi>b</mi></mrow><mo>)</mo></mrow>");
		checkParse("$a+b$", "$a+b$",
				"<mrow><mi>a</mi><mo>+</mo><mi>b</mi></mrow>");
		checkParse("$a-b$", "$a\u2212b$",
				"<mrow><mi>a</mi><mo>\u2212</mo><mi>b</mi></mrow>");
		checkParse("$a>b$", "$a>b$",
				"<mrow><mi>a</mi><mo>></mo><mi>b</mi></mrow>");
		checkParse("$a>=b$", "$a>=b$",
				"<mrow><mi>a</mi><mo>>=</mo><mi>b</mi></mrow>");
		checkParse("$a<b$", "$a<b$",
				"<mrow><mi>a</mi><mo><</mo><mi>b</mi></mrow>");
		checkParse("$a<=b$", "$a<=b$",
				"<mrow><mi>a</mi><mo><=</mo><mi>b</mi></mrow>");
		checkParse("$a=b$", "$a=b$",
				"<mrow><mi>a</mi><mo>=</mo><mi>b</mi></mrow>");
		checkParse("$a==b$", "$a==b$",
				"<mrow><mi>a</mi><mo>==</mo><mi>b</mi></mrow>");
		checkParse(
				"$f(x,y)$",
				"$f{({x,y})}$",
				"<mrow><mi>f</mi><mrow><mo>(</mo><mrow><mi>x</mi><mo>,</mo><mi>y</mi></mrow><mo>)</mo></mrow></mrow>");

		checkException("$a\\b$");

	}

	@Test
	public void testMathGreek() throws ParseException {
		checkParse("$\\alpha$", "$\u03b1$", "<mi>\u03b1</mi>");
		checkParse("$a\\betac$", "$a\u03b2c$",
				"<mrow><mi>a</mi><mi>\u03b2</mi><mi>c</mi></mrow>");
	}

	@Test
	public void testMathNumber() throws ParseException {
		checkParse("$2+13=15$", "$2+13=15$",
				"<mrow><mn>2</mn><mo>+</mo><mn>13</mn><mo>=</mo><mn>15</mn></mrow>");
		checkParse("$2-13={-11}$", "$2\u221213={-11}$",
				"<mrow><mn>2</mn><mo>\u2212</mo><mn>13</mn><mo>=</mo><mn>-11</mn></mrow>");
		checkParse("$-2-13={-15}$", "${-2}\u221213={-15}$",
				"<mrow><mn>-2</mn><mo>\u2212</mo><mn>13</mn><mo>=</mo><mn>-15</mn></mrow>");
		checkParse("$3*10^{-2}$", "$3\u221710^{-2}$",
				"<mrow><mn>3</mn><mo>\u2217</mo><msup><mn>10</mn><mn>-2</mn></msup></mrow>");
		checkParse(
				"$3*10^-2$",
				"$3\u221710^\u22122$",
				"<mrow><mn>3</mn><mo>\u2217</mo><msup><mn>10</mn><mo>\u2212</mo></msup><mn>2</mn></mrow>");
	}

	@Test
	public void testMathScrpit() throws ParseException {
		checkParse("$10^{-2}$", "$10^{-2}$",
				"<msup><mn>10</mn><mn>-2</mn></msup>");
		checkParse("$F_2$", "$F_2$", "<msub><mi>F</mi><mn>2</mn></msub>");
		checkParse("$F_2^a$", "$F_2^a$",
				"<msubsup><mi>F</mi><mn>2</mn><mi>a</mi></msubsup>");
		checkParse(
				"$_aF_a$",
				"${}_aF_a$",
				"<mrow><msub><mrow></mrow><mi>a</mi></msub><msub><mi>F</mi><mi>a</mi></msub></mrow>");
		checkParse(
				"$^2F^2$",
				"${}^2F^2$",
				"<mrow><msup><mrow></mrow><mn>2</mn></msup><msup><mi>F</mi><mn>2</mn></msup></mrow>");
		checkParse(
				"$_a^2F_a^2$",
				"${}_a^2F_a^2$",
				"<mrow><msubsup><mrow></mrow><mi>a</mi><mn>2</mn></msubsup><msubsup><mi>F</mi><mi>a</mi><mn>2</mn></msubsup></mrow>");
		checkParse(
				"$^2_aF^2_a$",
				"${}_a^2F_a^2$",
				"<mrow><msubsup><mrow></mrow><mi>a</mi><mn>2</mn></msubsup><msubsup><mi>F</mi><mi>a</mi><mn>2</mn></msubsup></mrow>");
		checkParse(
				"V$_\\textrm{LSB}$",
				"V${}_\\textrm{LSB}$",
				"<mrow><mtext>V</mtext><msub><mrow></mrow><mstyle mathvariant=NORMAL><mtext>LSB</mtext></mstyle></msub></mrow>");

		checkException("$F_^$");
		checkException("$F^_$");
		checkException("$F_a_a$");
		checkException("$F^2^2$");
	}

	@Test
	public void testMathFont() throws ParseException {
		checkParse("$a\\mathbf{b}$", "$a\\mathbf{b}$",
				"<mrow><mi>a</mi><mstyle mathvariant=BOLD><mi>b</mi></mstyle></mrow>");
		checkParse("$a\\mathbf{bc}$", "$a\\mathbf{bc}$",
				"<mrow><mi>a</mi><mstyle mathvariant=BOLD><mi>b</mi><mi>c</mi></mstyle></mrow>");

		checkException("ab\\mathtbf cde");
	}

	@Test
	public void testMathTextFont() throws ParseException {
		checkParse("$a\\textbf{bc}$", "$a\\textbf{bc}$",
				"<mrow><mi>a</mi><mstyle mathvariant=BOLD><mtext>bc</mtext></mstyle></mrow>");
		checkParse("$a\\textbf{b c}$", "$a\\textbf{b c}$",
				"<mrow><mi>a</mi><mstyle mathvariant=BOLD><mtext>b c</mtext></mstyle></mrow>");
		checkParse(
				"$a\\textbf{b $c$d}$",
				"$a\\textbf{b $c$d}$",
				"<mrow><mi>a</mi><mstyle mathvariant=BOLD><mtext>b </mtext><mi>c</mi><mtext>d</mtext></mstyle></mrow>");

		checkException("ab\\textbf cde");
	}

}
