package com.wudayu.htm.junit;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;
import android.text.format.Formatter;

import com.wudayu.htm.utils.NetworkInfoHelper;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 4, 2013 10:46:50 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class TestHaha extends AndroidTestCase {

	@SuppressWarnings("deprecation")
	public void testHaha() throws UnknownHostException, SocketException {

		System.out.println("getLocalIpAddress = "
				+ NetworkInfoHelper.getCurrentIpAddress());

		TelephonyManager telManager = (TelephonyManager) this.getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		System.out.println("Sim Card State---------getSimState() = "
				+ NetworkInfoHelper.getSimStateNameBySimState(this.getContext(), telManager.getSimState()));
		System.out.println("Operator----getNetworkOperatorName() = "
				+ telManager.getNetworkOperatorName());
		System.out.println("PhoneNumber---------getLine1Number() = "
				+ telManager.getLine1Number());
		System.out.println("Country Code--getNetworkCountryIso() = "
				+ telManager.getNetworkCountryIso());
		System.out.println("Phone Type------------getPhoneType() = "
				+ NetworkInfoHelper.getPhoneTypeNameByPhoneType(this.getContext(), telManager.getPhoneType()));
		System.out.println("Network Type--------getNetworkType() = "
				+ NetworkInfoHelper.getNetworkTypeNameByNetworkType(this.getContext(), telManager.getNetworkType()));// switch case
		System.out.println("DeviceID(IMEI)---------getDeviceId() = "
				+ telManager.getDeviceId());
		System.out.println("SubscriberID(IMSI)-getSubscriberId() = "
				+ telManager.getSubscriberId());

		WifiManager wifiManager = (WifiManager) this.getContext()
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		ScanResult scanResult = wifiManager.getScanResults().get(0);

		// wifiManager.getWifiState(); need judge

		System.out.println("getWifiState  = " + NetworkInfoHelper.getWifiStateNameByWifiState(this.getContext(), wifiManager.getWifiState()));// switch
																			// case

		System.out.println("getMacAddress = " + wifiInfo.getMacAddress());
		System.out.println("getSSID       = " + wifiInfo.getSSID());
		System.out.println("getBSSID      = " + wifiInfo.getBSSID());
		System.out.println("getLinkSpeed  = " + wifiInfo.getLinkSpeed()
				+ " Mbps");
		System.out.println("getIpAddress  = "
				+ Formatter.formatIpAddress(wifiInfo.getIpAddress()));
		System.out.println("netmask       = "
				+ Formatter.formatIpAddress(dhcpInfo.netmask));
		System.out.println("gateway       = "
				+ Formatter.formatIpAddress(dhcpInfo.gateway));
		System.out.println("dns1          = "
				+ Formatter.formatIpAddress(dhcpInfo.dns1));
		System.out.println("dns2          = "
				+ Formatter.formatIpAddress(dhcpInfo.dns2));
		System.out.println("capabilities  = " + scanResult.capabilities);
		System.out.println("frequency     = " + scanResult.frequency + " MHz");
	}

}
