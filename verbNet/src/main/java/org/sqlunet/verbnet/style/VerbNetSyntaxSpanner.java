package org.sqlunet.verbnet.style;

import org.sqlunet.style.RegExprSpanner;

public class VerbNetSyntaxSpanner extends RegExprSpanner
{
	// patterns

	static private final String[] patterns = new String[] { //
		"^([^\\s\n]*)", // cat : 1 capture //$NON-NLS-1$
		"^[^\\s\n]* (\\p{Upper}[\\p{Lower}_\\p{Upper}]*)", // value : 1 capture //$NON-NLS-1$
	};

	// factories

	static private final SpanFactory[][] factories = new SpanFactory[][] { new SpanFactory[] { VerbNetFactories.catFactory, }, // cat
		new SpanFactory[] { VerbNetFactories.catValueFactory, }, // value
	};

	public VerbNetSyntaxSpanner()
	{
		super(VerbNetSyntaxSpanner.patterns, VerbNetSyntaxSpanner.factories);
	}
}
