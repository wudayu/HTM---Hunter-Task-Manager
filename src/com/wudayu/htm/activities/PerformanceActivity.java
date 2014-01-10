package com.wudayu.htm.activities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.wudayu.htm.R;
import com.wudayu.htm.views.DrawHistoryView;
import com.wudayu.htm.views.DrawNegtiveLineView;
import com.wudayu.htm.views.DrawPositiveLineView;
import com.wudayu.htm.views.DrawTextView;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Jan 31, 2013 21:05:42 PM
 * @Description: This is David Wu's property.
 * 
 **/

public class PerformanceActivity extends Activity {

	/*
	 * a constant for recognizing the refresh message
	 */
	private static final int REFRESH = 0x0001;

	/*
	 * the frame layouts for cpu, memory and battery
	 */
	private FrameLayout fmltCpuUsage;
	private FrameLayout fmltMemUsage;
	private FrameLayout fmltBatUsage;
	private FrameLayout fmltCpuHistory;
	private FrameLayout fmltMemHistory;

	/*
	 * the height and width of cpu, memory and battery framelayout
	 */
	private int widthCpuUsage, heightCpuUsage;
	private int widthMemUsage, heightMemUsage;
	private int widthBatUsage, heightBatUsage;
	private int widthCpuHistory, heightCpuHistory;
	private int widthMemHistory, heightMemHistory;

	/*
	 * the button on the bottom left the battery history button
	 */
	private Button btnBatteryHistory;
	/*
	 * the button on the bottom
	 */
	private Button btnMoreInfo;

	private Queue<Integer> queCpuHistory;
	private Queue<Integer> queMemHistory;

	private final int BASE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * duel with the different orientation
		 * use different layout
		 */
		if (Configuration.ORIENTATION_LANDSCAPE == this.getResources().getConfiguration().orientation)
			setContentView(R.layout.portrait_activity_performance);
		else
			setContentView(R.layout.activity_performance);

		/*
		 * build a battery BroadcastReceiver to receive the battery level
		 */
		BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				batLevel = intent.getIntExtra("level", 0);
			}
		};

		/*
		 * Initialize Variables
		 */
		initializeQueues();
		/*
		 * initialize widgets
		 */
		fmltCpuUsage = (FrameLayout) findViewById(R.id.framelayout_cpu_usage_activity_performance);
		fmltMemUsage = (FrameLayout) findViewById(R.id.framelayout_mem_usage_activity_performance);
		fmltBatUsage = (FrameLayout) findViewById(R.id.framelayout_bat_usage_activity_performance);
		fmltCpuHistory = (FrameLayout) findViewById(R.id.framelayout_cpu_history_activity_performance);
		fmltMemHistory = (FrameLayout) findViewById(R.id.framelayout_mem_history_activity_performance);
		btnBatteryHistory = (Button) findViewById(R.id.btn_bat_history_activity_performance);
		btnMoreInfo = (Button) findViewById(R.id.btn_more_info_activity_performance);

		/*
		 * set listener or settings to widgets
		 */
		btnBatteryHistory
				.setOnClickListener(new BtnBatteryHistoryOnClickListener());
		btnMoreInfo.setOnClickListener(new BtnMoreInfoOnClickListener());

		/*
		 * register the battery BroadcastReceiver to this application
		 */
		registerReceiver(batteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		new Handler().post(initialFrameLayoutTask);
	}

	/*
	 * use to initialize the FrameLayout's width, height...
	 */
	private Runnable initialFrameLayoutTask = new Runnable() { 
        public void run() {
        	widthCpuUsage = fmltCpuUsage.getWidth();
        	heightCpuUsage = fmltCpuUsage.getHeight();
        	widthMemUsage = fmltMemUsage.getWidth();
        	heightMemUsage = fmltMemUsage.getHeight();
        	widthBatUsage = fmltBatUsage.getWidth();
        	heightBatUsage = fmltBatUsage.getHeight();
        	widthCpuHistory = fmltCpuHistory.getWidth();
        	heightCpuHistory = fmltCpuHistory.getHeight();
        	widthMemHistory = fmltMemHistory.getWidth();
        	heightMemHistory = fmltMemHistory.getHeight();

    		new RefreshThread().start();
        }
    };

	/*
	 * display the page of the about phone
	 */
	private class BtnMoreInfoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings$DeviceInfoSettingsActivity");
			startActivity(intent);
		}
		
	}

	/*
	 * display the page of the system battery history
	 */
	private class BtnBatteryHistoryOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings$PowerUsageSummaryActivity");
			startActivity(intent);
		}

	}

	/*
	 * initialize two queues
	 * all values are null
	 */
	private void initializeQueues() {
		queCpuHistory = new LinkedList<Integer>();
		queMemHistory = new LinkedList<Integer>();

		for (int i = 0; i < DrawHistoryView.POINT_COUNT; ++i) {
			queCpuHistory.offer(null);
			queMemHistory.offer(null);
		}
	}

	/*
	 * these four variable is used to calculate the cpu usage
	 */
	double cpuUsage = 0;
	int batLevel = 0;
	long total = 0;
	long idle = 0;

	/**
	 * readCpuUsage() is for read cpu usage
	 * by reading the file, /proc/stat
	 */
	public void readCpuUsage() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();

			String[] toks = load.split(" ");

			long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]);
			long currIdle = Long.parseLong(toks[5]);

			this.cpuUsage = (currTotal - total) * 100.0f
					/ (currTotal - total + currIdle - idle);
			this.total = currTotal;
			this.idle = currIdle;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * this handler is very important it is used to do the things like refresh
	 * the frame layouts
	 */
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PerformanceActivity.REFRESH) {
				Double cpu = this.getCpuUsage();
				Long availMem = Long.valueOf(this.getAvailMemory());
				Long totalMem = Long.valueOf(this.getTotalMemory());
				Integer battery = batLevel;

				/*
				 * draw usage frame layouts
				 */
				drawCpuUsage(cpu);

				drawMemUsage(totalMem, availMem);

				drawBatUsage(battery);

				/*
				 * draw history frame layouts
				 */
				drawCpuHistory();

				drawMemHistory();
			}
		}

		/*
		 * call the readCpuUsage() outside get the cpu usage
		 */
		public double getCpuUsage() {
			readCpuUsage();
			return cpuUsage;
		}

		/*
		 * get available memory
		 */
		public String getAvailMemory() {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo mi = new MemoryInfo();
			am.getMemoryInfo(mi);

			return String.valueOf(mi.availMem);
		}

		/*
		 * get total memory
		 */
		public String getTotalMemory() {
			String str1 = "/proc/meminfo";
			String str2;
			String[] arrayOfString;
			long initial_memory = 0;
			try {
				FileReader localFileReader = new FileReader(str1);
				BufferedReader localBufferedReader = new BufferedReader(
						localFileReader, 8192);
				str2 = localBufferedReader.readLine();
				arrayOfString = str2.split("\\s+");

				initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
				localBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return String.valueOf(initial_memory);
		}
	};

	/*
	 * the thread used to control the refresh of all the framelayout
	 */
	public class RefreshThread extends Thread {
		public void run() {
			while (true) {
				Message msg = new Message();
				msg.what = REFRESH;
				msg.obj = this;
				mHandler.sendMessage(msg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/*
	 * draw the usage frame layout for cpu
	 */
	private void drawCpuUsage(double usage) {
		int humCpu = (int) (usage / BASE + 1 < BASE ? usage / BASE + 1 : BASE);

		queCpuHistory.poll();
		queCpuHistory.offer((int) usage);

		drawUsage(fmltCpuUsage, widthCpuUsage, heightCpuUsage, humCpu, Math.round(usage)
				+ " %");
	}

	/*
	 * draw the usage frame layout for memory
	 */
	private void drawMemUsage(Long total, Long avail) {
		long per = (total - avail) * 100 / total;
		int humMem = (int) (per / BASE + 1 < BASE  ? per / BASE + 1 : BASE);

		queMemHistory.poll();
		queMemHistory.offer((int) per);

		drawUsage(
				fmltMemUsage, widthMemUsage, heightMemUsage,
				humMem,
				Formatter.formatFileSize(getBaseContext(), total - avail)
						+ " / "
						+ Formatter.formatFileSize(getBaseContext(), total));
	}

	/*
	 * draw the usage frame layout for battery
	 */
	private void drawBatUsage(Integer remain) {
		int humBat = (int) (remain / BASE + 1 < BASE ? remain / BASE + 1 : BASE);
		drawUsage(fmltBatUsage, widthBatUsage, heightBatUsage, humBat, remain + " %");
	}

	/**
	 * drawUsage() is very important it used to draw the usage frame layout on
	 * this activity after some tests, I found that, 1dp == 2pix
	 * 
	 * @param layout
	 *            the layout need draw for
	 * @param posCount
	 *            the positive line count
	 * @param text
	 *            the text which need to
	 */
	private void drawUsage(FrameLayout layout, int width, int height, int posCount, String text) {
		final int fontSize = height / 6;
		int stackHeight = height * 7 / 10;
		int numberHeight = height - stackHeight;

		layout.removeAllViews();
		
		/*
		 * draw lines
		 */
		for (int i = 0; i < BASE; ++i) {
			if (i < posCount)
				layout.addView(new DrawPositiveLineView(this, width * 1 / BASE, stackHeight - stackHeight * i / BASE,
						width * 9 / BASE, stackHeight - stackHeight * i / BASE));
			else
				layout.addView(new DrawNegtiveLineView(this, width * 1 / BASE, stackHeight - stackHeight * i / BASE,
						width * 9 / BASE, stackHeight - stackHeight * i / BASE));
		}
		/*
		 * draw text
		 */
		layout.addView(new DrawTextView(this, width / 2, stackHeight + (numberHeight + fontSize) / 2, text, fontSize));
	}

	/*
	 * draw the history frame layout for cpu
	 */
	private void drawCpuHistory() {
		drawHistory(fmltCpuHistory, widthCpuHistory, heightCpuHistory, queCpuHistory);
	}

	/*
	 * draw the history frame layout for memory
	 */
	private void drawMemHistory() {
		drawHistory(fmltMemHistory, widthMemHistory, heightMemHistory, queMemHistory);
	}

	/**
	 * drawHistory() is very important it used to draw the frame layout on this
	 * activity
	 * 
	 * @param layout
	 *            the layout need draw for
	 * @param queue
	 *            the data need for drawing
	 */
	private void drawHistory(FrameLayout layout, int width, int height, Queue<Integer> queue) {
		layout.removeAllViews();
		layout.addView(new DrawHistoryView(this, queue));
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
