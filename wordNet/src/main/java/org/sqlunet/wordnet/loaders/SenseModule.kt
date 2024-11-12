/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import android.os.Parcelable
import org.sqlunet.HasWordId
import org.sqlunet.browser.TreeFragment
import org.sqlunet.model.TreeFactory.makeHotQueryLinkNode
import org.sqlunet.model.TreeFactory.makeHotQueryNode
import org.sqlunet.model.TreeFactory.makeHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeIconTextNode
import org.sqlunet.model.TreeFactory.makeLinkHotQueryTreeNode
import org.sqlunet.model.TreeFactory.makeQueryTreeNode
import org.sqlunet.model.TreeFactory.makeTextNode
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.model.TreeFactory.setNoResult
import org.sqlunet.treeview.control.Link
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.wordnet.R

/**
 * Module for WordNet sense
 *
 * @param fragment fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SenseModule(fragment: TreeFragment) : SynsetModule(fragment) {

    /**
     * Word id
     */
    private var wordId: Long? = null

    override fun unmarshal(pointer: Parcelable) {
        super.unmarshal(pointer)
        wordId = null
        if (pointer is HasWordId) {
            val wordPointer = pointer as HasWordId
            wordId = wordPointer.getWordId()
        }
    }

    override fun process(node: TreeNode) {
        if (wordId != null && wordId != 0L && synsetId != null && synsetId != 0L) {
            // anchor nodes
            val synsetNode = makeTextNode(senseLabel, false).addTo(node)
            val membersNode = makeIconTextNode(membersLabel, R.drawable.members, false).addTo(node)

            // synset
            synset(synsetId!!, synsetNode, false)

            // member set
            members(synsetId!!, membersNode)

            // morph
            val morphsNode = makeTreeNode(morphsLabel, R.drawable.morph, false).addTo(node)
            morphs(wordId!!, morphsNode)

            // relations
            if (expand) {
                val link: Link = RelationLink(synsetId!!, maxRecursion, fragment)
                makeLinkHotQueryTreeNode(relationsLabel, R.drawable.ic_relations, false, RelationsQuery(synsetId!!, wordId!!), link, R.drawable.ic_link_relation).addTo(node)
            } else {
                makeQueryTreeNode(relationsLabel, R.drawable.ic_relations, false, RelationsQuery(synsetId!!, wordId!!)).addTo(node)
            }

            // samples
            if (expand) {
                makeHotQueryTreeNode(samplesLabel, R.drawable.sample, false, SamplesQuery(synsetId!!)).addTo(node)
            } else {
                makeQueryTreeNode(samplesLabel, R.drawable.sample, false, SamplesQuery(synsetId!!)).addTo(node)
            }

            // usages
            makeHotQueryNode(usagesLabel, R.drawable.usage, false, UsagesQuery(synsetId!!)).addTo(node)

            // externals
            makeHotQueryLinkNode(iliLabel, R.drawable.ili, false, IliQuery(synsetId!!)).addTo(node)
            makeHotQueryLinkNode(wikidataLabel, R.drawable.wikidata, false, WikidataQuery(synsetId!!)).addTo(node)

            // special
            if (pos != null) {
                when (pos!!) {
                    'v' -> {
                        val vframesNode = makeTreeNode(verbFramesLabel, R.drawable.verbframe, false).addTo(node)
                        val vtemplatesNode = makeTreeNode(verbTemplatesLabel, R.drawable.verbtemplate, false).addTo(node)
                        vFrames(synsetId!!, wordId!!, vframesNode)
                        vTemplates(synsetId!!, wordId!!, vtemplatesNode)
                    }

                    'a' -> {
                        val adjpositionsNode = makeTreeNode(adjPositionsLabel, R.drawable.adjposition, false).addTo(node)
                        adjPosition(synsetId!!, wordId!!, adjpositionsNode)
                    }
                }
            } else {
                val vframesNode = makeTreeNode(verbFramesLabel, R.drawable.verbframe, false).addTo(node)
                val vtemplatesNode = makeTreeNode(verbTemplatesLabel, R.drawable.verbtemplate, false).addTo(node)
                val adjpositionsNode = makeTreeNode(adjPositionsLabel, R.drawable.adjposition, false).addTo(node)
                vFrames(synsetId!!, wordId!!, vframesNode)
                vTemplates(synsetId!!, wordId!!, vtemplatesNode)
                adjPosition(synsetId!!, wordId!!, adjpositionsNode)
            }
        } else {
            setNoResult(node)
        }
    }
}
