package org.sqlunet.concurrency;

@FunctionalInterface
public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
