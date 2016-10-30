package org.sqlunet.verbnet.sql;

/**
 * VerbNet frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnFrame
{
	/**
	 * Frame number
	 */
	public final String number;

	/**
	 * Frame xtag
	 */
	public final String xTag;

	/**
	 * Frame major descriptor
	 */
	public final String description1;

	/**
	 * Frame minor descriptor
	 */
	public final String description2;
	/**
	 * Frame examples
	 */
	public final String[] examples;
	/**
	 * Frame syntax data
	 */
	private final String syntax;
	/**
	 * Frame semantics data
	 */
	private final String semantics;

	/**
	 * VerbNet frame
	 *
	 * @param number       is the frame name
	 * @param xTag         is the frame xtag
	 * @param description1 is the frame major descriptor
	 * @param description2 is the frame minor descriptor
	 * @param syntax       is the syntax data
	 * @param semantics    is the semantic data
	 * @param examples     examples
	 */
	VnFrame(final String number, final String xTag, final String description1, final String description2, final String syntax, final String semantics, final String... examples)
	{
		this.number = number;
		this.xTag = xTag;
		this.description1 = description1;
		this.description2 = description2;
		this.syntax = syntax;
		this.semantics = semantics;
		this.examples = examples;
	}

	/**
	 * Get syntactic data (XML)
	 *
	 * @return syntactic data
	 */
	public String getSyntax()
	{
		return this.syntax;
	}

	/**
	 * Get semantics data (XML)
	 *
	 * @return semantics data
	 */
	public String getSemantics()
	{
		return this.semantics;
	}
}
