package org.sqlunet.predicatematrix.loaders;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.SqlunetViewModelFactory;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.browser.FnFrameActivity;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.Pm_X;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.PredicateMatrix;
import org.sqlunet.predicatematrix.provider.PredicateMatrixProvider;
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.browser.PbRoleSetActivity;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.control.Link;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.browser.VnClassActivity;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * Base module for PredicateMatrix
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract class BaseModule extends Module
{
	// static private final String LOG = "PredicateMatrix";
	// resources

	/**
	 * Drawable for roles
	 */
	private final Drawable classDrawable;

	/**
	 * Drawable for role
	 */
	private final Drawable roleDrawable;

	/**
	 * Constructor
	 */
	BaseModule(@NonNull final Fragment fragment)
	{
		super(fragment);

		// spanner
		assert this.context != null;
		this.classDrawable = Spanner.getDrawable(this.context, R.drawable.roles);
		this.roleDrawable = Spanner.getDrawable(this.context, R.drawable.role);
	}

	// L O A D E R S

	/**
	 * Load from word
	 *
	 * @param word      target word
	 * @param parent    parent node
	 * @param displayer displayer
	 */
	void fromWord(final String word, final TreeNode parent, final Displayer displayer)
	{
		new PmProcessOnIteration(parent, displayer)
		{
			@NonNull
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.WORD + "= ?";
			}

			@NonNull
			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{word};
			}
		}.run(this.fragment);
	}

	/**
	 * Load from word
	 *
	 * @param word   word
	 * @param parent parent node
	 */
	void fromWordGrouped(final String word, final TreeNode parent)
	{
		new PmProcessGrouped(parent, new DisplayerByPmRole())
		{
			@NonNull
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.WORD + "= ?";
			}

			@NonNull
			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{word};
			}
		}.run(this.fragment);
	}

	/**
	 * Load from role id
	 *
	 * @param pmRoleId  role id
	 * @param parent    parent node
	 * @param displayer displayer
	 */
	void fromRoleId(final long pmRoleId, final TreeNode parent, final Displayer displayer)
	{
		new PmProcessOnIteration(parent, displayer)
		{
			@NonNull
			@Override
			protected String getSelection()
			{
				return PredicateMatrix.PMROLEID + "= ?";
			}

			@NonNull
			@Override
			protected String[] getSelectionArgs()
			{
				return new String[]{Long.toString(pmRoleId)};
			}
		}.run(this.fragment);
	}

	// D A T A

	/**
	 * PredicateMatrix role
	 */
	static class PmRole implements Comparable<PmRole>
	{
		/**
		 * PredicateMatrix predicate id
		 */
		final long pmPredId;

		/**
		 * PredicateMatrix role id
		 */
		final long pmRoleId;

		/**
		 * PredicateMatrix predicate
		 */
		final String pmPredicate;

		/**
		 * PredicateMatrix role
		 */
		final String pmRole;

		/**
		 * PredicateMatrix pos
		 */
		final String pmPos;

		/**
		 * Constructor
		 *
		 * @param pmPredId    PredicateMatrix predicate id
		 * @param pmRoleId    PredicateMatrix role id
		 * @param pmPredicate predicate
		 * @param pmRole      PredicateMatrix role
		 * @param pmPos       PredicateMatrix pos
		 */
		PmRole(final long pmPredId, final long pmRoleId, final String pmPredicate, final String pmRole, final String pmPos)
		{
			this.pmPredId = pmPredId;
			this.pmRoleId = pmRoleId;
			this.pmPredicate = pmPredicate;
			this.pmRole = pmRole;
			this.pmPos = pmPos;
		}

		@Override
		public int compareTo(@NonNull final PmRole another)
		{
			if (this.pmPos.charAt(0) != another.pmPos.charAt(0))
			{
				return this.pmPos.charAt(0) > another.pmPos.charAt(0) ? 1 : -1;
			}
			if (this.pmPredId != another.pmPredId)
			{
				return this.pmPredId > another.pmPredId ? 1 : -1;
			}
			if (this.pmRoleId != another.pmRoleId)
			{
				return this.pmRoleId > another.pmRoleId ? 1 : -1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof PmRole))
			{
				return false;
			}
			final PmRole pmdata2 = (PmRole) another;
			return this.pmPos.charAt(0) == pmdata2.pmPos.charAt(0) && this.pmRoleId == pmdata2.pmRoleId && this.pmPredId == pmdata2.pmPredId;
		}

		@Override
		public int hashCode()
		{
			return (int) (17 * (int) this.pmPos.charAt(0) + 19 * this.pmRoleId + 3 * this.pmPredId);
		}

		@NonNull
		@Override
		public String toString()
		{
			return toRoleString();
		}

		/**
		 * Role data to string
		 *
		 * @return role data as string
		 */
		@NonNull
		String toRoleString()
		{
			return (this.pmPos == null ? "null" : this.pmPos) + //
					'-' + //
					this.pmPredicate + '-' + //
					(this.pmRole == null ? "null" : this.pmRole) //
					;
		}

		/**
		 * Role data to string
		 *
		 * @return role data as string
		 */
		@NonNull
		String toData()
		{
			return toRoleData();
		}

		/**
		 * Role data to string
		 *
		 * @return role data as string
		 */
		@NonNull
		String toRoleData()
		{
			return this.pmPos + '-' + Long.toString(this.pmRoleId) + '-' + Long.toString(this.pmPredId);
		}
	}

	/**
	 * PredicateMatrix row
	 */
	static class PmRow extends PmRole
	{
		/**
		 * PredicateMatrix row id
		 */
		private final long pmId;

		/**
		 * Constructor
		 *
		 * @param pmId        PredicateMatrix row id
		 * @param pmPredId    PredicateMatrix predicate id
		 * @param pmRoleId    PredicateMatrix role id
		 * @param pmPredicate PredicateMatrix predicate
		 * @param pmRole      PredicateMatrix role
		 * @param pmPos       PredicateMatrix pos
		 */
		PmRow(final long pmId, final long pmPredId, final long pmRoleId, final String pmPredicate, final String pmRole, final String pmPos)
		{
			super(pmPredId, pmRoleId, pmPredicate, pmRole, pmPos);
			this.pmId = pmId;
		}

		@NonNull
		@Override
		public String toString()
		{
			return '[' + Long.toString(this.pmId) + ']' + '-' + super.toString();
		}

		@NonNull
		@Override
		String toData()
		{
			return '[' + Long.toString(this.pmId) + ']' + '-' + super.toData();
		}
	}

	/**
	 * WordNet data
	 */
	static class WnData implements Comparable<WnData>
	{
		/**
		 * Synset id
		 */
		final long synsetId;

		/**
		 * Synset definition
		 */
		final String definition;

		/**
		 * Constructor
		 *
		 * @param synsetId   synset id
		 * @param definition definition
		 */
		WnData(final long synsetId, final String definition)
		{
			this.synsetId = synsetId;
			this.definition = definition;
		}

		@Override
		public int compareTo(@NonNull final WnData another)
		{
			if (this.synsetId != another.synsetId)
			{
				return this.synsetId > another.synsetId ? 1 : -1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof WnData))
			{
				return false;
			}
			final WnData wnData2 = (WnData) another;
			return this.synsetId == wnData2.synsetId;
		}

		@Override
		public int hashCode()
		{
			return (int) this.synsetId;
		}

		@NonNull
		@Override
		public String toString()
		{
			return this.definition;
		}
	}

	/**
	 * VerbNet data
	 */
	static class VnData implements Comparable<VnData>
	{
		/**
		 * Class id
		 */
		final long vnClassId;

		/**
		 * Role id
		 */
		final long vnRoleId;

		/**
		 * Class
		 */
		final String vnClass;

		/**
		 * Role
		 */
		final String vnRole;

		/**
		 * Constructor
		 *
		 * @param vnClassId class id
		 * @param vnRoleId  role id
		 * @param vnClass   class
		 * @param vnRole    role
		 */
		VnData(final long vnClassId, final long vnRoleId, final String vnClass, final String vnRole)
		{
			this.vnClassId = vnClassId;
			this.vnRoleId = vnRoleId;
			this.vnClass = vnClass;
			this.vnRole = vnRole;
		}

		@Override
		public int compareTo(@NonNull final VnData another)
		{
			if (this.vnClassId != another.vnClassId)
			{
				return this.vnClassId > another.vnClassId ? 1 : -1;
			}
			if (this.vnRoleId != another.vnRoleId)
			{
				return this.vnRoleId > another.vnRoleId ? 1 : -1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof VnData))
			{
				return false;
			}
			final VnData vnData2 = (VnData) another;
			return this.vnClassId == vnData2.vnClassId && this.vnRoleId == vnData2.vnRoleId;
		}

		@Override
		public int hashCode()
		{
			return (int) (19 * this.vnClassId + 13 * this.vnRoleId);
		}

		@NonNull
		@Override
		public String toString()
		{
			return this.vnClass + '-' + this.vnRole;
		}

		/**
		 * Convert id data to string
		 *
		 * @return string
		 */
		@NonNull
		CharSequence toData()
		{
			return Long.toString(this.vnClassId) + '-' + Long.toString(this.vnRoleId);
		}
	}

	/**
	 * PropBank data
	 */
	static class PbData implements Comparable<PbData>
	{
		/**
		 * Role set id
		 */
		final long pbRoleSetId;

		/**
		 * Role id
		 */
		final long pbRoleId;

		/**
		 * Role set
		 */
		final String pbRoleSet;

		/**
		 * Role set description
		 */
		final String pbRoleSetDescr;

		/**
		 * Role
		 */
		final String pbRole;

		/**
		 * Role description
		 */
		final String pbRoleDescr;

		/**
		 * @param pbRoleSetId    role set id
		 * @param pbRoleId       role id
		 * @param pbRoleSet      role set
		 * @param pbRoleSetDescr role set description
		 * @param pbRole         role
		 * @param pbRoleDescr    role description
		 */
		PbData(final long pbRoleSetId, final long pbRoleId, final String pbRoleSet, final String pbRoleSetDescr, final String pbRole, final String pbRoleDescr)
		{
			this.pbRoleSetId = pbRoleSetId;
			this.pbRoleId = pbRoleId;
			this.pbRoleSet = pbRoleSet;
			this.pbRoleSetDescr = pbRoleSetDescr;
			this.pbRole = pbRole;
			this.pbRoleDescr = pbRoleDescr;
		}

		@Override
		public int compareTo(@NonNull final PbData another)
		{
			if (this.pbRoleSetId != another.pbRoleSetId)
			{
				return this.pbRoleSetId > another.pbRoleSetId ? 1 : -1;
			}
			if (this.pbRoleId != another.pbRoleId)
			{
				return this.pbRoleId > another.pbRoleId ? 1 : -1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof PbData))
			{
				return false;
			}
			final PbData pbdata2 = (PbData) another;
			return this.pbRoleSetId == pbdata2.pbRoleSetId && this.pbRoleId == pbdata2.pbRoleId;
		}

		@Override
		public int hashCode()
		{
			return (int) (23 * this.pbRoleSetId + 51 * this.pbRoleId);
		}

		@NonNull
		@Override
		public String toString()
		{
			return this.pbRoleSet + '-' + this.pbRole;
		}

		/**
		 * Convert id data to string
		 *
		 * @return string
		 */
		@NonNull
		CharSequence toData()
		{
			return Long.toString(this.pbRoleSetId) + '-' + Long.toString(this.pbRoleId);
		}
	}

	/**
	 * FrameNet data
	 */
	static class FnData implements Comparable<FnData>
	{
		/**
		 * Frame id
		 */
		final long fnFrameId;

		/**
		 * Frame element id
		 */
		final long fnFeId;

		/**
		 * Frame
		 */
		final String fnFrame;

		/**
		 * Frame
		 */
		final String fnFe;

		/**
		 * Constructor
		 *
		 * @param fnFrameId Frame id
		 * @param fnFeId    Frame element id
		 * @param fnFrame   Frame
		 * @param fnFe      Frame element
		 */
		FnData(final long fnFrameId, final long fnFeId, final String fnFrame, final String fnFe)
		{
			this.fnFrameId = fnFrameId;
			this.fnFeId = fnFeId;
			this.fnFrame = fnFrame;
			this.fnFe = fnFe;
		}

		@Override
		public int compareTo(@NonNull final FnData another)
		{
			if (this.fnFrameId != another.fnFrameId)
			{
				return this.fnFrameId > another.fnFrameId ? 1 : -1;
			}
			if (this.fnFeId != another.fnFeId)
			{
				return this.fnFeId > another.fnFeId ? 1 : -1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object another)
		{
			if (!(another instanceof FnData))
			{
				return false;
			}
			final FnData fndata2 = (FnData) another;
			return this.fnFrameId == fndata2.fnFrameId && this.fnFeId == fndata2.fnFeId;
		}

		@Override
		public int hashCode()
		{
			return (int) (17 * this.fnFrameId + 7 * this.fnFeId);
		}

		@NonNull
		@Override
		public String toString()
		{
			return this.fnFrame + '-' + this.fnFe;
		}

		/**
		 * Convert id data to string
		 *
		 * @return string
		 */
		@NonNull
		CharSequence toData()
		{
			return Long.toString(this.fnFrameId) + '-' + Long.toString(this.fnFeId);
		}
	}

	// D I S P L A Y E R S

	/**
	 * Abstract PredicateMatrix loader callbacks
	 */
	abstract class PmCallbacks
	{
		/**
		 * Displayer
		 */
		final Displayer displayer;

		/**
		 * Parent node
		 */
		final TreeNode parent;

		/**
		 * Constructor
		 *
		 * @param parent    parent node
		 * @param displayer displayer
		 */
		PmCallbacks(final TreeNode parent, final Displayer displayer)
		{
			this.parent = parent;
			this.displayer = displayer;
		}

		/**
		 * Selection
		 *
		 * @return selection
		 */
		@NonNull
		abstract protected String getSelection();

		/**
		 * Selection arguments
		 *
		 * @return selection arguments
		 */
		@NonNull
		abstract protected String[] getSelectionArgs();

		@NonNull
		public void run(final Fragment fragment)
		{
			final Uri uri = Uri.parse(PredicateMatrixProvider.makeUri(Pm_X.CONTENT_URI_TABLE));
			final String[] projection = { //
					PredicateMatrix.PMID, //
					PredicateMatrix.PMROLEID, //
					PredicateMatrix.PMPREDID, //
					PredicateMatrix.PMPREDICATE, //
					PredicateMatrix.PMROLE, //
					PredicateMatrixContract.PMROLE + '.' + PredicateMatrix.PMPOS, //

					PredicateMatrix.WORD, //
					PredicateMatrix.SYNSETID, //
					Pm_X.DEFINITION, //

					PredicateMatrix.VNCLASSID, //
					Pm_X.VNCLASS, //
					PredicateMatrixContract.VNROLETYPE + '.' + Pm_X.VNROLETYPEID, //
					Pm_X.VNROLETYPE, //

					PredicateMatrix.PBROLESETID, //
					Pm_X.PBROLESETNAME, //
					Pm_X.PBROLESETDESCR, //
					Pm_X.PBROLESETHEAD, //
					Pm_X.PBROLEID, //
					Pm_X.PBROLEDESCR, //
					PredicateMatrixContract.PBARG + '.' + Pm_X.PBROLENARG, //
					Pm_X.PBROLENARGDESCR, //

					PredicateMatrix.FRAMEID, //
					Pm_X.FRAME, //
					Pm_X.FRAMEDEFINITION, //
					Pm_X.LEXUNIT, //
					Pm_X.LUDEFINITION, //
					Pm_X.LUDICT, //
					PredicateMatrixContract.FNFETYPE + '.' + Pm_X.FETYPEID, //
					Pm_X.FETYPE, //
					Pm_X.FEABBREV, //
					Pm_X.FEDEFINITION, //
			};
			final String selection = getSelection();
			final String[] selectionArgs = getSelectionArgs();
			final String sortOrder = this.displayer.getRequiredOrder();

			final SqlunetViewModel model = ViewModelProviders.of(fragment, new SqlunetViewModelFactory(fragment, uri, projection, selection, selectionArgs, sortOrder)).get(SqlunetViewModel.class);
			final String tag = "pm.pm";
			model.loadData(tag);
			model.getData().observe(fragment, entry -> {

				final String key = entry.getKey();
				if (!tag.equals(key))
				{
					return;
				}
				final Cursor cursor = entry.getValue();
				pmToView(cursor, parent);
			});
		}

		private void pmToView(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
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
					final long pmId = cursor.getLong(idPmId);
					final long pmRoleId = cursor.getLong(idPmRoleId);
					final long pmPredId = cursor.getLong(idPmPredId);
					final String pmPredicate = cursor.getString(idPmPredicate);
					final String pmRole = cursor.getString(idPmRole);
					final String pmPos = cursor.getString(idPmPos);
					final PmRow pmRow = new PmRow(pmId, pmPredId, pmRoleId, pmPredicate, pmRole, pmPos);

					// final String word = cursor.getString(idWord);
					final long synsetId = cursor.getLong(idSynsetId);
					final String definition = cursor.getString(idDefinition);
					final WnData wnData = new WnData(synsetId, definition);

					final long vnClassId = cursor.getLong(idVnClassId);
					final long vnRoleId = cursor.getLong(idVnRoleTypeId);
					final String vnClass = cursor.getString(idVnClass);
					final String vnRole = cursor.getString(idVnRoleType);
					final VnData vnData = new VnData(vnClassId, vnRoleId, vnClass, vnRole);

					final long pbRoleSetId = cursor.getLong(idPbRoleSetId);
					final long pbRoleId = cursor.getLong(idPbRoleId);
					final String pbRoleSet = cursor.getString(idPbRoleSet);
					final String pbRoleSetDescr = cursor.getString(idPbRoleSetDescr);
					final String pbRole = cursor.getString(idPbRole);
					final String pbRoleDescr = cursor.getString(idPbRoleDescr);
					final PbData pbData = new PbData(pbRoleSetId, pbRoleId, pbRoleSet, pbRoleSetDescr, pbRole, pbRoleDescr);

					final long fnFrameId = cursor.getLong(idFnFrameId);
					final long fnFeId = cursor.getLong(idFnFeTypeId);
					final String fnFrame = cursor.getString(idFnFrame);
					final String fnFe = cursor.getString(idFnFeType);
					final FnData fnData = new FnData(fnFrameId, fnFeId, fnFrame, fnFe);

					// process
					process(this.parent, wnData, pmRow, vnData, pbData, fnData);
				}
				while (cursor.moveToNext());

				endProcess();

				// fire event
				FireEvent.onResults(this.parent, this.displayer.getExpandLevels());
			}

			// TODO no need to call cursor.close() ?
		}

		/**
		 * Process row
		 *
		 * @param parent parent node
		 * @param wnData WordNet data
		 * @param pmRow  PredicateMatrix row
		 * @param vnData VerbNet data
		 * @param pbData PropBank data
		 * @param fnData FrameNet data
		 */
		abstract protected void process(final TreeNode parent, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData);

		/**
		 * End of processing
		 */
		void endProcess()
		{
			//
		}
	}

	/**
	 * Processor of data based on grouping rows
	 */
	abstract class PmProcessGrouped extends PmCallbacks
	{
		/**
		 * Role ids
		 */
		private final Collection<Integer> pmRoleIds = new ArrayList<>();

		/**
		 * Map role id to role
		 */
		private final SparseArray<PmRole> pm = new SparseArray<>();

		/**
		 * Map role id to VerbNet data
		 */
		private final SparseArray<Set<VnData>> vnMap = new SparseArray<>();

		/**
		 * Map role id to PropBankNet data
		 */
		private final SparseArray<Set<PbData>> pbMap = new SparseArray<>();

		/**
		 * Map role id to FrameNet data
		 */
		private final SparseArray<Set<FnData>> fnMap = new SparseArray<>();

		/**
		 * Map VerbNet/PropBank/FrameNet data to WordNet data
		 */
		private final Map<Object, Set<WnData>> wnMap = new HashMap<>();

		/**
		 * Grouping role id
		 */
		private long pmRoleId;

		/**
		 * Constructor
		 *
		 * @param parent    parent
		 * @param displayer displayer
		 */
		PmProcessGrouped(final TreeNode parent, final Displayer displayer)
		{
			super(parent, displayer);
			this.pmRoleId = 0;
		}

		@Override
		protected void process(final TreeNode parent, final WnData wnData, @NonNull final PmRow pmRow, @NonNull final VnData vnData, @NonNull final PbData pbData, @NonNull final FnData fnData)
		{
			if (this.pmRoleId != pmRow.pmRoleId)
			{
				this.pmRoleIds.add((int) pmRow.pmRoleId);
				this.pm.append((int) pmRow.pmRoleId, pmRow);
				this.pmRoleId = pmRow.pmRoleId;
			}

			if (vnData.vnClassId > 0)
			{
				Set<VnData> vnSet = this.vnMap.get((int) this.pmRoleId);
				if (vnSet == null)
				{
					vnSet = new TreeSet<>();
					this.vnMap.put((int) this.pmRoleId, vnSet);
				}
				vnSet.add(vnData);

				// wn
				Set<WnData> wnSet = this.wnMap.get(vnData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					this.wnMap.put(vnData, wnSet);
				}
				wnSet.add(wnData);
			}

			if (pbData.pbRoleSetId > 0)
			{
				Set<PbData> pbSet = this.pbMap.get((int) this.pmRoleId);
				if (pbSet == null)
				{
					pbSet = new TreeSet<>();
					this.pbMap.put((int) this.pmRoleId, pbSet);
				}
				pbSet.add(pbData);

				// wn
				Set<WnData> wnSet = this.wnMap.get(pbData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					this.wnMap.put(pbData, wnSet);
				}
				wnSet.add(wnData);
			}

			if (fnData.fnFrameId > 0)
			{
				Set<FnData> fnSet = this.fnMap.get((int) this.pmRoleId);
				if (fnSet == null)
				{
					fnSet = new TreeSet<>();
					this.fnMap.put((int) this.pmRoleId, fnSet);
				}
				fnSet.add(fnData);

				// wn
				Set<WnData> wnSet = this.wnMap.get(fnData);
				if (wnSet == null)
				{
					wnSet = new HashSet<>();
					this.wnMap.put(fnData, wnSet);
				}
				wnSet.add(wnData);
			}
		}

		@Override
		protected void endProcess()
		{
			for (int pmRoleId : this.pmRoleIds)
			{
				final PmRole pmRole = this.pm.get(pmRoleId);
				final Set<VnData> vnDatas = this.vnMap.get(pmRoleId);
				final Set<PbData> pbDatas = this.pbMap.get(pmRoleId);
				final Set<FnData> fnDatas = this.fnMap.get(pmRoleId);

				final SpannableStringBuilder pmsb = new SpannableStringBuilder();
				final TreeNode pmroleNode = this.displayer.displayPmRole(this.parent, pmsb, pmRole);
				final Collection<String> aliases = new TreeSet<>();

				if (vnDatas != null)
				{
					for (VnData vnData : vnDatas)
					{
						final Set<WnData> wnData = this.wnMap.get(vnData);
						assert wnData != null;
						final TreeNode vnNode = this.displayer.makeVnNode(vnData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(vnNode);

						// contribute to header
						if (vnData.vnRole != null && !vnData.vnRole.isEmpty())
						{
							aliases.add(vnData.vnRole);
						}
					}
				}
				if (pbDatas != null)
				{
					for (PbData pbData : pbDatas)
					{
						final Set<WnData> wnData = this.wnMap.get(pbData);
						assert wnData != null;
						final TreeNode pbNode = this.displayer.makePbNode(pbData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(pbNode);

						// contribute to header
						if (pbData.pbRoleDescr != null && !pbData.pbRoleDescr.isEmpty())
						{
							aliases.add(capitalize1(pbData.pbRoleDescr));
						}
					}
				}
				if (fnDatas != null)
				{
					for (FnData fnData : fnDatas)
					{
						final Set<WnData> wnData = this.wnMap.get(fnData);
						assert wnData != null;
						final TreeNode fnNode = this.displayer.makeFnNode(fnData, wnData.toArray(new WnData[0]));
						pmroleNode.addChild(fnNode);

						// contribute to header
						if (fnData.fnFe != null && !fnData.fnFe.isEmpty())
						{
							aliases.add(fnData.fnFe);
						}
					}
				}

				// add aliases to header
				for (String alias : aliases)
				{
					pmsb.append(' ');
					Spanner.append(pmsb, alias, 0, PredicateMatrixFactories.roleAliasFactory);
				}
			}
		}
	}

	/**
	 * Processor of data based on row iteration
	 */
	abstract class PmProcessOnIteration extends PmCallbacks
	{
		/**
		 * Constructor
		 *
		 * @param parent    parent
		 * @param displayer displayer to use
		 */
		PmProcessOnIteration(final TreeNode parent, final Displayer displayer)
		{
			super(parent, displayer);
		}

		@Override
		protected void process(final TreeNode parent, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData)
		{
			this.displayer.display(parent, wnData, pmRow, vnData, pbData, fnData);
		}
	}

	/**
	 * Abstract displayer
	 */
	abstract class Displayer
	{
		/**
		 * Display
		 *
		 * @param parentNode parent node
		 * @param wnData     WordNet data
		 * @param pmRow      PredicateMatrix row
		 * @param vnData     VerbNet data
		 * @param pbData     PropBank data
		 * @param fnData     FrameNet data
		 */
		abstract protected void display(final TreeNode parentNode, final WnData wnData, final PmRow pmRow, final VnData vnData, final PbData pbData, final FnData fnData);

		/**
		 * Get required sort order
		 *
		 * @return sort order
		 */
		@NonNull
		abstract protected String getRequiredOrder();

		/**
		 * Get number of expanded tree level
		 *
		 * @return number of expanded tree levels
		 */
		abstract protected int getExpandLevels();

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

		/**
		 * Display PredicateMatrix row
		 *
		 * @param parentNode    parent node
		 * @param wnData        WordNet data
		 * @param pmRow         PredicateMatrix row
		 * @param vnData        VerbNet data
		 * @param pbData        PropBank data
		 * @param fnData        FrameNet data
		 * @param wnDataOnRow   whether to display WordNet data on label
		 * @param wnDataOnXData whether to display WordNet data on extended data
		 */
		void displayRow(@NonNull final TreeNode parentNode, final WnData wnData, @NonNull final PmRow pmRow, @NonNull final VnData vnData, @NonNull final PbData pbData, @NonNull final FnData fnData, final boolean wnDataOnRow, @SuppressWarnings("SameParameterValue") final boolean wnDataOnXData)
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

		/**
		 * Display PredicateMatrix role
		 *
		 * @param parentNode parent node
		 * @param pmRole     PredicateMatrix role
		 * @return created node
		 */
		@NonNull
		TreeNode displayPmRole(@NonNull final TreeNode parentNode, @NonNull final PmRole pmRole)
		{
			final SpannableStringBuilder pmsb = new SpannableStringBuilder();
			return displayPmRole(parentNode, pmsb, pmRole);
		}

		/**
		 * Display PredicateMatrix role
		 *
		 * @param parentNode parent node
		 * @param pmRole     PredicateMatrix role
		 * @return created node
		 */
		@NonNull
		TreeNode displayPmRole(@NonNull final TreeNode parentNode, @NonNull final SpannableStringBuilder pmsb, @NonNull final PmRole pmRole)
		{
			if (pmRole.pmRole != null)
			{
				final String roleName = pmRole.toRoleString();
				Spanner.append(pmsb, roleName, 0, PredicateMatrixFactories.groupFactory);

				pmsb.append(' ');
				final String roleData = pmRole.toRoleData();
				Spanner.append(pmsb, roleData, 0, PredicateMatrixFactories.dataFactory);
			}

			return TreeFactory.addTreeNode(parentNode, pmsb, R.drawable.role, BaseModule.this.context);
		}

		/**
		 * Display PredicateMatrix row
		 *
		 * @param parentNode parent node
		 * @param pmRow      PredicateMatrix row
		 * @param wnData     WordNet data
		 * @return created node
		 */
		@NonNull
		TreeNode displayPmRow(@NonNull final TreeNode parentNode, @NonNull final PmRow pmRow, @Nullable final WnData wnData)
		{
			final SpannableStringBuilder pmsb = new SpannableStringBuilder();
			// rolesb.append("predicate role ");
			if (pmRow.pmRole != null)
			{
				final String rowName = pmRow.toRoleString();
				Spanner.append(pmsb, rowName, 0, PredicateMatrixFactories.nameFactory);
			}

			pmsb.append(' ');
			Spanner.append(pmsb, pmRow.toData(), 0, PredicateMatrixFactories.dataFactory);

			if (wnData != null)
			{
				pmsb.append(' ');
				Spanner.append(pmsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return TreeFactory.addTreeNode(parentNode, pmsb, R.drawable.predicatematrix, BaseModule.this.context);
		}

		/**
		 * Make VerbNet node
		 *
		 * @param vnData  VerbNet data
		 * @param wnDatas WordNet data
		 * @return created node
		 */
		@NonNull
		TreeNode makeVnNode(@NonNull final VnData vnData, @NonNull final WnData... wnDatas)
		{
			final SpannableStringBuilder vnsb = new SpannableStringBuilder();
			if (vnData.vnClass != null && !vnData.vnClass.isEmpty())
			{
				Spanner.appendImage(vnsb, BaseModule.this.classDrawable);
				vnsb.append(' ');
				Spanner.append(vnsb, vnData.vnClass, 0, PredicateMatrixFactories.classFactory);
			}
			vnsb.append(' ');
			Spanner.appendImage(vnsb, BaseModule.this.roleDrawable);
			vnsb.append(' ');
			if (vnData.vnRole != null && !vnData.vnRole.isEmpty())
			{
				Spanner.append(vnsb, vnData.vnRole, 0, PredicateMatrixFactories.roleFactory);
			}
			else
			{
				vnsb.append('∅');
			}

			vnsb.append(' ');
			Spanner.append(vnsb, vnData.toData(), 0, PredicateMatrixFactories.dataFactory);

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					vnsb.append(' ');
					vnsb.append('|');
				}
				vnsb.append(' ');
				Spanner.append(vnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return vnData.vnClassId == 0L ? TreeFactory.newLeafNode(vnsb, R.drawable.verbnet, BaseModule.this.context) : TreeFactory.newLinkLeafNode(vnsb, R.drawable.verbnet, new VnClassLink(vnData.vnClassId), BaseModule.this.context);
		}

		/**
		 * Make PropBank node
		 *
		 * @param pbData  PropBank data
		 * @param wnDatas WordNet data
		 * @return created node
		 */
		@NonNull
		TreeNode makePbNode(@NonNull final PbData pbData, @NonNull final WnData... wnDatas)
		{
			// pb
			final SpannableStringBuilder pbsb = new SpannableStringBuilder();
			if (pbData.pbRoleSet != null && !pbData.pbRoleSet.isEmpty())
			{
				Spanner.appendImage(pbsb, BaseModule.this.classDrawable);
				pbsb.append(' ');
				Spanner.append(pbsb, pbData.pbRoleSet, 0, PredicateMatrixFactories.classFactory);
			}
			pbsb.append(' ');
			Spanner.appendImage(pbsb, BaseModule.this.roleDrawable);
			pbsb.append(' ');
			if (pbData.pbRole != null && !pbData.pbRole.isEmpty())
			{
				Spanner.append(pbsb, pbData.pbRole, 0, PredicateMatrixFactories.roleFactory);
				pbsb.append(' ');
				if (pbData.pbRoleDescr != null)
				{
					pbsb.append('-');
					pbsb.append(' ');
					Spanner.append(pbsb, capitalize1(pbData.pbRoleDescr), 0, PredicateMatrixFactories.roleFactory);
				}
			}
			else
			{
				pbsb.append('∅');
			}

			pbsb.append(' ');
			Spanner.append(pbsb, pbData.toData(), 0, PredicateMatrixFactories.dataFactory);

			if (pbData.pbRoleSetDescr != null)
			{
				pbsb.append(' ');
				Spanner.append(pbsb, pbData.pbRoleSetDescr, 0, PredicateMatrixFactories.dataFactory);
			}

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					pbsb.append(' ');
					pbsb.append('|');
				}
				pbsb.append(' ');
				Spanner.append(pbsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return pbData.pbRoleSetId == 0L ? TreeFactory.newLeafNode(pbsb, R.drawable.propbank, BaseModule.this.context) : TreeFactory.newLinkLeafNode(pbsb, R.drawable.propbank, new PbRoleSetLink(pbData.pbRoleSetId), BaseModule.this.context);
		}

		/**
		 * Make FrameNet node
		 *
		 * @param fnData  FrameNet data
		 * @param wnDatas WordNet data
		 * @return created node
		 */
		@NonNull
		TreeNode makeFnNode(@NonNull final FnData fnData, @NonNull final WnData... wnDatas)
		{
			// fn
			final SpannableStringBuilder fnsb = new SpannableStringBuilder();
			if (fnData.fnFrame != null && !fnData.fnFrame.isEmpty())
			{
				Spanner.appendImage(fnsb, BaseModule.this.classDrawable);
				fnsb.append(' ');
				Spanner.append(fnsb, fnData.fnFrame, 0, PredicateMatrixFactories.classFactory);
			}
			fnsb.append(' ');
			Spanner.appendImage(fnsb, BaseModule.this.roleDrawable);
			fnsb.append(' ');
			if (fnData.fnFe != null && !fnData.fnFe.isEmpty())
			{
				Spanner.append(fnsb, fnData.fnFe, 0, PredicateMatrixFactories.roleFactory);
			}
			else
			{
				fnsb.append('∅');
			}

			fnsb.append(' ');
			Spanner.append(fnsb, fnData.toData(), 0, PredicateMatrixFactories.dataFactory);

			// wn
			boolean first = true;
			for (WnData wnData : wnDatas)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					fnsb.append(' ');
					fnsb.append('|');
				}
				fnsb.append(' ');
				Spanner.append(fnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
			}

			return fnData.fnFrameId == 0L ? TreeFactory.newLeafNode(fnsb, R.drawable.framenet, BaseModule.this.context) : TreeFactory.newLinkLeafNode(fnsb, R.drawable.framenet, new FnFrameLink(fnData.fnFrameId), BaseModule.this.context);
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
			Spanner.append(sb, "SEMLINK ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & SYNONYMS) != 0)
			Spanner.append(sb, "SYNONYMS ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & FRAME) != 0)
			Spanner.append(sb, "FRAME ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & FN_FE) != 0)
			Spanner.append(sb, "FN_FE", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & ADDED_FRAME_FN_ROLE) != 0)
			Spanner.append(sb, "ADDED_FRAME-FN_ROLE ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & FN_MAPPING) != 0)
			Spanner.append(sb, "FN_MAPPING ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & VN_MAPPING) != 0)
			Spanner.append(sb, "VN_MAPPING ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & PREDICATE_MAPPING) != 0)
			Spanner.append(sb, "PREDICATE_MAPPING ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & ROLE_MAPPING) != 0)
			Spanner.append(sb, "ROLE_MAPPING ", 0, PredicateMatrixFactories.dataFactory);
		if ((sources & WN_MISSING) != 0)
			Spanner.append(sb, "WN_MISSING ", 0, PredicateMatrixFactories.dataFactory);
		Spanner.append(sb, Long.toString(sources), 0, PredicateMatrixFactories.dataFactory);
	}
	
	*/
	/* @formatter:on */

	// Q U E R I E S

	/**
	 * Displayer grouping on synset
	 */
	class DisplayerBySynset extends Displayer
	{
		/**
		 * Grouping synset id
		 */
		private long synsetId = -1;

		/**
		 * Grouping node
		 */
		private TreeNode synsetNode;

		@Override
		public void display(@NonNull final TreeNode parentNode, @NonNull final WnData wnData, @NonNull final PmRow pmRole, @NonNull final VnData vnData, @NonNull final PbData pbData, @NonNull final FnData fnData)
		{
			if (this.synsetId != wnData.synsetId)
			{
				// record
				this.synsetId = wnData.synsetId;

				final SpannableStringBuilder synsetsb = new SpannableStringBuilder();
				if (this.synsetId != 0)
				{
					Spanner.append(synsetsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory);
					synsetsb.append(' ');
					Spanner.append(synsetsb, Long.toString(this.synsetId), 0, PredicateMatrixFactories.dataFactory);
				}
				else
				{
					synsetsb.append('∅');
				}

				// attach synset
				this.synsetNode = TreeFactory.addTreeNode(parentNode, synsetsb, R.drawable.synset, BaseModule.this.context);
			}
			super.displayRow(this.synsetNode, wnData, pmRole, vnData, pbData, fnData, false, false);
		}

		@NonNull
		@Override
		protected String getRequiredOrder()
		{
			return "synsetid";
		}

		@Override
		protected int getExpandLevels()
		{
			return 3;
		}
	}

	/**
	 * Displayer grouping on PredicateMatrix role
	 */
	class DisplayerByPmRole extends Displayer
	{
		/**
		 * Grouping PredicateMatrix role id
		 */
		private long pmRoleId = 0;

		/**
		 * Grouping node
		 */
		private TreeNode pmRoleNode;

		@Override
		public void display(@NonNull final TreeNode parentNode, final WnData wnData, @NonNull final PmRow pmRole, @NonNull final VnData vnData, @NonNull final PbData pbData, @NonNull final FnData fnData)
		{
			if (this.pmRoleId != pmRole.pmRoleId)
			{
				// group
				this.pmRoleNode = displayPmRole(parentNode, pmRole);

				// record
				this.pmRoleId = pmRole.pmRoleId;
			}

			super.displayRow(this.pmRoleNode, wnData, pmRole, vnData, pbData, fnData, true, false);
		}

		@NonNull
		@Override
		protected String getRequiredOrder()
		{
			return "pmroleid";
		}

		@Override
		protected int getExpandLevels()
		{
			return 2;
		}
	}

	/**
	 * Displayer not grouping rows
	 */
	class DisplayerUngrouped extends Displayer
	{
		@Override
		public void display(@NonNull final TreeNode parentNode, final WnData wnData, @NonNull final PmRow pmRole, @NonNull final VnData vnData, @NonNull final PbData pbData, @NonNull final FnData fnData)
		{
			super.displayRow(parentNode, wnData, pmRole, vnData, pbData, fnData, true, false);
		}

		@NonNull
		@Override
		protected String getRequiredOrder()
		{
			return "pmid";
		}

		@Override
		protected int getExpandLevels()
		{
			return 2;
		}
	}

	// R O L E S

	/**
	 * VerbNet class link data
	 */
	private class VnClassLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param classId class id
		 */
		VnClassLink(final long classId)
		{
			super(classId);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process()
		{
			final Parcelable pointer = new VnClassPointer(this.id);
			final Intent intent = new Intent(BaseModule.this.context, VnClassActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			assert BaseModule.this.context != null;
			BaseModule.this.context.startActivity(intent);
		}
	}

	/**
	 * PropBank role set link data
	 */
	private class PbRoleSetLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param roleSetId role set id
		 */
		PbRoleSetLink(final long roleSetId)
		{
			super(roleSetId);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process()
		{
			final Parcelable pointer = new PbRoleSetPointer(this.id);
			final Intent intent = new Intent(BaseModule.this.context, PbRoleSetActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			assert BaseModule.this.context != null;
			BaseModule.this.context.startActivity(intent);
		}
	}

	/**
	 * FrameNet frame link data
	 */
	private class FnFrameLink extends Link
	{
		/**
		 * Constructor
		 *
		 * @param frameId frame id
		 */
		FnFrameLink(final long frameId)
		{
			super(frameId);
		}

		@SuppressWarnings("boxing")
		@Override
		public void process()
		{
			final Parcelable pointer = new FnFramePointer(this.id);
			final Intent intent = new Intent(BaseModule.this.context, FnFrameActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			intent.setAction(ProviderArgs.ACTION_QUERY);

			assert BaseModule.this.context != null;
			BaseModule.this.context.startActivity(intent);
		}
	}

	// H E L P E R S

	/**
	 * Utility to capitalize first character
	 *
	 * @param s string
	 * @return string with capitalized first character
	 */
	private String capitalize1(@NonNull final String s)
	{
		return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
	}
}
