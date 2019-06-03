/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.loaders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Query;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnMembers_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X;
import org.sqlunet.verbnet.provider.VerbNetProvider;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.verbnet.style.VerbNetSemanticsProcessor;
import org.sqlunet.verbnet.style.VerbNetSemanticsSpanner;
import org.sqlunet.verbnet.style.VerbNetSyntaxSpanner;
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOp.TreeOps;
import org.sqlunet.view.TreeOpExecute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWTREE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWCHILD;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWMAIN;
import static org.sqlunet.view.TreeOp.TreeOpCode.NEWEXTRA;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

/**
 * VerbNet base module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("RedundantSuppression")
abstract class BaseModule extends Module
{
	// agents

	/**
	 * Processor
	 */
	@NonNull
	private final VerbNetSemanticsProcessor semanticsProcessor;

	/**
	 * Syntax spanner
	 */
	@NonNull
	private final VerbNetSyntaxSpanner syntaxSpanner;

	/**
	 * Semantics
	 */
	@NonNull
	private final VerbNetSemanticsSpanner semanticsSpanner;

	// drawables

	/**
	 * Drawable for class
	 */
	private final Drawable drawableClass;

	/**
	 * Drawable for member
	 */
	private final Drawable drawableMember;

	/**
	 * Drawable for role sets
	 */
	final Drawable drawableRoles;

	/**
	 * Drawable for role
	 */
	private final Drawable drawableRole;

	/**
	 * Drawable for frame
	 */
	private final Drawable drawableFrame;

	/**
	 * Drawable for syntax
	 */
	private final Drawable drawableSyntax;

	/**
	 * Drawable for semantics
	 */
	private final Drawable drawableSemantics;

	/**
	 * Drawable for example
	 */
	private final Drawable drawableExample;

	/**
	 * Drawable for definition
	 */
	private final Drawable drawableDefinition;

	/**
	 * Drawable for grouping
	 */
	private final Drawable drawableGrouping;

	// View models

	private SqlunetViewTreeModel vnClassFromClassIdModel;

	private SqlunetViewTreeModel vnMembersFromClassIdModel;

	private SqlunetViewTreeModel vnRolesFromClassIdModel;

	private SqlunetViewTreeModel vnFramesFromClassIdModel;

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
		this.drawableClass = Spanner.getDrawable(context, R.drawable.roleclass);
		this.drawableMember = Spanner.getDrawable(context, R.drawable.member);
		this.drawableRoles = Spanner.getDrawable(context, R.drawable.roles);
		this.drawableRole = Spanner.getDrawable(context, R.drawable.role);
		this.drawableFrame = Spanner.getDrawable(context, R.drawable.vnframe);
		this.drawableSyntax = Spanner.getDrawable(context, R.drawable.syntax);
		this.drawableSemantics = Spanner.getDrawable(context, R.drawable.semantics);
		this.drawableExample = Spanner.getDrawable(context, R.drawable.sample);
		this.drawableDefinition = Spanner.getDrawable(context, R.drawable.definition);
		this.drawableGrouping = Spanner.getDrawable(context, R.drawable.info);

		// create processors and spanners
		this.semanticsProcessor = new VerbNetSemanticsProcessor();
		this.syntaxSpanner = new VerbNetSyntaxSpanner();
		this.semanticsSpanner = new VerbNetSemanticsSpanner();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.vnClassFromClassIdModel = ViewModelProviders.of(this.fragment).get("vn.class(classid)", SqlunetViewTreeModel.class);
		this.vnClassFromClassIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vnMembersFromClassIdModel = ViewModelProviders.of(this.fragment).get("vn.members(classid)", SqlunetViewTreeModel.class);
		this.vnMembersFromClassIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vnRolesFromClassIdModel = ViewModelProviders.of(this.fragment).get("vn.roles(classid)", SqlunetViewTreeModel.class);
		this.vnRolesFromClassIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));

		this.vnFramesFromClassIdModel = ViewModelProviders.of(this.fragment).get("vn.frames(classid)", SqlunetViewTreeModel.class);
		this.vnFramesFromClassIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
	}

	// L O A D E R S

	// vnClass

	/**
	 * VerbNet class
	 *
	 * @param classId class id
	 * @param parent  parent node
	 */
	void vnClass(final long classId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(VerbNetProvider.makeUri(VnClasses.CONTENT_URI_TABLE));
		final String[] projection = { //
				VnClasses.CLASSID, //
				VnClasses.CLASS, //
				VnClasses.CLASSTAG, //
		};
		final String selection = VnClasses.CLASSID + " = ?";
		final String[] selectionArgs = {Long.toString(classId)};
		this.vnClassFromClassIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vnClassCursorToTreeModel(cursor, classId, parent));
	}

	private TreeOp[] vnClassCursorToTreeModel(@NonNull final Cursor cursor, final long classId, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}

		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// column indices
			// final int idClassId = cursor.getColumnIndex(VnClasses_X.CLASSID);
			final int idClass = cursor.getColumnIndex(VnClasses.CLASS);
			// final int idClassTag = cursor.getColumnIndex(VnClasses.CLASSTAG);

			// data
			// final int classId = cursor.getInt(idClassId);
			final String vnClass = cursor.getString(idClass);

			// sb.append("[class]");
			Spanner.appendImage(sb, BaseModule.this.drawableClass);
			sb.append(' ');
			Spanner.append(sb, vnClass, 0, VerbNetFactories.classFactory);
			// sb.append(" tag=");
			// sb.append(cursor.getString(idClassTag));
			sb.append(" id=");
			sb.append(Long.toString(classId));

			// attach result
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);

			// sub nodes
			final TreeNode membersNode = TreeFactory.makeHotQueryNode("Members", R.drawable.members, false, new MembersQuery(classId)).addTo(parent);
			final TreeNode rolesNode = TreeFactory.makeHotQueryNode("Roles", R.drawable.roles, false, new RolesQuery(classId)).addTo(parent);
			final TreeNode framesNode = TreeFactory.makeQueryNode("Frames", R.drawable.vnframe, false, new FramesQuery(classId)).addTo(parent);

			// changed
			changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, membersNode, NEWEXTRA, rolesNode, NEWEXTRA, framesNode, NEWTREE, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}

	// vnMembers

	/**
	 * VerbNet members
	 *
	 * @param classId class id
	 * @param parent  parent node
	 */
	private void vnMembers(final int classId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(VerbNetProvider.makeUri(VnClasses_VnMembers_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				VnClasses_VnMembers_X.WORDID, //
				VnClasses_VnMembers_X.VNWORDID, //
				VnClasses_VnMembers_X.LEMMA, //
				"REPLACE(REPLACE(GROUP_CONCAT(DISTINCT REPLACE(" + VnClasses_VnMembers_X.DEFINITION + ",',','#')),',','|'),'#',',') AS " + VnClasses_VnMembers_X.DEFINITIONS, //
				"GROUP_CONCAT(DISTINCT " + VnClasses_VnMembers_X.GROUPING + ") AS " + VnClasses_VnMembers_X.GROUPINGS, //
				VnClasses_VnMembers_X.CLASSID, //
		};
		final String selection = VnClasses_VnRoles_X.CLASSID + " = ?";
		final String[] selectionArgs = {Long.toString(classId)};
		final String sortOrder = VnClasses_VnMembers_X.LEMMA;
		this.vnMembersFromClassIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> vnMembersCursorToTreeModel(cursor, parent));
	}

	@NonNull
	private TreeOp[] vnMembersCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final TreeOps changedList = new TreeOps(NEWTREE, parent);

			// column indices
			// final int idWordId = cursor.getColumnIndex(VnClasses_VnMembers_X.WORDID);
			// final int idVnWordId = cursor.getColumnIndex(VnClasses_VnMembers_X.VNWORDID);
			final int idLemma = cursor.getColumnIndex(VnClasses_VnMembers_X.LEMMA);
			final int idGroupings = cursor.getColumnIndex(VnClasses_VnMembers_X.GROUPINGS);
			final int idDefinitions = cursor.getColumnIndex(VnClasses_VnMembers_X.DEFINITIONS);

			do
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				// member
				// Spanner.appendImage(sb, BaseModule.this.drawableMember);
				// sb.append(' ');
				Spanner.append(sb, cursor.getString(idLemma), 0, VerbNetFactories.memberFactory);

				final String definitions = cursor.getString(idDefinitions);
				final String groupings = cursor.getString(idGroupings);
				if (definitions != null || groupings != null)
				{
					final TreeNode memberNode = TreeFactory.makeTreeNode(sb, R.drawable.member, false).addTo(parent);
					changedList.add(NEWCHILD, memberNode);

					final SpannableStringBuilder sb2 = new SpannableStringBuilder();

					// definitions
					boolean first = true;
					if (definitions != null)
					{
						for (String definition : definitions.split("\\|"))
						{
							if (first)
							{
								first = false;
							}
							else
							{
								sb2.append('\n');
							}

							Spanner.appendImage(sb2, BaseModule.this.drawableDefinition);
							sb2.append(' ');
							Spanner.append(sb2, definition.trim(), 0, VerbNetFactories.definitionFactory);
						}
					}

					// groupings
					first = true;
					if (groupings != null)
					{
						for (String grouping : groupings.split(","))
						{
							if (first)
							{
								if (sb2.length() > 0)
								{
									sb2.append('\n');
								}
								first = false;
							}
							else
							{
								sb2.append('\n');
							}

							Spanner.appendImage(sb2, BaseModule.this.drawableGrouping);
							sb2.append(' ');
							Spanner.append(sb2, grouping.trim(), 0, VerbNetFactories.groupingFactory);
						}
					}

					// attach definition and groupings result
					final TreeNode node = TreeFactory.makeTextNode(sb2, false).addTo(memberNode);
					changedList.add(NEWCHILD, node);
				}
				else
				{
					final TreeNode node = TreeFactory.makeLeafNode(sb, R.drawable.member, false).addTo(parent);
					changedList.add(NEWCHILD, node);
				}
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

	// vnRoles

	/**
	 * VerbNet roles
	 *
	 * @param classId class id
	 * @param parent  parent node
	 */
	private void vnRoles(final int classId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(VerbNetProvider.makeUri(VnClasses_VnRoles_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				VnClasses_VnRoles_X.ROLEID, //
				VnClasses_VnRoles_X.ROLETYPE, //
				VnClasses_VnRoles_X.RESTRS, //
				VnClasses_VnRoles_X.CLASSID, //
		};
		final String selection = VnClasses_VnRoles_X.CLASSID + " = ?";
		final String[] selectionArgs = {Long.toString(classId)};
		this.vnRolesFromClassIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vnRolesCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] vnRolesCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// column indices
			// final int idRoleId = cursor.getColumnIndex(VnClasses_VnRoles.ROLEID);
			final int idRoleType = cursor.getColumnIndex(VnClasses_VnRoles_X.ROLETYPE);
			final int idRestrs = cursor.getColumnIndex(VnClasses_VnRoles_X.RESTRS);

			// read cursor
			while (true)
			{
				// role
				Spanner.appendImage(sb, BaseModule.this.drawableRole);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idRoleType), 0, VerbNetFactories.roleFactory);

				// restr
				final CharSequence restrs = cursor.getString(idRestrs);
				if (restrs != null)
				{
					sb.append(' ');
					Spanner.append(sb, restrs, 0, VerbNetFactories.restrsFactory);
				}

				// role id
				// final int roleId = cursor.getInt(idRoleId);
				// sb.append(" role id=");
				// sb.append(Integer.toString(roleId));

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

	// vnFrames

	private void vnFrames(final int classId, @NonNull final TreeNode parent)
	{
		final Uri uri = Uri.parse(VerbNetProvider.makeUri(VnClasses_VnFrames_X.CONTENT_URI_TABLE));
		final String[] projection = { //
				VnClasses_VnFrames_X.FRAMEID, //
				VnClasses_VnFrames_X.NUMBER, //
				VnClasses_VnFrames_X.XTAG, //
				VnClasses_VnFrames_X.FRAMENAME, //
				VnClasses_VnFrames_X.FRAMESUBNAME, //
				VnClasses_VnFrames_X.SYNTAX, //
				VnClasses_VnFrames_X.SEMANTICS, //
				"GROUP_CONCAT(" + VnClasses_VnFrames_X.EXAMPLE + " , '|') AS " + VnClasses_VnFrames_X.EXAMPLES, //
				VnClasses_VnFrames_X.CLASSID, //
		};
		final String selection = VnClasses_VnFrames_X.CLASSID + " = ?";
		final String[] selectionArgs = {Long.toString(classId)};
		this.vnFramesFromClassIdModel.loadData(uri, projection, selection, selectionArgs, null, cursor -> vnFramesToView(cursor, parent));
	}

	private TreeOp[] vnFramesToView(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		TreeOp[] changed;
		if (cursor.moveToFirst())
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();

			// column indices
			// final int idFrameId = cursor.getColumnIndex(VnClasses_VnFrames.FRAMEID);
			final int idFrameName = cursor.getColumnIndex(VnClasses_VnFrames_X.FRAMENAME);
			final int idFrameSubName = cursor.getColumnIndex(VnClasses_VnFrames_X.FRAMESUBNAME);
			final int idSyntax = cursor.getColumnIndex(VnClasses_VnFrames_X.SYNTAX);
			final int idSemantics = cursor.getColumnIndex(VnClasses_VnFrames_X.SEMANTICS);
			final int idExamples = cursor.getColumnIndex(VnClasses_VnFrames_X.EXAMPLES);

			// read cursor
			while (true)
			{
				// frame
				Spanner.appendImage(sb, BaseModule.this.drawableFrame);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idFrameName), 0, VerbNetFactories.frameFactory);
				sb.append(' ');
				Spanner.append(sb, cursor.getString(idFrameSubName), 0, VerbNetFactories.framesubnameFactory);

				// frame id
				// sb.append(Integer.toString(cursor.getInt(idFrameId)));
				// sb.append('\n');

				// syntax
				final String syntax = cursor.getString(idSyntax);
				for (final String line : syntax.split("\n")) //
				{
					sb.append('\n');
					sb.append('\t');
					Spanner.appendImage(sb, BaseModule.this.drawableSyntax);
					BaseModule.this.syntaxSpanner.append(line, sb, 0);
				}

				// semantics
				final String semantics = cursor.getString(idSemantics);
				for (final String line : semantics.split("\n")) //
				{
					sb.append('\n');
					sb.append('\t');
					Spanner.appendImage(sb, BaseModule.this.drawableSemantics);
					final CharSequence statement = BaseModule.this.semanticsProcessor.process(line);
					BaseModule.this.semanticsSpanner.append(statement, sb, 0);
				}

				// examples
				final String examplesConcat = cursor.getString(idExamples);
				final String[] examples = examplesConcat.split("\\|");
				for (final String example : examples)
				{
					sb.append('\n');
					sb.append('\t');
					Spanner.appendImage(sb, BaseModule.this.drawableExample);
					Spanner.append(sb, example, 0, VerbNetFactories.exampleFactory);
				}

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

	/**
	 * Groups
	 *
	 * @param group items concat with '|'
	 * @return node
	 */
	@Nullable
	@SuppressWarnings("unused")
	protected TreeNode items(@NonNull final TreeNode parent, @Nullable final String group)
	{
		if (group != null)
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			final String[] items = group.split("\\|");
			if (items.length == 1)
			{
				Spanner.appendImage(sb, BaseModule.this.drawableMember);
				Spanner.append(sb, items[0], 0, VerbNetFactories.memberFactory);
				return TreeFactory.makeIconTextNode(sb, R.drawable.member, false).addTo(parent);
			}
			else if (items.length > 1)
			{
				final TreeNode groupingsNode = TreeFactory.makeIconTextNode("Group", R.drawable.member, false).addTo(parent);
				boolean first = true;
				for (final String item : items)
				{
					if (first)
					{
						first = false;
					}
					else
					{
						sb.append('\n');
					}
					Spanner.appendImage(sb, BaseModule.this.drawableMember);
					Spanner.append(sb, item, 0, VerbNetFactories.memberFactory);
				}
				final TreeNode childNode = TreeFactory.makeTextNode(sb, false).addTo(groupingsNode);
				return groupingsNode;
			}
		}
		return null;
	}

	/**
	 * Members query
	 */
	class MembersQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param classId class id
		 */
		MembersQuery(final long classId)
		{
			super(classId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			vnMembers((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "members for " + this.id;
		}
	}

	/**
	 * Roles query
	 */
	class RolesQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param classId class id
		 */
		RolesQuery(final long classId)
		{
			super(classId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			vnRoles((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "roles for " + this.id;
		}
	}

	/**
	 * Frames query
	 */
	class FramesQuery extends Query
	{
		/**
		 * Constructor
		 *
		 * @param classId class id
		 */
		FramesQuery(final long classId)
		{
			super(classId);
		}

		@Override
		public void process(@NonNull final TreeNode node)
		{
			vnFrames((int) this.id, node);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "vnframes for " + this.id;
		}
	}
}
