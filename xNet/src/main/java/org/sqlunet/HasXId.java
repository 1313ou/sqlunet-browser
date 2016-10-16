package org.sqlunet;

/**
 * Has Extended Id interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public interface HasXId
{
	Long getXclassid();

	Long getXinstanceid();

	String getXsources();
}
