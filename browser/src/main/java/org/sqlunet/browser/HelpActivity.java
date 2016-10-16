package org.sqlunet.browser;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Help activity
 *
 * @author Bernard Bou
 */
public class HelpActivity extends Activity
{
	/**
	 * Log tag
	 */
	private static final String TAG = "Help activity"; //$NON-NLS-1$

	/**
	 * Show help
	 */
	static public void start(final Context context)
	{
		final Intent intent = new Intent(context, HelpActivity.class);
		context.startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// web view
		final WebView webview = (WebView) findViewById(R.id.webView);
		webview.setWebViewClient(new WebViewClient()
		{
			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public void onReceivedError(final WebView view, final WebResourceRequest request, WebResourceError error)
			{
				super.onReceivedError(view, request, error);
				Log.e(HelpActivity.TAG, error.toString());
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl)
			{
				super.onReceivedError(view, errorCode, description, failingUrl);
				Log.e(HelpActivity.TAG, failingUrl + ':' + description + ',' + errorCode);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request)
			{
				final Uri uri = request.getUrl();
				view.loadUrl(uri.toString());
				return false;
			}

			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url)
			{
				view.loadUrl(url);
				return false;
			}
		});

		final String lang = getString(R.string.lang_tag);
		String url = "file:///android_asset/help/"; //$NON-NLS-1$
		if (!lang.isEmpty())
			url += lang + '-';
		url += "index.html"; //$NON-NLS-1$
		webview.loadUrl(url);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		if (item.getItemId() == R.id.action_about)
		{
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
