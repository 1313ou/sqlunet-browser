package org.sqlunet.framenet;

import org.sqlunet.framenet.sql.FnLabel;

import java.util.ArrayList;
import java.util.List;

public class Utils
{
	static public int posToPosId(final Character c)
	{
		if (c == null)
			return -1;
		switch (c)
		{
			case 'n':
				return 1;
			case 'v':
				return 2;
			case 'a':
				return 3; // adj
			case 'r':
				return 4; // adv
			case 'p':
				return 6; // prep
			case '1':
				return 7; // num
			case 'c':
				return 8; // coor
			case '!':
				return 9; // intj
			case '~':
				return 10; // art
			case '&':
				return 9; // scon
		}
		return -1;
	}


	/**
	 * Parse labels from the result set
	 *
	 * @param labelsString label string
	 * @return the labels from the result set
	 */
	public static List<FnLabel> parseLabels(final String labelsString)
	{
		if (labelsString == null)
			return null;
		List<FnLabel> result = null;
		final String[] labels = labelsString.split("\\|"); //$NON-NLS-1$
		for (final String label : labels)
		{
			final String[] fields = label.split(":"); //$NON-NLS-1$
			final String from = fields[0];
			final String to = fields[1];
			final String value = fields[2];
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
