package org.sqlunet.propbank.loaders;

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

import org.sqlunet.browser.Module;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.provider.PropbankContract;
import org.sqlunet.propbank.provider.PropbankContract.PbRolesets;
import org.sqlunet.propbank.provider.PropbankContract.PbRolesets_PbExamples;
import org.sqlunet.propbank.provider.PropbankContract.PbRolesets_PbRoles;
import org.sqlunet.propbank.provider.PropbankContract.Words_PbRolesets;
import org.sqlunet.propbank.style.PropbankFactories;
import org.sqlunet.propbank.style.PropbankSpanner;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.QueryHolder;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import java.util.Arrays;

/**
 * Module for rolesets
 *
 * @author Bernard Bou
 */
abstract class BasicModule extends Module
{
	// resources

	/**
	 * Drawable for rolesets
	 */
	private Drawable rolesetDrawable;

	/**
	 * Drawable for roles
	 */
	private Drawable rolesDrawable;

	/**
	 * Drawable for relation
	 */
	private Drawable relationDrawable;

	/**
	 * Drawable for role
	 */
	private Drawable roleDrawable;

	/**
	 * Drawable for theta role
	 */
	private Drawable thetaDrawable;

	/**
	 * Drawable for definition
	 */
	private Drawable definitionDrawable;

	/**
	 * Drawable for sample
	 */
	private Drawable sampleDrawable;

	// spanner

	/**
	 * Spanner
	 */
	private PropbankSpanner spanner;

	/**
	 * Constructor
	 */
	BasicModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/**
	 * Unmarshall query arguments
	 *
	 * @param query parceled query
	 */
	abstract void unmarshall(final Parcelable query);

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.Module#init(android.widget.TextView, android.widget.TextView, android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query)
	{
		// spanner
		this.spanner = new PropbankSpanner(getContext());

		// drawables
		this.rolesetDrawable = Spanner.getDrawable(getContext(), R.drawable.roleset);
		this.rolesDrawable = Spanner.getDrawable(getContext(), R.drawable.roles);
		this.relationDrawable = Spanner.getDrawable(getContext(), R.drawable.relation);
		this.roleDrawable = Spanner.getDrawable(getContext(), R.drawable.role);
		this.thetaDrawable = Spanner.getDrawable(getContext(), R.drawable.theta);
		this.definitionDrawable = Spanner.getDrawable(getContext(), R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(getContext(), R.drawable.sample);

		// get query
		unmarshall(query);
	}

	// L O A D E R S

	// rolesets

	void roleset(final long rolesetid0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(PbRolesets.CONTENT_URI);
				final String[] projection = new String[]{ //
						PbRolesets.ROLESETID, //
						PbRolesets.ROLESETNAME, //
						PbRolesets.ROLESETHEAD, //
						PbRolesets.ROLESETDESC, //
				};
				final String selection = PbRolesets.ROLESETID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[]{Long.toString(rolesetid0)};
				final String sortOrder = null;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				}
				if (cursor.moveToFirst())
				{
					// column indices
					final int idRolesetId = cursor.getColumnIndex(PbRolesets.ROLESETID);
					final int idRolesetName = cursor.getColumnIndex(PbRolesets.ROLESETNAME);
					final int idRolesetDesc = cursor.getColumnIndex(PbRolesets.ROLESETDESC);
					final int idRolesetHead = cursor.getColumnIndex(PbRolesets.ROLESETHEAD);

					// read cursor
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// data
					final int rolesetid1 = cursor.getInt(idRolesetId);

					// roleset
					Spanner.appendImage(sb, BasicModule.this.rolesetDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idRolesetName), 0, PropbankFactories.rolesetFactory);
					sb.append(' ');
					sb.append("head="); //$NON-NLS-1$
					sb.append(cursor.getString(idRolesetHead));
					sb.append('\n');

					// description
					Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
					Spanner.append(sb, cursor.getString(idRolesetDesc), 0, PropbankFactories.definitionFactory);

					// sub nodes
					final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(rolesetid1, R.drawable.roles, "Roles"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode examplesNode = TreeFactory.newQueryNode(new ExamplesQuery(rolesetid1, R.drawable.sample, "Examples"), BasicModule.this.getContext()); //$NON-NLS-1$

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), rolesNode, examplesNode);

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

	void rolesets(final long wordid0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(PropbankContract.Words_PbRolesets.CONTENT_URI);
				final String[] projection = new String[]{ //
						Words_PbRolesets.ROLESETID, //
						Words_PbRolesets.ROLESETNAME, //
						Words_PbRolesets.ROLESETHEAD, //
						Words_PbRolesets.ROLESETDESC, //
				};
				final String selection = Words_PbRolesets.WORDID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[]{Long.toString(wordid0)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idRolesetId = cursor.getColumnIndex(Words_PbRolesets.ROLESETID);
					final int idRolesetName = cursor.getColumnIndex(Words_PbRolesets.ROLESETNAME);
					final int idRolesetDesc = cursor.getColumnIndex(Words_PbRolesets.ROLESETDESC);
					final int idRolesetHead = cursor.getColumnIndex(Words_PbRolesets.ROLESETHEAD);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						final int rolesetid1 = cursor.getInt(idRolesetId);

						// roleset
						Spanner.appendImage(sb, BasicModule.this.rolesDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRolesetName), 0, PropbankFactories.rolesetFactory);
						sb.append(' ');
						sb.append("head="); //$NON-NLS-1$
						sb.append(cursor.getString(idRolesetHead));
						sb.append('\n');

						// description
						Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
						Spanner.append(sb, cursor.getString(idRolesetDesc), 0, PropbankFactories.definitionFactory);

						// sub nodes
						final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(rolesetid1, R.drawable.roles, "Roles"), BasicModule.this.getContext()); //$NON-NLS-1$
						final TreeNode examplesNode = TreeFactory.newQueryNode(new ExamplesQuery(rolesetid1, R.drawable.sample, "Examples"), BasicModule.this.getContext()); //$NON-NLS-1$

						// attach result
						TreeFactory.addTextNode(parent, sb, getContext(), rolesNode, examplesNode);
					}
					while (cursor.moveToNext());

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

	// roles

	private void roles(final int rolesetid, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(PbRolesets_PbRoles.CONTENT_URI);
				final String[] projection = new String[]{ //
						PbRolesets_PbRoles.ROLEID, //
						PbRolesets_PbRoles.ROLEDESCR, //
						PbRolesets_PbRoles.NARG, //
						PbRolesets_PbRoles.FUNCNAME, //
						PbRolesets_PbRoles.THETANAME, //
				};
				final String selection = PbRolesets_PbRoles.ROLESETID + "= ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[]{Long.toString(rolesetid)};
				final String sortOrder = null;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();
				if (cursor.moveToFirst())
				{
					// column indices
					// final int idRoleId = cursor.getColumnIndex(PbRolesets_PbRoles.ROLEID);
					final int idRoleDescr = cursor.getColumnIndex(PbRolesets_PbRoles.ROLEDESCR);
					final int idFunc = cursor.getColumnIndex(PbRolesets_PbRoles.FUNCNAME);
					final int idTheta = cursor.getColumnIndex(PbRolesets_PbRoles.THETANAME);
					final int idNArg = cursor.getColumnIndex(PbRolesets_PbRoles.NARG);

					// read cursor
					while (true)
					{
						// data

						// n
						sb.append(cursor.getString(idNArg));
						sb.append(' ');

						// theta
						String theta = cursor.getString(idTheta);
						if (theta != null && !theta.isEmpty())
						{
							Spanner.appendImage(sb, BasicModule.this.thetaDrawable);
							sb.append(' ');
							Spanner.append(sb, theta, 0, PropbankFactories.thetaFactory);
							sb.append(' ');
						}

						// role
						Spanner.appendImage(sb, BasicModule.this.roleDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRoleDescr), 0, PropbankFactories.roleFactory);

						// func
						if (!cursor.isNull(idFunc))
						{
							sb.append(' ');
							sb.append("func="); //$NON-NLS-1$
							sb.append(Integer.toString(cursor.getInt(idFunc)));
						}

						// final int roleid = cursor.getInt(idRoleId);
						// sb.append(" roleid=");
						// sb.append(Integer.toString(roleid));
						// sb.append(' ');

						if (!cursor.moveToNext())
						//noinspection BreakStatement
						{
							break;
						}

						sb.append('\n');
					}

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

	// examples

	private void examples(final int rolesetid, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(PbRolesets_PbExamples.CONTENT_URI);
				final String[] projection = new String[]{ //
						PbRolesets_PbExamples.TEXT, //
						PbRolesets_PbExamples.REL, //
						"GROUP_CONCAT(" + // //$NON-NLS-1$
								PbRolesets_PbExamples.NARG + //
								"||'~'" + // //$NON-NLS-1$
								"||(CASE WHEN " + PbRolesets_PbExamples.FUNCNAME + " IS NULL THEN '*' ELSE " + PbRolesets_PbExamples.FUNCNAME + " END)" + // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								"||'~'" + // //$NON-NLS-1$
								"||" + PbRolesets_PbExamples.ROLEDESCR + // //$NON-NLS-1$
								"||'~'" + // //$NON-NLS-1$
								"||(CASE WHEN " + PbRolesets_PbExamples.THETANAME + " IS NULL THEN '*' ELSE " + PbRolesets_PbExamples.THETANAME + " END)" + // //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								"||'~'" + // //$NON-NLS-1$
								"||" + PbRolesets_PbExamples.ARG + ",'|') AS " + PbRolesets_PbExamples.ARGS, // //$NON-NLS-1$ //$NON-NLS-2$
						PbRolesets_PbExamples.ASPECTNAME, //
						PbRolesets_PbExamples.FORMNAME, //
						PbRolesets_PbExamples.TENSENAME, //
						PbRolesets_PbExamples.VOICENAME, //
						PbRolesets_PbExamples.PERSONNAME, //
				};
				final String selection = PbRolesets_PbExamples.ROLESETID + "= ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[]{Long.toString(rolesetid)};
				final String sortOrder = PbRolesets_PbExamples.EXAMPLEID + ',' + PbRolesets_PbExamples.NARG;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idText = cursor.getColumnIndex(PbRolesets_PbExamples.TEXT);
					final int idRel = cursor.getColumnIndex(PbRolesets_PbExamples.REL);
					final int idArgs = cursor.getColumnIndex(PbRolesets_PbExamples.ARGS);

					// read cursor
					do
					{
						// data

						// text
						String text = cursor.getString(idText);
						Spanner.appendImage(sb, BasicModule.this.sampleDrawable);
						Spanner.append(sb, text, 0, PropbankFactories.exampleFactory);
						sb.append('\n');

						// relation
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.relationDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRel), 0, PropbankFactories.relationFactory);

						// args
						final String argspack = cursor.getString(idArgs);
						if (argspack != null)
						{
							final String[] args = argspack.split("\\|"); //$NON-NLS-1$
							Arrays.sort(args);
							for (final String arg : args)
							{
								final String[] fields = arg.split("~"); //$NON-NLS-1$
								if (fields.length < 5)
								{
									sb.append(arg);
									continue;
								}

								sb.append('\n');
								sb.append('\t');

								// n
								// sb.append(fields[0]);
								// sb.append(' ');

								// theta
								Spanner.appendImage(sb, BasicModule.this.thetaDrawable);
								sb.append(' ');
								Spanner.append(sb, fields[3], 0, PropbankFactories.thetaFactory);
								sb.append(' ');

								// role
								Spanner.appendImage(sb, BasicModule.this.roleDrawable);
								sb.append(' ');
								Spanner.append(sb, fields[2], 0, PropbankFactories.roleFactory);

								// func
								if (!fields[1].isEmpty())
								{
									// sb.append(" func=");
									sb.append(' ');
									sb.append(fields[1]);
								}

								// subtext
								sb.append(' ');
								// sb.append("subtext=");
								Spanner.append(sb, fields[4], 0, PropbankFactories.textFactory);
							}
						}
					}
					while (cursor.moveToNext());

					// extra format
					BasicModule.this.spanner.setSpan(sb, 0, 0);

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

	class RolesQuery extends QueryHolder.Query
	{
		public RolesQuery(final long rolesetid0, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(rolesetid0, icon, text);
		}

		@Override
		public void process(final TreeNode node0)
		{
			roles((int) this.id, node0);
		}
	}

	class ExamplesQuery extends QueryHolder.Query
	{
		public ExamplesQuery(final long rolesetid0, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(rolesetid0, icon, text);
		}

		@Override
		public void process(final TreeNode node0)
		{
			examples((int) this.id, node0);
		}
	}
}
