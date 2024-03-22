/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

import android.annotation.SuppressLint
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.util.SparseArray

/**
 * Id-name mappings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object Mapping {

    private const val TAG = "Mapping"

    /**
     * `topsId` is a constant for Tops domain id
     */
    const val topsId = 3

    /**
     * `hyponymId` is id for hyponym relation
     */
    const val hyponymId = 2

    /**
     * `instanceHyponymId` is id for instance hyponym relation
     */
    const val instanceHyponymId = 4

    /**
     * is the constant for unspecified search types (pos, domains, relations)
     */
    const val ANYTYPE = -1

    /**
     * is the constant for non recursive queries
     */
    const val NONRECURSIVE = -1

    /**
     * `domains` is an array of domains
     */
    private val domains: MutableList<Domain> = ArrayList()

    /**
     * `domains` is map of domains by name
     */
    private val domainsByName: MutableMap<String, Domain> = HashMap()

    /**
     * `relationsById` is relations mapped by id
     */
    private val relationsById: SparseArray<Relation> = SparseArray()

    /**
     * `relationsByName` is relations mapped by name
     */
    private var relationsByName: MutableMap<String, Relation> = HashMap()

    @JvmStatic
    val posNames: Array<String>
        get() = arrayOf("noun", "verb", "adj", "adv")

    @JvmStatic
    val domainNames: Array<String>
        get() {
            val nameSet: Set<String> = domainsByName.keys
            return nameSet.toTypedArray<String>()
        }

    @JvmStatic
    val relationNames: Array<String>
        get() {
            val nameSet: Set<String> = relationsByName.keys
            return nameSet.toTypedArray<String>()
        }

    /**
     * Read domain mappings from database
     *
     * @param connection connection
     */
    @JvmStatic
    fun initDomains(connection: SQLiteDatabase) {
        // domain
        try {
            DomainsQuery(connection).use { query ->
                query.execute()
                while (query.next()) {
                    val id = query.id
                    val pos = query.pos
                    val name = query.posDomainName.replace(' ', '.')
                    val domain = Domain(id, pos, name)
                    domains.add(domain)
                    domainsByName[name] = domain
                }
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While initializing domains", e)
            throw RuntimeException(e)
        }
    }

    /**
     * Read relation mappings from database
     *
     * @param connection connection
     */
    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun initRelations(connection: SQLiteDatabase) {
        try {
            RelationsQuery(connection).use { query ->
                query.execute()
                while (query.next()) {
                    val id = query.id
                    val name = query.name.replace(' ', '_').lowercase()
                    val recurses = query.recurse
                    val relation = Relation(id, name, recurses)
                    relationsById.put(id, relation)
                    relationsByName[name] = relation
                }
            }
        } catch (e: SQLException) {
            Log.e(TAG, "While initializing relations", e)
            throw RuntimeException(e)
        }
    }

    /**
     * Find part-of-speech name from domainid
     *
     * @param domainId is the domainid
     * @return part-of-speech name
     */
    fun getDomainPosName(domainId: Int): String {
        return try {
            val domain = domains[domainId]
            getPosName(domain.posId)
        } catch (e: IndexOutOfBoundsException) {
            "domainid.$domainId"
        } catch (e: IllegalArgumentException) {
            "domainid.$domainId"
        }
    }

    /**
     * Find domain name from domain id
     *
     * @param domainId is the domain id
     * @return domain name or "domainid.xxx" if not found
     */
    fun getDomainName(domainId: Int): String {
        return try {
            val domain = domains[domainId]
            domain.domainName
        } catch (e: IndexOutOfBoundsException) {
            "domainid.$domainId"
        }
    }

    /**
     * Find domain id from part-of-speech name and domain name
     *
     * @param posName    target part-of-speech name
     * @param domainName target domain name
     * @return domain id or -1 if not found
     */
    @JvmStatic
    fun getDomainId(posName: String?, domainName: String?): Int {
        if (posName == null || domainName == null) {
            return ANYTYPE
        }
        val fullName = "$posName.$domainName"
        return try {
            val domain = domainsByName[fullName]!!
            domain.id
        } catch (e: NullPointerException) {
            ANYTYPE
        }
    }

    /**
     * Find part-of-speech id (n,v,a,r) from part-of-speech name
     *
     * @param posName target part-of-speech name
     * @return part-of-speech id or ANYTYPE if not found
     */
    @JvmStatic
    fun getPosId(posName: String?): Int {
        if (posName == null) {
            return ANYTYPE
        }
        when (posName) {
            "noun" -> return 'n'.toInt()
            "verb" -> return 'v'.toInt()
            "adj" -> return 'a'.toInt()
            "adv" -> return 'r'.toInt()
        }
        return ANYTYPE
    }

    /**
     * Find part-of-speech id (n,v,a,r) from part-of-speech name
     *
     * @param posId target part-of-speech id
     * @return part-of-speech name or null if not found
     */
    private fun getPosName(posId: Int): String {
        when (posId) {
            'n'.toInt() -> return "noun"
            'v'.toInt() -> return "verb"
            'a'.toInt() -> return "adj"
            'r'.toInt() -> return "adv"
        }
        throw IllegalArgumentException("" + posId)
    }

    /**
     * Find relation name from relation id
     *
     * @param relationId target relation id
     * @return relation name or "relation.xxx" if not found
     */
    @JvmStatic
    fun getRelationName(relationId: Int): String {
        return try {
            val relation = relationsById[relationId]
            relation.name
        } catch (e: NullPointerException) {
            "relation.$relationId"
        }
    }

    /**
     * Find relation id from relation name
     *
     * @param relationName target relation name
     * @return relation id or ANYTYPE if it is not found
     */
    @JvmStatic
    fun getRelationId(relationName: String?): Int {
        return if (relationName == null) {
            ANYTYPE
        } else try {
            val relation = relationsByName[relationName]!!
            relation.id
        } catch (e: NullPointerException) {
            ANYTYPE
        }
    }

    /**
     * Determine if this relation can recurse
     *
     * @param relationId target relation id
     * @return whether this relation can recurse
     */
    @JvmStatic
    fun canRecurse(relationId: Int): Boolean {
        return try {
            val relation = relationsById[relationId]
            relation.recurses
        } catch (e: IndexOutOfBoundsException) {
            false
        } catch (e: NullPointerException) {
            false
        }
    }
}