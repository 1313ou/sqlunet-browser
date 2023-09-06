/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet;

import androidx.annotation.Nullable;

/**
 * Has extended-id interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface HasXId
{
	/**
	 * Get extended class id (class=vn:class id,pb:rolesetid,fn:frame id)
	 *
	 * @return extended class id (class=vn:class,pb:rolesetid,fn:frame)
	 */
	@Nullable
	Long getXId();

	/**
	 * Get extended class id (class=vn:class id,pb:roleset id,fn:frame id)
	 *
	 * @return extended class id (class=vn:class id,pb:roleset id,fn:frame id)
	 */
	@Nullable
	Long getXClassId();

	/**
	 * Get extended member id (fn:lexunit)
	 *
	 * @return extended member id (fn:lexunit)
	 */
	@Nullable
	Long getXMemberId();

	/**
	 * Get extended sources
	 *
	 * @return extended sources
	 */
	@Nullable
	String getXSources();
}
