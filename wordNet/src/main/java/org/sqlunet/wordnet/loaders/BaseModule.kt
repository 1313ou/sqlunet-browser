/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeLeafNode
import org.sqlunet.model.TreeFactory.makeLinkHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeLinkLeafNode
import org.sqlunet.model.TreeFactory.makeLinkNode
import org.sqlunet.model.TreeFactory.makeLinkQueryTreeNode
import org.sqlunet.model.TreeFactory.makeMoreNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.model.TreeFactory.setTextNode
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Factories.boldFactory
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.control.Query
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.SensePointer
import org.sqlunet.wordnet.SynsetPointer
import org.sqlunet.wordnet.WordPointer
import org.sqlunet.wordnet.browser.RelationActivity
import org.sqlunet.wordnet.browser.SynsetActivity
import org.sqlunet.wordnet.browser.WordActivity
import org.sqlunet.wordnet.provider.V
import org.sqlunet.wordnet.provider.WordNetContract
import org.sqlunet.wordnet.provider.WordNetContract.AnyRelations_Senses_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.Domains
import org.sqlunet.wordnet.provider.WordNetContract.Ilis
import org.sqlunet.wordnet.provider.WordNetContract.LexRelations_Senses_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.Lexes_Morphs
import org.sqlunet.wordnet.provider.WordNetContract.Poses
import org.sqlunet.wordnet.provider.WordNetContract.Relations
import org.sqlunet.wordnet.provider.WordNetContract.Samples
import org.sqlunet.wordnet.provider.WordNetContract.SemRelations_Synsets_Words_X
import org.sqlunet.wordnet.provider.WordNetContract.Senses
import org.sqlunet.wordnet.provider.WordNetContract.Senses_AdjPositions
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbFrames
import org.sqlunet.wordnet.provider.WordNetContract.Senses_VerbTemplates
import org.sqlunet.wordnet.provider.WordNetContract.Senses_Words
import org.sqlunet.wordnet.provider.WordNetContract.Synsets
import org.sqlunet.wordnet.provider.WordNetContract.Usages
import org.sqlunet.wordnet.provider.WordNetContract.Wikidatas
import org.sqlunet.wordnet.provider.WordNetContract.Words_Lexes_Morphs
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetProvider
import org.sqlunet.wordnet.style.WordNetFactories
import org.sqlunet.wordnet.style.WordNetFactories.dataFactory
import org.sqlunet.wordnet.style.WordNetFactories.iliFactory
import org.sqlunet.wordnet.style.WordNetFactories.wikidataFactory
import java.util.Locale

/**
 * Base module for WordNet
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule internal constructor(fragment: TreeFragment) : Module(fragment) {

    // resources

    protected val wordLabel: String
    protected val senseLabel: String
    protected val synsetLabel: String
    private val sensesLabel: String
    protected val membersLabel: String
    protected val samplesLabel: String
    protected val usagesLabel: String
    protected val iliLabel: String
    protected val wikidataLabel: String
    protected val relationsLabel: String
    protected val upLabel: String
    protected val downLabel: String
    protected val verbFramesLabel: String
    protected val verbTemplatesLabel: String
    protected val adjPositionsLabel: String
    protected val morphsLabel: String
    private val synsetDrawable: Drawable
    private val memberDrawable: Drawable
    private val definitionDrawable: Drawable
    private val sampleDrawable: Drawable
    private val usageDrawable: Drawable
    private val iliDrawable: Drawable
    private val wikidataDrawable: Drawable
    private val posDrawable: Drawable
    private val domainDrawable: Drawable
    private val verbframeDrawable: Drawable
    private val morphDrawable: Drawable

    /**
     * Whether to display semantic relation names
     */
    private var displaySemRelationName = true

    /**
     * Whether to display lexical relation names
     */
    private var displayLexRelationName = true

    /**
     * Max relation recursion
     */
    protected var maxRecursion = Int.MAX_VALUE

    // view models

    private lateinit var wordModel: SqlunetViewTreeModel
    private lateinit var sensesFromWordModel: SqlunetViewTreeModel
    private lateinit var sensesFromWordIdModel: SqlunetViewTreeModel
    private lateinit var senseFromSenseIdModel: SqlunetViewTreeModel
    private lateinit var senseFromSenseKeyModel: SqlunetViewTreeModel
    private lateinit var senseFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var synsetFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var membersFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var members2FromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var samplesFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var usagesFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var iliFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var wikidataFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var relationsFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var semRelationsFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var semRelationsFromSynsetIdRelationIdModel: SqlunetViewTreeModel
    private lateinit var lexRelationsFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var lexRelationsFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var vFramesFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var vFramesFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var vTemplatesFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var vTemplatesFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var adjPositionFromSynsetIdModel: SqlunetViewTreeModel
    private lateinit var adjPositionFromSynsetIdWordIdModel: SqlunetViewTreeModel
    private lateinit var morphsFromWordIdModel: SqlunetViewTreeModel

    init {

        // models
        makeModels()

        // drawables
        val context = this@BaseModule.fragment.requireContext()
        wordLabel = context.getString(R.string.wordnet_word_)
        senseLabel = context.getString(R.string.wordnet_sense_)
        synsetLabel = context.getString(R.string.wordnet_synset_)
        sensesLabel = context.getString(R.string.wordnet_senses_)
        membersLabel = context.getString(R.string.wordnet_members_)
        samplesLabel = context.getString(R.string.wordnet_samples_)
        usagesLabel = context.getString(R.string.wordnet_usages_)
        iliLabel = context.getString(R.string.wordnet_ili_)
        wikidataLabel = context.getString(R.string.wordnet_wikidata_)
        upLabel = context.getString(R.string.wordnet_up_)
        relationsLabel = context.getString(R.string.wordnet_relations_)
        downLabel = context.getString(R.string.wordnet_down_)
        verbFramesLabel = context.getString(R.string.wordnet_verbframes_)
        verbTemplatesLabel = context.getString(R.string.wordnet_verbtemplates_)
        adjPositionsLabel = context.getString(R.string.wordnet_adjpositions_)
        morphsLabel = context.getString(R.string.wordnet_morphs_)
        synsetDrawable = getDrawable(context, R.drawable.synset)
        memberDrawable = getDrawable(context, R.drawable.synsetmember)
        definitionDrawable = getDrawable(context, R.drawable.definition)
        sampleDrawable = getDrawable(context, R.drawable.sample)
        usageDrawable = getDrawable(context, R.drawable.usage)
        iliDrawable = getDrawable(context, R.drawable.ili)
        wikidataDrawable = getDrawable(context, R.drawable.wikidata)
        posDrawable = getDrawable(context, R.drawable.pos)
        domainDrawable = getDrawable(context, R.drawable.domain)
        verbframeDrawable = getDrawable(context, R.drawable.verbframe)
        morphDrawable = getDrawable(context, R.drawable.morph)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        wordModel = ViewModelProvider(fragment)["wn.word(wordid)", SqlunetViewTreeModel::class.java]
        wordModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        sensesFromWordModel = ViewModelProvider(fragment)["wn.senses(word)", SqlunetViewTreeModel::class.java]
        sensesFromWordModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        sensesFromWordIdModel = ViewModelProvider(fragment)["wn.senses(wordid)", SqlunetViewTreeModel::class.java]
        sensesFromWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        senseFromSenseIdModel = ViewModelProvider(fragment)["wn.sense(senseid)", SqlunetViewTreeModel::class.java]
        senseFromSenseIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        senseFromSenseKeyModel = ViewModelProvider(fragment)["wn.sense(sensekey)", SqlunetViewTreeModel::class.java]
        senseFromSenseKeyModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        senseFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.sense(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        senseFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        synsetFromSynsetIdModel = ViewModelProvider(fragment)["wn.synset(synsetid)", SqlunetViewTreeModel::class.java]
        synsetFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        membersFromSynsetIdModel = ViewModelProvider(fragment)["wn.members(synsetid)", SqlunetViewTreeModel::class.java]
        membersFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        members2FromSynsetIdModel = ViewModelProvider(fragment)["wn.memberSet(synsetid)", SqlunetViewTreeModel::class.java]
        members2FromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        samplesFromSynsetIdModel = ViewModelProvider(fragment)["wn.samples(synsetid)", SqlunetViewTreeModel::class.java]
        samplesFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        usagesFromSynsetIdModel = ViewModelProvider(fragment)["wn.usages(synsetid)", SqlunetViewTreeModel::class.java]
        usagesFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        iliFromSynsetIdModel = ViewModelProvider(fragment)["wn.ili(synsetid)", SqlunetViewTreeModel::class.java]
        iliFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        wikidataFromSynsetIdModel = ViewModelProvider(fragment)["wn.wikidata(synsetid)", SqlunetViewTreeModel::class.java]
        wikidataFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        relationsFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.relations(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        relationsFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        semRelationsFromSynsetIdModel = ViewModelProvider(fragment)["wn.semrelations(synsetid)", SqlunetViewTreeModel::class.java]
        semRelationsFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        semRelationsFromSynsetIdRelationIdModel = ViewModelProvider(fragment)["wn.semrelations(synsetid,relationid)", SqlunetViewTreeModel::class.java]
        semRelationsFromSynsetIdRelationIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        lexRelationsFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.lexrelations(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        lexRelationsFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        lexRelationsFromSynsetIdModel = ViewModelProvider(fragment)["wn.lexrelations(synsetid)", SqlunetViewTreeModel::class.java]
        lexRelationsFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vFramesFromSynsetIdModel = ViewModelProvider(fragment)["wn.vframes(synsetid)", SqlunetViewTreeModel::class.java]
        vFramesFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vFramesFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.vframes(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        vFramesFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vTemplatesFromSynsetIdModel = ViewModelProvider(fragment)["wn.vtemplates(synsetid)", SqlunetViewTreeModel::class.java]
        vTemplatesFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vTemplatesFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.vtemplates(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        vTemplatesFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        adjPositionFromSynsetIdModel = ViewModelProvider(fragment)["wn.adjposition(synsetid)", SqlunetViewTreeModel::class.java]
        adjPositionFromSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        adjPositionFromSynsetIdWordIdModel = ViewModelProvider(fragment)["wn.adjposition(synsetid,wordid)", SqlunetViewTreeModel::class.java]
        adjPositionFromSynsetIdWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        morphsFromWordIdModel = ViewModelProvider(fragment)["wn.morphs(wordid)", SqlunetViewTreeModel::class.java]
        morphsFromWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    /**
     * Set max recursion level
     *
     * @param maxRecursion max recursion level
     */
    fun setMaxRecursionLevel(maxRecursion: Int) {
        this.maxRecursion = if (maxRecursion == -1) Int.MAX_VALUE else maxRecursion
    }

    /**
     * Set display relation names
     *
     * @param displaySemRelationName display semantic relation name
     * @param displayLexRelationName display lexical relation name
     */
    fun setDisplayRelationNames(displaySemRelationName: Boolean, displayLexRelationName: Boolean) {
        this.displaySemRelationName = displaySemRelationName
        this.displayLexRelationName = displayLexRelationName
    }

    // W O R D

    /**
     * Word
     *
     * @param wordId     word id
     * @param parent     tree parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    fun word(wordId: Long, parent: TreeNode, addNewNode: Boolean) {
        // load the contents
        val sql = Queries.prepareWord(wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        wordModel.loadData(uri, sql) { cursor: Cursor -> wordCursorToTreeModel(cursor, parent, addNewNode) }
    }

    private fun wordCursorToTreeModel(cursor: Cursor, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()

            // var idWordId = cursor.getColumnIndex(Words.WORDID)
            val idWord = cursor.getColumnIndex(Words_Lexes_Morphs.WORD)
            val idMorphs = cursor.getColumnIndex(Words_Lexes_Morphs.MORPHS)
            val word = cursor.getString(idWord)
            val morphs = cursor.getString(idMorphs)
            appendImage(sb, memberDrawable)
            sb.append(' ')
            append(sb, word, 0, WordNetFactories.wordFactory)
            if (morphs != null && morphs.isNotEmpty()) {
                sb.append(' ')
                append(sb, morphs, 0, dataFactory)
            }

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                setTextNode(parent, sb, R.drawable.member)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // S E N S E

    /**
     * Senses from word
     *
     * @param word   word
     * @param parent tree parent node
     */
    protected fun senses(word: String, parent: TreeNode) {
        // load the contents
        val sql = Queries.prepareSenses(word)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        sensesFromWordModel.loadData(uri, sql) { cursor: Cursor -> sensesCursorToTreeModel(cursor, parent) }
    }

    /**
     * Senses from word id
     *
     * @param wordId word id
     * @param parent tree parent node
     */
    fun senses(wordId: Long, parent: TreeNode) {
        // load the contents
        val sql = Queries.prepareSenses(wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        sensesFromWordIdModel.loadData(uri, sql) { cursor: Cursor -> sensesCursorToTreeModel(cursor, parent) }
    }

    private fun sensesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID)
            val idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID)
            val idPosName = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.POS)
            val idDomain = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN)
            val idDefinition = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION)
            val idTagCount = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT)
            val idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD)
            do {
                val wordId = cursor.getLong(idWordId)
                val synsetId = cursor.getLong(idSynsetId)
                val posName = cursor.getString(idPosName)
                val domain = cursor.getString(idDomain)
                val definition = cursor.getString(idDefinition)
                val cased = cursor.getString(idCased)
                val tagCount = cursor.getInt(idTagCount)
                val sb = SpannableStringBuilder()
                sense(sb, synsetId, posName, domain, definition, tagCount, cased)

                // result
                val synsetNode = makeLinkNode(sb, R.drawable.synset, false, SenseLink(synsetId, wordId, maxRecursion, fragment)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, synsetNode)
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
     * Sense
     *
     * @param senseId sense id
     * @param parent  parent node
     */
    fun sense(senseId: Long, parent: TreeNode) {
        // load the contents
        val sql = Queries.prepareSense(senseId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        senseFromSenseIdModel.loadData(uri, sql) { cursor: Cursor -> senseFromSenseIdCursorToTreeModel(cursor, parent) }
    }

    private fun senseFromSenseIdCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp> = if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Poses.POS)
            val idSynsetId = cursor.getColumnIndex(Synsets.DEFINITION)
            val wordId = cursor.getLong(idWordId)
            val synsetId = cursor.getLong(idSynsetId)
            sense(synsetId, wordId, parent)
            seq(TreeOpCode.NOOP, parent)
        } else {
            setNoResult(parent)
            seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Sense
     *
     * @param senseKey sense key
     * @param parent   parent node
     */
    fun sense(senseKey: String, parent: TreeNode) {
        // load the contents
        val sql = Queries.prepareSense(senseKey)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        senseFromSenseKeyModel.loadData(uri, sql) { cursor: Cursor -> senseFromSenseKeyCursorToTreeModel(cursor, parent) }
    }

    private fun senseFromSenseKeyCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Senses.WORDID)
            val idSynsetId = cursor.getColumnIndex(Senses.SYNSETID)
            val wordId = cursor.getLong(idWordId)
            val synsetId = cursor.getLong(idSynsetId)

            // sub nodes
            val wordNode = makeTextNode(wordLabel, false).addTo(parent)

            // word
            word(wordId, wordNode, false)
            sense(synsetId, wordId, wordNode)
            changed = seq(TreeOpCode.NOOP, parent, TreeOpCode.NEWUNIQUE, wordNode)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Sense
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param parent   parent node
     */
    private fun sense(synsetId: Long, wordId: Long, parent: TreeNode) {
        // load the contents
        val sql = Queries.prepareSense(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        senseFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> senseFromSynsetIdWordIdCursorToTreeModel(cursor, synsetId, wordId, parent) }
    }

    private fun senseFromSynsetIdWordIdCursorToTreeModel(cursor: Cursor, synsetId: Long, wordId: Long, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()
            val idPosName = cursor.getColumnIndex(Poses.POS)
            val idDomain = cursor.getColumnIndex(Domains.DOMAIN)
            val idDefinition = cursor.getColumnIndex(Synsets.DEFINITION)
            val posName = cursor.getString(idPosName)
            val domain = cursor.getString(idDomain)
            val definition = cursor.getString(idDefinition)
            appendImage(sb, synsetDrawable)
            sb.append(' ')
            synset(sb, synsetId, posName, domain, definition)

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)

            // subnodes
            val relationsNode = makeHotQueryTreeNode(relationsLabel, R.drawable.ic_relations, false, RelationsQuery(synsetId, wordId)).addTo(parent)
            val samplesNode = makeHotQueryTreeNode(samplesLabel, R.drawable.sample, false, SamplesQuery(synsetId)).addTo(parent)
            changed = seq(TreeOpCode.NEWMAIN, node, TreeOpCode.NEWEXTRA, relationsNode, TreeOpCode.NEWEXTRA, samplesNode, TreeOpCode.NEWTREE, parent)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Sense to string builder
     *
     * @param sb         string builder
     * @param synsetId   synset id
     * @param posName    pos
     * @param domain     domain
     * @param definition definition
     * @param tagCount   tag count
     * @param cased      cased
     * @return string builder
     */
    private fun sense(sb: SpannableStringBuilder, synsetId: Long, posName: CharSequence, domain: CharSequence, definition: CharSequence, tagCount: Int, cased: CharSequence?): SpannableStringBuilder {
        synsetHead(sb, synsetId, posName, domain)
        if (!cased.isNullOrEmpty()) {
            appendImage(sb, memberDrawable)
            sb.append(' ')
            append(sb, cased, 0, WordNetFactories.wordFactory)
            sb.append(' ')
        }
        if (tagCount > 0) {
            sb.append(' ')
            append(sb, "tagcount:$tagCount", 0, dataFactory)
        }
        sb.append('\n')
        synsetDefinition(sb, definition)
        return sb
    }

    // S Y N S E T

    /**
     * Synset
     *
     * @param synsetId   synset id
     * @param parent     parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    fun synset(synsetId: Long, parent: TreeNode, addNewNode: Boolean) {
        val sql = Queries.prepareSynset(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        synsetFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> synsetCursorToTreeModel(cursor, synsetId, parent, addNewNode) }
    }

    private fun synsetCursorToTreeModel(cursor: Cursor, synsetId: Long, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()
            val idPosName = cursor.getColumnIndex(Poses.POS)
            val idDomain = cursor.getColumnIndex(Domains.DOMAIN)
            val idDefinition = cursor.getColumnIndex(Synsets.DEFINITION)
            val posName = cursor.getString(idPosName)
            val domain = cursor.getString(idDomain)
            val definition = cursor.getString(idDefinition)
            appendImage(sb, synsetDrawable)
            sb.append(' ')
            synset(sb, synsetId, posName, domain, definition)

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                setTextNode(parent, sb) //, R.drawable.synset)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Synset to string builder
     *
     * @param sb         string builder
     * @param synsetId   synset id
     * @param posName    pos
     * @param domain     domain
     * @param definition definition
     * @return string builder
     */
    private fun synset(sb: SpannableStringBuilder, synsetId: Long, posName: CharSequence, domain: CharSequence, definition: CharSequence): SpannableStringBuilder {
        synsetHead(sb, synsetId, posName, domain)
        sb.append('\n')
        synsetDefinition(sb, definition)
        return sb
    }

    /**
     * Synset head to string builder
     *
     * @param sb       string builder
     * @param synsetId synset id
     * @param posName  pos
     * @param domain   domain
     * @return string builder
     */
    private fun synsetHead(sb: SpannableStringBuilder, synsetId: Long, posName: CharSequence, domain: CharSequence): SpannableStringBuilder {
        appendImage(sb, posDrawable)
        sb.append(' ')
        sb.append(posName)
        sb.append(' ')
        appendImage(sb, domainDrawable)
        sb.append(' ')
        sb.append(domain)
        sb.append(' ')
        append(sb, synsetId.toString(), 0, dataFactory)
        return sb
    }

    /**
     * Synset definition to string builder
     *
     * @param sb         string builder
     * @param definition definition
     * @return string builder
     */
    private fun synsetDefinition(sb: SpannableStringBuilder, definition: CharSequence): SpannableStringBuilder {
        appendImage(sb, definitionDrawable)
        sb.append(' ')
        append(sb, definition, 0, WordNetFactories.definitionFactory)
        return sb
    }

    // M E M B E R S

    /**
     * Members
     *
     * @param synsetId synset id
     * @param parent   parent node
     */
    fun members(synsetId: Long, parent: TreeNode) {
        val sql = Queries.prepareMembers(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        membersFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> membersCursorToTreeModel(cursor, parent) }
    }

    private fun membersCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idWordId = cursor.getColumnIndex(Senses_Words.WORDID)
            val idMember = cursor.getColumnIndex(Senses_Words.WORD)
            // int i = 1
            do {
                val wordId = cursor.getLong(idWordId)
                val member = cursor.getString(idMember)
                val sb = SpannableStringBuilder()
                append(sb, member, 0, WordNetFactories.membersFactory)

                // result
                val memberNode = makeLinkNode(sb, R.drawable.member, false, WordLink(fragment, wordId)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, memberNode)
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
     * Members as one set in one node
     *
     * @param synsetId    synset id
     * @param parent      parent node
     * @param concatQuery whether query returns members concat and not distinct rows
     * @param addNewNode  whether to addItem to (or set) node
     * @noinspection SameParameterValue
     */
    fun memberSet(synsetId: Long, parent: TreeNode, concatQuery: Boolean, addNewNode: Boolean) {
        val sql = Queries.prepareMembers2(synsetId, concatQuery)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        members2FromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> memberSetCursorToTreeModel(cursor, parent, concatQuery, addNewNode) }
    }

    private fun memberSetCursorToTreeModel(cursor: Cursor, parent: TreeNode, concatQuery: Boolean, addNewNode: Boolean): Array<TreeOp> {
        if (concatQuery) {
            if (cursor.count > 1) {
                throw RuntimeException("Unexpected number of rows")
            }
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()
            if (concatQuery) {
                val idMembers = cursor.getColumnIndex(Senses_Words.MEMBERS)
                sb.append('{')
                append(sb, cursor.getString(idMembers), 0, WordNetFactories.membersFactory)
                sb.append('}')
            } else {
                val wordId = cursor.getColumnIndex(WordNetContract.Words.WORD)
                do {
                    val word = cursor.getString(wordId)
                    if (sb.isNotEmpty()) {
                        sb.append('\n')
                    }
                    //Spanner.appendImage(sb, BaseModule.this.memberDrawable)
                    //sb.append(' ')
                    append(sb, word, 0, WordNetFactories.membersFactory)
                } while (cursor.moveToNext())
            }

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                setTextNode(parent, sb, R.drawable.members)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // S A M P L E S

    /**
     * Samples
     *
     * @param synsetId   synset id
     * @param parent     parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    private fun samples(synsetId: Long, parent: TreeNode, @Suppress("SameParameterValue") addNewNode: Boolean) {
        val sql = Queries.prepareSamples(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        samplesFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> samplesCursorToTreeModel(cursor, parent, addNewNode) }
    }

    private fun samplesCursorToTreeModel(cursor: Cursor, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val idSample = cursor.getColumnIndex(Samples.SAMPLE)
            val sb = SpannableStringBuilder()
            do {
                val sample = cursor.getString(idSample)
                // var sampleId = cursor.getInt(idSampleId)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, sampleDrawable)
                sb.append(' ')
                // sb.append(sampleId)
                // sb.append(' ')
                append(sb, sample, 0, WordNetFactories.sampleFactory)
                // var formattedSample = String.format(Locale.ENGLISH, "[%d] %s", sampleId, sample)
                // sb.append(formattedSample)
            } while (cursor.moveToNext())

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                setTextNode(parent, sb, R.drawable.sample)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // U S A G E S

    /**
     * Usages
     *
     * @param synsetId   synset id
     * @param parent     parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    private fun usages(synsetId: Long, parent: TreeNode, @Suppress("SameParameterValue") addNewNode: Boolean) {
        val sql = Queries.prepareUsages(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        usagesFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> usagesCursorToTreeModel(cursor, parent, addNewNode) }
    }

    private fun usagesCursorToTreeModel(cursor: Cursor, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val idUsage = cursor.getColumnIndex(Usages.USAGENOTE)
            val sb = SpannableStringBuilder()
            do {
                val usage = cursor.getString(idUsage)
                // var usageId = cursor.getInt(idUsageId)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                // appendImage(sb, usageDrawable)
                // sb.append(' ')
                // sb.append(usageId)
                // sb.append(' ')
                append(sb, usage, 0, WordNetFactories.usageFactory)
                // var formattedUsage = String.format(Locale.ENGLISH, "[%d] %s", usageId, usageNote)
                // sb.append(formattedUsage)
            } while (cursor.moveToNext())

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                setTextNode(parent, sb, R.drawable.usage)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // E X T E R N A L   L I N K S

    /**
     * Ili
     *
     * @param synsetId   synset id
     * @param parent     parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    private fun ili(synsetId: Long, parent: TreeNode, @Suppress("SameParameterValue") addNewNode: Boolean) {
        val sql = Queries.prepareIli(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        iliFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> iliCursorToTreeModel(cursor, parent, addNewNode) }
    }

    private fun iliCursorToTreeModel(cursor: Cursor, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val idIli = cursor.getColumnIndex(Ilis.ILI)
            val ili = cursor.getString(idIli).substring(1)
            val callback = {
                Log.d(TAG, "ILI $ili clicked!")
                val context = this@BaseModule.fragment.requireContext()
                val url = String.format(context.resources.getString(R.string.ili_url), ili)
                Toast.makeText(context, "ILI $ili clicked!", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
            val sb = SpannableStringBuilder()
            append(sb, "ILI", 0, boldFactory)
            sb.append(' ')
            append(sb, ili, 0, iliFactory)

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                node.payload!![1] = callback
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                parent.payload!![1] = callback
                setTextNode(parent, sb, R.drawable.ili)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Wikidata
     *
     * @param synsetId   synset id
     * @param parent     parent node
     * @param addNewNode whether to addItem to (or set) node
     */
    private fun wikidata(synsetId: Long, parent: TreeNode, @Suppress("SameParameterValue") addNewNode: Boolean) {
        val sql = Queries.prepareWikidata(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        wikidataFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> wikidataCursorToTreeModel(cursor, parent, addNewNode) }
    }

    private fun wikidataCursorToTreeModel(cursor: Cursor, parent: TreeNode, addNewNode: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val idWikidata = cursor.getColumnIndex(Wikidatas.WIKIDATA)
            val wikidata = cursor.getString(idWikidata)
            val callback = {
                Log.d(TAG, "Wikidata $wikidata clicked!")
                val context = fragment.requireContext()
                val url = String.format(context.resources.getString(R.string.wikidata_url), wikidata)
                Toast.makeText(context, "Wikidata $wikidata clicked!", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
            val sb = SpannableStringBuilder()
            append(sb, "Wikidata", 0, boldFactory)
            sb.append(' ')
            append(sb, wikidata, 0, wikidataFactory)

            // result
            changed = if (addNewNode) {
                val node = makeTextNode(sb, false).addTo(parent)
                node.payload!![1] = callback
                seq(TreeOpCode.NEWUNIQUE, node)
            } else {
                parent.payload!![1] = callback
                setTextNode(parent, sb, R.drawable.wikidata)
                seq(TreeOpCode.UPDATE, parent)
            }
        } else {
            setNoResult(parent)
            changed = seq(if (addNewNode) TreeOpCode.DEADEND else TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // R E L A T I O N S

    /**
     * Relations (union)
     *
     * @param synsetId                synset id
     * @param wordId                  word id
     * @param parent                  parent node
     * @param deadendParentIfNoResult mark parent node as deadend if there is no result
     */
    private fun relations(synsetId: Long, wordId: Long, parent: TreeNode, @Suppress("SameParameterValue") deadendParentIfNoResult: Boolean) {
        val sql = Queries.prepareRelations(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        relationsFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> relationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult) }
    }

    private fun relationsCursorToTreeModel(cursor: Cursor, parent: TreeNode, deadendParentIfNoResult: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idRelationId = cursor.getColumnIndex(Relations.RELATIONID)
            val idRelation = cursor.getColumnIndex(Relations.RELATION)
            val idTargetSynsetId = cursor.getColumnIndex(V.SYNSET2ID)
            val idTargetDefinition = cursor.getColumnIndex(V.DEFINITION2)
            val idTargetMembers = cursor.getColumnIndex(AnyRelations_Senses_Words_X.MEMBERS2)
            val idRecurses = cursor.getColumnIndex(AnyRelations_Senses_Words_X.RECURSES)
            val idTargetWordId = cursor.getColumnIndex(V.WORD2ID)
            val idTargetWord = cursor.getColumnIndex(V.WORD2)
            do {
                val sb = SpannableStringBuilder()
                val relationId = cursor.getInt(idRelationId)
                val relation = cursor.getString(idRelation)
                val targetSynsetId = cursor.getLong(idTargetSynsetId)
                val targetDefinition = cursor.getString(idTargetDefinition)
                var targetMembers = cursor.getString(idTargetMembers)
                val relationCanRecurse = !cursor.isNull(idRecurses) && cursor.getInt(idRecurses) != 0
                val targetWord = if (cursor.isNull(idTargetWord)) null else cursor.getString(idTargetWord)
                val targetWordId = if (cursor.isNull(idTargetWordId)) null else cursor.getLong(idTargetWordId)
                if (targetWordId == null && displaySemRelationName || targetWordId != null && displayLexRelationName) {
                    append(sb, relation, 0, WordNetFactories.relationFactory)
                    sb.append(' ')
                }
                appendMembers(sb, targetMembers, targetWord)
                sb.append(' ')
                append(sb, targetDefinition, 0, WordNetFactories.definitionFactory)

                // recursion
                if (relationCanRecurse) {
                    val relationsNode = makeLinkQueryTreeNode(sb, getRelationRes(relationId), false, SubRelationsQuery(targetSynsetId, relationId, maxRecursion), SynsetLink(targetSynsetId, maxRecursion, fragment), 0).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, relationsNode)
                } else {
                    val node = makeLinkLeafNode(sb, getRelationRes(relationId), false, if (targetWordId == null) SynsetLink(targetSynsetId, maxRecursion, fragment) else SenseLink(targetSynsetId, targetWordId, maxRecursion, fragment)).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, node)
                }
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            changed = if (deadendParentIfNoResult) {
                setNoResult(parent)
                seq(TreeOpCode.DEADEND, parent)
            } else {
                seq(TreeOpCode.NOOP, parent)
            }
        }
        cursor.close()
        return changed
    }

    private fun appendMembers(sb: SpannableStringBuilder, members: String, targetWord: String?) {
        if (targetWord != null) {
            append(sb, targetWord, 0, WordNetFactories.targetMemberFactory)
            val otherMembers = members
                .splitToSequence(",")
                .filterNot { it == targetWord }
                .joinToString(separator = ",")
            if (!otherMembers.isEmpty())
                sb.append(',')
            append(sb, otherMembers, 0, WordNetFactories.otherMembersFactory)
        } else {
            append(sb, members, 0, WordNetFactories.membersFactory)
        }
    }

    private fun appendMembers(sb: SpannableStringBuilder, members: String) {
        append(sb, members, 0, WordNetFactories.membersFactory)
    }

    // S E M R E L A T I O N S

    /**
     * Semantic relations
     *
     * @param synsetId                synset id
     * @param parent                  parent node
     * @param deadendParentIfNoResult mark parent node as deadend if there is no result
     */
    private fun semRelations(synsetId: Long, parent: TreeNode, @Suppress("SameParameterValue") deadendParentIfNoResult: Boolean) {
        val sql = Queries.prepareSemRelations(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        semRelationsFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> semRelationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult) }
    }

    private fun semRelationsCursorToTreeModel(cursor: Cursor, parent: TreeNode, deadendParentIfNoResult: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idRelationId = cursor.getColumnIndex(Relations.RELATIONID)
            val idRelation = cursor.getColumnIndex(Relations.RELATION)
            val idTargetSynsetId = cursor.getColumnIndex(V.SYNSET2ID)
            val idTargetDefinition = cursor.getColumnIndex(V.DEFINITION2)
            val idTargetMembers = cursor.getColumnIndex(SemRelations_Synsets_Words_X.MEMBERS2)
            val idRecurses = cursor.getColumnIndex(SemRelations_Synsets_Words_X.RECURSES)
            do {
                val sb = SpannableStringBuilder()
                val relationId = cursor.getInt(idRelationId)
                val relation = cursor.getString(idRelation)
                val targetSynsetId = cursor.getLong(idTargetSynsetId)
                val targetDefinition = cursor.getString(idTargetDefinition)
                val targetMembers = cursor.getString(idTargetMembers)
                val relationCanRecurse = cursor.getInt(idRecurses) != 0
                if (displaySemRelationName) {
                    append(sb, relation, 0, WordNetFactories.relationFactory)
                    sb.append(' ')
                }
                appendMembers(sb, targetMembers)
                sb.append(' ')
                append(sb, targetDefinition, 0, WordNetFactories.definitionFactory)

                // recursion
                if (relationCanRecurse) {
                    val relationsNode = makeLinkQueryTreeNode(sb, getRelationRes(relationId), false, SubRelationsQuery(targetSynsetId, relationId, maxRecursion), SynsetLink(targetSynsetId, maxRecursion, fragment), 0).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, relationsNode)
                } else {
                    val node = makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, node)
                }
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            changed = if (deadendParentIfNoResult) {
                setNoResult(parent)
                seq(TreeOpCode.DEADEND, parent)
            } else {
                seq(TreeOpCode.NOOP, parent)
            }
        }
        cursor.close()
        return changed
    }

    /**
     * Semantic relations
     *
     * @param synsetId                synset id
     * @param relationId              relation id
     * @param recurseLevel            recurse level
     * @param hot                     whether query is executed immediately
     * @param parent                  parent node
     * @param deadendParentIfNoResult mark parent node as deadend if there is no result
     */
    private fun semRelations(synsetId: Long, relationId: Int, recurseLevel: Int, @Suppress("SameParameterValue") hot: Boolean, parent: TreeNode, @Suppress("SameParameterValue") deadendParentIfNoResult: Boolean) {
        val sql = Queries.prepareSemRelations(synsetId, relationId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        semRelationsFromSynsetIdRelationIdModel.loadData(uri, sql) { cursor: Cursor -> semRelationsFromSynsetIdRelationIdCursorToTreeModel(cursor, relationId, recurseLevel, hot, parent, deadendParentIfNoResult) }
    }

    private fun semRelationsFromSynsetIdRelationIdCursorToTreeModel(cursor: Cursor, relationId: Int, recurseLevel: Int, hot: Boolean, parent: TreeNode, deadendParentIfNoResult: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // var idRelationId = cursor.getColumnIndex(Relations.RELATIONID)
            // var idRelation = cursor.getColumnIndex(Relations.RELATION)
            val idTargetSynsetId = cursor.getColumnIndex(V.SYNSET2ID)
            val idTargetDefinition = cursor.getColumnIndex(V.DEFINITION2)
            val idTargetMembers = cursor.getColumnIndex(SemRelations_Synsets_Words_X.MEMBERS2)
            val idRecurses = cursor.getColumnIndex(SemRelations_Synsets_Words_X.RECURSES)
            do {
                val sb = SpannableStringBuilder()
                // var relationId = cursor.getInt(idRelationId)
                // var relation = cursor.getString(idRelation)
                val targetSynsetId = cursor.getLong(idTargetSynsetId)
                val targetDefinition = cursor.getString(idTargetDefinition)
                val targetMembers = cursor.getString(idTargetMembers)
                val relationCanRecurse = cursor.getInt(idRecurses) != 0
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendMembers(sb, targetMembers)
                sb.append(' ')
                append(sb, targetDefinition, 0, WordNetFactories.definitionFactory)

                // recurse
                if (relationCanRecurse) {
                    if (recurseLevel > 1) {
                        val newRecurseLevel = recurseLevel - 1
                        val relationsNode = if (hot) makeLinkHotQueryTreeNode(
                            sb,
                            getRelationRes(relationId),
                            false,
                            SubRelationsQuery(targetSynsetId, relationId, newRecurseLevel, true),
                            SynsetLink(targetSynsetId, maxRecursion, fragment),
                            0
                        ).addTo(parent) else makeLinkQueryTreeNode(sb, getRelationRes(relationId), false, SubRelationsQuery(targetSynsetId, relationId, newRecurseLevel), SynsetLink(targetSynsetId, maxRecursion, fragment), 0).addTo(parent)
                        changedList.add(TreeOpCode.NEWCHILD, relationsNode)
                    } else {
                        val moreNode = makeMoreNode(sb, getRelationRes(relationId), false).addTo(parent)
                        changedList.add(TreeOpCode.NEWCHILD, moreNode)
                    }
                } else {
                    val node = makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, node)
                }
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            changed = if (deadendParentIfNoResult) {
                setNoResult(parent)
                seq(TreeOpCode.DEADEND, parent)
            } else {
                seq(TreeOpCode.NOOP, parent)
            }
        }
        cursor.close()
        return changed
    }

    // L E X R E L A T I O N S

    /**
     * Lexical relations
     *
     * @param synsetId                synset id
     * @param wordId                  word id
     * @param parent                  parent node
     * @param deadendParentIfNoResult mark parent node as deadend if there is no result
     */
    private fun lexRelations(synsetId: Long, wordId: Long, parent: TreeNode, deadendParentIfNoResult: Boolean) {
        val sql = Queries.prepareLexRelations(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        lexRelationsFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> lexRelationsCursorToTreeModel(cursor, parent, deadendParentIfNoResult) }
    }

    private fun lexRelationsCursorToTreeModel(cursor: Cursor, parent: TreeNode, deadendParentIfNoResult: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idRelationId = cursor.getColumnIndex(Relations.RELATIONID)
            val idRelation = cursor.getColumnIndex(Relations.RELATION)
            val idTargetSynsetId = cursor.getColumnIndex(V.SYNSET2ID)
            val idTargetDefinition = cursor.getColumnIndex(V.DEFINITION2)
            val idTargetMembers = cursor.getColumnIndex(LexRelations_Senses_Words_X.MEMBERS2)
            val idTargetWordId = cursor.getColumnIndex(V.WORD2ID)
            val idTargetWord = cursor.getColumnIndex(V.WORD2)
            do {
                val sb = SpannableStringBuilder()
                val relationId = cursor.getInt(idRelationId)
                val relation = cursor.getString(idRelation)
                val targetSynsetId = cursor.getLong(idTargetSynsetId)
                val targetDefinition = cursor.getString(idTargetDefinition)
                val targetWord = cursor.getString(idTargetWord)
                var targetMembers = cursor.getString(idTargetMembers)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                if (displayLexRelationName) {
                    append(sb, relation, 0, WordNetFactories.relationFactory)
                    sb.append(' ')
                }
                appendMembers(sb, targetMembers, targetWord)
                sb.append(' ')
                append(sb, targetDefinition, 0, WordNetFactories.definitionFactory)

                // attach result
                val relationNode = makeLinkLeafNode(sb, getRelationRes(relationId), false, SenseLink(targetSynsetId, idTargetWordId.toLong(), maxRecursion, fragment)).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, relationNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            changed = if (deadendParentIfNoResult) {
                setNoResult(parent)
                seq(TreeOpCode.DEADEND, parent)
            } else {
                seq(TreeOpCode.NOOP, parent)
            }
        }
        cursor.close()
        return changed
    }

    /**
     * Lexical relations
     *
     * @param synsetId                synset id
     * @param parent                  parent
     * @param deadendParentIfNoResult mark parent node as deadend if there is no result
     */
    fun lexRelations(synsetId: Long, parent: TreeNode, deadendParentIfNoResult: Boolean) {
        val sql = Queries.prepareLexRelations(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        lexRelationsFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> lexRelationsFromSynsetIdCursorToTreeModel(cursor, parent, deadendParentIfNoResult) }
    }

    private fun lexRelationsFromSynsetIdCursorToTreeModel(cursor: Cursor, parent: TreeNode, deadendParentIfNoResult: Boolean): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)
            val idRelationId = cursor.getColumnIndex(Relations.RELATIONID)
            val idTargetDefinition = cursor.getColumnIndex(V.DEFINITION2)
            val idTargetMembers = cursor.getColumnIndex(LexRelations_Senses_Words_X.MEMBERS2)
            val idTargetWord = cursor.getColumnIndex(V.WORD2)
            val sb = SpannableStringBuilder()
            do {
                val relationId = cursor.getInt(idRelationId)
                // var relationId = cursor.getInt(idRelationId)
                // var relation = cursor.getString(idRelation)
                val targetDefinition = cursor.getString(idTargetDefinition)
                val targetWord = cursor.getString(idTargetWord)
                var targetMembers = cursor.getString(idTargetMembers)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendMembers(sb, targetMembers, targetWord)
                sb.append(' ')
                append(sb, targetDefinition, 0, WordNetFactories.definitionFactory)

                // attach result
                val relationNode = makeLeafNode(sb, getRelationRes(relationId), false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, relationNode)
            } while (cursor.moveToNext())

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)
            changedList.add(TreeOpCode.NEWCHILD, node)
            changed = changedList.toArray()
        } else {
            changed = if (deadendParentIfNoResult) {
                setNoResult(parent)
                seq(TreeOpCode.DEADEND, parent)
            } else {
                seq(TreeOpCode.NOOP, parent)
            }
        }
        cursor.close()
        return changed
    }

    // V F R A M E S

    /**
     * Verb frames
     *
     * @param synsetId synset id
     * @param parent   parent node
     */
    fun vFrames(synsetId: Long, parent: TreeNode) {
        val sql = Queries.prepareVFrames(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        vFramesFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> vframesCursorToTreeModel(cursor, parent) }
    }

    /**
     * Verb frames
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param parent   parent node
     */
    fun vFrames(synsetId: Long, wordId: Long, parent: TreeNode) {
        val sql = Queries.prepareVFrames(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        vFramesFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> vframesCursorToTreeModel(cursor, parent) }
    }

    private fun vframesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val vframeId = cursor.getColumnIndex(Senses_VerbFrames.FRAME)
            val sb = SpannableStringBuilder()
            do {
                val vframe = cursor.getString(vframeId)
                val formattedVframe = String.format(Locale.ENGLISH, "%s", vframe)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, verbframeDrawable)
                sb.append(' ')
                sb.append(formattedVframe)
            } while (cursor.moveToNext())

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

    // V T E M P L A T E S

    /**
     * Verb templates
     *
     * @param synsetId synset id
     * @param parent   parent node
     */
    fun vTemplates(synsetId: Long, parent: TreeNode) {
        val sql = Queries.prepareVTemplates(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        vTemplatesFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> vTemplatesCursorToTreeModel(cursor, parent) }
    }

    private fun vTemplatesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val vTemplateId = cursor.getColumnIndex(Senses_VerbTemplates.TEMPLATE)
            val sb = SpannableStringBuilder()
            do {
                val vTemplate = cursor.getString(vTemplateId)
                val formattedVTemplate = String.format(Locale.ENGLISH, vTemplate, "[-]")
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, verbframeDrawable)
                sb.append(' ')
                sb.append(formattedVTemplate)
            } while (cursor.moveToNext())

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
     * Verb templates
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param parent   parent node
     */
    fun vTemplates(synsetId: Long, wordId: Long, parent: TreeNode) {
        val sql = Queries.prepareVTemplates(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        vTemplatesFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> vTemplatesFromSynsetIdWordIdCursorToTreeModel(cursor, parent) }
    }

    private fun vTemplatesFromSynsetIdWordIdCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val word = "---"
            val vTemplateId = cursor.getColumnIndex(Senses_VerbTemplates.TEMPLATE)
            val sb = SpannableStringBuilder()
            do {
                val vTemplate = cursor.getString(vTemplateId)
                val formattedVTemplate = String.format(Locale.ENGLISH, vTemplate, "[$word]")
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, verbframeDrawable)
                sb.append(' ')
                sb.append(formattedVTemplate)
            } while (cursor.moveToNext())

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

    // A D J P O S I T I O N S

    /**
     * Adjective positions
     *
     * @param synsetId synset id
     * @param parent   parent node
     */
    fun adjPosition(synsetId: Long, parent: TreeNode) {
        val sql = Queries.prepareAdjPosition(synsetId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        adjPositionFromSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> adjPositionCursorToTreeModel(cursor, parent) }
    }

    /**
     * Adjective positions
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param parent   parent node
     */
    fun adjPosition(synsetId: Long, wordId: Long, parent: TreeNode) {
        val sql = Queries.prepareAdjPosition(synsetId, wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        adjPositionFromSynsetIdWordIdModel.loadData(uri, sql) { cursor: Cursor -> adjPositionCursorToTreeModel(cursor, parent) }
    }

    private fun adjPositionCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val positionId = cursor.getColumnIndex(Senses_AdjPositions.POSITION)
            val sb = SpannableStringBuilder()
            do {
                val position = cursor.getString(positionId)
                val formattedPosition = String.format(Locale.ENGLISH, "%s", position)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, verbframeDrawable)
                sb.append(' ')
                sb.append(formattedPosition)
            } while (cursor.moveToNext())

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

    // M O R P H S

    /**
     * Morphology
     *
     * @param wordId word id
     * @param parent parent node
     */
    fun morphs(wordId: Long, parent: TreeNode) {
        val sql = Queries.prepareMorphs(wordId)
        val uri = Uri.parse(WordNetProvider.makeUri(sql.providerUri))
        morphsFromWordIdModel.loadData(uri, sql) { cursor: Cursor -> morphsCursorToTreeModel(cursor, parent) }
    }

    private fun morphsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val morphId = cursor.getColumnIndex(Lexes_Morphs.MORPH)
            val posId = cursor.getColumnIndex(Lexes_Morphs.POSID)
            val sb = SpannableStringBuilder()
            do {
                val morph1 = cursor.getString(morphId)
                val pos1 = cursor.getString(posId)
                val formattedMorph = String.format(Locale.ENGLISH, "(%s) %s", pos1, morph1)
                if (sb.isNotEmpty()) {
                    sb.append('\n')
                }
                appendImage(sb, morphDrawable)
                sb.append(' ')
                sb.append(formattedMorph)
            } while (cursor.moveToNext())

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

    // H E L P E R S

    /**
     * Match relation id to drawable resource id
     *
     * @param relationId relation id
     * @return kink res id
     */
    private fun getRelationRes(relationId: Int): Int {
        return when (relationId) {
            1 -> R.drawable.ic_hypernym
            2 -> R.drawable.ic_hyponym
            3 -> R.drawable.ic_instance_hypernym
            4 -> R.drawable.ic_instance_hyponym
            11 -> R.drawable.ic_part_holonym
            12 -> R.drawable.ic_part_meronym
            13 -> R.drawable.ic_member_holonym
            14 -> R.drawable.ic_member_meronym
            15 -> R.drawable.ic_substance_holonym
            16 -> R.drawable.ic_substance_meronym
            21 -> R.drawable.ic_entails
            22 -> R.drawable.ic_entailed
            23 -> R.drawable.ic_causes
            24 -> R.drawable.ic_caused
            30 -> R.drawable.ic_antonym
            40 -> R.drawable.ic_similar
            50 -> R.drawable.ic_also
            60 -> R.drawable.ic_attribute
            70 -> R.drawable.ic_verb_group
            71 -> R.drawable.ic_participle
            80 -> R.drawable.ic_pertainym
            81 -> R.drawable.ic_derivation
            91 -> R.drawable.ic_domain_topic
            92 -> R.drawable.ic_domain_member_topic
            93 -> R.drawable.ic_domain_region
            94 -> R.drawable.ic_domain_member_region
            95 -> R.drawable.ic_exemplifies
            96 -> R.drawable.ic_exemplified
            97 -> R.drawable.ic_domain
            98 -> R.drawable.ic_domain_member
            99 -> R.drawable.ic_other
            100 -> R.drawable.ic_state
            101 -> R.drawable.ic_result
            102 -> R.drawable.ic_event
            110 -> R.drawable.ic_property
            120 -> R.drawable.ic_location
            121 -> R.drawable.ic_destination
            130 -> R.drawable.ic_agent
            131 -> R.drawable.ic_undergoer
            140 -> R.drawable.ic_uses
            141 -> R.drawable.ic_instrument
            142 -> R.drawable.ic_bymeansof
            150 -> R.drawable.ic_material
            160 -> R.drawable.ic_vehicle
            170 -> R.drawable.ic_bodypart
            200 -> R.drawable.ic_collocation
            else -> R.drawable.error
        }
    }

    // Q U E R I E S

    /**
     * Relations query
     *
     * @param synsetId synset id
     * @param wordId   word id
     */
    internal inner class RelationsQuery(
        synsetId: Long,
        val wordId: Long,
    ) : Query(synsetId) {

        override fun process(node: TreeNode) {
            relations(id, wordId, node, true)

            // sem relations
            //semRelations(this.id, node, false)

            // lex relations
            //lexRelations(this.id, this.wordId, node, false)
        }

        override fun toString(): String {
            return "relations for $id,$wordId"
        }
    }

    /**
     * Semantic Relation query
     *
     * @param synsetId synset id
     */
    internal inner class SemRelationsQuery(synsetId: Long) : Query(synsetId) {

        override fun process(node: TreeNode) {
            semRelations(id, node, true)
        }

        override fun toString(): String {
            return "semrelations for $id"
        }
    }

    /**
     * Lexical Relation query
     *
     * @param synsetId synset id
     * @param wordId   word id
     */
    internal inner class LexRelationsQuery(
        synsetId: Long,
        val wordId: Long,
    ) : Query(synsetId) {

        override fun process(node: TreeNode) {
            lexRelations(id, wordId, node, true)
        }

        override fun toString(): String {
            return "lexrelations for $id,$wordId"
        }
    }

    /**
     * Sub relations of give type query
     *
     * @param synsetId     synset id
     * @param relationId   relation id
     * @param recurseLevel recurse level
     * @param hot          whether result nodes are hot queries
     */
    internal inner class SubRelationsQuery @JvmOverloads constructor(
        synsetId: Long,
        private val relationId: Int,
        private val recurseLevel: Int,
        private val hot: Boolean = false,
    ) : Query(synsetId) {

        override fun process(node: TreeNode) {
            semRelations(id, relationId, recurseLevel, hot, node, true)
        }

        override fun toString(): String {
            return "sub semrelations of type $relationId for $id at level $recurseLevel"
        }
    }

    /**
     * Samples query
     *
     * @param synsetId synset id
     */
    internal inner class SamplesQuery(synsetId: Long) : Query(synsetId) {

        override fun process(node: TreeNode) {
            samples(id, node, true)
        }

        override fun toString(): String {
            return "samples for $id"
        }
    }

    /**
     * Usages query
     *
     * @param synsetId synset id
     */
    internal inner class UsagesQuery(synsetId: Long) : Query(synsetId) {

        override fun process(node: TreeNode) {
            usages(id, node, false)
        }

        override fun toString(): String {
            return "usages for $id"
        }
    }

    /**
     * Ili query
     *
     * @param synsetId synset id
     */
    internal inner class IliQuery(synsetId: Long) : Query(synsetId) {

        override fun process(node: TreeNode) {
            ili(id, node, false)
        }

        override fun toString(): String {
            return "ili for $id"
        }
    }

    /**
     * Wikidata query
     *
     * @param synsetId synset id
     */
    internal inner class WikidataQuery(synsetId: Long) : Query(synsetId) {

        override fun process(node: TreeNode) {
            wikidata(id, node, false)
        }

        override fun toString(): String {
            return "wikidata for $id"
        }
    }

    /**
     * Word link data
     *
     * @param wordId   word id
     * @param fragment fragment
     */
    open class BaseWordLink(wordId: Long, protected val fragment: Fragment) : Link(wordId) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = WordPointer(id)
            val intent = Intent(context, WordActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.action = ProviderArgs.ACTION_QUERY
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "word for $id"
        }
    }

    /**
     * Word link data
     *
     * @param wordId   word id
     * @param fragment fragment
     */
    class WordLink internal constructor(fragment: Fragment, wordId: Long) : BaseWordLink(wordId, fragment)

    /**
     * Synset link data
     *
     * @param synsetId synset id
     * @param recurse  max recursion level
     * @param fragment fragment
     */
    open class BaseSynsetLink(synsetId: Long, val recurse: Int, protected val fragment: Fragment) : Link(synsetId) {

        val parameters: Bundle?

        init {
            val args = fragment.arguments
            parameters = args?.getBundle(ProviderArgs.ARG_RENDERPARAMETERS)
        }

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = SynsetPointer(id)
            val intent = Intent(context, SynsetActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse)
            intent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
            intent.action = ProviderArgs.ACTION_QUERY
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "synset for $id"
        }
    }

    /**
     * Synset link data
     *
     * @param synsetId synset id
     * @param recurse  max recursion level
     * @param fragment fragment
     */
    class SynsetLink internal constructor(synsetId: Long, recurse: Int, fragment: Fragment) : BaseSynsetLink(synsetId, recurse, fragment)

    /**
     * Relations link data
     *
     * @param synsetId synset id
     * @param recurse  max recursion level
     * @param fragment fragment
     */
    class RelationLink(synsetId: Long, val recurse: Int, private val fragment: Fragment) : Link(synsetId) {

        val parameters: Bundle?

        init {
            val args = fragment.arguments
            parameters = args?.getBundle(ProviderArgs.ARG_RENDERPARAMETERS)
        }

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = SynsetPointer(id)
            val intent = Intent(context, RelationActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse)
            intent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
            intent.action = ProviderArgs.ACTION_QUERY
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "relation for $id"
        }
    }

    /**
     * Sense link data
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param recurse  recurse
     * @param fragment fragment
     */
    open class BaseSenseLink(synsetId: Long, private val wordId: Long, recurse: Int, fragment: Fragment) : BaseSynsetLink(synsetId, recurse, fragment) {

        override fun process() {
            val context = fragment.context ?: return
            val pointer: Parcelable = SensePointer(id, wordId)
            val intent = Intent(context, SynsetActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
            intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
            intent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse)
            intent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
            intent.action = ProviderArgs.ACTION_QUERY
            context.startActivity(intent)
        }

        override fun toString(): String {
            return "sense for $id,$wordId"
        }
    }

    /**
     * Sense link data
     *
     * @param synsetId synset id
     * @param wordId   word id
     * @param recurse  max recursion level
     * @param fragment fragment
     */
    class SenseLink internal constructor(synsetId: Long, wordId: Long, recurse: Int, fragment: Fragment) : BaseSenseLink(synsetId, wordId, recurse, fragment)

    companion object {

        private const val TAG = "BaseModule"
    }
}
