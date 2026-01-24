/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.loaders

import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.HasPos
import org.sqlunet.HasWordId
import org.sqlunet.bnc.R
import org.sqlunet.bnc.loaders.Queries.prepareBnc
import org.sqlunet.bnc.provider.BNCContract.Words_BNCs
import org.sqlunet.bnc.provider.BNCProvider.Companion.makeUri
import org.sqlunet.bnc.style.BNCFactories
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory
import org.sqlunet.style.Spanner
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOpExecute
import androidx.core.net.toUri
import org.sqlunet.xnet.R as XNetR

class BaseModule(fragment: TreeFragment) : Module(fragment) {

    // Query

    private var wordId: Long? = null
    private var pos: Char? = null

    // Resources

    /**
     * Drawable for conv/task
     */
    private val convtaskDrawable: Drawable

    /**
     * Drawable for spoken/written
     */
    private val imaginfDrawable: Drawable

    /**
     * Drawable for spoken/written
     */
    private val spwrDrawable: Drawable

    /**
     * Drawable for pos
     */
    private val posDrawable: Drawable

    // View models

    /**
     * View model
     */
    private var bncFromWordIdModel: SqlunetViewTreeModel? = null

    init {

        // models
        makeModels()

        // drawables
        val context = this.fragment.requireContext()
        convtaskDrawable = Spanner.getDrawable(context, R.drawable.convtask)
        imaginfDrawable = Spanner.getDrawable(context, R.drawable.imaginf)
        spwrDrawable = Spanner.getDrawable(context, R.drawable.spwr)
        posDrawable = Spanner.getDrawable(context, XNetR.drawable.pos)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        bncFromWordIdModel = ViewModelProvider(fragment)["bnc.bnc(wordid)", SqlunetViewTreeModel::class.java]
        bncFromWordIdModel!!.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data!!) }
    }

    override fun unmarshal(pointer: Parcelable) {
        wordId = null
        pos = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
        if (pointer is HasPos) {
            val posPointer = pointer as HasPos
            pos = posPointer.getPos()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null) {
            // data
            bnc(wordId!!, pos, node)
        }
    }

    // L O A D E R S

    // contents

    /**
     * Load BNC data
     *
     * @param wordId word id
     * @param pos    pos
     * @param parent parent node
     */
    private fun bnc(wordId: Long, pos: Char?, parent: TreeNode) {
        val sql = prepareBnc(wordId, pos)
        val uri = makeUri(sql.providerUri).toUri()
        bncFromWordIdModel!!.loadData(uri, sql) { cursor: Cursor -> bncCursorToTreeModel(cursor, parent) }
    }

    private fun bncCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        val sb = SpannableStringBuilder()
        // if (cursor.getCount() > 1)
        // throw RuntimeException("Unexpected number of rows")
        if (cursor.moveToFirst()) {
            val idPos = cursor.getColumnIndexOrThrow(Words_BNCs.POSID)
            val idFreq = cursor.getColumnIndexOrThrow(Words_BNCs.FREQ)
            val idRange = cursor.getColumnIndexOrThrow(Words_BNCs.RANGE)
            val idDisp = cursor.getColumnIndexOrThrow(Words_BNCs.DISP)
            val idConvFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ1)
            val idConvRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE1)
            val idConvDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP1)
            val idTaskFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ2)
            val idTaskRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE2)
            val idTaskDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP2)
            val idImagFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ1)
            val idImagRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE1)
            val idImagDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP1)
            val idInfFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ2)
            val idInfRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE2)
            val idInfDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP2)
            val idSpFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.FREQ1)
            val idSpRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.RANGE1)
            val idSpDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.DISP1)
            val idWrFreq = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.FREQ2)
            val idWrRange = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.RANGE2)
            val idWrDisp = cursor.getColumnIndexOrThrow(Words_BNCs.BNCSPWRS + Words_BNCs.DISP2)
            do {
                val pos1 = cursor.getString(idPos)
                Spanner.appendImage(sb, posDrawable)
                sb.append(' ')
                sb.append(pos1)
                sb.append('\n')
                var value1: String?
                if (cursor.getString(idFreq).also { value1 = it } != null) {
                    sb.append("frequency=").append(value1).append(" per million").append('\n')
                }
                if (cursor.getString(idRange).also { value1 = it } != null) {
                    sb.append("range=").append(value1).append('\n')
                }
                if (cursor.getString(idDisp).also { value1 = it } != null) {
                    sb.append("dispersion=").append(value1)
                }
                sb.append('\n')
                var fvalue = cursor.getString(idConvFreq)
                var fvalue2 = cursor.getString(idTaskFreq)
                var rvalue = cursor.getString(idConvRange)
                var rvalue2 = cursor.getString(idTaskRange)
                var dvalue = cursor.getString(idConvDisp)
                var dvalue2 = cursor.getString(idTaskDisp)
                if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null) {
                    Spanner.appendImage(sb, convtaskDrawable)
                    sb.append(' ')
                    Spanner.append(sb, "conversation / task\n", 0, BNCFactories.headerFactory)
                    if (fvalue != null && fvalue2 != null) {
                        sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n')
                    }
                    if (rvalue != null && rvalue2 != null) {
                        sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n')
                    }
                    if (dvalue != null && dvalue2 != null) {
                        sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2)
                    }
                    sb.append('\n')
                }
                fvalue = cursor.getString(idImagFreq)
                fvalue2 = cursor.getString(idInfFreq)
                rvalue = cursor.getString(idImagRange)
                rvalue2 = cursor.getString(idInfRange)
                dvalue = cursor.getString(idImagDisp)
                dvalue2 = cursor.getString(idInfDisp)
                if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null) {
                    Spanner.appendImage(sb, imaginfDrawable)
                    sb.append(' ')
                    Spanner.append(sb, "imagination / information\n", 0, BNCFactories.headerFactory)
                    if (fvalue != null && fvalue2 != null) {
                        sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n')
                    }
                    if (rvalue != null && rvalue2 != null) {
                        sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n')
                    }
                    if (dvalue != null && dvalue2 != null) {
                        sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2)
                    }
                    sb.append('\n')
                }
                fvalue = cursor.getString(idSpFreq)
                fvalue2 = cursor.getString(idWrFreq)
                rvalue = cursor.getString(idSpRange)
                rvalue2 = cursor.getString(idWrRange)
                dvalue = cursor.getString(idSpDisp)
                dvalue2 = cursor.getString(idWrDisp)
                if (fvalue != null || fvalue2 != null || rvalue != null || rvalue2 != null || dvalue != null || dvalue2 != null) {
                    Spanner.appendImage(sb, spwrDrawable)
                    sb.append(' ')
                    Spanner.append(sb, "spoken / written\n", 0, BNCFactories.headerFactory)
                    if (fvalue != null && fvalue2 != null) {
                        sb.append("frequency=").append(fvalue).append(" / ").append(fvalue2).append('\n')
                    }
                    if (rvalue != null && rvalue2 != null) {
                        sb.append("range=").append(rvalue).append(" / ").append(rvalue2).append('\n')
                    }
                    if (dvalue != null && dvalue2 != null) {
                        sb.append("dispersion=").append(dvalue).append(" / ").append(dvalue2)
                    }
                    sb.append('\n')
                }
            } while (cursor.moveToNext())

            // attach result
            val node = TreeFactory.makeTextNode(sb, false).addTo(parent)
            changed = TreeOp.seq(TreeOpCode.NEWUNIQUE, node)
        } else {
            TreeFactory.setNoResult(parent)
            changed = TreeOp.seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }
}
