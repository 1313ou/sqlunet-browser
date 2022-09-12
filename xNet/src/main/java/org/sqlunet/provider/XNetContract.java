/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.provider;

import org.sqlunet.loaders.Queries;
import org.sqlunet.xnet.provider.Q;
import org.sqlunet.xnet.provider.V;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public final class XNetContract
{
	// A L I A S E S

	public static final String AS_WORDS = V.AS_WORDS;
	public static final String AS_SENSES = V.AS_SENSES;
	public static final String AS_SYNSETS = V.AS_SYNSETS;
	public static final String AS_POSES = V.AS_POSES;
	public static final String AS_CLASSES = V.AS_CLASSES;

	public interface Words_FnWords_PbWords_VnWords
	{
		String URI = "words_fnwords_pbwords_vnwords";
		String WORD = V.WORD;
		String WORDID = V.WORDID;
		String FNWORDID = V.FNWORDID;
		String PBWORDID = V.PBWORDID;
		String VNWORDID = V.VNWORDID;
		String SYNSETID = V.SYNSETID;
		String LUID = V.LUID;
		String SENSEID = V.SENSEID;
		String SENSENUM = V.SENSENUM;
		String SENSEKEY = V.SENSEKEY;
		String POSID = V.POSID;
		String POS = V.POS;
		String DOMAIN = V.DOMAIN;
		String DEFINITION = V.DEFINITION;
		String CASED = V.CASEDWORD;
		String TAGCOUNT = V.TAGCOUNT;
		String SOURCES = V.SOURCES;
	}

	public interface Words_Pronunciations_FnWords_PbWords_VnWords extends Words_FnWords_PbWords_VnWords
	{
		String URI = "words_pronunciations_fnwords_pbwords_vnwords";
		String PRONUNCIATION = V.PRONUNCIATION;
		String VARIETY = V.VARIETY;
		String PRONUNCIATIONS = Queries.PRONUNCIATIONS;
	}

	public interface Words_PbWords_VnWords
	{
		String URI = "words_pbwords_vnwords";
		String WORD = V.WORD;
		String WORDID = V.WORDID;
		String PBWORDID = V.PBWORDID;
		String VNWORDID = V.VNWORDID;
		String SYNSETID = V.SYNSETID;
		String LUID = V.LUID;
		String SENSEID = V.SENSEID;
		String SENSENUM = V.SENSENUM;
		String SENSEKEY = V.SENSEKEY;
		String POSID = V.POSID;
		String POS = V.POS;
		String DOMAIN = V.DOMAIN;
		String DEFINITION = V.DEFINITION;
		String CASEDWORD = V.CASEDWORD;
		String TAGCOUNT = V.TAGCOUNT;
		String SOURCES = V.SOURCES;
	}

	public interface Words_VnWords_VnClasses
	{
		String URI = "words_vnwords_vnclasses";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String VNWORDID = V.VNWORDID;
		String CLASSID = V.CLASSID;
		String CLASS = V.CLASS;
		String CLASSTAG = V.CLASSTAG;
		String DEFINITION = V.DEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_PbWords_PbRoleSets
	{
		String URI = "words_pbwords_pbrolesets";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String ROLESETID = V.ROLESETID;
		String ROLESETNAME = V.ROLESETNAME;
		String ROLESETHEAD = V.ROLESETHEAD;
		String ROLESETDESCR = V.ROLESETDESCR;
		String DEFINITION = V.DEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_XNet
	{
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String XID = V.XID;
		String XCLASSID = V.XCLASSID;
		String XMEMBERID = V.XMEMBERID;
		String XNAME = V.XNAME;
		String XHEADER = V.XHEADER;
		String XINFO = V.XINFO;
		String XDEFINITION = V.XDEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	// Sources

	public interface Sources
	{
		String TABLE = Q.SOURCES.TABLE;
		String URI = Sources.TABLE;
		String ID = "idsource";
		String NAME = "name";
		String VERSION = "version";
		String URL = "url";
		String PROVIDER = "provider";
		String REFERENCE = "reference";
	}

	// Meta

	public interface Meta
	{
		String TABLE = Q.META.TABLE;
		String URI = Meta.TABLE;
		String CREATED = "created";
		String DBSIZE = "dbsize";
		String BUILD = "build";
	}

	// PredicateMatrix

	public interface PredicateMatrix
	{
		String URI = "predicatematrix";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String VNWORDID = V.VNWORDID;
		String VNCLASSID = V.CLASSID;
		String PBWORDID = V.PBWORDID;
		String PBROLESETID = V.ROLESETID;
		String FNWORDID = V.FNWORDID;
		String LUID = V.LUID;
		String FRAMEID = V.FRAMEID;
	}

	public interface PredicateMatrix_VerbNet
	{
		String URI = "predicatematrix_verbnet";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String VNWORDID = V.VNWORDID;
		String CLASSID = V.CLASSID;
	}

	public interface PredicateMatrix_PropBank
	{
		String URI = "predicatematrix_propbank";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String PBWORDID = V.PBWORDID;
		String ROLESETID = V.ROLESETID;
	}

	public interface PredicateMatrix_FrameNet
	{
		String URI = "predicatematrix_framenet";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String FNWORDID = V.FNWORDID;
		String LUID = V.LUID;
		String FRAMEID = V.FRAMEID;
	}

	// PredicateMatrix unions

	public interface Words_XNet_U
	{
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String XID = V.XID;
		String XCLASSID = V.XCLASSID;
		String XMEMBERID = V.XMEMBERID;
		String XNAME = V.XNAME;
		String XHEADER = V.XHEADER;
		String XINFO = V.XINFO;
		String XPRONUNCIATION = V.XPRONUNCIATION;
		String XDEFINITION = V.XDEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_VnWords_VnClasses_U
	{
		String URI = "words_vnwords_vnclasses_u";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String VNWORDID = V.VNWORDID;
		String CLASSID = V.CLASSID;
		String CLASS = V.CLASS;
		String CLASSTAG = V.CLASSTAG;
		String DEFINITION = V.DEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_VnWords_VnClasses_1U2
	{
		String URI = "words_vnwords_vnclasses_1u2";
	}

	public interface Words_VnWords_VnClasses_1
	{
		String URI = "words_vnwords_vnclasses_1";
	}

	public interface Words_VnWords_VnClasses_2
	{
		String URI = "words_vnwords_vnclasses_2";
	}

	public interface Words_PbWords_PbRoleSets_U
	{
		String URI = "words_pbwords_pbrolesets_u";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String ROLESETID = V.ROLESETID;
		String ROLESETNAME = V.ROLESETNAME;
		String ROLESETHEAD = V.ROLESETHEAD;
		String ROLESETDESCR = V.ROLESETDESCR;
		String DEFINITION = V.DEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_PbWords_PbRoleSets_1
	{
		String URI = "words_pbwords_pbrolesets_1";
	}

	public interface Words_PbWords_PbRoleSets_2
	{
		String URI = "words_pbwords_pbrolesets_2";
	}

	public interface Words_PbWords_PbRoleSets_1U2
	{
		String URI = "words_pbwords_pbrolesets_1u2";
	}

	public interface Words_FnWords_FnFrames_U
	{
		String URI = "words_fnwords_fnframes_u";
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String FNWORDID = V.FNWORDID;
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAMEDEFINITION = V.FRAMEDEFINITION;
		String LUID = V.LUID;
		String LEXUNIT = V.LEXUNIT;
		String LUDEFINITION = V.LUDEFINITION;
		String DEFINITION = V.DEFINITION;
		String SOURCE = V.SOURCE;
		String SOURCES = V.SOURCES;
	}

	public interface Words_FnWords_FnFrames_1U2
	{
		String URI = "words_fnwords_fnframes_1u2";
	}

	public interface Words_FnWords_FnFrames_1
	{
		String URI = "words_fnwords_fnframes_1";
	}

	public interface Words_FnWords_FnFrames_2
	{
		String URI = "words_fnwords_fnframes_2";
	}
}
