/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import android.os.Parcelable
import org.sqlunet.HasPos
import org.sqlunet.HasSynsetId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeIconTextNode
import org.sqlunet.model.TreeFactory.makeLinkHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeQueryTreeNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.wordnet.R

/**
 * Module for WordNet synset
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class SynsetModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Synset id
     */
    var synsetId: Long? = null

    /**
     * Pos
     */
    var pos: Char? = null

    /**
     * Expand flag
     */
    var expand = true

    override fun unmarshal(pointer: Parcelable) {
        synsetId = null
        pos = null
        if (pointer is HasSynsetId) {
            val synsetPointer = pointer as HasSynsetId
            val value = synsetPointer.getSynsetId()
            if (value != 0L) {
                synsetId = value
            }
        }
        if (pointer is HasPos) {
            val posPointer = pointer as HasPos
            pos = posPointer.getPos()
        }
    }

    override fun process(node: TreeNode) {
        if (synsetId != null && synsetId != 0L) {
            // anchor nodes
            val synsetNode = makeTextNode(synsetLabel, false).addTo(node)
            val membersNode = makeIconTextNode(membersLabel, R.drawable.members, false).addTo(node)

            // synset
            synset(synsetId!!, synsetNode, false)

            // member set
            members(synsetId!!, membersNode)

            // special
            if (pos != null) {
                when (pos!!) {

                    'v' -> {
                        this.vFrames(synsetId!!, node)
                        this.vTemplates(synsetId!!, node)
                    }

                    'a' -> this.adjPosition(synsetId!!, node)
                }
            }

            // relations and samples
            if (expand) {
                val link: Link = RelationLink(synsetId!!, maxRecursion, fragment)
                makeLinkHotQueryTreeNode(relationsLabel, R.drawable.ic_relations, false, RelationsQuery(synsetId!!, 0), link, R.drawable.ic_link_relation).addTo(node)
            } else {
                makeQueryTreeNode(relationsLabel, R.drawable.ic_relations, false, RelationsQuery(synsetId!!, 0)).addTo(node)
            }
            if (expand) {
                makeHotQueryTreeNode(samplesLabel, R.drawable.sample, false, SamplesQuery(synsetId!!)).addTo(node)
            } else {
                makeQueryTreeNode(samplesLabel, R.drawable.sample, false, SamplesQuery(synsetId!!)).addTo(node)
            }
        } else {
            setNoResult(node)
        }
    }
}
