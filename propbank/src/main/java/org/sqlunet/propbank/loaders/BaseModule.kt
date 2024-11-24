/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.loaders

import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeQueryTreeNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.propbank.R
import org.sqlunet.propbank.loaders.Queries.prepareExamples
import org.sqlunet.propbank.loaders.Queries.prepareRoleSet
import org.sqlunet.propbank.loaders.Queries.prepareRoleSets
import org.sqlunet.propbank.loaders.Queries.prepareRoles
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbExamples
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_PbRoles
import org.sqlunet.propbank.provider.PropBankContract.PbRoleSets_X
import org.sqlunet.propbank.provider.PropBankContract.Words_PbRoleSets
import org.sqlunet.propbank.provider.PropBankProvider
import org.sqlunet.propbank.style.PropBankFactories
import org.sqlunet.propbank.style.PropBankSpanner
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.treeview.control.Query
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute
import java.util.Arrays

/**
 * Module for PropBank role sets
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule(fragment: TreeFragment) : Module(fragment) {

    // resources

    private val rolesLabel: String

    private val examplesLabel: String

    /**
     * Drawable for roleSets
     */
    private val roleSetDrawable: Drawable

    /**
     * Drawable for roles
     */
    private val rolesDrawable: Drawable

    /**
     * Drawable for relation
     */
    private val relationDrawable: Drawable

    /**
     * Drawable for role
     */
    private val roleDrawable: Drawable

    /**
     * Drawable for alias
     */
    private val aliasDrawable: Drawable

    /**
     * Drawable for vnrole
     */
    private val vnroleDrawable: Drawable

    /**
     * Drawable for fnfe
     */
    private val fnfeDrawable: Drawable

    /**
     * Drawable for definition
     */
    private val definitionDrawable: Drawable

    /**
     * Drawable for sample
     */
    private val sampleDrawable: Drawable

    // agents

    /**
     * Spanner
     */
    private val spanner: PropBankSpanner

    // view models

    private lateinit var pbRoleSetFromRoleSetIdModel: SqlunetViewTreeModel
    private lateinit var roleSetsFromWordIdModel: SqlunetViewTreeModel
    private lateinit var rolesFromRoleSetIdModel: SqlunetViewTreeModel
    private lateinit var examplesFromRoleSetIdModel: SqlunetViewTreeModel

    init {

        // models
        makeModels()

        // drawables
        val context = this@BaseModule.fragment.requireContext()
        rolesLabel = context.getString(R.string.propbank_roles_)
        examplesLabel = context.getString(R.string.propbank_examples_)
        roleSetDrawable = getDrawable(context, R.drawable.roleclass)
        rolesDrawable = getDrawable(context, R.drawable.roles)
        relationDrawable = getDrawable(context, R.drawable.relation)
        roleDrawable = getDrawable(context, R.drawable.role)
        vnroleDrawable = getDrawable(context, R.drawable.vnrole)
        fnfeDrawable = getDrawable(context, R.drawable.fnfe)
        aliasDrawable = getDrawable(context, R.drawable.alias)
        definitionDrawable = getDrawable(context, R.drawable.definition)
        sampleDrawable = getDrawable(context, R.drawable.sample)

        // spanner
        spanner = PropBankSpanner(context)
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        pbRoleSetFromRoleSetIdModel = ViewModelProvider(fragment)["pb.roleset(rolesetid)", SqlunetViewTreeModel::class.java]
        pbRoleSetFromRoleSetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        roleSetsFromWordIdModel = ViewModelProvider(fragment)["pb.rolesets(wordid)", SqlunetViewTreeModel::class.java]
        roleSetsFromWordIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        rolesFromRoleSetIdModel = ViewModelProvider(fragment)["pb.roles(rolesetid)", SqlunetViewTreeModel::class.java]
        rolesFromRoleSetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        examplesFromRoleSetIdModel = ViewModelProvider(fragment)["pb.examples(rolesetid)", SqlunetViewTreeModel::class.java]
        examplesFromRoleSetIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    // R O L E   S E T S

    /**
     * Role set from id
     *
     * @param roleSetId role set id
     * @param parent    parent node
     */
    fun roleSet(roleSetId: Long, parent: TreeNode) {
        val sql = prepareRoleSet(roleSetId)
        val uri = Uri.parse(PropBankProvider.makeUri(sql.providerUri))
        pbRoleSetFromRoleSetIdModel.loadData(uri, sql) { cursor: Cursor -> roleSetCursorToTreeModel(cursor, roleSetId, parent) }
    }

    private fun roleSetCursorToTreeModel(cursor: Cursor, roleSetId: Long, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            // column indices
            // var idRoleSetId = cursor.getColumnIndex(PbRoleSets_X.ROLESETID)
            val idRoleSetName = cursor.getColumnIndex(PbRoleSets_X.ROLESETNAME)
            val idRoleSetDesc = cursor.getColumnIndex(PbRoleSets_X.ROLESETDESC)
            val idRoleSetHead = cursor.getColumnIndex(PbRoleSets_X.ROLESETHEAD)
            val idAliases = cursor.getColumnIndex(PbRoleSets_X.ALIASES)

            // read cursor
            val sb = SpannableStringBuilder()

            // data
            // var roleSetId = cursor.getInt(idRoleSetId)

            // roleSet
            appendImage(sb, roleSetDrawable)
            sb.append(' ')
            append(sb, cursor.getString(idRoleSetName), 0, PropBankFactories.roleSetFactory)
            sb.append(' ')
            sb.append("head=")
            sb.append(cursor.getString(idRoleSetHead))
            sb.append('\n')
            appendImage(sb, aliasDrawable)
            sb.append(cursor.getString(idAliases))
            sb.append('\n')

            // description
            appendImage(sb, definitionDrawable)
            append(sb, cursor.getString(idRoleSetDesc), 0, PropBankFactories.definitionFactory)

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)

            // sub nodes
            val rolesNode = makeHotQueryTreeNode(rolesLabel, R.drawable.roles, false, RolesQuery(roleSetId)).addTo(parent)
            val examplesNode = makeQueryTreeNode(examplesLabel, R.drawable.sample, false, ExamplesQuery(roleSetId)).addTo(parent)
            changed = seq(TreeOpCode.NEWMAIN, node, TreeOpCode.NEWEXTRA, rolesNode, TreeOpCode.NEWEXTRA, examplesNode, TreeOpCode.NEWTREE, parent)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    /**
     * Role sets for word id
     *
     * @param wordId word id
     * @param parent parent node
     */
    fun roleSets(wordId: Long, parent: TreeNode) {
        val sql = prepareRoleSets(wordId)
        val uri = Uri.parse(PropBankProvider.makeUri(sql.providerUri))
        roleSetsFromWordIdModel.loadData(uri, sql) { cursor: Cursor -> roleSetsCursorToTreeModel(cursor, parent) }
    }

    private fun roleSetsCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            val idRoleSetId = cursor.getColumnIndex(Words_PbRoleSets.ROLESETID)
            val idRoleSetName = cursor.getColumnIndex(Words_PbRoleSets.ROLESETNAME)
            val idRoleSetDesc = cursor.getColumnIndex(Words_PbRoleSets.ROLESETDESC)
            val idRoleSetHead = cursor.getColumnIndex(Words_PbRoleSets.ROLESETHEAD)

            // read cursor
            do {
                val sb = SpannableStringBuilder()

                // data
                val roleSetId = cursor.getInt(idRoleSetId)

                // roleSet
                appendImage(sb, rolesDrawable)
                sb.append(' ')
                append(sb, cursor.getString(idRoleSetName), 0, PropBankFactories.roleSetFactory)
                sb.append(' ')
                sb.append("head=")
                sb.append(cursor.getString(idRoleSetHead))
                sb.append('\n')

                // description
                appendImage(sb, definitionDrawable)
                append(sb, cursor.getString(idRoleSetDesc), 0, PropBankFactories.definitionFactory)

                // attach result
                val node = makeTextNode(sb, false).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, node)

                // sub nodes
                val rolesNode = makeHotQueryTreeNode(rolesLabel, R.drawable.roles, false, RolesQuery(roleSetId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, rolesNode)
                val examplesNode = makeQueryTreeNode(examplesLabel, R.drawable.sample, false, ExamplesQuery(roleSetId.toLong())).addTo(parent)
                changedList.add(TreeOpCode.NEWCHILD, examplesNode)
            } while (cursor.moveToNext())
            changed = changedList.toArray()
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // R O L E S

    /**
     * Roles in role set
     *
     * @param roleSetId role set id
     * @param parent    parent node
     */
    private fun roles(roleSetId: Int, parent: TreeNode) {
        val sql = prepareRoles(roleSetId)
        val uri = Uri.parse(PropBankProvider.makeUri(sql.providerUri))
        rolesFromRoleSetIdModel.loadData(uri, sql) { cursor: Cursor -> rolesCursorToTreeModel(cursor, parent) }
    }

    private fun rolesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        val sb = SpannableStringBuilder()
        if (cursor.moveToFirst()) {
            // column indices
            // var idRoleId = cursor.getColumnIndex(PbRoleSets_PbRoles.ROLEID)
            val idRoleDescr = cursor.getColumnIndex(PbRoleSets_PbRoles.ROLEDESCR)
            val idArgType = cursor.getColumnIndex(PbRoleSets_PbRoles.ARGTYPE)
            val idFunc = cursor.getColumnIndex(PbRoleSets_PbRoles.FUNC)
            val idFuncDescr = cursor.getColumnIndex(PbRoleSets_PbRoles.FUNCDESCR)
            val idVnRole = cursor.getColumnIndex(PbRoleSets_PbRoles.VNROLE)
            val idFnFe = cursor.getColumnIndex(PbRoleSets_PbRoles.FNFE)

            // read cursor
            while (true) {
                // data

                // n
                sb.append(cursor.getString(idArgType) ?: "-")
                sb.append(' ')

                // role
                appendImage(sb, roleDrawable)
                sb.append(' ')
                append(sb, capitalize1(cursor.getString(idRoleDescr)), 0, PropBankFactories.roleFactory)

                // func
                if (!cursor.isNull(idFunc)) {
                    val func = cursor.getString(idFunc)
                    sb.append(' ')
                    append(sb, func, 0, PropBankFactories.funcFactory)
                    sb.append(' ')
                    sb.append(cursor.getString(idFuncDescr))
                }

                // vnrole
                val vnRole = cursor.getString(idVnRole)
                if (vnRole != null && vnRole.isNotEmpty()) {
                    sb.append("\n\t\t ")
                    sb.append(' ')
                    appendImage(sb, vnroleDrawable)
                    sb.append(' ')
                    append(sb, vnRole, 0, PropBankFactories.vnroleFactory)
                }

                // fnfe
                val fnFe = cursor.getString(idFnFe)
                if (fnFe != null && fnFe.isNotEmpty()) {
                    sb.append('\n')
                    sb.append("\t\t ")
                    sb.append(' ')
                    appendImage(sb, fnfeDrawable)
                    sb.append(' ')
                    append(sb, fnFe, 0, PropBankFactories.fnfeFactory)
                }

                // var roleId = cursor.getInt(idRoleId)
                // sb.append(" role id=")
                // sb.append(Integer.toString(roleId))
                // sb.append(' ')

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

    // E X A M P L E S

    /**
     * Examples in role set
     *
     * @param roleSetId role set id
     * @param parent    parent node
     */
    private fun examples(roleSetId: Int, parent: TreeNode) {
        val sql = prepareExamples(roleSetId)
        val uri = Uri.parse(PropBankProvider.makeUri(sql.providerUri))
        examplesFromRoleSetIdModel.loadData(uri, sql) { cursor: Cursor -> examplesCursorToTreeModel(cursor, parent) }
    }

    private fun examplesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        val sb = SpannableStringBuilder()
        if (cursor.moveToFirst()) {
            // column indices
            val idText = cursor.getColumnIndex(PbRoleSets_PbExamples.TEXT)
            val idRel = cursor.getColumnIndex(PbRoleSets_PbExamples.REL)
            val idArgs = cursor.getColumnIndex(PbRoleSets_PbExamples.ARGS)

            // read cursor
            while (true) {
                // text
                val text = cursor.getString(idText)
                appendImage(sb, sampleDrawable)
                append(sb, text, 0, PropBankFactories.exampleFactory)
                sb.append('\n')

                // relation
                sb.append('\t')
                appendImage(sb, relationDrawable)
                sb.append(' ')
                append(sb, cursor.getString(idRel), 0, PropBankFactories.relationFactory)

                // args
                val argsPack = cursor.getString(idArgs)
                if (argsPack != null) {
                    val args = argsPack.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    Arrays.sort(args)
                    for (arg in args) {
                        val fields = arg.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (fields.size < 6) {
                            sb.append(arg)
                            continue
                        }
                        sb.append("\n\t")

                        // n
                        val n = fields[0]
                        sb.append('[')
                        sb.append(n)
                        sb.append(']')
                        sb.append(' ')

                        // role
                        val roleDescr = fields[2]
                        appendImage(sb, roleDrawable)
                        if (roleDescr.isNotEmpty()) {
                            sb.append(' ')
                            if (roleDescr != "*") {
                                append(sb, capitalize1(roleDescr), 0, PropBankFactories.roleFactory)
                            } else if (n == "M") {
                                sb.append("modifier")
                            }
                            sb.append(' ')
                        }

                        // func
                        val func = fields[1]
                        if (func.isNotEmpty() && func != "*") {
                            // sb.append(" func=")
                            sb.append(' ')
                            append(sb, func, 0, PropBankFactories.funcFactory)
                        }

                        // vnrole
                        val vnRole = fields[3]
                        if (!vnRole.isEmpty() && vnRole != "*") {
                            sb.append("\n\t\t\t ")
                            appendImage(sb, vnroleDrawable)
                            sb.append(' ')
                            append(sb, vnRole, 0, PropBankFactories.vnroleFactory)
                        }

                        // fnfe
                        val fnFe = fields[4]
                        if (!fnFe.isEmpty() && fnFe != "*") {
                            sb.append("\n\t\t\t ")
                            appendImage(sb, fnfeDrawable)
                            sb.append(' ')
                            append(sb, fnFe, 0, PropBankFactories.fnfeFactory)
                        }

                        // subtext
                        sb.append("\n\t\t\t ")
                        sb.append(fields[5])
                        //append(sb, fields[5], 0, PropBankFactories.textFactory)
                    }
                }
                if (!cursor.moveToNext()) {
                    break
                }
                sb.append('\n')
            }

            // extra format
            spanner.setSpan(sb, 0, 0)

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

    // Q U E R I E S

    /**
     * Role query
     */
    private inner class RolesQuery(roleSetId: Long) : Query(roleSetId) {

        override fun process(node: TreeNode) {
            roles(id.toInt(), node)
        }

        override fun toString(): String {
            return "roles for roleset $id"
        }
    }

    /**
     * Examples query
     */
    private inner class ExamplesQuery(roleSetId: Long) : Query(roleSetId) {

        override fun process(node: TreeNode) {
            examples(id.toInt(), node)
        }

        override fun toString(): String {
            return "examples for roleset $id"
        }
    }

    // H E L P E R S

    /**
     * Utility to capitalize first character
     *
     * @param s string
     * @return string with capitalized first character
     */
    private fun capitalize1(s: String): CharSequence {
        return s.substring(0, 1).uppercase() + s.substring(1)
    }
}
