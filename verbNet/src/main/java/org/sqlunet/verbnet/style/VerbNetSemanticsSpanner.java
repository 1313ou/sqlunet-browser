/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.style;

import org.sqlunet.style.RegExprSpanner;

import java.util.Objects;

import androidx.annotation.Nullable;

/**
 * VerbNet semantics processor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetSemanticsSpanner extends RegExprSpanner
{
	/**
	 * Patterns
	 */
	static private final String[] patterns = {"([^\\(\n]*)\\((.*)\\)", // predicate/args : 2 captures //
			"event:((?:E|(?:start|end|result|during)\\(E\\)))", // event arg : 1 capture //
			"[\\( ]((?!event|E)[^\\(\\), \n]*)", // role arg //
			"(constant\\:[^\\s,\\)]*)", // constant //
	};

	/**
	 * Factories
	 */
	@Nullable
	static private final SpanFactory[][] semanticFactories = {new SpanFactory[]{VerbNetFactories.predicateFactory, VerbNetFactories.argsFactory,}, // predicate/args
			new SpanFactory[]{VerbNetFactories.eventFactory,}, // event
			new SpanFactory[]{VerbNetFactories.themroleFactory,}, // role arg
			new SpanFactory[]{VerbNetFactories.constantFactory,}, // constant
	};

	/**
	 * Constructor
	 */
	public VerbNetSemanticsSpanner()
	{
		super(VerbNetSemanticsSpanner.patterns, Objects.requireNonNull(VerbNetSemanticsSpanner.semanticFactories));
	}
}
