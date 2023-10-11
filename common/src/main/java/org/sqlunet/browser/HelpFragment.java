/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Help fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HelpFragment extends Fragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "HelpF";

	/**
	 * Constructor
	 */
	public HelpFragment()
	{
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		try
		{
			// view
			final View view = inflater.inflate(R.layout.fragment_help, container, false);

			// web view
			final WebView webView = view.findViewById(R.id.webView);
			webView.clearCache(true);
			webView.clearHistory();
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			webView.setWebViewClient(new WebViewClient()
			{
				@Override
				public void onReceivedError(final WebView webView, final int errorCode, final String description, final String failingUrl)
				{
					super.onReceivedError(webView, errorCode, description, failingUrl);
					Log.e(TAG, failingUrl + ':' + description + ',' + errorCode);
				}

				@TargetApi(Build.VERSION_CODES.N)
				@Override
				public void onReceivedError(final WebView webView, final WebResourceRequest request, @NonNull WebResourceError error)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
					{
						super.onReceivedError(webView, request, error);
					}
					Log.e(TAG, error.toString());
				}

				@Override
				public boolean shouldOverrideUrlLoading(@NonNull final WebView webView, @NonNull final String url)
				{
					if (url.endsWith("pdf") || url.endsWith("PDF"))
					{
						final Uri uri = Uri.parse(url);
						if (handleUri(uri, "application/pdf"))
						{
							return true;
						}
					}
					webView.loadUrl(url);
					return false;
				}

				@TargetApi(Build.VERSION_CODES.N)
				@Override
				public boolean shouldOverrideUrlLoading(@NonNull final WebView webView, @NonNull final WebResourceRequest request)
				{
					final Uri uri = request.getUrl();
					final String fileName = uri.getLastPathSegment();
					if (fileName != null && (fileName.endsWith("pdf") || fileName.endsWith("PDF")))
					{
						if (handleUri(uri, "application/pdf"))
						{
							return true;
						}
					}
					webView.loadUrl(uri.toString());
					return false;
				}
			});

			final String lang = getString(R.string.lang_tag);
			String url = "file:///android_asset/help/";
			if (!lang.isEmpty())
			{
				url += lang + '-';
			}
			url += "index.html";
			webView.loadUrl(url);

			return view;
		}
		catch (InflateException e)
		{
			Toast.makeText(requireContext(), "No WebView support", Toast.LENGTH_LONG).show();
			return null;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();
		final ActionBar actionBar = activity.getSupportActionBar();
		assert actionBar != null;
		actionBar.setCustomView(null);
		actionBar.setBackgroundDrawable(null);
	}

	private boolean handleUri(final Uri uri, @SuppressWarnings("SameParameterValue") final String mime)
	{
		try
		{
			final Intent intentUrl = new Intent(Intent.ACTION_VIEW);
			intentUrl.setDataAndType(uri, mime);
			intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentUrl);
			return true;
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(requireContext(), R.string.status_viewer_failed, Toast.LENGTH_LONG).show();
		}
		return false;
	}
}