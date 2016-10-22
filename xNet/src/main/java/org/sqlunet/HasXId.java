package org.sqlunet;

/**
 * Has extended-id interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface HasXId
{
	/**
	 * Get extended class id (class=vn:class,pb:rolesetid,fn:frame)
	 *
	 * @return extended class id (class=vn:class,pb:rolesetid,fn:frame)
	 */
	Long getXClassId();

	/**
	 * Get extended instance id (instance=vn:role,pb:role,fn:frame element)
	 *
	 * @return extended instance id (instance=vn:role,pb:role,fn:frame element)
	 */
	Long getXInstanceId();

	/**
	 * Get extended sources
	 *
	 * @return extended sources
	 */
	String getXSources();
}
