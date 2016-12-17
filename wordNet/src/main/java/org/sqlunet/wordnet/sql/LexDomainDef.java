package org.sqlunet.wordnet.sql;

/**
 * LexDomainDef, utility class to encapsulate lexdomain data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class LexDomainDef
{
	/**
	 * <code>id</code> lexdomain id
	 */
	public final int id;

	/**
	 * <code>pos</code> pos id
	 */
	@SuppressWarnings("unused")
	public final int pos;

	/**
	 * <code>posName</code> part-of-speech
	 */
	public final String posName;

	/**
	 * <code>lexDomainName</code> lexdomain name
	 */
	public final String lexDomainName;

	/**
	 * Constructor
	 *
	 * @param id   lexdomain id
	 * @param pos  part-of-speech id
	 * @param name lexdomain name
	 */
	public LexDomainDef(final int id, final int pos, final String name)
	{
		super();
		this.id = id;
		this.pos = pos;
		this.posName = getPosName(name);
		this.lexDomainName = getLexDomainName(name);
	}

	/**
	 * Get part-of-speech name
	 *
	 * @param string full lexdomain name
	 * @return the part-of-speech name
	 */
	private String getPosName(final String string)
	{
		final int index = string.indexOf('.');
		return index == -1 ? string : string.substring(0, index);
	}

	/**
	 * Get lexdomain name
	 *
	 * @param string full lexdomain name
	 * @return the lexdomain name
	 */
	private String getLexDomainName(final String string)
	{
		final int index = string.indexOf('.');
		return index == -1 ? string : string.substring(index + 1);
	}
}