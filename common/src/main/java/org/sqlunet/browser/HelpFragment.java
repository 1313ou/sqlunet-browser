package org.sqlunet.browser;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import android.widget.Toast;

import org.sqlunet.browser.common.R;

/**
 * Help fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HelpFragment extends NavigableFragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "HelpFragment";

	/**
	 * Constructor
	 */
	public HelpFragment()
	{
		this.titleId = R.string.title_help_section;
	}

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
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					super.onReceivedError(webView, request, error);
				}
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
				final String fileName = uri.getLastPathSegment();
				if (fileName.endsWith("pdf") || fileName.endsWith("PDF"))
				{
					if (handleUri(uri, "application/pdf"))
					{
						return true;
					}
				}
				webView.loadUrl(uri.toString());
				return false;
			}

			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(final WebView webView, final String url)
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

	private boolean handleUri(final Uri uri, @SuppressWarnings("SameParameterValue") final String mime)
	{
		try
		{
			final Intent intentUrl = new Intent(Intent.ACTION_VIEW);
			intentUrl.setDataAndType(uri, mime);
			intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			getActivity().startActivity(intentUrl);
			return true;
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(getActivity(), R.string.status_viewer_failed, Toast.LENGTH_LONG).show();
		}
		return false;
	}
}