package org.sqlunet.verbnet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.verbnet.provider.VerbNetContract;

import androidx.annotation.Nullable;

public class Queries
{
	public static Module.ContentProviderSql prepareVnClass(final long classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses.URI;
		providerSql.projection = new String[]{ //
				VerbNetContract.VnClasses.CLASSID, //
				VerbNetContract.VnClasses.CLASS, //
				VerbNetContract.VnClasses.CLASSTAG, //
		};
		providerSql.selection = VerbNetContract.VnClasses.CLASSID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(classId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnClasses(final long wordId, @Nullable final Long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.Words_VnClasses.URI;
		providerSql.projection = new String[]{ //
				VerbNetContract.Words_VnClasses.CLASSID, //
				VerbNetContract.Words_VnClasses.CLASS, //
				VerbNetContract.Words_VnClasses.CLASSTAG, //
				"(" + VerbNetContract.Words_VnClasses.SYNSETID + " IS NULL) AS " + VerbNetContract.Words_VnClasses.NULLSYNSET, //
				VerbNetContract.Words_VnClasses.SENSENUM, //
				VerbNetContract.Words_VnClasses.SENSEKEY, //
				VerbNetContract.Words_VnClasses.QUALITY, //
		};
		providerSql.selection = VerbNetContract.Words_VnClasses.WORDID + " = ?";
		if (synsetId != null && synsetId != 0)
		{
			providerSql.selection += " AND (" + VerbNetContract.Words_VnClasses.SYNSETID + " = ? OR " + VerbNetContract.Words_VnClasses.SYNSETID + " IS NULL)";
			providerSql.selectionArgs = new String[]{ //
					Long.toString(wordId), //
					Long.toString(synsetId)};
		}
		else
		{
			providerSql.selectionArgs = new String[]{ //
					Long.toString(wordId)};
		}
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnMembers(final int classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses_VnMembers_X.URI_BY_WORD;
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
		providerSql.sortBy = VerbNetContract.VnClasses_VnMembers_X.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVnRoles(final int classId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = VerbNetContract.VnClasses_VnRoles_X.URI_BY_ROLE;
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
		providerSql.providerUri = VerbNetContract.VnClasses_VnFrames_X.URI_BY_FRAME;
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
