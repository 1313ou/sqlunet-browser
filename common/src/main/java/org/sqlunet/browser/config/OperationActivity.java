package org.sqlunet.browser.config;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.browser.common.R;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

public class OperationActivity extends AppCompatActivity
{
	private static final String TAG = "OperationActivity";

	public static final String ARG_OP = "arg_op";

	public static final String OP_COPY = "op_copy";

	public static final String OP_UNZIP = "op_unzip";

	public static final String OP_EXEC_SQL = "op_exec_sql";

	public static final String OP_EXEC_ZIPPED_SQL = "op_exec_zipped_sql";

	public static final String OP_MD5 = "op_md5";

	private ActivityResultLauncher<Intent> launcher;

	private final Function<Uri, Object> handler = uri -> {
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
		Object result = null;
		switch (op)
		{
			case OP_COPY:
				result = FileOperations.copy(uri, this);
				break;
			case OP_UNZIP:
				result = FileOperations.unzip(uri, this);
				break;
			case OP_EXEC_SQL:
				result = FileOperations.execSql(uri, this);
				break;
			case OP_EXEC_ZIPPED_SQL:
				result = FileOperations.execZippedSql(uri, "sql", this);
				break;
			case OP_MD5:
				result = FileOperations.md5(uri, this);
				break;
		}
		return result;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		launcher = SAFUtils.makeListener(this, handler);
		setContentView(R.layout.activity_file_operation);
		SAFUtils.pick("*/*", launcher);
	}
}