/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.speak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

public class Pronunciation implements Comparable<Pronunciation>
{
	public final String ipa;

	public final String variety;

	public Pronunciation(final String ipa, final String variety)
	{
		this.ipa = ipa;
		this.variety = variety;
	}

	public boolean hasVariety()
	{
		return variety != null;
	}

	public static final Comparator<Pronunciation> COMPARATOR = (p1, p2) -> {

		if (p1.variety == null && p2.variety == null)
		{
			return 0;
		}
		if (p1.variety == null)
		{
			return -1;
		}
		if (p2.variety == null)
		{
			return 1;
		}

		// priority
		int priority1 = priority(p1.variety);
		int priority2 = priority(p2.variety);
		int c = Integer.compare(priority1, priority2);
		if (c != 0)
		{
			return c;
		}

		// name
		c = p1.variety.compareToIgnoreCase(p2.variety);
		if (c == 0)
		{
			return 0;
		}
		return p1.ipa.compareTo(p2.ipa);
	};

	private static int priority(final String s)
	{
		switch (s)
		{
			case "GB":
				return -5;
			case "US":
				return -4;
			default:
				return 0;
		}
	}

	@Override
	public int compareTo(final Pronunciation that)
	{
		return COMPARATOR.compare(this, that);
	}

	@NonNull
	@Override
	public String toString()
	{
		return variety == null ? String.format("/%s/", ipa) : String.format("[%s] /%s/", variety, ipa);
	}

	public static String[] toStrings(final List<Pronunciation> pronunciations)
	{
		int n = pronunciations.size();
		String[] result = new String[n];
		for (int i = 0; i < n; i++)
		{
			result[i] = pronunciations.get(i).toString();
		}
		return result;
	}

	private static final Pattern PATTERN = Pattern.compile("\\[(..)\\] /(.*)/");

	private static final Pattern PATTERN2 = Pattern.compile("/(.*)/");

	public static List<Pronunciation> pronunciations(final String pronunciationBundle)
	{
		if (pronunciationBundle == null)
		{
			return null;
		}
		String[] pronunciations = pronunciationBundle.split(",");
		List<Pronunciation> result = new ArrayList<>();
		for (String pronunciation : pronunciations)
		{
			pronunciation = pronunciation.trim();
			Matcher m = PATTERN.matcher(pronunciation);
			if (m.find())
			{
				String ipa = m.group(2);
				String country = m.group(1);
				result.add(new Pronunciation(ipa, country));
			}
			else
			{
				Matcher m2 = PATTERN2.matcher(pronunciation);
				if (m2.find())
				{
					String ipa = m2.group(1);
					result.add(new Pronunciation(ipa, null));
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	public static String sortedPronunciations(String pronunciationBundle)
	{
		if (pronunciationBundle == null)
		{
			return null;
		}
		List<Pronunciation> pronunciations = pronunciations(pronunciationBundle);
		return String.join(",", toStrings(pronunciations));
	}
}
