/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.loaders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles;
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X;
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets;
import org.sqlunet.propbank.provider.PropBankProvider;
import org.sqlunet.propbank.style.PropBankFactories;
import org.sqlunet.propbank.style.PropBankSpanner;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import java.util.Arrays;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWEXTRA;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

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
	@NonNull
	private final PropBankSpanner spanner;

	// View models

	private SqlunetViewTreeModel pbRoleSetFromRoleSetIdModel;

	private SqlunetViewTreeModel roleSetsFromWordIdModel;

	private SqlunetViewTreeModel rolesFromRoleSetIdModel;

	private SqlunetViewTreeModel examplesFromRoleSetIdModel;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BaseModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();

		// drawables
		final Context context = BaseModule.this.fragment.requireContext();
		this.roleSetDrawable = Spanner.getDrawable(context, R.drawable.roleclass);
		this.rolesDrawable = Spanner.getDrawable(context, R.drawable.roles);
		this.relationDrawable = Spanner.getDrawable(context, R.drawable.relation);
		this.roleDrawable = Spanner.getDrawable(context, R.drawable.role);
		this.thetaDrawable = Spanner.getDrawable(context, R.drawable.theta);
		this.aliasDrawable = Spanner.getDrawable(context, R.drawable.alias);
		this.definitionDrawable = Spanner.getDrawable(context, R.drawable.definition);
		this.sampleDrawable = Spanner.getDrawable(context, R.drawable.sample);

		// spanner
		this.spanner = new PropBankSpanner(context);
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.pbRoleSetFromRoleSetIdModel = ViewModelProviders.of(this.fragment).get("pb.roleset(rolesetid)", SqlunetViewTreeModel.class);
		this.pbRoleSetFromRoleSetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.roleSetsFromWordIdModel = ViewModelProviders.of(this.fragment).get("pb.rolesets(wordid)", SqlunetViewTreeModel.class);
		this.roleSetsFromWordIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.rolesFromRoleSetIdModel = ViewModelProviders.of(this.fragment).get("pb.roles(rolesetid)", SqlunetViewTreeModel.class);
		this.rolesFromRoleSetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.examplesFromRoleSetIdModel = ViewModelProviders.of(this.fragment).get("pb.examples(rolesetid)", SqlunetViewTreeModel.class);
		this.examplesFromRoleSetIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	// R O L E   S E T S

	/**
	 * Role set from id
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	void roleSet(final long roleSetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(PropBankProvider.makeUri(PbRoleSets_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				PbRoleSets_X.ROLESETID, //
				PbRoleSets_X.ROLESETNAME, //
				PbRoleSets_X.ROLESETHEAD, //
				PbRoleSets_X.ROLESETDESC, //
				"GROUP_CONCAT(" + PbRoleSets_X.LEMMA + ") AS " + PbRoleSets_X.ALIASES};
		final String selection = PbRoleSets_X.ROLESETID + " = ?";
		final String[] selectionArgs = {Long.toString(roleSetId)};
		this.pbRoleSetFromRoleSetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> roleSetCursorToTreeModel(cursor, roleSetId, parent));
	}

	private TreeOp[] roleSetCursorToTreeModel(@NonNull final Cursor cursor, final long roleSetId, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			// column indices
			// final int idRoleSetId = cursor.getColumnIndex(PbRoleSets_X.ROLESETID);
			final int idRoleSetName = cursor.getColumnIndex(PbRoleSets_X.ROLESETNAME);
			final int idRoleSetDesc = cursor.getColumnIndex(PbRoleSets_X.ROLESETDESC);
			final int idRoleSetHead = cursor.getColumnIndex(PbRoleSets_X.ROLESETHEAD);
			final int idAliases = cursor.getColumnIndex(PbRoleSets_X.ALIASES);

			// read cursor
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// data
			// final int roleSetId = cursor.getInt(idRoleSetId);

			// roleSet
			Spanner.appendImage(sb, BaseModule.this.roleSetDrawable);
			sb.append(' ');
			Spanner.append(sb, cursor.getString(idRoleSetName), 0, PropBankFactories.roleSetFactory);
			sb.append(' ');
			sb.append("head=");
			sb.append(cursor.getString(idRoleSetHead));
			sb.append('\n');
			Spanner.appendImage(sb, BaseModule.this.aliasDrawable);
			sb.append(cursor.getString(idAliases));
			sb.append('\n');

			// description
			Spanner.appendImage(sb, BaseModule.this.definitionDrawable);
			Spanner.append(sb, cursor.getString(idRoleSetDesc), 0, PropBankFactories.definitionFactory);

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);

			// sub nodes
			final TreeNode rolesNode = TreeFactory.makeHotQueryNode("Roles", R.drawable.roles, false, new RolesQuery(roleSetId)).addTo(parent);
			final TreeNode examplesNode = TreeFactory.makeQueryNode("Examples", R.drawable.sample, false, new ExamplesQuery(roleSetId)).addTo(parent);

			changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, rolesNode, NEWEXTRA, examplesNode, NEWTREE, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	/**
	 * Role sets for word id
	 *
	 * @param wordId word id
	 * @param parent parent node
	 */
	void roleSets(final long wordId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(PropBankProvider.makeUri(Words_PbRoleSets.CONTENT_URI_TABLE));
		final String[] projection = { //
				Words_PbRoleSets.ROLESETID, //
				Words_PbRoleSets.ROLESETNAME, //
				Words_PbRoleSets.ROLESETHEAD, //
				Words_PbRoleSets.ROLESETDESC, //
		};
		final String selection = Words_PbRoleSets.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.roleSetsFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> roleSetsCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] roleSetsCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOp.TreeOps changedList = new TreeOps(NEWTREE, parent);

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
				final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);
				changedList.add(NEWCHILD, node);

				// sub nodes
				final TreeNode rolesNode = TreeFactory.makeHotQueryNode("Roles", R.drawable.roles, false, new RolesQuery(roleSetId)).addTo(parent);
				changedList.add(NEWCHILD, rolesNode);
				final TreeNode examplesNode = TreeFactory.makeQueryNode("Examples", R.drawable.sample, false, new ExamplesQuery(roleSetId)).addTo(parent);
				changedList.add(NEWCHILD, examplesNode);
			}
			while (cursor.moveToNext());
			changed = changedList.toArray();
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// R O L E S

	/**
	 * Roles in role set
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	private void roles(final int roleSetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(PropBankProvider.makeUri(PbRoleSets_PbRoles.CONTENT_URI_TABLE));
		final String[] projection = { //
				PbRoleSets_PbRoles.ROLEID, //
				PbRoleSets_PbRoles.ROLEDESCR, //
				PbRoleSets_PbRoles.NARG, //
				PbRoleSets_PbRoles.FUNCNAME, //
				PbRoleSets_PbRoles.THETANAME, //
		};
		final String selection = PbRoleSets_PbRoles.ROLESETID + "= ?";
		final String[] selectionArgs = {Long.toString(roleSetId)};
		this.rolesFromRoleSetIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> rolesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] rolesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
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

	// E X A M P L E S

	/**
	 * Examples in role set
	 *
	 * @param roleSetId role set id
	 * @param parent    parent node
	 */
	private void examples(final int roleSetId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(PropBankProvider.makeUri(PbRoleSets_PbExamples.CONTENT_URI_TABLE));
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
		this.examplesFromRoleSetIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> examplesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] examplesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
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
				final String argsPack = cursor.getString(idArgs);
				if (argsPack != null)
				{
					final String[] args = argsPack.split("\\|");
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

	// Q U E R I E S

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
		public void process(@NonNull final TreeNode node)
		{
			roles((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "roles for roleset " + this.id;
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
		public void process(@NonNull final TreeNode node)
		{
			examples((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "examples for roleset " + this.id;
		}
	}

	// H E L P E R S

	/**
	 * Utility to capitalize first character
	 *
	 * @param s string
	 * @return string with capitalized first character
	 */
	private CharSequence capitalize1(@NonNull final String s)
	{
		return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
	}
}
