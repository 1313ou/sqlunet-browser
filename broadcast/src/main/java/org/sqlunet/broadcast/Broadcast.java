/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.broadcast;

public interface Broadcast
{
	enum RequestType
	{
		NEW, KILL
	}

	String BROADCAST_ACTION = "org.grammmarscope.provider.ACTION";

	String BROADCAST_ACTION_REQUEST = "org.grammmarscope.provider.REQUEST";

	enum EventType
	{
		CONNECTED, CONNECTED_FAILURE, DISCONNECTED, BOUND, BOUND_FAILURE, UNBOUND, EMBEDDED_LOADED, EMBEDDED_LOADED_FAILURE, EMBEDDED_UNLOADED, LOADED, LOADED_FAILURE, UNLOADED
	}

	String BROADCAST_LISTEN = "org.grammmarscope.provider.CHANGE";

	String BROADCAST_LISTEN_EVENT = "org.grammmarscope.provider.EVENT";
}
