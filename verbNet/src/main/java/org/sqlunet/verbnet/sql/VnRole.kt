/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

/**
 * VerbNet role
 *
 * @param roleType              role type
 * @param selectionRestrictions selectional restriction (XML)
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnRole(
    val roleType: String,
    val selectionRestrictions: String?
) 