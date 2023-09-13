/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.concurrency;

@FunctionalInterface
public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
