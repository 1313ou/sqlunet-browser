/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.loaders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.bnc.R;
import org.sqlunet.bnc.provider.BNCContract.Words_BNCs;
import org.sqlunet.bnc.provider.BNCProvider;
import org.sqlunet.bnc.style.BNCFactories;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOpExecute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

public class BaseModule extends Module
{
	/**
	 * Query
	 */
	@Nullable
	private Long wordId;

	@Nullable
	private Character pos;

	// Resources

	/**
	 * Drawable for conv/task
	 */
	@NonNull
	private final Drawable convtaskDrawable;

	/**
	 * Drawable for spoken/written
	 */
	@NonNull
	private final Drawable imaginfDrawable;

	/**
	 * Drawable for spoken/written
	 */
	@NonNull
	private final Drawable spwrDrawable;

	/**
	 * Drawable for pos
	 */
	@NonNull
	private final Drawable posDrawable;

	// View models

	private SqlunetViewTreeModel bncFromWordIdModel;

	/**
	 * Constructor
	 */
	public BaseModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();

		// drawables
		final Context context = this.fragment.requireContext();
		this.convtaskDrawable = Spanner.getDrawable(context, R.drawable.convtask);
		this.imaginfDrawable = Spanner.getDrawable(context, R.drawable.imaginf);
		this.spwrDrawable = Spanner.getDrawable(context, R.drawable.spwr);
		this.posDrawable = Spanner.getDrawable(context, R.drawable.pos);
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.bncFromWordIdModel = new ViewModelProvider(this.fragment).get("bnc.bnc(wordid)", SqlunetViewTreeModel.class);
		this.bncFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.wordId = null;
		this.pos = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
		if (pointer instanceof HasPos)
		{
			final HasPos posPointer = (HasPos) pointer;
			this.pos = posPointer.getPos();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.wordId != null)
		{
			// data
			bnc(this.wordId, this.pos, node);
		}
	}

	// L O A D E R S

	// contents

	/**
	 * Load BNC data
	 *
	 * @param wordId word id
	 * @param pos    pos
	 * @param parent parent node
	 */
	private void bnc(final long wordId, @Nullable final Character pos, @NonNull final TreeNode parent)
	{
		final ContentProviderSql sql = Queries.prepareBnc(wordId, pos);
		final Uri uri = Uri.parse(BNCProvider.makeUri(sql.providerUri));
		this.bncFromWordIdModel.loadData(uri, sql, cursor -> bncCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] bncCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		// if (cursor.getCount() > 1)
		// throw new RuntimeException("Unexpected number of rows");
		if (cursor.moveToFirst())
		{
			final int idPos = cursor.getColumnIndexOrThrow(Words_BNCs.POSID);
			final int idFreq = cursor.getColumnIndexOrThrow(Words_BNCs.FREQ);
			final int idRange = cursor.getColumnIndexOrThrow(Words_BNCs.RANGE);
			final int idDisp = cursor.getColumnIndexOrThrow(Words_BNCs.DISP);

			final int idConvFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ1);
			final int idConvRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE1);
			final int idConvDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP1);

			final int idTaskFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ2);
			final int idTaskRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE2);
			final int idTaskDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP2);

			final int idImagFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ1);
			final int idImagRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE1);
			final int idImagDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP1);

			final int idInfFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ2);
			final int idInfRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE2);
			final int idInfDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP2);

			final int idSpFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.FREQ1);
			final int idSpRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.RANGE1);
			final int idSpDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.DISP1);

			final int idWrFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.FREQ2);
			final int idWrRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.RANGE2);
			final int idWrDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.DISP2);

			do
			{
				String pos1 = cursor.getString(idPos);
				Spanner.appendImage(sb, BaseModule.this.posDrawable);
				sb.append(' ');
				sb.append(pos1);
				sb.append('\n');

				String value1;
				if ((value1 = cursor.getString(idFreq)) != null)
				{
					sb.append("frequency=").append(value1).append(" per million").append('\n');
				}
				if ((value1 = cursor.getString(idRange)) != null)
				{
					sb.append("range=").append(value1).append('\n');
				}
				if ((value1 = cursor.getString(idDisp)) != null)
				{
					sb.append("dispersion=").append(value1);
				}
				sb.append('\n');

				String fvalue = cursor.getString(idConvFreq);
				String fvalue2 = cursor.getString(idTaskFreq);
				String rvalue = cursor.getString(idConvRange);
				String rvalue2 = cursor.getString(idTaskRange);
				String dvalue = cursor.getString(idConvDisp);
				String dvalue2 = cursor.getString(idTaskDisp);
				if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null)
				{
					Spanner.appendImage(sb, BaseModule.this.convtaskDrawable);
					sb.append(' ');
					Spanner.append(sb, "conversation / task\n", 0, BNCFactories.headerFactory);
					if (fvalue != null && fvalue2 != null)
					{
						sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n');
					}
					if (rvalue != null && rvalue2 != null)
					{
						sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n');
					}
					if (dvalue != null && dvalue2 != null)
					{
						sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2);
					}
					sb.append('\n');
				}

				fvalue = cursor.getString(idImagFreq);
				fvalue2 = cursor.getString(idInfFreq);
				rvalue = cursor.getString(idImagRange);
				rvalue2 = cursor.getString(idInfRange);
				dvalue = cursor.getString(idImagDisp);
				dvalue2 = cursor.getString(idInfDisp);
				if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null)
				{
					Spanner.appendImage(sb, BaseModule.this.imaginfDrawable);
					sb.append(' ');
					Spanner.append(sb, "imagination / information\n", 0, BNCFactories.headerFactory);
					if (fvalue != null && fvalue2 != null)
					{
						sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n');
					}
					if (rvalue != null && rvalue2 != null)
					{
						sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n');
					}
					if (dvalue != null && dvalue2 != null)
					{
						sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2);
					}
					sb.append('\n');
				}

				fvalue = cursor.getString(idSpFreq);
				fvalue2 = cursor.getString(idWrFreq);
				rvalue = cursor.getString(idSpRange);
				rvalue2 = cursor.getString(idWrRange);
				dvalue = cursor.getString(idSpDisp);
				dvalue2 = cursor.getString(idWrDisp);
				if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null)
				{
					Spanner.appendImage(sb, BaseModule.this.spwrDrawable);
					sb.append(' ');
					Spanner.append(sb, "spoken / written\n", 0, BNCFactories.headerFactory);
					if (fvalue != null && fvalue2 != null)
					{
						sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n');
					}
					if (rvalue != null && rvalue2 != null)
					{
						sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n');
					}
					if (dvalue != null && dvalue2 != null)
					{
						sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2);
					}
					sb.append('\n');
				}
			}
			while (cursor.moveToNext());

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
			changed = TreeOp.seq(NEWUNIQUE, node);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}
}
