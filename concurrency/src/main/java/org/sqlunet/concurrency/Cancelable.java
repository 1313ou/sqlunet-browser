/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.concurrency;

@FunctionalInterface
public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
