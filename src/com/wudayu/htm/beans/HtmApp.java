package com.wudayu.htm.beans;

import android.graphics.drawable.Drawable;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 2, 2013 5:56:55 PM
 * @Description: This is David Wu's property.
 *
 **/

/**
 * This class used to be app info keeper
 * @author David
 *
 */
public class HtmApp extends HtmBaseApp {

	/*
	 * constructors for app object
	 */
	public HtmApp() {
		super();
	}
	public HtmApp(int id, String name, String pkgName, Drawable icon, int size) {
		super(id, name, pkgName, icon, size);
	}

	/*
	 * toString() method for app
	 * used to print the information for testing
	 */
	@Override
	public String toString() {
		return "HtmApp [id=" + id + ", name=" + name + ", pkgName=" + pkgName
				+ ", icon=" + icon + ", size=" + size + "]";
	}

}
