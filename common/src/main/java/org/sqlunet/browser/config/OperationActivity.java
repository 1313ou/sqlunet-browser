/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser.config;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.browser.common.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

public class OperationActivity extends AppCompatActivity
{
	private static final String TAG = "OperationActivity";

	public static final String ARG_OP = "arg_op";

	public static final String OP_COPY = "op_copy";

	public static final String OP_UNZIP = "op_unzip";

	public static final String OP_UNZIP_ENTRY = "op_unzip_entry";

	public static final String OP_EXEC_SQL = "op_exec_sql";

	public static final String OP_EXEC_ZIPPED_SQL = "op_exec_zipped_sql";

	public static final String OP_MD5 = "op_md5";

	public static final String ARG_TYPES = "arg_types";

	public static final String ARG_ZIP_ENTRY = "arg_zip_entry";

	private final Consumer<Uri> consumer = uri -> {

		ContentResolver resolver = getContentResolver();
		String type = SAFUtils.getType(uri, resolver);
		Log.i(TAG, "Type: " + type);
		if (type == null)
		{
			Log.i(TAG, "Type: null");
		}
		/*
		else if (type.startsWith("image/"))
		{
			return imageHandler.apply(uri);
		}
		else
		{
			return textHandler.apply(uri);
		}
		*/
		String op = getIntent().getStringExtra(ARG_OP);
		switch (op)
		{
			case OP_COPY:
				Operations.copy(uri, this);
				break;
			case OP_UNZIP:
				Operations.unzip(uri, this);
				break;
			case OP_UNZIP_ENTRY:
				Operations.unzipEntry(uri, getIntent().getStringExtra(ARG_ZIP_ENTRY), this);
				break;
			case OP_EXEC_SQL:
				Operations.execSql(uri, this);
				break;
			case OP_EXEC_ZIPPED_SQL:
				Operations.execZippedSql(uri, getIntent().getStringExtra(ARG_ZIP_ENTRY), this);
				break;
			case OP_MD5:
				Operations.md5(uri, this);
				break;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActivityResultLauncher<Intent> launcher = SAFUtils.makeListener(this, consumer);
		setContentView(R.layout.activity_operation);

		String[] types = getIntent().getStringArrayExtra(ARG_TYPES);
		SAFUtils.pick(launcher, types);
	}
}