/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.fn.web;

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
import org.sqlunet.browser.fn.BuildConfig;
import org.sqlunet.browser.fn.DocumentTransformer;
import org.sqlunet.browser.fn.R;
import org.sqlunet.browser.fn.Settings;
import org.sqlunet.browser.web.DocumentStringLoader;
import org.sqlunet.browser.web.WebModel;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.dom.DomValidator;
import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.sql.FrameNetImplementation;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.LogUtils;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.DataSource;
import org.sqlunet.sql.NodeFactory;
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

	static public final String FRAGMENT_TAG = "web";

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

		WebDocumentStringLoader(@NonNull final Context context, final Parcelable pointer, final int type, final String data, final int sources, final boolean xml)
		{
			super();
			this.context = context;
			this.pointer = pointer;
			this.type = type;
			this.data = data;
			this.sources = sources;
			this.xml = xml;
		}

		@Nullable
		@Override
		public String getDoc()
		{
			try (DataSource dataSource = new DataSource(StorageSettings.getDatabasePath(this.context)))
			{
				// data source
				final SQLiteDatabase db = dataSource.getConnection();

				// dom documents
				Document fnDomDoc = null;

				// selector mode
				final boolean isSelector = this.data != null;

				// make documents
				if (isSelector)
				{
					// this is a selector query
					if (Settings.Source.FRAMENET.test(sources))
					{
						fnDomDoc = new FrameNetImplementation(true).querySelectorDoc(db, data, null);
					}
				}
				else
				{
					// this is a detail query
					switch (this.type)
					{
						case ProviderArgs.ARG_QUERYTYPE_ALL:
							if (this.pointer != null)
							{
								if (this.pointer instanceof Pointer)
								{
									final Pointer xPointer = (Pointer) this.pointer;
									final long id = xPointer.getId();
									final boolean isFrame = xPointer instanceof FnFramePointer;
									fnDomDoc = isFrame ? new FrameNetImplementation(true).queryFrameDoc(db, id, null) : new FrameNetImplementation(true).queryLexUnitDoc(db, id);
								}
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT:
							final FnLexUnitPointer lexunitPointer = (FnLexUnitPointer) this.pointer;
							Log.d(TAG, "ArgPosition: fnlexunit=" + lexunitPointer);
							if (lexunitPointer != null && Settings.Source.FRAMENET.test(this.sources))
							{
								fnDomDoc = new FrameNetImplementation(true).queryLexUnitDoc(db, lexunitPointer.getId());
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_FNFRAME:
							final FnFramePointer framePointer = (FnFramePointer) this.pointer;
							Log.d(TAG, "ArgPosition: fnframe=" + framePointer);
							if (framePointer != null && Settings.Source.FRAMENET.test(this.sources))
							{
								fnDomDoc = new FrameNetImplementation(true).queryFrameDoc(db, framePointer.getId(), null);
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_FNSENTENCE:
							final FnSentencePointer sentencePointer = (FnSentencePointer) this.pointer;
							Log.d(TAG, "ArgPosition: fnsentence=" + sentencePointer);
							if (sentencePointer != null && Settings.Source.FRAMENET.test(this.sources))
							{
								fnDomDoc = new FrameNetImplementation(true).querySentenceDoc(db, sentencePointer.getId());
							}
							break;

						case ProviderArgs.ARG_QUERYTYPE_FNANNOSET:
							final FnAnnoSetPointer annoSetPointer = (FnAnnoSetPointer) this.pointer;
							Log.d(TAG, "ArgPosition: fnannoset=" + annoSetPointer);
							if (annoSetPointer != null && Settings.Source.FRAMENET.test(this.sources))
							{
								fnDomDoc = new FrameNetImplementation(true).queryAnnoSetDoc(db, annoSetPointer.getId());
							}
							break;
					}
				}

				// stringify
				return docsToString(this.data, this.xml, isSelector, fnDomDoc);
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
			return inflater.inflate(R.layout.fragment_web, container, false);
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

		// webview
		this.webview = view.findViewById(R.id.webView);

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
		this.model = new ViewModelProvider(this).get("fn:web(doc)", WebModel.class);
		this.model.getData().observe(getViewLifecycleOwner(), doc -> {
			Log.d(TAG, "onLoadFinished");
			final String mimeType = xml ? "text/xml" : "text/html";
			final String baseUrl = "file:///android_asset/";
			WebFragment.this.webview.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", null);
			//WebFragment.this.webview.loadUrl("_about:blank");	}
		});
	}

	/**
	 * Load web view with data
	 */
	@SuppressWarnings("UnusedReturnValue")
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
					Log.d(TAG, "Query: " + query + " name=" + name + " value=" + value);
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
							case "fnframeid":
								type = ProviderArgs.ARG_QUERYTYPE_FNFRAME;
								pointer = new FnFramePointer(id);
								break;
							case "fnluid":
								type = ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT;
								pointer = new FnLexUnitPointer(id);
								break;
							case "fnsentenceid":
								type = ProviderArgs.ARG_QUERYTYPE_FNSENTENCE;
								pointer = new FnSentencePointer(id);
								break;
							case "fnannosetid":
								type = ProviderArgs.ARG_QUERYTYPE_FNANNOSET;
								pointer = new FnAnnoSetPointer(id);
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

		// settings sources
		int mask = 0;
		if (Settings.getFrameNetPref(requireContext()))
		{
			mask |= Settings.Source.FRAMENET.set(mask);
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
		Log.d(TAG, "ArgPosition: query=" + pointer);

		// hint
		//final String posString = args.getString(ProviderArgs.ARG_HINTPOS);
		//final Character pos = posString == null ? null : posString.charAt(0);

		// text
		final String data = args.getString(ProviderArgs.ARG_QUERYSTRING);
		Log.d(TAG, "ArgPosition: data=" + data);

		// load the contents
		this.model.loadData(new WebDocumentStringLoader(requireContext(), pointer, type, data, sources, xml));
	}

	/**
	 * Assemble result
	 *
	 * @param word       query word
	 * @param xml        assemble as xml (or document to be xslt-transformed if false)
	 * @param isSelector is selector source
	 * @param fnDomDoc   framenet document
	 * @return string
	 */
	@NonNull
	private String docsToString(@SuppressWarnings("UnusedParameters") final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			@Nullable final Document fnDomDoc)
	{
		// LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(vnDomDoc), false, "vn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(pbDomDoc), false, "pb_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(fnDomDoc), false, "fn_sqlunet.log");
		// LogUtils.writeLog(DomTransformer.docToXml(bncDomDoc), false, "bnc_sqlunet.log");

		String data;
		if (xml)
		{
			// merge all into one
			final Document rootDomDoc = DomFactory.makeDocument();
			NodeFactory.makeRootNode(rootDomDoc, rootDomDoc, "sqlunet", null, WebFragment.SQLUNET_NS);
			if (fnDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(fnDomDoc.getFirstChild(), true));
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
			if (fnDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/framenet.css").append(STYLESHEET2);
			}

			// javascripts
			sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2);
			sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2);
			if (fnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/framenet.js").append(SCRIPT2);
			}

			// body
			sb.append(BODY2);

			// top
			sb.append(TOP);

			// xslt-transformed data
			sb.append(LIST1);
			if (fnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(new DocumentTransformer().docToHtml(fnDomDoc, Settings.Source.FRAMENET.toString(), isSelector));
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
				DomValidator.validateDocs(xsd, fnDomDoc);
				LogUtils.writeLog(false, requireContext(), null, fnDomDoc);
				LogUtils.writeLog(data, false, requireContext(), null);
				Log.d(TAG, "output=\n" + data);
			}
		}
		return data;
	}
}
