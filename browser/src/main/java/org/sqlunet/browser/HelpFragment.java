package org.sqlunet.browser;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
	static private final String TAG = "HelpFragment";

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);

		// view
		final View view = inflater.inflate(R.layout.fragment_help, container, false);

		// web view
		final WebView webview = (WebView) view.findViewById(R.id.webView);
		webview.setWebViewClient(new WebViewClient()
		{
			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public void onReceivedError(final WebView webView, final WebResourceRequest request, WebResourceError error)
			{
				super.onReceivedError(webView, request, error);
				Log.e(HelpFragment.TAG, error.toString());
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onReceivedError(final WebView webView, final int errorCode, final String description, final String failingUrl)
			{
				super.onReceivedError(webView, errorCode, description, failingUrl);
				Log.e(HelpFragment.TAG, failingUrl + ':' + description + ',' + errorCode);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(final WebView webView, final WebResourceRequest request)
			{
				final Uri uri = request.getUrl();
				webView.loadUrl(uri.toString());
				return false;
			}

			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(final WebView webView, final String url)
			{
				webView.loadUrl(url);
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
		webview.loadUrl(url);

		return view;
	}
}
