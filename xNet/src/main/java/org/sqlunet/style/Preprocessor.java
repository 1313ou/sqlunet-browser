package org.sqlunet.style;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessor
 * 
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Preprocessor
{
	/**
	 * Replacer
	 * 
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	static class Replacer
	{
		/**
		 * Replaced pattern
		 */
		final private Pattern pattern;

		/**
		 * Replacement string
		 */
		final private String replacement;

		/**
		 * Constructor
		 * 
		 * @param regexpr
		 *        regexpr
		 * @param replacement
		 *        replacement
		 */
		Replacer(final String regexpr, final String replacement)
		{
			this.pattern = Pattern.compile(regexpr);
			this.replacement = replacement;
		}

		/**
		 * Replace
		 * 
		 * @param input
		 *        input
		 * @return output
		 */
		public String replace(final String input)
		{
			final Matcher matcher = this.pattern.matcher(input);
			return matcher.replaceAll(this.replacement);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return this.pattern + " -> " + this.replacement; //$NON-NLS-1$
		}
	}

	/**
	 * Array of replacers
	 */
	private final Replacer[] replacers;

	/**
	 * Constructor
	 * 
	 * @param data
	 *        regexpr-replacement pairs
	 */
	protected Preprocessor(final String... data)
	{
		final int n = data.length / 2;
		this.replacers = new Replacer[n];
		int j = 0;
		for (int i = 0; i < n; i++)
		{
			this.replacers[i] = new Replacer(data[j], data[j + 1]);
			j += 2;
		}
	}

	/**
	 * Process
	 * 
	 * @param input
	 *        input
	 * @return output
	 */
	public CharSequence process(final CharSequence input)
	{
		if (input == null)
			return null;
		String string = input.toString();
		for (final Replacer replacer : this.replacers)
		{
			string = replacer.replace(string);
		}
		return string;
	}
}