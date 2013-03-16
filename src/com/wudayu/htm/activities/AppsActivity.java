package com.wudayu.htm.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wudayu.htm.R;
import com.wudayu.htm.adapters.AppsListAdapter;
import com.wudayu.htm.beans.HtmApp;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 31, 2013 21:04:05 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class AppsActivity extends Activity {

	/*
	 * these four variables are used to be the arguments for method
	 * getAppsListInOrder() and refreshListViewInOrder()
	 */
	private static final String ORDER_METHOD_NAME = "name";
	private static final String ORDER_METHOD_MEM = "memory";

	private static final String ORDER_DIRECT_ASC = "asc";
	private static final String ORDER_DIRECT_DESC = "desc";

	/*
	 * used to record the current order direct
	 */
	private String orderCurrDirect;

	/**
	 * Global Variables
	 */
	/*
	 * the current applications list
	 */
	List<HtmApp> appsList;
	/*
	 * the Adapter that controls the application list
	 */
	AppsListAdapter appsListAdapter;
	/*
	 * ListView for showing running apps
	 */
	private ListView lvApps;
	/*
	 * three bottom buttons
	 */
	private Button btnRefresh;
	private Button btnKill;
	private Button btnKillAll;
	/*
	 * three top stuff that can be clicked
	 */
	private CheckBox chkAllItem;
	private TextView txtNames;
	private TextView txtMems;
	/*
	 * ProgressDialog is used to show on the top while a lot of work are doing
	 * background
	 */
	private ProgressDialog progressDialog;
	/*
	 * handler is used to handle the message
	 */
	private Handler handler;
	/*
	 * preferencesExcepList used to store preferences exception list
	 */
	private SharedPreferences preferencesExcepList;
	/*
	 * exceptionList used to store the package names from preferences exception
	 * list
	 */
	private List<String> exceptionList;
	/*
	 * get from default preference file
	 * display the hunter task manager itself or not
	 */
	private boolean displayHtm;

	private void loadDefaultPreference() {
		SharedPreferences preference = getSharedPreferences("com.wudayu.htm_preferences", 0);
		displayHtm = preference.getBoolean("displayHtm", false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps);

		System.out.println("Apps onCreate() Called");

		/*
		 * Initialize Variables
		 */
		appsList = new ArrayList<HtmApp>();
		exceptionList = new ArrayList<String>();
		orderCurrDirect = ORDER_DIRECT_ASC;
		initializeExceptionList();

		/*
		 * initialize widgets
		 */
		chkAllItem = (CheckBox) findViewById(R.id.chk_appslist_activity_apps);
		lvApps = (ListView) findViewById(R.id.lv_apps_activity_apps);
		btnRefresh = (Button) findViewById(R.id.btn_refresh_activity_apps);
		btnKill = (Button) findViewById(R.id.btn_kill_activity_apps);
		btnKillAll = (Button) findViewById(R.id.btn_kill_all_activity_apps);
		txtNames = (TextView) findViewById(R.id.txt_appsnames_activity_apps);
		txtMems = (TextView) findViewById(R.id.txt_appsmems_activity_apps);

		/*
		 * set listener or settings to widgets
		 */
		chkAllItem.setOnCheckedChangeListener(new chkAllItemOnClickListener());
		lvApps.setOnItemClickListener(new AppsListOnItemClickListener());
		lvApps.setOnItemLongClickListener(new AppsListOnItemLongClickListener());
		txtNames.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtMems.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		btnRefresh.setOnClickListener(new btnRefreshOnClickListener());
		btnKill.setOnClickListener(new btnKillOnClickListener());
		btnKillAll.setOnClickListener(new btnKillAllOnClickListener());
		txtNames.setOnClickListener(new txtNamesOnClickListener());
		txtMems.setOnClickListener(new txtMemsOnClickListener());

		/*
		 * load the application settings and ListView first
		 */
		loadDefaultPreference();
		refreshListView();
	}

	@Override
	protected void onStart() {
		System.out.println("Apps onStart() Called");

		super.onStart();
	}

	/*
	 * the event when user press one item from the listview
	 */
	private class AppsListOnItemClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String pkgName = appsList.get(position).getPkgName();
			/*
			 * when app has been pressed, we open the detail form
			 */
			goToAppInfoActivity(pkgName);
		}

	}

	/*
	 * registerForContextMenu when user long press the item from listview
	 */
	private class AppsListOnItemLongClickListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			registerForContextMenu(parent);

			return false;
		}

	}

	/*
	 * inflate the context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_lv_apps_activity_apps, menu);
	}

	/*
	 * the event when user press the item from context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		/*
		 * the tag is convertView's tag from AppsListAdapter or we can say
		 * targetView is the convertView
		 */
		AppsListAdapter.ViewCache cache = (AppsListAdapter.ViewCache) info.targetView
				.getTag();
		String pkgName = cache.pkgName;
		PackageManager pm = getPackageManager();

		switch (item.getItemId()) {
		/*
		 * switch to app
		 */
		case R.id.context_item_switch_to_app_activity_apps:
			Intent intent = pm.getLaunchIntentForPackage(pkgName);
			getApplicationContext().startActivity(intent);
			return true;
			/*
			 * kill task
			 */
		case R.id.context_item_kill_task_activity_apps:
			killApp(pkgName);
			refreshListView();
			Toast.makeText(getApplicationContext(),
					getString(R.string.str_apps_have_killed_activity_apps),
					Toast.LENGTH_SHORT).show();
			return true;
			/*
			 * storage detail
			 */
		case R.id.context_item_storage_detail_activity_apps:
			goToAppInfoActivity(pkgName);
			return true;
			/*
			 * force stop or uninstall
			 */
		case R.id.context_item_forcestop_uninstall_activity_apps:
			goToAppInfoActivity(pkgName);
			return true;
			/*
			 * add to exception list user can use EditExceptionListActivity to
			 * edit exception list
			 */
		case R.id.context_item_add_to_exception_list_activity_apps:
			addToExceptionList(pkgName);
			refreshListView();
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * add application into exception list
	 * @param pkgName package name
	 */
	private void addToExceptionList(String pkgName) {
		SharedPreferences.Editor editor = preferencesExcepList.edit();
		editor.putString(pkgName, pkgName);
		editor.commit();
		exceptionList.add(pkgName);
	}

	/**
	 * open application detail infomation activity
	 * @param pkgName package name
	 */
	public void goToAppInfoActivity(String pkgName) {
		Intent intent = new Intent(
				android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + pkgName));
		startActivity(intent);
	}

	/*
	 * used to sort by name
	 */
	private class txtNamesOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * if the current order is asc then use desc instead vice versa
			 */
			if (orderCurrDirect == ORDER_DIRECT_ASC) {
				refreshListViewInOrder(ORDER_METHOD_NAME, ORDER_DIRECT_DESC);
				orderCurrDirect = ORDER_DIRECT_DESC;
			} else {
				refreshListViewInOrder(ORDER_METHOD_NAME, ORDER_DIRECT_ASC);
				orderCurrDirect = ORDER_DIRECT_ASC;
			}
		}

	}

	/*
	 * used to sort by memory
	 */
	private class txtMemsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * if the current order is asc then use desc instead vice versa
			 */
			if (orderCurrDirect == ORDER_DIRECT_ASC) {
				refreshListViewInOrder(ORDER_METHOD_MEM, ORDER_DIRECT_DESC);
				orderCurrDirect = ORDER_DIRECT_DESC;
			} else {
				refreshListViewInOrder(ORDER_METHOD_MEM, ORDER_DIRECT_ASC);
				orderCurrDirect = ORDER_DIRECT_ASC;
			}
		}

	}

	/*
	 * the button 'Kill''s onClickListener
	 */
	private class btnKillOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * this variable is used to make sure that the user has selected any
			 * items
			 */
			boolean nothingChecked = true;
			for (int i = 0; i < appsList.size(); ++i) {
				if (AppsListAdapter.getSelMapValueByKey(i)) {
					killApp(appsList.get(i).getPkgName());
					nothingChecked = false;
				}
			}
			/*
			 * if nothing selected, notify the user and quit current function
			 */
			if (nothingChecked) {
				Toast.makeText(AppsActivity.this,
						R.string.str_nothing_has_checked_activity_apps,
						Toast.LENGTH_SHORT).show();
				return;
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(AppsActivity.this,
					getString(R.string.str_apps_have_killed_activity_apps),
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * the button 'Kill All''s onClickListener
	 */
	private class btnKillAllOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * kill application one by one
			 */
			for (HtmApp app : appsList) {
				killApp(app.getPkgName());
			}
			/*
			 * reload the list from memory
			 */
			refreshListView();
			Toast.makeText(AppsActivity.this,
					getString(R.string.str_apps_have_killed_activity_apps),
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * the button 'Refresh''s onClickListener
	 */
	private class btnRefreshOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			/*
			 * call loadDefaultPreference() to reload preference
			 * is simple just call refreshListView() to reload list from memory
			 */
			loadDefaultPreference();
			refreshListView();
		}

	}

	/*
	 * the checkbox on the left top for select all items from listview
	 * OnCheckedChangeListener
	 */
	private class chkAllItemOnClickListener implements OnCheckedChangeListener {

		/**
		 * onCheckedChanged() is used to check all checkbox on each item of the
		 * listview
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			for (int i = 0; i < appsList.size(); ++i)
				AppsListAdapter.setSelMapValueByKey(i, isChecked);
			/*
			 * notify the adapter to recall its getView() method to refresh the
			 * listview
			 */
			appsListAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void onRestart() {
		System.out.println("Apps onRestart() Called");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		System.out.println("Apps onResume() Called");
		super.onResume();
	}

	@Override
	protected void onPause() {
		System.out.println("Apps onPause() Called");
		super.onPause();
	}

	@Override
	protected void onStop() {
		System.out.println("Apps onStop() Called");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		System.out.println("Apps onDestroy() Called");

		/*
		 * important, when the screen rotate
		 * current activity will be closed
		 * finish() will be called
		 * before that, progressDialog must be dismissed
		 */
		if (progressDialog != null)
			progressDialog.dismiss();

		super.onDestroy();
	}

	/**
	 * getAppsListWithoutOrder() is used to get applications list for type
	 * HtmApp but without any order
	 * 
	 */
	public void getAppsListWithoutOrder() {
		/*
		 * initialize the list appsList
		 */
		appsList.clear();

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		/*
		 * get the Running Process list
		 */
		List<ActivityManager.RunningAppProcessInfo> listOfProcesses = activityManager
				.getRunningAppProcesses();

		Iterator<RunningAppProcessInfo> iter = listOfProcesses.iterator();
		/*
		 * we need use PackageManager to get the information of the process
		 */
		PackageManager pm = this.getPackageManager();
		while (iter.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (iter
					.next());
			CharSequence appName = null;
			Drawable appIcon = null;
			int memorySize = 0;
			/*
			 * check if the process is user's or system's
			 */
			if ((displayHtm ? true : !info.processName.equals(getPackageName()))
					&& (!exceptionList.contains(info.processName))
					&& pm.getLaunchIntentForPackage(info.processName) != null) {
				try {
					/*
					 * get the application name, icon and memory size
					 */
					appName = pm.getApplicationLabel(pm.getApplicationInfo(
							info.processName, PackageManager.GET_META_DATA));
					appIcon = pm.getApplicationIcon(pm.getApplicationInfo(
							info.processName, PackageManager.GET_META_DATA));
					memorySize = activityManager
							.getProcessMemoryInfo(new int[] { info.pid })[0]
							.getTotalPss();

					HtmApp app = new HtmApp(info.pid, appName.toString(),
							info.processName, appIcon, memorySize);
					appsList.add(app);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * sortAppsList() is used to sort the applications list for type HtmApp with
	 * order
	 * 
	 * @param sortMethod
	 *            sort by name or memory
	 * @param orderDirect
	 *            asc or desc
	 */
	public void sortAppsList(String sortMethod, String orderDirect) {
		/*
		 * use different comparator in different arguments
		 */
		if (sortMethod == ORDER_METHOD_NAME) {
			if (orderDirect == ORDER_DIRECT_ASC)
				Collections.sort(appsList, new ComparatorHtmAppNameASC());
			else
				Collections.sort(appsList, new ComparatorHtmAppNameDESC());
		} else if (sortMethod == ORDER_METHOD_MEM) {
			if (orderDirect != ORDER_DIRECT_DESC)
				Collections.sort(appsList, new ComparatorHtmAppMemASC());
			else
				Collections.sort(appsList, new ComparatorHtmAppMemDESC());
		}

	}

	/**
	 * ComparatorHtmAppNameASC is sort by name in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmAppNameASC implements Comparator<HtmApp> {
		@Override
		public int compare(HtmApp lhs, HtmApp rhs) {
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}
	}

	/**
	 * ComparatorHtmAppNameDESC is sort by name in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmAppNameDESC implements Comparator<HtmApp> {
		@Override
		public int compare(HtmApp lhs, HtmApp rhs) {
			return rhs.getName().compareToIgnoreCase(lhs.getName());
		}
	}

	/**
	 * ComparatorHtmAppMemASC is sort by memory in asc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmAppMemASC implements Comparator<HtmApp> {
		@Override
		public int compare(HtmApp lhs, HtmApp rhs) {
			return lhs.getSize() - rhs.getSize();
		}
	}

	/**
	 * ComparatorHtmAppMemDESC is sort by memory in desc
	 * 
	 * @author David
	 * 
	 */
	public class ComparatorHtmAppMemDESC implements Comparator<HtmApp> {
		@Override
		public int compare(HtmApp lhs, HtmApp rhs) {
			return rhs.getSize() - lhs.getSize();
		}
	}

	/**
	 * refreshListView() is used to refresh the list by reloading the memory,
	 * the procedure is slow so I use a progressDialog to make the user
	 * experience right
	 */
	private void refreshListView() {

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				bindAppsListIntoListView();

				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				chkAllItem.setChecked(false);
			}
		};
		
		if (progressDialog == null) {
			initializeExceptionList();

			progressDialog = ProgressDialog
			.show(AppsActivity.this,
					getString(R.string.str_title_refresh_apps_list_activity_apps),
					getString(R.string.str_content_refresh_apps_list_activity_apps));

			new Thread() {
				public void run() {
					getAppsListWithoutOrder();
	
					handler.sendEmptyMessage(0);
				}
			}.start();
		}

	}

	/**
	 * initialize exception list
	 */
	private void initializeExceptionList() {
		exceptionList.clear();
		preferencesExcepList = getSharedPreferences(
				getString(R.string.preferences_exception_list_app_sysapp), 0);
		exceptionList.addAll(preferencesExcepList.getAll().keySet());
	}

	/**
	 * refreshListViewInOrder didn't like refreshListViewWithoutOrder it doesn't
	 * reload the memory, so it's fast just for sort
	 * 
	 * @param sortMethod
	 *            sort by name or memory
	 * @param orderDirect
	 *            asc or desc
	 */
	private void refreshListViewInOrder(String sortMethod, String orderDirect) {
		sortAppsList(sortMethod, orderDirect);
		bindAppsListIntoListView();
	}

	/**
	 * bindAppsListIntoListView() is the Method for setting or resetting the
	 * adapter for listview
	 */
	private void bindAppsListIntoListView() {
		appsListAdapter = new AppsListAdapter(getApplicationContext(),
				appsList, R.layout.lvitem_app_lv_apps_activity_apps);
		lvApps.setAdapter(appsListAdapter);
	}

	/**
	 * killApp() is used to kill one app by their package name
	 * 
	 * @param pkgName
	 *            package name
	 */
	private void killApp(String pkgName) {
		ActivityManager actvityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		actvityManager.killBackgroundProcesses(pkgName);
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
