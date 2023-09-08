/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.broadcast;

import androidx.annotation.Nullable;

public interface IProvider<R>
{
	@Nullable
	R process(String... args);

	void kill();

	int STATUS_BOUND = 0x00000001;
	int STATUS_LOADED = 0x00000002;
	int STATUS_EMBEDDED = 0x10000000;

	int getStatus();
}
