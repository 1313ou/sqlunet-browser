package org.sqlunet.wordnet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.wordnet.provider.V;
import org.sqlunet.wordnet.provider.WordNetContract;

public class Queries
{
	static final String ALLMORPHS = "allmorphs";

	public static Module.ContentProviderSql prepareWn(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				"'wn' AS " + XSqlUNetContract.Words_XNet_U.SOURCES, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS " + XSqlUNetContract.Words_XNet_U.XID, //
				"NULL AS " + XSqlUNetContract.Words_XNet_U.XCLASSID, //
				"NULL AS " + XSqlUNetContract.Words_XNet_U.XMEMBERID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + "|| '.' ||" + WordNetContract.AS_POSES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + " AS " + XSqlUNetContract.Words_XNet_U.XNAME, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN + " AS " + XSqlUNetContract.Words_XNet_U.XHEADER, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY + " AS " + XSqlUNetContract.Words_XNet_U.XINFO, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION + " AS " + XSqlUNetContract.Words_XNet_U.XDEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id"};
		providerSql.selection = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareWord(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Words_Lexes_Morphs.CONTENT_URI_TABLE_BY_WORD;
		providerSql.projection = new String[]{ //
				WordNetContract.Words_Lexes_Morphs.WORD, //
				WordNetContract.Words_Lexes_Morphs.WORDID, //
				"GROUP_CONCAT(" + WordNetContract.Words_Lexes_Morphs.MORPH + "||'-'||" + WordNetContract.Words_Lexes_Morphs.POSID + ") AS " + ALLMORPHS};
		providerSql.selection = WordNetContract.Words.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSenses(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id", //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD};
		providerSql.selection = WordNetContract.AS_WORDS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + " = ?";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSenses(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id", //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN, //
				WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD};
		providerSql.selection = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		providerSql.sortBy = WordNetContract.AS_POSES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSense(final long senseId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Senses.WORDID, //
				WordNetContract.Senses.SYNSETID, //
		};
		providerSql.selection = WordNetContract.Senses.SENSEID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(senseId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSense(final String senseKey)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Senses.WORDID, //
				WordNetContract.Senses.SYNSETID, //
		};
		providerSql.selection = WordNetContract.Senses.SENSEKEY + " = ?";
		providerSql.selectionArgs = new String[]{senseKey};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSense(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Synsets_Poses_Domains.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Synsets.DEFINITION, //
				WordNetContract.Poses.POS, //
				WordNetContract.Domains.DOMAIN, //
		};
		providerSql.selection = WordNetContract.Synsets_Poses_Domains.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSynset(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Synsets_Poses_Domains.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Synsets.DEFINITION, //
				WordNetContract.Poses.POS, //
				WordNetContract.Domains.DOMAIN, //
		};
		providerSql.selection = WordNetContract.Synsets_Poses_Domains.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareMembers(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_Words.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_Words.WORDID, WordNetContract.Senses_Words.WORD};
		providerSql.selection = WordNetContract.Senses_Words.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		providerSql.sortBy = WordNetContract.Senses_Words.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareMembers2(final long synsetId, final boolean membersGrouped)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = membersGrouped ? WordNetContract.Senses_Words.CONTENT_URI_TABLE_BY_SYNSET : WordNetContract.Senses_Words.CONTENT_URI_TABLE;
		providerSql.projection = membersGrouped ? //
				new String[]{WordNetContract.Senses_Words.SYNSETID} : new String[]{WordNetContract.Words.WORD};
		providerSql.selection = WordNetContract.Senses_Words.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		providerSql.sortBy = WordNetContract.Words.WORD;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSamples(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Samples.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{ //
				WordNetContract.Samples.SAMPLEID, //
				WordNetContract.Samples.SAMPLE, //
		};
		providerSql.selection = WordNetContract.Samples.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		providerSql.sortBy = WordNetContract.Samples.SAMPLEID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareRelations(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.AnyRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET;
		providerSql.projection = new String[]{ //
				WordNetContract.RELATIONTYPE, WordNetContract.Relations.RELATIONID, //
				WordNetContract.Relations.RELATION, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2, //
				"GROUP_CONCAT(" + WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + ") AS " + WordNetContract.AnyRelations_Senses_Words_X.MEMBERS2, //
				WordNetContract.AnyRelations_Senses_Words_X.RECURSES, //
				WordNetContract.AS_WORDS2 + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID, //
				WordNetContract.AS_WORDS2 + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2, //
		};
		providerSql.selection = "synset1id = ? /**/|/**/ synset1id = ? AND word1id = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Long.toString(synsetId), Long.toString(wordId)};
		providerSql.sortBy = WordNetContract.Relations.RELATIONID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSemRelations(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.SemRelations_Synsets_Words_X.CONTENT_URI_TABLE_BY_SYNSET;
		providerSql.projection = new String[]{ //
				WordNetContract.Relations.RELATIONID, //
				WordNetContract.Relations.RELATION, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2, //
				WordNetContract.Relations.RECURSES, //
		};
		providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.SemRelations_Synsets_Words_X.SYNSET1ID + " = ?";  ////
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		providerSql.sortBy = WordNetContract.Relations.RELATIONID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSemRelations(final long synsetId, final int relationId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.SemRelations_Synsets_Words_X.CONTENT_URI_TABLE_BY_SYNSET;
		providerSql.projection = new String[]{ //
				WordNetContract.Relations.RELATIONID, //
				WordNetContract.Relations.RELATION, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2, //
				WordNetContract.Relations.RECURSES, //
		};
		providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.SemRelations_Synsets_Words_X.SYNSET1ID + " = ? AND " + WordNetContract.Relations.RELATIONID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Integer.toString(relationId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLexRelations(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.LexRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET;
		providerSql.projection = new String[]{ //
				WordNetContract.Relations.RELATIONID, //
				WordNetContract.Relations.RELATION, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2, //
				WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID, //
				WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2, //
		};
		providerSql.selection = WordNetContract.AS_RELATIONS + ".synset1id = ? AND " + WordNetContract.AS_RELATIONS + ".word1id = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Long.toString(wordId)};
		providerSql.sortBy = WordNetContract.Relations.RELATIONID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLexRelations(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.LexRelations_Senses_Words_X.CONTENT_URI_TABLE_BY_SYNSET;
		providerSql.projection = new String[]{ //
				WordNetContract.Relations.RELATIONID, //
				WordNetContract.Relations.RELATION, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID, //
				WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2, //
				WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID, //
				WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2, //
		};
		providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.LexRelations_Senses_Words_X.SYNSET1ID + " = ?";  ////
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVFrames(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_VerbFrames.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_VerbFrames.FRAME};
		providerSql.selection = WordNetContract.Senses_VerbFrames.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVFrames(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_VerbFrames.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_VerbFrames.FRAME};
		providerSql.selection = WordNetContract.Senses_VerbFrames.SYNSETID + " = ? AND " + WordNetContract.Senses_VerbFrames.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVTemplates(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_VerbTemplates.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_VerbTemplates.TEMPLATE};
		providerSql.selection = WordNetContract.Senses_VerbTemplates.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareVTemplates(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_VerbTemplates.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_VerbTemplates.TEMPLATE};
		providerSql.selection = WordNetContract.Senses_VerbTemplates.SYNSETID + " = ? AND " + WordNetContract.Senses_VerbTemplates.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAdjPosition(final long synsetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_AdjPositions.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_AdjPositions.POSITION};
		providerSql.selection = WordNetContract.Senses_AdjPositions.SYNSETID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAdjPosition(final long synsetId, final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Senses_AdjPositions.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Senses_AdjPositions.POSITION};
		providerSql.selection = WordNetContract.Senses_AdjPositions.SYNSETID + " = ? AND " + WordNetContract.Senses_AdjPositions.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(synsetId), Long.toString(wordId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareMorphs(final long wordId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = WordNetContract.Lexes_Morphs.CONTENT_URI_TABLE;
		providerSql.projection = new String[]{WordNetContract.Lexes_Morphs.POSID, WordNetContract.Lexes_Morphs.MORPH};
		providerSql.selection = WordNetContract.Lexes_Morphs.WORDID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(wordId)};
		return providerSql;
	}
}
