package com.wudayu.htm.beans;

import android.graphics.drawable.Drawable;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 4, 2013 10:02:50 PM
 * @Description: This is David Wu's property.
 *
 **/

/**
 * This class used to be sysapp info keeper
 * @author David
 *
 */
public class HtmSysapp extends HtmBaseApp {

	/*
	 * constructors for sysapp object
	 */
	public HtmSysapp() {
		super();
		// TODO Auto-generated constructor stub
	}
	public HtmSysapp(int id, String name, String pkgName, Drawable icon,
			int size) {
		super(id, name, pkgName, icon, size);
	}

	/*
	 * toString() method for sysapp
	 * used to print the information for testing
	 */
	@Override
	public String toString() {
		return "HtmSysapp [id=" + id + ", name=" + name + ", pkgName="
				+ pkgName + ", icon=" + icon + ", size=" + size + "]";
	}

}
