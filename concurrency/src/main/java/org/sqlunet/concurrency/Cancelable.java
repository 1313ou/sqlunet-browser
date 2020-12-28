package org.sqlunet.concurrency;

public interface Cancelable
{
	boolean cancel(boolean mayInterruptIfRunning);
}
