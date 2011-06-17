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
 * 
 */
package org.jplot2d.tex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This Class contains the simplified model of MathML. The complete model is
 * described in: <a href="http://www.w3.org/TR/MathML2/appendixd.html">Document
 * Object Model for MathML</a>
 * <p>
 * The corresponding MathML DOM Interface is MathMLMathElement
 * 
 * @author Jingjing Li
 * 
 */
public abstract class MathElement {

	public enum MathVariant {
		NORMAL, BOLD, ITALIC; // bold_italic,
		// double_struck, bold_fraktur, script, bold_script, fraktur,
		// SANS_SERIF,
		// bold_sans_serif, sans_serif_italic, sans_serif_bold_italic,
		// MONOSPACE
		public String mathCommand() {
			switch (this) {
			case NORMAL:
				return "\\mathrm";
			case BOLD:
				return "\\mathbf";
			case ITALIC:
				return "\\mathit";
			}
			return null;
		}

		public String textCommand() {
			switch (this) {
			case NORMAL:
				return "\\textrm";
			case BOLD:
				return "\\textbf";
			case ITALIC:
				return "\\textit";
			}
			return null;
		}
	}

	/**
	 * The Null MathElement is a special MathElement which represent a empty
	 * element.
	 */
	public static final MathElement EMPTY = new MathElement() {

		public String toMathML() {
			return "<mrow></mrow>";
		}

		public void fillTeXString(TeXStringBuilder sb) {
			sb.append("{}");
		}

	};

	public abstract String toMathML();

	public abstract void fillTeXString(TeXStringBuilder sb);

	protected String toTeXString() {
		TeXStringBuilder sb = new TeXStringBuilder();
		this.fillTeXString(sb);
		if (sb.isMathMode()) {
			sb.append('$');
		}
		return sb.toString();
	}

	private String str;

	public String toString() {
		if (str == null) {
			str = toTeXString();
		}
		return str;
	}

	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof MathElement) {
			return this.toString().equals(((MathElement) anObject).toString());
		}
		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * MathML DOM Interface: MathMLPresentationElement
	 */
	public static abstract class PElement extends MathElement {
	}

	/**
	 * MathML DOM Interface: MathMLPresentationContainer
	 */
	public static abstract class PContainer extends PElement {
		protected final List<PElement> pl = new ArrayList<PElement>();

		public int size() {
			return pl.size();
		}

		public List<PElement> getElementList() {
			return pl;
		}

		public PElement get(int index) {
			return pl.get(index);
		}

		protected void fillMathML(StringBuilder sb) {
			if (pl.size() == 0) {
				return;
			} else {
				for (Iterator<PElement> itr = pl.iterator(); itr.hasNext();) {
					PElement p = itr.next();
					sb.append(p.toMathML());
				}
			}
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (pl.size() == 0) {
				return;
			} else {
				for (Iterator<PElement> itr = pl.iterator(); itr.hasNext();) {
					PElement p = itr.next();
					p.fillTeXString(sb);
				}
			}
		}

		/**
		 * Check if a Mtext child exist in this Mstyle, recursing into mrow.
		 * 
		 * @return
		 */
		protected boolean isMtextChildExist() {
			for (Iterator<PElement> itr = pl.iterator(); itr.hasNext();) {
				PElement p = itr.next();
				if (p instanceof Mtext) {
					return true;
				}
				if (p instanceof Mrow) {
					if (((Mrow) p).isMtextChildExist()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	/**
	 * Horizontally Group Sub-Expressions
	 */
	public static class Mrow extends PContainer {
		public Mrow() {

		}

		public Mrow(List<PElement> mes) {
			pl.addAll(mes);
		}

		public String toMathML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<mrow>");
			fillMathML(sb);
			sb.append("</mrow>");
			return sb.toString();
		}

		public void fillTeXString(TeXStringBuilder sb) {
			sb.append("{");
			super.fillTeXString(sb);
			sb.append("}");
		}

		/**
		 * As a top level container, the toString not contains the pair of {}
		 */
		protected String toTeXString() {
			TeXStringBuilder sb = new TeXStringBuilder();
			super.fillTeXString(sb);
			if (sb.isMathMode()) {
				sb.append('$');
			}
			return sb.toString();
		}
	}

	public static class Mstyle extends PContainer {
		protected final MathVariant _mathvariant;

		public Mstyle(MathVariant mathvariant, PElement pe) {
			_mathvariant = mathvariant;
			if (pe instanceof Mrow) {
				pl.addAll(((Mrow) pe).pl);
			} else {
				pl.add(pe);
			}
		}

		public MathVariant getMathVariant() {
			return _mathvariant;
		}

		public String toMathML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<mstyle mathvariant=" + _mathvariant + ">");
			fillMathML(sb);
			sb.append("</mstyle>");
			return sb.toString();
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isTextMode()) {
				sb.append(_mathvariant.textCommand());
				sb.append('{');
				super.fillTeXString(sb);
				sb.append('}');
			} else {
				if (isMtextChildExist()) {
					sb.append(_mathvariant.textCommand());
					sb.append('{');
					sb.goTextMode();
					super.fillTeXString(sb);
					sb.goMathMode();
					sb.append('}');
				} else {
					sb.append(_mathvariant.mathCommand());
					sb.append('{');
					super.fillTeXString(sb);
					sb.append('}');
				}
			}
		}
	}

	/**
	 * Extends of Math, multiple lines of MathElement
	 */
	public static class XLines extends MathElement {
		private final List<MathElement> mel = new ArrayList<MathElement>();

		public XLines(List<MathElement> xlines) {
			mel.addAll(xlines);
		}

		public int size() {
			return mel.size();
		}

		public MathElement[] getElements() {
			MathElement[] pa = new MathElement[mel.size()];
			return mel.toArray(pa);
		}

		public MathElement get(int index) {
			return mel.get(index);
		}

		public String toMathML() {
			StringBuilder sb = new StringBuilder();
			sb.append("<xlines>");

			for (Iterator<MathElement> itr = mel.iterator(); itr.hasNext();) {
				MathElement p = itr.next();
				sb.append(p.toMathML());
			}
			sb.append("</xlines>");
			return sb.toString();
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isMathMode()) {
				throw new IllegalStateException();
			}

			Iterator<MathElement> itr = mel.iterator();
			MathElement p = itr.next();
			p.fillTeXString(sb);
			while (itr.hasNext()) {
				if (sb.isMathMode()) {
					sb.append('$');
					sb.goTextMode();
				}
				sb.append("\\n");
				p = itr.next();
				p.fillTeXString(sb);
			}
		}
	}

	/**
	 * Presentation Token
	 */
	public static abstract class PToken extends PElement {

		protected String _string;

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isTextMode()) {
				sb.goMathMode();
				sb.append('$');
			}
			sb.append(escape());
		}

		protected String escape() {
			return _string;
		}

		public String getToken() {
			return _string;
		}
	}

	public static class Mi extends PToken {
		public Mi(String s) {
			_string = s;
		}

		public Mi(char c) {
			_string = String.valueOf(c);
		}

		public String toMathML() {
			return "<mi>" + _string + "</mi>";
		}

		public String escape() {
			return escape(_string);
		}
	}

	public static class Mn extends PToken {
		public Mn(String s) {
			_string = s;
		}

		public String toMathML() {
			return "<mn>" + _string + "</mn>";
		}

		public String escape() {
			if (_string.startsWith("-")) {
				return "{" + _string + "}";
			} else {
				return _string;
			}
		}
	}

	public static class Mo extends PToken {
		public Mo(String s) {
			_string = s;
		}

		public String toMathML() {
			return "<mo>" + _string + "</mo>";
		}
	}

	public static class Mtext extends PToken {
		public Mtext(String s) {
			_string = s;
		}

		public String toMathML() {
			return "<mtext>" + _string + "</mtext>";
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isMathMode()) {
				sb.append('$');
				sb.goTextMode();
			}
			sb.append(escape());
		}

		public String escape() {
			return escape(_string);
		}
	}

	public static class Msub extends PElement {
		public PElement base;

		public PElement subscript;

		public Msub(PElement base, PElement subscript) {
			if (base == null) {
				base = new Mrow();
			}
			this.base = base;
			this.subscript = subscript;
		}

		public String toMathML() {
			return "<msub>" + base.toMathML() + subscript.toMathML()
					+ "</msub>";
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isTextMode()) {
				sb.goMathMode();
				sb.append('$');
			}
			base.fillTeXString(sb);
			sb.append("_");
			subscript.fillTeXString(sb);
		}

	}

	public static class Msup extends PElement {
		public PElement base;

		public PElement superscript;

		public Msup(PElement base, PElement superscript) {
			if (base == null) {
				base = new Mrow();
			}
			this.base = base;
			this.superscript = superscript;
		}

		public String toMathML() {
			return "<msup>" + base.toMathML() + superscript.toMathML()
					+ "</msup>";
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isTextMode()) {
				sb.goMathMode();
				sb.append('$');
			}
			base.fillTeXString(sb);
			sb.append("^");
			superscript.fillTeXString(sb);
		}
	}

	public static class Msubsup extends PElement {
		public PElement base;

		public PElement subscript;

		public PElement superscript;

		public Msubsup(PElement base, PElement sub, PElement sup) {
			if (base == null) {
				base = new Mrow();
			}
			this.base = base;
			subscript = sub;
			superscript = sup;
		}

		public String toMathML() {
			return "<msubsup>" + base.toMathML() + subscript.toMathML()
					+ superscript.toMathML() + "</msubsup>";
		}

		public void fillTeXString(TeXStringBuilder sb) {
			if (sb.isTextMode()) {
				sb.goMathMode();
				sb.append('$');
			}
			base.fillTeXString(sb);
			sb.append("_");
			subscript.fillTeXString(sb);
			sb.append("^");
			superscript.fillTeXString(sb);
		}

	}

	private static class TeXStringBuilder {

		private final StringBuilder sb = new StringBuilder();

		private boolean mathMode;

		public TeXStringBuilder() {
		}

		public void append(String string) {
			sb.append(string);
		}

		public void append(char c) {
			sb.append(c);
		}

		public boolean isTextMode() {
			return !mathMode;
		}

		public boolean isMathMode() {
			return mathMode;
		}

		public void goTextMode() {
			mathMode = false;
		}

		public void goMathMode() {
			mathMode = true;
		}

		public String toString() {
			return sb.toString();
		}
	}

	/** escape the special characters in the given string */
	public static String escape(String s) {
		return s.replace("\u005c\u005c", "\u005c\u005c\u005c\u005c")
				.replace("$", "\u005c\u005c$").replace("{", "\u005c\u005c{")
				.replace("}", "\u005c\u005c}");
	}

}
