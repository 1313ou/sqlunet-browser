/*
 * Copyright (c) 2023. Bernard Bou
 */

package com.bbou.concurrency;

@FunctionalInterface
public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
