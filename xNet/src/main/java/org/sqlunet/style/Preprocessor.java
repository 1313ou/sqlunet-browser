/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
		 * @param regexpr     regexpr
		 * @param replacement replacement
		 */
		Replacer(@NonNull final String regexpr, final String replacement)
		{
			this.pattern = Pattern.compile(regexpr);
			this.replacement = replacement;
		}

		/**
		 * Replace
		 *
		 * @param input input
		 * @return output
		 */
		String replace(@NonNull final CharSequence input)
		{
			final Matcher matcher = this.pattern.matcher(input);
			return matcher.replaceAll(this.replacement);
		}

		@NonNull
		@Override
		public String toString()
		{
			return this.pattern + " -> " + this.replacement;
		}
	}

	/**
	 * Array of replacers
	 */
	@NonNull
	private final Replacer[] replacers;

	/**
	 * Constructor
	 *
	 * @param data regexpr-replacement pairs
	 */
	protected Preprocessor(@NonNull final String... data)
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
	 * @param input input
	 * @return output
	 */
	@Nullable
	public CharSequence process(@Nullable final CharSequence input)
	{
		if (input == null)
		{
			return null;
		}
		String string = input.toString();
		for (final Replacer replacer : this.replacers)
		{
			string = replacer.replace(string);
		}
		return string;
	}
}