package com.wudayu.htm.beans;

import android.content.Intent;
import android.graphics.drawable.Drawable;


/**
 *
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 5, 2013 10:06:27 AM
 * @Description: This is David Wu's property.
 *
 **/

/**
 * This class used to be service info keeper
 * @author David
 *
 */
public class HtmService extends HtmBaseApp {

	/*
	 * service name of each service
	 */
	private String serviceName;

	/*
	 * short service name of each service
	 */
	private String shortServiceName;

	/*
	 * service intent of each service
	 */
	private Intent serviceIntent;

	/*
	 * process name of the service
	 */
	private String processName;

	/*
	 * getter() and setter()
	 */
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Intent getServiceIntent() {
		return serviceIntent;
	}

	public void setServiceIntent(Intent serviceIntent) {
		this.serviceIntent = serviceIntent;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getShortServiceName() {
		return shortServiceName;
	}

	public void setShortServiceName(String shortServiceName) {
		this.shortServiceName = shortServiceName;
	}

	/*
	 * constructors for service object
	 */
	public HtmService() {
		super();
		this.serviceName = null;
		this.shortServiceName = null;
		this.serviceIntent = null;
		this.processName = null;
	}
	public HtmService(int id, String name, String pkgName, Drawable icon,
			int size, String serviceName, String shortServiceName, Intent serviceIntent,
			String processName) {
		super(id, name, pkgName, icon, size);

		this.serviceName = serviceName;
		this.shortServiceName = shortServiceName;
		this.serviceIntent = serviceIntent;
		this.processName = processName;
	}

	/*
	 * toString() method for service
	 * used to print the information for testing
	 */
	@Override
	public String toString() {
		return "HtmService [serviceName=" + serviceName + ", shortServiceName="
				+ shortServiceName + ", serviceIntent=" + serviceIntent
				+ ", processName=" + processName + "]";
	}

}
