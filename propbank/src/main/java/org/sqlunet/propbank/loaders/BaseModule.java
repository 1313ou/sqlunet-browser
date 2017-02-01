package org.sqlunet.propbank.loaders;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X;
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets;
import org.sqlunet.propbank.style.PropBankFactories;
import org.sqlunet.propbank.style.PropBankSpanner;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;

import java.util.Arrays;
import java.util.Locale;

/**
 * Module for PropBank role sets
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BaseModule extends Module
{
	// resources

	/**
	 * Drawable for roleSets
	 */
	private final Drawable roleSetDrawable;

	/**
	 * Drawable for roles
	 */
	private final Drawable rolesDrawable;

	/**
	 * Drawable for relation
	 */
	private final Drawable relationDrawable;

	/**
	 * Drawable for role
	 */
	private final Drawable roleDrawable;

	/**
	 * Drawable for alias
	 */
	private final Drawable aliasDrawable;

	/**
	 * Drawable for theta role
	 */
	private final Drawable thetaDrawable;

	/**
	 * Drawable for definition
	 */
	private final Drawable definitionDrawable;

	/**
	 * Drawable for sample
	 */
	private final Drawable sampleDrawable;

	// agents

	/**
	 * Spanner
	 */
	private final PropBankSpanner spanner;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BaseModule(final Fragment fragment)
	{
		super(fragment);

		// drawables
		this.roleSetDrawable = Spanner.getDrawable(this.context, R.drawable.roleclass);
		this.rolesDrawable = Spanner.getDrawable(this.context, R.drawable.roles);
		this.relationDrawable = Spanner.getDrawable(this.context, R.drawable.relation);
		this.roleDrawable = Spanner.getDrawable(this.context, R.drawable.role);
		this.thetaDrawable = Spanner.getDrawable(this.context, R.drawable.theta);
		this.aliasDrawable = Spanner.getDrawable(this.context, R.drawable.alias);
		this.definitionDrawable = Spanner.getDrawable(this.context, R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(this.context, R.drawable.sample);

		// spanner
		this.spanner = new PropBankSpanner(this.context);
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
				final Uri uri = Uri.parse(PbRoleSets_X.CONTENT_URI);
				final String[] projection = { //
						PbRoleSets_X.ROLESETID, //
						PbRoleSets_X.ROLESETNAME, //
						PbRoleSets_X.ROLESETHEAD, //
						PbRoleSets_X.ROLESETDESC, //
						"GROUP_CONCAT(" + PbRoleSets_X.LEMMA + ") AS " + PbRoleSets_X.ALIASES};
				final String selection = PbRoleSets_X.ROLESETID + " = ?";
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = null;
				return new CursorLoader(BaseModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
				}
				if (cursor.moveToFirst())
				{
					// column indices
					// final int idRolesetId = cursor.getColumnIndex(PbRoleSets_X.ROLESETID);
					final int idRolesetName = cursor.getColumnIndex(PbRoleSets_X.ROLESETNAME);
					final int idRolesetDesc = cursor.getColumnIndex(PbRoleSets_X.ROLESETDESC);
					final int idRolesetHead = cursor.getColumnIndex(PbRoleSets_X.ROLESETHEAD);
					final int idAliases = cursor.getColumnIndex(PbRoleSets_X.ALIASES);

					// read cursor
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// data
					// final int roleSetId = cursor.getInt(idRolesetId);

					// roleSet
					Spanner.appendImage(sb, BaseModule.this.roleSetDrawable);
					sb.append(' ');
					Spanner.append(sb, cursor.getString(idRolesetName), 0, PropBankFactories.roleSetFactory);
					sb.append(' ');
					sb.append("head=");
					sb.append(cursor.getString(idRolesetHead));
					sb.append('\n');
					Spanner.appendImage(sb, BaseModule.this.aliasDrawable);
					sb.append(cursor.getString(idAliases));
					sb.append('\n');

					// description
					Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
					Spanner.append(sb, cursor.getString(idRolesetDesc), 0, PropBankFactories.definitionFactory);

					// attach result
					TreeFactory.addTextNode(parent, sb, BaseModule.this.context);

					// sub nodes
					final TreeNode rolesNode = TreeFactory.newQueryNode("Roles", R.drawable.roles, new RolesQuery(roleSetId), true, BaseModule.this.context).addTo(parent);
					final TreeNode examplesNode = TreeFactory.newQueryNode("Examples", R.drawable.sample, new ExamplesQuery(roleSetId), false, BaseModule.this.context).addTo(parent);

					// fire event
					FireEvent.onQueryReady(rolesNode);
					FireEvent.onQueryReady(examplesNode);
					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
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
				final String selection = Words_PbRoleSets.WORDID + " = ?";
				final String[] selectionArgs = {Long.toString(wordId)};
				final String sortOrder = null;
				return new CursorLoader(BaseModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
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
						Spanner.appendImage(sb, BaseModule.this.rolesDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRoleSetName), 0, PropBankFactories.roleSetFactory);
						sb.append(' ');
						sb.append("head=");
						sb.append(cursor.getString(idRoleSetHead));
						sb.append('\n');

						// description
						Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
						Spanner.append(sb, cursor.getString(idRoleSetDesc), 0, PropBankFactories.definitionFactory);

						// attach result
						TreeFactory.addTextNode(parent, sb, BaseModule.this.context);

						// sub nodes
						final TreeNode rolesNode = TreeFactory.newQueryNode("Roles", R.drawable.roles, new RolesQuery(roleSetId), true, BaseModule.this.context).addTo(parent);
						final TreeNode examplesNode = TreeFactory.newQueryNode("Examples", R.drawable.sample, new ExamplesQuery(roleSetId), false, BaseModule.this.context).addTo(parent);

						// fire event
						FireEvent.onQueryReady(rolesNode);
						FireEvent.onQueryReady(examplesNode);
					}
					while (cursor.moveToNext());

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
				final String selection = PbRoleSets_PbRoles.ROLESETID + "= ?";
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = null;
				return new CursorLoader(BaseModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
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
						Spanner.appendImage(sb, BaseModule.this.roleDrawable);
						sb.append(' ');
						Spanner.append(sb, capitalize1(cursor.getString(idRoleDescr)), 0, PropBankFactories.roleFactory);

						// theta
						String theta = cursor.getString(idTheta);
						if (theta != null && !theta.isEmpty())
						{
							sb.append(' ');
							Spanner.appendImage(sb, BaseModule.this.thetaDrawable);
							sb.append(' ');
							Spanner.append(sb, theta, 0, PropBankFactories.thetaFactory);
						}

						// func
						if (!cursor.isNull(idFunc))
						{
							sb.append(' ');
							sb.append("func=");
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
					TreeFactory.addTextNode(parent, sb, BaseModule.this.context);

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
				final String selection = PbRoleSets_PbExamples.ROLESETID + "= ?";
				final String[] selectionArgs = {Long.toString(roleSetId)};
				final String sortOrder = PbRoleSets_PbExamples.EXAMPLEID + ',' + PbRoleSets_PbExamples.NARG;
				return new CursorLoader(BaseModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@SuppressWarnings("BreakStatement")
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
						Spanner.appendImage(sb, BaseModule.this.sampleDrawable);
						Spanner.append(sb, text, 0, PropBankFactories.exampleFactory);
						sb.append('\n');

						// relation
						sb.append('\t');
						Spanner.appendImage(sb, BaseModule.this.relationDrawable);
						sb.append(' ');
						Spanner.append(sb, cursor.getString(idRel), 0, PropBankFactories.relationFactory);

						// args
						final String argspack = cursor.getString(idArgs);
						if (argspack != null)
						{
							final String[] args = argspack.split("\\|");
							Arrays.sort(args);
							for (final String arg : args)
							{
								final String[] fields = arg.split("~");
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
								Spanner.appendImage(sb, BaseModule.this.roleDrawable);
								sb.append(' ');
								Spanner.append(sb, capitalize1(fields[2]), 0, PropBankFactories.roleFactory);
								sb.append(' ');

								// theta
								Spanner.appendImage(sb, BaseModule.this.thetaDrawable);
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
					BaseModule.this.spanner.setSpan(sb, 0, 0);

					// attach result
					TreeFactory.addTextNode(parent, sb, BaseModule.this.context);

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
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Role query
	 */
	private class RolesQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param roleSetId role set id
		 */
		RolesQuery(final long roleSetId)
		{
			super(roleSetId);
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
	private class ExamplesQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param roleSetId role set id
		 */
		ExamplesQuery(final long roleSetId)
		{
			super(roleSetId);
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
