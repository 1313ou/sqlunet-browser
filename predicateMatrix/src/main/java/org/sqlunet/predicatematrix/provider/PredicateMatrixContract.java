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

	static public final String PMROLE = "mr";
	static public final String PMPREDICATE = "mp";
	static public final String VNCLASS = "vc";
	static public final String VNROLE = "vr";
	static public final String VNROLETYPE = "vt";
	static public final String PBROLESET = "pc";
	static public final String PBROLE = "pr";
	static public final String PBARG = "pa";
	static public final String FNFRAME = "ff";
	static public final String FNFE = "fe";
	static public final String FNFETYPE = "ft";
	static public final String FNLU = "fu";

	static public class PredicateMatrix
	{
		static public final String PMID = "pmid";
		static public final String PMROLEID = "pmroleid";
		static public final String PMPREDID = "pmpredid";
		static public final String PMPREDICATE = "predicate";
		static public final String PMROLE = "role";
		static public final String PMPOS = "pos";
		static public final String WORD = "lemma";
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String PMWSOURCE = "wsource";
		static public final String VNWORDID = "vnwordid";
		static public final String VNCLASSID = "vnclassid";
		static public final String VNROLEID = "vnroleid";
		static public final String PBWORDID = "pbwordid";
		static public final String PBROLESETID = "pbrolesetid";
		static public final String PBROLEID = "pbroleid";
		static public final String FNWORDID = "fnwordid";
		static public final String FRAMEID = "fnframeid";
		static public final String LUID = "fnluid";
		static public final String FEID = "fnfeid";
	}

	static public final class Pm extends PredicateMatrix
	{
		static public final String TABLE = "pm";
		static public final String CONTENT_URI_TABLE = Pm.TABLE;
	}

	static public final class Pm_X extends PredicateMatrix
	{
		static public final String TABLE = "pm_x";
		static public final String CONTENT_URI_TABLE = Pm_X.TABLE;
		static public final String DEFINITION = "definition";
		static public final String VNCLASS = "class";
		static public final String VNROLERESTRID = "restrsid";
		static public final String VNROLETYPEID = "roletypeid";
		static public final String VNROLETYPE = "roletype";
		static public final String PBROLESETNAME = "rolesetname";
		static public final String PBROLESETDESCR = "rolesetdescr";
		static public final String PBROLESETHEAD = "rolesethead";
		static public final String PBROLEDESCR = "roledescr";
		static public final String PBROLENARG = "narg";
		static public final String PBROLENARGDESCR = "nargdescr";
		static public final String FRAME = "frame";
		static public final String FRAMEDEFINITION = "framedefinition";
		static public final String LEXUNIT = "lexunit";
		static public final String LUDEFINITION = "ludefinition";
		static public final String LUDICT = "ludict";
		static public final String FETYPEID = "fetypeid";
		static public final String FETYPE = "fetype";
		static public final String FEABBREV = "feabbrev";
		static public final String FEDEFINITION = "fedefinition";
	}
}
