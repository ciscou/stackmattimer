package com.rubikaz.cisco.android.stackmattimer.social;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.rubikaz.cisco.android.stackmattimer.R;
import com.rubikaz.cisco.android.stackmattimer.StackMatSessionTimes;
import com.rubikaz.cisco.android.stackmattimer.StackMatTime;
import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public class FacebookManager {
	public static void shareSingle(final StackMatTimer timer,
			final StackMatTime time) {
		fb = getFacebookInstance(timer);

		final Runnable errorToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(timer,
						R.string.facebook_error,
						Toast.LENGTH_LONG).show();
			}
		};

		if (fb.isSessionValid()) {
			final ProgressDialog pd = ProgressDialog.show(timer, timer.getString(R.string.facebook_sharing),
					timer.getString(R.string.facebook_sharing_single).replaceAll("\\#\\{time\\}", time.toString()), true);

			final Runnable successToastRunnable = new Runnable() {
				@Override
				public void run() {
					Toast
							.makeText(
									timer,
									timer.getString(R.string.facebook_shared_single).replaceAll("\\#\\{time\\}", time.toString()),
									Toast.LENGTH_LONG).show();
				}
			};

			final Bundle parameters = new Bundle();
			parameters.putString("access_token", fb.getAccessToken());
			parameters.putString("message", timer.getString(R.string.facebook_single_message).replaceAll("\\#\\{time\\}", time.toString()).replaceAll("\\#\\{scramble\\}", time.getScramble()).replaceAll("\\#\\{puzzle\\}", timer.getPuzzleType()));

			AsyncFacebookRunner fbr = new AsyncFacebookRunner(fb);
			RequestListener listener = new RequestListener() {
				@Override
				public void onComplete(String json) {
					Log.d(FacebookManager.class.getSimpleName(), time
							.toString()
							+ " single posted in Facebook wall. Response was "
							+ json);
					timer.runOnUiThread(successToastRunnable);
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}

				@Override
				public void onFacebookError(FacebookError e) {
					onError(e);
				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e) {
					onError(e);
				}

				@Override
				public void onIOException(IOException e) {
					onError(e);
				}

				@Override
				public void onMalformedURLException(MalformedURLException e) {
					onError(e);
				}

				private void onError(Throwable e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook publishing error", e);
					timer.runOnUiThread(errorToastRunnable);
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}
			};

			Log.d(FacebookManager.class.getSimpleName(), "Posting "
					+ time.toString() + " single in Facebook wall...");
			fbr.request("me/feed", parameters, "POST", listener);
		} else {
			Log.d(FacebookManager.class.getSimpleName(),
					"Authorizing in Facebook...");
			DialogListener listener = new DialogListener() {
				@Override
				public void onCancel() {
				}

				@Override
				public void onComplete(Bundle values) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Authorization in Facebook completed ? "
									+ String.valueOf(fb.isSessionValid()));
					if (fb.isSessionValid()) {
						Editor editor = timer.getSharedPreferences("facebook",
								Context.MODE_PRIVATE).edit();
						editor.putString("access_token", fb.getAccessToken());
						editor.putLong("expires", fb.getAccessExpires());
						editor.commit();
						shareSingle(timer, time);
					} else {
						timer.runOnUiThread(errorToastRunnable);
					}
				}

				@Override
				public void onError(DialogError e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook authentication error", e);
					timer.runOnUiThread(errorToastRunnable);
				}

				@Override
				public void onFacebookError(FacebookError e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook authentication error", e);
					timer.runOnUiThread(errorToastRunnable);
				}
			};
			fb.authorize(timer, "154620441238349",
					new String[] { "publish_stream" }, listener);
		}
	}

	public static void shareAverage(final StackMatTimer timer,
			final StackMatSessionTimes times, final int ntimes, final int offset, final boolean rolling, final boolean best) {
		fb = getFacebookInstance(timer);

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
		final Runnable errorToastRunnable = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(timer,
						R.string.facebook_error,
						Toast.LENGTH_LONG).show();
			}
		};

		if (fb.isSessionValid()) {
			final ProgressDialog pd = ProgressDialog.show(timer, timer.getString(R.string.facebook_sharing),
					timer.getString(R.string.facebook_sharing_average).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)), true);

			final Runnable successToastRunnable = new Runnable() {
				@Override
				public void run() {
					Toast
							.makeText(
									timer,
									timer.getString(R.string.facebook_shared_average).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)),
									Toast.LENGTH_LONG).show();
				}
			};

			final Bundle parameters = new Bundle();
			parameters.putString("access_token", fb.getAccessToken());
			String subject = timer.getString(R.string.facebook_average_subject).replaceAll("\\#\\{time\\}", blablabla.toString()).replaceAll("\\#\\{n\\}", String.valueOf(ntimes)).replaceAll("\\#\\{puzzle\\}", timer.getPuzzleType());
			Log.d("XXX", "sharing in fb, " + subject);
			String message = timer.getString(R.string.facebook_average_message);
			Log.d("XXX", "sharing in fb, ntimes=" + String.valueOf(ntimes) + ", offset=" + String.valueOf(offset));
			for (int i = ntimes + 5 + offset - 1; i >= 5 + offset; i--) {
				StackMatTime time = times.getTime(i);
				message += timer.getString(R.string.facebook_average_message_each_time).replaceAll("\\#\\{time\\}", time.toString()).replaceAll("\\#\\{scramble\\}", time.getScramble());
				Log.d("XXX", "sharing in fb, " + message);
			}
			parameters.putString("subject", subject);
			parameters.putString("message", message);

			Log.d(FacebookManager.class.getSimpleName(), "Posting "
					+ blablabla.toString() + " average of "
					+ String.valueOf(ntimes) + " in Facebook wall...");

			AsyncFacebookRunner fbr = new AsyncFacebookRunner(fb);
			RequestListener listener = new RequestListener() {

				@Override
				public void onComplete(String json) {
					Log.d(FacebookManager.class.getSimpleName(), blablabla
							.toString()
							+ " average of "
							+ String.valueOf(ntimes)
							+ " posted in Facebook wall. Response was " + json);
					timer.runOnUiThread(successToastRunnable);
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}

				@Override
				public void onFacebookError(FacebookError e) {
					onError(e);
				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e) {
					onError(e);
				}

				@Override
				public void onIOException(IOException e) {
					onError(e);
				}

				@Override
				public void onMalformedURLException(MalformedURLException e) {
					onError(e);
				}

				private void onError(Throwable e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook publishing error", e);
					timer.runOnUiThread(errorToastRunnable);
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}

			};
			fbr.request("me/notes", parameters, "POST", listener);

		} else {
			Log.d(FacebookManager.class.getSimpleName(),
					"Authorizing in Facebook...");

			DialogListener listener = new DialogListener() {
				@Override
				public void onCancel() {
				}

				@Override
				public void onComplete(Bundle values) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Authorization in Facebook completed ? "
									+ String.valueOf(fb.isSessionValid()));
					if (fb.isSessionValid()) {
						Editor editor = timer.getSharedPreferences("facebook",
								Context.MODE_PRIVATE).edit();
						editor.putString("access_token", fb.getAccessToken());
						editor.putLong("expires", fb.getAccessExpires());
						editor.commit();
						shareAverage(timer, times, ntimes, offset, rolling, best);
					} else {
						timer.runOnUiThread(errorToastRunnable);
					}
				}

				@Override
				public void onError(DialogError e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook authentication error", e);
					timer.runOnUiThread(errorToastRunnable);
				}

				@Override
				public void onFacebookError(FacebookError e) {
					Log.d(FacebookManager.class.getSimpleName(),
							"Facebook authentication error", e);
					timer.runOnUiThread(errorToastRunnable);
				}
			};

			fb.authorize(timer, "154620441238349",
					new String[] { "publish_stream" }, listener);
		}
	}

	private static Facebook getFacebookInstance(StackMatTimer timer) {
		if (fb == null) {
			fb = new Facebook();
			SharedPreferences savedSession = timer.getSharedPreferences(
					"facebook", Context.MODE_PRIVATE);
			fb.setAccessToken(savedSession.getString("access_token", null));
			fb.setAccessExpires(savedSession.getLong("expires", 0));
		}
		return fb;
	}

	private static Facebook fb = null;
}
