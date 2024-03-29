/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.loaders

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeLinkNode
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.syntagnet.R
import org.sqlunet.syntagnet.provider.SyntagNetContract
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X
import org.sqlunet.syntagnet.provider.SyntagNetProvider
import org.sqlunet.syntagnet.style.SyntagNetFactories
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import org.sqlunet.wordnet.loaders.BaseModule.BaseSynsetLink
import org.sqlunet.wordnet.loaders.BaseModule.BaseWordLink

/**
 * Module for SyntagNet collocations
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule(fragment: TreeFragment) : Module(fragment) {

    // view models

    private lateinit var collocationFromCollocationIdModel: SqlunetViewTreeModel
    private lateinit var collocationsFromWordIdModel: SqlunetViewTreeModel
    private lateinit var collocationsFromWordModel: SqlunetViewTreeModel

    init {
        makeModels()
    }

    /**
     * Whether target comes second in collocation
     */
    protected abstract fun isTargetSecond(word1Id: Long, word2Id: Long): Boolean

    /**
     * Make view models
     */
    private fun makeModels() {
        collocationFromCollocationIdModel = ViewModelProvider(fragment)["sn.collocation(collocationid)", SqlunetViewTreeModel::class.java]
        collocationFromCollocationIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        collocationsFromWordIdModel = ViewModelProvider(fragment)["sn.collocations(wordid)", SqlunetViewTreeModel::class.java]
        collocationsFromWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        collocationsFromWordModel = ViewModelProvider(fragment)["sn.collocations(word)", SqlunetViewTreeModel::class.java]
        collocationsFromWordModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    // C O L L O C A T I O N S

    /**
     * Collocation from id
     *
     * @param collocationId collocation id
     * @param parent        parent node
     */
    fun collocation(collocationId: Long, parent: TreeNode) {
        val sql = Queries.prepareCollocation(collocationId)
        val uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri))
        collocationFromCollocationIdModel.loadData(uri, sql) { cursor: Cursor -> collocationCursorToTreeModel(cursor, parent) }
    }

    /**
     * Collocation from ids
     *
     * @param word1Id   word 1 id
     * @param word2Id   word 2 id
     * @param synset1Id synset 1 id
     * @param synset2Id synset 2 id
     * @param parent    parent node
     */
    fun collocations(word1Id: Long?, word2Id: Long?, synset1Id: Long?, synset2Id: Long?, parent: TreeNode) {
        val sql = Queries.prepareCollocations(word1Id, word2Id, synset1Id, synset2Id)
        val uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri))
        collocationFromCollocationIdModel.loadData(uri, sql) { cursor: Cursor -> collocationsCursorToTreeModel(cursor, parent) }
    }

    /**
     * Collocations for word id
     *
     * @param wordId word id
     * @param parent parent node
     */
    fun collocations(wordId: Long, parent: TreeNode) {
        val sql = Queries.prepareCollocations(wordId)
        val uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri))
        collocationsFromWordIdModel.loadData(uri, sql) { cursor: Cursor -> collocationsCursorToTreeModel(cursor, parent) }
    }

    /**
     * Collocations for word
     *
     * @param word word	 * @param parent parent node
     */
    fun collocations(word: String, parent: TreeNode) {
        val sql = Queries.prepareCollocations(word)
        val uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri))
        collocationsFromWordModel.loadData(uri, sql) { cursor: Cursor -> collocationsCursorToTreeModel(cursor, parent) }
    }

    private fun collocationCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        return collocationsCursorToTreeModel(cursor, parent)
    }

    private fun collocationsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idCollocationId = cursor.getColumnIndex(SnCollocations_X.COLLOCATIONID)
            val idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID)
            val idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID)
            val idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID)
            val idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID)
            val idWord1 = cursor.getColumnIndex(SyntagNetContract.WORD1)
            val idWord2 = cursor.getColumnIndex(SyntagNetContract.WORD2)
            val idDefinition1 = cursor.getColumnIndex(SyntagNetContract.DEFINITION1)
            val idDefinition2 = cursor.getColumnIndex(SyntagNetContract.DEFINITION2)
            val idPos1 = cursor.getColumnIndex(SyntagNetContract.POS1)
            val idPos2 = cursor.getColumnIndex(SyntagNetContract.POS2)

            // read cursor
            val isSingle = cursor.count == 1
            do {
                // data
                val collocationId = cursor.getInt(idCollocationId)
                val word1 = cursor.getString(idWord1)
                val word2 = cursor.getString(idWord2)
                val word1Id = cursor.getLong(idWord1Id)
                val word2Id = cursor.getLong(idWord2Id)
                val synset1Id = cursor.getLong(idSynset1Id)
                val synset2Id = cursor.getLong(idSynset2Id)
                val pos1 = cursor.getString(idPos1)
                val pos2 = cursor.getString(idPos2)
                val definition1 = cursor.getString(idDefinition1)
                val definition2 = cursor.getString(idDefinition2)
                val isTargetSecond = isTargetSecond(word1Id, word2Id)
                makeContent(collocationId, word1, word2, word1Id, word2Id, synset1Id, synset2Id, pos1, pos2, definition1, definition2, isSingle, isTargetSecond, parent, changedList)

                // sub nodes
                //TODO more node
                //var moreNode = TreeFactory.makeLinkHotQueryNode("More", R.drawable.more, false, new MoreQuery(collocationId)).addTo(parent)
                //var more2Node = TreeFactory.makeQueryNode("More2", R.drawable.more2, false, new More2Query(collocationId)).addTo(parent)
                //changed = TreeOp.seq(NEWMAIN, node, NEWEXTRA, moreNode, NEWEXTRA, more2Node, NEWTREE, parent)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    @SuppressLint("DefaultLocale")
    private fun makeContent(
        collocationId: Int,
        word1: String, word2: String,
        word1Id: Long, word2Id: Long,
        synset1Id: Long, synset2Id: Long,
        pos1: String, pos2: String,
        definition1: String, definition2: String,
        isSingle: Boolean, isTargetSecond: Boolean, parent: TreeNode, changedList: TreeOps,
    ) {
        // header
        val sbh = SpannableStringBuilder()
        append(sbh, word1, 0, SyntagNetFactories.collocationFactory)
        sbh.append(' ')
        sbh.append(pos1)
        sbh.append(' ')
        append(sbh, word2, 0, SyntagNetFactories.collocationFactory)
        sbh.append(' ')
        sbh.append(pos2)
        sbh.append(' ')
        append(sbh, collocationId.toString(), 0, SyntagNetFactories.idsFactory)

        // collocation
        val collocationNode = makeTreeNode(sbh, R.drawable.collocation, !isSingle).addTo(parent)
        changedList.add(TreeOpCode.NEWCHILD, collocationNode)

        // contents

        // collocation 1
        val sb1w = SpannableStringBuilder()
        append(sb1w, word1, 0, if (isTargetSecond) SyntagNetFactories.word2Factory else SyntagNetFactories.word1Factory)
        val link1w: Link = BaseWordLink(word1Id, fragment)
        val collocation1wNode = makeLinkNode(sb1w, if (isTargetSecond) R.drawable.collocation2 else R.drawable.collocation1, false, link1w).addTo(collocationNode)
        changedList.add(TreeOpCode.NEWCHILD, collocation1wNode)
        val sb1s = SpannableStringBuilder()
        append(sb1s, definition1, 0, if (isTargetSecond) SyntagNetFactories.definition2Factory else SyntagNetFactories.definition1Factory)
        val link1s: Link = BaseSynsetLink(synset1Id, Int.MAX_VALUE, fragment)
        val collocation1sNode = makeLinkNode(sb1s, if (isTargetSecond) R.drawable.definition2 else R.drawable.definition1, false, link1s).addTo(collocationNode)
        changedList.add(TreeOpCode.NEWCHILD, collocation1sNode)

        // collocation 2
        val sb2w = SpannableStringBuilder()
        append(sb2w, word2, 0, if (isTargetSecond) SyntagNetFactories.word1Factory else SyntagNetFactories.word2Factory)
        val link2w: Link = BaseWordLink(word2Id, fragment)
        val collocation2wNode = makeLinkNode(sb2w, if (isTargetSecond) R.drawable.collocation1 else R.drawable.collocation2, false, link2w).addTo(collocationNode)
        changedList.add(TreeOpCode.NEWCHILD, collocation2wNode)
        val sb2s = SpannableStringBuilder()
        append(sb2s, definition2, 0, if (isTargetSecond) SyntagNetFactories.definition1Factory else SyntagNetFactories.definition2Factory)
        val link2s: Link = BaseSynsetLink(synset2Id, Int.MAX_VALUE, fragment)
        val collocation2sNode = makeLinkNode(sb2s, if (isTargetSecond) R.drawable.definition1 else R.drawable.definition2, false, link2s).addTo(collocationNode)
        changedList.add(TreeOpCode.NEWCHILD, collocation2sNode)

        // ids
        // val sbi = new SpannableStringBuilder()
        // Spanner.appendImage(sbi, BaseModule.this.infoDrawable)
        // Spanner.append(sbi, String.format(" %d %d , %d %d", word1Id, synset1Id, word2Id, synset2Id), 0, SyntagNetFactories.idsFactory)

        // val extraNode = TreeFactory.makeTextNode(sbi, false).addTo(collocationNode)
        // changedList.add(NEWCHILD, extraNode)
    }
}
