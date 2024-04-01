/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.FnSentencePointer
import org.sqlunet.framenet.R
import org.sqlunet.framenet.Utils
import org.sqlunet.framenet.browser.FnFrameActivity
import org.sqlunet.framenet.browser.FnLexUnitActivity
import org.sqlunet.framenet.browser.FnSentenceActivity
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSet
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForGovernor
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForPattern
import org.sqlunet.framenet.loaders.Queries.prepareAnnoSetsForValenceUnit
import org.sqlunet.framenet.loaders.Queries.prepareFesForFrame
import org.sqlunet.framenet.loaders.Queries.prepareFrame
import org.sqlunet.framenet.loaders.Queries.prepareGovernorsForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareGroupRealizationsForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareLayersForSentence
import org.sqlunet.framenet.loaders.Queries.prepareLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareLexUnitsForFrame
import org.sqlunet.framenet.loaders.Queries.prepareLexUnitsForWordAndPos
import org.sqlunet.framenet.loaders.Queries.prepareRealizationsForLexicalUnit
import org.sqlunet.framenet.loaders.Queries.prepareRelatedFrames
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForLexUnit
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForPattern
import org.sqlunet.framenet.loaders.Queries.prepareSentencesForValenceUnit
import org.sqlunet.framenet.provider.FrameNetContract.AnnoSets_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.Frames_FEs
import org.sqlunet.framenet.provider.FrameNetContract.Frames_Related
import org.sqlunet.framenet.provider.FrameNetContract.Frames_X
import org.sqlunet.framenet.provider.FrameNetContract.Governors_AnnoSets_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_FERealizations_ValenceUnits
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Governors
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_X
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.Patterns_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.Sentences_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Layers_X
import org.sqlunet.framenet.provider.FrameNetContract.ValenceUnits_Sentences
import org.sqlunet.framenet.provider.FrameNetContract.Words_LexUnits_Frames
import org.sqlunet.framenet.provider.FrameNetProvider
import org.sqlunet.framenet.style.FrameNetFactories
import org.sqlunet.framenet.style.FrameNetFrameProcessor
import org.sqlunet.framenet.style.FrameNetMarkupFactory
import org.sqlunet.framenet.style.FrameNetProcessor
import org.sqlunet.framenet.style.FrameNetSpanner
import org.sqlunet.model.TreeFactory.makeHotQueryNode
import org.sqlunet.model.TreeFactory.makeLinkNode
import org.sqlunet.model.TreeFactory.makeLinkTreeNode
import org.sqlunet.model.TreeFactory.makeQueryNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.style.Spanner.Companion.setSpan
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.control.Query
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import java.util.TreeMap

/**
 * Base framenet module
 *
 * @param fragment containing fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule internal constructor(fragment: TreeFragment) : Module(fragment) {
    /**
     * standAlone
     * FN standalone without WordNet
     * Queries vary slightly in that the fn_words table has the word string field,
     * In the global SqlUNet it is ot present but references should be made to the words table in wn.
     */

    // agents

    /**
     * Processor
     */
    private val processor: FrameNetProcessor

    /**
     * Frame Processor
     */
    private val frameProcessor: FrameNetFrameProcessor

    /**
     * Spanner
     */
    private val spanner: FrameNetSpanner?

    // resources

    private val frameLabel: String
    private val lexunitsLabel: String
    private val fesLabel: String
    private val relatedLabel: String
    private val realizationsLabel: String
    private val grouprealizationsLabel: String
    private val governorsLabel: String
    private val sentencesLabel: String

    /**
     * Drawable for frame
     */
    private val frameDrawable: Drawable

    /**
     * Drawable for FE
     */
    private val feDrawable: Drawable

    /**
     * Drawable for lexUnit
     */
    private val lexunitDrawable: Drawable

    /**
     * Drawable for definition
     */
    private val definitionDrawable: Drawable

    /**
     * Drawable for meta definition
     */
    private val metadefinitionDrawable: Drawable

    /**
     * Drawable for realization
     */
    private val realizationDrawable: Drawable

    /**
     * Drawable for sentence
     */
    private val sentenceDrawable: Drawable

    /**
     * Drawable for semtype
     */
    private val semtypeDrawable: Drawable

    /**
     * Drawable for coreset
     */
    private val coresetDrawable: Drawable

    /**
     * Drawable for layer
     */
    private val layerDrawable: Drawable

    // view models

    private lateinit var frameFromFrameIdModel: SqlunetViewTreeModel
    private lateinit var relatedFramesFromFrameIdModel: SqlunetViewTreeModel
    private lateinit var fesFromFrameIdModel: SqlunetViewTreeModel
    private lateinit var lexUnitFromLuIdModel: SqlunetViewTreeModel
    private lateinit var lexUnitsFromFrameIdModel: SqlunetViewTreeModel
    private lateinit var lexUnitsFromWordIdPosModel: SqlunetViewTreeModel
    private lateinit var governorsFromLuIdModel: SqlunetViewTreeModel
    private lateinit var realizationsFromLuIdModel: SqlunetViewTreeModel
    private lateinit var groupRealizationsFromLuIdModel: SqlunetViewTreeModel
    private lateinit var sentencesFromLuIdModel: SqlunetViewTreeModel
    private lateinit var sentencesFromPatternIdModel: SqlunetViewTreeModel
    private lateinit var sentencesFromVuIdModel: SqlunetViewTreeModel
    private lateinit var annoSetFromAnnoSetIdModel: SqlunetViewTreeModel
    private lateinit var annoSetsFromGovernorIdModel: SqlunetViewTreeModel
    private lateinit var annoSetsFromPatternIdModel: SqlunetViewTreeModel
    private lateinit var annoSetsFromVuIdModel: SqlunetViewTreeModel
    private lateinit var layersFromSentenceIdModel: SqlunetViewTreeModel

    /**
     * Make view models
     */
    private fun makeModels() {
        frameFromFrameIdModel = ViewModelProvider(fragment)["fn.frame(frameid)", SqlunetViewTreeModel::class.java]
        frameFromFrameIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        relatedFramesFromFrameIdModel = ViewModelProvider(fragment)["fn.relatedframes(frameid)", SqlunetViewTreeModel::class.java]
        relatedFramesFromFrameIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        fesFromFrameIdModel = ViewModelProvider(fragment)["fn.fes(frameid)", SqlunetViewTreeModel::class.java]
        fesFromFrameIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        lexUnitFromLuIdModel = ViewModelProvider(fragment)["fn.lexunit(luid)", SqlunetViewTreeModel::class.java]
        lexUnitFromLuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        lexUnitsFromFrameIdModel = ViewModelProvider(fragment)["fn.lexunits(frameid)", SqlunetViewTreeModel::class.java]
        lexUnitsFromFrameIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        lexUnitsFromWordIdPosModel = ViewModelProvider(fragment)["fn.lexunits(wordid,pos)", SqlunetViewTreeModel::class.java]
        lexUnitsFromWordIdPosModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        governorsFromLuIdModel = ViewModelProvider(fragment)["fn.governors(luid)", SqlunetViewTreeModel::class.java]
        governorsFromLuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        realizationsFromLuIdModel = ViewModelProvider(fragment)["fn.realizations(luid)", SqlunetViewTreeModel::class.java]
        realizationsFromLuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        groupRealizationsFromLuIdModel = ViewModelProvider(fragment)["fn.grouprealizations(luid)", SqlunetViewTreeModel::class.java]
        groupRealizationsFromLuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        sentencesFromLuIdModel = ViewModelProvider(fragment)["fn.sentences(luid)", SqlunetViewTreeModel::class.java]
        sentencesFromLuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        sentencesFromPatternIdModel = ViewModelProvider(fragment)["fn.sentences(patternid)", SqlunetViewTreeModel::class.java]
        sentencesFromPatternIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        sentencesFromVuIdModel = ViewModelProvider(fragment)["fn.sentences(vuid)", SqlunetViewTreeModel::class.java]
        sentencesFromVuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        annoSetFromAnnoSetIdModel = ViewModelProvider(fragment)["fn.annoset(annosetid)", SqlunetViewTreeModel::class.java]
        annoSetFromAnnoSetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        annoSetsFromGovernorIdModel = ViewModelProvider(fragment)["fn.annosets(governorid)", SqlunetViewTreeModel::class.java]
        annoSetsFromGovernorIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        annoSetsFromPatternIdModel = ViewModelProvider(fragment)["fn.annosets(patternid)", SqlunetViewTreeModel::class.java]
        annoSetsFromPatternIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        annoSetsFromVuIdModel = ViewModelProvider(fragment)["fn.annosets(vuid)", SqlunetViewTreeModel::class.java]
        annoSetsFromVuIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        layersFromSentenceIdModel = ViewModelProvider(fragment)["fn.layers(sentenceid)", SqlunetViewTreeModel::class.java]
        layersFromSentenceIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    // F R A M E

    /**
     * FrameNet frame
     *
     * @param frameId frame id
     * @param parent  parent node
     */
    fun frame(frameId: Long, parent: TreeNode) {
        val sql = prepareFrame(frameId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        frameFromFrameIdModel.loadData(uri, sql) { cursor: Cursor -> frameCursorToTreeModel(cursor, frameId, parent) }
    }

    private fun frameCursorToTreeModel(cursor: Cursor, frameId: Long, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()

            // column indices
            // var idFrameId = cursor.getColumnIndex(Frames_X.FRAMEID)
            val idFrame = cursor.getColumnIndex(Frames_X.FRAME)
            val idFrameDefinition = cursor.getColumnIndex(Frames_X.FRAMEDEFINITION)

            // data
            // var frameId = cursor.getInt(idFrameId)

            // frame
            appendImage(sb, frameDrawable)
            sb.append(' ')
            append(sb, cursor.getString(idFrame), 0, FrameNetFactories.frameFactory)
            if (VERBOSE) {
                sb.append(' ')
                sb.append(frameId.toString())
            }
            sb.append('\n')

            // definition
            appendImage(sb, metadefinitionDrawable)
            sb.append(' ')
            var frameDefinition = cursor.getString(idFrameDefinition)
            frameDefinition = frameDefinition.replace("\n*<ex></ex>\n*".toRegex(), "") // TODO remove in sqlunet database
            val frameDefinitionFields = processDefinition(frameDefinition, 0)
            sb.append(frameDefinitionFields[0])

            // examples in definition
            for (i in 1 until frameDefinitionFields.size) {
                sb.append('\n')
                sb.append(frameDefinitionFields[i])
            }

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)

            // sub nodes
            val fesNode = makeHotQueryNode(fesLabel, R.drawable.roles, false, FEsQuery(frameId)).addTo(parent)
            val lexUnitsNode = makeHotQueryNode(lexunitsLabel, R.drawable.members, false, LexUnitsQuery(frameId)).addTo(parent)
            val relatedNode = makeQueryNode(relatedLabel, R.drawable.roleclass, false, RelatedQuery(frameId)).addTo(parent)
            changed = seq(TreeOpCode.NEWMAIN, node, TreeOpCode.NEWEXTRA, fesNode, TreeOpCode.NEWEXTRA, lexUnitsNode, TreeOpCode.NEWEXTRA, relatedNode, TreeOpCode.NEWTREE, parent)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    init {

        // models
        makeModels()

        // drawables
        val context = this.fragment.requireContext()
        frameLabel = context.getString(R.string.framenet_frame_)
        lexunitsLabel = context.getString(R.string.framenet_lexunits_)
        fesLabel = context.getString(R.string.framenet_fes_)
        relatedLabel = context.getString(R.string.framenet_related_)
        realizationsLabel = context.getString(R.string.framenet_realizations_)
        grouprealizationsLabel = context.getString(R.string.framenet_grouprealizations_)
        governorsLabel = context.getString(R.string.framenet_governors_)
        sentencesLabel = context.getString(R.string.framenet_sentences_)
        frameDrawable = getDrawable(context, R.drawable.roleclass)
        feDrawable = getDrawable(context, R.drawable.role)
        lexunitDrawable = getDrawable(context, R.drawable.member)
        definitionDrawable = getDrawable(context, R.drawable.definition)
        metadefinitionDrawable = getDrawable(context, R.drawable.metadefinition)
        sentenceDrawable = getDrawable(context, R.drawable.sentence)
        realizationDrawable = getDrawable(context, R.drawable.realization)
        semtypeDrawable = getDrawable(context, R.drawable.semtype)
        coresetDrawable = getDrawable(context, R.drawable.coreset)
        layerDrawable = getDrawable(context, R.drawable.layer)

        // agents
        processor = FrameNetProcessor()
        frameProcessor = FrameNetFrameProcessor
        spanner = FrameNetSpanner(context)
    }

    /**
     * Related frames
     *
     * @param frameId frame id
     * @param parent  parent node
     */
    private fun relatedFrames(frameId: Long, parent: TreeNode) {
        val sql = prepareRelatedFrames(frameId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        relatedFramesFromFrameIdModel.loadData(uri, sql) { cursor: Cursor -> relatedFramesCursorToTreeModel(cursor, frameId, parent) }
    }

    private fun relatedFramesCursorToTreeModel(cursor: Cursor, frameId: Long, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            // val idFrameId = cursor.getColumnIndex(Frames_Related.FRAMEID)
            // val idFrame = cursor.getColumnIndex(Frames_Related.FRAME)
            // val idFrame2Id = cursor.getColumnIndex(Frames_Related.FRAME2ID)
            // val idFrame2 = cursor.getColumnIndex(Frames_Related.FRAME2)
            val idFrameId = cursor.getColumnIndex("i1")
            val idFrame = cursor.getColumnIndex("f1")
            val idFrame2Id = cursor.getColumnIndex("i2")
            val idFrame2 = cursor.getColumnIndex("f2")
            val idRelationId = cursor.getColumnIndex(Frames_Related.RELATIONID)
            // var idRelation = cursor.getColumnIndex(Frames_Related.RELATION)
            val relatedNodes: MutableMap<Int, TreeNode> = TreeMap()
            do {
                val sb: Editable = SpannableStringBuilder()

                // data
                val frame1Id = cursor.getInt(idFrameId)
                val frame2Id = cursor.getInt(idFrame2Id)
                val relationId = cursor.getInt(idRelationId)
                val frame1 = cursor.getString(idFrame)
                val frame2 = cursor.getString(idFrame2)
                // val relation = cursor.getString(idRelation)
                val gloss = FRAMERELATION_GLOSS[relationId - 1]

                // related
                if (VERBOSE) {
                    sb.append(relationId.toString())
                    sb.append(' ')
                }

                // slots
                val slot1 = frame1Id.toLong() == frameId
                val slot2 = frame2Id.toLong() == frameId

                // arg 1
                val sb1 = SpannableStringBuilder()
                if (slot1) {
                    sb1.append("it")
                } else {
                    append(sb1, frame1, 0, FrameNetFactories.frameFactory)
                    if (VERBOSE) {
                        sb1.append(' ')
                        sb1.append(frame1Id.toString())
                    }
                }

                // arg 2
                val sb2 = SpannableStringBuilder()
                if (slot2) {
                    sb2.append("it")
                } else {
                    append(sb2, frame2, 0, FrameNetFactories.frameFactory)
                    if (VERBOSE) {
                        sb2.append(' ')
                        sb2.append(frame2Id.toString())
                    }
                }

                // relation
                var position = gloss.indexOf("%s")
                val sbr = SpannableStringBuilder(gloss)
                sbr.replace(position, position + 2, sb1)
                position = sbr.toString().indexOf("%s")
                sbr.replace(position, position + 2, sb2)
                sb.append(sbr)

                // result
                val targetFrameId = (if (slot1) frame2Id else frame1Id).toLong()
                val memberNode = makeLinkNode(sb, R.drawable.roleclass, false, FnFrameLink(targetFrameId))
                relatedNodes[FRAMERELATION_RANK[relationId - 1]] = memberNode
            } while (cursor.moveToNext())
            for (relatedNode in relatedNodes.values) {
                relatedNode.addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, relatedNode)
            }
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // F E S

    /**
     * Frame elements for frame
     *
     * @param frameId frame id
     * @param parent  parent node
     */
    private fun fesForFrame(frameId: Int, parent: TreeNode) {
        val sql = prepareFesForFrame(frameId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        fesFromFrameIdModel.loadData(uri, sql) { cursor: Cursor -> fesCursorToTreeModel(cursor, parent) }
    }

    private fun fesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idFeType = cursor.getColumnIndex(Frames_FEs.FETYPE)
            val idFeAbbrev = cursor.getColumnIndex(Frames_FEs.FEABBREV)
            val idDefinition = cursor.getColumnIndex(Frames_FEs.FEDEFINITION)
            val idSemTypes = cursor.getColumnIndex(Frames_FEs.SEMTYPES)
            val idCoreset = cursor.getColumnIndex(Frames_FEs.CORESET)
            val idCoreTypeId = cursor.getColumnIndex(Frames_FEs.CORETYPEID)
            val idCoreType = cursor.getColumnIndex(Frames_FEs.CORETYPE)

            // read cursor
            do {
                val sb = SpannableStringBuilder()
                val feType = cursor.getString(idFeType)
                val feAbbrev = cursor.getString(idFeAbbrev)
                val feDefinition = cursor.getString(idDefinition).trim { it <= ' ' }.replace("\n+".toRegex(), "\n").replace("\n$".toRegex(), "")
                val feSemTypes = cursor.getString(idSemTypes)
                val isInCoreSet = !cursor.isNull(idCoreset)
                val coreTypeId = cursor.getInt(idCoreTypeId)
                val coreType = cursor.getString(idCoreType)

                // fe
                append(sb, feType, 0, FrameNetFactories.feFactory)
                sb.append(' ')
                append(sb, feAbbrev, 0, FrameNetFactories.feAbbrevFactory)

                // attach fe
                val feNode = makeTreeNode(sb, if (coreTypeId == 1) R.drawable.rolex else R.drawable.role, true).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, feNode)

                // more info
                val sb2 = SpannableStringBuilder()

                // fe definition
                val frameDefinitionFields = processDefinition(feDefinition, FrameNetMarkupFactory.FEDEF.toLong())
                sb2.append('\t')
                appendImage(sb2, metadefinitionDrawable)
                sb2.append(' ')
                sb2.append(frameDefinitionFields[0])
                if (frameDefinitionFields.size > 1) {
                    sb2.append('\n')
                    sb2.append('\t')
                    sb2.append(frameDefinitionFields[1])
                }

                // core type
                sb2.append('\n')
                sb2.append('\t')
                appendImage(sb2, coresetDrawable)
                sb2.append(' ')
                sb2.append(coreType)

                // coreset
                if (isInCoreSet) {
                    val coreset = cursor.getInt(idCoreset)
                    sb2.append('\n')
                    sb2.append('\t')
                    appendImage(sb2, coresetDrawable)
                    sb2.append("[coreset] ")
                    sb2.append(coreset.toString())
                }

                // sem types
                if (feSemTypes != null) {
                    sb2.append('\n')
                    sb2.append('\t')
                    appendImage(sb2, semtypeDrawable)
                    sb2.append(' ')
                    sb2.append(feSemTypes)
                }

                // attach extra node to fe node
                val extraNode = makeTextNode(sb2, false).addTo(feNode)
                changedList.add(TreeOpCode.NEWCHILD, extraNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // L E X U N I T S

    /**
     * Lex unit
     *
     * @param luId      lu id
     * @param parent    parent node
     * @param withFrame whether to include frames
     * @param withFes   whether to include frame elements
     */
    fun lexUnit(luId: Long, parent: TreeNode, withFrame: Boolean, withFes: Boolean) {
        val sql = prepareLexUnit(luId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        lexUnitFromLuIdModel.loadData(uri, sql) { cursor: Cursor -> lexUnitCursorToTreeModel(cursor, luId, parent, withFrame, withFes) }
    }

    private fun lexUnitCursorToTreeModel(cursor: Cursor, luId: Long, parent: TreeNode, withFrame: Boolean, withFes: Boolean): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val sb = SpannableStringBuilder()

            // column indices
            // var idLuId = cursor.getColumnIndex(LexUnits_X.LUID)
            val idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT)
            val idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION)
            val idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT)
            val idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE)
            val idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION)
            val idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID)
            val idFrame = cursor.getColumnIndex(LexUnits_X.FRAME)

            // data
            // var luId = cursor.getInt(idLuId)
            val definition = cursor.getString(idDefinition)
            val dictionary = cursor.getString(idDictionary)
            val incorporatedFEType = cursor.getString(idIncorporatedFEType)
            val incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition)
            val frameId = cursor.getInt(idFrameId)
            val frame = cursor.getString(idFrame)

            // lexUnit
            appendImage(sb, lexunitDrawable)
            sb.append(' ')
            append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory)
            if (VERBOSE) {
                sb.append(' ')
                sb.append(luId.toString())
            }

            // definition
            sb.append('\n')
            appendImage(sb, definitionDrawable)
            sb.append(' ')
            append(sb, definition.trim { it <= ' ' }, 0, FrameNetFactories.definitionFactory)
            sb.append(' ')
            sb.append('[')
            sb.append(dictionary)
            sb.append(']')

            // incorporated fe
            if (incorporatedFEType != null) {
                sb.append('\n')
                appendImage(sb, feDrawable)
                sb.append(' ')
                sb.append("Incorporated")
                sb.append(' ')
                append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory)
                if (incorporatedFEDefinition != null) {
                    sb.append(' ')
                    sb.append('-')
                    sb.append(' ')
                    val definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF.toLong())
                    sb.append(definitionFields[0])
                    // if (definitionFields.length > 1)
                    // {
                    // sb.append('\n')
                    // sb.append('\t')
                    // sb.append(definitionFields[1])
                    // }
                }
            }

            // with-frame option
            if (withFrame) {
                val sb2 = SpannableStringBuilder()
                sb2.append("Frame")
                sb2.append(' ')
                append(sb2, frame, 0, FrameNetFactories.boldFactory)
                val frameNode = makeHotQueryNode(sb2, R.drawable.roleclass, false, FrameQuery(frameId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, frameNode)
                if (withFes) {
                    val fesNode = makeQueryNode(fesLabel, R.drawable.roles, false, FEsQuery(frameId.toLong())).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, fesNode)
                }
            } else {
                val node = makeTextNode(sb, false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, node)
            }

            // sub nodes
            val realizationsNode = makeQueryNode(realizationsLabel, R.drawable.realization, false, RealizationsQuery(luId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, realizationsNode)
            val groupRealizationsNode = makeQueryNode(grouprealizationsLabel, R.drawable.grouprealization, false, GroupRealizationsQuery(luId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, groupRealizationsNode)
            val governorsNode = makeQueryNode(governorsLabel, R.drawable.governor, false, GovernorsQuery(luId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, governorsNode)
            val sentencesNode = makeQueryNode(sentencesLabel, R.drawable.sentence, false, SentencesForLexUnitQuery(luId)).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, sentencesNode)
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Lex units for frame
     *
     * @param frameId   frame id
     * @param parent    parent node
     * @param withFrame whether to include frame
     */
    private fun lexUnitsForFrame(frameId: Long, parent: TreeNode, withFrame: Boolean) {
        val sql = prepareLexUnitsForFrame(frameId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        lexUnitsFromFrameIdModel.loadData(uri, sql) { cursor: Cursor -> lexUnitsCursorToTreeModel(cursor, frameId, parent, withFrame) }
    }

    private fun lexUnitsCursorToTreeModel(cursor: Cursor, frameId: Long, parent: TreeNode, withFrame: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            // var idFrameId = cursor.getColumnIndex(LexUnits_X.FRAMEID);
            val idLuId = cursor.getColumnIndex(LexUnits_X.LUID)
            val idLexUnit = cursor.getColumnIndex(LexUnits_X.LEXUNIT)
            val idDefinition = cursor.getColumnIndex(LexUnits_X.LUDEFINITION)
            val idDictionary = cursor.getColumnIndex(LexUnits_X.LUDICT)
            val idIncorporatedFEType = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFETYPE)
            val idIncorporatedFEDefinition = cursor.getColumnIndex(LexUnits_X.INCORPORATEDFEDEFINITION)

            // data
            do {
                val sb = SpannableStringBuilder()

                // var frameId = cursor.getInt(idFrameId);
                val luId = cursor.getLong(idLuId)
                val lexUnit = cursor.getString(idLexUnit)
                val definition = cursor.getString(idDefinition)
                val dictionary = cursor.getString(idDictionary)
                val incorporatedFEType = cursor.getString(idIncorporatedFEType)
                val incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition)

                // lex unit
                append(sb, lexUnit, 0, FrameNetFactories.lexunitFactory)
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(luId.toString())
                }

                // attach lex unit
                val luNode = makeLinkTreeNode(sb, R.drawable.member, true, FnLexUnitLink(luId)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, luNode)

                // frame
                if (withFrame) {
                    val frameNode = makeQueryNode(frameLabel, R.drawable.roleclass, false, FrameQuery(frameId)).addTo(luNode)
                    changedList.add(TreeOpCode.NEWCHILD, frameNode)
                    val fesNode = makeQueryNode(fesLabel, R.drawable.roles, false, FEsQuery(frameId)).addTo(luNode)
                    changedList.add(TreeOpCode.NEWCHILD, fesNode)
                }

                // more info
                val sb2 = SpannableStringBuilder()

                // definition
                appendImage(sb2, definitionDrawable)
                sb2.append(' ')
                append(sb2, definition.trim { it <= ' ' }, 0, FrameNetFactories.definitionFactory)
                if (dictionary != null) {
                    sb2.append(' ')
                    sb2.append('[')
                    sb2.append(dictionary)
                    sb2.append(']')
                }

                // incorporated fe
                if (incorporatedFEType != null) {
                    sb2.append('\n')
                    appendImage(sb2, feDrawable)
                    sb2.append(' ')
                    sb2.append("Incorporated")
                    sb2.append(' ')
                    append(sb2, incorporatedFEType, 0, FrameNetFactories.fe2Factory)
                    if (incorporatedFEDefinition != null) {
                        sb2.append(' ')
                        sb2.append('-')
                        sb2.append(' ')
                        val definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF.toLong())
                        append(sb2, definitionFields[0], 0, FrameNetFactories.definitionFactory)
                        // if (definitionFields.length > 1)
                        // {
                        // sb.append('\n');
                        // sb.append('\t');
                        // sb.append(definitionFields[1]);
                        // }
                    }
                }

                // attach extra node to lex unit node
                val extraNode = makeTextNode(sb2, false).addTo(luNode)
                changedList.add(TreeOpCode.NEWCHILD, extraNode)

                // sub nodes
                val realizationsNode = makeQueryNode(realizationsLabel, R.drawable.realization, false, RealizationsQuery(luId)).addTo(luNode)
                changedList.add(TreeOpCode.NEWCHILD, realizationsNode)
                val groupRealizationsNode = makeQueryNode(grouprealizationsLabel, R.drawable.grouprealization, false, GroupRealizationsQuery(luId)).addTo(luNode)
                changedList.add(TreeOpCode.NEWCHILD, groupRealizationsNode)
                val governorsNode = makeQueryNode(governorsLabel, R.drawable.governor, false, GovernorsQuery(luId)).addTo(luNode)
                changedList.add(TreeOpCode.NEWCHILD, governorsNode)
                val sentencesNode = makeQueryNode(sentencesLabel, R.drawable.sentence, false, SentencesForLexUnitQuery(luId)).addTo(luNode)
                changedList.add(TreeOpCode.NEWCHILD, sentencesNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Lex units for word and pos
     *
     * @param wordId word id
     * @param pos    pos
     * @param parent parent node
     */
    fun lexUnitsForWordAndPos(wordId: Long, pos: Char?, parent: TreeNode) {
        val sql = prepareLexUnitsForWordAndPos(wordId, pos)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        lexUnitsFromWordIdPosModel.loadData(uri, sql) { cursor: Cursor -> lexUnitsFromWordIdPosCursorToTreeModel(cursor, parent) }
    }

    private fun lexUnitsFromWordIdPosCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idLuId = cursor.getColumnIndex(Words_LexUnits_Frames.LUID)
            val idLexUnit = cursor.getColumnIndex(Words_LexUnits_Frames.LEXUNIT)
            val idDefinition = cursor.getColumnIndex(Words_LexUnits_Frames.LUDEFINITION)
            val idDictionary = cursor.getColumnIndex(Words_LexUnits_Frames.LUDICT)
            val idFrameId = cursor.getColumnIndex(Words_LexUnits_Frames.FRAMEID)
            val idIncorporatedFEType = cursor.getColumnIndex(Words_LexUnits_Frames.INCORPORATEDFETYPE)
            val idIncorporatedFEDefinition = cursor.getColumnIndex(Words_LexUnits_Frames.INCORPORATEDFEDEFINITION)

            // data
            do {
                val sb = SpannableStringBuilder()
                val luId = cursor.getInt(idLuId)
                val frameId = cursor.getInt(idFrameId)
                val definition = cursor.getString(idDefinition)
                val dictionary = cursor.getString(idDictionary)
                val incorporatedFEType = cursor.getString(idIncorporatedFEType)
                val incorporatedFEDefinition = cursor.getString(idIncorporatedFEDefinition)

                // lex unit
                appendImage(sb, lexunitDrawable)
                sb.append(' ')
                append(sb, cursor.getString(idLexUnit), 0, FrameNetFactories.lexunitFactory)
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(luId.toString())
                }

                // definition
                sb.append('\n')
                appendImage(sb, definitionDrawable)
                sb.append(' ')
                append(sb, definition.trim { it <= ' ' }, 0, FrameNetFactories.definitionFactory)
                if (dictionary != null) {
                    sb.append(' ')
                    sb.append('[')
                    sb.append(dictionary)
                    sb.append(']')
                }

                // incorporated FE
                if (incorporatedFEType != null) {
                    sb.append('\n')
                    appendImage(sb, feDrawable)
                    sb.append(' ')
                    sb.append("Incorporated")
                    sb.append(' ')
                    append(sb, incorporatedFEType, 0, FrameNetFactories.fe2Factory)
                    if (incorporatedFEDefinition != null) {
                        sb.append(' ')
                        sb.append('-')
                        sb.append(' ')
                        val definitionFields = processDefinition(incorporatedFEDefinition, FrameNetMarkupFactory.FEDEF.toLong())
                        sb.append(definitionFields[0])
                        // if (definitionFields.length > 1)
                        // {
                        // sb.append('\n');
                        // sb.append('\t');
                        // sb.append(definitionFields[1]);
                        // }
                    }
                }
                // attach result
                val node = makeTextNode(sb, false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, node)

                // sub nodes
                val frameNode = makeHotQueryNode(frameLabel, R.drawable.roleclass, false, FrameQuery(frameId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, frameNode)
                val fesNode = makeQueryNode(fesLabel, R.drawable.roles, false, FEsQuery(frameId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, fesNode)
                val realizationsNode = makeQueryNode(realizationsLabel, R.drawable.realization, false, RealizationsQuery(luId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, realizationsNode)
                val groupRealizationsNode = makeQueryNode(grouprealizationsLabel, R.drawable.grouprealization, false, GroupRealizationsQuery(luId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, groupRealizationsNode)
                val governorsNode = makeQueryNode(governorsLabel, R.drawable.governor, false, GovernorsQuery(luId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, governorsNode)
                val sentencesNode = makeQueryNode(sentencesLabel, R.drawable.sentence, false, SentencesForLexUnitQuery(luId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, sentencesNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // G O V E R N O R S

    /**
     * Governors for lex unit
     *
     * @param luId   lex unit id
     * @param parent parent node
     */
    private fun governorsForLexUnit(luId: Long, parent: TreeNode) {
        val sql = prepareGovernorsForLexUnit(luId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        governorsFromLuIdModel.loadData(uri, sql) { cursor: Cursor -> governorsCursorToTreeModel(cursor, parent) }
    }

    private fun governorsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idGovernorId = cursor.getColumnIndex(LexUnits_Governors.GOVERNORID)
            val idGovernorType = cursor.getColumnIndex(LexUnits_Governors.GOVERNORTYPE)
            val idWord = cursor.getColumnIndex(LexUnits_Governors.WORD)
            val idWordId = cursor.getColumnIndex(LexUnits_Governors.FNWORDID)

            // data
            do {
                val sb = SpannableStringBuilder()

                // data
                val governorId = cursor.getLong(idGovernorId)
                val governorType = cursor.getString(idGovernorType)
                val word = cursor.getString(idWord)

                // type
                append(sb, governorType, 0, FrameNetFactories.governorTypeFactory)
                sb.append(' ')
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(governorId.toString())
                    sb.append(' ')
                    sb.append(cursor.getInt(idWordId).toString())
                }
                sb.append(' ')
                append(sb, word, 0, FrameNetFactories.governorFactory)

                // attach annoSets node
                val annoSetsNode = makeQueryNode(sb, R.drawable.governor, false, AnnoSetsForGovernorQuery(governorId)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, annoSetsNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // R E A L I Z A T I O N S

    /**
     * Realizations for lex unit
     *
     * @param luId   lex unit id
     * @param parent parent node
     */
    private fun realizationsForLexicalUnit(luId: Long, parent: TreeNode) {
        val sql = prepareRealizationsForLexicalUnit(luId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        realizationsFromLuIdModel.loadData(uri, sql) { cursor: Cursor -> realizationsCursorToTreeModel(cursor, parent) }
    }

    private fun realizationsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idFerId = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FERID)
            val idFeType = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FETYPE)
            val idFers = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.FERS)
            val idTotal = cursor.getColumnIndex(LexUnits_FERealizations_ValenceUnits.TOTAL)

            // data
            do {
                val sb = SpannableStringBuilder()

                // realization
                append(sb, cursor.getString(idFeType), 0, FrameNetFactories.feFactory)
                sb.append(' ')
                sb.append("[annotated] ")
                sb.append(cursor.getInt(idTotal).toString())
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(cursor.getString(idFerId))
                }

                // fe
                val feNode = makeTreeNode(sb, R.drawable.role, false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, feNode)

                // fe realizations
                val fers = cursor.getString(idFers)
                for (fer in fers.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    // pt:gf:valenceUnit id

                    // valenceUnit id
                    var vuId: Long = -1
                    val fields = fer.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (fields.size > 2) {
                        vuId = fields[2].toLong()
                    }
                    val sb1 = SpannableStringBuilder()

                    // pt
                    if (fields.isNotEmpty()) {
                        append(sb1, processPT(fields[0]), 0, FrameNetFactories.ptFactory)
                        sb1.append(' ')
                    }
                    // gf
                    if (fields.size > 1) {
                        sb1.append(' ')
                        append(sb1, fields[1], 0, FrameNetFactories.gfFactory)
                    }
                    if (VERBOSE) {
                        sb.append(' ')
                        sb.append(vuId.toString())
                    }

                    // attach fer node
                    val ferNode = makeQueryNode(sb1, R.drawable.realization, false, SentencesForValenceUnitQuery(vuId)).addTo(feNode)
                    changedList.add(TreeOpCode.NEWCHILD, ferNode)
                }
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Group realizations for lex unit
     *
     * @param luId   lex unit id
     * @param parent parent node
     */
    private fun groupRealizationsForLexUnit(luId: Long, parent: TreeNode) {
        val sql = prepareGroupRealizationsForLexUnit(luId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        groupRealizationsFromLuIdModel.loadData(uri, sql) { cursor: Cursor -> groupRealizationsCursorToTreeModel(cursor, parent) }
    }

    private fun groupRealizationsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idFEGRId = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID)
            val idGroupRealizations = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS)
            val idPatternId = cursor.getColumnIndex(LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PATTERNID)

            // data
            var groupNumber = 0
            var groupId: Long = -1
            var groupNode: TreeNode? = null
            do {
                val sb = SpannableStringBuilder()

                // data
                val feGroupId = cursor.getLong(idFEGRId)
                val groupRealizations = cursor.getString(idGroupRealizations)
                val patternId = cursor.getInt(idPatternId)
                // Log.d(TAG, "Group realizations: " + feGroupId + ' ' + groupRealizations + ' ' + patternId)
                if (groupRealizations == null) {
                    continue
                }

                // group
                if (groupId != feGroupId) {
                    val sb1: Editable = SpannableStringBuilder()
                    sb1.append("group")
                    sb1.append(' ')
                    sb1.append((++groupNumber).toString())
                    if (VERBOSE) {
                        sb1.append(' ')
                        sb1.append(feGroupId.toString())
                    }
                    groupId = feGroupId
                    groupNode = makeTreeNode(sb1, R.drawable.grouprealization, false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, groupNode)
                }

                // group realization
                parseGroupRealizations(groupRealizations, sb)

                // attach sentences node
                val sentencesNode = makeQueryNode(sb, R.drawable.grouprealization, false, SentencesForPatternQuery(patternId.toLong())).addTo(groupNode!!)
                changedList.add(TreeOpCode.NEWCHILD, sentencesNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Parse group realizations
     *
     * @param aggregate aggregate to parse
     * @param sb        builder to host result
     * @return builder
     */
    private fun parseGroupRealizations(aggregate: String, sb: SpannableStringBuilder): CharSequence {
        // fe.pt.gf,fe.pt.gf,...
        val groupRealizations = aggregate.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var first = true
        for (groupRealization in groupRealizations) {
            if (first) {
                first = false
            } else {
                sb.append('\n')
            }
            appendImage(sb, realizationDrawable)

            // fe.pt.gf
            val components = groupRealization.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (components.isNotEmpty()) {
                append(sb, components[0], 0, FrameNetFactories.feFactory)
            }
            sb.append(' ')
            if (components.size > 1) {
                // pt
                append(sb, processPT(components[1]), 0, FrameNetFactories.ptFactory)
            }
            sb.append(' ')
            if (components.size > 2) {
                // gf
                append(sb, components[2], 0, FrameNetFactories.gfFactory)
            }
        }
        return sb
    }

    // S E N T E N C E S

    /**
     * Sentences for lex unit
     *
     * @param luId   lex unit id
     * @param parent parent node
     */
    private fun sentencesForLexUnit(luId: Long, parent: TreeNode) {
        val sql = prepareSentencesForLexUnit(luId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        sentencesFromLuIdModel.loadData(uri, sql) { cursor: Cursor -> sentencesCursor1ToTreeModel(cursor, parent) }
    }

    private fun sentencesCursor1ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idText = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT)
            val idLayerType = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE)
            val idAnnotations = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION)
            val idSentenceId = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID)

            // read cursor
            do {
                val sb = SpannableStringBuilder()
                val text = cursor.getString(idText)
                val layerType = cursor.getString(idLayerType)
                val annotations = cursor.getString(idAnnotations)
                val sentenceId = cursor.getLong(idSentenceId)

                // sentence text
                val sentenceStart = sb.length
                append(sb, text, 0, FrameNetFactories.sentenceFactory)
                if (VERBOSE) {
                    sb.append(sentenceId.toString())
                    sb.append(' ')
                }

                // labels
                val labels = Utils.parseLabels(annotations)
                if (labels != null) {
                    for (label in labels) {
                        sb.append('\n')

                        // segment
                        var subtext: String
                        val from = label.from.toInt()
                        val to = label.to.toInt() + 1
                        val len = text.length
                        subtext = if (from < 0 || to > len || from > to) {
                            val idAnnoSetId = cursor.getColumnIndex(LexUnits_Sentences_AnnoSets_Layers_Labels.ANNOSETID)
                            val annoSetId = cursor.getLong(idAnnoSetId)
                            Log.d(TAG, "annoSetId=" + annoSetId + "annotations=" + annotations + "label=" + label + "text=" + text)
                            label.toString() + " ERROR [" + label.from + ',' + label.to + ']'
                        } else {
                            text.substring(from, to)
                        }

                        // span text
                        setSpan(sb, sentenceStart + from, sentenceStart + to, 0, if ("Target" == layerType) FrameNetFactories.targetHighlightTextFactory else FrameNetFactories.highlightTextFactory)

                        // label
                        sb.append('\t')
                        append(sb, label.label, 0, if ("FE" == layerType) FrameNetFactories.feFactory else FrameNetFactories.labelFactory)
                        sb.append(' ')

                        // subtext value
                        val p = sb.length
                        append(sb, subtext, 0, FrameNetFactories.subtextFactory)

                        // value colors
                        if (label.bgColor != null) {
                            val color = label.bgColor.toInt(16)
                            sb.setSpan(BackgroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (label.fgColor != null) {
                            val color = label.fgColor.toInt(16)
                            sb.setSpan(ForegroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }

                // attach result
                val sentenceNode = makeLinkNode(sb, R.drawable.sentence, false, FnSentenceLink(sentenceId)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, sentenceNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Sentences for pattern
     *
     * @param patternId pattern id
     * @param parent    parent node
     */
    private fun sentencesForPattern(patternId: Long, parent: TreeNode) {
        val sql = prepareSentencesForPattern(patternId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        sentencesFromPatternIdModel.loadData(uri, sql) { cursor: Cursor -> sentencesCursor2ToTreeModel(cursor, parent) }
    }

    private fun sentencesCursor2ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idAnnotationId = cursor.getColumnIndex(Patterns_Sentences.ANNOSETID)
            val idText = cursor.getColumnIndex(Patterns_Sentences.TEXT)
            // var idSentenceId = cursor.getColumnIndex(Patterns_Sentences.SENTENCEID);

            // read cursor
            do {
                val sb = SpannableStringBuilder()
                val annotationId = cursor.getLong(idAnnotationId)
                val text = cursor.getString(idText)
                // var sentenceId = cursor.getLong(idSentenceId);

                // sentence
                append(sb, text, 0, FrameNetFactories.sentenceFactory)

                // annotation id
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(annotationId.toString())
                }

                // attach annoSet node
                val annoSetNode = makeQueryNode(sb, R.drawable.sentence, false, AnnoSetQuery(annotationId, false)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, annoSetNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Sentences for valence unit
     *
     * @param vuId   valence unit id
     * @param parent parent node
     */
    private fun sentencesForValenceUnit(vuId: Long, parent: TreeNode) {
        val sql = prepareSentencesForValenceUnit(vuId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        sentencesFromVuIdModel.loadData(uri, sql) { cursor: Cursor -> sentencesCursor3ToTreeModel(cursor, parent) }
    }

    private fun sentencesCursor3ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idAnnotationId = cursor.getColumnIndex(ValenceUnits_Sentences.ANNOSETID)
            val idText = cursor.getColumnIndex(ValenceUnits_Sentences.TEXT)
            // val idSentenceId = cursor.getColumnIndex(Patterns_Sentences.SENTENCEID)

            // read cursor
            do {
                val sb = SpannableStringBuilder()
                val annotationId = cursor.getLong(idAnnotationId)
                val text = cursor.getString(idText)
                // val sentenceId = cursor.getLong(idSentenceId)

                // sentence
                append(sb, text, 0, FrameNetFactories.sentenceFactory)

                // annotation id
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append(annotationId.toString())
                }

                // pattern
                val annoSetNode = makeQueryNode(sb, R.drawable.sentence, false, AnnoSetQuery(annotationId, false)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, annoSetNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // A N N O S E T S

    /**
     * AnnoSet
     *
     * @param annoSetId    annoSetId
     * @param parent       parent node
     * @param withSentence whether to include sentence
     */
    fun annoSet(annoSetId: Long, parent: TreeNode, withSentence: Boolean) {
        val sql = prepareAnnoSet(annoSetId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        annoSetFromAnnoSetIdModel.loadData(uri, sql) { cursor: Cursor -> annoSetCursorToTreeModel(cursor, parent, withSentence) }
    }

    private fun annoSetCursorToTreeModel(cursor: Cursor, parent: TreeNode, withSentence: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        val sb = SpannableStringBuilder()
        if (cursor.moveToFirst()) {
            // column indices
            val idLayerType = cursor.getColumnIndex(AnnoSets_Layers_X.LAYERTYPE)
            val idAnnotations = cursor.getColumnIndex(AnnoSets_Layers_X.LAYERANNOTATIONS)
            val idSentenceText = cursor.getColumnIndex(AnnoSets_Layers_X.SENTENCETEXT)
            val idSentenceId = cursor.getColumnIndex(AnnoSets_Layers_X.SENTENCEID)
            val idRank = cursor.getColumnIndex(AnnoSets_Layers_X.RANK)

            // read cursor
            var first = true
            while (true) {
                val layerType = cursor.getString(idLayerType)
                val annotations = cursor.getString(idAnnotations)
                val sentenceText = cursor.getString(idSentenceText)
                val isTarget = "Target" == layerType
                val isFE = "FE" == layerType

                // sentence
                if (withSentence && first) {
                    appendImage(sb, sentenceDrawable)
                    sb.append(' ')
                    append(sb, sentenceText, 0, FrameNetFactories.sentenceFactory)
                    if (VERBOSE) {
                        val sentenceId = cursor.getLong(idSentenceId)
                        sb.append(' ')
                        sb.append(sentenceId.toString())
                    }
                    sb.append('\n')
                    first = false
                }

                // layer
                appendImage(sb, layerDrawable)
                sb.append(' ')
                append(sb, processLayer(layerType), 0, if (isTarget) FrameNetFactories.targetFactory else FrameNetFactories.layerTypeFactory)
                if (VERBOSE) {
                    val rank = cursor.getString(idRank)
                    sb.append(' ')
                    sb.append('[')
                    sb.append(rank)
                    sb.append(']')
                }

                // annotations
                val labels = Utils.parseLabels(annotations)
                if (labels != null) {
                    for (label in labels) {
                        sb.append('\n')
                        sb.append('\t')
                        sb.append('\t')

                        // label
                        append(sb, label.label, 0, if (isFE) FrameNetFactories.feFactory else FrameNetFactories.labelFactory)
                        sb.append(' ')

                        // subtext value
                        var subtext: String
                        val from = label.from.toInt()
                        val to = label.to.toInt() + 1
                        val len = sentenceText.length
                        subtext = if (from < 0 || to > len || from > to) {
                            Log.d(TAG, "annotations=" + annotations + "label=" + label + "text=" + sentenceText)
                            label.toString() + " ERROR [" + label.from + ',' + label.to + ']'
                        } else {
                            sentenceText.substring(from, to)
                        }
                        val p = sb.length
                        append(sb, subtext, 0, FrameNetFactories.subtextFactory)

                        // value color
                        if (label.bgColor != null) {
                            val color = label.bgColor.toInt(16)
                            sb.setSpan(BackgroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (label.fgColor != null) {
                            val color = label.fgColor.toInt(16)
                            sb.setSpan(ForegroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
                if (!cursor.moveToNext()) {
                    break
                }
                sb.append('\n')
            }

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)
            changed = seq(TreeOpCode.NEWUNIQUE, node)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * AnnoSets for governor
     *
     * @param governorId governor id
     * @param parent     parent id
     */
    private fun annoSetsForGovernor(governorId: Long, parent: TreeNode) {
        val sql = prepareAnnoSetsForGovernor(governorId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        annoSetsFromGovernorIdModel.loadData(uri, sql) { cursor: Cursor -> annoSetsCursor1ToTreeModel(cursor, parent) }
    }

    private fun annoSetsCursor1ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idSentenceId = cursor.getColumnIndex(Governors_AnnoSets_Sentences.SENTENCEID)
            val idText = cursor.getColumnIndex(Governors_AnnoSets_Sentences.TEXT)
            val idAnnoSetId = cursor.getColumnIndex(Governors_AnnoSets_Sentences.ANNOSETID)

            // data
            do {
                val sb = SpannableStringBuilder()

                // data
                val text = cursor.getString(idText)
                val annoSetId = cursor.getLong(idAnnoSetId)

                // sentence
                append(sb, text, 0, FrameNetFactories.sentenceFactory)
                sb.append(' ')
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append("sentenceid=")
                    sb.append(cursor.getString(idSentenceId))
                    sb.append(' ')
                    sb.append("annosetid=")
                    sb.append(annoSetId.toString())
                }

                // attach annoSet node
                val annoSetNode = makeQueryNode(sb, R.drawable.annoset, false, AnnoSetQuery(annoSetId, false)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, annoSetNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * AnnoSets for pattern
     *
     * @param patternId pattern id
     * @param parent    parent node
     */
    fun annoSetsForPattern(patternId: Long, parent: TreeNode) {
        val sql = prepareAnnoSetsForPattern(patternId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        annoSetsFromPatternIdModel.loadData(uri, sql) { cursor: Cursor -> annoSetsCursor2ToTreeModel(cursor, parent) }
    }

    private fun annoSetsCursor2ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        // column indices
        val idLayerType = cursor.getColumnIndex(Patterns_Layers_X.LAYERTYPE)
        val idRank = cursor.getColumnIndex(Patterns_Layers_X.RANK)
        val idAnnotations = cursor.getColumnIndex(Patterns_Layers_X.LAYERANNOTATIONS)
        val idAnnoSetId = cursor.getColumnIndex(Patterns_Layers_X.ANNOSETID)
        val idSentenceText = cursor.getColumnIndex(Patterns_Layers_X.SENTENCETEXT)
        val changed = annoSets(parent, cursor, null, idSentenceText, idLayerType, idRank, idAnnotations, idAnnoSetId)
        cursor.close()
        return changed
    }

    /**
     * AnnoSets for valence unit
     *
     * @param vuId   valence unit id
     * @param parent parent node
     */
    fun annoSetsForValenceUnit(vuId: Long, parent: TreeNode) {
        val sql = prepareAnnoSetsForValenceUnit(vuId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        annoSetsFromVuIdModel.loadData(uri, sql) { cursor: Cursor -> annoSetsCursor3ToTreeModel(cursor, parent) }
    }

    private fun annoSetsCursor3ToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        // column indices
        val idLayerType = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERTYPE)
        val idRank = cursor.getColumnIndex(ValenceUnits_Layers_X.RANK)
        val idAnnotations = cursor.getColumnIndex(ValenceUnits_Layers_X.LAYERANNOTATIONS)
        val idAnnoSetId = cursor.getColumnIndex(ValenceUnits_Layers_X.ANNOSETID)
        val idSentenceText = cursor.getColumnIndex(ValenceUnits_Layers_X.SENTENCETEXT)
        val changed = annoSets(parent, cursor, null, idSentenceText, idLayerType, idRank, idAnnotations, idAnnoSetId)
        cursor.close()
        return changed
    }

    /**
     * AnnoSets helper function
     *
     * @param parent         parent node
     * @param cursor         cursor
     * @param sentenceText   sentence text
     * @param idSentenceText id of sentence column
     * @param idLayerType    id of layer type column
     * @param idRank         id of rank column
     * @param idAnnotations  id of annotations column
     * @param idAnnoSetId    id of annoSet i column
     * @return changed nodes
     */
    private fun annoSets(
        parent: TreeNode, cursor: Cursor,
        sentenceText: String?, idSentenceText: Int, idLayerType: Int, idRank: Int, idAnnotations: Int, idAnnoSetId: Int,
    ): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            var currentAnnoSetId: Long = -1
            var annoSetNode: TreeNode? = null
            var sb: SpannableStringBuilder? = null
            var sba: SpannableStringBuilder? = null

            // read cursor
            do {
                val layerType = cursor.getString(idLayerType)
                val annotations = cursor.getString(idAnnotations)
                val annoSetId = cursor.getLong(idAnnoSetId)
                val rank = cursor.getString(idRank)
                val isTarget = "Target" == layerType
                val isFE = "FE" == layerType
                val text = sentenceText ?: cursor.getString(idSentenceText)

                // annoSet grouping
                if (currentAnnoSetId != annoSetId) {
                    if (sb != null) {
                        // attach result
                        val node = makeTextNode(sb, false).addTo(annoSetNode!!)
                        changedList.add(TreeOpCode.NEWCHILD, node)
                    }
                    sb = SpannableStringBuilder()
                    sba = SpannableStringBuilder()
                    append(sba, "AnnoSet", 0, FrameNetFactories.annoSetFactory)
                    sba.append(' ')
                    append(sba, annoSetId.toString(), 0, FrameNetFactories.dataFactory)
                    annoSetNode = makeTreeNode(sba, R.drawable.annoset, false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, annoSetNode)
                    currentAnnoSetId = annoSetId
                }
                if (sb!!.isNotEmpty()) {
                    sb.append('\n')
                }

                // layer
                appendImage(sb, layerDrawable)
                sb.append(' ')
                append(sb, processLayer(layerType), 0, FrameNetFactories.layerTypeFactory)
                if (VERBOSE) {
                    sb.append(' ')
                    sb.append('[')
                    sb.append(rank)
                    sb.append(']')
                }

                // annotations
                val labels = Utils.parseLabels(annotations)
                if (labels != null) {
                    for (label in labels) {
                        sb.append('\n')
                        sb.append('\t')
                        sb.append('\t')

                        // label
                        append(
                            sb, label.label, 0, if (isFE)
                                FrameNetFactories.feFactory else
                                FrameNetFactories.labelFactory
                        )
                        sb.append(' ')

                        // subtext value
                        val from = label.from.toInt()
                        val to = label.to.toInt() + 1
                        var subtext: String
                        val len = text.length
                        subtext = if (from < 0 || to > len || from > to) {
                            Log.d(TAG, "annoSetId=" + annoSetId + "annotations=" + annotations + "label=" + label + "text=" + text)
                            label.toString() + " ERROR [" + label.from + ',' + label.to + ']'
                        } else {
                            text.substring(from, to)
                        }
                        val p = sb.length
                        append(
                            sb, subtext, 0, if (isTarget)
                                FrameNetFactories.targetFactory else
                                FrameNetFactories.subtextFactory
                        )

                        // target
                        if (isTarget) {
                            sba!!.append(' ')
                            append(sba, subtext, 0, FrameNetFactories.targetFactory)
                        }

                        // value colors
                        if (label.bgColor != null) {
                            val color = label.bgColor.toInt(16)
                            sb.setSpan(BackgroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (label.fgColor != null) {
                            val color = label.fgColor.toInt(16)
                            sb.setSpan(ForegroundColorSpan(color or -0x1000000), p, p + subtext.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            } while (cursor.moveToNext())

            // attach remaining result
            if (sb!!.isNotEmpty()) {
                // attach result
                val node = makeTextNode(sb, false).addTo(annoSetNode!!)
                changedList.add(TreeOpCode.NEWCHILD, node)
            }
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        return changed
    }

    // L A Y E R S

    /**
     * Layers for sentence
     *
     * @param sentenceId sentence id
     * @param text       reference sentence text
     * @param parent     parent node
     */
    fun layersForSentence(sentenceId: Long, text: String, parent: TreeNode) {
        val sql = prepareLayersForSentence(sentenceId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        layersFromSentenceIdModel.loadData(uri, sql) { cursor: Cursor -> layersCursorToTreeModel(cursor, text, parent) }
    }

    private fun layersCursorToTreeModel(cursor: Cursor, text: String, parent: TreeNode): Array<TreeOp> {
        // column indices
        val idLayerType = cursor.getColumnIndex(Sentences_Layers_X.LAYERTYPE)
        val idRank = cursor.getColumnIndex(Sentences_Layers_X.RANK)
        val idAnnotations = cursor.getColumnIndex(Sentences_Layers_X.LAYERANNOTATIONS)
        val idAnnoSetId = cursor.getColumnIndex(Sentences_Layers_X.ANNOSETID)
        val changed = annoSets(parent, cursor, text, -1, idLayerType, idRank, idAnnotations, idAnnoSetId)
        cursor.close()
        return changed
    }

    // A G E N T S

    /**
     * Process definition
     *
     * @param text  text
     * @param flags flags
     * @return processed definition
     */
    private fun processDefinition(text: CharSequence, flags: Long): Array<CharSequence?> {
        val isFrame = flags and FrameNetMarkupFactory.FEDEF.toLong() == 0L
        val texts = processor.split(text)
        val fields = arrayOfNulls<CharSequence>(texts.size)
        for (i in texts.indices) {
            val field = (if (isFrame) frameProcessor.process(texts[i]) else texts[i])!!
            if (i == 0) {
                fields[i] = spanner!!.process(field, flags, if (isFrame) FrameNetFactories.metaFrameDefinitionFactory else FrameNetFactories.metaFeDefinitionFactory)
            } else {
                fields[i] = spanner!!.process(field, flags, null)
            }
        }
        return fields
    }

    /**
     * Process layer name
     *
     * @param name layer name
     * @return processed layer name
     */
    private fun processLayer(name: CharSequence): CharSequence {
        if ("FE".contentEquals(name)) {
            return "Frame element"
        }
        if ("PT".contentEquals(name)) {
            return "Phrase type"
        }
        return if ("GF".contentEquals(name)) {
            "Grammatical function"
        } else name
    }

    /**
     * Process PT
     *
     * @param name PT name
     * @return processed PT
     */
    private fun processPT(name: CharSequence): CharSequence {
        if ("CNI".contentEquals(name)) {
            return "constructional ∅"
        }
        if ("DNI".contentEquals(name)) {
            return "definite ∅"
        }
        return if ("INI".contentEquals(name)) {
            "indefinite ∅"
        } else name
    }

    // Q U E R I E S

    /**
     * Frame query
     */
    internal inner class FrameQuery(frameId: Long) : Query(frameId) {

        override fun process(node: TreeNode) {
            frame(id.toInt().toLong(), node)
        }

        override fun toString(): String {
            return "frame for $id"
        }
    }

    /**
     * Related frame query
     */
    internal inner class RelatedQuery(frameId: Long) : Query(frameId) {

        override fun process(node: TreeNode) {
            relatedFrames(id.toInt().toLong(), node)
        }

        override fun toString(): String {
            return "related for $id"
        }
    }

    /**
     * Lex units query
     */
    internal inner class LexUnitsQuery(frameId: Long) : Query(frameId) {

        override fun process(node: TreeNode) {
            lexUnitsForFrame(id.toInt().toLong(), node, false)
        }

        override fun toString(): String {
            return "lexunits for frame $id"
        }
    }

    /**
     * Frame elements query
     */
    internal inner class FEsQuery(frameId: Long) : Query(frameId) {

        override fun process(node: TreeNode) {
            fesForFrame(id.toInt(), node)
        }

        override fun toString(): String {
            return "fes for frame $id"
        }
    }

    /**
     * Governors query
     */
    internal inner class GovernorsQuery(luId: Long) : Query(luId) {

        override fun process(node: TreeNode) {
            governorsForLexUnit(id, node)
        }

        override fun toString(): String {
            return "govs for lu $id"
        }
    }

    /**
     * Realizations query
     */
    internal inner class RealizationsQuery(luId: Long) : Query(luId) {

        override fun process(node: TreeNode) {
            realizationsForLexicalUnit(id, node)
        }

        override fun toString(): String {
            return "realizations for lu $id"
        }
    }

    /**
     * Group realizations query
     */
    internal inner class GroupRealizationsQuery(luId: Long) : Query(luId) {

        override fun process(node: TreeNode) {
            groupRealizationsForLexUnit(id, node)
        }

        override fun toString(): String {
            return "grouprealizations for lu $id"
        }
    }

    /**
     * Sentences for pattern query
     */
    internal inner class SentencesForPatternQuery(patternId: Long) : Query(patternId) {

        override fun process(node: TreeNode) {
            sentencesForPattern(id, node)
        }

        override fun toString(): String {
            return "sentences for pattern $id"
        }
    }

    /**
     * Sentences for valence unit query
     */
    internal inner class SentencesForValenceUnitQuery(vuId: Long) : Query(vuId) {

        override fun process(node: TreeNode) {
            sentencesForValenceUnit(id, node)
        }

        override fun toString(): String {
            return "sentences for vu $id"
        }
    }

    /**
     * Sentences for lex unit query
     */
    internal inner class SentencesForLexUnitQuery(luId: Long) : Query(luId) {

        override fun process(node: TreeNode) {
            sentencesForLexUnit(id, node)
        }

        override fun toString(): String {
            return "sentences for lu $id"
        }
    }

    /**
     * AnnoSet query
     */
    internal inner class AnnoSetQuery(annoSetId: Long, private val withSentence: Boolean) : Query(annoSetId) {

        override fun process(node: TreeNode) {
            annoSet(id, node, withSentence)
        }

        override fun toString(): String {
            return "annoset for $id"
        }
    }

    /**
     * AnnoSets for governor query
     */
    internal inner class AnnoSetsForGovernorQuery(governorId: Long) : Query(governorId) {

        override fun process(node: TreeNode) {
            annoSetsForGovernor(id, node)
        }

        override fun toString(): String {
            return "annosets for gov $id"
        }
    }

    // R E L A T I O N S

    /**
     * Fn frame link data
     */
    internal inner class FnFrameLink(frameId: Long) : Link(frameId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = FnFramePointer(id)
            val intent = Intent(context, FnFrameActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "frame for $id"
        }
    }

    /**
     * Fn lex unit link data
     */
    internal inner class FnLexUnitLink(luId: Long) : Link(luId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = FnLexUnitPointer(id)
            val intent = Intent(context, FnLexUnitActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "lexunit for $id"
        }
    }

    /**
     * Fn sentence link data
     */
    internal inner class FnSentenceLink(sentenceId: Long) : Link(sentenceId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = FnSentencePointer(id)
            val intent = Intent(context, FnSentenceActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.setAction(ProviderArgs.ACTION_QUERY)
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "sentence for $id"
        }
    }

    companion object {

        private const val TAG = "BaseModule"

        /**
         * Verbose flag
         */
        private var VERBOSE = false

        // C R E A T I O N

        /**
         * Set always-simple-preferences flag
         *
         * @param verbose flag
         */
        fun setVerbose(verbose: Boolean) {
            VERBOSE = verbose
        }

        private val FRAMERELATION_RANK = intArrayOf(
            20,  // 1  - Has Subframe(s)
            11,  // 2  - Inherits from
            60,  // 3  - Is Causative of
            70,  // 4  - Is Inchoative of
            10,  // 5  - Is Inherited by
            51,  // 6  - Is Perspectivized in
            41,  // 7  - Is Preceded by
            31,  // 8  - Is Used by
            50,  // 9  - Perspective on
            40,  // 10 - Precedes
            80,  // 11 - See also
            21,  // 12 - Subframe of
            30
        )
        private val FRAMERELATION_GLOSS = arrayOf(
            "%s has %s as subframe",  // 1  - Has Subframe(s)
            "%s inherits %s",  // 2  - Inherits from
            "%s is causative of %s",  // 3  - Is Causative of
            "%s is inchoative of %s",  // 4  - Is Inchoative of
            "%s is inherited by %s",  // 5  - Is Inherited by
            "%s is perspectivized in %s",  // 6  - Is Perspectivized in
            "%s is preceded by %s",  // 7  - Is Preceded by
            "%s is used by %s",  // 8  - Is Used by
            "%s perspectivizes %s",  // 9  - Perspective on
            "%s precedes %s",  // 10 - Precedes
            "%s has see-also relation to %s",  // 11 - See also
            "%s is subframe of %s",  // 12 - Subframe of
            "%s uses %s"
        )
    }
}
