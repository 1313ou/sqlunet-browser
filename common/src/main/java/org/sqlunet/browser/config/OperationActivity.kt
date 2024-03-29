/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import org.sqlunet.browser.common.R

class OperationActivity : AppCompatActivity() {

    private val consumer = Consumer { uri: Uri? ->
        val resolver = contentResolver
        val type = SAFUtils.getType(uri!!, resolver)
        Log.i(TAG, "Type: $type")
        if (type == null) {
            Log.i(TAG, "Type: null")
        }
        val op = intent.getStringExtra(ARG_OP)
        when (op) {
            OP_COPY -> Operations.copy(uri, this)
            OP_UNZIP -> Operations.unzip(uri, this)
            OP_UNZIP_ENTRY -> Operations.unzipEntry(uri, intent.getStringExtra(ARG_ZIP_ENTRY)!!, this)
            OP_EXEC_SQL -> Operations.execSql(uri, this)
            OP_EXEC_ZIPPED_SQL -> Operations.execZippedSql(uri, intent.getStringExtra(ARG_ZIP_ENTRY)!!, this)
            OP_MD5 -> Operations.md5(uri, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launcher = SAFUtils.makeListener(this, consumer)
        setContentView(R.layout.activity_operation)
        val types = intent.getStringArrayExtra(ARG_TYPES)
        SAFUtils.pick(launcher, *types!!)
    }

    companion object {

        private const val TAG = "OperationActivity"
        const val ARG_OP = "arg_op"
        const val OP_COPY = "op_copy"
        const val OP_UNZIP = "op_unzip"
        const val OP_UNZIP_ENTRY = "op_unzip_entry"
        const val OP_EXEC_SQL = "op_exec_sql"
        const val OP_EXEC_ZIPPED_SQL = "op_exec_zipped_sql"
        const val OP_MD5 = "op_md5"
        const val ARG_TYPES = "arg_types"
        const val ARG_ZIP_ENTRY = "arg_zip_entry"
    }
}