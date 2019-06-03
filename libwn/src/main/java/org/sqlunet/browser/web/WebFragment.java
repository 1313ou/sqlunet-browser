/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.sqlunet.Pointer;
import org.sqlunet.bnc.sql.BncImplementation;
import org.sqlunet.browser.wn.DocumentTransformer;
import org.sqlunet.browser.wn.Settings;
import org.sqlunet.browser.wn.lib.BuildConfig;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.dom.DomValidator;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.LogUtils;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.DataSource;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.sql.WordNetImplementation;
import org.w3c.dom.Document;

import java.net.URLDecoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * A fragment representing a SqlUNet web view.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WebFragment extends Fragment
{
	static private final String TAG = "WebF";

	static private final String SQLUNET_NS = "http://org.sqlunet";

	static class WebDocumentStringLoader implements DocumentStringLoader
	{
		@NonNull
		final Context context;
		final private int type;
		final private String data;
		final private Parcelable pointer;
		final private Character pos;
		final private int sources;
		final private boolean xml;

		WebDocumentStringLoader(@NonNull final Context context, final int type, final String data, final Parcelable pointer, final Character pos, final int sources, final boolean xml)
		{
			super();
			this.context = context;
			this.data = data;
			this.type = type;
			this.pointer = pointer;
			this.pos = pos;
			this.sources = sources;
			this.xml = xml;
		}

		@Nullable
		@SuppressWarnings({"boxing"})
		@Override
		public String getDoc()
		{
			DataSource dataSource = null;
			try
			{
				// data source
				dataSource = new DataSource(StorageSettings.getDatabasePath(this.context));
				final SQLiteDatabase db = dataSource.getConnection();
				WordNetImplementation.init(db);

				// dom documents
				Document wnDomDoc = null;
				Document bncDomDoc = null;

				// selector mode
				final boolean isSelector = this.data != null;

				// make documents
				if (isSelector)
				{
					// this is a selector query
					if (Settings.Source.WORDNET.test(this.sources))
					{
						wnDomDoc = new WordNetImplementation().querySelectorDoc(db, this.data);
					}
				}
				else
				{
					// this is a detail query
					switch (this.type)
					{
						case ProviderArgs.ARG_QUERYTYPE_ALL:
							if (pointer != null)
							{
								final SensePointer sense2Pointer = (SensePointer) this.pointer;
								final long wordId = sense2Pointer.getWordId();
								final Long synsetId = sense2Pointer.getSynsetId();
								if (Settings.Source.WORDNET.test(this.sources))
								{
									wnDomDoc = new WordNetImplementation().queryDoc(db, wordId, synsetId, true, false);
								}
								if (Settings.Source.BNC.test(this.sources))
								{
									bncDomDoc = new BncImplementation().queryDoc(db, wordId, pos);
								}
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_WORD:
							@SuppressWarnings("TypeMayBeWeakened") final WordPointer wordPointer = (WordPointer) this.pointer;
							Log.d(WebFragment.TAG, "word=" + wordPointer);
							if (wordPointer != null && Settings.Source.WORDNET.test(this.sources))
							{
								wnDomDoc = new WordNetImplementation().queryWordDoc(db, wordPointer.getWordId());
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_SYNSET:
							@SuppressWarnings("TypeMayBeWeakened") final SynsetPointer synsetPointer = (SynsetPointer) this.pointer;
							Log.d(WebFragment.TAG, "synset=" + synsetPointer);
							if (synsetPointer != null && Settings.Source.WORDNET.test(this.sources))
							{
								wnDomDoc = new WordNetImplementation().querySynsetDoc(db, synsetPointer.getSynsetId());
							}
							break;
					}
				}

				// stringify
				return WebFragment.docsToString(data, xml, isSelector, wnDomDoc, bncDomDoc);
			}
			catch (@NonNull final Exception e)
			{
				Log.e(WebFragment.TAG, "getDoc", e);
			}
			finally
			{
				if (dataSource != null)
				{
					dataSource.close();
				}
			}
			return null;
		}
	}

	/**
	 * HTML stuff
	 */
	static private final String BODY1 = "<HTML><HEAD>";
	static private final String BODY2 = "</HEAD><BODY>";
	static private final String BODY3 = "</BODY></HTML>";
	static private final String TOP = "<DIV class='titlesection'><IMG class='titleimg' src='images/logo.png'/></DIV>";
	static private final String STYLESHEET1 = "<LINK rel='stylesheet' type='text/css' href='";
	static private final String STYLESHEET2 = "' />";
	static private final String SCRIPT1 = "<SCRIPT type='text/javascript' src='";
	static private final String SCRIPT2 = "'></SCRIPT>";
	static private final String LIST1 = "<UL style='display: block;'>";
	static private final String LIST2 = "</UL>";
	static private final String ITEM1 = "<LI class='treeitem treepanel'>";
	static private final String ITEM2 = "</LI>";

	/**
	 * WebView
	 */
	@SuppressWarnings("unused")
	private WebView webview;

	/**
	 * View model
	 */
	@SuppressWarnings("unused")
	private WebModel model;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public WebFragment()
	{
	}

	@Override
	public void onAttach(@NonNull final Context context)
	{
		super.onAttach(context);
		makeModels();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		final boolean xml = Settings.getXmlPref(requireContext());
		this.model = ViewModelProviders.of(this).get("wn:web(doc)", WebModel.class);
		this.model.getData().observe(this, doc -> {
			Log.d(WebFragment.TAG, "onLoadFinished");
			final String mimeType = xml ? "text/xml" : "text/html";
			final String baseUrl = "file:///android_asset/";
			WebFragment.this.webview.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", null);
			//WebFragment.this.webview.loadUrl("_about:blank");
		});
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_web, container, false);

		// webview
		this.webview = view.findViewById(R.id.webView);

		// load view
		load();

		return view;
	}

	/**
	 * Load web view with data
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void load()
	{
		// settings
		final WebSettings webSettings = this.webview.getSettings();

		// enable javascript
		webSettings.setJavaScriptEnabled(true);

		// zoom support
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);

		// client
		final WebViewClient webClient = new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String urlString)
			{
				final Uri uri = Uri.parse(urlString);
				return handleUrl(uri);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, @NonNull final WebResourceRequest request)
			{
				final Uri uri = request.getUrl();
				return handleUrl(uri);
			}

			@SuppressWarnings("unused")
			private boolean handleUrl(@NonNull final Uri uri)
			{
				Log.d(WebFragment.TAG, "Uri " + uri);
				try
				{
					final String query = URLDecoder.decode(uri.getQuery(), "UTF-8");
					final String[] target = query.split("=");
					final String name = target[0];
					final String value = target[1];
					Log.d(WebFragment.TAG, "QUERY " + query + " name=" + name + " value=" + value);
					final Intent targetIntent = new Intent(requireContext(), WebActivity.class);
					if ("word".equals(name)) //
					{
						targetIntent.putExtra(ProviderArgs.ARG_QUERYSTRING, value);
					}
					else
					{
						final long id = Long.valueOf(value);

						// warn with id
						final Activity activity = getActivity();
						if (activity != null && !isDetached() && !activity.isFinishing() && !activity.isDestroyed())
						{
							activity.runOnUiThread(() -> Toast.makeText(activity, "id=" + id, Toast.LENGTH_SHORT).show());
						}

						// prepare data
						int type;
						Pointer pointer;

						switch (name)
						{
							case "wordid":
								type = ProviderArgs.ARG_QUERYTYPE_WORD;
								pointer = new WordPointer(id);
								break;
							case "synsetid":
								type = ProviderArgs.ARG_QUERYTYPE_SYNSET;
								pointer = new SynsetPointer(id);
								break;
							default:
								Log.e(WebFragment.TAG, "Ill-formed Uri: " + uri);
								return false;
						}

						final int recurse = Settings.getRecursePref(getContext());
						targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse);
					}
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					startActivity(targetIntent);
					return true;
				}
				catch (@NonNull final Exception e)
				{
					Log.e(WebFragment.TAG, "Error while loading Uri: " + uri, e);
				}
				return false;
			}
		};
		this.webview.setWebViewClient(webClient);

		// settings sources
		Context context = requireContext();

		int mask = 0;
		if (Settings.getWordNetPref(context))
		{
			mask = Settings.Source.WORDNET.set(mask);
		}
		if (Settings.getBncPref(context))
		{
			mask |= Settings.Source.BNC.set(mask);
		}
		final int sources = mask;

		// settings output
		final boolean xml = Settings.getXmlPref(context);

		// unmarshal arguments
		Bundle args = getArguments();
		assert args != null;

		// type
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_ALL);

		// pointer
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		Log.d(WebFragment.TAG, "query=" + pointer);

		// hint
		final String posString = args.getString(ProviderArgs.ARG_HINTPOS);
		final Character pos = posString == null ? null : posString.charAt(0);

		// text
		final String data = args.getString(ProviderArgs.ARG_QUERYSTRING);
		Log.d(WebFragment.TAG, "data=" + data);

		// load the contents
		this.model.loadData(new WebDocumentStringLoader(requireContext(), type, data, pointer, pos, sources, xml));
	}

	/**
	 * Assemble progressMessage
	 *
	 * @param word       query word
	 * @param xml        assemble as xml (or document to be xslt-transformed if false)
	 * @param isSelector is selector source
	 * @param wnDomDoc   wordnet document
	 * @param bncDomDoc  bnc document
	 * @return string
	 */
	static private String docsToString(@SuppressWarnings("UnusedParameters") final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			@Nullable final Document wnDomDoc,  //
			@Nullable final Document bncDomDoc)
	{
		// LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(bncDomDoc), false, "bnc_sqlunet.log");

		String data;
		if (xml)
		{
			// merge all into one
			final Document rootDomDoc = DomFactory.makeDocument();
			NodeFactory.makeRootNode(rootDomDoc, rootDomDoc, "sqlunet", null, WebFragment.SQLUNET_NS);
			if (wnDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(wnDomDoc.getFirstChild(), true));
			}
			if (bncDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(bncDomDoc.getFirstChild(), true));
			}

			data = DomTransformer.docToXml(rootDomDoc);
			if (BuildConfig.DEBUG)
			{
				LogUtils.writeLog(data, false, null);
				//noinspection ConstantConditions
				DomValidator.validateStrings(DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd"), data);
				Log.d(TAG, "output=\n" + data);
			}
		}
		else
		{
			final StringBuilder sb = new StringBuilder();

			// header
			sb.append(BODY1);

			// css style sheet
			sb.append(STYLESHEET1).append("css/style.css").append(STYLESHEET2);
			sb.append(STYLESHEET1).append("css/tree.css").append(STYLESHEET2);
			sb.append(STYLESHEET1).append("css/wordnet.css").append(STYLESHEET2);
			if (bncDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/bnc.css").append(STYLESHEET2);
			}

			// javascripts
			sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2);
			if (bncDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/bnc.js").append(SCRIPT2);
			}

			// body
			sb.append(BODY2);

			// top
			sb.append(TOP);

			// xslt-transformed data
			sb.append(LIST1);
			if (wnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(wnDomDoc, Settings.Source.WORDNET.toString(), isSelector));
				sb.append(ITEM2);
			}
			if (bncDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(bncDomDoc, Settings.Source.BNC.toString(), isSelector));
				sb.append(ITEM2);
			}
			sb.append(LIST2);

			// tail
			sb.append(BODY3);

			data = sb.toString();

			if (BuildConfig.DEBUG)
			{
				//noinspection ConstantConditions
				DomValidator.validateDocs(DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd"), wnDomDoc, bncDomDoc);
				LogUtils.writeLog(false, null, wnDomDoc, bncDomDoc);
				LogUtils.writeLog(data, false, null);
				Log.d(TAG, "output=\n" + data);
			}
		}
		return data;
	}
}
