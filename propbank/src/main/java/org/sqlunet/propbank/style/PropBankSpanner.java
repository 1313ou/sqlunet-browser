/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.propbank.style;

import android.content.Context;
import android.text.style.ImageSpan;

import org.sqlunet.propbank.R;
import org.sqlunet.style.RegExprSpanner;

import androidx.annotation.NonNull;

/**
 * Spanner for PropBank
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankSpanner extends RegExprSpanner
{
	/**
	 * Patterns
	 */
	static private final String[] patterns = {"((?:\\[\\*\\]|\\*trace\\*)(\\-?\\d*))", // *trace*-n or [*] : 1 capture //
			// static public String[] patterns = new String[] { "(\\*trace\\*)(\\-?\\d*)", // *trace*-n or [*] : 1 capture
	};

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public PropBankSpanner(@NonNull final Context context)
	{
		// trace number
		// trace
		super(PropBankSpanner.patterns, new SpanFactory[][]{ //
				new SpanFactory[]{ //
						flags -> new ImageSpan(context, R.drawable.trace), //
						flags -> new HiddenSpan(),},});
	}
}
