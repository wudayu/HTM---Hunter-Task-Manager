package com.wudayu.htm;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.wudayu.htm.activities.AppsActivity;
import com.wudayu.htm.activities.EditExceptionListActivity;
import com.wudayu.htm.activities.NetworkingActivity;
import com.wudayu.htm.activities.PerformanceActivity;
import com.wudayu.htm.activities.PreferencesActivity;
import com.wudayu.htm.activities.ServicesActivity;
import com.wudayu.htm.activities.SysappsActivity;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 30, 2013 9:41:45 PM
 * @Description: This is David Wu's property.
 * 
 **/

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	/*
	 * define the widgets
	 */
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * find the widgets
		 */
		tabHost = getTabHost();

		/*
		 * Initialize the widgets and add tabs
		 */
		addTabToTabHost();

	}

	private void addTabToTabHost() {
		/*
		 * add five activities into MainActivity tabhost
		 */
		setTabIndicator(
				getResources().getString(R.string.str_tab_apps_activity_main),
				1, new Intent(this, AppsActivity.class));
		setTabIndicator(
				getResources()
						.getString(R.string.str_tab_sysapps_activity_main), 2,
				new Intent(this, SysappsActivity.class));
		setTabIndicator(
				getResources().getString(
						R.string.str_tab_services_activity_main), 3,
				new Intent(this, ServicesActivity.class));
		setTabIndicator(
				getResources().getString(
						R.string.str_tab_performance_activity_main), 4,
				new Intent(this, PerformanceActivity.class));
		setTabIndicator(
				getResources().getString(
						R.string.str_tab_networking_activity_main), 5,
				new Intent(this, NetworkingActivity.class));
	}

	private void setTabIndicator(String title, int nId, Intent intent) {
		/*
		 * indicate the style of each tab
		 */
		View view = LayoutInflater.from(this.tabHost.getContext()).inflate(
				R.layout.tab_style_activity_main, null);

		TextView text = (TextView) view
				.findViewById(R.id.tab_label_activity_main);
		String strId = String.valueOf(nId);

		text.setText(title);

		/*
		 * create a new tab
		 */
		TabHost.TabSpec localTabSpec = this.tabHost.newTabSpec(strId)
				.setIndicator(view).setContent(intent);

		/*
		 * load new tab
		 */
		tabHost.addTab(localTabSpec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu_activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		/*
		 * open the preferences activity
		 */
		case R.id.menu_preferences:
			Intent preferencesActivity = new Intent(this,
					PreferencesActivity.class);
			startActivity(preferencesActivity);
			return true;
			/*
			 * open the edit exception list
			 */
		case R.id.menu_edit_exception_list:
			Intent editExceptionListActivity = new Intent(this,
					EditExceptionListActivity.class);
			startActivity(editExceptionListActivity);
			return true;
		case R.id.menu_about:
			aboutInfo();
			return true;
			/*
			 * exit
			 */
		case R.id.menu_exit:
			sureToExit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * about infomation
	 */
	private void aboutInfo() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.menu_about))
				.setMessage(
						getString(R.string.app_name) + " "
								+ getString(R.string.version) + "\n\n"
								+ getString(R.string.author) + "\n"
								+ getString(R.string.email))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(getString(R.string.str_btn_positive), null);

		dialog.show();
	}

	/*
	 * pop up a dialog to make sure the user want to leave
	 */
	private void sureToExit() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.str_exit_app_dialog_check))
				.setMessage(getString(R.string.str_exit_app_dialog_message))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(getString(R.string.str_btn_positive),
						new btnYesExitOnClickListener())
				.setNegativeButton(getString(R.string.str_btn_negtive), null);

		dialog.show();
	}

	/*
	 * when you press exit button from option menu a dialog poped up this is the
	 * action when you pressed yes
	 */
	private class btnYesExitOnClickListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

	/**
	 * the code below is trying to close the program with double click back
	 * button
	 * 
	 */

	/*
	 * It is different from the code in the tab activities because when I press
	 * the Back button the tab activities get the signal first, then the
	 * MainActivity get the signal, so it is necessary to overwrite the
	 * onKeyDown method when the Back key down, do nothing
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}

		return false;
	}

}
