/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import org.sqlunet.xnet.R;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

/**
 * Color values
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Colors
{
	static public int highlightBackColor = 0xFFffff59;
	static public int highlightForeColor = 0xFF000000;

	static public int classBackColor = 0xFF8b0000;
	static public int classForeColor = 0xFFffffff;

	static public int roleBackColor = 0xFFff00ff;
	static public int roleForeColor = 0xFFffffff;

	static public int memberBackColor = 0xFFff0000;
	static public int memberForeColor = 0xFFffffff;

	static public int dataBackColor = Color.TRANSPARENT; // 0x00000000
	static public int dataForeColor = Color.GRAY; // 0xFF888888

	static public int definitionBackColor = Color.TRANSPARENT; // 0x00000000
	static public int definitionForeColor = 0xFF4682B4;

	static public int exampleBackColor = 0xFFFFFFDD;
	static public int exampleForeColor = 0xFF000000;

	// PM
	static public int predicateNameBackColor = 0xFFFFFF99;
	static public int predicateNameForeColor = 0xFF000000;

	static public int roleAliasBackColor = 0xFF4251bd;
	static public int roleAliasForeColor = 0xFFffffff;

	// WN
	static public int lemmaBackColor = Color.TRANSPARENT;
	static public int lemmaForeColor = Color.BLACK;

	static public int membersBackColor = Color.TRANSPARENT;
	static public int membersForeColor = Color.DKGRAY;

	static public int wordBackColor = Color.TRANSPARENT;
	static public int wordForeColor = Color.BLACK;

	// FN
	static public int metaFrameDefinitionBackColor = Color.TRANSPARENT; // 0x00000000
	static public int metaFrameDefinitionForeColor = 0xFF8B0000;

	static public int tBackColor = Color.GRAY; // 0xFF888888
	static public int tForeColor = Color.WHITE; // 0xFFffffff

	static public int feBackColor = Color.MAGENTA; // 0xFFff00ff
	static public int feForeColor = Color.WHITE; // 0xFFffffff

	static public int fe2BackColor = Color.GRAY; // 0xFF888888
	static public int fe2ForeColor = Color.WHITE; // 0xFFffffff

	static public int xfenBackColor = Color.TRANSPARENT; // 0x00000000
	static public int xfenForeColor = Color.MAGENTA; // 0xFFff00ff

	static public int feAbbrevBackColor = Color.TRANSPARENT; // 0x00000000
	static public int feAbbrevForeColor = Color.MAGENTA; // 0xFFff00ff

	static public int tag1BackColor = Color.TRANSPARENT; // 0x00000000
	static public int tag1ForeColor = Color.GREEN; // 0xFF00ff00

	static public int tag2BackColor = Color.TRANSPARENT; // 0x00000000
	static public int tag2ForeColor = Color.CYAN; // 0xFF00ffff

	static public int metaFeDefinitionBackColor = Color.TRANSPARENT; // 0x00000000
	static public int metaFeDefinitionForeColor = 0xFF4682B4;

	static public int fenBackColor = Color.MAGENTA; // 0xFFff00ff;
	static public int fenForeColor = Color.WHITE; // 0xFFffffff

	static public int fenWithinDefBackColor = Color.TRANSPARENT; // 0x00000000
	static public int fenWithinDefForeColor = Color.MAGENTA; // 0xFFff00ff;

	static public int fexBackColor = 0xFFDD80DD;
	static public int fexForeColor = 0xFFffffff;

	static public int fexWithinDefBackColor = Color.TRANSPARENT; // 0x00000000
	static public int fexWithinDefForeColor = Color.GRAY; // 0xFF888888

	static public int xBackColor = Color.TRANSPARENT; // 0x00000000
	static public int xForeColor = Color.BLACK; // 0xFF000000;

	static public int layerTypeBackColor = 0xFFFFFF59;
	static public int layerTypeForeColor = 0xFF000000;

	static public int labelBackColor = 0xFFE9967A;
	static public int labelForeColor = 0xFFffffff;

	static public int subtextBackColor = Color.TRANSPARENT; // 0x00000000
	static public int subtextForeColor = Color.GRAY; // 0xFF888888

	static public int governorTypeBackColor = Color.TRANSPARENT; // 0x00000000
	static public int governorTypeForeColor = 0xFFFFA500;

	static public int groupBackColor = 0xFFFFA500;
	static public int groupForeColor = 0xFF000000;

	static public int targetBackColor = Color.RED; // 0xFFff0000
	static public int targetForeColor = Color.WHITE; // 0xFFffffff

	static public int targetHighlightTextBackColor = Color.BLACK; // 0xFF000000
	static public int targetHighlightTextForeColor = Color.WHITE; // 0xFFffffff

	static public int ptBackColor = Color.TRANSPARENT; // 0x00000000
	static public int ptForeColor = Color.BLACK; // 0xFF000000

	static public int gfBackColor = Color.TRANSPARENT; // 0x00000000
	static public int gfForeColor = Color.GRAY; // 0xFF888888

	static public int governorBackColor = Color.TRANSPARENT; // 0x00000000
	static public int governorForeColor = Color.BLACK; // 0xFF000000

	// VN
	static public int groupingBackColor = Color.TRANSPARENT; // 0x00000000
	static public int groupingForeColor = Color.LTGRAY; // 0xFFcccccc

	static public int vnPredicateBackColor = 0xFFFFA500;
	static public int vnPredicateForeColor = 0xFFffffff;

	static public int vnFrameBackColor = 0xFF6F4E37;
	static public int vnFrameForeColor = 0xFFffffff;

	static public int vnFrameSubnameBackColor = Color.LTGRAY; // 0xFFcccccc
	static public int vnFrameSubnameForeColor = 0xFFffffff;

	static public int themroleBackColor = 0xFFFFA500;
	static public int themroleForeColor = 0xFFffffff;

	static public int catBackColor = 0xFFFFE5B4;
	static public int catForeColor = Color.DKGRAY; // 0xFF444444

	static public int catValueBackColor = 0xFFDD80DD;
	static public int catValueForeColor = 0xFFffffff;

	static public int restrBackColor = 0xFFFFE5B4;
	static public int restrForeColor = Color.DKGRAY; // 0xFF444444

	static public int constantBackColor = Color.LTGRAY; // 0xFFcccccc
	static public int constantForeColor = 0xFF000000;

	static public int eventBackColor = 0xFF0000ff;
	static public int eventForeColor = 0xFFffffff;

	// PB
	static public int thetaBackColor = Color.BLUE;
	static public int thetaForeColor = Color.WHITE;

	static public int relationBackColor = Color.GRAY;
	static public int relationForeColor = Color.WHITE;

	// SN
	static public int collocationBackColor = Color.WHITE;
	static public int collocationForeColor = Color.DKGRAY;

	static public int word1BackColor = 0xFFE46D42;
	static public int word1ForeColor = Color.WHITE;

	static public int word2BackColor = 0xFF76608B;
	static public int word2ForeColor = Color.WHITE;

	static public int definition1BackColor = Color.WHITE;
	static public int definition1ForeColor = 0xFFE46D42;

	static public int definition2BackColor = Color.WHITE;
	static public int definition2ForeColor = 0xFF76608B;

	static public int idsBackColor = Color.TRANSPARENT;
	static public int idsForeColor = Color.GRAY;

	// BNC
	static public int bncHeaderBackColor = 0xFFE9967A;
	static public int bncHeaderForeColor = 0xFFE9967A;

	// TEXT
	static public int textBackColor = Color.TRANSPARENT;
	static public int textForeColor = Color.GRAY;

	static public int textMatchBackColor = Color.TRANSPARENT;
	static public int textMatchForeColor = Color.BLACK;

	static public int textDimmedBackColor = Color.TRANSPARENT;
	static public int textDimmedForeColor = Color.GRAY;

	static public int textNormalBackColor = Color.TRANSPARENT;
	static public int textNormalForeColor = Color.BLACK;

	// SQL
	static public int sqlKeywordBackColor = Color.TRANSPARENT;
	static public int sqlKeywordForeColor = 0xFF800080;

	static public int sqlSlashBackColor = Color.TRANSPARENT;
	static public int sqlSlashForeColor = 0xFF008000;

	static public int sqlQuestionMarkBackColor = Color.TRANSPARENT;
	static public int sqlQuestionMarkForeColor = Color.RED;

	// REPORT

	static public int storageTypeBackColor = 0xFFE9967A; // pink
	static public int storageTypeForeColor = Color.BLACK;

	static public int storageValueBackColor = Color.TRANSPARENT;
	static public int storageValueForeColor = Color.GRAY;

	static public int dirTypeBackColor = 0xFFFFFF99; // ltyellow
	static public int dirTypeForeColor = Color.BLACK;

	static public int dirValueBackColor = Color.TRANSPARENT;
	static public int dirValueForeColor = Color.GRAY;

	static public int dirOkBackColor = 0xFF008B00; // dkgreen
	static public int dirOkForeColor = Color.BLACK;

	static public int dirFailBackColor = 0xFF8B0000; // dkred
	static public int dirFailForeColor = Color.BLACK;


	static public void setColorsFromResources(@NonNull final Context context)
	{
		int[] palette = context.getResources().getIntArray(R.array.palette);

		highlightBackColor = palette[0];
		highlightForeColor = palette[1];
	}

	public static int[] getColors(@NonNull final Context context, @ColorRes final int... colorIds)
	{
		int[] result = new int[colorIds.length];
		for (int i = 0; i < colorIds.length; i++)
		{
			result[i] = context.getResources().getColor(colorIds[i]);
		}
		return result;
	}
}
