/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.sql;

/**
 * VerbNet role
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnRole
{
	/**
	 * Role type
	 */
	public final String roleType;

	/**
	 * Role selectional restrictions
	 */
	public final String selectionRestrictions;

	/**
	 * Constructor
	 *
	 * @param roleType              role type
	 * @param selectionRestrictions selectional restriction (XML)
	 */
	VnRole(final String roleType, final String selectionRestrictions)
	{
		this.roleType = roleType;
		this.selectionRestrictions = selectionRestrictions;
	}
}