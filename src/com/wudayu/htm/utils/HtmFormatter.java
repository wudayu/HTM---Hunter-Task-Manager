package com.wudayu.htm.utils;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 2, 2013 9:24:48 PM
 * @Description: This is David Wu's property.
 *
 **/

/**
 * This class is used to deal with the format thing
 * @author David
 *
 */
public final class HtmFormatter {

	public static final int BASE_NUM = 10;

	public static final int BASE_POINT_COUNT = 13;

	/**
	 * sizeKbToString is used for in case the size is larger than 1024
	 * @param size original size in kb
	 * @return the right size in right unit
	 */
	public static String sizeKbToString(int size) {
		if (size < 1024)
			return String.valueOf(size) + "KB";

		return String.format("%.2f", size / 1024.0) + "MB";
	}

}
