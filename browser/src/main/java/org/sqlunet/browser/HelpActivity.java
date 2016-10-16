package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
			@Override
			public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl)
			{
				Log.e(HelpActivity.TAG, failingUrl + ':' + description + ',' + errorCode);
			}

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

	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	// */
	// @Override
	// public boolean onCreateOptionsMenu(final Menu menu)
	// {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.help, menu);
	// return true;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	// @Override
	// public boolean onOptionsItemSelected(final MenuItem item)
	// {
	// if (item.getItemId() == R.id.action_tips)
	// {
	// Tip.showTips(getFragmentManager());
	// return true;
	// }
	// else if (item.getItemId() == R.id.action_help)
	// {
	// HelpActivity.start(this);
	// return true;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * Show help
	 */
	static public void start(final Context context)
	{
		final Intent intent = new Intent(context, HelpActivity.class);
		context.startActivity(intent);
	}
}
