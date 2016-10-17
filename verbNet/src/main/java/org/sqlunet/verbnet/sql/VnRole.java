package org.sqlunet.verbnet.sql;

/**
 * @author bbou
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
	 * @param roleType              is the role type
	 * @param selectionRestrictions is the selectional restriction (XML)
	 */
	VnRole(final String roleType, final String selectionRestrictions)
	{
		this.roleType = roleType;
		this.selectionRestrictions = selectionRestrictions;
	}
}