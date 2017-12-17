package org.sqlunet.propbank.style;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;

import org.sqlunet.propbank.R;
import org.sqlunet.style.RegExprSpanner;

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
	public PropBankSpanner(final Context context)
	{
		super(PropBankSpanner.patterns, new SpanFactory[][]{ //
				new SpanFactory[]{ //
						new SpanFactory() // trace
						{
							@NonNull
							@Override
							public Object makeSpans(long flags)
							{
								return new ImageSpan(context, R.drawable.trace);
							}
						}, //

						new SpanFactory() // trace number
						{
							@NonNull
							@Override
							public Object makeSpans(long flags)
							{
								return new HiddenSpan();
							}
						},},});
	}
}
