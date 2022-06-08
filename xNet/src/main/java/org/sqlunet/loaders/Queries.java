package org.sqlunet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.XNetContract;

public class Queries
{
	public static final String PRONUNCIATIONS = "pronunciations";

	public static final String AXPRONUNCIATIONS = "xpronunciations";

	private static final String GROUPID_COLUMN = "_id";

	// B R O W S E R

	/**
	 * Load word data
	 *
	 * @param word word
	 */
	public static Module.ContentProviderSql prepareWordSelect(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_FnWords_PbWords_VnWords.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_FnWords_PbWords_VnWords.SYNSETID + " AS _id", //
				XNetContract.Words_FnWords_PbWords_VnWords.WORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.SENSEID, //
				XNetContract.Words_FnWords_PbWords_VnWords.SENSENUM, //
				XNetContract.Words_FnWords_PbWords_VnWords.SENSEKEY, //
				XNetContract.Words_FnWords_PbWords_VnWords.LUID, //
				XNetContract.Words_FnWords_PbWords_VnWords.TAGCOUNT, //
				XNetContract.Words_FnWords_PbWords_VnWords.SYNSETID, //
				XNetContract.Words_FnWords_PbWords_VnWords.DEFINITION, //
				XNetContract.AS_POSES + '.' + XNetContract.Words_FnWords_PbWords_VnWords.POSID, //
				XNetContract.Words_FnWords_PbWords_VnWords.POS, //
				XNetContract.Words_FnWords_PbWords_VnWords.DOMAIN, //
				XNetContract.Words_FnWords_PbWords_VnWords.CASED, //
				XNetContract.Words_FnWords_PbWords_VnWords.FNWORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.VNWORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.PBWORDID, //
		};
		providerSql.selection = XNetContract.AS_WORDS + '.' + XNetContract.Words_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XNetContract.AS_POSES + '.' + XNetContract.Words_FnWords_PbWords_VnWords.POS + ',' + XNetContract.Words_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load word data
	 *
	 * @param word word
	 */
	public static Module.ContentProviderSql prepareWordPronunciationSelect(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID + " AS _id", //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.WORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SENSEID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SENSENUM, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SENSEKEY, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.LUID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.TAGCOUNT, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.DEFINITION, //
				XNetContract.AS_POSES + '.' + XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.POSID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.POS, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.DOMAIN, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.CASED, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.FNWORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VNWORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PBWORDID, //
				"GROUP_CONCAT(CASE WHEN " +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VARIETY + " IS NULL THEN '/'||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATION + "||'/' ELSE '['||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VARIETY + "||'] '||'/'||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATION + "||'/' END) AS " + PRONUNCIATIONS, //
		};
		providerSql.selection = XNetContract.AS_WORDS + '.' + XNetContract.Words_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XNetContract.AS_POSES + '.' + XNetContract.Words_FnWords_PbWords_VnWords.POS + ',' + XNetContract.Words_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load word x data
	 *
	 * @param word word
	 */
	public static Module.ContentProviderSql prepareWordXSelect(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_FnWords_PbWords_VnWords.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_FnWords_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				XNetContract.Words_FnWords_PbWords_VnWords.WORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.FNWORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.VNWORDID, //
				XNetContract.Words_FnWords_PbWords_VnWords.PBWORDID, //
		};
		providerSql.selection = XNetContract.AS_WORDS + '.' + XNetContract.Words_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XNetContract.AS_POSES + '.' + XNetContract.Words_FnWords_PbWords_VnWords.POS + ',' + XNetContract.Words_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load word and pronunciation x data
	 *
	 * @param word word
	 */
	public static Module.ContentProviderSql prepareWordPronunciationXSelect(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.WORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.FNWORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VNWORDID, //
				XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PBWORDID, //
				"GROUP_CONCAT(CASE WHEN " +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VARIETY + " IS NULL THEN '/'||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATION + "||'/' ELSE '['||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.VARIETY + "||'] '||'/'||" +
						XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATION + "||'/' END) AS " + PRONUNCIATIONS, //
		};
		providerSql.selection = XNetContract.AS_WORDS + '.' + XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XNetContract.AS_POSES + '.' + XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.POS + ',' + XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load VerbNet x data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql prepareVnXSelect(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_VnWords_VnClasses_U.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_VnWords_VnClasses_U.WORDID, //
				XNetContract.Words_VnWords_VnClasses_U.SYNSETID, //
				XNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + XNetContract.Words_XNet_U.XID, //
				XNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + XNetContract.Words_XNet_U.XCLASSID, //
				"NULL AS " + XNetContract.Words_XNet_U.XMEMBERID, //
				"TRIM(" + XNetContract.Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + XNetContract.Words_XNet_U.XNAME, //
				XNetContract.Words_VnWords_VnClasses_U.CLASS + " AS " + XNetContract.Words_XNet_U.XHEADER, //
				XNetContract.Words_VnWords_VnClasses_U.CLASSTAG + " AS " + XNetContract.Words_XNet_U.XINFO, //
				XNetContract.Words_VnWords_VnClasses_U.DEFINITION + " AS " + XNetContract.Words_XNet_U.XDEFINITION, //
				"NULL AS " + PRONUNCIATIONS, //
				"RANDOM() AS _id",};
		providerSql.selection = XNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XNetContract.Words_VnWords_VnClasses_U.CLASSID;
		return providerSql;
	}

	/**
	 * Load PropBank x data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql preparePbXSelect(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_PbWords_PbRoleSets_U.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_PbWords_PbRoleSets_U.WORDID, //
				XNetContract.Words_PbWords_PbRoleSets_U.SYNSETID, //
				XNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + XNetContract.Words_XNet_U.XID, //
				XNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + XNetContract.Words_XNet_U.XCLASSID, //
				"NULL AS " + XNetContract.Words_XNet_U.XMEMBERID, //
				"TRIM(" + XNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + ",'.0123456789')" + " AS " + XNetContract.Words_XNet_U.XNAME, //
				XNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + " AS " + XNetContract.Words_XNet_U.XHEADER, //
				//Words_PbWords_PbRoleSets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
				XNetContract.Words_PbWords_PbRoleSets_U.ROLESETDESCR + " AS " + XNetContract.Words_XNet_U.XINFO, //
				XNetContract.Words_PbWords_PbRoleSets_U.DEFINITION + " AS " + XNetContract.Words_XNet_U.XDEFINITION, //
				"NULL AS " + PRONUNCIATIONS, //
				"RANDOM() AS _id",};
		providerSql.selection = XNetContract.Words_PbWords_PbRoleSets_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XNetContract.Words_PbWords_PbRoleSets_U.ROLESETID;
		return providerSql;
	}

	/**
	 * Load FrameNet x data
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql prepareFnXSelect(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_FnWords_FnFrames_U.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_FnWords_FnFrames_U.WORDID, //
				XNetContract.Words_FnWords_FnFrames_U.SYNSETID, //
				XNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + XNetContract.Words_XNet_U.XID, //
				XNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + XNetContract.Words_XNet_U.XCLASSID, //
				XNetContract.Words_FnWords_FnFrames_U.LUID + " AS " + XNetContract.Words_XNet_U.XMEMBERID, //
				"GROUP_CONCAT(" + XNetContract.Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + XNetContract.Words_XNet_U.XNAME, //
				XNetContract.Words_FnWords_FnFrames_U.FRAME + " AS " + XNetContract.Words_XNet_U.XHEADER, //
				"GROUP_CONCAT(" + XNetContract.Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + XNetContract.Words_XNet_U.XINFO, //
				XNetContract.Words_FnWords_FnFrames_U.DEFINITION + " AS " + XNetContract.Words_XNet_U.XDEFINITION, //
				"NULL AS " + PRONUNCIATIONS, //
				"RANDOM() AS _id",};
		providerSql.selection = XNetContract.Words_FnWords_FnFrames_U.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XNetContract.Words_FnWords_FnFrames_U.LUID + ' ' + "IS NULL" + ',' + XNetContract.Words_FnWords_FnFrames_U.SOURCE + ',' + XNetContract.Words_FnWords_FnFrames_U.FRAMEID;
		return providerSql;
	}

	// V N   B R O W S E R

	/**
	 * Load word x data for VN browser
	 *
	 * @param word word
	 */
	public static Module.ContentProviderSql prepareWordXSelectVn(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_PbWords_VnWords.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
				XNetContract.Words_PbWords_VnWords.WORDID, //
				XNetContract.Words_PbWords_VnWords.VNWORDID, //
				XNetContract.Words_PbWords_VnWords.PBWORDID, //
		};
		providerSql.selection = XNetContract.AS_WORDS + '.' + XNetContract.Words_PbWords_VnWords.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = XNetContract.AS_POSES + '.' + XNetContract.Words_PbWords_VnWords.POSID + ',' + XNetContract.Words_PbWords_VnWords.SENSENUM;
		return providerSql;
	}

	/**
	 * Load VerbNet x data for VN browser
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql prepareVnXSelectVn(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_VnWords_VnClasses.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_VnWords_VnClasses.WORDID, //
				XNetContract.Words_VnWords_VnClasses.SYNSETID, //
				XNetContract.Words_VnWords_VnClasses.CLASSID + " AS " + XNetContract.Words_XNet.XID, //
				XNetContract.Words_VnWords_VnClasses.CLASSID + " AS " + XNetContract.Words_XNet.XCLASSID, //
				"NULL AS " + XNetContract.Words_XNet.XMEMBERID, //
				"TRIM(" + XNetContract.Words_VnWords_VnClasses.CLASS + ",'-.0123456789')" + " AS " + XNetContract.Words_XNet.XNAME, //
				XNetContract.Words_VnWords_VnClasses.CLASS + " AS " + XNetContract.Words_XNet.XHEADER, //
				XNetContract.Words_VnWords_VnClasses.CLASSTAG + " AS " + XNetContract.Words_XNet.XINFO, //
				XNetContract.Words_VnWords_VnClasses.DEFINITION + " AS " + XNetContract.Words_XNet.XDEFINITION, //
				"'vn' AS " + XNetContract.Words_XNet.SOURCES, //
				XNetContract.AS_CLASSES + ".rowid AS _id",};
		providerSql.selection = XNetContract.Words_VnWords_VnClasses.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XNetContract.Words_VnWords_VnClasses.CLASSID;
		return providerSql;
	}

	/**
	 * Load PropBank x data for VN browser
	 *
	 * @param wordId word id
	 */
	public static Module.ContentProviderSql preparePbSelectVn(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = XNetContract.Words_PbWords_PbRoleSets.URI;
		providerSql.projection = new String[]{ //
				XNetContract.Words_PbWords_PbRoleSets.WORDID, //
				"NULL AS " + XNetContract.Words_PbWords_PbRoleSets.SYNSETID, //
				XNetContract.Words_PbWords_PbRoleSets.ROLESETID + " AS " + XNetContract.Words_XNet.XID, //
				XNetContract.Words_PbWords_PbRoleSets.ROLESETID + " AS " + XNetContract.Words_XNet.XCLASSID, //
				"NULL AS " + XNetContract.Words_XNet.XMEMBERID, //
				"TRIM(" + XNetContract.Words_PbWords_PbRoleSets.ROLESETNAME + ",'.0123456789')" + " AS " + XNetContract.Words_XNet.XNAME, //
				XNetContract.Words_PbWords_PbRoleSets.ROLESETNAME + " AS " + XNetContract.Words_XNet.XHEADER, //
				//Words_PbWords_PbRoleSets.ROLESETHEAD + " AS " + Words_XNet.XHEADER, //
				XNetContract.Words_PbWords_PbRoleSets.ROLESETDESCR + " AS " + XNetContract.Words_XNet.XINFO, //
				"NULL AS " + XNetContract.Words_XNet.XDEFINITION, //
				"'pb' AS " + XNetContract.Words_XNet.SOURCES, //
				XNetContract.AS_CLASSES + ".rowid AS _id",};
		providerSql.selection = XNetContract.Words_PbWords_PbRoleSets.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = XNetContract.Words_PbWords_PbRoleSets.ROLESETID;
		return providerSql;
	}
}
