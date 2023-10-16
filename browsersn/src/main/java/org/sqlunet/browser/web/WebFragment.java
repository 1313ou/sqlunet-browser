/*
 * Copyright (c) 2023. Bernard Bou
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
import android.view.InflateException;
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
import org.sqlunet.browser.selector.CollocationSelectorPointer;
import org.sqlunet.browser.sn.BuildConfig;
import org.sqlunet.browser.sn.DocumentTransformer;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.Settings;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.dom.DomValidator;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.LogUtils;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.DataSource;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.syntagnet.SnCollocationPointer;
import org.sqlunet.syntagnet.sql.SyntagNetImplementation;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;
import org.sqlunet.wordnet.sql.WordNetImplementation;
import org.w3c.dom.Document;

import java.net.URL;
import java.net.URLDecoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * A fragment representing a SqlUNet web view.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WebFragment extends Fragment
{
	static private final String TAG = "WebF";

	static private final String SQLUNET_NS = "http://org.sqlunet";

	private class WebDocumentStringLoader implements DocumentStringLoader
	{
		@NonNull
		final Context context;
		final Parcelable pointer;
		final int type;
		final String data;
		final int sources;
		final boolean xml;
		final Character pos;

		WebDocumentStringLoader(@NonNull final Context context, final Parcelable pointer, final Character pos, final int type, final String data, final int sources, final boolean xml)
		{
			super();
			this.context = context;
			this.pointer = pointer;
			this.type = type;
			this.data = data;
			this.sources = sources;
			this.xml = xml;
			this.pos = pos;
		}

		@Nullable
		@SuppressWarnings("boxing")
		@Override
		public String getDoc()
		{
			try (DataSource dataSource = new DataSource(StorageSettings.getDatabasePath(this.context)))
			{
				// data source
				final SQLiteDatabase db = dataSource.getConnection();
				WordNetImplementation.init(db);

				// dom documents
				Document wnDomDoc = null;
				Document snDomDoc = null;
				Document bncDomDoc = null;

				// selector mode
				final boolean isSelector = data != null;

				// make documents
				if (isSelector)
				{
					// this is a selector query
					if (Settings.Source.WORDNET.test(sources))
					{
						wnDomDoc = new WordNetImplementation().querySelectorDoc(db, data);
					}
					if (Settings.Source.SYNTAGNET.test(sources))
					{
						snDomDoc = new SyntagNetImplementation().querySelectorDoc(db, data);
					}
				}
				else
				{
					// this is a detail query
					switch (type)
					{
						case ProviderArgs.ARG_QUERYTYPE_ALL:
							if (pointer != null)
							{
								if (pointer instanceof XSelectorPointer)
								{
									final XSelectorPointer xPointer = (XSelectorPointer) pointer;
									final String xSources = xPointer.getXSources();
									final long wordId = xPointer.getWordId();
									final long synsetId = xPointer.getSynsetId();
									if (xSources == null || xSources.contains("wn")) //
									{
										wnDomDoc = new WordNetImplementation().querySenseDoc(db, wordId, synsetId);
									}
									if (Settings.Source.BNC.test(sources))
									{
										bncDomDoc = new BncImplementation().queryDoc(db, wordId, pos);
									}
								}
								else if (pointer instanceof SnCollocationPointer)
								{
									final SnCollocationPointer collocationPointer = (SnCollocationPointer) pointer;
									final long collocationId = collocationPointer.getId();
									snDomDoc = new SyntagNetImplementation().queryCollocationDoc(db, collocationId);
								}
								else if (pointer instanceof CollocationSelectorPointer)
								{
									final CollocationSelectorPointer selectorPointer = (CollocationSelectorPointer) pointer;
									final long wordId = selectorPointer.getWordId();
									final long synsetId = selectorPointer.getSynsetId();
									final long word2Id = selectorPointer.getWord2Id();
									final long synset2Id = selectorPointer.getSynset2Id();
									if (Settings.Source.WORDNET.test(sources))
									{
										wnDomDoc = new WordNetImplementation().queryDoc(db, wordId, synsetId, true, false);
									}
									if (Settings.Source.SYNTAGNET.test(sources))
									{
										snDomDoc = new SyntagNetImplementation().queryDoc(db, wordId, synsetId, word2Id, synset2Id);
									}
									if (Settings.Source.BNC.test(sources))
									{
										bncDomDoc = new BncImplementation().queryDoc(db, wordId, pos);
									}
								}
								else
								{
									final SensePointer sense2Pointer = (SensePointer) pointer;
									final long wordId = sense2Pointer.getWordId();
									final Long synsetId = sense2Pointer.getSynsetId();
									if (Settings.Source.WORDNET.test(sources))
									{
										wnDomDoc = new WordNetImplementation().queryDoc(db, wordId, synsetId, true, false);
									}
									if (Settings.Source.SYNTAGNET.test(sources))
									{
										snDomDoc = new SyntagNetImplementation().queryDoc(db, wordId, synsetId, pos);
									}
									if (Settings.Source.BNC.test(sources))
									{
										bncDomDoc = new BncImplementation().queryDoc(db, wordId, pos);
									}
								}
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_WORD:
							@SuppressWarnings("TypeMayBeWeakened") final WordPointer wordPointer = (WordPointer) pointer;
							Log.d(TAG, "ARG_POSITION word=" + wordPointer);
							if (wordPointer != null && Settings.Source.WORDNET.test(sources))
							{
								wnDomDoc = new WordNetImplementation().queryWordDoc(db, wordPointer.getWordId());
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_SYNSET:
							@SuppressWarnings("TypeMayBeWeakened") final SynsetPointer synsetPointer = (SynsetPointer) pointer;
							Log.d(TAG, "ARG_POSITION synset=" + synsetPointer);
							if (synsetPointer != null && Settings.Source.WORDNET.test(sources))
							{
								wnDomDoc = new WordNetImplementation().querySynsetDoc(db, synsetPointer.getSynsetId());
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_COLLOCATION:
							final SnCollocationPointer collocationPointer = (SnCollocationPointer) pointer;
							Log.d(TAG, "ARG_POSITION collocation=" + collocationPointer);
							if (collocationPointer != null && Settings.Source.SYNTAGNET.test(sources))
							{
								snDomDoc = new SyntagNetImplementation().queryCollocationDoc(db, collocationPointer.getId());
							}
							break;
					}
				}

				// stringify
				return docsToString(data, xml, isSelector, wnDomDoc, snDomDoc, bncDomDoc);
			}
			catch (@NonNull final Exception e)
			{
				Log.e(TAG, "getDoc", e);
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
	private WebView webview;

	// View model

	private WebModel model;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public WebFragment()
	{
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		try
		{
			// view
			final View view = inflater.inflate(R.layout.fragment_web, container, false);

			// webview
			this.webview = view.findViewById(R.id.webView);

			return view;
		}
		catch (InflateException e)
		{
			Toast.makeText(requireContext(), "No WebView support", Toast.LENGTH_LONG).show();
			return null;
		}
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// models
		makeModels();

		// load data
		load();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		final boolean xml = Settings.getXmlPref(requireContext());
		this.model = new ViewModelProvider(this).get("web(doc)", WebModel.class);
		this.model.getData().observe(getViewLifecycleOwner(), doc -> {

			Log.d(TAG, "onLoadFinished");
			final String mimeType = xml ? "text/xml" : "text/html";
			final String baseUrl = "file:///android_asset/";
			WebFragment.this.webview.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", null);
			//WebFragment.this.webview.loadUrl("_about:blank");
		});
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
			public boolean shouldOverrideUrlLoading(@NonNull final WebView view, @NonNull final String urlString)
			{
				final Uri uri = Uri.parse(urlString);
				return handleUrl(uri);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(@NonNull final WebView view, @NonNull final WebResourceRequest request)
			{
				final Uri uri = request.getUrl();
				return handleUrl(uri);
			}

			private boolean handleUrl(@NonNull final Uri uri)
			{
				Log.d(TAG, "Uri " + uri);
				try
				{
					final String query = URLDecoder.decode(uri.getQuery(), "UTF-8");
					final String[] target = query.split("=");
					final String name = target[0];
					final String value = target[1];
					Log.d(TAG, "QUERY " + query + " name=" + name + " value=" + value);
					final Intent targetIntent = new Intent(requireContext(), WebActivity.class);
					if ("word".equals(name)) //
					{
						targetIntent.putExtra(ProviderArgs.ARG_QUERYSTRING, value);
					}
					else
					{
						final long id = Long.parseLong(value);

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
							case "collocationid":
								type = ProviderArgs.ARG_QUERYTYPE_COLLOCATION;
								pointer = new SnCollocationPointer(id);
								break;
							default:
								Log.e(TAG, "Ill-formed Uri: " + uri);
								return false;
						}

						// parameters
						final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext());
						final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

						targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse);
						targetIntent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters);
					}
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					startActivity(targetIntent);
					return true;
				}
				catch (@NonNull final Exception e)
				{
					Log.e(TAG, "Error while loading Uri: " + uri, e);
				}
				return false;
			}
		};
		this.webview.setWebViewClient(webClient);

		// settings sources
		int mask = 0;
		if (Settings.getWordNetPref(requireContext()))
		{
			mask = Settings.Source.WORDNET.set(mask);
		}
		if (Settings.getSyntagNetPref(requireContext()))
		{
			mask |= Settings.Source.SYNTAGNET.set(mask);
		}
		if (Settings.getBncPref(requireContext()))
		{
			mask |= Settings.Source.BNC.set(mask);
		}
		final int sources = mask;

		// settings output
		final boolean xml = Settings.getXmlPref(requireContext());

		// unmarshal arguments
		Bundle args = getArguments();
		assert args != null;

		// type
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_ALL);

		// pointer
		final Parcelable pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable.class) : args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		Log.d(TAG, "ARG_POSITION query=" + pointer);

		// hint
		final String posString = args.getString(ProviderArgs.ARG_HINTPOS);
		final Character pos = posString == null ? null : posString.charAt(0);

		// text
		final String data = args.getString(ProviderArgs.ARG_QUERYSTRING);
		Log.d(TAG, "ARG_POSITION data=" + data);

		// load the contents
		this.model.loadData(new WebDocumentStringLoader(requireContext(), pointer, pos, type, data, sources, xml));
	}

	/**
	 * Assemble result
	 *
	 * @param word       query word
	 * @param xml        assemble as xml (or document to be xslt-transformed if false)
	 * @param isSelector is selector source
	 * @param wnDomDoc   wordnet document
	 * @param snDomDoc   syntagnet document
	 * @param bncDomDoc  bnc document
	 * @return string
	 */
	@NonNull
	private String docsToString(@SuppressWarnings("UnusedParameters") final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			@Nullable final Document wnDomDoc,  //
			@Nullable final Document snDomDoc,  //
			@Nullable final Document bncDomDoc)
	{
		// LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(snDomDoc), false, "sn_sqlunet.log");
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
			if (snDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(snDomDoc.getFirstChild(), true));
			}
			if (bncDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(bncDomDoc.getFirstChild(), true));
			}

			data = DomTransformer.docToXml(rootDomDoc);
			if (BuildConfig.DEBUG)
			{
				LogUtils.writeLog(data, false, requireContext(), null);
				final URL xsd = DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd");
				assert xsd != null;
				DomValidator.validateStrings(xsd, data);
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
			if (snDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/syntagnet.css").append(STYLESHEET2);
			}
			if (bncDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/bnc.css").append(STYLESHEET2);
			}

			// javascripts
			sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2);
			if (snDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/syntagnet.js'></script>");
			}
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
			if (snDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(snDomDoc, Settings.Source.SYNTAGNET.toString(), isSelector));
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
				final URL xsd = DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd");
				assert xsd != null;
				DomValidator.validateDocs(xsd, wnDomDoc, snDomDoc, bncDomDoc);
				LogUtils.writeLog(false, requireContext(),null, wnDomDoc, snDomDoc, bncDomDoc);
				LogUtils.writeLog(data, false, requireContext(),null);
				Log.d(TAG, "output=\n" + data);
			}
		}
		return data;
	}
}
