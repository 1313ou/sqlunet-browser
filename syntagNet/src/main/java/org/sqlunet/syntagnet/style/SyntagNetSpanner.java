/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.style;

import android.content.Context;
import android.text.style.ImageSpan;

import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.syntagnet.R;

import androidx.annotation.NonNull;

/**
 * Spanner for SyntagNet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetSpanner extends RegExprSpanner
{
	/**
	 * Patterns
	 */
	static private final String[] patterns = {"(.*)",};

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public SyntagNetSpanner(@NonNull final Context context)
	{
		// trace number
		// trace
		super(SyntagNetSpanner.patterns, new SpanFactory[][]{ //
				new SpanFactory[]{ //
						flags -> new ImageSpan(context, R.drawable.info), //
				},
		});
	}
}
