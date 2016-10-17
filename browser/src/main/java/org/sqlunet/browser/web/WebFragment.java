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

import org.sqlunet.bnc.sql.BncImplementation;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xselector.XPointer;
import org.sqlunet.dom.Factory;
import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.sql.FrameNetImplementation;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.sql.PropBankImplementation;
import org.sqlunet.provider.SqlUNetContract;
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
 * A fragment representing a web view.
 */
public class WebFragment extends Fragment
{
	static private final String TAG = "WebFragment"; //$NON-NLS-1$

	/**
	 * HTML stuff
	 */
	private static final String BODY1 = "<html><head>"; //$NON-NLS-1$
	private static final String BODY2 = "</head><body>"; //$NON-NLS-1$
	private static final String BODY3 = "</body></html>"; //$NON-NLS-1$
	private static final String TOP = "<DIV class='titlesection'><IMG class='titleimg' src='images/logo.png'/></DIV>"; //$NON-NLS-1$
	private static final String STYLESHEET1 = "<link rel='stylesheet' type='text/css' href='"; //$NON-NLS-1$
	private static final String STYLESHEET2 = "' />"; //$NON-NLS-1$
	private static final String SCRIPT1 = "<script type='text/javascript' src='"; //$NON-NLS-1$
	private static final String SCRIPT2 = "'></script>"; //$NON-NLS-1$
	private static final String LIST1 = "<OL style='display: block;'>"; //$NON-NLS-1$
	private static final String LIST2 = "</OL>"; //$NON-NLS-1$
	private static final String ITEM1 = "<LI class='treeitem treepanel'>"; //$NON-NLS-1$
	private static final String ITEM2 = "</LI>"; //$NON-NLS-1$
	/**
	 * WebView
	 */
	private WebView webview;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public WebFragment()
	{
		//
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
	static private String docsToString(@SuppressWarnings({"SameParameterValue", "UnusedParameters"}) final String word,  //
			final boolean xml,  //
			final boolean isSelector,  //
			final Document wnDomDoc,  //
			final Document vnDomDoc,  //
			final Document pbDomDoc,  //
			final Document fnDomDoc,  //
			final Document bncDomDoc)
	{
		String data;
		if (xml)
		{
			final Document rootDomDoc = Factory.makeDocument();
			NodeFactory.makeNode(rootDomDoc, rootDomDoc, "sqlunet", null); //$NON-NLS-1$
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

			data = XSLTransformer.docToXml(rootDomDoc);
			// Log.d(TAG, "xml=" + data);
		}
		else
		{
			final StringBuilder sb = new StringBuilder();

			// header
			sb.append(BODY1);

			// css style sheet
			sb.append(STYLESHEET1).append("css/style.css").append(STYLESHEET2); //$NON-NLS-1$
			sb.append(STYLESHEET1).append("css/tree.css").append(STYLESHEET2); //$NON-NLS-1$
			sb.append(STYLESHEET1).append("css/wordnet.css").append(STYLESHEET2); //$NON-NLS-1$
			if (vnDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/verbnet.css").append(STYLESHEET2); //$NON-NLS-1$
			}
			if (pbDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/propbank.css").append(STYLESHEET2); //$NON-NLS-1$
			}
			if (fnDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/framenet.css").append(STYLESHEET2); //$NON-NLS-1$
			}
			if (bncDomDoc != null)
			{
				sb.append(STYLESHEET1).append("css/bnc.css").append(STYLESHEET2); //$NON-NLS-1$
			}

			// javascripts
			sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2); //$NON-NLS-1$
			sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2); //$NON-NLS-1$
			sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2); //$NON-NLS-1$
			sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2); //$NON-NLS-1$
			if (vnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/verbnet.js'></script>"); //$NON-NLS-1$
			}
			if (pbDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/propbank.js").append(SCRIPT2); //$NON-NLS-1$
			}
			if (fnDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/framenet.js").append(SCRIPT2); //$NON-NLS-1$
			}
			if (bncDomDoc != null)
			{
				sb.append(SCRIPT1).append("js/bnc.js").append(SCRIPT2); //$NON-NLS-1$
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
				sb.append(XSLTransformer.docToHtml(wnDomDoc, Settings.Source.WORDNET, isSelector));
				sb.append(ITEM2);
			}
			if (vnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(XSLTransformer.docToHtml(vnDomDoc, Settings.Source.VERBNET, isSelector));
				sb.append(ITEM2);
			}
			if (pbDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(XSLTransformer.docToHtml(pbDomDoc, Settings.Source.PROPBANK, isSelector));
				sb.append(ITEM2);
			}
			if (fnDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(XSLTransformer.docToHtml(fnDomDoc, Settings.Source.FRAMENET, isSelector));
				sb.append(ITEM2);
			}
			if (bncDomDoc != null)
			{
				sb.append(ITEM1);
				sb.append(XSLTransformer.docToHtml(bncDomDoc, Settings.Source.BNC, isSelector));
				sb.append(ITEM2);
			}
			sb.append(LIST2);

			// tail
			sb.append(BODY3);

			data = sb.toString();
		}
		// System.out.println(data);
		return data;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_web, container, false);

		// webview
		this.webview = (WebView) rootView.findViewById(R.id.webView);

		// load view
		load();

		return rootView;
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
				Log.d(WebFragment.TAG, "Uri " + uri); //$NON-NLS-1$
				try
				{
					final String query = URLDecoder.decode(uri.getQuery(), "UTF-8"); //$NON-NLS-1$
					final String[] target = query.split("="); //$NON-NLS-1$
					final String type = target[0]; // $NON-NLS-1$
					final String data = target[1]; // $NON-NLS-1$
					Log.d(WebFragment.TAG, "QUERY " + query + " type=" + type + " data=" + data); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					final Intent searchIntent = new Intent(getActivity(), WebActivity.class);
					if ("word".equals(type)) //$NON-NLS-1$
					{
						searchIntent.putExtra(SqlUNetContract.ARG_QUERYSTRING, data);
					}
					else
					{
						final long id = Long.valueOf(data);

						// toast with id
						final Activity activity = WebFragment.this.getActivity();
						activity.runOnUiThread(new Runnable()
						{
							public void run()
							{
								Toast.makeText(activity, "id=" + id, Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
							}
						});

						// prepare data
						int action = 0;
						Parcelable pointer = null;

						if ("synsetid".equals(type)) //$NON-NLS-1$
						{
							action = SqlUNetContract.ARG_QUERYACTION_SYNSET;
							final SynsetPointer xpointer = new SynsetPointer();
							xpointer.setSynset(id, null);
							pointer = xpointer;
						}
						else if ("vnclassid".equals(type)) //$NON-NLS-1$
						{
							action = SqlUNetContract.ARG_QUERYACTION_VNCLASS;
							final VnClassPointer xpointer = new VnClassPointer();
							xpointer.classid = id;
							pointer = xpointer;
						}
						else if ("fnframeid".equals(type)) //$NON-NLS-1$
						{
							action = SqlUNetContract.ARG_QUERYACTION_PBROLESET;
							final PbRoleSetPointer xpointer = new PbRoleSetPointer();
							xpointer.rolesetid = id;
							pointer = xpointer;
						}
						else if ("fnluid".equals(type)) //$NON-NLS-1$
						{
							action = SqlUNetContract.ARG_QUERYACTION_FNLEXUNIT;
							final FnLexUnitPointer xpointer = new FnLexUnitPointer();
							xpointer.luid = id;
							pointer = xpointer;
						}

						searchIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, action);
						searchIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, pointer);
					}
					startActivity(searchIntent);
					return true;
				}
				catch (final Exception e)
				{
					Log.e(WebFragment.TAG, "URL loading ", e); //$NON-NLS-1$
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
		if (args == null)
		{
			args = getActivity().getIntent().getExtras();
		}

		// pointer
		final Parcelable pointer = args.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
		Log.d(WebFragment.TAG, "ARG query=" + pointer); //$NON-NLS-1$

		// text
		final String data = args.getString(SqlUNetContract.ARG_QUERYSTRING);
		Log.d(WebFragment.TAG, "ARG data=" + data); //$NON-NLS-1$

		// action
		final int action = args.getInt(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_ALL);

		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<String>()
		{
			/*
			 * (non-Javadoc)
			 *
			 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
			 */
			@Override
			public Loader<String> onCreateLoader(final int loaderId0, final Bundle args0)
			{
				return new DocumentStringLoader(getActivity())
				{
					/*
					 * (non-Javadoc)
					 *
					 * @see org.sqlunet.DocumentLoader#getDoc(android.os.CancellationSignal)
					 */
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
								switch (action)
								{
									case SqlUNetContract.ARG_QUERYACTION_ALL:
										if (pointer != null)
										{
											if (pointer instanceof XPointer)
											{
												final XPointer xpointer = (XPointer) pointer;
												final String xSources = xpointer.getXsources();
												final Long xclassId = xpointer.getXclassid();
												// final Long xinstanceId = xpointer.getXinstanceid();
												final Long wordId = xpointer.getWordId();
												final Long synsetId = xpointer.getSynsetId();
												final Character pos = xpointer.getPos();
												if (xSources.contains("wn")) //$NON-NLS-1$
												{
													wnDomDoc = new WordNetImplementation().querySenseDoc(db, wordId, synsetId);
												}
												if (xSources.contains("vn")) //$NON-NLS-1$
												{
													vnDomDoc = new VerbNetImplementation().queryClassDoc(db, xclassId, pos);
												}
												if (xSources.contains("pb")) //$NON-NLS-1$
												{
													pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, xclassId, pos);
												}
												if (xSources.contains("fn")) //$NON-NLS-1$
												{
													fnDomDoc = new FrameNetImplementation().queryFrameDoc(db, xclassId, pos);
												}
											}
											else
											{
												final SensePointer sensePointer = (SensePointer) pointer;
												final Long wordId = sensePointer.getWordId();
												final Long synsetId = sensePointer.getSynsetId();
												final Character pos = sensePointer.getPos();
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

									case SqlUNetContract.ARG_QUERYACTION_SYNSET:
										@SuppressWarnings("TypeMayBeWeakened") final SynsetPointer synsetPointer = (SynsetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG synset=" + synsetPointer); //$NON-NLS-1$
										if (synsetPointer != null && Settings.Source.WORDNET.test(sources))
										{
											wnDomDoc = new WordNetImplementation().querySynsetDoc(db, synsetPointer.getSynsetId());
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_VNCLASS:
										final VnClassPointer vnclassPointer = (VnClassPointer) pointer;
										Log.d(WebFragment.TAG, "ARG vnclass=" + vnclassPointer); //$NON-NLS-1$
										if (vnclassPointer != null && Settings.Source.VERBNET.test(sources))
										{
											vnDomDoc = new VerbNetImplementation().queryClassDoc(db, vnclassPointer.getClassId(), null);
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_PBROLESET:
										final PbRoleSetPointer pbrolesetPointer = (PbRoleSetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnframe=" + pbrolesetPointer); //$NON-NLS-1$
										if (pbrolesetPointer != null && Settings.Source.PROPBANK.test(sources))
										{
											pbDomDoc = new PropBankImplementation().queryRoleSetDoc(db, pbrolesetPointer.getRoleSetId(), null);
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_FNLEXUNIT:
										final FnLexUnitPointer lexunitPointer = (FnLexUnitPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnlexunit=" + lexunitPointer); //$NON-NLS-1$
										if (lexunitPointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryLexUnitDoc(db, lexunitPointer.luid);
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_FNFRAME:
										final FnFramePointer framePointer = (FnFramePointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnframe=" + framePointer); //$NON-NLS-1$
										if (framePointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryFrameDoc(db, framePointer.frameid, null);
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_FNSENTENCE:
										final FnSentencePointer sentencePointer = (FnSentencePointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnsentence=" + sentencePointer); //$NON-NLS-1$
										if (sentencePointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().querySentenceDoc(db, sentencePointer.getSentenceId());
										}
										break;

									case SqlUNetContract.ARG_QUERYACTION_FNANNOSET:
										final FnAnnoSetPointer annosetPointer = (FnAnnoSetPointer) pointer;
										Log.d(WebFragment.TAG, "ARG fnannoset=" + annosetPointer); //$NON-NLS-1$
										if (annosetPointer != null && Settings.Source.FRAMENET.test(sources))
										{
											fnDomDoc = new FrameNetImplementation().queryAnnoSetDoc(db, annosetPointer.annosetid);
										}
										break;
								}
							}

							// stringify
							return WebFragment.docsToString(data, xml, isSelector, wnDomDoc, vnDomDoc, pbDomDoc, fnDomDoc, bncDomDoc);
						}
						catch (final Exception e)
						{
							Log.e(WebFragment.TAG, "getDoc", e); //$NON-NLS-1$
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
				Log.d(WebFragment.TAG, "onLoadFinished"); //$NON-NLS-1$
				final String mimeType = xml ? "text/xml" : "text/html"; //$NON-NLS-1$ //$NON-NLS-2$
				final String baseUrl = "file:///android_asset/"; //$NON-NLS-1$
				final String historyUrl = null;
				WebFragment.this.webview.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", historyUrl); //$NON-NLS-1$
			}

			@Override
			public void onLoaderReset(final Loader<String> arg0)
			{
				WebFragment.this.webview.loadUrl("about:blank"); //$NON-NLS-1$
			}
		});
	}
}
