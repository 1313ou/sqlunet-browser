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
import org.sqlunet.model.TreeFactory.makeQueryTreeNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.wordnet.R

/**
 * Module for WordNet relation
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class RelationModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Synset id
     */
    var synsetId: Long? = null

    /**
     * Pos
     */
    var pos: Char? = null

    /**
     * Expand container flag
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
            val synsetNode = makeTextNode(senseLabel, false).addTo(node)
            val membersNode = makeIconTextNode(membersLabel, R.drawable.members, false).addTo(node)

            // synset
            synset(synsetId!!, synsetNode, false)

            // members
            // members(this.synsetId, membersNode)
            memberSet(synsetId!!, membersNode, concatQuery = true, addNewNode = false)

            // up relations
            if (expand) {
                makeHotQueryTreeNode(upLabel, R.drawable.up, false, SubRelationsQuery(synsetId!!, HYPERNYM, maxRecursion, true)).addTo(node)
            } else {
                makeQueryTreeNode(upLabel, R.drawable.up, false, SubRelationsQuery(synsetId!!, HYPERNYM, maxRecursion, false)).addTo(node)
            }
            // down relations
            if (expand) {
                makeHotQueryTreeNode(downLabel, R.drawable.down, false, SubRelationsQuery(synsetId!!, HYPONYM, maxRecursion, false)).addTo(node)
            } else {
                makeQueryTreeNode(downLabel, R.drawable.down, false, SubRelationsQuery(synsetId!!, HYPONYM, maxRecursion, false)).addTo(node)
            }
        } else {
            setNoResult(node)
        }
    }

    companion object {

        private const val HYPERNYM = 1
        private const val HYPONYM = 2
    }
}
