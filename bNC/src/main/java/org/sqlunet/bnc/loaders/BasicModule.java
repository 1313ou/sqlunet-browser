package org.sqlunet.bnc.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.bnc.R;
import org.sqlunet.bnc.provider.BNCContract.Words_BNCs;
import org.sqlunet.bnc.style.BNCFactories;
import org.sqlunet.browser.Module;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.view.FireEvent;

public class BasicModule extends Module
{
	/**
	 * Query
	 */
	private Long wordId;

	private Character pos;

	// Resources

	/**
	 * Drawable for domain
	 */
	private final Drawable domainDrawable;

	/**
	 * Drawable for pos
	 */
	private final Drawable posDrawable;

	/**
	 * Constructor
	 */
	public BasicModule(final Fragment fragment)
	{
		super(fragment);

		// drawables
		this.domainDrawable = Spanner.getDrawable(this.context, R.drawable.domain);
		this.posDrawable = Spanner.getDrawable(this.context, R.drawable.pos);
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
	public void process(final TreeNode node)
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
	private void bnc(final long wordId, final Character pos, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_BNCs.CONTENT_URI);
				final String[] projection = {Words_BNCs.POS, Words_BNCs.FREQ, Words_BNCs.RANGE, Words_BNCs.DISP, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ1, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE1, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP1, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ2, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE2, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP2, //

						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ1, //
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE1, //
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP1, //
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ2, //
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE2, //
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP2, //

						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ1, //
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE1, //
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP1, //
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ2, //
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE2, //
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP2, //
				};
				final String selection = pos == null ? Words_BNCs.WORDID + " = ?" : Words_BNCs.WORDID + " = ? AND " + Words_BNCs.POS + "= ?";
				final String[] selectionArgs = pos == null ? new String[]{Long.toString(wordId)} : new String[]{Long.toString(wordId), Character.toString(pos),};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();
				// if (cursor.getCount() > 1)
				// throw new RuntimeException("Unexpected number of rows");
				if (cursor.moveToFirst())
				{
					final int idPos = cursor.getColumnIndexOrThrow(Words_BNCs.POS);
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
						Spanner.appendImage(sb, BasicModule.this.posDrawable);
						sb.append(' ');
						sb.append(pos1);
						sb.append('\n');

						String value1;
						if ((value1 = cursor.getString(idFreq)) != null)
						{
							sb.append("freq=").append(value1).append(' ');
						}
						if ((value1 = cursor.getString(idRange)) != null)
						{
							sb.append("range=").append(value1).append(' ');
						}
						if ((value1 = cursor.getString(idDisp)) != null)
						{
							sb.append("disp=").append(value1);
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
							Spanner.appendImage(sb, BasicModule.this.domainDrawable);
							sb.append(' ');
							Spanner.append(sb, "conversation / task\n", 0, BNCFactories.headerFactory);
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' ');
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' ');
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2);
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
							Spanner.appendImage(sb, BasicModule.this.domainDrawable);
							sb.append(' ');
							Spanner.append(sb, "imagination / information\n", 0, BNCFactories.headerFactory);
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' ');
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' ');
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2);
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
							Spanner.appendImage(sb, BasicModule.this.domainDrawable);
							sb.append(' ');
							Spanner.append(sb, "spoken / written\n", 0, BNCFactories.headerFactory);
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' ');
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' ');
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2);
							}
							sb.append('\n');

						}
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.context);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg)
			{
				//
			}
		});
	}
}
