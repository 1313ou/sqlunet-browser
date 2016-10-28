package org.sqlunet.verbnet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.QueryHolder;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_X;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.verbnet.style.VerbNetSemanticsProcessor;
import org.sqlunet.verbnet.style.VerbNetSemanticsSpanner;
import org.sqlunet.verbnet.style.VerbNetSyntaxSpanner;
import org.sqlunet.view.TreeFactory;

/**
 * VerbNet basic module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("RedundantSuppression")
abstract class BasicModule extends Module
{
	// agents

	/**
	 * Processor
	 */
	private VerbNetSemanticsProcessor semanticsProcessor;

	/**
	 * Syntax spanner
	 */
	private VerbNetSyntaxSpanner syntaxSpanner;

	/**
	 * Semantics
	 */
	private VerbNetSemanticsSpanner semanticsSpanner;

	// drawables

	/**
	 * Drawable for role sets
	 */
	@SuppressWarnings("WeakerAccess")
	protected Drawable drawableRoles;

	/**
	 * Drawable for class
	 */
	private Drawable drawableClass;

	/**
	 * Drawable for (group) item
	 */
	private Drawable drawableItem;

	/**
	 * Drawable for role
	 */
	private Drawable drawableRole;

	/**
	 * Drawable for frame
	 */
	private Drawable drawableFrame;

	/**
	 * Drawable for syntax
	 */
	private Drawable drawableSyntax;

	/**
	 * Drawable for semantics
	 */
	private Drawable drawableSemantics;

	/**
	 * Drawable for example
	 */
	private Drawable drawableExample;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	BasicModule(final Fragment fragment)
	{
		super(fragment);

		// context
		final Context context = getContext();

		// drawable
		this.drawableClass = Spanner.getDrawable(context, R.drawable.vnclass);
		this.drawableItem = Spanner.getDrawable(context, R.drawable.groupitem);
		this.drawableRoles = Spanner.getDrawable(context, R.drawable.roles);
		this.drawableRole = Spanner.getDrawable(context, R.drawable.role);
		this.drawableFrame = Spanner.getDrawable(context, R.drawable.vnframe);
		this.drawableSyntax = Spanner.getDrawable(context, R.drawable.syntax);
		this.drawableSemantics = Spanner.getDrawable(context, R.drawable.semantics);
		this.drawableExample = Spanner.getDrawable(context, R.drawable.sample);

		// create processors and spanners
		this.semanticsProcessor = new VerbNetSemanticsProcessor();
		this.syntaxSpanner = new VerbNetSyntaxSpanner();
		this.semanticsSpanner = new VerbNetSemanticsSpanner();
	}

	// L O A D E R S

	// vnClass

	/**
	 * VerbNet class
	 *
	 * @param classId class id
	 * @param parent  parent node
	 */
	void vnClass(final long classId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VnClasses_X.CONTENT_URI);
				final String[] projection = { //
						VnClasses_X.CLASSID, //
						VnClasses_X.CLASS, //
						VnClasses_X.CLASSTAG, //
						"GROUP_CONCAT(" + VnClasses_X.GROUPING + ", '|') AS " + VnClasses_X.GROUPINGS, //
				};
				final String selection = VnClasses_X.CLASSID + " = ?"; //
				final String[] selectionArgs = { //
						Long.toString(classId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
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
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					// final int idClassId = cursor.getColumnIndex(VnClasses_X.CLASSID);
					final int idClass = cursor.getColumnIndex(VnClasses_X.CLASS);
					final int idGroupings = cursor.getColumnIndex(VnClasses_X.GROUPINGS);
					// final int idClassTag = cursor.getColumnIndex(VnClasses.CLASSTAG);

					// read cursor

					// data
					// final int classId = cursor.getInt(idClassId);
					final String vnClass = cursor.getString(idClass);
					final String groupings = cursor.getString(idGroupings);

					// sb.append("[class]");
					Spanner.appendImage(sb, BasicModule.this.drawableClass);
					sb.append(' ');
					Spanner.append(sb, vnClass, 0, VerbNetFactories.classFactory);
					// sb.append(" tag=");
					// sb.append(cursor.getString(idClassTag));
					sb.append(" id="); //
					sb.append(Long.toString(classId));

					// groupings
					final TreeNode itemsNode = groupings(groupings);

					// sub nodes
					final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(classId, R.drawable.role, "Roles"), BasicModule.this.getContext()); //
					final TreeNode framesNode = TreeFactory.newQueryNode(new FramesQuery(classId, R.drawable.vnframe, "Frames"), //
							BasicModule.this.getContext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), itemsNode, rolesNode, framesNode);

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

	// vnRoles

	/**
	 * VerbNet roles
	 *
	 * @param classId class id
	 * @param parent  parent node
	 */
	private void vnRoles(final int classId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VnClasses_VnRoles_X.CONTENT_URI);
				final String[] projection = { //
						VnClasses_VnRoles_X.ROLEID, //
						VnClasses_VnRoles_X.ROLETYPE, //
						VnClasses_VnRoles_X.RESTRS, //
						VnClasses_VnRoles_X.CLASSID, //
				};
				final String selection = VnClasses_VnRoles_X.CLASSID + " = ?"; //
				final String[] selectionArgs = {Long.toString(classId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
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
						Spanner.appendImage(sb, BasicModule.this.drawableRole);
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

	// vnFrames

	private void vnFrames(final int classId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(VnClasses_VnFrames_X.CONTENT_URI);
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
				final String selection = VnClasses_VnFrames_X.CLASSID + " = ?"; //
				final String[] selectionArgs = {Long.toString(classId)};
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				final SpannableStringBuilder sb = new SpannableStringBuilder();

				if (cursor.moveToFirst())
				{
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
						Spanner.appendImage(sb, BasicModule.this.drawableFrame);
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
							Spanner.appendImage(sb, BasicModule.this.drawableSyntax);
							BasicModule.this.syntaxSpanner.append(line, sb, 0);
						}

						// semantics
						final String semantics = cursor.getString(idSemantics);
						for (final String line : semantics.split("\n")) //
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.drawableSemantics);
							final CharSequence statement = BasicModule.this.semanticsProcessor.process(line);
							BasicModule.this.semanticsSpanner.append(statement, sb, 0);
						}

						// examples
						final String examplesConcat = cursor.getString(idExamples);
						final String[] examples = examplesConcat.split("\\|"); //
						for (final String example : examples)
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.drawableExample);
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

	/**
	 * Groupings
	 *
	 * @param itemGroup item groups
	 * @return node
	 */
	@SuppressWarnings("WeakerAccess")
	protected TreeNode groupings(final String itemGroup)
	{
		if (itemGroup != null)
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			final String[] items = itemGroup.split("\\|"); //
			if (items.length == 1)
			{
				Spanner.appendImage(sb, BasicModule.this.drawableItem);
				Spanner.append(sb, items[0], 0, VerbNetFactories.itemFactory);
			}
			else if (items.length > 1)
			{
				final TreeNode groupingsNode = TreeFactory.newTreeNode("Groupings", R.drawable.groupitem, this.getContext()); //
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
					Spanner.appendImage(sb, BasicModule.this.drawableItem);
					Spanner.append(sb, item, 0, VerbNetFactories.itemFactory);
				}
				groupingsNode.addChild(TreeFactory.newTextNode(sb, this.getContext()));
				return groupingsNode;
			}
		}
		return null;
	}

	/**
	 * Roles query
	 */
	class RolesQuery extends QueryHolder.Query
	{
		public RolesQuery(final long classId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(classId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			vnRoles((int) this.id, node);
		}
	}

	/**
	 * Frames query
	 */
	class FramesQuery extends QueryHolder.Query
	{
		public FramesQuery(final long classId, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(classId, icon, text);
		}

		@Override
		public void process(final TreeNode node)
		{
			vnFrames((int) this.id, node);
		}
	}
}
