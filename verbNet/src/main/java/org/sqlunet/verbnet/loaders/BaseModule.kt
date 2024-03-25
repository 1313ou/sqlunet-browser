/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.loaders

import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.Module
import org.sqlunet.browser.SqlunetViewTreeModel
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryNode
import org.sqlunet.model.TreeFactory.makeIconTextNode
import org.sqlunet.model.TreeFactory.makeLeafNode
import org.sqlunet.model.TreeFactory.makeQueryNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.style.Spanner.Companion.append
import org.sqlunet.style.Spanner.Companion.appendImage
import org.sqlunet.style.Spanner.Companion.getDrawable
import org.sqlunet.treeview.control.Query
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.verbnet.R
import org.sqlunet.verbnet.loaders.Queries.prepareVnClass
import org.sqlunet.verbnet.loaders.Queries.prepareVnFrames
import org.sqlunet.verbnet.loaders.Queries.prepareVnMembers
import org.sqlunet.verbnet.loaders.Queries.prepareVnRoles
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnFrames_X
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnMembers_X
import org.sqlunet.verbnet.provider.VerbNetContract.VnClasses_VnRoles_X
import org.sqlunet.verbnet.provider.VerbNetProvider
import org.sqlunet.verbnet.style.VerbNetFactories
import org.sqlunet.verbnet.style.VerbNetSemanticsProcessor
import org.sqlunet.verbnet.style.VerbNetSemanticsSpanner
import org.sqlunet.verbnet.style.VerbNetSyntaxSpanner
import org.sqlunet.view.TreeOp
import org.sqlunet.view.TreeOp.Companion.seq
import org.sqlunet.view.TreeOp.TreeOpCode
import org.sqlunet.view.TreeOp.TreeOps
import org.sqlunet.view.TreeOpExecute

/**
 * VerbNet base module
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseModule(fragment: TreeFragment) : Module(fragment) {

    // agents

    /**
     * Processor
     */
    private val semanticsProcessor: VerbNetSemanticsProcessor

    /**
     * Syntax spanner
     */
    private val syntaxSpanner: VerbNetSyntaxSpanner

    /**
     * Semantics
     */
    private val semanticsSpanner: VerbNetSemanticsSpanner

    // resources

    @JvmField
    protected val membersLabel: String
    private val groupLabel: String

    @JvmField
    protected val rolesLabel: String

    @JvmField
    protected val framesLabel: String
    private val examplesLabel: String

    /**
     * Drawable for class
     */
    private val drawableClass: Drawable

    /**
     * Drawable for member
     */
    private val drawableMember: Drawable

    /**
     * Drawable for role sets
     */
    @JvmField
    val drawableRoles: Drawable

    /**
     * Drawable for role
     */
    private val drawableRole: Drawable

    /**
     * Drawable for frame
     */
    private val drawableFrame: Drawable

    /**
     * Drawable for syntax
     */
    private val drawableSyntax: Drawable

    /**
     * Drawable for semantics
     */
    private val drawableSemantics: Drawable

    /**
     * Drawable for example
     */
    private val drawableExample: Drawable

    /**
     * Drawable for definition
     */
    private val drawableDefinition: Drawable

    /**
     * Drawable for grouping
     */
    private val drawableGrouping: Drawable

    // view models

    private lateinit var vnClassFromClassIdModel: SqlunetViewTreeModel
    private lateinit var vnMembersFromClassIdModel: SqlunetViewTreeModel
    private lateinit var vnRolesFromClassIdModel: SqlunetViewTreeModel
    private lateinit var vnFramesFromClassIdModel: SqlunetViewTreeModel

    init {

        // models
        makeModels()

        // drawables
        val context = this@BaseModule.fragment.requireContext()
        membersLabel = context.getString(R.string.verbnet_members_)
        groupLabel = context.getString(R.string.verbnet_group_)
        rolesLabel = context.getString(R.string.verbnet_roles_)
        framesLabel = context.getString(R.string.verbnet_frames_)
        examplesLabel = context.getString(R.string.verbnet_examples_)
        drawableClass = getDrawable(context, R.drawable.roleclass)
        drawableMember = getDrawable(context, R.drawable.member)
        drawableRoles = getDrawable(context, R.drawable.roles)
        drawableRole = getDrawable(context, R.drawable.role)
        drawableFrame = getDrawable(context, R.drawable.vnframe)
        drawableSyntax = getDrawable(context, R.drawable.syntax)
        drawableSemantics = getDrawable(context, R.drawable.semantics)
        drawableExample = getDrawable(context, R.drawable.sample)
        drawableDefinition = getDrawable(context, R.drawable.definition)
        drawableGrouping = getDrawable(context, R.drawable.grouping)

        // create processors and spanners
        semanticsProcessor = VerbNetSemanticsProcessor
        syntaxSpanner = VerbNetSyntaxSpanner
        semanticsSpanner = VerbNetSemanticsSpanner
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        vnClassFromClassIdModel = ViewModelProvider(fragment)["vn.class(classid)", SqlunetViewTreeModel::class.java]
        vnClassFromClassIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vnMembersFromClassIdModel = ViewModelProvider(fragment)["vn.members(classid)", SqlunetViewTreeModel::class.java]
        vnMembersFromClassIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vnRolesFromClassIdModel = ViewModelProvider(fragment)["vn.roles(classid)", SqlunetViewTreeModel::class.java]
        vnRolesFromClassIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
        vnFramesFromClassIdModel = ViewModelProvider(fragment)["vn.frames(classid)", SqlunetViewTreeModel::class.java]
        vnFramesFromClassIdModel.data.observe(fragment) { data: Array<TreeOp>? -> TreeOpExecute(fragment).exec(data) }
    }

    // L O A D E R S

    // vnClass

    /**
     * VerbNet class
     *
     * @param classId class id
     * @param parent  parent node
     */
    fun vnClass(classId: Long, parent: TreeNode) {
        val sql = prepareVnClass(classId)
        val uri = Uri.parse(VerbNetProvider.makeUri(sql.providerUri))
        vnClassFromClassIdModel.loadData(uri, sql) { cursor: Cursor -> vnClassCursorToTreeModel(cursor, classId, parent) }
    }

    private fun vnClassCursorToTreeModel(cursor: Cursor, classId: Long, parent: TreeNode): Array<TreeOp> {
        if (cursor.count > 1) {
            throw RuntimeException("Unexpected number of rows")
        }
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()

            // column indices
            // final int idClassId = cursor.getColumnIndex(VnClasses_X.CLASSID);
            val idClass = cursor.getColumnIndex(VnClasses.CLASS)
            // final int idClassTag = cursor.getColumnIndex(VnClasses.CLASSTAG);

            // data
            // final int classId = cursor.getInt(idClassId);
            val vnClass = cursor.getString(idClass)

            // sb.append("[class]");
            appendImage(sb, drawableClass)
            sb.append(' ')
            append(sb, vnClass, 0, VerbNetFactories.classFactory)
            // sb.append(" tag=");
            // sb.append(cursor.getString(idClassTag));
            sb.append(" id=")
            sb.append(classId.toString())

            // attach result
            val node = makeTextNode(sb, false).addTo(parent)

            // sub nodes
            val membersNode = makeHotQueryNode(membersLabel, R.drawable.members, false, MembersQuery(classId)).addTo(parent)
            val rolesNode = makeHotQueryNode(rolesLabel, R.drawable.roles, false, RolesQuery(classId)).addTo(parent)
            val framesNode = makeQueryNode(framesLabel, R.drawable.vnframe, false, FramesQuery(classId)).addTo(parent)

            // changed
            changed = seq(TreeOpCode.NEWMAIN, node, TreeOpCode.NEWEXTRA, membersNode, TreeOpCode.NEWEXTRA, rolesNode, TreeOpCode.NEWEXTRA, framesNode, TreeOpCode.NEWTREE, parent)
        } else {
            setNoResult(parent)
            changed = seq(TreeOpCode.REMOVE, parent)
        }
        cursor.close()
        return changed
    }

    // vnMembers

    /**
     * VerbNet members
     *
     * @param classId class id
     * @param parent  parent node
     */
    private fun vnMembers(classId: Int, parent: TreeNode) {
        val sql = prepareVnMembers(classId)
        val uri = Uri.parse(VerbNetProvider.makeUri(sql.providerUri))
        vnMembersFromClassIdModel.loadData(uri, sql) { cursor: Cursor -> vnMembersCursorToTreeModel(cursor, parent) }
    }

    private fun vnMembersCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val changedList = TreeOps(TreeOpCode.NEWTREE, parent)

            // column indices
            // val idWordId = cursor.getColumnIndex(VnClasses_VnMembers_X.WORDID)
            // val idVnWordId = cursor.getColumnIndex(VnClasses_VnMembers_X.VNWORDID)
            val idWord = cursor.getColumnIndex(VnClasses_VnMembers_X.WORD)
            val idGroupings = cursor.getColumnIndex(VnClasses_VnMembers_X.GROUPINGS)
            val idDefinitions = cursor.getColumnIndex(VnClasses_VnMembers_X.DEFINITIONS)
            do {
                val sb = SpannableStringBuilder()

                // member
                // Spanner.appendImage(sb, BaseModule.this.drawableMember);
                // sb.append(' ')
                append(sb, cursor.getString(idWord), 0, VerbNetFactories.memberFactory)
                val definitions = cursor.getString(idDefinitions)
                val groupings = cursor.getString(idGroupings)
                if (definitions != null || groupings != null) {
                    val memberNode = makeTreeNode(sb, R.drawable.member, false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, memberNode)
                    val sb2 = SpannableStringBuilder()

                    // definitions
                    var first = true
                    if (definitions != null) {
                        for (definition in definitions.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                            if (first) {
                                first = false
                            } else {
                                sb2.append('\n')
                            }
                            appendImage(sb2, drawableDefinition)
                            sb2.append(' ')
                            append(sb2, definition.trim { it <= ' ' }, 0, VerbNetFactories.definitionFactory)
                        }
                    }

                    // groupings
                    first = true
                    if (groupings != null) {
                        for (grouping in groupings.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                            if (first) {
                                if (sb2.isNotEmpty()) {
                                    sb2.append('\n')
                                }
                                first = false
                            } else {
                                sb2.append('\n')
                            }
                            appendImage(sb2, drawableGrouping)
                            sb2.append(' ')
                            append(sb2, grouping.trim { it <= ' ' }, 0, VerbNetFactories.groupingFactory)
                        }
                    }

                    // attach definition and groupings result
                    val node = makeTextNode(sb2, false).addTo(memberNode)
                    changedList.add(TreeOpCode.NEWCHILD, node)
                } else {
                    val node = makeLeafNode(sb, R.drawable.member, false).addTo(parent)
                    changedList.add(TreeOpCode.NEWCHILD, node)
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

    // vnRoles

    /**
     * VerbNet roles
     *
     * @param classId class id
     * @param parent  parent node
     */
    private fun vnRoles(classId: Int, parent: TreeNode) {
        val sql = prepareVnRoles(classId)
        val uri = Uri.parse(VerbNetProvider.makeUri(sql.providerUri))
        vnRolesFromClassIdModel.loadData(uri, sql) { cursor: Cursor -> vnRolesCursorToTreeModel(cursor, parent) }
    }

    private fun vnRolesCursorToTreeModel(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()

            // column indices
            // final int idRoleId = cursor.getColumnIndex(VnClasses_VnRoles.ROLEID);
            val idRoleType = cursor.getColumnIndex(VnClasses_VnRoles_X.ROLETYPE)
            val idRestrs = cursor.getColumnIndex(VnClasses_VnRoles_X.RESTRS)

            // read cursor
            while (true) {
                // role
                appendImage(sb, drawableRole)
                sb.append(' ')
                append(sb, cursor.getString(idRoleType), 0, VerbNetFactories.roleFactory)

                // restr
                val restrs: CharSequence? = cursor.getString(idRestrs)
                if (restrs != null) {
                    sb.append(' ')
                    append(sb, restrs, 0, VerbNetFactories.restrsFactory)
                }

                // role id
                // final int roleId = cursor.getInt(idRoleId);
                // sb.append(" role id=");
                // sb.append(Integer.toString(roleId));
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

    // vnFrames

    private fun vnFrames(classId: Int, parent: TreeNode) {
        val sql = prepareVnFrames(classId)
        val uri = Uri.parse(VerbNetProvider.makeUri(sql.providerUri))
        vnFramesFromClassIdModel.loadData(uri, sql) { cursor: Cursor -> vnFramesToView(cursor, parent) }
    }

    private fun vnFramesToView(cursor: Cursor, parent: TreeNode): Array<TreeOp> {
        val changed: Array<TreeOp>
        if (cursor.moveToFirst()) {
            val sb = SpannableStringBuilder()

            // column indices
            // val idFrameId = cursor.getColumnIndex(VnClasses_VnFrames.FRAMEID)
            val idFrameName = cursor.getColumnIndex(VnClasses_VnFrames_X.FRAMENAME)
            val idFrameSubName = cursor.getColumnIndex(VnClasses_VnFrames_X.FRAMESUBNAME)
            val idSyntax = cursor.getColumnIndex(VnClasses_VnFrames_X.SYNTAX)
            val idSemantics = cursor.getColumnIndex(VnClasses_VnFrames_X.SEMANTICS)
            val idExamples = cursor.getColumnIndex(VnClasses_VnFrames_X.EXAMPLES)

            // read cursor
            while (true) {
                // frame
                appendImage(sb, drawableFrame)
                sb.append(' ')
                append(sb, cursor.getString(idFrameName), 0, VerbNetFactories.frameFactory)
                sb.append(' ')
                append(sb, cursor.getString(idFrameSubName), 0, VerbNetFactories.framesubnameFactory)

                // frame id
                // sb.append(Integer.toString(cursor.getInt(idFrameId)))
                // sb.append('\n')

                // syntax
                val syntax = cursor.getString(idSyntax)
                for (line in syntax.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())  //
                {
                    sb.append('\n')
                    sb.append('\t')
                    appendImage(sb, drawableSyntax)
                    syntaxSpanner.append(line, sb, 0)
                }

                // semantics
                val semantics = cursor.getString(idSemantics)
                for (line in semantics.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())  //
                {
                    sb.append('\n')
                    sb.append('\t')
                    appendImage(sb, drawableSemantics)
                    val statement = semanticsProcessor.process(line)
                    semanticsSpanner.append(statement, sb, 0)
                }

                // examples
                val examplesConcat = cursor.getString(idExamples)
                val examples = examplesConcat.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (example in examples) {
                    sb.append('\n')
                    sb.append('\t')
                    appendImage(sb, drawableExample)
                    append(sb, example, 0, VerbNetFactories.exampleFactory)
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
     * Groups
     *
     * @param group items concat with '|'
     * @return node
     */
    @Suppress("unused")
    protected fun items(parent: TreeNode, group: String?): TreeNode? {
        if (group != null) {
            val sb = SpannableStringBuilder()
            val items = group.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (items.size == 1) {
                appendImage(sb, drawableMember)
                append(sb, items[0], 0, VerbNetFactories.memberFactory)
                return makeIconTextNode(sb, R.drawable.member, false).addTo(parent)
            } else if (items.size > 1) {
                val groupingsNode = makeIconTextNode(groupLabel, R.drawable.member, false).addTo(parent)
                var first = true
                for (item in items) {
                    if (first) {
                        first = false
                    } else {
                        sb.append('\n')
                    }
                    appendImage(sb, drawableMember)
                    append(sb, item, 0, VerbNetFactories.memberFactory)
                }
                makeTextNode(sb, false).addTo(groupingsNode)
                return groupingsNode
            }
        }
        return null
    }

    /**
     * Members query
     */
    internal inner class MembersQuery(classId: Long) : Query(classId) {
        override fun process(node: TreeNode) {
            vnMembers(id.toInt(), node)
        }

        override fun toString(): String {
            return "members for $id"
        }
    }

    /**
     * Roles query
     */
    internal inner class RolesQuery(classId: Long) : Query(classId) {
        override fun process(node: TreeNode) {
            vnRoles(id.toInt(), node)
        }

        override fun toString(): String {
            return "roles for $id"
        }
    }

    /**
     * Frames query
     */
    internal inner class FramesQuery(classId: Long) : Query(classId) {
        override fun process(node: TreeNode) {
            vnFrames(id.toInt(), node)
        }

        override fun toString(): String {
            return "vnframes for $id"
        }
    }
}
