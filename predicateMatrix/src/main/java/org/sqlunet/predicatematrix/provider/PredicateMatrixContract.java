/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.predicatematrix.provider;

/**
 * PredicateMatrix provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixContract
{
	// A L I A S E S

	static public final String AS_PMROLES = V.AS_PMROLES;
	static public final String AS_PMPREDICATES = V.AS_PMPREDICATES;
	static public final String AS_VNCLASSES = V.AS_VNCLASSES;
	static public final String AS_VNROLES = V.AS_VNROLES;
	static public final String AS_VNROLETYPES = V.AS_VNROLETYPES;
	static public final String AS_PBROLESETS = V.AS_PBROLESETS;
	static public final String AS_PBROLES = V.AS_PBROLES;
	static public final String AS_PBARGS = V.AS_PBARGS;
	static public final String AS_FNFRAMES = V.AS_FNFRAMES;
	static public final String AS_FNFES = V.AS_FNFES;
	static public final String AS_FNFETYPES = V.AS_FNFETYPES;
	static public final String AS_FNLUS = V.AS_FNLUS;

	public interface PredicateMatrix
	{
		String PMID = V.PMID;
		String PMROLEID = V.PMROLEID;
		String PMPREDICATEID = V.PREDICATEID;
		String PMPREDICATE = V.PREDICATE;
		String PMROLE = V.ROLE;
		String PMPOS = V.POS;
		String WORD = V.WORD;
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String PMWSOURCE = V.WSOURCE;
		String VNWORDID = V.VNWORDID;
		String VNCLASSID = V.VNCLASSID;
		String VNROLEID = V.VNROLEID;
		String PBWORDID = V.PBWORDID;
		String PBROLESETID = V.PBROLESETID;
		String PBROLEID = V.PBROLEID;
		String FNWORDID = V.FNWORDID;
		String FRAMEID = V.FN_FRAMEID;
		String LUID = V.FN_LUID;
		String FEID = V.FN_FEID;
	}

	public interface Pm extends PredicateMatrix
	{
		String TABLE = "pm_pms";
		String URI = TABLE;
	}

	public interface Pm_X extends PredicateMatrix
	{
		String URI = "pm_x";
		String DEFINITION = "definition";
		String VNCLASS = "class";
		String VNROLERESTRID = "restrsid";
		String VNROLETYPEID = "roletypeid";
		String VNROLETYPE = "roletype";
		String PBROLESETNAME = "rolesetname";
		String PBROLESETDESCR = "rolesetdescr";
		String PBROLESETHEAD = "rolesethead";
		String PBROLEDESCR = "roledescr";
		String PBROLEARGTYPEID = "argtypeid";
		String PBROLEARGTYPE = "argtype";
		String FRAME = "frame";
		String FRAMEDEFINITION = "framedefinition";
		String LEXUNIT = "lexunit";
		String LUDEFINITION = "ludefinition";
		String LUDICT = "ludict";
		String FETYPEID = "fetypeid";
		String FETYPE = "fetype";
		String FEABBREV = "feabbrev";
		String FEDEFINITION = "fedefinition";
	}
}
