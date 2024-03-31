/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.loaders

import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.util.SparseArray
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.browser.FnFrameActivity
import org.sqlunet.model.TreeFactory.makeLeafNode
import org.sqlunet.model.TreeFactory.makeLinkLeafNode
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.predicatematrix.R
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromRoleId
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromWord
import org.sqlunet.predicatematrix.loaders.Queries.preparePmFromWordGrouped
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.Pm_X
import org.sqlunet.predicatematrix.provider.PredicateMatrixProvider
import org.sqlunet.predicatematrix.style.PredicateMatrixFactories
import org.sqlunet.propbank.PbRoleSetPointer
import org.sqlunet.propbank.browser.PbRoleSetActivity
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.verbnet.VnClassPointer
import org.sqlunet.verbnet.browser.VnClassActivity
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import java.util.TreeSet

/**
 * Base module for PredicateMatrix
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule(fragment: TreeFragment) : Module(fragment) {

    // resources

    /**
     * Drawable for roles
     */
    private val classDrawable: Drawable

    /**
     * Drawable for role
     */
    private val roleDrawable: Drawable

    // view models

    private lateinit var model: SqlunetViewTreeModel

    init {

        // models
        makeModels()

        // spanner
        val context = this@BaseModule.fragment.requireContext()
        classDrawable = getDrawable(context, R.drawable.roles)
        roleDrawable = getDrawable(context, R.drawable.role)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        model = ViewModelProvider(fragment)["pm.pm(?)", SqlunetViewTreeModel::class.java]
        model.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    // L O A D E R S

    /**
     * Load from word
     *
     * @param word      target word
     * @param parent    parent node
     * @param displayer displayer
     */
    fun fromWord(word: String?, parent: TreeNode, displayer: Displayer) {
        val sql = preparePmFromWord(word!!, displayer.requiredOrder)
        PmProcessOnIteration(parent, displayer).run(sql)
    }

    /**
     * Load from word
     *
     * @param word   word
     * @param parent parent node
     */
    fun fromWordGrouped(word: String?, parent: TreeNode) {
        val displayer: Displayer = DisplayerByPmRole()
        val sql = preparePmFromWordGrouped(word!!, displayer.requiredOrder)
        PmProcessGrouped(parent, displayer).run(sql)
    }

    /**
     * Load from role id
     *
     * @param pmRoleId  role id
     * @param parent    parent node
     * @param displayer displayer
     */
    fun fromRoleId(pmRoleId: Long, parent: TreeNode, displayer: Displayer) {
        val sql = preparePmFromRoleId(pmRoleId, displayer.requiredOrder)
        PmProcessOnIteration(parent, displayer).run(sql)
    }

    // D A T A

    /**
     * PredicateMatrix role
     *
     * @param pmPredicateId PredicateMatrix predicate id
     * @param pmRoleId      PredicateMatrix role id
     * @param pmPredicate   predicate
     * @param pmRole        PredicateMatrix role
     * @param pmPos         PredicateMatrix pos
     */
    open class PmRole(
        val pmPredicateId: Long,
        val pmRoleId: Long,
        private val pmPredicate: String,
        val pmRole: String?,
        val pmPos: String?,
    ) : Comparable<PmRole> {

        override fun compareTo(other: PmRole): Int {
            if (pmPos!![0] != other.pmPos!![0]) {
                return if (pmPos[0] > other.pmPos[0]) 1 else -1
            }
            if (pmPredicateId != other.pmPredicateId) {
                return if (pmPredicateId > other.pmPredicateId) 1 else -1
            }
            return if (pmRoleId != other.pmRoleId) {
                if (pmRoleId > other.pmRoleId) 1 else -1
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (other !is PmRole) {
                return false
            }
            return pmPos!![0] == other.pmPos!![0] && pmRoleId == other.pmRoleId && pmPredicateId == other.pmPredicateId
        }

        override fun hashCode(): Int {
            return (17 * pmPos!![0].code + 19 * pmRoleId + 3 * pmPredicateId).toInt()
        }

        override fun toString(): String {
            return toRoleString()
        }

        /**
         * Role data to string
         *
         * @return role data as string
         */
        fun toRoleString(): String {
            return (pmPos ?: "null") +
                    '-' +
                    pmPredicate + '-' +
                    (pmRole ?: "null")
        }

        /**
         * Role data to string
         *
         * @return role data as string
         */
        open fun toData(): String {
            return toRoleData()
        }

        /**
         * Role data to string
         *
         * @return role data as string
         */
        fun toRoleData(): String {
            return "$pmPos-$pmRoleId-$pmPredicateId"
        }
    }

    /**
     * PredicateMatrix row/role
     *
     * @param pmId          PredicateMatrix row id
     * @param pmPredicateId PredicateMatrix predicate id
     * @param pmRoleId      PredicateMatrix role id
     * @param pmPredicate   PredicateMatrix predicate
     * @param pmRole        PredicateMatrix role
     * @param pmPos         PredicateMatrix pos
     */
    class PmRow(
        private val pmId: Long,
        pmPredicateId: Long, pmRoleId: Long, pmPredicate: String, pmRole: String?, pmPos: String?,
    ) : PmRole(pmPredicateId, pmRoleId, pmPredicate, pmRole, pmPos) {

        override fun toString(): String {
            return '['.toString() + pmId.toString() + ']' + '-' + super.toString()
        }

        override fun toData(): String {
            return '['.toString() + pmId.toString() + ']' + '-' + super.toData()
        }
    }

    /**
     * WordNet data
     *
     * @param synsetId   synset id
     * @param definition definition
     */
    class WnData(val synsetId: Long, val definition: String) : Comparable<WnData> {

        override fun compareTo(other: WnData): Int {
            return if (synsetId != other.synsetId) {
                if (synsetId > other.synsetId) 1 else -1
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (other !is WnData) {
                return false
            }
            return synsetId == other.synsetId
        }

        override fun hashCode(): Int {
            return synsetId.toInt()
        }

        override fun toString(): String {
            return definition
        }
    }

    /**
     * VerbNet data
     *
     * @param vnClassId class id
     * @param vnRoleId  role id
     * @param vnClass   class
     * @param vnRole    role
     */
    class VnData(
        val vnClassId: Long,
        val vnRoleId: Long,
        val vnClass: String?,
        val vnRole: String?,
    ) : Comparable<VnData> {

        override fun compareTo(other: VnData): Int {
            if (vnClassId != other.vnClassId) {
                return if (vnClassId > other.vnClassId) 1 else -1
            }
            return if (vnRoleId != other.vnRoleId) {
                if (vnRoleId > other.vnRoleId) 1 else -1
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (other !is VnData) {
                return false
            }
            return vnClassId == other.vnClassId && vnRoleId == other.vnRoleId
        }

        override fun hashCode(): Int {
            return (19 * vnClassId + 13 * vnRoleId).toInt()
        }

        override fun toString(): String {
            return "$vnClass-$vnRole"
        }

        /**
         * Convert id data to string
         *
         * @return string
         */
        fun toData(): CharSequence {
            return "$vnClassId-$vnRoleId"
        }
    }

    /**
     * PropBank data
     *
     * @param pbRoleSetId    role set id
     * @param pbRoleId       role id
     * @param pbRoleSet      role set
     * @param pbRoleSetDescr role set description
     * @param pbRole         role
     * @param pbRoleDescr    role description
     */
    class PbData(
        val pbRoleSetId: Long,
        val pbRoleId: Long,
        val pbRoleSet: String?,
        val pbRoleSetDescr: String?,
        val pbRole: String?,
        val pbRoleDescr: String,
    ) : Comparable<PbData> {

        override fun compareTo(other: PbData): Int {
            if (pbRoleSetId != other.pbRoleSetId) {
                return if (pbRoleSetId > other.pbRoleSetId) 1 else -1
            }
            return if (pbRoleId != other.pbRoleId) {
                if (pbRoleId > other.pbRoleId) 1 else -1
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (other !is PbData) {
                return false
            }
            return pbRoleSetId == other.pbRoleSetId && pbRoleId == other.pbRoleId
        }

        override fun hashCode(): Int {
            return (23 * pbRoleSetId + 51 * pbRoleId).toInt()
        }

        override fun toString(): String {
            return "$pbRoleSet-$pbRole"
        }

        /**
         * Convert id data to string
         *
         * @return string
         */
        fun toData(): CharSequence {
            return "$pbRoleSetId-$pbRoleId"
        }
    }

    /**
     * FrameNet data
     *
     * @param fnFrameId Frame id
     * @param fnFeId    Frame element id
     * @param fnFrame   Frame
     * @param fnFe      Frame element
     */
    class FnData(
        /**
         * Frame id
         */
        val fnFrameId: Long,
        /**
         * Frame element id
         */
        val fnFeId: Long,
        /**
         * Frame
         */
        val fnFrame: String?,
        /**
         * Frame
         */
        val fnFe: String?,
    ) : Comparable<FnData> {

        override fun compareTo(other: FnData): Int {
            if (fnFrameId != other.fnFrameId) {
                return if (fnFrameId > other.fnFrameId) 1 else -1
            }
            return if (fnFeId != other.fnFeId) {
                if (fnFeId > other.fnFeId) 1 else -1
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (other !is FnData) {
                return false
            }
            return fnFrameId == other.fnFrameId && fnFeId == other.fnFeId
        }

        override fun hashCode(): Int {
            return (17 * fnFrameId + 7 * fnFeId).toInt()
        }

        override fun toString(): String {
            return "$fnFrame-$fnFe"
        }

        /**
         * Convert id data to string
         *
         * @return string
         */
        fun toData(): CharSequence {
            return "$fnFrameId-$fnFeId"
        }
    }

    // D I S P L A Y E R S

    /**
     * Abstract PredicateMatrix callbacks
     *
     * @param parent    parent node
     * @param displayer displayer
     */
    internal abstract inner class PmProcess(
        val parent: TreeNode,
        val displayer: Displayer,
    ) {

        /**
         * Changed list
         */
        protected val changedList: TreeOps = TreeOps(TreeOpCode.NEWTREE, parent)

        fun run(sql: ContentProviderSql) {
            val uri = Uri.parse(PredicateMatrixProvider.makeUri(sql.providerUri))
            model.loadData(uri, sql) { cursor: Cursor -> pmCursorToTreeModel(cursor, parent) }
        }

        private fun pmCursorToTreeModel(cursor: Cursor, @Suppress("UNUSED_PARAMETER") parent: TreeNode): Array<TreeOp> {
            if (cursor.moveToFirst()) {
                // column indices
                val idPmId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMID)
                val idPmRoleId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMROLEID)
                val idPmPredicateId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMPREDICATEID)
                val idPmPredicate = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMPREDICATE)
                val idPmRole = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMROLE)
                val idPmPos = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PMPOS)

                // val idWord = cursor.getColumnIndex(PredicateMatrix.WORD)
                val idSynsetId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.SYNSETID)
                val idDefinition = cursor.getColumnIndex(Pm_X.DEFINITION)
                val idVnClassId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.VNCLASSID)
                val idVnClass = cursor.getColumnIndex(Pm_X.VNCLASS)
                val idVnRoleTypeId = cursor.getColumnIndex(Pm_X.VNROLETYPEID)
                val idVnRoleType = cursor.getColumnIndex(Pm_X.VNROLETYPE)
                // val idVnRoleId = cursor.getColumnIndex(Pm_X.VNROLEID)
                val idPbRoleSetId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.PBROLESETID)
                val idPbRoleSet = cursor.getColumnIndex(Pm_X.PBROLESETNAME)
                val idPbRoleSetDescr = cursor.getColumnIndex(Pm_X.PBROLESETDESCR)
                val idPbRoleId = cursor.getColumnIndex(Pm_X.PBROLEID)
                val idPbRole = cursor.getColumnIndex(Pm_X.PBROLEARGTYPE)
                val idPbRoleDescr = cursor.getColumnIndex(Pm_X.PBROLEDESCR)
                // val idPbRoleSetHead = cursor.getColumnIndex(Pm_X.PBROLESETHEAD)
                // val idPbRoleArgType = cursor.getColumnIndex(Pm_X.PBROLEARGTYPE)
                val idFnFrameId = cursor.getColumnIndex(PredicateMatrixContract.PredicateMatrix.FNFRAMEID)
                val idFnFrame = cursor.getColumnIndex(Pm_X.FNFRAME)
                val idFnFeTypeId = cursor.getColumnIndex(Pm_X.FNFETYPEID)
                val idFnFeType = cursor.getColumnIndex(Pm_X.FNFETYPE)
                // val idFnFrameDefinition = cursor.getColumnIndex(Pm_X.FRAMEDEFINITION)
                // val idFnLexUnit = cursor.getColumnIndex(Pm_X.LEXUNIT)
                // val idFnLuDefinition = cursor.getColumnIndex(Pm_X.LUDEFINITION)
                // val idFnLuDict = cursor.getColumnIndex(Pm_X.LUDICT)
                // val idFnFeAbbrev = cursor.getColumnIndex(Pm_X.FEABBREV)
                // val idFnFeDefinition = cursor.getColumnIndex(Pm_X.FEDEFINITION)

                // read cursor
                do {
                    // data
                    val pmId = cursor.getLong(idPmId)
                    val pmRoleId = cursor.getLong(idPmRoleId)
                    val pmPredicateId = cursor.getLong(idPmPredicateId)
                    val pmPredicate = cursor.getString(idPmPredicate)
                    val pmRole = cursor.getString(idPmRole)
                    val pmPos = cursor.getString(idPmPos)
                    val pmRow = PmRow(pmId, pmPredicateId, pmRoleId, pmPredicate, pmRole, pmPos)

                    // var word = cursor.getString(idWord)
                    val synsetId = cursor.getLong(idSynsetId)
                    val definition = cursor.getString(idDefinition)
                    val wnData = if (synsetId != 0L) WnData(synsetId, definition) else null
                    val vnClassId = cursor.getLong(idVnClassId)
                    val vnRoleId = cursor.getLong(idVnRoleTypeId)
                    val vnClass = cursor.getString(idVnClass)
                    val vnRole = cursor.getString(idVnRoleType)
                    val vnData = if (vnClassId != 0L && vnRoleId != 0L) VnData(vnClassId, vnRoleId, vnClass, vnRole) else null
                    val pbRoleSetId = cursor.getLong(idPbRoleSetId)
                    val pbRoleId = cursor.getLong(idPbRoleId)
                    val pbRoleSet = cursor.getString(idPbRoleSet)
                    val pbRoleSetDescr = cursor.getString(idPbRoleSetDescr)
                    val pbRole = cursor.getString(idPbRole)
                    val pbRoleDescr = cursor.getString(idPbRoleDescr)
                    val pbData = if (pbRoleSetId != 0L && pbRoleId != 0L) PbData(pbRoleSetId, pbRoleId, pbRoleSet, pbRoleSetDescr, pbRole, pbRoleDescr) else null
                    val fnFrameId = cursor.getLong(idFnFrameId)
                    val fnFeId = cursor.getLong(idFnFeTypeId)
                    val fnFrame = cursor.getString(idFnFrame)
                    val fnFe = cursor.getString(idFnFeType)
                    val fnData = if (fnFrameId != 0L && fnFeId != 0L) FnData(fnFrameId, fnFeId, fnFrame, fnFe) else null

                    // process
                    process(this.parent, wnData, pmRow, vnData, pbData, fnData)
                } while (cursor.moveToNext())
                endProcess()
            }
            cursor.close()
            return changedList.toArray()
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
        protected abstract fun process(parent: TreeNode, wnData: WnData?, pmRow: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?)

        /**
         * End of processing
         */
        open fun endProcess() {}
    }

    /**
     * Processor of data based on grouping rows
     *
     * @param parent    parent
     * @param displayer displayer
     */
    internal inner class PmProcessGrouped(parent: TreeNode, displayer: Displayer) : PmProcess(parent, displayer) {

        /**
         * Role ids
         */
        private val pmRoleIds: MutableCollection<Int> = ArrayList()

        /**
         * Map role id to role
         */
        private val pm = SparseArray<PmRole>()

        /**
         * Map role id to VerbNet data
         */
        private val vnMap = SparseArray<MutableSet<VnData>>()

        /**
         * Map role id to PropBankNet data
         */
        private val pbMap = SparseArray<MutableSet<PbData>>()

        /**
         * Map role id to FrameNet data
         */
        private val fnMap = SparseArray<MutableSet<FnData>>()

        /**
         * Map VerbNet/PropBank/FrameNet data to WordNet data
         */
        private val wnMap: MutableMap<Any, MutableSet<WnData>> = HashMap()

        /**
         * Grouping role id
         */
        private var pmRoleId: Long = 0

        override fun process(parent: TreeNode, wnData: WnData?, pmRow: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?) {
            if (pmRoleId != pmRow.pmRoleId) {
                pmRoleIds.add(pmRow.pmRoleId.toInt())
                pm.append(pmRow.pmRoleId.toInt(), pmRow)
                pmRoleId = pmRow.pmRoleId
            }
            if (vnData != null) {
                var vnSet = vnMap[pmRoleId.toInt()]
                if (vnSet == null) {
                    vnSet = TreeSet()
                    vnMap.put(pmRoleId.toInt(), vnSet)
                }
                vnSet.add(vnData)

                // wn
                var wnSet = wnMap[vnData]
                if (wnSet == null) {
                    wnSet = HashSet()
                    wnMap[vnData] = wnSet
                }
                if (wnData != null) {
                    wnSet.add(wnData)
                }
            }
            if (pbData != null) {
                var pbSet = pbMap[pmRoleId.toInt()]
                if (pbSet == null) {
                    pbSet = TreeSet()
                    pbMap.put(pmRoleId.toInt(), pbSet)
                }
                pbSet.add(pbData)

                // wn
                var wnSet = wnMap[pbData]
                if (wnSet == null) {
                    wnSet = HashSet()
                    wnMap[pbData] = wnSet
                }
                if (wnData != null) {
                    wnSet.add(wnData)
                }
            }
            if (fnData != null) {
                var fnSet = fnMap[pmRoleId.toInt()]
                if (fnSet == null) {
                    fnSet = TreeSet()
                    fnMap.put(pmRoleId.toInt(), fnSet)
                }
                fnSet.add(fnData)

                // wn
                var wnSet = wnMap[fnData]
                if (wnSet == null) {
                    wnSet = HashSet()
                    wnMap[fnData] = wnSet
                }
                if (wnData != null) {
                    wnSet.add(wnData)
                }
            }
        }

        override fun endProcess() {
            for (pmRoleId in pmRoleIds) {
                val pmRole = pm[pmRoleId]
                val vnDatas: Set<VnData>? = vnMap[pmRoleId]
                val pbDatas: Set<PbData>? = pbMap[pmRoleId]
                val fnDatas: Set<FnData>? = fnMap[pmRoleId]
                val pmsb = SpannableStringBuilder()
                val pmroleNode = displayer.displayPmRole(parent, pmsb, pmRole, changedList)
                val aliases: MutableCollection<String?> = TreeSet()
                if (vnDatas != null) {
                    for (vnData in vnDatas) {
                        val wnData: Set<WnData> = wnMap[vnData]!!
                        displayer.makeVnNode(pmroleNode, changedList, vnData, *wnData.toTypedArray<WnData>())

                        // contribute to header
                        if (!vnData.vnRole.isNullOrEmpty()) {
                            aliases.add(vnData.vnRole)
                        }
                    }
                }
                if (pbDatas != null) {
                    for (pbData in pbDatas) {
                        val wnData: Set<WnData> = wnMap[pbData]!!
                        displayer.makePbNode(pmroleNode, changedList, pbData, *wnData.toTypedArray<WnData>())

                        // contribute to header
                        if (pbData.pbRoleDescr.isNotEmpty()) {
                            aliases.add(capitalize1(pbData.pbRoleDescr))
                        }
                    }
                }
                if (fnDatas != null) {
                    for (fnData in fnDatas) {
                        val wnData: Set<WnData> = wnMap[fnData]!!
                        displayer.makeFnNode(pmroleNode, changedList, fnData, *wnData.toTypedArray<WnData>())

                        // contribute to header
                        if (!fnData.fnFe.isNullOrEmpty()) {
                            aliases.add(fnData.fnFe)
                        }
                    }
                }

                // add aliases to header
                for (alias in aliases) {
                    pmsb.append(' ')
                    append(pmsb, alias, 0, PredicateMatrixFactories.roleAliasFactory)
                }
            }
        }
    }

    /**
     * Processor of data based on row iteration
     *
     * @param parent    parent
     * @param displayer displayer to use
     */
    internal inner class PmProcessOnIteration(parent: TreeNode, displayer: Displayer) : PmProcess(parent, displayer) {

        override fun process(parent: TreeNode, wnData: WnData?, pmRow: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?) {
            displayer.display(parent, wnData, pmRow, vnData, pbData, fnData, changedList)
        }
    }

    /**
     * Abstract displayer
     */
    abstract inner class Displayer {

        /**
         * Display
         *
         * @param parentNode  parent node
         * @param wnData      WordNet data
         * @param pmRole      PredicateMatrix row
         * @param vnData      VerbNet data
         * @param pbData      PropBank data
         * @param fnData      FrameNet data
         * @param changedList tree ops
         */
        abstract fun display(parentNode: TreeNode, wnData: WnData?, pmRole: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?, changedList: TreeOps)

        /**
         * Get required sort order
         *
         * @return sort order
         */
        abstract val requiredOrder: String

        protected abstract val expandLevels: Int

        /**
         * Utility to capitalize first character
         *
         * @param s string
         * @return string with capitalized first character
         */
        private fun capitalize1(s: String): CharSequence {
            return s.substring(0, 1).uppercase() + s.substring(1)
        }

        /**
         * Display PredicateMatrix row
         *
         * @param parent        parent node
         * @param wnData        WordNet data
         * @param pmRow         PredicateMatrix row
         * @param vnData        VerbNet data
         * @param pbData        PropBank data
         * @param fnData        FrameNet data
         * @param wnDataOnRow   whether to display WordNet data on label
         * @param wnDataOnXData whether to display WordNet data on extended data
         */
        fun displayRow(parent: TreeNode, wnData: WnData?, pmRow: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?, wnDataOnRow: Boolean, wnDataOnXData: Boolean, changedList: TreeOps) {
            // entry
            val roleNode = displayPmRow(parent, pmRow, if (wnDataOnRow) wnData else null, changedList)

            // vn
            if (vnData != null) {
                if (wnDataOnXData && wnData != null) {
                    makeVnNode(roleNode, changedList, vnData, wnData)
                } else {
                    makeVnNode(roleNode, changedList, vnData)
                }
            }

            // pb
            if (pbData != null) {
                if (wnDataOnXData && wnData != null) {
                    makePbNode(roleNode, changedList, pbData, wnData)
                } else {
                    makePbNode(roleNode, changedList, pbData)
                }
            }

            // fn
            if (fnData != null) {
                if (wnDataOnXData && wnData != null) {
                    makeFnNode(roleNode, changedList, fnData, wnData)
                } else {
                    makeFnNode(roleNode, changedList, fnData)
                }
            }
        }

        /**
         * Display PredicateMatrix role
         *
         * @param parentNode parent node
         * @param pmRole     PredicateMatrix role
         * @return created node
         */
        fun displayPmRole(parentNode: TreeNode, pmRole: PmRole, changedList: TreeOps): TreeNode {
            val pmsb = SpannableStringBuilder()
            return displayPmRole(parentNode, pmsb, pmRole, changedList)
        }

        /**
         * Display PredicateMatrix role
         *
         * @param parent parent node
         * @param pmRole PredicateMatrix role
         * @return created node
         */
        fun displayPmRole(parent: TreeNode, pmsb: SpannableStringBuilder, pmRole: PmRole, changedList: TreeOps): TreeNode {
            if (pmRole.pmRole != null) {
                val roleName = pmRole.toRoleString()
                append(pmsb, roleName, 0, PredicateMatrixFactories.groupFactory)
                pmsb.append(' ')
                val roleData = pmRole.toRoleData()
                append(pmsb, roleData, 0, PredicateMatrixFactories.dataFactory)
            }
            val result = makeTreeNode(pmsb, R.drawable.role, false).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, result)
            return result
        }

        /**
         * Display PredicateMatrix row
         *
         * @param parent parent node
         * @param pmRow  PredicateMatrix row
         * @param wnData WordNet data
         * @return created node
         */
        private fun displayPmRow(parent: TreeNode, pmRow: PmRow, wnData: WnData?, changedList: TreeOps): TreeNode {
            val pmsb = SpannableStringBuilder()
            // rolesb.append("predicate role ")
            if (pmRow.pmRole != null) {
                val rowName = pmRow.toRoleString()
                append(pmsb, rowName, 0, PredicateMatrixFactories.nameFactory)
            }
            pmsb.append(' ')
            append(pmsb, pmRow.toData(), 0, PredicateMatrixFactories.dataFactory)
            if (wnData != null) {
                pmsb.append(' ')
                append(pmsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory)
            }
            val result = makeTreeNode(pmsb, R.drawable.predicatematrix, false).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, result)
            return result
        }

        /**
         * Make VerbNet node
         *
         * @param parent  parent node
         * @param vnData  VerbNet data
         * @param wnDatas WordNet data
         * @return created node
         */
        fun makeVnNode(parent: TreeNode, changedList: TreeOps, vnData: VnData, vararg wnDatas: WnData): TreeNode {
            val vnsb = SpannableStringBuilder()
            if (!vnData.vnClass.isNullOrEmpty()) {
                appendImage(vnsb, classDrawable)
                vnsb.append(' ')
                append(vnsb, vnData.vnClass, 0, PredicateMatrixFactories.classFactory)
            }
            vnsb.append(' ')
            appendImage(vnsb, roleDrawable)
            vnsb.append(' ')
            if (!vnData.vnRole.isNullOrEmpty()) {
                append(vnsb, vnData.vnRole, 0, PredicateMatrixFactories.roleFactory)
            } else {
                vnsb.append('∅')
            }
            vnsb.append(' ')
            append(vnsb, vnData.toData(), 0, PredicateMatrixFactories.dataFactory)

            // wn
            var first = true
            for (wnData in wnDatas) {
                if (first) {
                    first = false
                } else {
                    vnsb.append(' ')
                    vnsb.append('|')
                }
                vnsb.append(' ')
                append(vnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory)
            }
            val result = if (vnData.vnClassId == 0L)
                makeLeafNode(vnsb, R.drawable.verbnet, false).addTo(parent) else
                makeLinkLeafNode(vnsb, R.drawable.verbnet, false, VnClassLink(vnData.vnClassId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, result)
            return result
        }

        /**
         * Make PropBank node
         *
         * @param parent  parent node
         * @param pbData  PropBank data
         * @param wnDatas WordNet data
         * @return created node
         */
        fun makePbNode(parent: TreeNode, changedList: TreeOps, pbData: PbData, vararg wnDatas: WnData): TreeNode {
            // pb
            val pbsb = SpannableStringBuilder()
            if (!pbData.pbRoleSet.isNullOrEmpty()) {
                appendImage(pbsb, classDrawable)
                pbsb.append(' ')
                append(pbsb, pbData.pbRoleSet, 0, PredicateMatrixFactories.classFactory)
            }
            pbsb.append(' ')
            appendImage(pbsb, roleDrawable)
            pbsb.append(' ')
            if (!pbData.pbRole.isNullOrEmpty()) {
                append(pbsb, pbData.pbRole, 0, PredicateMatrixFactories.roleFactory)
                pbsb.append(' ')
                pbsb.append('-')
                pbsb.append(' ')
                append(pbsb, capitalize1(pbData.pbRoleDescr), 0, PredicateMatrixFactories.roleFactory)
            } else {
                pbsb.append('∅')
            }
            pbsb.append(' ')
            append(pbsb, pbData.toData(), 0, PredicateMatrixFactories.dataFactory)
            if (pbData.pbRoleSetDescr != null) {
                pbsb.append(' ')
                append(pbsb, pbData.pbRoleSetDescr, 0, PredicateMatrixFactories.dataFactory)
            }

            // wn
            var first = true
            for (wnData in wnDatas) {
                if (first) {
                    first = false
                } else {
                    pbsb.append(' ')
                    pbsb.append('|')
                }
                pbsb.append(' ')
                append(pbsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory)
            }
            val result = if (pbData.pbRoleSetId == 0L)
                makeLeafNode(pbsb, R.drawable.propbank, false).addTo(parent) else
                makeLinkLeafNode(pbsb, R.drawable.propbank, false, PbRoleSetLink(pbData.pbRoleSetId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, result)
            return result
        }

        /**
         * Make FrameNet node
         *
         * @param parent  parent node
         * @param fnData  FrameNet data
         * @param wnDatas WordNet data
         * @return created node
         */
        fun makeFnNode(parent: TreeNode, changedList: TreeOps, fnData: FnData, vararg wnDatas: WnData): TreeNode {
            // fn
            val fnsb = SpannableStringBuilder()
            if (!fnData.fnFrame.isNullOrEmpty()) {
                appendImage(fnsb, classDrawable)
                fnsb.append(' ')
                append(fnsb, fnData.fnFrame, 0, PredicateMatrixFactories.classFactory)
            }
            fnsb.append(' ')
            appendImage(fnsb, roleDrawable)
            fnsb.append(' ')
            if (!fnData.fnFe.isNullOrEmpty()) {
                append(fnsb, fnData.fnFe, 0, PredicateMatrixFactories.roleFactory)
            } else {
                fnsb.append('∅')
            }
            fnsb.append(' ')
            append(fnsb, fnData.toData(), 0, PredicateMatrixFactories.dataFactory)

            // wn
            var first = true
            for (wnData in wnDatas) {
                if (first) {
                    first = false
                } else {
                    fnsb.append(' ')
                    fnsb.append('|')
                }
                fnsb.append(' ')
                append(fnsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory)
            }
            val result = if (fnData.fnFrameId == 0L)
                makeLeafNode(fnsb, R.drawable.framenet, false).addTo(parent) else
                makeLinkLeafNode(fnsb, R.drawable.framenet, false, FnFrameLink(fnData.fnFrameId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, result)
            return result
        }
    }

    /*
    fun parsePmSources(sb: SpannableStringBuilder, sources: Int) {
        if ((sources and SEMLINK) != 0)
            Spanner.append(sb, "SEMLINK ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and SYNONYMS) != 0)
            Spanner.append(sb, "SYNONYMS ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and FRAME) != 0)
            Spanner.append(sb, "FRAME ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and FN_FE) != 0)
            Spanner.append(sb, "FN_FE", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and ADDED_FRAME_FN_ROLE) != 0)
            Spanner.append(sb, "ADDED_FRAME-FN_ROLE ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and FN_MAPPING) != 0)
            Spanner.append(sb, "FN_MAPPING ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and VN_MAPPING) != 0)
            Spanner.append(sb, "VN_MAPPING ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and PREDICATE_MAPPING) != 0)
            Spanner.append(sb, "PREDICATE_MAPPING ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and ROLE_MAPPING) != 0)
            Spanner.append(sb, "ROLE_MAPPING ", 0, PredicateMatrixFactories.dataFactory)
        if ((sources and WN_MISSING) != 0)
            Spanner.append(sb, "WN_MISSING ", 0, PredicateMatrixFactories.dataFactory)
        Spanner.append(sb, sources.toString(), 0, PredicateMatrixFactories.dataFactory)
    }
    */

    // Q U E R I E S

    /**
     * Displayer grouping on synset
     */
    internal inner class DisplayerBySynset : Displayer() {

        /**
         * Grouping synset id
         */
        private var synsetId: Long = -1

        /**
         * Grouping node
         */
        private var synsetNode: TreeNode? = null

        override fun display(parentNode: TreeNode, wnData: WnData?, pmRole: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?, changedList: TreeOps) {
            if (wnData != null && synsetId != wnData.synsetId) {
                // record
                synsetId = wnData.synsetId
                val synsetsb = SpannableStringBuilder()
                if (synsetId != 0L) {
                    append(synsetsb, wnData.definition, 0, PredicateMatrixFactories.definitionFactory)
                    synsetsb.append(' ')
                    append(synsetsb, synsetId.toString(), 0, PredicateMatrixFactories.dataFactory)
                } else {
                    synsetsb.append('∅')
                }

                // attach synset
                synsetNode = makeTreeNode(synsetsb, R.drawable.synset, false).addTo(parentNode)
                changedList.add(TreeOpCode.NEWCHILD, synsetNode!!)
            }
            super.displayRow(synsetNode ?: parentNode, wnData, pmRole, vnData, pbData, fnData, wnDataOnRow = false, wnDataOnXData = false, changedList = changedList)
        }

        override val requiredOrder: String
            get() = "synsetid"

        override val expandLevels: Int
            get() = 3
    }

    /**
     * Displayer grouping on PredicateMatrix role
     */
    internal inner class DisplayerByPmRole : Displayer() {

        /**
         * Grouping PredicateMatrix role id
         */
        private var pmRoleId: Long = 0

        /**
         * Grouping node
         */
        private var pmRoleNode: TreeNode? = null

        override fun display(parentNode: TreeNode, wnData: WnData?, pmRole: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?, changedList: TreeOps) {
            if (pmRoleId != pmRole.pmRoleId) {
                // group
                pmRoleNode = displayPmRole(parentNode, pmRole, changedList)

                // record
                pmRoleId = pmRole.pmRoleId
            }
            super.displayRow(pmRoleNode!!, wnData, pmRole, vnData, pbData, fnData, wnDataOnRow = true, wnDataOnXData = false, changedList = changedList)
        }

        override val requiredOrder: String
            get() = "pmroleid"

        override val expandLevels: Int
            get() = 2
    }

    /**
     * Displayer not grouping rows
     */
    internal inner class DisplayerUngrouped : Displayer() {

        override fun display(parentNode: TreeNode, wnData: WnData?, pmRole: PmRow, vnData: VnData?, pbData: PbData?, fnData: FnData?, changedList: TreeOps) {
            super.displayRow(parentNode, wnData, pmRole, vnData, pbData, fnData, wnDataOnRow = true, wnDataOnXData = false, changedList = changedList)
        }

        override val requiredOrder: String
            get() = "pmid"

        override val expandLevels: Int
            get() = 2
    }

    // R O L E S

    /**
     * VerbNet class link data
     */
    private inner class VnClassLink(classId: Long) : Link(classId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = VnClassPointer(id)
            val intent = Intent(context, VnClassActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }
    }

    /**
     * PropBank role set link data
     */
    private inner class PbRoleSetLink(roleSetId: Long) : Link(roleSetId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = PbRoleSetPointer(id)
            val intent = Intent(context, PbRoleSetActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }
    }

    /**
     * FrameNet frame link data
     */
    private inner class FnFrameLink(frameId: Long) : Link(frameId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = FnFramePointer(id)
            val intent = Intent(context, FnFrameActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }
    }

    companion object {

        // sources

        /*
        const val SEMLINK = 0x1
        const val SYNONYMS = 0x2
        const val FRAME = 0x10
        const val FN_FE = 0x20
        const val ADDED_FRAME_FN_ROLE = 0x40 // ADDED_FRAME-FN_ROLE
        const val FN_MAPPING = 0x100
        const val VN_MAPPING = 0x200
        const val PREDICATE_MAPPING = 0x400
        const val ROLE_MAPPING = 0x800
        const val WN_MISSING = 0x1000
        */

        // H E L P E R S

        /**
         * Utility to capitalize first character
         *
         * @param s string
         * @return string with capitalized first character
         */
        private fun capitalize1(s: String): String {
            return s.substring(0, 1).uppercase() + s.substring(1)
        }
    }
}
