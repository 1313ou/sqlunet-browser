/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.web;

import android.util.Log;

import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;

import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * XSL Transformer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class DocumentTransformer
{
	static private final String TAG = "DocumentTransformer";

	/**
	 * Transform Document to HTML
	 *
	 * @param doc        doc
	 * @param isSelector is selector source
	 * @return html
	 */
	@NonNull
	public String docToHtml(final Document doc, final String source, final boolean isSelector)
	{
		// Log.d(TAG, "to be transformed:" + DomTransformer.docToXml(doc));
		try
		{
			return DomTransformer.docToString(doc, getXSLStream(source, isSelector), "html");
		}
		catch (@NonNull final Exception e)
		{
			Log.e(TAG, "While transforming doc to HTML", e);
			return "error " + e;
		}
	}

	/**
	 * Get XSL file
	 *
	 * @param from       type of source
	 * @param isSelector is selector source
	 * @return XSL inputstream
	 */
	@Nullable
	abstract protected InputStream getXSLStream(final String from, final boolean isSelector);
}
