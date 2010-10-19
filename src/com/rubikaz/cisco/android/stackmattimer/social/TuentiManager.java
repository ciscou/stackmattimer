package com.rubikaz.cisco.android.stackmattimer.social;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rubikaz.cisco.android.stackmattimer.R;
import com.rubikaz.cisco.android.stackmattimer.StackMatSessionTimes;
import com.rubikaz.cisco.android.stackmattimer.StackMatTime;
import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;
import com.rubikaz.cisco.tuenti.AsynchronousTuenti;
import com.rubikaz.cisco.tuenti.TuentiException;
import com.rubikaz.cisco.tuenti.TuentiRequestListener;

public class TuentiManager {

	public static void shareSingle(final StackMatTimer timer,
			final StackMatTime time) {
		SharedPreferences savedSession = timer.getSharedPreferences("tuenti",
				Context.MODE_PRIVATE);
		int user_id = savedSession.getInt("user_id", -1);
		String session_id = savedSession.getString("session_id", null);
		final AsynchronousTuenti tuenti = new AsynchronousTuenti(user_id,
				session_id);

		final ProgressDialog pd = ProgressDialog.show(timer, timer.getString(R.string.tuenti_sharing),
				timer.getString(R.string.tuenti_sharing_single).replaceAll("\\#\\{time\\}", time.toString()), true);

		final Runnable errorToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(timer,
						R.string.tuenti_error,
						Toast.LENGTH_LONG).show();
			}
		};

		final Runnable successToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast
						.makeText(
								timer,
								timer.getString(R.string.tuenti_shared_single).replaceAll("\\#\\{time\\}", time.toString()),
								Toast.LENGTH_LONG).show();
			}
		};

		final TuentiRequestListener addPostToProfileWallListener = new TuentiRequestListener() {
			public void onComplete() {
				Log.d(TuentiManager.class.getSimpleName(), "Done posting");
				pd.dismiss();
				Editor editor = timer.getSharedPreferences("tuenti",
						Context.MODE_PRIVATE).edit();
				editor.putInt("user_id", tuenti.getUserID());
				editor.putString("session_id", tuenti.getSessionID());
				editor.commit();
				timer.runOnUiThread(successToastRunnable);
			}

			public void onError(TuentiException te) {
				Log.d(TuentiManager.class.getSimpleName(), "Tuenti error", te);
				pd.dismiss();
				timer.runOnUiThread(errorToastRunnable);
			}
		};

		final Thread addPostToProfileWallThread = new Thread() {
			@Override
			public void run() {
				Log.d(TuentiManager.class.getSimpleName(), "Posting...");
				tuenti.addPostToProfileWall(tuenti.getUserID(),
						timer.getString(R.string.tuenti_single_message).replaceAll("\\#\\{time\\}", time.toString()).replaceAll("\\#\\{scramble\\}", time.getScramble()).replaceAll("\\#\\{puzzle\\}", timer.getPuzzleType()),
						addPostToProfileWallListener);
			}
		};

		Thread mainThread;

		if (tuenti.isAuthenticated()) {
			mainThread = addPostToProfileWallThread;
		} else {
			final TuentiRequestListener loginRequestListener = new TuentiRequestListener() {
				public void onComplete() {
					Log.d(TuentiManager.class.getSimpleName(), "Done login");
					Editor editor = timer.getSharedPreferences("tuenti",
							Context.MODE_PRIVATE).edit();
					editor.putInt("user_id", tuenti.getUserID());
					editor.putString("session_id", tuenti.getSessionID());
					editor.commit();
					addPostToProfileWallThread.start();
				}

				public void onError(TuentiException te) {
					Log.d(TuentiManager.class.getSimpleName(), "Tuenti error",
							te);
					pd.dismiss();
					timer.runOnUiThread(errorToastRunnable);
				}
			};

			Thread loginThread = new Thread() {
				@Override
				public void run() {
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							final Dialog dialog = new Dialog(timer);
							dialog.setContentView(R.layout.tuenti_login_dialog);
							dialog.setTitle(R.string.tuenti_login);
							Button ok = (Button) dialog.findViewById(R.id.ok);
							ok.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									EditText email = (EditText) dialog
											.findViewById(R.id.email);
									EditText password = (EditText) dialog
											.findViewById(R.id.password);
									Log.d(TuentiManager.class.getSimpleName(),
											"Login...");
									tuenti.login(email.getText().toString(),
											password.getText().toString(),
											loginRequestListener);
									dialog.dismiss();
								}
							});
							Button cancel = (Button) dialog
									.findViewById(R.id.cancel);
							cancel.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									pd.dismiss();
									dialog.dismiss();
								}
							});

							dialog.show();
						}

					});
				}
			};

			mainThread = loginThread;
		}

		mainThread.start();
	}

	public static void shareAverage(final StackMatTimer timer,
			final StackMatSessionTimes times, final int ntimes, final int offset, final boolean rolling, final boolean best) {
		SharedPreferences savedSession = timer.getSharedPreferences("tuenti",
				Context.MODE_PRIVATE);
		int user_id = savedSession.getInt("user_id", -1);
		String session_id = savedSession.getString("session_id", null);
		final AsynchronousTuenti tuenti = new AsynchronousTuenti(user_id,
				session_id);

		final StackMatTime blablabla;
		if(best)
			blablabla = new StackMatTime(ntimes == 12 ? times
				.getBRAvg12() : times.getBRAvg5());
		else
			if(rolling)
				blablabla = new StackMatTime(ntimes == 12 ? times
					.getRAvg12() : times.getRAvg5());
			else
				blablabla = new StackMatTime(times.getAvg());
		
		final ProgressDialog pd = ProgressDialog.show(timer, timer.getString(R.string.tuenti_sharing),
				timer.getString(R.string.tuenti_sharing_average).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)), true);

		final Runnable errorToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(timer,
						R.string.tuenti_error,
						Toast.LENGTH_LONG).show();
			}
		};

		final Runnable successToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast
						.makeText(
								timer,
								timer.getString(R.string.tuenti_shared_average).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)).replaceAll("\\#\\{puzzle\\}", timer.getPuzzleType()),
								Toast.LENGTH_LONG).show();
			}
		};

		final TuentiRequestListener addPostToProfileWallListener = new TuentiRequestListener() {
			public void onComplete() {
				Log.d(TuentiManager.class.getSimpleName(), "Done posting");
				pd.dismiss();
				Editor editor = timer.getSharedPreferences("tuenti",
						Context.MODE_PRIVATE).edit();
				editor.putInt("user_id", tuenti.getUserID());
				editor.putString("session_id", tuenti.getSessionID());
				editor.commit();
				timer.runOnUiThread(successToastRunnable);
			}

			public void onError(TuentiException te) {
				Log.d(TuentiManager.class.getSimpleName(), "Tuenti error", te);
				pd.dismiss();
				timer.runOnUiThread(errorToastRunnable);
			}
		};

		final Thread addPostToProfileWallThread = new Thread() {
			@Override
			public void run() {
				Log.d(TuentiManager.class.getSimpleName(), "Posting...");
				String subject = timer.getString(R.string.tuenti_average_subject).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)).replaceAll("\\#\\{puzzle\\}", timer.getPuzzleType());
				String message = timer.getString(R.string.tuenti_average_message);
				for (int i = ntimes + 5 + offset - 1; i >= 5 + offset; i--) {
					StackMatTime time = times.getTime(i);
					message += " " + time.toString();
				}
				tuenti.addPostToProfileWall(tuenti.getUserID(), subject + " - "
						+ message, addPostToProfileWallListener);
			}
		};

		Thread mainThread;

		if (tuenti.isAuthenticated()) {
			mainThread = addPostToProfileWallThread;
		} else {
			final TuentiRequestListener loginRequestListener = new TuentiRequestListener() {
				public void onComplete() {
					Log.d(TuentiManager.class.getSimpleName(), "Done login");
					Editor editor = timer.getSharedPreferences("tuenti",
							Context.MODE_PRIVATE).edit();
					editor.putInt("user_id", tuenti.getUserID());
					editor.putString("session_id", tuenti.getSessionID());
					editor.commit();
					addPostToProfileWallThread.start();
				}

				public void onError(TuentiException te) {
					Log.d(TuentiManager.class.getSimpleName(), "Tuenti error",
							te);
					pd.dismiss();
					timer.runOnUiThread(errorToastRunnable);
				}
			};

			Thread loginThread = new Thread() {
				@Override
				public void run() {
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							final Dialog dialog = new Dialog(timer);
							dialog.setContentView(R.layout.tuenti_login_dialog);
							dialog.setTitle(R.string.tuenti_login);
							Button ok = (Button) dialog.findViewById(R.id.ok);
							ok.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									EditText email = (EditText) dialog
											.findViewById(R.id.email);
									EditText password = (EditText) dialog
											.findViewById(R.id.password);
									Log.d(TuentiManager.class.getSimpleName(),
											"Login...");
									tuenti.login(email.getText().toString(),
											password.getText().toString(),
											loginRequestListener);
									dialog.dismiss();
								}
							});
							Button cancel = (Button) dialog
									.findViewById(R.id.cancel);
							cancel.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									pd.dismiss();
									dialog.dismiss();
								}
							});

							dialog.show();
						}

					});
				}
			};

			mainThread = loginThread;
		}

		mainThread.start();
	}

}
