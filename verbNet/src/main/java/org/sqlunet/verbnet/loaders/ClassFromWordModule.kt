/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.loaders

import android.database.Cursor
import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.HasSynsetId
import org.sqlunet.HasWordId
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeQueryTreeNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.verbnet.R
import org.sqlunet.verbnet.loaders.Queries.prepareVnClasses
import org.sqlunet.verbnet.provider.VerbNetContract.Words_VnClasses
import org.sqlunet.verbnet.provider.VerbNetProvider
import org.sqlunet.verbnet.style.VerbNetFactories
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import androidx.core.net.toUri
import org.sqlunet.xnet.R as XNetR

/**
 * VerbNet class from word/sense module
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class ClassFromWordModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Word id
     */
    private var wordId: Long? = null

    /**
     * Synset id (null=ignore)
     */
    private var synsetId: Long? = null

    // view models

    private lateinit var vnClassesFromWordIdSynsetIdModel: SqlunetViewTreeModel

    init {
        makeModels()
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        vnClassesFromWordIdSynsetIdModel = ViewModelProvider(fragment)["vn.classes(wordid,synsetid)", SqlunetViewTreeModel::class.java]
        vnClassesFromWordIdSynsetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    override fun unmarshal(pointer: Parcelable) {
        wordId = null
        synsetId = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
        if (pointer is HasSynsetId) {
            val synsetPointer = pointer as HasSynsetId
            synsetId = synsetPointer.getSynsetId()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null) {
            vnClasses(wordId!!, synsetId, node)
        } else {
            setNoResult(node)
        }
    }

    // L O A D E R S

    /**
     * Classes from word id and synset id
     *
     * @param wordId   word id
     * @param synsetId synset id (null or 0 means ignore)
     * @param parent   parent node
     */
    private fun vnClasses(wordId: Long, synsetId: Long?, parent: TreeNode) {
        val sql = prepareVnClasses(wordId, synsetId)
        val uri = VerbNetProvider.makeUri(sql.providerUri).toUri()
        vnClassesFromWordIdSynsetIdModel.loadData(uri, sql) { cursor: Cursor -> vnClassesCursorToTreeModel(cursor, parent) }
    }

    private fun vnClassesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idClassId = cursor.getColumnIndex(Words_VnClasses.CLASSID)
            val idClass = cursor.getColumnIndex(Words_VnClasses.CLASS)
            // val idClassTag = cursor.getColumnIndex(Words_VnClasses.CLASSTAG)

            // read cursor
            do {
                val sb = SpannableStringBuilder()

                // data
                val classId = cursor.getInt(idClassId)
                val vnClass = cursor.getString(idClass)

                // sb.append("[class]")
                appendImage(sb, drawableRoles)
                sb.append(' ')
                append(sb, vnClass, 0, VerbNetFactories.classFactory)
                // sb.append(" tag=")
                // sb.append(cursor.getString(idClassTag))
                sb.append(" id=")
                sb.append(classId.toString())

                // attach result
                val node = makeTextNode(sb, false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, node)

                // sub nodes
                val membersNode = makeHotQueryTreeNode(membersLabel, XNetR.drawable.members, false, MembersQuery(classId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, membersNode)
                val rolesNode = makeHotQueryTreeNode(rolesLabel, XNetR.drawable.roles, false, RolesQuery(classId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, rolesNode)
                val framesNode = makeQueryTreeNode(framesLabel, R.drawable.vnframe, false, FramesQuery(classId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, framesNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }
}
