package com.wudayu.htm.beans;

import android.graphics.drawable.Drawable;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 5, 2013 9:58:49 AM
 * @Description: This is David Wu's property.
 *
 **/

/**
 * This class used to be base app info keeper
 * @author David
 *
 */
public class HtmBaseApp {

	/*
	 * pid of each base app
	 */
	protected int id;
	/*
	 * base app name
	 */
	protected String name;
	/*
	 * base app package name
	 */
	protected String pkgName;
	/*
	 * base app icon
	 */
	protected Drawable icon;
	/*
	 * base app memory size
	 */
	protected int size;

	/*
	 * getter() and setter() for base app
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	/*
	 * constructors for base app object
	 */
	public HtmBaseApp() {
		super();
		this.id = -1;
		this.name = null;
		this.pkgName = null;
		this.icon = null;
		this.size = -1;
	}
	public HtmBaseApp(int id, String name, String pkgName, Drawable icon,
			int size) {
		super();
		this.id = id;
		this.name = name;
		this.pkgName = pkgName;
		this.icon = icon;
		this.size = size;
	}

	/*
	 * toString() method for base app
	 * used to print the information for testing
	 */
	@Override
	public String toString() {
		return "HtmBaseApp [id=" + id + ", name=" + name + ", pkgName="
				+ pkgName + ", icon=" + icon + ", size=" + size + "]";
	}

}
