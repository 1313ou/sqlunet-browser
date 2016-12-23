package org.sqlunet.browser.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
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
import org.sqlunet.browser.BuildConfig;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.dom.DomValidator;
import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.sql.FrameNetImplementation;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.sql.PropBankImplementation;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.LogUtils;
import org.sqlunet.settings.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.DataSource;
import org.sqlunet.sql.NodeFactory;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.sql.VerbNetImplementation;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.sql.WordNetImplementation;
import org.w3c.dom.Document;

import java.net.URLDecoder;

/**
 * A fragment representing a SqlUNet web view.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WebFragment extends Fragment
{
	static private final String TAG = "WebFragment";

	private static final String SQLUNET_NS = "http://org.sqlunet";

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

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public WebFragment()
	{
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
	 * @param fnDomDoc   framenet document
	 * @param bncDomDoc  bnc document
	 * @return string
	 */
	static private String docsToString(@SuppressWarnings("UnusedParameters") final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			final Document wnDomDoc,  //
			final Document vnDomDoc,  //
			final Document pbDomDoc,  //
			final Document fnDomDoc,  //
			final Document bncDomDoc)
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
			if (fnDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(fnDomDoc.getFirstChild(), true));
			}
			if (bncDomDoc != null)
			{
				rootDomDoc.getDocumentElement().appendChild(rootDomDoc.importNode(bncDomDoc.getFirstChild(), true));
			}

			data = DomTransformer.docToXml(rootDomDoc);
			if (BuildConfig.DEBUG)
			{
				LogUtils.writeLog(data, false, null);
				DomValidator.validateStrings(DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd"), data);
			}


			// Log.d(TAG, "xml=\n" + data);
		}
		else
		{
			if (BuildConfig.DEBUG)
			{
				DomValidator.validateDocs(DocumentTransformer.class.getResource("/org/sqlunet/SqlUNet.xsd"), wnDomDoc, vnDomDoc, pbDomDoc, fnDomDoc, bncDomDoc);
			}

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
			if (fnDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/framenet.css").append(STYLESHEET2);
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
			if (vnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/verbnet.js'></script>");
			}
			if (pbDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/propbank.js").append(SCRIPT2);
			}
			if (fnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/framenet.js").append(SCRIPT2);
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
				sb.append(DocumentTransformer.docToHtml(wnDomDoc, Settings.Source.WORDNET, isSelector));
				sb.append(ITEM2);
			}
			if (vnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(DocumentTransformer.docToHtml(vnDomDoc, Settings.Source.VERBNET, isSelector));
				sb.append(ITEM2);
			}
			if (pbDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(DocumentTransformer.docToHtml(pbDomDoc, Settings.Source.PROPBANK, isSelector));
				sb.append(ITEM2);
			}
			if (fnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(DocumentTransformer.docToHtml(fnDomDoc, Settings.Source.FRAMENET, isSelector));
				sb.append(ITEM2);
			}
			if (bncDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(DocumentTransformer.docToHtml(bncDomDoc, Settings.Source.BNC, isSelector));
				sb.append(ITEM2);
			}
			sb.append(LIST2);

			// tail
			sb.append(BODY3);

			data = sb.toString();

			if (BuildConfig.DEBUG)
			{
				LogUtils.writeLog(data, false, null);
			}
		}
		return data;
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_web, container, false);

		// webview
		this.webview = (WebView) view.findViewById(R.id.webView);

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
			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String urlString)
			{
				final Uri uri = Uri.parse(urlString);
				return handleUrl(uri);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request)
			{
				final Uri uri = request.getUrl();
				return handleUrl(uri);
			}

			private boolean handleUrl(final Uri uri)
			{
				Log.d(WebFragment.TAG, "Uri " + uri);
				try
				{
					final String query = URLDecoder.decode(uri.getQuery(), "UTF-8");
					final String[] target = query.split("=");
					final String name = target[0];
					final String value = target[1];
					Log.d(WebFragment.TAG, "QUERY " + query + " name=" + name + " value=" + value);
					final Intent targetIntent = new Intent(getActivity(), WebActivity.class);
					if ("word".equals(name)) //
					{
						targetIntent.putExtra(ProviderArgs.ARG_QUERYSTRING, value);
					}
					else
					{
						final long id = Long.valueOf(value);

						// toast with id
						final Activity activity = WebFragment.this.getActivity();
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(activity, "id=" + id, Toast.LENGTH_SHORT).show();
							}
						});

						// prepare data
						int type = 0;
						Pointer pointer = null;

						if ("synsetid".equals(name)) //
						{
							type = ProviderArgs.ARG_QUERYTYPE_SYNSET;
							pointer = new SynsetPointer(id);
						}
						else if ("vnclassid".equals(name)) //
						{
							type = ProviderArgs.ARG_QUERYTYPE_VNCLASS;
							pointer = new VnClassPointer(id);
						}
						else if ("fnframeid".equals(name)) //
						{
							type = ProviderArgs.ARG_QUERYTYPE_PBROLESET;
							pointer = new PbRoleSetPointer(id);
						}
						else if ("fnluid".equals(name)) //
						{
							type = ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT;
							pointer = new FnLexUnitPointer(id);
						}

						targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type);
						targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
					}

					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					startActivity(targetIntent);
					return true;
				}
				catch (final Exception e)
				{
					Log.e(WebFragment.TAG, "URL loading ", e);
				}
				return false;
			}
		};
		this.webview.setWebViewClient(webClient);

		// settings sources
		int mask = 0;
		if (Settings.getWordNetPref(getActivity()))
		{
			mask = Settings.Source.WORDNET.set(mask);
		}
		if (Settings.getVerbNetPref(getActivity()))
		{
			mask |= Settings.Source.VERBNET.set(mask);
		}
		if (Settings.getPropBankPref(getActivity()))
		{
			mask |= Settings.Source.PROPBANK.set(mask);
		}
		if (Settings.getFrameNetPref(getActivity()))
		{
			mask |= Settings.Source.FRAMENET.set(mask);
		}
		if (Settings.getBncPref(getActivity()))
		{
			mask |= Settings.Source.BNC.set(mask);
		}
		final int sources = mask;

		// settings output
		final boolean xml = Settings.getXmlPref(getActivity());

		// unmarshal arguments
		Bundle args = getArguments();
		assert args != null;

		// type
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_ALL);

		// pointer
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		Log.d(WebFragment.TAG, "ARG query=" + pointer);

		// hint
		final String posString = args.getString(ProviderArgs.ARG_HINTPOS);
		final Character pos = posString == null ? null : posString.charAt(0);

		// text
		final String data = args.getString(ProviderArgs.ARG_QUERYSTRING);
		Log.d(WebFragment.TAG, "ARG data=" + data);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<String>()
		{
			@Override
			public Loader<String> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				return new DocumentStringLoader(getActivity())
				{
					@SuppressWarnings({"boxing"})
					@Override
					protected String getDoc()
					{
						DataSource dataSource = null;
						try
						{
							// data source
							dataSource = new DataSource(StorageSettings.getDatabasePath(getContext()));
							final SQLiteDatabase db = dataSource.getConnection();
							WordNetImplementation.init(db);

							// dom documents
							Document wnDomDoc = null;
							Document vnDomDoc = null;
							Document pbDomDoc = null;
							Document fnDomDoc = null;
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

								if (Settings.Source.VERBNET.test(sources))
								{
									vnDomDoc = new VerbNetImplementation().querySelectorDoc(db, data);
								}

								if (Settings.Source.PROPBANK.test(sources))
								{
									pbDomDoc = new PropBankImplementation().querySelectorDoc(db, data);
								}

								if (Settings.Source.FRAMENET.test(sources))
								{
									fnDomDoc = new FrameNetImplementation().querySelectorDoc(db, data, null);
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
												final Long xClassId = xPointer.getXClassId();
												// final Long xMemberId = xpointer.getXMemberId();
												final Long wordId = xPointer.getWordId();
												final Long synsetId = xPointer.getSynsetId();
												if (xSources.contains("wn")) //
												{
													wnDomDoc = new WordNetImplementation().querySenseDoc(db, wordId, synsetId);
												}
												if (xSources.contains("vn")) //
												{
													vnDomDoc = new VerbNetImplementation().queryClassDoc(db, xClassId, pos);
												}
												if (xSources.contains("pb")) //
												{
													pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, xClassId, pos);
												}
												if (xSources.contains("fn")) //
												{
													fnDomDoc = new FrameNetImplementation().queryFrameDoc(db, xClassId, pos);
												}
											}
											else
											{
												final SensePointer sense2Pointer = (SensePointer) pointer;
												final Long wordId = sense2Pointer.getWordId();
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
												if (Settings.Source.FRAMENET.test(sources))
												{
													fnDomDoc = new FrameNetImplementation().queryDoc(db, wordId, pos);
												}
												if (Settings.Source.BNC.test(sources))
												{
													bncDomDoc = new BncImplementation().queryDoc(db, wordId, pos);
												}
											}
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_SYNSET:
										@SuppressWarnings("TypeMayBeWeakened") final SynsetPointer synsetPointer = (SynsetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG synset=" + synsetPointer);
										if (synsetPointer != null && Settings.Source.WORDNET.test(sources))
										{
											wnDomDoc = new WordNetImplementation().querySynsetDoc(db, synsetPointer.getSynsetId());
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_VNCLASS:
										final VnClassPointer vnclassPointer = (VnClassPointer) pointer;
										Log.d(WebFragment.TAG, "ARG vnclass=" + vnclassPointer);
										if (vnclassPointer != null && Settings.Source.VERBNET.test(sources))
										{
											vnDomDoc = new VerbNetImplementation().queryClassDoc(db, vnclassPointer.getId(), null);
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_PBROLESET:
										final PbRoleSetPointer pbroleSetPointer = (PbRoleSetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnframe=" + pbroleSetPointer);
										if (pbroleSetPointer != null && Settings.Source.PROPBANK.test(sources))
										{
											pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, pbroleSetPointer.getId(), null);
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT:
										final FnLexUnitPointer lexunitPointer = (FnLexUnitPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnlexunit=" + lexunitPointer);
										if (lexunitPointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryLexUnitDoc(db, lexunitPointer.getId());
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_FNFRAME:
										final FnFramePointer framePointer = (FnFramePointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnframe=" + framePointer);
										if (framePointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryFrameDoc(db, framePointer.getId(), null);
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_FNSENTENCE:
										final FnSentencePointer sentencePointer = (FnSentencePointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnsentence=" + sentencePointer);
										if (sentencePointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().querySentenceDoc(db, sentencePointer.getId());
										}
										break;

									case ProviderArgs.ARG_QUERYTYPE_FNANNOSET:
										final FnAnnoSetPointer annoSetPointer = (FnAnnoSetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnannoset=" + annoSetPointer);
										if (annoSetPointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryAnnoSetDoc(db, annoSetPointer.getId());
										}
										break;
								}
							}

							// stringify
							return WebFragment.docsToString(data, xml, isSelector, wnDomDoc, vnDomDoc, pbDomDoc, fnDomDoc, bncDomDoc);
						}
						catch (final Exception e)
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
				};
			}

			@Override
			public void onLoadFinished(final Loader<String> loader, final String doc)
			{
				Log.d(WebFragment.TAG, "onLoadFinished");
				final String mimeType = xml ? "text/xml" : "text/html";
				final String baseUrl = "file:///android_asset/";
				final String historyUrl = null;
				WebFragment.this.webview.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", historyUrl);
			}

			@Override
			public void onLoaderReset(final Loader<String> loader)
			{
				WebFragment.this.webview.loadUrl("_about:blank");
			}
		});
	}
}
