/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet;

import org.sqlunet.framenet.sql.FnLabel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class Utils
{
	/**
	 * Convert WordNet pos character to pos id
	 *
	 * @param c pos character
	 * @return pos id
	 */
	static public int posToPosId(@Nullable final Character c)
	{
		if (c == null)
		{
			return -1;
		}
		/*
			# posid, pos
			'1', 'A'
			'2', 'ADV'
			'3', 'ART'
			'4', 'AVP'
			'5', 'C'
			'6', 'CCON'
			'7', 'IDIO'
			'8', 'INTJ'
			'9', 'N'
			'10', 'NUM'
			'11', 'PREP'
			'12', 'PRON'
			'13', 'SCON'
			'14', 'V'
		*/
		switch (c)
		{
			case 'n':
				return 9;
			case 'v':
				return 14;
			case 's':
			case 'a':
				return 1; // adj
			case 'r':
				return 2; // adv
			/*
			case 'p':
				return 11; // prep
			case '1':
				return 10; // num
			case '!':
				return 8; // intj
			case '~':
				return 3; // art
			case 's':
				return 13; // scon subordinating conjunction
			case '&':
				return 8; // ccon conjunction
			*/
		}
		return -1;
	}


	/**
	 * Parse labels from the result set
	 *
	 * @param labelsString label string
	 * @return the labels from the result set
	 */
	@Nullable
	static public List<FnLabel> parseLabels(@Nullable final String labelsString)
	{
		if (labelsString == null)
		{
			return null;
		}
		List<FnLabel> result = null;
		final String[] labels = labelsString.split("\\|");
		for (final String label : labels)
		{
			final String[] fields = label.split(":");
			final String from = fields.length < 1 ? null : fields[0];
			final String to = fields.length < 2 ? null : fields[1];
			final String value = fields.length < 3 ? null : fields[2];
			final String iType = fields.length < 4 ? null : fields[3];
			final String bgColor = fields.length < 5 ? null : fields[4];
			final String fgColor = fields.length < 6 ? null : fields[5];
			if (result == null)
			{
				result = new ArrayList<>();
			}
			result.add(new FnLabel(from, to, value, iType, bgColor, fgColor));
		}
		return result;
	}
}
