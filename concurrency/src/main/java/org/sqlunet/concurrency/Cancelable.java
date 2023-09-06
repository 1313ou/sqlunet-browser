/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.concurrency;

@FunctionalInterface
public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
