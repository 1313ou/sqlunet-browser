/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.support.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents the result of an in-app billing operation.
 * A result is composed of a response code (an integer) and possibly a
 * message (String). You can get those by calling
 * {@link #getResponse} and {@link #getMessage()}, respectively. You
 * can also inquire whether a result is a success or a failure by
 * calling {@link #isSuccess()} and {@link #isFailure()}.
 */
public class IabResult
{
	private final int mResponse;
	private final String mMessage;

	public IabResult(int response, @Nullable String message)
	{
		mResponse = response;
		if (message == null || message.trim().length() == 0)
		{
			mMessage = IabHelper.getResponseDesc(response);
		}
		else
		{
			mMessage = message + " (response: " + IabHelper.getResponseDesc(response) + ")";
		}
	}

	@SuppressWarnings("unused")
	public int getResponse()
	{
		return mResponse;
	}

	public String getMessage()
	{
		return mMessage;
	}

	public boolean isSuccess()
	{
		return mResponse == IabHelper.BILLING_RESPONSE_RESULT_OK;
	}

	public boolean isFailure()
	{
		return !isSuccess();
	}

	@NonNull
	public String toString()
	{
		return "IabResult: " + getMessage();
	}
}

