package org.sqlunet.verbnet.style;

import org.sqlunet.style.RegExprSpanner;

/**
 * VerbNet syntax processor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetSyntaxSpanner extends RegExprSpanner
{
	/**
	 * Patterns
	 */
	static private final String[] patterns = { //
			"^([^\\s\n]*)", // cat : 1 capture //
			"^[^\\s\n]* (\\p{Upper}[\\p{Lower}_\\p{Upper}]*)", // value : 1 capture //
	};

	/**
	 * Factories
	 */
	static private final SpanFactory[][] factories = {new SpanFactory[]{VerbNetFactories.catFactory,}, // cat
			new SpanFactory[]{VerbNetFactories.catValueFactory,}, // value
	};

	/**
	 * Constructor
	 */
	public VerbNetSyntaxSpanner()
	{
		super(VerbNetSyntaxSpanner.patterns, VerbNetSyntaxSpanner.factories);
	}
}
