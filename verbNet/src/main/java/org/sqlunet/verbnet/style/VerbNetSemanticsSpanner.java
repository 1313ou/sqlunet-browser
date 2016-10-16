package org.sqlunet.verbnet.style;

import org.sqlunet.style.RegExprSpanner;

public class VerbNetSemanticsSpanner extends RegExprSpanner
{
	// patterns

	private static final String[] patterns = new String[]{"([^\\(\n]*)\\((.*)\\)", // predicate/args : 2 captures //$NON-NLS-1$
			"event:((?:E|(?:start|end|result|during)\\(E\\)))", // event arg : 1 capture //$NON-NLS-1$
			"[\\( ]((?!event|E)[^\\(\\), \n]*)", // role arg //$NON-NLS-1$
			"(constant\\:[^\\s,\\)]*)", // constant //$NON-NLS-1$
	};

	// factories

	private static final SpanFactory[][] semanticFactories = new SpanFactory[][]{new SpanFactory[]{VerbNetFactories.predicateFactory, VerbNetFactories.argsFactory,}, // predicate/args
			new SpanFactory[]{VerbNetFactories.eventFactory,}, // event
			new SpanFactory[]{VerbNetFactories.themroleFactory,}, // role arg
			new SpanFactory[]{VerbNetFactories.constantFactory,}, // constant
	};

	public VerbNetSemanticsSpanner()
	{
		super(VerbNetSemanticsSpanner.patterns, VerbNetSemanticsSpanner.semanticFactories);
	}
}
