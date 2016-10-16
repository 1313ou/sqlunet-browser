package org.sqlunet.predicatematrix.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.SparseArray;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.browser.FnFrameActivity;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.Pm_X;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.PredicateMatrix;
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.LinkHolder;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.view.TreeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Module for predicate matrix
 *
 * @author Bernard Bou
 */
abstract class BasicModule extends Module
{
	// resources

	private static final String LOG = "PredicateMatrix"; //$NON-NLS-1$

	/**
	 * Drawable for roles
	 */
	private Drawable classDrawable;

	/**
	 * Drawable for role
	 */
	private Drawable roleDrawable;

	// spanner

	/**
	 * Constructor
	 */
	BasicModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	private static String capitalize1(final String s)
	{
		return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
	}

	/**
	 * Unmarshall query arguments
	 *
	 * @param query parceled query
	 */
	abstract void unmarshall(final Parcelable query);

	// D A T A

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.Module#init(android.widget.TextView, android.widget.TextView, android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query)
	{
		// spanner
		this.classDrawable = Spanner.getDrawable(getContext(), R.drawable.roles);
		this.roleDrawable = Spanner.getDrawable(getContext(), R.drawable.role);

		// get query
		unmarshall(query);
	}

	void fromRoleId(final long pmroleid, final TreeNode parent, final Displayer displayer)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new PmProcessOnIteration(parent, displayer)
		{
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.PMROLEID + "= ?"; //$NON-NLS-1$
			}

			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{Long.toString(pmroleid)};
			}
		});
	}

	void fromWord(final String word, final TreeNode parent, final Displayer displayer)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new PmProcessOnIteration(parent, displayer)
		{
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.WORD + "= ?"; //$NON-NLS-1$
			}

			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{word};
			}
		});
	}

	void fromWordGrouped(final String word, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new PmProcessGrouped(parent, new DisplayerByPmRole())
		{
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.WORD + "= ?"; //$NON-NLS-1$
			}

			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{word};
			}
		});
	}

	static class PmRole implements Comparable<PmRole>
	{
		final long pmpredid;

		final long pmroleid;

		final String pmpredicate;

		final String pmrole;

		final String pmpos;

		public PmRole(final long pmpredid, final long pmroleid, final String pmpredicate, final String pmrole, final String pmpos)
		{
			this.pmpredid = pmpredid;
			this.pmroleid = pmroleid;
			this.pmpredicate = pmpredicate;
			this.pmrole = pmrole;
			this.pmpos = pmpos;
		}

		@Override
		public int compareTo(final PmRole another)
		{
			if (pmpos.charAt(0) != another.pmpos.charAt(0))
				return pmpos.charAt(0) > another.pmpos.charAt(0) ? 1 : -1;
			if (pmpredid != another.pmpredid)
				return pmpredid > another.pmpredid ? 1 : -1;
			if (pmroleid != another.pmroleid)
				return pmroleid > another.pmroleid ? 1 : -1;
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof PmRole))
				return false;
			final PmRole pmdata2 = (PmRole) another;
			return this.pmpos.charAt(0) == pmdata2.pmpos.charAt(0) && this.pmroleid == pmdata2.pmroleid && this.pmpredid == pmdata2.pmpredid;
		}

		@Override
		public int hashCode()
		{
			return (int) (17 * (int) this.pmpos.charAt(0) + 19 * this.pmroleid + 3 * this.pmpredid);
		}

		public String toRoleData()
		{
			return this.pmpos + '-' + Long.toString(this.pmroleid) + '-' + Long.toString(this.pmpredid);
		}

		public String toData()
		{
			return toRoleData();
		}

		public String toRoleString()
		{
			return (this.pmpos == null ? "null" : this.pmpos) + //$NON-NLS-1$
					'-' + // $NON-NLS-1$
					this.pmpredicate +
					'-' + // $NON-NLS-1$
					(this.pmrole == null ? "null" : this.pmrole) //$NON-NLS-1$
					;
		}

		@Override
		public String toString()
		{
			return toRoleString();
		}
	}

	static class PmRow extends PmRole
	{
		public final long pmid;

		public PmRow(long pmid, long pmpredid, long pmroleid, String pmpredicate, String pmrole, String pmpos)
		{
			super(pmpredid, pmroleid, pmpredicate, pmrole, pmpos);
			this.pmid = pmid;
		}

		public String toData()
		{
			return '[' + Long.toString(this.pmid) + ']' + '-' + super.toData();
		}

		@Override
		public String toString()
		{
			return '[' + Long.toString(this.pmid) + ']' + '-' + super.toString();
		}
	}

	// L O A D E R S

	static class WnData implements Comparable<WnData>
	{
		final long synsetid;

		final String definition;

		public WnData(final long synsetid, final String definition)
		{
			this.synsetid = synsetid;
			this.definition = definition;
		}

		@Override
		public int compareTo(final WnData another)
		{
			if (synsetid != another.synsetid)
				return synsetid > another.synsetid ? 1 : -1;
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof WnData))
				return false;
			final WnData wndata2 = (WnData) another;
			return this.synsetid == wndata2.synsetid;
		}

		@Override
		public int hashCode()
		{
			return (int) this.synsetid;
		}

		@Override
		public String toString()
		{
			return this.definition;
		}
	}

	// P R O C E S S O R S

	static class VnData implements Comparable<VnData>
	{
		final long vnclassid;

		final long vnroleid;

		final String vnclass;

		final String vnrole;

		public VnData(final long vnclassid, final long vnroleid, final String vnclass, final String vnrole)
		{
			this.vnclassid = vnclassid;
			this.vnroleid = vnroleid;
			this.vnclass = vnclass;
			this.vnrole = vnrole;
		}

		@Override
		public int compareTo(final VnData another)
		{
			if (vnclassid != another.vnclassid)
				return vnclassid > another.vnclassid ? 1 : -1;
			if (vnroleid != another.vnroleid)
				return vnroleid > another.vnroleid ? 1 : -1;
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof VnData))
				return false;
			final VnData vndata2 = (VnData) another;
			return this.vnclassid == vndata2.vnclassid && this.vnroleid == vndata2.vnroleid;
		}

		@Override
		public int hashCode()
		{
			return (int) (19 * this.vnclassid + 13 * this.vnroleid);
		}

		public String toData()
		{
			return Long.toString(this.vnclassid) + '-' + Long.toString(this.vnroleid);
		}

		@Override
		public String toString()
		{
			return this.vnclass + '-' + this.vnrole;
		}
	}

	static class PbData implements Comparable<PbData>
	{
		final long pbrolesetid;

		final long pbroleid;

		final String pbroleset;

		final String pbrolesetdescr;

		final String pbrole;

		final String pbroledescr;

		public PbData(final long pbrolesetid, final long pbroleid, final String pbroleset, final String pbrolesetdescr, final String pbrole, final String pbroledescr)
		{
			this.pbrolesetid = pbrolesetid;
			this.pbroleid = pbroleid;
			this.pbroleset = pbroleset;
			this.pbrolesetdescr = pbrolesetdescr;
			this.pbrole = pbrole;
			this.pbroledescr = pbroledescr;
		}

		@Override
		public int compareTo(final PbData another)
		{
			if (pbrolesetid != another.pbrolesetid)
				return pbrolesetid > another.pbrolesetid ? 1 : -1;
			if (pbroleid != another.pbroleid)
				return pbroleid > another.pbroleid ? 1 : -1;
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof PbData))
				return false;
			final PbData pbdata2 = (PbData) another;
			return this.pbrolesetid == pbdata2.pbrolesetid && this.pbroleid == pbdata2.pbroleid;
		}

		@Override
		public int hashCode()
		{
			return (int) (23 * this.pbrolesetid + 51 * this.pbroleid);
		}

		public String toData()
		{
			return Long.toString(this.pbrolesetid) + '-' + Long.toString(this.pbroleid);
		}

		public String toString()
		{
			return this.pbroleset + '-' + this.pbrole;
		}
	}

	// D I S P L A Y E R S

	class FnData implements Comparable<FnData>
	{
		final long fnframeid;

		final long fnfeid;

		final String fnframe;

		final String fnfe;

		public FnData(final long fnframeid, final long fnfeid, final String fnframe, final String fnfe)
		{
			this.fnframeid = fnframeid;
			this.fnfeid = fnfeid;
			this.fnframe = fnframe;
			this.fnfe = fnfe;
		}

		@Override
		public int compareTo(final FnData another)
		{
			if (fnframeid != another.fnframeid)
				return fnframeid > another.fnframeid ? 1 : -1;
			if (fnfeid != another.fnfeid)
				return fnfeid > another.fnfeid ? 1 : -1;
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof FnData))
				return false;
			final FnData fndata2 = (FnData) another;
			return this.fnframeid == fndata2.fnframeid && this.fnfeid == fndata2.fnfeid;
		}

		@Override
		public int hashCode()
		{
			return (int) (17 * this.fnframeid + 7 * this.fnfeid);
		}

		public String toData()
		{
			return Long.toString(this.fnframeid) + '-' + Long.toString(this.fnfeid);
		}

		@Override
		public String toString()
		{
			return this.fnframe + '-' + this.fnfe;
		}
	}

	abstract class PmCallbacks implements LoaderCallbacks<Cursor>
	{
		final Displayer displayer;

		final TreeNode parent;

		public PmCallbacks(final TreeNode parent0, final Displayer displayer)
		{
			this.parent = parent0;
			this.displayer = displayer;
		}

		abstract protected String getSelection();

		abstract protected String[] getSelectionArgs();

		@Override
		public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
		{
			final Uri uri = Uri.parse(Pm_X.CONTENT_URI);
			final String[] projection = new String[]{ //
					PredicateMatrix.PMID, //
					PredicateMatrix.PMROLEID, //
					PredicateMatrix.PMPREDID, //
					PredicateMatrix.PMPREDICATE, //
					PredicateMatrix.PMROLE, //
					"mr." + PredicateMatrix.PMPOS, // //$NON-NLS-1$

					PredicateMatrix.WORD, //
					PredicateMatrix.SYNSETID, //
					Pm_X.DEFINITION, //

					PredicateMatrix.VNCLASSID, //
					Pm_X.VNCLASS, //
					"vt." + Pm_X.VNROLETYPEID, // //$NON-NLS-1$
					Pm_X.VNROLETYPE, //

					PredicateMatrix.PBROLESETID, //
					Pm_X.PBROLESETNAME, //
					Pm_X.PBROLESETDESCR, //
					Pm_X.PBROLESETHEAD, //
					Pm_X.PBROLEID, //
					Pm_X.PBROLEDESCR, //
					"pt." + Pm_X.PBROLENARG, // //$NON-NLS-1$
					Pm_X.PBROLENARGDESCR, //

					PredicateMatrix.FRAMEID, //
					Pm_X.FRAME, //
					Pm_X.FRAMEDEFINITION, //
					Pm_X.LEXUNIT, //
					Pm_X.LUDEFINITION, //
					Pm_X.LUDICT, //
					"ft." + Pm_X.FETYPEID, // //$NON-NLS-1$
					Pm_X.FETYPE, //
					Pm_X.FEABBREV, //
					Pm_X.FEDEFINITION, //
			};
			final String selection = getSelection();
			final String[] selectionArgs = getSelectionArgs();
			final String sortOrder = displayer.getRequiredOrder();
			return new CursorLoader(BasicModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
		}

		@Override
		public void onLoadFinished(final Loader<Cursor> loader0, final Cursor cursor)
		{
			if (cursor.moveToFirst())
			{
				// column indices
				final int idPmId = cursor.getColumnIndex(PredicateMatrix.PMID);
				final int idPmRoleId = cursor.getColumnIndex(PredicateMatrix.PMROLEID);
				final int idPmPredId = cursor.getColumnIndex(PredicateMatrix.PMPREDID);
				final int idPmPredicate = cursor.getColumnIndex(PredicateMatrix.PMPREDICATE);
				final int idPmRole = cursor.getColumnIndex(PredicateMatrix.PMROLE);
				final int idPmPos = cursor.getColumnIndex(PredicateMatrix.PMPOS);

				// final int idWord = cursor.getColumnIndex(PredicateMatrix.WORD);
				final int idSynsetId = cursor.getColumnIndex(PredicateMatrix.SYNSETID);
				final int idDefinition = cursor.getColumnIndex(Pm_X.DEFINITION);

				final int idVnClassId = cursor.getColumnIndex(PredicateMatrix.VNCLASSID);
				final int idVnClass = cursor.getColumnIndex(Pm_X.VNCLASS);
				final int idVnRoleTypeId = cursor.getColumnIndex(Pm_X.VNROLETYPEID);
				final int idVnRoleType = cursor.getColumnIndex(Pm_X.VNROLETYPE);
				// final int idVnRoleId = cursor.getColumnIndex(Pm_X.VNROLEID);
				// final int idVnRoleRestrsId = cursor.getColumnIndex(Pm_X.VNROLERESTRID);

				final int idPbRoleSetId = cursor.getColumnIndex(PredicateMatrix.PBROLESETID);
				final int idPbRoleSet = cursor.getColumnIndex(Pm_X.PBROLESETNAME);
				final int idPbRoleSetDescr = cursor.getColumnIndex(Pm_X.PBROLESETDESCR);
				final int idPbRoleId = cursor.getColumnIndex(Pm_X.PBROLEID);
				final int idPbRole = cursor.getColumnIndex(Pm_X.PBROLENARG);
				final int idPbRoleDescr = cursor.getColumnIndex(Pm_X.PBROLEDESCR);
				// final int idPbRoleSetHead = cursor.getColumnIndex(Pm_X.PBROLESETHEAD);
				// final int idPbRoleNArgDescr = cursor.getColumnIndex(Pm_X.PBROLENARGDESCR);

				final int idFnFrameId = cursor.getColumnIndex(PredicateMatrix.FRAMEID);
				final int idFnFrame = cursor.getColumnIndex(Pm_X.FRAME);
				final int idFnFeTypeId = cursor.getColumnIndex(Pm_X.FETYPEID);
				final int idFnFeType = cursor.getColumnIndex(Pm_X.FETYPE);
				// final int idFnFrameDefinition = cursor.getColumnIndex(Pm_X.FRAMEDEFINITION);
				// final int idFnLexUnit = cursor.getColumnIndex(Pm_X.LEXUNIT);
				// final int idFnLuDefinition = cursor.getColumnIndex(Pm_X.LUDEFINITION);
				// final int idFnLuDict = cursor.getColumnIndex(Pm_X.LUDICT);
				// final int idFnFeAbbrev = cursor.getColumnIndex(Pm_X.FEABBREV);
				// final int idFnFeDefinition = cursor.getColumnIndex(Pm_X.FEDEFINITION);

				// read cursor
				do
				{
					// data
					final long pmid = cursor.getLong(idPmId);
					final long pmroleid = cursor.getLong(idPmRoleId);
					final long pmpredid = cursor.getLong(idPmPredId);
					final String pmpredicate = cursor.getString(idPmPredicate);
					final String pmrole = cursor.getString(idPmRole);
					final String pmpos = cursor.getString(idPmPos);
					final PmRow pmRole = new PmRow(pmid, pmpredid, pmroleid, pmpredicate, pmrole, pmpos);

					// final String word = cursor.getString(idWord);
					final long synsetid = cursor.getLong(idSynsetId);
					final String definition = cursor.getString(idDefinition);
					final WnData wnData = new WnData(synsetid, definition);

					final long vnclassid = cursor.getLong(idVnClassId);
					final long vnroleid = cursor.getLong(idVnRoleTypeId);
					final String vnclass = cursor.getString(idVnClass);
					final String vnrole = cursor.getString(idVnRoleType);
					final VnData vnData = new VnData(vnclassid, vnroleid, vnclass, vnrole);

					final long pbrolesetid = cursor.getLong(idPbRoleSetId);
					final long pbroleid = cursor.getLong(idPbRoleId);
					final String pbroleset = cursor.getString(idPbRoleSet);
					final String pbrolesetdescr = cursor.getString(idPbRoleSetDescr);
					final String pbrole = cursor.getString(idPbRole);
					final String pbroledescr = cursor.getString(idPbRoleDescr);
					final PbData pbData = new PbData(pbrolesetid, pbroleid, pbroleset, pbrolesetdescr, pbrole, pbroledescr);

					final long fnframeid = cursor.getLong(idFnFrameId);
					final long fnfeid = cursor.getLong(idFnFeTypeId);
					final String fnframe = cursor.getString(idFnFrame);
					final String fnfe = cursor.getString(idFnFeType);
					final FnData fnData = new FnData(fnframeid, fnfeid, fnframe, fnfe);

					// process
					process(this.parent, wnData, pmRole, vnData, pbData, fnData);
				} while (cursor.moveToNext());

				endProcess(this.parent);

				// expand
				TreeView.expand(this.parent, this.displayer.getExpandLevels());
			}

			cursor.close();
		}

		@Override
		public void onLoaderReset(final Loader<Cursor> arg0)
		{
			//
		}

		abstract protected void process(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData);

		void endProcess(@SuppressWarnings("UnusedParameters") final TreeNode parent)
		{
			//
		}
	}

	abstract class PmProcessGrouped extends PmCallbacks
	{
		private final List<Integer> pmroleids = new ArrayList<>();

		private final SparseArray<PmRole> pm = new SparseArray<>();

		private final SparseArray<Set<VnData>> vnMap = new SparseArray<>();

		private final SparseArray<Set<PbData>> pbMap = new SparseArray<>();

		private final SparseArray<Set<FnData>> fnMap = new SparseArray<>();

		private final Map<Object, Set<WnData>> wnMap = new HashMap<>();

		private long pmroleid;

		public PmProcessGrouped(final TreeNode parent, final Displayer displayer)
		{
			super(parent, displayer);
			this.pmroleid = -1;
		}

		protected void process(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			if (this.pmroleid != pmRow.pmroleid)
			{
				pmroleids.add((int) pmRow.pmroleid);
				pm.append((int) pmRow.pmroleid, pmRow);
				this.pmroleid = pmRow.pmroleid;
			}

			if (vnData.vnclassid > 0)
			{
				Set<VnData> vnSet = vnMap.get((int) pmroleid);
				if (vnSet == null)
				{
					vnSet = new TreeSet<>();
					vnMap.put((int) pmroleid, vnSet);
				}
				vnSet.add(vnData);

				// wn
				Set<WnData> wnSet = wnMap.get(vnData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					wnMap.put(vnData, wnSet);
				}
				wnSet.add(wnData);
			}

			if (pbData.pbrolesetid > 0)
			{
				Set<PbData> pbSet = pbMap.get((int) pmroleid);
				if (pbSet == null)
				{
					pbSet = new TreeSet<>();
					pbMap.put((int) pmroleid, pbSet);
				}
				pbSet.add(pbData);

				// wn
				Set<WnData> wnSet = wnMap.get(pbData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					wnMap.put(pbData, wnSet);
				}
				wnSet.add(wnData);
			}

			if (fnData.fnframeid > 0)
			{
				Set<FnData> fnSet = fnMap.get((int) pmroleid);
				if (fnSet == null)
				{
					fnSet = new TreeSet<>();
					fnMap.put((int) pmroleid, fnSet);
				}
				fnSet.add(fnData);

				// wn
				Set<WnData> wnSet = wnMap.get(fnData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					wnMap.put(fnData, wnSet);
				}
				wnSet.add(wnData);
			}
		}

		protected void endProcess(TreeNode parent2)
		{
			for (int pmroleid : pmroleids)
			{
				final PmRole pmRole = pm.get(pmroleid);
				final Set<VnData> vnDatas = vnMap.get(pmroleid);
				final Set<PbData> pbDatas = pbMap.get(pmroleid);
				final Set<FnData> fnDatas = fnMap.get(pmroleid);
				final TreeNode pmroleNode = displayer.displayPmRole(this.parent, pmRole);
				if (vnDatas != null)
					for (VnData vnData : vnDatas)
					{
						final Set<WnData> wnData = wnMap.get(vnData);
						final TreeNode vnNode = displayer.makeVnNode(vnData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(vnNode);
					}
				if (pbDatas != null)
					for (PbData pbData : pbDatas)
					{
						final Set<WnData> wnData = wnMap.get(pbData);
						final TreeNode pbNode = displayer.makePbNode(pbData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(pbNode);
					}
				if (fnDatas != null)
					for (FnData fnData : fnDatas)
					{
						final Set<WnData> wnData = wnMap.get(fnData);
						final TreeNode fnNode = displayer.makeFnNode(fnData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(fnNode);
					}
			}
		}
	}

	abstract class PmProcessOnIteration extends PmCallbacks
	{
		public PmProcessOnIteration(final TreeNode parent, final Displayer displayer)
		{
			super(parent, displayer);
		}

		protected void process(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			displayer.display(parentNode, wnData, pmRow, vnData, pbData, fnData);
		}
	}

	abstract class Displayer
	{
		abstract protected void display(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData);

		abstract protected String getRequiredOrder();

		abstract protected int getExpandLevels();

		void displayRow(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData, final boolean wnDataOnRow, @SuppressWarnings("SameParameterValue") final boolean wnDataOnXData)
		{
			// vn
			final TreeNode vnNode = wnDataOnXData ? makeVnNode(vnData, wnData) : makeVnNode(vnData);

			// pb
			final TreeNode pbNode = wnDataOnXData ? makePbNode(pbData, wnData) : makePbNode(pbData);

			// fn
			final TreeNode fnNode = wnDataOnXData ? makeFnNode(fnData, wnData) : makeFnNode(fnData);

			// entry
			final TreeNode roleNode = displayPmRow(parentNode, pmRow, wnDataOnRow ? wnData : null);
			roleNode.addChildren(vnNode, pbNode, fnNode);
		}

		TreeNode displayPmRole(final TreeNode parentNode, final PmRole pmRole)
		{
			final SpannableStringBuilder pmsb = new SpannableStringBuilder();
			// rolesb.append("predicate role "); //$NON-NLS-1$
			if (pmRole.pmrole != null)
			{
				final String roleName = pmRole.toRoleString();
				Spanner.append(pmsb, roleName, 0, PredicateMatrixFactories.groupFactory); // $NON-NLS-1$

				pmsb.append(' ');
				final String roleData = pmRole.toRoleData();
				Spanner.append(pmsb, roleData, 0, PredicateMatrixFactories.dataFactory);
			}

			return TreeFactory.addTreeItemNode(parentNode, pmsb, R.drawable.role, BasicModule.this.getContext());
		}

		TreeNode displayPmRow(final TreeNode parentNode, final PmRow pmRow, final WnData wnData)
		{
			final SpannableStringBuilder pmsb = new SpannableStringBuilder();
			// rolesb.append("predicate role "); //$NON-NLS-1$
			if (pmRow.pmrole != null)
			{
				final String rowName = pmRow.toRoleString();
				Spanner.append(pmsb, rowName, 0, PredicateMatrixFactories.nameFactory); // $NON-NLS-1$
			}

			pmsb.append(' ');
			Spanner.append(pmsb, pmRow.toData(), 0, PredicateMatrixFactories.dataFactory);

			if (wnData != null)
			{
				pmsb.append(' '); // $NON-NLS-1$
				Spanner.append(pmsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return TreeFactory.addTreeItemNode(parentNode, pmsb, R.drawable.predicatematrix, BasicModule.this.getContext());
		}

		TreeNode makeVnNode(final VnData vnData, final WnData... wnDatas)
		{
			final SpannableStringBuilder vnsb = new SpannableStringBuilder();
			if (vnData.vnclass != null && !vnData.vnclass.isEmpty())
			{
				Spanner.appendImage(vnsb, BasicModule.this.classDrawable);
				vnsb.append(' ');
				Spanner.append(vnsb, vnData.vnclass, 0, PredicateMatrixFactories.classFactory);
			}
			vnsb.append(' ');
			Spanner.appendImage(vnsb, BasicModule.this.roleDrawable);
			vnsb.append(' ');
			if (vnData.vnrole != null && !vnData.vnrole.isEmpty())
			{
				Spanner.append(vnsb, vnData.vnrole, 0, PredicateMatrixFactories.roleFactory);
			} else
				vnsb.append('∅');

			vnsb.append(' ');
			Spanner.append(vnsb, vnData.toData(), 0, PredicateMatrixFactories.dataFactory);

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
					first = false;
				else
				{
					vnsb.append(' ');
					vnsb.append('|');
				}
				vnsb.append(' ');
				Spanner.append(vnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return vnData.vnclassid == 0L ?
					TreeFactory.newLeafNode(vnsb, R.drawable.verbnet, BasicModule.this.getContext()) :
					TreeFactory.newLinkNode(new VnClassQuery(vnData.vnclassid, R.drawable.verbnet, vnsb), BasicModule.this.getContext());
		}

		TreeNode makePbNode(final PbData pbData, final WnData... wnDatas)
		{
			// pb
			final SpannableStringBuilder pbsb = new SpannableStringBuilder();
			if (pbData.pbroleset != null && !pbData.pbroleset.isEmpty())
			{
				Spanner.appendImage(pbsb, BasicModule.this.classDrawable);
				pbsb.append(' ');
				Spanner.append(pbsb, pbData.pbroleset, 0, PredicateMatrixFactories.classFactory);
			}
			pbsb.append(' ');
			Spanner.appendImage(pbsb, BasicModule.this.roleDrawable);
			pbsb.append(' ');
			if (pbData.pbrole != null && !pbData.pbrole.isEmpty())
			{
				Spanner.append(pbsb, pbData.pbrole, 0, PredicateMatrixFactories.roleFactory);
				pbsb.append(' ');
				if (pbData.pbroledescr != null)
				{
					pbsb.append('-');
					pbsb.append(' ');
					Spanner.append(pbsb, capitalize1(pbData.pbroledescr), 0, PredicateMatrixFactories.roleFactory);
				}
			} else
				pbsb.append('∅');

			pbsb.append(' ');
			Spanner.append(pbsb, pbData.toData(), 0, PredicateMatrixFactories.dataFactory);

			if (pbData.pbrolesetdescr != null)
			{
				pbsb.append(' ');
				Spanner.append(pbsb, pbData.pbrolesetdescr, 0, PredicateMatrixFactories.dataFactory);
			}

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
					first = false;
				else
				{
					pbsb.append(' ');
					pbsb.append('|');
				}
				pbsb.append(' ');
				Spanner.append(pbsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return pbData.pbrolesetid == 0L ?
					TreeFactory.newLeafNode(pbsb, R.drawable.propbank, BasicModule.this.getContext()) :
					TreeFactory.newLinkNode(new PbRoleSetQuery(pbData.pbrolesetid, R.drawable.propbank, pbsb), BasicModule.this.getContext());
		}

		TreeNode makeFnNode(final FnData fnData, final WnData... wnDatas)
		{
			// fn
			final SpannableStringBuilder fnsb = new SpannableStringBuilder();
			if (fnData.fnframe != null && !fnData.fnframe.isEmpty())
			{
				Spanner.appendImage(fnsb, BasicModule.this.classDrawable);
				fnsb.append(' ');
				Spanner.append(fnsb, fnData.fnframe, 0, PredicateMatrixFactories.classFactory);
			}
			fnsb.append(' ');
			Spanner.appendImage(fnsb, BasicModule.this.roleDrawable);
			fnsb.append(' ');
			if (fnData.fnfe != null && !fnData.fnfe.isEmpty())
			{
				Spanner.append(fnsb, fnData.fnfe, 0, PredicateMatrixFactories.roleFactory);
			} else
				fnsb.append('∅');

			fnsb.append(' ');
			Spanner.append(fnsb, fnData.toData(), 0, PredicateMatrixFactories.dataFactory);

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
					first = false;
				else
				{
					fnsb.append(' ');
					fnsb.append('|');
				}
				fnsb.append(' ');
				Spanner.append(fnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return fnData.fnframeid == 0L ?
					TreeFactory.newLeafNode(fnsb, R.drawable.framenet, BasicModule.this.getContext()) :
					TreeFactory.newLinkNode(new FnFrameQuery(fnData.fnframeid, R.drawable.framenet, fnsb), BasicModule.this.getContext());
		}
	}

	/* @formatter:off */
	/*
	// sources
	static int SEMLINK = 0x1;

	static int SYNONYMS = 0x2;

	static int FRAME = 0x10;

	static int FN_FE = 0x20;

	static int ADDED_FRAME_FN_ROLE = 0x40; // ADDED_FRAME-FN_ROLE

	static int FN_MAPPING = 0x100;

	static int VN_MAPPING = 0x200;

	static int PREDICATE_MAPPING = 0x400;

	static int ROLE_MAPPING = 0x800;

	static int WN_MISSING = 0x1000;

	static void parsePmSources(final SpannableStringBuilder sb, final int sources)
	{
		if ((sources & SEMLINK) != 0)
			Spanner.append(sb, "SEMLINK ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & SYNONYMS) != 0)
			Spanner.append(sb, "SYNONYMS ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & FRAME) != 0)
			Spanner.append(sb, "FRAME ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & FN_FE) != 0)
			Spanner.append(sb, "FN_FE", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & ADDED_FRAME_FN_ROLE) != 0)
			Spanner.append(sb, "ADDED_FRAME-FN_ROLE ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & FN_MAPPING) != 0)
			Spanner.append(sb, "FN_MAPPING ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & VN_MAPPING) != 0)
			Spanner.append(sb, "VN_MAPPING ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & PREDICATE_MAPPING) != 0)
			Spanner.append(sb, "PREDICATE_MAPPING ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & ROLE_MAPPING) != 0)
			Spanner.append(sb, "ROLE_MAPPING ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$
		if ((sources & WN_MISSING) != 0)
			Spanner.append(sb, "WN_MISSING ", 0, PredicateMatrixFactories.dataFactory); //$NON-NLS-1$

		Spanner.append(sb, Long.toString(sources), 0, PredicateMatrixFactories.dataFactory);
	}
	
	*/
	/* @formatter:on */

	// Q U E R I E S

	class DisplayerBySynset extends Displayer
	{
		private long synsetid = -1L;

		private TreeNode synsetNode = null;

		public void display(final TreeNode parentNode, final WnData wnData, final PmRow pmRole, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			if (this.synsetid != wnData.synsetid)
			{
				final SpannableStringBuilder synsetsb = new SpannableStringBuilder();
				if (synsetid != -1)
				{
					Spanner.append(synsetsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
					synsetsb.append(' ');
					Spanner.append(synsetsb, Long.toString(synsetid), 0, PredicateMatrixFactories.dataFactory);
				} else
					synsetsb.append('∅');

				// attach synset
				this.synsetNode = TreeFactory.addTreeItemNode(parentNode, synsetsb, R.drawable.synset, BasicModule.this.getContext());

				// record
				this.synsetid = wnData.synsetid;
			}
			super.displayRow(this.synsetNode, wnData, pmRole, vnData, pbData, fnData, false, false);
		}

		@Override
		protected String getRequiredOrder()
		{
			return "synsetid"; //$NON-NLS-1$
		}

		@Override
		protected int getExpandLevels()
		{
			return 3;
		}
	}

	class DisplayerByPmRole extends Displayer
	{
		private long pmroleid = -1L;

		private TreeNode pmroleNode = null;

		public void display(final TreeNode parentNode, final WnData wnData, final PmRow pmRole, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			if (this.pmroleid != pmRole.pmroleid)
			{
				// group
				this.pmroleNode = displayPmRole(parentNode, pmRole);

				// record
				this.pmroleid = pmRole.pmroleid;
			}

			super.displayRow(this.pmroleNode, wnData, pmRole, vnData, pbData, fnData, true, false);
		}

		@Override
		protected String getRequiredOrder()
		{
			return "pmroleid"; //$NON-NLS-1$
		}

		@Override
		protected int getExpandLevels()
		{
			return 3;
		}
	}

	class DisplayerUngrouped extends Displayer
	{
		public void display(final TreeNode parentNode, final WnData wnData, final PmRow pmRole, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			super.displayRow(parentNode, wnData, pmRole, vnData, pbData, fnData, true, false);
		}

		@Override
		protected String getRequiredOrder()
		{
			return "pmid"; //$NON-NLS-1$
		}

		@Override
		protected int getExpandLevels()
		{
			return 2;
		}
	}

	// R O L E S

	class VnClassQuery extends LinkHolder.Link
	{
		public VnClassQuery(final long classid0, final int icon, final CharSequence text)
		{
			super(classid0, icon, text);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process(final TreeNode node0)
		{
			Log.d(LOG, "class " + this.id); //$NON-NLS-1$

			final VnClassPointer pointer = new VnClassPointer();
			pointer.classid = this.id;
			final Intent intent = new Intent(BasicModule.this.getContext(), VnClassActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_VNCLASS);
			intent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, pointer);

			BasicModule.this.getContext().startActivity(intent);
		}
	}

	class PbRoleSetQuery extends LinkHolder.Link
	{
		public PbRoleSetQuery(final long rolesetid0, final int icon, final CharSequence text)
		{
			super(rolesetid0, icon, text);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process(final TreeNode node0)
		{
			System.out.println("roleset " + this.id); //$NON-NLS-1$
			final PbRoleSetPointer pointer = new PbRoleSetPointer();
			pointer.rolesetid = this.id;
			final Intent intent = new Intent(BasicModule.this.getContext(), PbRoleSetActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_PBROLESET);
			intent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, pointer);

			BasicModule.this.getContext().startActivity(intent);
		}
	}

	class FnFrameQuery extends LinkHolder.Link
	{
		public FnFrameQuery(final long frameid0, final int icon, final CharSequence text)
		{
			super(frameid0, icon, text);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process(final TreeNode node0)
		{
			System.out.println("frame " + this.id); //$NON-NLS-1$

			final FnFramePointer pointer = new FnFramePointer();
			pointer.frameid = this.id;
			final Intent intent = new Intent(BasicModule.this.getContext(), FnFrameActivity.class);
			intent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNFRAME);
			intent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, pointer);

			BasicModule.this.getContext().startActivity(intent);
		}
	}
}
