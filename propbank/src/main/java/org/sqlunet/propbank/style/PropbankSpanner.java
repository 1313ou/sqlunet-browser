package org.sqlunet.propbank.style;

import org.sqlunet.propbank.R;
import org.sqlunet.style.RegExprSpanner;

import android.content.Context;
import android.text.style.ImageSpan;

/**
 * Spanner for PropBank
 * 
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropbankSpanner extends RegExprSpanner
{
	/**
	 * Patterns
	 */
	private static final String[] patterns = new String[] { "((?:\\[\\*\\]|\\*trace\\*)(\\-?\\d*))", // *trace*-n or [*] : 1 capture //$NON-NLS-1$
			// static public String[] patterns = new String[] { "(\\*trace\\*)(\\-?\\d*)", // *trace*-n or [*] : 1 capture
	};

	/**
	 * Constructor
	 * 
	 * @param context
	 *        context
	 */
	public PropbankSpanner(final Context context)
	{
		super(PropbankSpanner.patterns,
				new SpanFactory[][] { //
						new SpanFactory[] { //
								new SpanFactory() // trace
								{
									@Override
									public Object makeSpans(long flags)
									{
										return new ImageSpan(context, R.drawable.trace);
									}
								}, //

								new SpanFactory() // trace number
								{
									@Override
									public Object makeSpans(long flags)
									{
										return new HiddenSpan();
									}
								}, }, });
	}
}
