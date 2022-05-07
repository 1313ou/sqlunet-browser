package org.sqlunet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.XSqlUNetContract;

public class Queries
{
	static private final String GROUPID_COLUMN = "_id";

	public static Module.ContentProviderSql prepareWord(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XSqlUNetContract.Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.WORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.FNWORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.VNWORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.PBWORDID, //
		};
		providerSql.selection = XSqlUNetContract.WORD + '.' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XSqlUNetContract.POS + '.' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.POS + ',' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareWordX(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XSqlUNetContract.Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SYNSETID + " AS _id", //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.WORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SENSEID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SENSENUM, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SENSEKEY, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.LUID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.TAGCOUNT, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.SYNSETID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.DEFINITION, //
				XSqlUNetContract.POS + '.' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.POSID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.POS, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.DOMAIN, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.CASED, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.FNWORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.VNWORDID, //
				XSqlUNetContract.Words_FnWords_PbWords_VnWords.PBWORDID, //
		};
		providerSql.selection = XSqlUNetContract.WORD + '.' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XSqlUNetContract.POS + '.' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.POS + ',' + XSqlUNetContract.Words_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load VerbNet data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql prepareVn(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XSqlUNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.SYNSETID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + XSqlUNetContract.Words_XNet_U.XID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + XSqlUNetContract.Words_XNet_U.XCLASSID, //
				"NULL AS " + XSqlUNetContract.Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + XSqlUNetContract.Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + " AS " + XSqlUNetContract.Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSTAG + " AS " + XSqlUNetContract.Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.DEFINITION + " AS " + XSqlUNetContract.Words_XNet_U.XDEFINITION, //
				"RANDOM() AS _id",};
		providerSql.selection = XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID;
		return providerSql;
	}

	/**
	 * Load PropBank data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql preparePb(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XSqlUNetContract.Words_PbWords_PbRoleSets_U.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.WORDID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.SYNSETID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + XSqlUNetContract.Words_XNet_U.XID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + XSqlUNetContract.Words_XNet_U.XCLASSID, //
				"NULL AS " + XSqlUNetContract.Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + ",'.0123456789')" + " AS " + XSqlUNetContract.Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + " AS " + XSqlUNetContract.Words_XNet_U.XHEADER, //
				//Words_PbWords_PbRoleSets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETDESCR + " AS " + XSqlUNetContract.Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.DEFINITION + " AS " + XSqlUNetContract.Words_XNet_U.XDEFINITION, //
				"RANDOM() AS _id",};
		providerSql.selection = XSqlUNetContract.Words_PbWords_PbRoleSets_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID;
		return providerSql;
	}

	/**
	 * Load FrameNet data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql prepareFn(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XSqlUNetContract.Words_FnWords_FnFrames_U.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.SYNSETID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + XSqlUNetContract.Words_XNet_U.XID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + XSqlUNetContract.Words_XNet_U.XCLASSID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + " AS " + XSqlUNetContract.Words_XNet_U.XMEMBERID, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + XSqlUNetContract.Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAME + " AS " + XSqlUNetContract.Words_XNet_U.XHEADER, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + XSqlUNetContract.Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.DEFINITION + " AS " + XSqlUNetContract.Words_XNet_U.XDEFINITION, //
				"RANDOM() AS _id",};
		providerSql.selection = XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + ' ' + "IS NULL" + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.SOURCE + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID;
		return providerSql;
	}
}
