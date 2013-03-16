package com.wudayu.htm.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.wudayu.htm.R;

public class PreferencesActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * load the preferences xml file
		 */
		addPreferencesFromResource(R.layout.activity_preferences);
		/*
		 * add action bar home as up button
		 */
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			/*
			 * add go up event
			 */
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
