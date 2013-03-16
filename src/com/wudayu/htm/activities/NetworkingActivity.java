package com.wudayu.htm.activities;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wudayu.htm.R;
import com.wudayu.htm.adapters.NetworkInfoAdapter;
import com.wudayu.htm.utils.NetworkInfoHelper;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 30, 2013 21:06:15 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class NetworkingActivity extends Activity {

	// public static final int CountMobileInfo = 8;
	// public static final int CountWifiInfo = 12;
	// public static final int CountWifiInfoDis = 2;

	/*
	 * data for list views
	 */
	private List<String> hintsMobileInfo;
	private List<String> contentsMobileInfo;
	private List<String> hintsWifiInfo;
	private List<String> contentsWifiInfo;

	/*
	 * current ip text view
	 */
	private TextView txtCurrIp;

	/*
	 * the mobile info and wifi info list views
	 */
	private ListView lvMobileInfo;
	private ListView lvWifiInfo;

	/*
	 * triple text view buttons
	 */
	private TextView txtDataUsage;
	private TextView txtMobileSettings;
	private TextView txtWifiSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * duel with the different orientation
		 * use different layout
		 */
		if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation)
			setContentView(R.layout.portrait_activity_networking);
		else
			setContentView(R.layout.activity_networking);

		/*
		 * Initialize Variables
		 */
		hintsMobileInfo = new ArrayList<String>();
		contentsMobileInfo = new ArrayList<String>();
		hintsWifiInfo = new ArrayList<String>();
		contentsWifiInfo = new ArrayList<String>();

		/*
		 * initialize widgets
		 */
		txtCurrIp = (TextView) findViewById(R.id.txt_currentip_activity_networking);
		lvMobileInfo = (ListView) findViewById(R.id.lv_mobilephone_info_activity_networking);
		lvWifiInfo = (ListView) findViewById(R.id.lv_wifi_info_activity_networking);
		txtDataUsage = (TextView) findViewById(R.id.txt_data_usage_activity_networking);
		txtMobileSettings = (TextView) findViewById(R.id.txt_mobilephone_settings_activity_networking);
		txtWifiSettings = (TextView) findViewById(R.id.txt_wifi_settings_activity_networking);
		txtDataUsage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtMobileSettings.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtWifiSettings.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		/*
		 * set listener or settings to widgets
		 */
		txtDataUsage.setOnClickListener(new txtDataUsageOnClickListener());
		txtMobileSettings.setOnClickListener(new txtMobileSettingsOnClickListener());
		txtWifiSettings.setOnClickListener(new txtWifiSettingsOnClickListener());

		/*
		 * load data and put them on list views
		 */
		refreshPage();
	}

	/*
	 * display the page of the system wifi settings
	 */
	private class txtWifiSettingsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings$WifiSettingsActivity");
			startActivity(intent);
		}
		
	}

	/*
	 * display the page of the system mobile network settings
	 */
	private class txtMobileSettingsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings$WirelessSettingsActivity");
			startActivity(intent);
		}
		
	}

	/*
	 * display the page of the system data usage history
	 */
	private class txtDataUsageOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings$DataUsageSummaryActivity");
			startActivity(intent);
		}
		
	}

	private void refreshPage() {
		/*
		 * set text to the current ip
		 */
		refreshCurrIp();
		/*
		 * call refreshListViews
		 */
		refreshListViews();
	}

	/*
	 * get the current ip address
	 */
	private void refreshCurrIp() {
		try {
			String currIp = NetworkInfoHelper.getCurrentIpAddress();
			/*
			 * can not detected the current ip
			 * display can not detected message
			 * so does exception happens
			 */
			if (currIp == null)
				currIp = getString(R.string.str_can_not_detected_activity_networking);

			txtCurrIp
					.setText(getString(R.string.txt_currentip_activity_networking)
							+ currIp);
		} catch (SocketException e) {
			txtCurrIp
					.setText(getString(R.string.txt_currentip_activity_networking)
							+ getString(R.string.str_can_not_detected_activity_networking));
		}
	}

	private void refreshListViews() {
		/*
		 * get data
		 */
		getData();
		/*
		 * bind data into list views
		 */
		bindDataIntoListViews();
	}

	private void bindDataIntoListViews() {
		NetworkInfoAdapter mobileInfoAdapter = new NetworkInfoAdapter(this,
				hintsMobileInfo, contentsMobileInfo,
				R.layout.lvitem_info_lv_info_activity_networking);
		NetworkInfoAdapter wifiInfoAdapter = new NetworkInfoAdapter(this,
				hintsWifiInfo, contentsWifiInfo,
				R.layout.lvitem_info_lv_info_activity_networking);

		lvMobileInfo.setAdapter(mobileInfoAdapter);
		lvWifiInfo.setAdapter(wifiInfoAdapter);
	}

	/**
	 * get the data for mobile phone and wifi, then bind to the listview
	 */
	private void getData() {
		/*
		 * clear old data first
		 */
		hintsMobileInfo.clear();
		contentsMobileInfo.clear();
		hintsWifiInfo.clear();
		contentsWifiInfo.clear();

		getMobileInfoData(hintsMobileInfo, contentsMobileInfo);
		getWifiInfoData(hintsWifiInfo, contentsWifiInfo);
	}

	/**
	 * add the wifi info data
	 * 
	 * @param hints
	 *            the hints list
	 * @param contents
	 *            the contents list
	 */
	@SuppressWarnings("deprecation")
	private void getWifiInfoData(List<String> hints, List<String> contents) {
		/*
		 * use WifiManager to get information
		 */
		WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		/*
		 * need WifiInfo and DhcpInfo to get information
		 */
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

		/*
		 * when the wifi is not enabled, some arguments is not available use
		 * boolean needAdd to judge which should be displayed
		 */
		boolean needAdd = true;
		needAdd = wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;

		/*
		 * add wifi state
		 */
		hints.add(getString(R.string.hint_wifistate_activity_networking));
		contents.add(NetworkInfoHelper.getWifiStateNameByWifiState(this,
				wifiManager.getWifiState()));
		/*
		 * add mac address
		 */
		hints.add(getString(R.string.hint_macaddress_activity_networking));
		contents.add(wifiInfo.getMacAddress());

		if (needAdd) {
			/*
			 * when the wifi is not enabled the getScanResults() will
			 * return null
			 */
			ScanResult scanResult = wifiManager.getScanResults().get(0);

			/*
			 * add ssid
			 */
			hints.add(getString(R.string.hint_ssid_activity_networking));
			if (wifiInfo.getSSID() == null) {
				contents.add(getString(R.string.str_no_network_connected_activity_networking));
				return;
			}
			contents.add(wifiInfo.getSSID());
			/*
			 * add bssid
			 */
			hints.add(getString(R.string.hint_bssid_activity_networking));
			contents.add(wifiInfo.getBSSID());
			/*
			 * add link speed
			 */
			hints.add(getString(R.string.hint_linkspeed_activity_networking));
			contents.add(wifiInfo.getLinkSpeed() + " Mbps");
			/*
			 * add ip address
			 */
			hints.add(getString(R.string.hint_ipaddress_activity_networking));
			contents.add(Formatter.formatIpAddress(wifiInfo.getIpAddress()));
			/*
			 * add netmask
			 */
			hints.add(getString(R.string.hint_netmask_activity_networking));
			contents.add(Formatter.formatIpAddress(dhcpInfo.netmask));
			/*
			 * add gateway
			 */
			hints.add(getString(R.string.hint_gateway_activity_networking));
			contents.add(Formatter.formatIpAddress(dhcpInfo.gateway));
			/*
			 * add dns1
			 */
			hints.add(getString(R.string.hint_dns1_activity_networking));
			contents.add(Formatter.formatIpAddress(dhcpInfo.dns1));
			/*
			 * add dns2
			 */
			hints.add(getString(R.string.hint_dns2_activity_networking));
			contents.add(Formatter.formatIpAddress(dhcpInfo.dns2));
			/*
			 * add capabilities
			 */
			hints.add(getString(R.string.hint_capabilities_activity_networking));
			contents.add(scanResult.capabilities);
			/*
			 * add frequency
			 */
			hints.add(getString(R.string.hint_frequency_activity_networking));
			contents.add(scanResult.frequency + " MHz");
		}
	}

	/**
	 * add the mobile info data
	 * 
	 * @param hints
	 *            the hints list
	 * @param contents
	 *            the contents list
	 */
	private void getMobileInfoData(List<String> hints, List<String> contents) {
		/*
		 * use TelephonyManager to get information
		 */
		TelephonyManager telManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		/*
		 * when the sim is not ready, some arguments is not available use
		 * boolean needAdd to judge which should be displayed
		 */
		boolean needAdd = true;
		needAdd = telManager.getSimState() == TelephonyManager.SIM_STATE_READY;

		/*
		 * add Sim Card State
		 */
		hints.add(getString(R.string.hint_simstate_activity_networking));
		contents.add(NetworkInfoHelper.getSimStateNameBySimState(this,
				telManager.getSimState()));

		if (needAdd) {
			/*
			 * add Operator
			 */
			hints.add(getString(R.string.hint_operator_activity_networking));
			contents.add(telManager.getNetworkOperatorName());
			/*
			 * add PhoneNumber
			 */
			hints.add(getString(R.string.hint_phonenumber_activity_networking));
			contents.add(telManager.getLine1Number());
		}

		/*
		 * add Country Code
		 */
		hints.add(getString(R.string.hint_country_activity_networking));
		contents.add(telManager.getNetworkCountryIso());
		/*
		 * add Phone Type
		 */
		hints.add(getString(R.string.hint_phonetype_activity_networking));
		contents.add(NetworkInfoHelper.getPhoneTypeNameByPhoneType(this,
				telManager.getPhoneType()));
		/*
		 * add Network Type
		 */
		hints.add(getString(R.string.hint_networktype_activity_networking));
		contents.add(NetworkInfoHelper.getNetworkTypeNameByNetworkType(this,
				telManager.getNetworkType()));
		/*
		 * add DeviceID (IMEI)
		 */
		hints.add(getString(R.string.hint_deviceid_activity_networking));
		contents.add(telManager.getDeviceId());

		if (needAdd) {
			/*
			 * add SubscriberId (IMSI)
			 */
			hints.add(getString(R.string.hint_subscriberid_activity_networking));
			contents.add(telManager.getSubscriberId());
		}

	}

	/**
	 * the code below is trying to close the program with double click back
	 * button
	 * 
	 */
	private Boolean isExit = false;

	/*
	 * I block the first 'pushed back button' action
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByDoubleClick();
		}

		return false;
	}

	/*
	 * then, I make a Timer for 2 seconds if no 'pushed back button' action came
	 * in, then ignore the first action otherwise, kill myself.
	 */
	private void exitByDoubleClick() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, R.string.str_exit_app_toast_message,
					Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

}
