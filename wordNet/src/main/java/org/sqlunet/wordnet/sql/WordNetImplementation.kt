/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToString
import org.sqlunet.sql.NodeFactory.addAttributes
import org.sqlunet.sql.NodeFactory.makeNode
import org.sqlunet.wordnet.sql.Mapping.getDomainId
import org.sqlunet.wordnet.sql.Mapping.getPosId
import org.sqlunet.wordnet.sql.Mapping.getRelationId
import org.sqlunet.wordnet.sql.Mapping.initDomains
import org.sqlunet.wordnet.sql.Mapping.initRelations
import org.sqlunet.wordnet.sql.WnNodeFactory.makeDomainNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeMoreRelationNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makePosNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeRelationNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeSenseNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeSynsetNode
import org.sqlunet.wordnet.sql.WnNodeFactory.makeWordNode
import org.sqlunet.wordnet.sql.Word.Companion.make
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Encapsulates WordNet query implementation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WordNetImplementation : WordNetInterface {

    // S E L E C T O R

    /**
     * Business method that returns WordNet selector data as DOM document
     *
     * @param connection connection
     * @param word       target word
     * @return WordNet selector data as DOM document
     */
    override fun querySelectorDoc(connection: SQLiteDatabase, word: String): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(rootNode, "word", word)
        walkSelector(connection, doc, rootNode, word)
        return doc
    }

    /**
     * Business method that returns WordNet selector data as XML
     *
     * @param connection connection
     * @param word       target word
     * @return WordNet selector data as XML
     */
    override fun querySelectorXML(connection: SQLiteDatabase, word: String): String {
        val doc = querySelectorDoc(connection, word)
        return docToString(doc)
    }

    // D E T A I L

    /**
     * Business method that returns WordNet data as a Document
     *
     * @param connection    connection
     * @param word          target word
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as a DOM Document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String, withRelations: Boolean, recurse: Boolean): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(
            rootNode,
            "word", word,
            "withRelations", withRelations.toString(),
            "recurse", recurse.toString()
        )
        walk(connection, word, doc, rootNode, withRelations, recurse, Mapping.ANY_TYPE, Mapping.ANY_TYPE, Mapping.ANY_TYPE)
        return doc
    }

    /**
     * Business method that returns WordNet data as a Document
     *
     * @param connection    connection
     * @param wordId        target word id
     * @param synsetId      target synset id
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as a DOM Document
     */
    override fun queryDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long, withRelations: Boolean, recurse: Boolean): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(
            rootNode,
            "wordid", wordId.toString(),
            "synsetid", synsetId.toString(),
            "withRelations", withRelations.toString(),
            "recurse", recurse.toString()
        )
        walkSense(connection, wordId, synsetId, doc, rootNode, withRelations, recurse, Mapping.ANY_TYPE)
        return doc
    }

    /**
     * Business method that returns complete data as XML
     *
     * @param connection    connection
     * @param word          target word
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as XML
     */
    override fun queryXML(connection: SQLiteDatabase, word: String, withRelations: Boolean, recurse: Boolean): String {
        val doc = queryDoc(connection, word, withRelations, recurse)
        return docToString(doc)
    }

    /**
     * Business method that returns WordNet data as DOM document
     *
     * @param connection    connection
     * @param word          target word
     * @param posName       target part-of-speech
     * @param domainName    target domain
     * @param relationName  target relation type name
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as DOM document
     */
    override fun queryDoc(connection: SQLiteDatabase, word: String, posName: String, domainName: String, relationName: String, withRelations: Boolean, recurse: Boolean): Document {
        val doc = makeDocument()

        // parameters
        val posId = getPosId(posName)
        val domainId = getDomainId(posName, domainName)
        val relationId = getRelationId(relationName)

        // fill document
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(
            rootNode,
            "word", word,
            "pos", posName,
            "domain", domainName,
            "relation", relationName,
            "withrelations", withRelations.toString(),
            "recurse", recurse.toString()
        )
        walk(connection, word, doc, rootNode, withRelations, recurse, posId, domainId, relationId)
        return doc
    }

    /**
     * Business method that returns WordNet data as XML
     *
     * @param connection    connection
     * @param word          target word
     * @param posName       target part-of-speech
     * @param domainName    target domain
     * @param relationName  target relation type name
     * @param withRelations determines if queries are to include relations
     * @param recurse       determines if queries are to follow relations recursively
     * @return WordNet data as XML data
     */
    override fun queryXML(connection: SQLiteDatabase, word: String, posName: String, domainName: String, relationName: String, withRelations: Boolean, recurse: Boolean): String {
        val doc = queryDoc(connection, word, posName, domainName, relationName, withRelations, recurse)
        return docToString(doc)
    }

    /**
     * Business method that returns WordNet word data as DOM document
     *
     * @param connection connection
     * @param wordId     target word id
     * @return WordNet word data as DOM document
     */
    override fun queryWordDoc(connection: SQLiteDatabase, wordId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(
            rootNode,
            "wordid", wordId.toString()
        )

        // word
        val word = make(connection, wordId)!!
        val wordNode = makeWordNode(doc, rootNode, word.word, word.id)
        val query = SynsetsQueryFromWordId(connection, wordId)
        query.execute()
        while (query.next()) {
            // synset
            val synset = Synset(query)

            // sense node
            val senseElement = makeSenseNode(doc, wordNode, wordId, synset.synsetId, 0)
            addAttributes(
                senseElement,
                "pos", synset.posName,
                "domain", synset.domainName
            )

            // synset node
            walkSynsetHeader(doc, senseElement, synset)
        }
        return doc
    }

    /**
     * Business method that returns WordNet word data as XML
     *
     * @param connection connection
     * @param wordId     target word id
     * @return WordNet word data as XML
     */
    override fun queryWordXML(connection: SQLiteDatabase, wordId: Long): String {
        val doc = queryWordDoc(connection, wordId)
        return docToString(doc)
    }

    /**
     * Business method that returns WordNet sense data as DOM document
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   target synset id
     * @return WordNet synset data as DOM document
     */
    override fun querySenseDoc(connection: SQLiteDatabase, wordId: Long, synsetId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(
            rootNode,
            "wordid", wordId.toString(),
            "synsetid", synsetId.toString()
        )
        val senseNode: Node = makeSenseNode(doc, rootNode, wordId, synsetId, 0)
        val query = SynsetQuery(connection, synsetId)
        query.execute()
        if (query.next()) {
            // synset
            val synset = Synset(query)
            val synsetNode = walkSynset(connection, doc, senseNode, synset)

            // relations
            walkSynsetRelations(connection, doc, synsetNode, synset, wordId, withRelations = true, recurse = true, Mapping.ANY_TYPE)
        }
        return doc
    }

    /**
     * Business method that returns WordNet sense data as XML
     *
     * @param connection connection
     * @param wordId     target word id
     * @param synsetId   target synset id
     * @return WordNet synset data as XML
     */
    override fun querySenseXML(connection: SQLiteDatabase, wordId: Long, synsetId: Long): String {
        val doc = querySenseDoc(connection, wordId, synsetId)
        return docToString(doc)
    }

    /**
     * Business method that returns WordNet synset data as DOM document
     *
     * @param connection connection
     * @param synsetId   target synset id
     * @return WordNet synset data as DOM document
     */
    override fun querySynsetDoc(connection: SQLiteDatabase, synsetId: Long): Document {
        val doc = makeDocument()
        val rootNode = makeNode(doc, doc, "wordnet", null, WN_NS)
        addAttributes(rootNode, "synsetid", synsetId.toString())
        val query = SynsetQuery(connection, synsetId)
        query.execute()
        if (query.next()) {
            // synset
            val synset = Synset(query)
            val synsetNode = walkSynset(connection, doc, rootNode, synset)

            // relations
            walkSynsetRelations(connection, doc, synsetNode, synset, 0, withRelations = true, recurse = true, Mapping.ANY_TYPE)
        }
        return doc
    }

    /**
     * Business method that returns WordNet synset data as XML
     *
     * @param connection connection
     * @param synsetId   target synset id
     * @return WordNet synset data as XML
     */
    override fun querySynsetXML(connection: SQLiteDatabase, synsetId: Long): String {
        val doc = querySynsetDoc(connection, synsetId)
        return docToString(doc)
    }

    // I T E M S

    /**
     * WordNet parts-of-speech as array of strings
     */
    override val posNames: Array<String>
        get() = Mapping.posNames

    /**
     *  WordNet domains as array of strings
     */
    override val domainNames: Array<String>
        get() = Mapping.domainNames

    // I N I T I A L I Z E

    override val relationNames: Array<String>
        /**
         * Business method that returns WordNet relation names as array of strings
         *
         * @return array of Strings
         */
        get() = Mapping.relationNames

    companion object {

        // S E T T I N  G S

        private const val WN_NS = "http://org.sqlunet/wn"

        /**
         * The maximum recursion level defined for down-tree queries
         */
        private const val MAX_RECURSE_LEVEL = 2

        // W A L K

        /**
         * Perform queries for WordNet selector
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node walk will attach results to
         * @param targetWord target word
         */
        private fun walkSelector(connection: SQLiteDatabase, doc: Document, parent: Node, targetWord: String) {
            // word
            val word = make(connection, targetWord) ?: return

            // iterate synsets
            val synsets = word.getSynsets(connection) ?: return
            var domain: String? = null
            var pos: String? = null
            var domainNode: Node? = null
            var posNode: Node? = null
            for (i in synsets.indices) {
                val synset = synsets[i]

                // pos node
                val posName = synset.posName
                if (posName != pos) {
                    posNode = makePosNode(doc, parent, posName)
                    pos = posName
                    domain = null
                }

                // domain node
                val domainName = synset.domainName
                if (domainName != domain) {
                    domainNode = makeDomainNode(doc, posNode, domainName)
                    domain = domainName
                }

                // sense node
                val senseNode: Node = makeSenseNode(doc, domainNode, word.id, synset.synsetId, i + 1)

                // word node
                makeWordNode(doc, senseNode, word.word, word.id)

                // synset node
                walkSynset(connection, doc, senseNode, synset)
            }
        }

        /**
         * Perform queries for WordNet data from word
         *
         * @param connection       connection
         * @param targetWord       target word
         * @param doc              org.w3c.dom.Document being built
         * @param parent           org.w3c.dom.Node walk will attach results to
         * @param withRelations    determines if queries are to include relations
         * @param recurse          determines if queries are to follow relations recursively
         * @param targetPosId      target part-of-speech id (ANYTYPE for all types)
         * @param targetDomainId   target domain id (ANYTYPE for all types)
         * @param targetRelationId target relation type id (ANYTYPE for all types)
         */
        private fun walk(connection: SQLiteDatabase, targetWord: String, doc: Document, parent: Node, withRelations: Boolean, recurse: Boolean, targetPosId: Int, targetDomainId: Int, targetRelationId: Int) {
            // word
            val word = make(connection, targetWord) ?: return
            makeWordNode(doc, parent, word.word, word.id)

            // iterate synsets
            val synsets = (if (targetPosId == Mapping.ANY_TYPE && targetDomainId == Mapping.ANY_TYPE) word.getSynsets(connection) else word.getTypedSynsets(
                connection,
                if (targetDomainId == Mapping.ANY_TYPE) targetPosId else targetDomainId,
                targetDomainId != Mapping.ANY_TYPE
            ))
                ?: return
            var domain: String? = null
            var pos: String? = null
            var domainNode: Node? = null
            var posNode: Node? = null
            for (i in synsets.indices) {
                val synset = synsets[i]

                // pos node
                val posName = synset.posName
                if (posName != pos) {
                    posNode = makePosNode(doc, parent, posName)
                    pos = posName
                    domain = null
                }

                // domain node
                val domainName = synset.domainName
                if (domainName != domain) {
                    domainNode = makeDomainNode(doc, posNode, domainName)
                    domain = domainName
                }

                // sense node
                val senseNode: Node = makeSenseNode(doc, domainNode, word.id, synset.synsetId, i + 1)

                // synset nodes
                val synsetNode = walkSynset(connection, doc, senseNode, synset)

                // relations
                walkSynsetRelations(connection, doc, synsetNode, synset, word.id, withRelations, recurse, targetRelationId)
            }
        }

        /**
         * Perform queries for WordNet data from word id and synset id
         *
         * @param connection       connection
         * @param wordId           target word id
         * @param synsetId         target synset id
         * @param doc              org.w3c.dom.Document being built
         * @param parent           org.w3c.dom.Node walk will attach results to
         * @param withRelations    determines if queries are to include relations
         * @param recurse          determines if queries are to follow relations recursively
         * @param targetRelationId target relation type id
         */
        private fun walkSense(
            connection: SQLiteDatabase,
            wordId: Long,
            synsetId: Long?,
            doc: Document,
            parent: Node,
            withRelations: Boolean,
            recurse: Boolean,
            targetRelationId: Int,
        ) {
            if (synsetId == null) {
                val query = SynsetsQueryFromWordId(connection, wordId)
                query.execute()
                var posName: String? = null
                var domainName: String? = null
                var posNode: Node? = null
                var domainNode: Node? = null
                var i = 0
                while (query.next()) {
                    val synset = Synset(query)

                    // pos node
                    val synsetPosName = synset.posName
                    if (synsetPosName != posName) {
                        posNode = makePosNode(doc, parent, synsetPosName)
                        posName = synsetPosName
                    }

                    // domain node
                    val synsetDomainName = synset.domainName
                    if (synsetDomainName != domainName) {
                        domainNode = makeDomainNode(doc, posNode, synsetDomainName)
                        domainName = synsetDomainName
                    }

                    // sense node
                    val senseNode: Node = makeSenseNode(doc, domainNode, wordId, 0, ++i)

                    // synset
                    val synsetNode = walkSynset(connection, doc, senseNode, synset)

                    // relations
                    walkSynsetRelations(connection, doc, synsetNode, synset, wordId, withRelations, recurse, targetRelationId)
                }
                return
            }
            val query = SynsetQuery(connection, synsetId)
            query.execute()
            if (query.next()) {
                val synset = Synset(query)

                // pos node
                val posName = synset.posName
                val posNode = makePosNode(doc, parent, posName)

                // domain node
                val domainName = synset.domainName
                val domainNode = makeDomainNode(doc, posNode, domainName)

                // sense node
                val senseNode: Node = makeSenseNode(doc, domainNode, wordId, synsetId, 1)

                // synset
                val synsetNode = walkSynset(connection, doc, senseNode, synset)

                // relations
                walkSynsetRelations(connection, doc, synsetNode, synset, wordId, withRelations, recurse, targetRelationId)
            }
        }

        /**
         * Process synset data (summary)
         *
         * @param doc    org.w3c.dom.Document being built
         * @param parent org.w3c.dom.Node walk will attach results to
         * @param synset synset whose data are to be processed
         */
        private fun walkSynsetHeader(doc: Document, parent: Node, synset: Synset): Node {
            // anchor node
            val synsetNode: Node = makeSynsetNode(doc, parent, synset.synsetId, 0)

            // gloss
            makeNode(doc, synsetNode, "definition", synset.definition)
            return synsetNode
        }

        /**
         * Process synset data
         *
         * @param connection connection
         * @param doc        org.w3c.dom.Document being built
         * @param parent     org.w3c.dom.Node walk will attach results to
         * @param synset     synset whose data are to be processed
         */
        private fun walkSynset(connection: SQLiteDatabase, doc: Document, parent: Node?, synset: Synset): Node {
            // synset words
            val words = synset.getSynsetWords(connection)

            // anchor node
            val synsetNode: Node = makeSynsetNode(doc, parent, synset.synsetId, words?.size ?: 0)

            // words
            if (words != null) {
                for (word in words) {
                    val word2 = word.word.replace('_', ' ')
                    makeWordNode(doc, synsetNode, word2, word.id)
                }
            }

            // gloss
            makeNode(doc, synsetNode, "definition", synset.definition)

            // sample
            makeNode(doc, synsetNode, "sample", synset.sample)

            return synsetNode
        }

        /**
         * Perform synset relation queries for WordNet data
         *
         * @param connection       connection
         * @param doc              org.w3c.dom.Document being built
         * @param parent           org.w3c.dom.Node walk will attach results to
         * @param synset           target synset
         * @param wordId           target word id
         * @param withRelations    determines if queries are to include relations
         * @param recurse          determines if queries are to follow relations recursively
         * @param targetRelationId target relation type id
         */
        private fun walkSynsetRelations(connection: SQLiteDatabase, doc: Document, parent: Node, synset: Synset, wordId: Long, withRelations: Boolean, recurse: Boolean, targetRelationId: Int) {
            if (withRelations) {
                // relations node
                val relationsNode: Node = makeNode(doc, parent, "relations", null)

                // get related
                val relateds = (if (targetRelationId == Mapping.ANY_TYPE) synset.getRelateds(connection, wordId) else synset.getTypedRelateds(connection, wordId, targetRelationId)) ?: return

                // iterate relations
                var relationNode: Node? = null
                var currentRelationName: String? = null
                for (related in relateds) {
                    // anchor node
                    val relationName = related.relationName
                    if (relationName != currentRelationName) {
                        relationNode = makeRelationNode(doc, relationsNode, relationName, 0)
                        currentRelationName = relationName
                    }

                    // recurse check
                    val recurse2 =
                        if (recurse) if (synset.domainId == Mapping.TOPS_ID && (targetRelationId == Mapping.HYPONYM_ID || targetRelationId == Mapping.INSTANCE_HYPONYM_ID || targetRelationId == Mapping.ANY_TYPE)) Mapping.NON_RECURSIVE else 0 else Mapping.NON_RECURSIVE

                    // process relation
                    walkRelation(connection, doc, relationNode, related, wordId, recurse2, targetRelationId)
                }
            }
        }

        /**
         * Process synset relation (recurses)
         *
         * @param connection       connection
         * @param doc              org.w3c.dom.Document being built
         * @param parent0          org.w3c.dom.Node walk will attach results to
         * @param related          synset to start walk from
         * @param wordId           word id to start walk from
         * @param recurseLevel     recursion level
         * @param targetRelationId target relation type id (cannot be ANYTYPE for all types)
         */
        private fun walkRelation(connection: SQLiteDatabase, doc: Document, parent0: Node?, related: Related, wordId: Long, recurseLevel: Int, targetRelationId: Int) {
            var parent = parent0

            // word in lex relations
            if (related.wordId != 0L) {
                parent = makeSenseNode(doc, parent0, related.wordId, related.synsetId, 0)
                makeWordNode(doc, parent, related.word, related.wordId)
            }

            // synset
            val synsetNode = walkSynset(connection, doc, parent, related)

            // recurse
            if (recurseLevel != Mapping.NON_RECURSIVE && related.canRecurse()) {
                // relation node
                val relationsNode: Node = makeNode(doc, synsetNode, "relations", null)

                // stop recursion in case maximum level is reached and
                // hyponym/all relations and source synset domain is tops
                if ((targetRelationId == Mapping.HYPONYM_ID || targetRelationId == Mapping.INSTANCE_HYPONYM_ID) && recurseLevel >= MAX_RECURSE_LEVEL) {
                    makeMoreRelationNode(doc, relationsNode, related.relationName, recurseLevel)
                } else {
                    // get related
                    val subRelated = (if (targetRelationId == Mapping.ANY_TYPE) related.getRelateds(connection, wordId) else related.getTypedRelateds(connection, wordId, targetRelationId)) ?: return

                    // iterate subrelations
                    var subRelatedNode: Node? = null
                    var currentSubRelationName: String? = null
                    for (subRelation in subRelated) {
                        // anchor node
                        val relationName = subRelation.relationName
                        if (relationName != currentSubRelationName) {
                            subRelatedNode = makeRelationNode(doc, relationsNode, relationName, recurseLevel)
                            currentSubRelationName = relationName
                        }
                        walkRelation(connection, doc, subRelatedNode, subRelation, wordId, recurseLevel + 1, targetRelationId)
                    }
                }
            }
        }

        /**
         * Initial house-keeping - directory queries - connections - database static data
         */
        @JvmStatic
        fun init(connection: SQLiteDatabase) {
            // do queries for static maps
            initDomains(connection)
            initRelations(connection)
        }
    }
}
