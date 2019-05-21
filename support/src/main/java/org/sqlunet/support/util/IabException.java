/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.support.util;

import androidx.annotation.NonNull;

/**
 * Exception thrown when something went wrong with in-app billing.
 * An IabException has an associated IabResult (an error).
 * To get the IAB result that caused this exception to be thrown,
 * call {@link #getResult()}.
 */
class IabException extends Exception
{
	@NonNull
	private final IabResult mResult;

	private IabException(@NonNull IabResult r)
	{
		this(r, null);
	}

	public IabException(int response, String message)
	{
		this(new IabResult(response, message));
	}

	private IabException(@NonNull IabResult r, Exception cause)
	{
		super(r.getMessage(), cause);
		mResult = r;
	}

	public IabException(int response, String message, Exception cause)
	{
		this(new IabResult(response, message), cause);
	}

	/**
	 * Returns the IAB result (error) that this exception signals.
	 */
	@NonNull
	public IabResult getResult()
	{
		return mResult;
	}
}