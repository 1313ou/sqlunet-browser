package org.sqlunet.propbank.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles;
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets;
import org.sqlunet.propbank.style.PropBankFactories;
import org.sqlunet.propbank.style.PropBankSpanner;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.Query;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import java.util.Arrays;
import java.util.Locale;

/**
 * Module for PropBank role sets
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BasicModule extends Module
{
	// resources

	/**
	 * Drawable for roleSets
	 */
	private Drawable roleSetDrawable;

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

	// agents

	/**
	 * Spanner
	 */
	private PropBankSpanner spanner;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BasicModule(final Fragment fragment)
	{
		super(fragment);

		// drawables
		this.roleSetDrawable = Spanner.getDrawable(getContext(), R.drawable.roleset);
		this.rolesDrawable = Spanner.getDrawable(getContext(), R.drawable.roles);
		this.relationDrawable = Spanner.getDrawable(getContext(), R.drawable.relation);
		this.roleDrawable = Spanner.getDrawable(getContext(), R.drawable.role);
		this.thetaDrawable = Spanner.getDrawable(getContext(), R.drawable.theta);
		this.definitionDrawable = Spanner.getDrawable(getContext(), R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(getContext(), R.drawable.sample);

		// spanner
		this.spanner = new PropBankSpanner(getContext());
	}

	// L O A D E R S

	// role sets

	/**
	 * Role set from id
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	void roleSet(final long roleSetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(PropBankContract.PbRoleSets.CONTENT_URI);
				final String[] projection = { //
						PropBankContract.PbRoleSets.ROLESETID, //
						PropBankContract.PbRoleSets.ROLESETNAME, //
						PropBankContract.PbRoleSets.ROLESETHEAD, //
						PropBankContract.PbRoleSets.ROLESETDESC, //
				};
				final String selection = PropBankContract.PbRoleSets.ROLESETID + " = ?"; //
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = null;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //
				}
				if (cursor.moveToFirst())
				{
					// column indices
					// final int idRolesetId = cursor.getColumnIndex(PbRoleSets.ROLESETID);
					final int idRolesetName = cursor.getColumnIndex(PbRoleSets.ROLESETNAME);
					final int idRolesetDesc = cursor.getColumnIndex(PropBankContract.PbRoleSets.ROLESETDESC);
					final int idRolesetHead = cursor.getColumnIndex(PropBankContract.PbRoleSets.ROLESETHEAD);

					// read cursor
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// data
					// final int roleSetId = cursor.getInt(idRolesetId);

					// roleSet
					Spanner.appendImage(sb, BasicModule.this.roleSetDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idRolesetName), 0, PropBankFactories.roleSetFactory);
					sb.append(' ');
					sb.append("head="); //
					sb.append(cursor.getString(idRolesetHead));
					sb.append('\n');

					// description
					Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
					Spanner.append(sb, cursor.getString(idRolesetDesc), 0, PropBankFactories.definitionFactory);

					// sub nodes
					final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(roleSetId, R.drawable.roles, "Roles"), BasicModule.this.getContext()); //
					final TreeNode examplesNode = TreeFactory.newQueryNode(new ExamplesQuery(roleSetId, R.drawable.sample, "Examples"), BasicModule.this.getContext()); //

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), rolesNode, examplesNode);

					// expand
					TreeView.expand(parent, false);
					TreeView.expand(rolesNode, false);
				}
				else
				{
					TreeView.disable(parent);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Role sets for word id
	 *
	 * @param wordId word id
	 * @param parent parent node
	 */
	void roleSets(final long wordId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Words_PbRoleSets.CONTENT_URI);
				final String[] projection = { //
						Words_PbRoleSets.ROLESETID, //
						Words_PbRoleSets.ROLESETNAME, //
						Words_PbRoleSets.ROLESETHEAD, //
						Words_PbRoleSets.ROLESETDESC, //
				};
				final String selection = Words_PbRoleSets.WORDID + " = ?"; //
				final String[] selectionArgs = {Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.moveToFirst())
				{
					// column indices
					final int idRoleSetId = cursor.getColumnIndex(Words_PbRoleSets.ROLESETID);
					final int idRoleSetName = cursor.getColumnIndex(Words_PbRoleSets.ROLESETNAME);
					final int idRoleSetDesc = cursor.getColumnIndex(Words_PbRoleSets.ROLESETDESC);
					final int idRoleSetHead = cursor.getColumnIndex(Words_PbRoleSets.ROLESETHEAD);

					// read cursor
					do
					{
						final SpannableStringBuilder sb = new SpannableStringBuilder();

						// data
						final int roleSetId = cursor.getInt(idRoleSetId);

						// roleSet
						Spanner.appendImage(sb, BasicModule.this.rolesDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRoleSetName), 0, PropBankFactories.roleSetFactory);
						sb.append(' ');
						sb.append("head="); //
						sb.append(cursor.getString(idRoleSetHead));
						sb.append('\n');

						// description
						Spanner.appendImage(sb, BasicModule.this.definitionDrawable);
						Spanner.append(sb, cursor.getString(idRoleSetDesc), 0, PropBankFactories.definitionFactory);

						// sub nodes
						final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(roleSetId, R.drawable.roles, "Roles"), BasicModule.this.getContext()); //
						final TreeNode examplesNode = TreeFactory.newQueryNode(new ExamplesQuery(roleSetId, R.drawable.sample, "Examples"), BasicModule.this.getContext()); //

						// attach result
						TreeFactory.addTextNode(parent, sb, getContext(), rolesNode, examplesNode);
					}
					while (cursor.moveToNext());

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					TreeView.disable(parent);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	// roles

	/**
	 * Roles in role set
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	private void roles(final int roleSetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(PbRoleSets_PbRoles.CONTENT_URI);
				final String[] projection = { //
						PbRoleSets_PbRoles.ROLEID, //
						PbRoleSets_PbRoles.ROLEDESCR, //
						PbRoleSets_PbRoles.NARG, //
						PbRoleSets_PbRoles.FUNCNAME, //
						PbRoleSets_PbRoles.THETANAME, //
				};
				final String selection = PbRoleSets_PbRoles.ROLESETID + "= ?"; //
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = null;
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();
				if (cursor.moveToFirst())
				{
					// column indices
					// final int idRoleId = cursor.getColumnIndex(PbRoleSets_PbRoles.ROLEID);
					final int idRoleDescr = cursor.getColumnIndex(PbRoleSets_PbRoles.ROLEDESCR);
					final int idFunc = cursor.getColumnIndex(PbRoleSets_PbRoles.FUNCNAME);
					final int idTheta = cursor.getColumnIndex(PbRoleSets_PbRoles.THETANAME);
					final int idNArg = cursor.getColumnIndex(PropBankContract.PbRoleSets_PbRoles.NARG);

					// read cursor
					while (true)
					{
						// data

						// n
						sb.append(cursor.getString(idNArg));
						sb.append(' ');

						// role
						Spanner.appendImage(sb, BasicModule.this.roleDrawable);
						sb.append(' ');
						Spanner.append(sb, capitalize1(cursor.getString(idRoleDescr)), 0, PropBankFactories.roleFactory);

						// theta
						String theta = cursor.getString(idTheta);
						if (theta != null && !theta.isEmpty())
						{
							sb.append(' ');
							Spanner.appendImage(sb, BasicModule.this.thetaDrawable);
							sb.append(' ');
							Spanner.append(sb, theta, 0, PropBankFactories.thetaFactory);
						}

						// func
						if (!cursor.isNull(idFunc))
						{
							sb.append(' ');
							sb.append("func="); //
							sb.append(Integer.toString(cursor.getInt(idFunc)));
						}

						// final int roleId = cursor.getInt(idRoleId);
						// sb.append(" role id=");
						// sb.append(Integer.toString(roleId));
						// sb.append(' ');

						if (!cursor.moveToNext())
						{
							//noinspection BreakStatement
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
					TreeView.disable(parent);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	// examples

	/**
	 * Examples in role set
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	private void examples(final int roleSetId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(PbRoleSets_PbExamples.CONTENT_URI);
				final String[] projection = { //
						PbRoleSets_PbExamples.TEXT, //
						PbRoleSets_PbExamples.REL, //
						"GROUP_CONCAT(" + //
								PbRoleSets_PbExamples.NARG + //
								"||'~'" + //
								"||(CASE WHEN " + PbRoleSets_PbExamples.FUNCNAME + " IS NULL THEN '*' ELSE " + PbRoleSets_PbExamples.FUNCNAME + " END)" + //
								"||'~'" + //
								"||" + PbRoleSets_PbExamples.ROLEDESCR + //
								"||'~'" + //
								"||(CASE WHEN " + PbRoleSets_PbExamples.THETANAME + " IS NULL THEN '*' ELSE " + PbRoleSets_PbExamples.THETANAME + " END)" + //
								"||'~'" + //
								"||" + PbRoleSets_PbExamples.ARG + ",'|') AS " + PbRoleSets_PbExamples.ARGS, //
						PbRoleSets_PbExamples.ASPECTNAME, //
						PbRoleSets_PbExamples.FORMNAME, //
						PbRoleSets_PbExamples.TENSENAME, //
						PbRoleSets_PbExamples.VOICENAME, //
						PbRoleSets_PbExamples.PERSONNAME, //
				};
				final String selection = PbRoleSets_PbExamples.ROLESETID + "= ?"; //
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = PbRoleSets_PbExamples.EXAMPLEID + ',' + PbRoleSets_PbExamples.NARG; //
				return new CursorLoader(getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
					// column indices
					final int idText = cursor.getColumnIndex(PbRoleSets_PbExamples.TEXT);
					final int idRel = cursor.getColumnIndex(PbRoleSets_PbExamples.REL);
					final int idArgs = cursor.getColumnIndex(PbRoleSets_PbExamples.ARGS);

					// read cursor
					while (true)
					{
						// text
						String text = cursor.getString(idText);
						Spanner.appendImage(sb, BasicModule.this.sampleDrawable);
						Spanner.append(sb, text, 0, PropBankFactories.exampleFactory);
						sb.append('\n');

						// relation
						sb.append('\t');
						Spanner.appendImage(sb, BasicModule.this.relationDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRel), 0, PropBankFactories.relationFactory);

						// args
						final String argspack = cursor.getString(idArgs);
						if (argspack != null)
						{
							final String[] args = argspack.split("\\|"); //
							Arrays.sort(args);
							for (final String arg : args)
							{
								final String[] fields = arg.split("~"); //
								if (fields.length < 5)
								{
									sb.append(arg);
									continue;
								}

								sb.append('\n');
								sb.append('\t');

								// n
								sb.append(fields[0]);
								sb.append(' ');

								// role
								Spanner.appendImage(sb, BasicModule.this.roleDrawable);
								sb.append(' ');
								Spanner.append(sb, capitalize1(fields[2]), 0, PropBankFactories.roleFactory);
								sb.append(' ');

								// theta
								Spanner.appendImage(sb, BasicModule.this.thetaDrawable);
								sb.append(' ');
								Spanner.append(sb, fields[3], 0, PropBankFactories.thetaFactory);

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
								sb.append(fields[4]);
								// Spanner.append(sb, fields[4], 0, PropBankFactories.textFactory);
							}
						}

						if (!cursor.moveToNext())
						{
							break;
						}

						sb.append('\n');
					}

					// extra format
					BasicModule.this.spanner.setSpan(sb, 0, 0);

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext());

					System.out.println("DONE");

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					TreeView.disable(parent);
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Role query
	 */
	class RolesQuery extends Query.QueryData
	{
		/**
		 * Constructor
		 *
		 * @param roleSetId role set id
		 * @param icon      icon
		 * @param text      label text
		 */
		public RolesQuery(final long roleSetId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(roleSetId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			roles((int) this.id, node);
		}
	}

	/**
	 * Examples query
	 */
	class ExamplesQuery extends Query.QueryData
	{
		/**
		 * Constructor
		 *
		 * @param roleSetId role set id
		 * @param icon      icon
		 * @param text      label text
		 */
		public ExamplesQuery(final long roleSetId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(roleSetId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			examples((int) this.id, node);
		}
	}

	// H E L P E R S

	/**
	 * Utility to capitalize first character
	 *
	 * @param s string
	 * @return string with capitalized first character
	 */
	private CharSequence capitalize1(final String s)
	{
		return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
	}
}
