/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Set of roles attached to a VerbNet class
 *
 * @param roles is the list of roles
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnRoleSet private constructor(
    @JvmField val roles: MutableList<VnRole>,
) {
    companion object {
        /**
         * Make VerbNet role sets from query built from class id, word id and synset id
         *
         * @param connection connection
         * @param classId    is the class id to build query from
         * @return set of VerbNet roles
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, classId: Long): VnRoleSet? {
            VnRoleQueryFromClassId(connection, classId).use { query ->
                query.execute()
                var roleSet: VnRoleSet? = null
                while (query.next()) {
                    val roleType = query.roleType
                    val selectionRestrictions = query.selectionRestriction

                    // new role
                    val role = VnRole(roleType, selectionRestrictions)

                    // allocate
                    if (roleSet == null) {
                        roleSet = VnRoleSet(ArrayList())
                    }

                    // addItem role to role set
                    roleSet.roles.add(role)
                }
                return roleSet
            }
        }

        /**
         * Make VerbNet role sets from query built from class id, word id and synset id
         *
         * @param connection connection
         * @param classId    is the class id to build query from
         * @param wordId     is the word id to build query from
         * @param synsetId   is the synset id to build query from (null for any)
         * @return set of VerbNet roles
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, classId: Long, wordId: Long, synsetId: Long?): VnRoleSet? {
            VnRoleQueryFromClassIdAndSense(connection, classId, wordId, synsetId).use { query ->
                query.execute()
                var roleSet: VnRoleSet? = null
                while (query.next()) {
                    val roleType = query.roleType
                    val selectionRestrictions = query.selectionRestriction

                    // new role
                    val role = VnRole(roleType, selectionRestrictions)

                    // allocate
                    if (roleSet == null) {
                        roleSet = VnRoleSet(ArrayList())
                    }

                    // addItem role to role set
                    roleSet.roles.add(role)
                }
                return roleSet
            }
        }
    }
}
