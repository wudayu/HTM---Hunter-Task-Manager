package com.wudayu.htm.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.wudayu.htm.R;
import com.wudayu.htm.adapters.ExceptionListBaseAppAdapter;
import com.wudayu.htm.adapters.ExceptionListServiceAdapter;
import com.wudayu.htm.beans.HtmApp;
import com.wudayu.htm.beans.HtmBaseApp;
import com.wudayu.htm.beans.HtmService;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 31, 2013 22:14:45 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class EditExceptionListActivity extends Activity {

	/*
	 * the list view, the shared preferences,
	 * the adapter and the data for
	 * app and system app exception list
	 */
	ListView lvBaseAppExceptionList;
	SharedPreferences prefBaseAppExcepList;
	ExceptionListBaseAppAdapter baseAppAdapter;
	List<HtmBaseApp> baseAppExceptionList;

	/*
	 * the list view, the shared preferences,
	 * the adapter and the data for
	 * service exception list
	 */
	ListView lvServiceExceptionList;
	SharedPreferences prefServiceExcepList;
	ExceptionListServiceAdapter serviceAdapter;
	List<HtmService> serviceExceptionList;

	/*
	 * define the event when the user press the back button
	 * on the action bar
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * duel with the different orientation
		 * use different layout
		 */
		if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation)
			setContentView(R.layout.portrait_activity_edit_exception_list);
		else
			setContentView(R.layout.activity_edit_exception_list);

		/*
		 * define and initialize the action bar
		 * enable the up / back button on the action bar
		 * and set title for action bar
		 */
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.title_activity_edit_exception_list));

		/*
		 * initialize variables
		 * initialize widgets
		 * set item click listener for the lists
		 */
		baseAppExceptionList = new ArrayList<HtmBaseApp>();
		serviceExceptionList = new ArrayList<HtmService>();

		lvBaseAppExceptionList = (ListView) findViewById(R.id.lv_baseapp_exception_list_activity_edit_exception_list);
		lvServiceExceptionList = (ListView) findViewById(R.id.lv_service_exception_list_activity_edit_exception_list);

		lvBaseAppExceptionList
				.setOnItemClickListener(new LvBaseAppExceptionListOnItemClickListener());
		lvServiceExceptionList
				.setOnItemClickListener(new LvServiceExceptionListOnItemClickListener());

		/*
		 * initialize the data collections for each list
		 */
		getBaseAppExceptionList();
		getServiceExceptionList();

		/*
		 * initialize the adapters
		 */
		baseAppAdapter = new ExceptionListBaseAppAdapter(
				getApplicationContext(), baseAppExceptionList,
				R.layout.lvitem_baseapp_exception_activity_edit_exception_list);
		serviceAdapter = new ExceptionListServiceAdapter(
				getApplicationContext(), serviceExceptionList,
				R.layout.lvitem_service_exception_activity_edit_exception_list);

		/*
		 * bind the adapter to list views
		 */
		lvBaseAppExceptionList.setAdapter(baseAppAdapter);
		lvServiceExceptionList.setAdapter(serviceAdapter);
	}

	/*
	 * when pressed item on the list
	 * the item will be earsed from the list
	 * and the htm shared preferences
	 */
	private class LvBaseAppExceptionListOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*
			 * remove the item from the shared preferences
			 */
			SharedPreferences.Editor editor = prefBaseAppExcepList.edit();
			editor.remove(((ExceptionListBaseAppAdapter.ViewCache) view
					.getTag()).pkgName);
			editor.commit();
			/*
			 * re-get the exception list from shared preferences
			 * and notify the list view the data set is changed
			 */
			getBaseAppExceptionList();
			baseAppAdapter.notifyDataSetChanged();
			Toast.makeText(
					EditExceptionListActivity.this,
					getString(R.string.str_removed_toast_activity_edit_exception_list),
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * initialize the base app exception list collection
	 * the data is from the shared preferences
	 */
	public void getBaseAppExceptionList() {
		prefBaseAppExcepList = getSharedPreferences(
				getString(R.string.preferences_exception_list_app_sysapp), 0);
		Map<String, ?> exceptionPkgNames = prefBaseAppExcepList.getAll();
		PackageManager pm = getPackageManager();

		baseAppExceptionList.clear();

		/*
		 * get application name and packageName from the
		 * shared preferences and add to list collection
		 */
		for (String pkgName : exceptionPkgNames.keySet()) {
			CharSequence title = null;
			Drawable icon = null;
			HtmBaseApp app = null;
			try {
				icon = pm.getApplicationIcon(pm.getApplicationInfo(pkgName,
						PackageManager.GET_META_DATA));
				title = pm.getApplicationLabel(pm.getApplicationInfo(pkgName,
						PackageManager.GET_META_DATA));
				app = new HtmApp(-1, title.toString(), pkgName, icon, 0);
			} catch (Exception e) {
			}

			if (app != null) {
				baseAppExceptionList.add(app);
			}
		}
	}

	/*
	 * when pressed item on the list
	 * the item will be earsed from the list
	 * and the htm shared preferences
	 */
	private class LvServiceExceptionListOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*
			 * remove the item from the shared preferences
			 */
			SharedPreferences.Editor editor = prefServiceExcepList.edit();
			editor.remove(((ExceptionListServiceAdapter.ViewCache) view
					.getTag()).serviceName);
			editor.commit();
			/*
			 * re-get the exception list from shared preferences
			 * and notify the list view the data set is changed
			 */
			getServiceExceptionList();
			serviceAdapter.notifyDataSetChanged();
			Toast.makeText(
					EditExceptionListActivity.this,
					getString(R.string.str_removed_toast_activity_edit_exception_list),
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * initialize the service exception list collection
	 * the data is from the shared preferences
	 */
	public void getServiceExceptionList() {
		prefServiceExcepList = getSharedPreferences(
				getString(R.string.preferences_exception_list_service), 0);
		Map<String, ?> exceptionPkgNames = prefServiceExcepList.getAll();
		PackageManager pm = getPackageManager();

		serviceExceptionList.clear();

		/*
		 * get serviceName and packageName from the
		 * shared preferences and add to list collection
		 */
		for (String serviceName : exceptionPkgNames.keySet()) {
			String pkgName = (String) exceptionPkgNames.get(serviceName);
			Drawable icon = null;
			HtmService service = null;

			try {
				icon = pm.getApplicationIcon(pm.getApplicationInfo(pkgName,
						PackageManager.GET_META_DATA));
				service = new HtmService(-1, null, pkgName, icon, 0, serviceName, null, null, null);
			} catch (Exception e) {
			}

			if (service != null) {
				serviceExceptionList.add(service);
			}
		}
	}

}
