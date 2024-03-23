/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.loaders

import android.os.Parcelable
import org.sqlunet.Has2SynsetId
import org.sqlunet.Has2WordId
import org.sqlunet.HasSynsetId
import org.sqlunet.HasTarget
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.model.TreeNode

/**
 * Module for SyntagNet collocation from id
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class CollocationsModule(fragment: TreeFragment) : BaseModule(fragment) {

    /**
     * Collocation id
     */
    private var synset1Id: Long? = null

    /**
     * Collocation id
     */
    private var synset2Id: Long? = null

    /**
     * Collocation id
     */
    private var word1Id: Long? = null

    /**
     * Collocation id
     */
    private var word2Id: Long? = null

    /**
     * Target
     */
    private var target = 0

    override fun isTargetSecond(word1Id: Long, word2Id: Long): Boolean {
        return when (target) {
            1 -> false
            2 -> true
            else -> this.word2Id != null && this.word2Id == this.word1Id && this.word2Id == word2Id
        }
    }

    override fun unmarshal(pointer: Parcelable) {
        if (pointer is HasWordId) {
            val pointer1 = pointer as HasWordId
            val id = pointer1.getWordId()
            word1Id = if (id == -1L) null else pointer1.getWordId()
        }

        word2Id = if (pointer is Has2WordId) {
            val pointer2 = pointer as Has2WordId
            val id = pointer2.getWord2Id()
            if (id == -1L) null else pointer2.getWord2Id()
        } else word1Id

        if (pointer is HasSynsetId) {
            val pointer1 = pointer as HasSynsetId
            val id = pointer1.getSynsetId()
            synset1Id = if (id == -1L) null else pointer1.getSynsetId()
        }

        synset2Id = if (pointer is Has2SynsetId) {
            val pointer2 = pointer as Has2SynsetId
            val id = pointer2.getSynset2Id()
            if (id == -1L) null else pointer2.getSynset2Id()
        } else synset1Id

        target = if (pointer is HasTarget) {
            val pointer2 = pointer as HasTarget
            pointer2.getTarget()
        } else 0
    }

    override fun process(node: TreeNode) {
        if (word1Id != null || word2Id != null || synset1Id != null || synset2Id != null) {
            collocations(word1Id, word2Id, synset1Id, synset2Id, node)
        } else {
            setNoResult(node)
        }
    }
}
