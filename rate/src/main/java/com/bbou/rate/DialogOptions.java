/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class DialogOptions
{
	private boolean showNeutralButton = true;

	private boolean showNegativeButton = true;

	private boolean showTitle = true;

	private boolean cancelable = false;

	private StoreType storeType = StoreType.GOOGLE;

	private int titleResId = R.string.rate_dialog_title;

	private int messageResId = R.string.rate_dialog_message;

	private int textPositiveResId = R.string.rate_dialog_ok;

	private int textNeutralResId = R.string.rate_dialog_cancel;

	private int textNegativeResId = R.string.rate_dialog_no;

	@Nullable
	private String titleText = null;

	@Nullable
	private String messageText = null;

	@Nullable
	private String positiveText = null;

	@Nullable
	private String neutralText = null;

	@Nullable
	private String negativeText = null;

	private View view;

	public boolean shouldShowNeutralButton()
	{
		return showNeutralButton;
	}

	public void setShowNeutralButton(boolean showNeutralButton)
	{
		this.showNeutralButton = showNeutralButton;
	}

	public boolean shouldShowNegativeButton()
	{
		return showNegativeButton;
	}

	public void setShowNegativeButton(boolean showNegativeButton)
	{
		this.showNegativeButton = showNegativeButton;
	}

	public boolean shouldShowTitle()
	{
		return showTitle;
	}

	public void setShowTitle(boolean showTitle)
	{
		this.showTitle = showTitle;
	}

	public boolean getCancelable()
	{
		return cancelable;
	}

	public void setCancelable(boolean cancelable)
	{
		this.cancelable = cancelable;
	}

	public StoreType getStoreType()
	{
		return storeType;
	}

	public void setStoreType(StoreType appstore)
	{
		storeType = appstore;
	}

	public int getTitleResId()
	{
		return titleResId;
	}

	public void setTitleResId(int titleResId)
	{
		this.titleResId = titleResId;
	}

	public int getMessageResId()
	{
		return messageResId;
	}

	public void setMessageResId(int messageResId)
	{
		this.messageResId = messageResId;
	}

	public int getTextPositiveResId()
	{
		return textPositiveResId;
	}

	public void setTextPositiveResId(int textPositiveResId)
	{
		this.textPositiveResId = textPositiveResId;
	}

	public int getTextNeutralResId()
	{
		return textNeutralResId;
	}

	public void setTextNeutralResId(int textNeutralResId)
	{
		this.textNeutralResId = textNeutralResId;
	}

	public int getTextNegativeResId()
	{
		return textNegativeResId;
	}

	public void setTextNegativeResId(int textNegativeResId)
	{
		this.textNegativeResId = textNegativeResId;
	}

	public View getView()
	{
		return view;
	}

	public void setView(View view)
	{
		this.view = view;
	}

	@NonNull
	public String getTitleText(@NonNull Context context)
	{
		if (titleText == null)
		{
			return context.getString(titleResId);
		}
		return titleText;
	}

	public void setTitleText(@Nullable String titleText)
	{
		this.titleText = titleText;
	}

	@NonNull
	public String getMessageText(@NonNull Context context)
	{
		if (messageText == null)
		{
			return context.getString(messageResId);
		}
		return messageText;
	}

	public void setMessageText(@Nullable String messageText)
	{
		this.messageText = messageText;
	}

	@NonNull
	public String getPositiveText(@NonNull Context context)
	{
		if (positiveText == null)
		{
			return context.getString(textPositiveResId);
		}
		return positiveText;
	}

	public void setPositiveText(@Nullable String positiveText)
	{
		this.positiveText = positiveText;
	}

	@NonNull
	public String getNeutralText(@NonNull Context context)
	{
		if (neutralText == null)
		{
			return context.getString(textNeutralResId);
		}
		return neutralText;
	}

	public void setNeutralText(@Nullable String neutralText)
	{
		this.neutralText = neutralText;
	}

	@NonNull
	public String getNegativeText(@NonNull Context context)
	{
		if (negativeText == null)
		{
			return context.getString(textNegativeResId);
		}
		return negativeText;
	}

	public void setNegativeText(@Nullable String negativeText)
	{
		this.negativeText = negativeText;
	}
}
