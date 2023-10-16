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
import org.sqlunet.browser.vn.BuildConfig;
import org.sqlunet.browser.vn.DocumentTransformer;
import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.dom.DomValidator;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.sql.PropBankImplementation;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.LogUtils;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.DataSource;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.sql.VerbNetImplementation;
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
		@SuppressWarnings({"boxing"})
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
				Document vnDomDoc = null;
				Document pbDomDoc = null;

				// selector mode
				final boolean isSelector = this.data != null;

				// make documents
				if (isSelector)
				{
					// this is a selector query
					if (Settings.Source.WORDNET.test(sources))
					{
						wnDomDoc = new WordNetImplementation().querySelectorDoc(db, data);
					}

					if (Settings.Source.VERBNET.test(sources))
					{
						vnDomDoc = new VerbNetImplementation().querySelectorDoc(db, data);
					}

					if (Settings.Source.PROPBANK.test(sources))
					{
						pbDomDoc = new PropBankImplementation().querySelectorDoc(db, data);
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
								if (pointer instanceof XSelectorPointer)
								{
									final XSelectorPointer xPointer = (XSelectorPointer) pointer;
									final String xSources = xPointer.getXSources();
									final Long xClassId = xPointer.getXClassId();
									// final Long xMemberId = xpointer.getXMemberId();
									final long wordId = xPointer.getWordId();
									final long synsetId = xPointer.getSynsetId();
									if (xSources == null || xSources.contains("wn")) //
									{
										wnDomDoc = new WordNetImplementation().querySenseDoc(db, wordId, synsetId);
									}
									if ((xSources == null || xSources.contains("vn")) && xClassId != null) //
									{
										vnDomDoc = new VerbNetImplementation().queryClassDoc(db, xClassId, pos);
									}
									if ((xSources == null || xSources.contains("pb")) && xClassId != null) //
									{
										pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, xClassId, pos);
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

									if (Settings.Source.VERBNET.test(sources))
									{
										vnDomDoc = new VerbNetImplementation().queryDoc(db, wordId, synsetId, pos);
									}
									if (Settings.Source.PROPBANK.test(sources))
									{
										pbDomDoc = new PropBankImplementation().queryDoc(db, wordId, pos);
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

						case ProviderArgs.ARG_QUERYTYPE_VNCLASS:
							final VnClassPointer vnclassPointer = (VnClassPointer) pointer;
							Log.d(TAG, "ARG_POSITION vnclass=" + vnclassPointer);
							if (vnclassPointer != null && Settings.Source.VERBNET.test(sources))
							{
								vnDomDoc = new VerbNetImplementation().queryClassDoc(db, vnclassPointer.getId(), null);
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_PBROLESET:
							final PbRoleSetPointer pbroleSetPointer = (PbRoleSetPointer) pointer;
							Log.d(TAG, "ARG_POSITION pbroleset=" + pbroleSetPointer);
							if (pbroleSetPointer != null && Settings.Source.PROPBANK.test(sources))
							{
								pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, pbroleSetPointer.getId(), null);
							}
							break;
					}
				}

				// stringify
				return docsToString(this.data, this.xml, isSelector, wnDomDoc, vnDomDoc, pbDomDoc);
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
		this.model = new ViewModelProvider(this).get("vn:web(doc)", WebModel.class);
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
							case "vnclassid":
								type = ProviderArgs.ARG_QUERYTYPE_VNCLASS;
								pointer = new VnClassPointer(id);
								break;
							case "pbrolesetid":
								type = ProviderArgs.ARG_QUERYTYPE_PBROLESET;
								pointer = new PbRoleSetPointer(id);
								break;
							case "wordid":
								type = ProviderArgs.ARG_QUERYTYPE_WORD;
								pointer = new WordPointer(id);
								break;
							case "synsetid":
								type = ProviderArgs.ARG_QUERYTYPE_SYNSET;
								pointer = new SynsetPointer(id);
								break;
							default:
								Log.e(TAG, "Ill-formed Uri: " + uri);
								return false;
						}

						targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
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

		// context
		final Context context = requireContext();

		// settings sources
		final int enable = Settings.getAllPref(context);

		// settings output
		final boolean xml = Settings.getXmlPref(context);

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
		this.model.loadData(new WebDocumentStringLoader(context, pointer, pos, type, data, enable, xml));
	}

	/**
	 * Assemble result
	 *
	 * @param word       query word
	 * @param xml        assemble as xml (or document to be xslt-transformed if false)
	 * @param isSelector is selector source
	 * @param wnDomDoc   wordnet document
	 * @param vnDomDoc   verbnet document
	 * @param pbDomDoc   propbank document
	 * @return string
	 */
	@NonNull
	private String docsToString(@SuppressWarnings("UnusedParameters") final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			@Nullable final Document wnDomDoc,  //
			@Nullable final Document vnDomDoc,  //
			@Nullable final Document pbDomDoc)
	{
		// LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(vnDomDoc), false, "vn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(pbDomDoc), false, "pb_sqlunet.log");
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
			if (vnDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(vnDomDoc.getFirstChild(), true));
			}
			if (pbDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(pbDomDoc.getFirstChild(), true));
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
			if (vnDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/verbnet.css").append(STYLESHEET2);
			}
			if (pbDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/propbank.css").append(STYLESHEET2);
			}

			// javascripts
			sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2);
			if (vnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/verbnet.js'></script>");
			}
			if (pbDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/propbank.js").append(SCRIPT2);
			}

			// body
			sb.append(BODY2);

			// top
			sb.append(TOP);

			// xslt-transformed data
			sb.append(LIST1);
			if (vnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(vnDomDoc, Settings.Source.VERBNET.toString(), isSelector));
				sb.append(ITEM2);
			}
			if (pbDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(pbDomDoc, Settings.Source.PROPBANK.toString(), isSelector));
				sb.append(ITEM2);
			}
			if (wnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(wnDomDoc, Settings.Source.WORDNET.toString(), isSelector));
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
				DomValidator.validateDocs(xsd, wnDomDoc, vnDomDoc, pbDomDoc);
				LogUtils.writeLog(false, requireContext(), null, wnDomDoc, vnDomDoc, pbDomDoc);
				LogUtils.writeLog(data, false, requireContext(), null);
				Log.d(TAG, "output=\n" + data);
			}
		}
		return data;
	}
}
