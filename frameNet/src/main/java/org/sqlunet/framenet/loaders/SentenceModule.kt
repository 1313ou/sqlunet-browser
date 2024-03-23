/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.framenet.FnSentencePointer
import org.sqlunet.framenet.loaders.Queries.prepareSentence
import org.sqlunet.framenet.provider.FrameNetContract.Sentences
import org.sqlunet.framenet.provider.FrameNetProvider
import org.sqlunet.framenet.style.FrameNetFactories
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOpExecute

/**
 * Sentence module
 *
 * @param fragment containing fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SentenceModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Sentence id
     */
    private var sentenceId: Long? = null

    /**
     * Sentence text
     */
    private var sentenceText: String? = null

    // view models

    private lateinit var sentenceFromSentenceIdModel: SqlunetViewTreeModel

    init {
        makeModels()
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        sentenceFromSentenceIdModel = ViewModelProvider(fragment)["fn.sentence(sentenceid)", SqlunetViewTreeModel::class.java]
        sentenceFromSentenceIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    override fun unmarshal(pointer: Parcelable) {
        sentenceId = null
        if (pointer is FnSentencePointer) {
            sentenceId = pointer.id
        }
    }

    override fun process(node: TreeNode) {
        if (sentenceId != null) {
            sentence(sentenceId!!, node)
        }
    }

    // L O A D E R S

    /**
     * Sentence
     *
     * @param sentenceId sentence id
     * @param parent     parent node
     */
    private fun sentence(sentenceId: Long, parent: TreeNode) {
        val sql = prepareSentence(sentenceId)
        val uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri))
        sentenceFromSentenceIdModel.loadData(uri, sql) { cursor: Cursor -> sentenceCursorToTreeModel(cursor, parent) }
    }

    private fun sentenceCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()
            val idSentenceId = cursor.getColumnIndex(Sentences.SENTENCEID)
            val idText = cursor.getColumnIndex(Sentences.TEXT)

            // data
            sentenceText = cursor.getString(idText)
            val id = cursor.getLong(idSentenceId)
            append(sb, sentenceText, 0, FrameNetFactories.sentenceFactory)

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)

            // layers
            layersForSentence(id, sentenceText!!, parent)
            changed = seq(TreeOpCode.NEWUNIQUE, node)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }
}
