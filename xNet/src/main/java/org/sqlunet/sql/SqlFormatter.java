/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.sqlunet.sql;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Performs formatting of basic SQL statements (DML + query).
 * SqlFormatter in Hibernate project
 *
 * @author Gavin King
 * @author Steve Ebersole
 * @author Bernard Bou for adaptation
 */
public class SqlFormatter
{
	static private final String WHITESPACE = " \n\r\f\t";

	static private final Collection<String> BEGIN_CLAUSES = new HashSet<>();
	static private final Collection<String> END_CLAUSES = new HashSet<>();
	static private final Collection<String> LOGICAL = new HashSet<>();
	static private final Collection<String> QUANTIFIERS = new HashSet<>();
	static private final Collection<String> DML = new HashSet<>();
	static private final Collection<String> MISC = new HashSet<>();
	static private final Collection<String> KEYW = new HashSet<>();

	static
	{
		BEGIN_CLAUSES.add("left");
		BEGIN_CLAUSES.add("right");
		BEGIN_CLAUSES.add("inner");
		BEGIN_CLAUSES.add("outer");
		BEGIN_CLAUSES.add("group");
		BEGIN_CLAUSES.add("order");

		END_CLAUSES.add("where");
		END_CLAUSES.add("set");
		END_CLAUSES.add("having");
		END_CLAUSES.add("join");
		END_CLAUSES.add("from");
		END_CLAUSES.add("by");
		END_CLAUSES.add("join");
		END_CLAUSES.add("into");
		END_CLAUSES.add("union");

		LOGICAL.add("and");
		LOGICAL.add("or");
		LOGICAL.add("when");
		LOGICAL.add("else");
		LOGICAL.add("end");

		QUANTIFIERS.add("in");
		QUANTIFIERS.add("all");
		QUANTIFIERS.add("exists");
		QUANTIFIERS.add("some");
		QUANTIFIERS.add("any");

		DML.add("insert");
		DML.add("update");
		DML.add("delete");

		MISC.add("select");
		MISC.add("on");

		KEYW.add("is");
		KEYW.add("null");
		KEYW.add("as");
		KEYW.add("using");
		KEYW.add("union");
		KEYW.add("case");
		KEYW.add("then");
		KEYW.add("between");
		KEYW.add("in");
		KEYW.add("like");
		KEYW.add("asc");
		KEYW.add("desc");
		KEYW.add("not");
	}

	static private final String INDENT_STRING = "    ";
	static private final String INITIAL = ""; //"\n    ";

	@NonNull
	static public CharSequence format(@NonNull String source)
	{
		return new FormatProcess(source, false).perform();
	}

	@NonNull
	static public CharSequence styledFormat(@NonNull CharSequence source)
	{
		return new FormatProcess(source, true).perform();
	}

	static private class FormatProcess
	{
		final boolean style;

		boolean beginLine = true;
		boolean afterBeginBeforeEnd;
		boolean afterByOrSetOrFromOrSelect;
		boolean afterOn;
		boolean afterBetween;
		boolean afterInsert;
		int inFunction;
		int parensSinceSelect;
		private final LinkedList<Integer> parenCounts = new LinkedList<>();
		private final LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<>();

		int indent = 1;

		final SpannableStringBuilder result = new SpannableStringBuilder();
		@NonNull
		final StringTokenizer tokens;
		String lastToken;
		String token;
		String lcToken;

		FormatProcess(@NonNull CharSequence sql, boolean style)
		{
			this.style = style;
			this.tokens = new StringTokenizer(sql.toString(), "()+*/-=<>'`\"[]," + WHITESPACE, true);
		}

		@NonNull
		CharSequence perform()
		{
			initial();

			while (this.tokens.hasMoreTokens())
			{
				this.token = this.tokens.nextToken();
				this.lcToken = this.token.toLowerCase(Locale.ROOT);

				// single quote
				if ("'".equals(this.token))
				{
					String t;
					do
					{
						t = this.tokens.nextToken();
						this.token += t;
					}
					// cannot handle single quotes
					while (!"'".equals(t) && this.tokens.hasMoreTokens());
				}
				// double quote
				else if ("\"".equals(this.token))
				{
					String t;
					do
					{
						t = this.tokens.nextToken();
						this.token += t;
					}
					while (!"\"".equals(t));
				}

				// comma
				if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token))
				{
					commaAfterByOrFromOrSelect();
				}
				else if (this.afterOn && ",".equals(this.token))
				{
					commaAfterOn();
				}

				// parenthesis
				else if ("(".equals(this.token))
				{
					openParen();
				}
				else if (")".equals(this.token))
				{
					closeParen();
				}

				// left/right/inner/outer/group/order/right/inner/outer/group/order
				else if (BEGIN_CLAUSES.contains(this.lcToken))
				{
					beginNewClause();
				}

				// where/set/having/join/from/by/join/into/union
				else if (END_CLAUSES.contains(this.lcToken))
				{
					endNewClause();
				}

				// select
				else if ("select".equals(this.lcToken))
				{
					select();
				}

				// insert/update/delete
				else if (DML.contains(this.lcToken))
				{
					updateOrInsertOrDelete();
				}

				// other
				else if ("values".equals(this.lcToken))
				{
					values();
				}

				else if ("on".equals(this.lcToken))
				{
					on();
				}

				else if (this.afterBetween && "and".equals(this.lcToken))
				{
					misc();
					this.afterBetween = false;
				}

				else if (LOGICAL.contains(this.lcToken))
				{
					logical();
				}

				else if (isWhitespace(this.token))
				{
					white();
				}

				else
				{
					misc();
				}

				if (!isWhitespace(this.token))
				{
					this.lastToken = this.lcToken;
				}

			}
			return this.result;
		}

		private void commaAfterOn()
		{
			out();
			this.indent--;
			newline();
			this.afterOn = false;
			this.afterByOrSetOrFromOrSelect = true;
		}

		private void commaAfterByOrFromOrSelect()
		{
			out();
			newline();
		}

		private void openParen()
		{
			if (isFunctionName(this.lastToken) || this.inFunction > 0)
			{
				this.inFunction++;
			}
			this.beginLine = false;
			if (this.inFunction > 0)
			{
				out();
			}
			else
			{
				out();
				if (!this.afterByOrSetOrFromOrSelect)
				{
					this.indent++;
					newline();
					this.beginLine = true;
				}
			}
			this.parensSinceSelect++;
		}

		private void closeParen()
		{
			this.parensSinceSelect--;
			if (this.parensSinceSelect < 0)
			{
				this.indent--;
				this.parensSinceSelect = this.parenCounts.removeLast();
				this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast();
			}
			if (this.inFunction > 0)
			{
				this.inFunction--;
				out();
			}
			else
			{
				if (!this.afterByOrSetOrFromOrSelect)
				{
					this.indent--;
					newline();
				}
				out();
			}
			this.beginLine = false;
		}

		private void select()
		{
			out_keyw();
			this.indent++;
			newline();
			this.parenCounts.addLast(this.parensSinceSelect);
			this.afterByOrFromOrSelects.addLast(this.afterByOrSetOrFromOrSelect);
			this.parensSinceSelect = 0;
			this.afterByOrSetOrFromOrSelect = true;
		}

		private void updateOrInsertOrDelete()
		{
			out_keyw();
			this.indent++;
			this.beginLine = false;
			if ("update".equals(this.lcToken))
			{
				newline();
			}
			if ("insert".equals(this.lcToken))
			{
				this.afterInsert = true;
			}
		}

		private void beginNewClause()
		{
			if (!this.afterBeginBeforeEnd)
			{
				if (this.afterOn)
				{
					this.indent--;
					this.afterOn = false;
				}
				this.indent--;
				newline();
			}
			out_keyw();
			this.beginLine = false;
			this.afterBeginBeforeEnd = true;
		}

		private void endNewClause()
		{
			if (!this.afterBeginBeforeEnd)
			{
				this.indent--;
				if (this.afterOn)
				{
					this.indent--;
					this.afterOn = false;
				}
				newline();
			}
			out_keyw();
			if (!"union".equals(this.lcToken))
			{
				this.indent++;
			}
			newline();
			this.afterBeginBeforeEnd = false;
			this.afterByOrSetOrFromOrSelect = "by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken);
		}

		private void values()
		{
			this.indent--;
			newline();
			out_keyw();
			this.indent++;
			newline();
		}

		private void logical()
		{
			if ("end".equals(this.lcToken))
			{
				this.indent--;
			}
			newline();
			out_keyw();
			this.beginLine = false;
		}

		private void on()
		{
			this.indent++;
			this.afterOn = true;
			newline();
			out_keyw();
			this.beginLine = false;
		}

		private void misc()
		{
			if (KEYW.contains(this.lcToken))
			{
				out_keyw();
			}
			else
			{
				out_misc();
			}
			if ("between".equals(this.lcToken))
			{
				this.afterBetween = true;
			}
			if (this.afterInsert)
			{
				newline();
				this.afterInsert = false;
			}
			else
			{
				this.beginLine = false;
				if ("case".equals(this.lcToken))
				{
					this.indent++;
				}
			}
		}

		// test functions

		static private boolean isFunctionName(@NonNull String tok)
		{
			final char begin = tok.charAt(0);
			final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
			return isIdentifier &&
					!LOGICAL.contains(tok) &&
					!END_CLAUSES.contains(tok) &&
					!QUANTIFIERS.contains(tok) &&
					!DML.contains(tok) &&
					!MISC.contains(tok);
		}

		static private boolean isWhitespace(@NonNull CharSequence token)
		{
			return WHITESPACE.contains(token);
		}

		// output

		private void initial()
		{
			this.result.append(INITIAL);
		}

		private void white()
		{
			if (!this.beginLine)
			{
				this.result.append(" ");
			}
		}

		private void newline()
		{
			this.result.append("\n");
			for (int i = 0; i < this.indent; i++)
			{
				this.result.append(INDENT_STRING);
			}
			this.beginLine = true;
		}

		private void out()
		{
			this.result.append(this.token);
		}

		private void out_keyw()
		{
			if (this.style)
			{
				append(this.result, this.token, new ForegroundColorSpan(0xFF800080), new StyleSpan(Typeface.BOLD));
			}
			else
			{
				out();
			}
		}

		private void out_misc()
		{
			if (this.style)
			{
				switch (this.token.charAt(0))
				{
					case '\'':
						append(this.result, this.token, new ForegroundColorSpan(0xFF008000));
						break;
					case '?':
						append(this.result, this.token, new ForegroundColorSpan(Color.RED));
						break;
					default:
						out();
						break;
				}
			}
			else
			{
				out();
			}
		}

		/**
		 * Append text with style
		 *
		 * @param sb    spannable string builder
		 * @param text  text
		 * @param spans spans to apply
		 */
		static private void append(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence text, @NonNull final Object... spans)
		{
			if (text == null || text.length() == 0)
			{
				return;
			}

			final int from = sb.length();
			sb.append(text);
			final int to = sb.length();

			for (final Object span : spans)
			{
				sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
}
