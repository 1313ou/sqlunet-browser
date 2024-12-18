/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

import org.junit.Test
import org.querybuilder.Generator

class RunQueryBuilderTest {

    val module = "bnc"
    val dirProto = "src/proto/${module}"

    @Test
    fun queryInstantiateSqliteDialect() {
        // op, inDir, source, outDir, dest
        Generator.main(arrayOf("instantiate", dirProto, "SqLiteDialect.java", "-"))
    }

    @Test
    fun queryQ() {
        // op, inDir, source, outDir, dest
        Generator.main(arrayOf("generate_q_class", dirProto, "-", "Q", "org.sqlunet.${module}"))
    }

    @Test
    fun queryV() {
        // op, inDir, source, outDir, dest
        Generator.main(arrayOf("generate_v_class", dirProto, "-", "V", "org.sqlunet.${module}"))
    }

    @Test
    fun queryQV() {
        // op, inDir, source, outDir, dest
        Generator.main(arrayOf("generate_qv_class", dirProto, "-", "QV", "org.sqlunet.${module}"))
    }


    @Test
    fun export() {
        // op, inDir, outDir, dest
        Generator.main(arrayOf("export", dirProto, "-"))
    }
}
