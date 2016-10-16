package org.sqlunet.verbnet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.renderer.QueryHolder;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.TreeFactory;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.R;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X;
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_X;
import org.sqlunet.verbnet.style.VerbNetFactories;
import org.sqlunet.verbnet.style.VerbNetSemanticsProcessor;
import org.sqlunet.verbnet.style.VerbNetSemanticsSpanner;
import org.sqlunet.verbnet.style.VerbNetSyntaxSpanner;

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

abstract class BasicModule extends Module
{
	/**
	 * Processor
	 */
	private VerbNetSemanticsProcessor semanticsProcessor;

	// spanners

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
	 * Drawable for class
	 */
	private Drawable drawableClass;

	/**
	 * Drawable for (group) item
	 */
	private Drawable drawableItem;

	/**
	 * Drawable for role sets
	 */
	Drawable drawableRoles;

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
	 * @param fragment0
	 *            host fragment
	 */
	BasicModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/**
	 * Unmarshal data from parcelable
	 *
	 * @param arguments
	 *            parcelable
	 */
	abstract void unmarshall(final Parcelable arguments);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.Module#init(android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable arguments)
	{
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

		// get query
		unmarshall(arguments);
	}

	// L O A D E R S

	// vnclasses

	void vnclasses(final long classid0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(VnClasses_X.CONTENT_URI);
				final String[] projection = new String[] { //
						VnClasses_X.CLASSID, //
						VnClasses_X.CLASS, //
						VnClasses_X.CLASSTAG, //
						"GROUP_CONCAT(" + VnClasses_X.GROUPING + ", '|') AS " + VnClasses_X.GROUPINGS, // //$NON-NLS-1$ //$NON-NLS-2$
				};
				final String selection = VnClasses_X.CLASSID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[] { //
						Long.toString(classid0) };
				final String sortOrder = null;
				return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					// column indices
					final int idClassId = cursor.getColumnIndex(VnClasses_X.CLASSID);
					final int idClass = cursor.getColumnIndex(VnClasses_X.CLASS);
					final int idGroupings = cursor.getColumnIndex(VnClasses_X.GROUPINGS);
					// final int idClassTag = cursor.getColumnIndex(VnClasses.CLASSTAG);

					// read cursor

					// data
					final int classid1 = cursor.getInt(idClassId);
					final String vnclass = cursor.getString(idClass);
					final String groupings = cursor.getString(idGroupings);

					// sb.append("[class]");
					Spanner.appendImage(sb, BasicModule.this.drawableClass);
					sb.append(' ');
					Spanner.append(sb, vnclass, 0, VerbNetFactories.classFactory);
					// sb.append(" tag=");
					// sb.append(cursor.getString(idClassTag));
					sb.append(" id="); //$NON-NLS-1$
					sb.append(Integer.toString(classid1));

					// groupings
					final TreeNode itemsNode = groupings(groupings);

					// sub nodes
					final TreeNode rolesNode = TreeFactory.newQueryNode(new RolesQuery(classid1, R.drawable.role, "Roles"), BasicModule.this.getContext()); //$NON-NLS-1$
					final TreeNode framesNode = TreeFactory.newQueryNode(new FramesQuery(classid1, R.drawable.vnframe, "Frames"), //$NON-NLS-1$
							BasicModule.this.getContext());

					// attach result
					TreeFactory.addTextNode(parent, sb, BasicModule.this.getContext(), itemsNode, rolesNode, framesNode);

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

	// vnroles

	private void vnroles(final int classid, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(VnClasses_VnRoles_X.CONTENT_URI);
				final String[] projection = new String[] { //
						VnClasses_VnRoles_X.ROLEID, //
						VnClasses_VnRoles_X.ROLETYPE, //
						VnClasses_VnRoles_X.RESTRS, //
						VnClasses_VnRoles_X.CLASSID, //
				};
				final String selection = VnClasses_VnRoles_X.CLASSID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[] { Long.toString(classid) };
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

						// roleid
						// final int roleid = cursor.getInt(idRoleId);
						// sb.append(" roleid=");
						// sb.append(Integer.toString(roleid));

						if (!cursor.moveToNext())
							//noinspection BreakStatement
							break;

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

	// vnframes

	private void vnframes(final int classid, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(VnClasses_VnFrames_X.CONTENT_URI);
				final String[] projection = new String[] { //
						VnClasses_VnFrames_X.FRAMEID, //
						VnClasses_VnFrames_X.NUMBER, //
						VnClasses_VnFrames_X.XTAG, //
						VnClasses_VnFrames_X.FRAMENAME, //
						VnClasses_VnFrames_X.FRAMESUBNAME, //
						VnClasses_VnFrames_X.SYNTAX, //
						VnClasses_VnFrames_X.SEMANTICS, //
						"GROUP_CONCAT(" + VnClasses_VnFrames_X.EXAMPLE + " , '|') AS " + VnClasses_VnFrames_X.EXAMPLES, // //$NON-NLS-1$ //$NON-NLS-2$
						VnClasses_VnFrames_X.CLASSID, //
				};
				final String selection = VnClasses_VnFrames_X.CLASSID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[] { Long.toString(classid) };
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

						// frameid
						// sb.append(Integer.toString(cursor.getInt(idFrameId)));
						// sb.append('\n');

						// syntax
						final String syntax = cursor.getString(idSyntax);
						for (final String line : syntax.split("\n")) //$NON-NLS-1$
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.drawableSyntax);
							BasicModule.this.syntaxSpanner.append(line, sb, 0);
						}

						// semantics
						final String semantics = cursor.getString(idSemantics);
						for (final String line : semantics.split("\n")) //$NON-NLS-1$
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.drawableSemantics);
							final CharSequence statement = BasicModule.this.semanticsProcessor.process(line);
							BasicModule.this.semanticsSpanner.append(statement, sb, 0);
						}

						// examples
						final String examplesConcat = cursor.getString(idExamples);
						final String[] examples = examplesConcat.split("\\|"); //$NON-NLS-1$
						for (final String example : examples)
						{
							sb.append('\n');
							sb.append('\t');
							Spanner.appendImage(sb, BasicModule.this.drawableExample);
							Spanner.append(sb, example, 0, VerbNetFactories.exampleFactory);
						}

						if (!cursor.moveToNext())
							//noinspection BreakStatement
							break;
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

	TreeNode groupings(final String itemGroup)
	{
		if (itemGroup != null)
		{
			final SpannableStringBuilder sb = new SpannableStringBuilder();
			final String[] items = itemGroup.split("\\|"); //$NON-NLS-1$
			if (items.length == 1)
			{
				Spanner.appendImage(sb, BasicModule.this.drawableItem);
				Spanner.append(sb, items[0], 0, VerbNetFactories.itemFactory);
			}
			else if (items.length > 1)
			{
				final TreeNode groupingsNode = TreeFactory.newTreeNode("Groupings", R.drawable.groupitem, this.getContext()); //$NON-NLS-1$
				boolean first = true;
				for (final String item : items)
				{
					if (first)
						first = false;
					else
						sb.append('\n');
					Spanner.appendImage(sb, BasicModule.this.drawableItem);
					Spanner.append(sb, item, 0, VerbNetFactories.itemFactory);
				}
				groupingsNode.addChild(TreeFactory.newTextNode(sb, this.getContext()));
				return groupingsNode;
			}
		}
		return null;
	}

	class RolesQuery extends QueryHolder.Query
	{
		public RolesQuery(final long classid0, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(classid0, icon, text);
		}

		@Override
		public void process(final TreeNode node0)
		{
			vnroles((int) this.id, node0);
		}
	}

	class FramesQuery extends QueryHolder.Query
	{
		public FramesQuery(final long classid0, final int icon, @SuppressWarnings("SameParameterValue") final CharSequence text)
		{
			super(classid0, icon, text);
		}

		@Override
		public void process(final TreeNode node0)
		{
			vnframes((int) this.id, node0);
		}
	}
}
