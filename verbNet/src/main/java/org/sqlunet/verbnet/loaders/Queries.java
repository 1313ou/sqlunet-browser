package org.sqlunet.verbnet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.verbnet.provider.VerbNetContract;

public class Queries
{
	public static Module.ContentProviderSql prepareVnClass(final long classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				VerbNetContract.VnClasses.CLASSID, //
				VerbNetContract.VnClasses.CLASS, //
				VerbNetContract.VnClasses.CLASSTAG, //
		};
		providerSql.selection = VerbNetContract.VnClasses.CLASSID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(classId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnMembers(final int classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses_VnMembers_X.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				VerbNetContract.VnClasses_VnMembers_X.WORDID, //
				VerbNetContract.VnClasses_VnMembers_X.VNWORDID, //
				VerbNetContract.VnClasses_VnMembers_X.WORD, //
				"REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(" + VerbNetContract.VnClasses_VnMembers_X.DEFINITION + ",',','#')),',','|'),'#',',') AS " + VerbNetContract.VnClasses_VnMembers_X.DEFINITIONS, //
				"GROUP_CONCAT(DISTINCT " + VerbNetContract.VnClasses_VnMembers_X.GROUPING + ") AS " + VerbNetContract.VnClasses_VnMembers_X.GROUPINGS, //
				VerbNetContract.VnClasses_VnMembers_X.CLASSID, //
		};
		providerSql.selection = VerbNetContract.VnClasses_VnRoles_X.CLASSID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(classId)};
		final String sortOrder = VerbNetContract.VnClasses_VnMembers_X.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnRoles(final int classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses_VnRoles_X.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				VerbNetContract.VnClasses_VnRoles_X.ROLEID, //
				VerbNetContract.VnClasses_VnRoles_X.ROLETYPE, //
				VerbNetContract.VnClasses_VnRoles_X.RESTRS, //
				VerbNetContract.VnClasses_VnRoles_X.CLASSID, //
		};
		providerSql.selection = VerbNetContract.VnClasses_VnRoles_X.CLASSID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(classId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnFrames(final int classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses_VnFrames_X.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				VerbNetContract.VnClasses_VnFrames_X.FRAMEID, //
				VerbNetContract.VnClasses_VnFrames_X.NUMBER, //
				VerbNetContract.VnClasses_VnFrames_X.XTAG, //
				VerbNetContract.VnClasses_VnFrames_X.FRAMENAME, //
				VerbNetContract.VnClasses_VnFrames_X.FRAMESUBNAME, //
				VerbNetContract.VnClasses_VnFrames_X.SYNTAX, //
				VerbNetContract.VnClasses_VnFrames_X.SEMANTICS, //
				"GROUP_CONCAT(" + VerbNetContract.VnClasses_VnFrames_X.EXAMPLE + " , '|') AS " + VerbNetContract.VnClasses_VnFrames_X.EXAMPLES, //
				VerbNetContract.VnClasses_VnFrames_X.CLASSID, //
		};
		providerSql.selection = VerbNetContract.VnClasses_VnFrames_X.CLASSID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(classId)};
		return providerSql;
	}
}
