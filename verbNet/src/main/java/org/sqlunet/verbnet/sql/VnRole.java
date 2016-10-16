package org.sqlunet.verbnet.sql;

/**
 * @author bbou
 */
class VnRole
{
	/**
	 * Role type
	 */
	public final String theRoleType;

	/**
	 * Role selectional restrictions
	 */
	public final String theSelectionRestrictions;

	/**
	 * Constructor
	 *
	 * @param thisRoleType
	 *            is the role type
	 * @param theseSelectionRestrictions
	 *            is the selectional restriction (XML)
	 */
	VnRole(final String thisRoleType, final String theseSelectionRestrictions)
	{
		this.theRoleType = thisRoleType;
		this.theSelectionRestrictions = theseSelectionRestrictions;
	}
}