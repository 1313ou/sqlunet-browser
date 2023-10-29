/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.config;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TestFragment extends Fragment
{
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_test, container, false);
		final TextView textView = view.findViewById(android.R.id.text1);
		CharSequence text = buildText();
		textView.setText(text);
		return view;
	}

	CharSequence buildText()
	{
		Bundle args = getArguments();
		if (args == null)
		{
			return "null";
		}
		return args.toString();
	}
}
