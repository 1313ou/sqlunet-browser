/*
 * Copyright (c) 2023. Bernard Bou
 */
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.sqlunet.sql

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import org.sqlunet.style.Colors
import org.sqlunet.style.Factories
import org.sqlunet.style.Spanner
import java.util.LinkedList
import java.util.StringTokenizer

/**
 * Performs formatting of basic SQL statements (DML + query).
 * SqlFormatter in Hibernate project
 *
 * @author Gavin King
 * @author Steve Ebersole
 * @author Bernard Bou for adaptation
 */
object SqlFormatter {

    private const val WHITESPACE = " \n\r\u000c\t"
    private val BEGIN_CLAUSES: MutableCollection<String?> = HashSet()
    private val END_CLAUSES: MutableCollection<String?> = HashSet()
    private val LOGICAL: MutableCollection<String?> = HashSet()
    private val QUANTIFIERS: MutableCollection<String> = HashSet()
    private val DML: MutableCollection<String?> = HashSet()
    private val MISC: MutableCollection<String> = HashSet()
    private val KEYW: MutableCollection<String?> = HashSet()

    init {
        BEGIN_CLAUSES.add("left")
        BEGIN_CLAUSES.add("right")
        BEGIN_CLAUSES.add("inner")
        BEGIN_CLAUSES.add("outer")
        BEGIN_CLAUSES.add("group")
        BEGIN_CLAUSES.add("order")
        END_CLAUSES.add("where")
        END_CLAUSES.add("set")
        END_CLAUSES.add("having")
        END_CLAUSES.add("join")
        END_CLAUSES.add("from")
        END_CLAUSES.add("by")
        END_CLAUSES.add("join")
        END_CLAUSES.add("into")
        END_CLAUSES.add("union")
        LOGICAL.add("and")
        LOGICAL.add("or")
        LOGICAL.add("when")
        LOGICAL.add("else")
        LOGICAL.add("end")
        QUANTIFIERS.add("in")
        QUANTIFIERS.add("all")
        QUANTIFIERS.add("exists")
        QUANTIFIERS.add("some")
        QUANTIFIERS.add("any")
        DML.add("insert")
        DML.add("update")
        DML.add("delete")
        MISC.add("select")
        MISC.add("on")
        KEYW.add("is")
        KEYW.add("null")
        KEYW.add("as")
        KEYW.add("using")
        KEYW.add("union")
        KEYW.add("case")
        KEYW.add("then")
        KEYW.add("between")
        KEYW.add("in")
        KEYW.add("like")
        KEYW.add("asc")
        KEYW.add("desc")
        KEYW.add("not")
    }

    private const val INDENT_STRING = "    "
    private const val INITIAL = "" //"\n    "
    @JvmStatic
    fun format(source: String): CharSequence {
        return FormatProcess(source, false).perform()
    }

    @JvmStatic
    fun styledFormat(source: CharSequence): CharSequence {
        return FormatProcess(source, true).perform()
    }

    private class FormatProcess(sql: CharSequence, val style: Boolean) {
        var beginLine = true
        var afterBeginBeforeEnd = false
        var afterByOrSetOrFromOrSelect = false
        var afterOn = false
        var afterBetween = false
        var afterInsert = false
        var inFunction = 0
        var parensSinceSelect = 0
        private val parenCounts = LinkedList<Int>()
        private val afterByOrFromOrSelects = LinkedList<Boolean>()
        var indent = 1
        val result = SpannableStringBuilder()
        val tokens: StringTokenizer
        var lastToken: String? = null
        var token: String? = null
        var lcToken: String? = null

        init {
            tokens = StringTokenizer(sql.toString(), "()+*/-=<>'`\"[],$WHITESPACE", true)
        }

        fun perform(): CharSequence {
            initial()
            while (tokens.hasMoreTokens()) {
                token = tokens.nextToken()
                lcToken = token!!.lowercase()

                // single quote
                if ("'" == token) {
                    var t: String
                    do {
                        t = tokens.nextToken()
                        token += t
                    } while ("'" != t && tokens.hasMoreTokens())
                } else if ("\"" == token) {
                    var t: String
                    do {
                        t = tokens.nextToken()
                        token += t
                    } while ("\"" != t)
                }

                // comma
                if (afterByOrSetOrFromOrSelect && "," == token) {
                    commaAfterByOrFromOrSelect()
                } else if (afterOn && "," == token) {
                    commaAfterOn()
                } else if ("(" == token) {
                    openParen()
                } else if (")" == token) {
                    closeParen()
                } else if (BEGIN_CLAUSES.contains(lcToken)) {
                    beginNewClause()
                } else if (END_CLAUSES.contains(lcToken)) {
                    endNewClause()
                } else if ("select" == lcToken) {
                    select()
                } else if (DML.contains(lcToken)) {
                    updateOrInsertOrDelete()
                } else if ("values" == lcToken) {
                    values()
                } else if ("on" == lcToken) {
                    on()
                } else if (afterBetween && "and" == lcToken) {
                    misc()
                    afterBetween = false
                } else if (LOGICAL.contains(lcToken)) {
                    logical()
                } else if (isWhitespace(token!!)) {
                    white()
                } else {
                    misc()
                }
                if (!isWhitespace(token!!)) {
                    lastToken = lcToken
                }
            }
            return result
        }

        private fun commaAfterOn() {
            out()
            indent--
            newline()
            afterOn = false
            afterByOrSetOrFromOrSelect = true
        }

        private fun commaAfterByOrFromOrSelect() {
            out()
            newline()
        }

        private fun openParen() {
            if (isFunctionName(lastToken!!) || inFunction > 0) {
                inFunction++
            }
            beginLine = false
            if (inFunction > 0) {
                out()
            } else {
                out()
                if (!afterByOrSetOrFromOrSelect) {
                    indent++
                    newline()
                    beginLine = true
                }
            }
            parensSinceSelect++
        }

        private fun closeParen() {
            parensSinceSelect--
            if (parensSinceSelect < 0) {
                indent--
                parensSinceSelect = parenCounts.removeLast()
                afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast()
            }
            if (inFunction > 0) {
                inFunction--
            } else {
                if (!afterByOrSetOrFromOrSelect) {
                    indent--
                    newline()
                }
            }
            out()
            beginLine = false
        }

        private fun select() {
            outKeyw()
            indent++
            newline()
            parenCounts.addLast(parensSinceSelect)
            afterByOrFromOrSelects.addLast(afterByOrSetOrFromOrSelect)
            parensSinceSelect = 0
            afterByOrSetOrFromOrSelect = true
        }

        private fun updateOrInsertOrDelete() {
            outKeyw()
            indent++
            beginLine = false
            if ("update" == lcToken) {
                newline()
            }
            if ("insert" == lcToken) {
                afterInsert = true
            }
        }

        private fun beginNewClause() {
            if (!afterBeginBeforeEnd) {
                if (afterOn) {
                    indent--
                    afterOn = false
                }
                indent--
                newline()
            }
            outKeyw()
            beginLine = false
            afterBeginBeforeEnd = true
        }

        private fun endNewClause() {
            if (!afterBeginBeforeEnd) {
                indent--
                if (afterOn) {
                    indent--
                    afterOn = false
                }
                newline()
            }
            outKeyw()
            if ("union" != lcToken) {
                indent++
            }
            newline()
            afterBeginBeforeEnd = false
            afterByOrSetOrFromOrSelect = "by" == lcToken || "set" == lcToken || "from" == lcToken
        }

        private fun values() {
            indent--
            newline()
            outKeyw()
            indent++
            newline()
        }

        private fun logical() {
            if ("end" == lcToken) {
                indent--
            }
            newline()
            outKeyw()
            beginLine = false
        }

        private fun on() {
            indent++
            afterOn = true
            newline()
            outKeyw()
            beginLine = false
        }

        private fun misc() {
            if (KEYW.contains(lcToken)) {
                outKeyw()
            } else {
                outMisc()
            }
            if ("between" == lcToken) {
                afterBetween = true
            }
            if (afterInsert) {
                newline()
                afterInsert = false
            } else {
                beginLine = false
                if ("case" == lcToken) {
                    indent++
                }
            }
        }

        // output
        private fun initial() {
            result.append(INITIAL)
        }

        private fun white() {
            if (!beginLine) {
                result.append(" ")
            }
        }

        private fun newline() {
            result.append("\n")
            for (i in 0 until indent) {
                result.append(INDENT_STRING)
            }
            beginLine = true
        }

        private fun out() {
            result.append(token)
        }

        private fun outKeyw() {
            if (style) {
                Spanner.appendWithSpans(result, token, Factories.spans(Colors.sqlKeywordBackColor, Colors.sqlKeywordForeColor, StyleSpan(Typeface.BOLD)))
            } else {
                out()
            }
        }

        private fun outMisc() {
            if (style) {
                when (token!![0]) {
                    '\'' -> Spanner.appendWithSpans(result, token, Factories.spans(Colors.sqlSlashBackColor, Colors.sqlSlashForeColor))
                    '?' -> Spanner.appendWithSpans(result, token, Factories.spans(Colors.sqlQuestionMarkBackColor, Colors.sqlQuestionMarkForeColor))
                    else -> out()
                }
            } else {
                out()
            }
        }

        companion object {
            // test functions
            private fun isFunctionName(tok: String): Boolean {
                val begin = tok[0]
                val isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin
                return isIdentifier && !LOGICAL.contains(tok) && !END_CLAUSES.contains(tok) && !QUANTIFIERS.contains(tok) && !DML.contains(tok) && !MISC.contains(tok)
            }

            private fun isWhitespace(token: CharSequence): Boolean {
                return WHITESPACE.contains(token)
            }
        }
    }
}
