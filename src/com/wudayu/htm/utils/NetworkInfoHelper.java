package com.wudayu.htm.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.wudayu.htm.R;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 15, 2013 9:27:07 AM
 * @Description: This is David Wu's property.
 * 
 **/

public class NetworkInfoHelper {

	public static String getSimStateNameBySimState(Context context, int simState) {

		switch (simState) {
		case TelephonyManager.SIM_STATE_ABSENT:
			return context.getString(R.string.state_simstate_absent_activity_networking);
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			return context.getString(R.string.state_simstate_networklocked_activity_networking);
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			return context.getString(R.string.state_simstate_pinrequired_activity_networking);
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			return context.getString(R.string.state_simstate_pukrequired_activity_networking);
		case TelephonyManager.SIM_STATE_READY:
			return context.getString(R.string.state_simstate_ready_activity_networking);
		case TelephonyManager.SIM_STATE_UNKNOWN:
			return context.getString(R.string.state_simstate_unknown_activity_networking);
		default:
			return null;
		}

	}

	public static String getPhoneTypeNameByPhoneType(Context context, int phoneType) {

		switch (phoneType) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			return context
					.getString(R.string.state_phonetype_cdma_activity_networking);
		case TelephonyManager.PHONE_TYPE_GSM:
			return context
					.getString(R.string.state_phonetype_gsm_activity_networking);
		case TelephonyManager.PHONE_TYPE_NONE:
			return context
					.getString(R.string.state_phonetype_none_activity_networking);
		case TelephonyManager.PHONE_TYPE_SIP:
			return context
					.getString(R.string.state_phonetype_sip_activity_networking);
		default:
			return null;
		}

	}

	public static String getNetworkTypeNameByNetworkType(Context context,
			int networkType) {

		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return context
					.getString(R.string.state_networktype_1xrtt_activity_networking);
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return context
					.getString(R.string.state_networktype_cdma_activity_networking);
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return context
					.getString(R.string.state_networktype_edge_activity_networking);
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return context
					.getString(R.string.state_networktype_ehrpd_activity_networking);
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return context
					.getString(R.string.state_networktype_evdo0_activity_networking);
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return context
					.getString(R.string.state_networktype_evdoa_activity_networking);
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return context
					.getString(R.string.state_networktype_evdob_activity_networking);
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return context
					.getString(R.string.state_networktype_gprs_activity_networking);
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return context
					.getString(R.string.state_networktype_hsdpa_activity_networking);
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return context
					.getString(R.string.state_networktype_hspa_activity_networking);
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return context
					.getString(R.string.state_networktype_hspap_activity_networking);
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return context
					.getString(R.string.state_networktype_hsupa_activity_networking);
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return context
					.getString(R.string.state_networktype_iden_activity_networking);
		case TelephonyManager.NETWORK_TYPE_LTE:
			return context
					.getString(R.string.state_networktype_lte_activity_networking);
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return context
					.getString(R.string.state_networktype_umts_activity_networking);
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return context
					.getString(R.string.state_networktype_unknown_activity_networking);
		default:
			return null;
		}

	}

	public static String getWifiStateNameByWifiState(Context context, int wifiType) {

		switch (wifiType) {

		case WifiManager.WIFI_STATE_DISABLED:
			return context
					.getString(R.string.state_wifistate_disabled_activity_networking);
		case WifiManager.WIFI_STATE_DISABLING:
			return context
					.getString(R.string.state_wifistate_disabling_activity_networking);
		case WifiManager.WIFI_STATE_ENABLED:
			return context
					.getString(R.string.state_wifistate_enabled_activity_networking);
		case WifiManager.WIFI_STATE_ENABLING:
			return context
					.getString(R.string.state_wifistate_enabling_activity_networking);
		case WifiManager.WIFI_STATE_UNKNOWN:
			return context
					.getString(R.string.state_wifistate_unknown_activity_networking);
		default:
			return null;
		}

	}

	public static String getCurrentIpAddress() throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf
					.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()
						&& (true | InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()))) {
					return inetAddress.getHostAddress().toString();
				}
			}
		}

		return null;
	}

}
