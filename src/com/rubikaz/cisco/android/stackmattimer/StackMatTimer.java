package com.rubikaz.cisco.android.stackmattimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.rubikaz.cisco.android.stackmattimer.scramble.ScrambleGenerator;
import com.rubikaz.cisco.android.stackmattimer.social.FacebookManager;
import com.rubikaz.cisco.android.stackmattimer.social.TuentiManager;
import com.rubikaz.cisco.android.stackmattimer.social.TwitterManager;
import com.rubikaz.cisco.android.stackmattimer.state.StackMatTimerState;
import com.rubikaz.cisco.android.stackmattimer.state.StackMatTimerStateOff;

public class StackMatTimer extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		lcd = (TextView) findViewById(R.id.lcd);
		greenLed = (TextView) findViewById(R.id.green_led);
		redLed = (TextView) findViewById(R.id.red_led);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/lcd.ttf");
		lcd.setTypeface(tf);
		scramble = (TextView) findViewById(R.id.scramble);

		sensor = (Button) findViewById(R.id.sensor);
		sensor.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onSensorDown();
					return false;
				case MotionEvent.ACTION_UP:
					onSensorUp();
					return false;
				case MotionEvent.ACTION_MOVE:
					return false;
				default:
					return false;
				}
			}
		});

		times = (ListView) findViewById(R.id.times);
		times.setOnCreateContextMenuListener(this);
		timesDrawer = (SlidingDrawer) findViewById(R.id.times_drawer);

		// startNewSession();
		// displayNextScramble();
		// setState(new StackMatTimerStateOn(this));

		setState(new StackMatTimerStateOff(this));
		onPowerButtonClicked(null);

		new Thread() {
			@Override
			public void run() {
				while (true) {
					tick();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}

	public void showTimes() {
		Log.d(getClass().getSimpleName(), "show times");
		if (!timesDrawer.isOpened())
			timesDrawer.animateOpen();
	}

	public void hideTimes() {
		Log.d(getClass().getSimpleName(), "hide times");
		if (timesDrawer.isOpened())
			timesDrawer.animateClose();
	}

	public synchronized void onPowerButtonClicked(View view) {
		state.onPowerButtonClicked();
	}

	public synchronized void onResetButtonClicked(View view) {
		state.onResetButtonClicked();
	}

	public synchronized void onSensorDown() {
		state.onSensorDown();
	}

	public synchronized void onSensorUp() {
		state.onSensorUp();
	}

	public synchronized void setState(StackMatTimerState state) {
		Log.d(getClass().getSimpleName(), "set state "
				+ state.getClass().getSimpleName());
		this.state = state;
	}

	public void setLcd(long ms) {
		lcdText = "";

		if (ms < 0) {
			lcdText = "";
		} else {
			lcdText = new StackMatTime(ms).toString();
		}

		Runnable refreshLcd = new Runnable() {
			@Override
			public void run() {
				lcd.setText(lcdText);
			}
		};

		runOnUiThread(refreshLcd);
	}

	public void setGreenLed(boolean on) {
		greenLedColor = on ? Color.GREEN : Color.TRANSPARENT;
		Runnable refreshGreenLed = new Runnable() {
			@Override
			public void run() {
				greenLed.setTextColor(greenLedColor);
			}
		};
		runOnUiThread(refreshGreenLed);
	}

	public void setRedLed(boolean on) {
		redLedColor = on ? Color.RED : Color.TRANSPARENT;
		Runnable refreshRedLed = new Runnable() {
			@Override
			public void run() {
				redLed.setTextColor(redLedColor);
			}
		};
		runOnUiThread(refreshRedLed);
	}

	public synchronized void tick() {
		state.tick();
	}

	public void startNewSession() {
		session_times = new StackMatSessionTimes();
		times.setAdapter(new SimpleAdapter(this, session_times.getTimes(),
				R.layout.time_scramble_list_item, new String[] { "time",
						"scramble" }, new int[] { R.id.time, R.id.scramble }));
		/*for(int i=0; i<15; i++)
			session_times.add(new StackMatTime("test", (i+1) * 1000));
		session_times.calculateAvgs();
		((SimpleAdapter)times.getAdapter()).notifyDataSetChanged();*/
	}

	public void finishCurrentSession() {
		// TODO save times to DB
		// times = null;
		// hideTimes();
	}

	public void saveTime(long time) {
		StackMatTime stack_mat_time = new StackMatTime(scrambleText, time);
		// TODO save time to DB
		// TODO check single record

		session_times.add(stack_mat_time);
		session_times.calculateAvgs();
		// TODO check avgs records

		SimpleAdapter sa = (SimpleAdapter) times.getAdapter();
		sa.notifyDataSetChanged();
	}

	public void displayNextScramble() {
		scrambleText = ScrambleGenerator.nextScramble(getPuzzleType());
		Runnable refreshScramble = new Runnable() {
			@Override
			public void run() {
				scramble.setText(scrambleText);
			}
		};
		runOnUiThread(refreshScramble);
	}
	
	public void hideScramble() {
		Runnable hideScramble = new Runnable() {
			@Override
			public void run() {
				scramble.setText("");
			}
		};
		runOnUiThread(hideScramble);
	}
	
	public void showScramble() {
		Runnable showScramble = new Runnable() {
			@Override
			public void run() {
				scramble.setText(scrambleText);
			}
		};
		runOnUiThread(showScramble);
	}

	public void endSession() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setTitle(R.string.end_session);
		alert.setMessage(R.string.really_end_session);
		alert.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						puzzle_type = next_puzzle_type;
						setState(new StackMatTimerStateOff(StackMatTimer.this));
					}
				});
		alert.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						next_puzzle_type = puzzle_type;
					}
				});
		alert.show();
	}

	public String getPuzzleType() {
		return puzzle_type;
	}

	public void setPuzzleType(String puzzle_type) {
		if (!puzzle_type.equals(this.puzzle_type)) {
			if (state instanceof StackMatTimerStateOff) {
				this.puzzle_type = puzzle_type;
			} else {
				next_puzzle_type = puzzle_type;
				endSession();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int order = Menu.FIRST;
		for (int i = 0; i < PUZZLE_TYPES.length; i++)
			menu.add(Menu.NONE, i, order++, PUZZLE_TYPES[i]);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		setPuzzleType(PUZZLE_TYPES[item.getItemId()]);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo cmi) {
		super.onCreateContextMenu(menu, view, cmi);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) cmi;

		if (info.position > 4) {
			StackMatTime time = session_times.getTime(info.position);
			menu.add(Menu.NONE + 1, MENU_PLUS_2, Menu.FIRST, "+2").setChecked(
					time.isPlus2());
			menu.add(Menu.NONE + 1, MENU_DNF, Menu.FIRST + 1, "DNF")
					.setChecked(time.isDNF());
			menu.add(Menu.NONE, MENU_SHARE_FB, Menu.FIRST + 2,
					R.string.facebook_share);
			//menu.add(Menu.NONE, MENU_SHARE_TW, Menu.FIRST + 3,
			//		R.string.twitter_share);
			//menu.add(Menu.NONE, MENU_SHARE_TU, Menu.FIRST + 4,
			//		R.string.tuenti_share);
			menu.add(Menu.NONE, MENU_DELETE, Menu.FIRST + 5, R.string.delete);

			menu.setHeaderTitle(time.toString());
			menu.setGroupCheckable(Menu.NONE + 1, true, false);
		} else {
			int ntimes = 0;
			// int offset = 0;
			
			if (info.position == 0) {
				ntimes = session_times.getNTimes();
			} else if (info.position == 1) {
				ntimes = 5;
			} else if (info.position == 2) {
				ntimes = 12;
			} else if (info.position == 3) {
				ntimes = 5;
				// offset = session_times.getBRAvg5Offset();
			} else if (info.position == 4) {
				ntimes = 12;
				// offset = session_times.getBRAvg12Offset();
			}
			
			if (session_times.getNTimes() >= ntimes) {
				menu.add(Menu.NONE, MENU_SHARE_AVG_FB, Menu.FIRST + 0,
						R.string.facebook_share);
				//menu.add(Menu.NONE, MENU_SHARE_AVG_TW, Menu.FIRST + 1,
				//		R.string.twitter_share);
				//menu.add(Menu.NONE, MENU_SHARE_AVG_TU, Menu.FIRST + 2,
				//		R.string.tuenti_share);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		if (info.position > 4) {
			StackMatTime time = session_times.getTime(info.position);
			final SimpleAdapter sa = (SimpleAdapter) times.getAdapter();

			switch (item.getItemId()) {
			case MENU_PLUS_2:
				time.togglePlus2();
				item.setChecked(time.isPlus2());
				session_times.recalculateTime(info.position);
				session_times.calculateAvgs();
				sa.notifyDataSetChanged();
				break;
			case MENU_DNF:
				time.toggleDNF();
				item.setChecked(time.isDNF());
				session_times.recalculateTime(info.position);
				session_times.calculateAvgs();
				sa.notifyDataSetChanged();
				break;
			case MENU_SHARE_FB:
				FacebookManager.shareSingle(this, time);
				break;
			case MENU_SHARE_TW:
				TwitterManager.shareSingle(this, time);
				break;
			case MENU_SHARE_TU:
				TuentiManager.shareSingle(this, time);
				break;
			case MENU_DELETE:
				final int pos = info.position;
				new AlertDialog.Builder(this).setIcon(
						android.R.drawable.ic_dialog_alert).setTitle(
						R.string.delete).setMessage(R.string.really_delete)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										session_times.deleteTime(pos);
										session_times.calculateAvgs();
										sa.notifyDataSetChanged();
									}
								}).setNegativeButton(R.string.no, null).show();
				break;
			}
		} else {
			int ntimes = 0;
			int offset = 0;
			boolean rolling = true;
			boolean best = false;
			
			if (info.position == 0) {
				ntimes = session_times.getNTimes();
				rolling = false;
			} else if (info.position == 1) {
				ntimes = 5;
			} else if (info.position == 2) {
				ntimes = 12;
			} else if (info.position == 3) {
				ntimes = 5;
				offset = session_times.getBRAvg5Offset();
				best = true;
			} else if (info.position == 4) {
				ntimes = 12;
				offset = session_times.getBRAvg12Offset();
				best = true;
			}
			
			switch (item.getItemId()) {
			case MENU_SHARE_AVG_FB:
				FacebookManager.shareAverage(this, session_times, ntimes, offset, rolling, best);
				break;
			case MENU_SHARE_AVG_TW:
				TwitterManager.shareAverage(this, session_times, ntimes, offset, rolling, best);
				break;
			case MENU_SHARE_AVG_TU:
				TuentiManager.shareAverage(this, session_times, ntimes, offset, rolling, best);
				break;
			}
		}

		return true;
	}

	private StackMatTimerState state;

	private Button sensor;

	private TextView scramble;
	private String scrambleText;
	private TextView lcd;
	private String lcdText;
	private TextView greenLed;
	private int greenLedColor;
	private TextView redLed;
	private int redLedColor;

	private ListView times;
	private SlidingDrawer timesDrawer;

	private StackMatSessionTimes session_times;

	private final String PUZZLE_TYPES[] = new String[] { "2x2x2", "3x3x3", "4x4x4", "5x5x5", "6x6x6", "7x7x7" };
	private String puzzle_type = "3x3x3";
	private String next_puzzle_type = puzzle_type;

	public static final int MENU_DNF = 1;
	public static final int MENU_PLUS_2 = 2;
	public static final int MENU_DELETE = 3;
	public static final int MENU_SHARE_FB = 4;
	public static final int MENU_SHARE_TW = 5;
	public static final int MENU_SHARE_TU = 6;
	public static final int MENU_SHARE_AVG_FB = 7;
	public static final int MENU_SHARE_AVG_TW = 8;
	public static final int MENU_SHARE_AVG_TU = 9;
}
