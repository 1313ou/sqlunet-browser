/*
 * Copyright (c) 2023. Bernard Bou
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
	// static public final String AS_FNLUS = V.AS_FNLUS;

	public interface PredicateMatrix
	{
		String PMID = V.PMID;
		String PMROLEID = V.PMROLEID;
		String PMPREDICATEID = V.PREDICATEID;
		String PMPREDICATE = V.PREDICATE;
		String PMROLE = V.ROLE;
		String PMPOS = V.POS;
		String PMWSOURCE = V.WSOURCE;

		String WORD = V.WORD;
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;

		String VNWORDID = V.VNWORDID;
		String VNCLASSID = V.VN_CLASSID;
		String VNROLEID = V.VN_ROLEID;

		String PBWORDID = V.PBWORDID;
		String PBROLESETID = V.PB_ROLESETID;
		String PBROLEID = V.PB_ROLEID;

		String FNWORDID = V.FNWORDID;
		String FNFRAMEID = V.FN_FRAMEID;
		String FNFEID = V.FN_FEID;
		// String FNLUID = V.FN_LUID;
	}

	public interface Pm extends PredicateMatrix
	{
		String TABLE = "pm_pms";
		String URI = TABLE;
	}

	public interface Pm_X extends PredicateMatrix
	{
		String URI = "pm_x";

		String DEFINITION = V._DEFINITION;

		String VNCLASS = V._CLASS;
		String VNROLETYPEID = V._VNROLETYPEID;
		String VNROLETYPE = V._VNROLETYPE;

		String PBROLESETNAME = V._PBROLESETNAME;
		String PBROLESETDESCR = V._PBROLESETDESCR;
		String PBROLESETHEAD = V._PBROLESETHEAD;
		String PBROLEDESCR = V._PBROLEDESCR;
		String PBROLEARGTYPE = V._PBARGTYPE;

		String FNFETYPEID = V.FNFETYPEID;
		String FNFRAME = V._FNFRAME;
		String FNFRAMEDEFINITION = V._FNFRAMEDEFINITION;
		String FNFETYPE = V._FNFETYPE;
		String FNFEABBREV = V._FNFEABBREV;
		String FNFEDEFINITION = V._FNFEDEFINITION;
		//String FNLEXUNIT = V._FNLEXUNIT;
		//String FNLUDEFINITION = V._FNLUDEFINITION;
		//String FNLUDICT = V._FNLUDICT;
	}
}
