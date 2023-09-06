/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import androidx.annotation.NonNull;

/**
 * Domain, utility class to encapsulate domain data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class Domain
{
	/**
	 * <code>id</code> domain id
	 */
	public final int id;

	/**
	 * <code>pos</code> pos id
	 */
	public final int posId;

	/**
	 * <code>domainName</code> domain name
	 */
	@NonNull
	public final String domainName;

	/**
	 * Constructor
	 *
	 * @param id   domain id
	 * @param pos  part-of-speech id
	 * @param name domain name
	 */
	public Domain(final int id, final int pos, @NonNull final String name)
	{
		super();
		this.id = id;
		this.posId = pos;
		this.domainName = getDomainName(name);
	}

	/**
	 * Get domain name
	 *
	 * @param string full domain name
	 * @return the domain name
	 */
	@NonNull
	private String getDomainName(@NonNull final String string)
	{
		final int index = string.indexOf('.');
		return index == -1 ? string : string.substring(index + 1);
	}
}