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
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

public class BasicModule extends Module
{
	/**
	 * Query
	 */
	private Long wordid;

	private Character pos;

	// Resources

	/**
	 * Drawable for domain
	 */
	private Drawable domainDrawable;

	/**
	 * Drawable for pos
	 */
	private Drawable posDrawable;

	/**
	 * Constructor
	 */
	public BasicModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.Module#init(android.widget.TextView, android.widget.TextView, android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query)
	{
		// drawables
		this.domainDrawable = Spanner.getDrawable(getContext(), R.drawable.domain);
		this.posDrawable = Spanner.getDrawable(getContext(), R.drawable.pos);

		// get query
		if (query instanceof HasWordId)
		{
			final HasWordId wordQuery = (HasWordId) query;
			this.wordid = wordQuery.getWordId();
		}
		if (query instanceof HasPos)
		{
			final HasPos posQuery = (HasPos) query;
			this.pos = posQuery.getPos();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.wordid != null)
		{
			// data
			bnc(this.wordid, this.pos, node);
		}
	}

	// L O A D E R S

	// contents

	private void bnc(final long wordid0, final Character pos0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(Words_BNCs.CONTENT_URI);
				final String[] projection = {Words_BNCs.POS, Words_BNCs.FREQ, Words_BNCs.RANGE, Words_BNCs.DISP, //
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ1, // //$NON-NLS-1$
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE1, // //$NON-NLS-1$
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP1, // //$NON-NLS-1$
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ2, // //$NON-NLS-1$
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE2, // //$NON-NLS-1$
						Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP2, // //$NON-NLS-1$

						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ1, // //$NON-NLS-1$
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE1, // //$NON-NLS-1$
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP1, // //$NON-NLS-1$
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ2, // //$NON-NLS-1$
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE2, // //$NON-NLS-1$
						Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP2, // //$NON-NLS-1$

						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ1, // //$NON-NLS-1$
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE1, // //$NON-NLS-1$
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP1, // //$NON-NLS-1$
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ2, // //$NON-NLS-1$
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE2, // //$NON-NLS-1$
						Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP2, // //$NON-NLS-1$
				};
				final String selection = pos0 == null ? Words_BNCs.WORDID + " = ?" : Words_BNCs.WORDID + " = ? AND " + Words_BNCs.POS + "= ?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String[] selectionArgs = pos0 == null ? new String[]{Long.toString(wordid0)} : new String[]{Long.toString(wordid0), Character.toString(pos0),};
				final String sortOrder = null;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
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
							sb.append("freq=").append(value1).append(' '); //$NON-NLS-1$
						}
						if ((value1 = cursor.getString(idRange)) != null)
						{
							sb.append("range=").append(value1).append(' '); //$NON-NLS-1$
						}
						if ((value1 = cursor.getString(idDisp)) != null)
						{
							sb.append("disp=").append(value1); //$NON-NLS-1$
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
							Spanner.append(sb, "conversation / task\n", 0, BNCFactories.headerFactory); //$NON-NLS-1$
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2); //$NON-NLS-1$ //$NON-NLS-2$
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
							Spanner.append(sb, "imagination / information\n", 0, BNCFactories.headerFactory); //$NON-NLS-1$
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2); //$NON-NLS-1$ //$NON-NLS-2$
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
							Spanner.append(sb, "spoken / written\n", 0, BNCFactories.headerFactory); //$NON-NLS-1$
							if (fvalue != null && fvalue2 != null)
							{
								sb.append("freq=").append(fvalue).append(" / ").append(fvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (rvalue != null && rvalue2 != null)
							{
								sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
							}
							if (dvalue != null && dvalue2 != null)
							{
								sb.append("disp=").append(dvalue).append(" / ").append(dvalue2); //$NON-NLS-1$ //$NON-NLS-2$
							}
							sb.append('\n');

						}
					}
					while (cursor.moveToNext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				//
			}
		});
	}
}
