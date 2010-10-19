package com.rubikaz.cisco.android.stackmattimer.social;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.rubikaz.cisco.android.stackmattimer.StackMatSessionTimes;
import com.rubikaz.cisco.android.stackmattimer.StackMatTime;
import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

// FIXME use asyncrhonous twitter

public class TwitterManager {
	public static void shareSingle(final StackMatTimer timer,
			final StackMatTime time) {
		SharedPreferences savedSession = timer.getSharedPreferences("twitter",
				Context.MODE_PRIVATE);
		final String token = savedSession.getString("token", null);
		final String tokenSecret = savedSession.getString("token_secret", null);

		if (token == null || tokenSecret == null) {
			final Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer("zHG9bsY9aTQL22EfWu4wvA",
					"5HS6Ap5S65h3JwpAQtr2UKXCe35jmvtZmefOE16TFo");

			RequestToken requestToken = null;
			try {
				requestToken = twitter.getOAuthRequestToken();
			} catch (TwitterException e2) {
				// TODO Auto-generated catch block
				Log.d(TwitterManager.class.getSimpleName(),
						"requestToken error", e2);
			}
			final RequestToken rt = requestToken;
			// FIXME hacer esto en condiciones
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());

			AlertDialog.Builder alert = new AlertDialog.Builder(timer);
			// FIXME traducir
			alert.setTitle("Title");
			// FIXME traducir
			alert.setMessage("Message");
			final EditText input = new EditText(timer);
			alert.setView(input);
			// FIXME traducir
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							AccessToken accessToken = null;
							try {
								accessToken = twitter.getOAuthAccessToken(rt,
										input.getText().toString());
							} catch (TwitterException te) {
								// FIXME mensaje de error
								if (401 == te.getStatusCode()) {
									System.out
											.println("Unable to get the access token.");
								} else {
									Log.d(TwitterManager.class.getSimpleName(),
											"getOAuthToken error", te);
								}
							}

							if (accessToken != null) {
									Editor editor = timer.getSharedPreferences(
											"twitter", Context.MODE_PRIVATE)
											.edit();
									editor.putString("token", accessToken
											.getToken());
									editor.putString("token_secret",
											accessToken.getTokenSecret());
									editor.commit();
									TwitterManager.shareSingle(timer, time);
							} else {
								// FIXME mensaje de error
								System.out.println("access token == null!");
							}
						}
					});
			// FIXME traducir
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
			alert.show();
		} else {
			// FIXME traducir
			final ProgressDialog pd = ProgressDialog.show(timer, "Sharing",
					"Sharing " + time.toString()
							+ " single in Twitter. Please wait...", true);

			new Thread() {
				@Override
				public void run() {

					AccessToken accessToken = new AccessToken(token,
							tokenSecret);
					final Twitter twitter = new TwitterFactory()
							.getOAuthAuthorizedInstance(
									"zHG9bsY9aTQL22EfWu4wvA",
									"5HS6Ap5S65h3JwpAQtr2UKXCe35jmvtZmefOE16TFo",
									accessToken);
					if (accessToken != null) {
						try {
							// FIXME traducir
							String message = "has just solved a " + timer.getPuzzleType() + " in "
									+ time.toString() + ", scramble was "
									+ time.getScramble();
							Status status = twitter.updateStatus(message);
							System.out
									.println("Successfully updated the status to ["
											+ status.getText() + "].");
							timer.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// FIXME traducir
									Toast
											.makeText(
													timer,
													"Your "
															+ time.toString()
															+ " single has been published in your Twitter wall.",
													Toast.LENGTH_LONG).show();
								}
							});
						} catch (final TwitterException e) {
							// TODO Auto-generated catch block
							Log.d(TwitterManager.class.getSimpleName(),
									"updateStatus error", e);
							timer.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// FIXME traducir
									Toast.makeText(
											timer,
											"Sorry, there was an error. Check your internet connection and try again. "
													+ e.getMessage(),
											Toast.LENGTH_LONG).show();
								}
							});
						}
					}
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}
			}.start();
		}
	}

	// FIXME arreglar lo mismo que en el caso de single...
	public static void shareAverage(final StackMatTimer timer,
			final StackMatSessionTimes times, final int ntimes, final int offset, final boolean rolling, final boolean best) {
		SharedPreferences savedSession = timer.getSharedPreferences("twitter",
				Context.MODE_PRIVATE);
		final String token = savedSession.getString("token", null);
		final String tokenSecret = savedSession.getString("token_secret", null);

		if (token == null || tokenSecret == null) {
			final Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer("zHG9bsY9aTQL22EfWu4wvA",
					"5HS6Ap5S65h3JwpAQtr2UKXCe35jmvtZmefOE16TFo");

			RequestToken requestToken = null;
			try {
				requestToken = twitter.getOAuthRequestToken();
			} catch (TwitterException e2) {
				// TODO Auto-generated catch block
				Log.d(TwitterManager.class.getSimpleName(),
						"requestToken error", e2);
			}
			final RequestToken rt = requestToken;
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());

			AlertDialog.Builder alert = new AlertDialog.Builder(timer);
			alert.setTitle("Title");
			alert.setMessage("Message");
			final EditText input = new EditText(timer);
			alert.setView(input);
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							AccessToken accessToken = null;
							try {
								accessToken = twitter.getOAuthAccessToken(rt,
										input.getText().toString());
							} catch (TwitterException te) {
								if (401 == te.getStatusCode()) {
									System.out
											.println("Unable to get the access token.");
								} else {
									Log.d(TwitterManager.class.getSimpleName(),
											"getOAuthToken error", te);
								}
							}

							if (accessToken != null) {
								try {
									System.out.println("access token for user "
											+ String.valueOf(twitter
													.verifyCredentials()
													.getId()) + " is ["
											+ accessToken.getToken() + ", "
											+ accessToken.getTokenSecret()
											+ "]");
									Editor editor = timer.getSharedPreferences(
											"twitter", Context.MODE_PRIVATE)
											.edit();
									editor.putString("token", accessToken
											.getToken());
									editor.putString("token_secret",
											accessToken.getTokenSecret());
									editor.commit();
									TwitterManager.shareAverage(timer, times,
											ntimes, offset, rolling, best);
								} catch (TwitterException e) {
									// TODO Auto-generated catch block
									Log.d(TwitterManager.class.getSimpleName(),
											"verifyCredentials error", e);
								}
							} else {
								System.out.println("access token == null!");
							}
						}
					});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
			alert.show();
		} else {
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
			
			final ProgressDialog pd = ProgressDialog.show(timer, "Sharing",
					"Sharing " + blablabla.toString() + " average of "
							+ String.valueOf(ntimes)
							+ " " + timer.getPuzzleType() + " in Twitter. Please wait...", true);

			new Thread() {
				@Override
				public void run() {

					AccessToken accessToken = new AccessToken(token,
							tokenSecret);
					final Twitter twitter = new TwitterFactory()
							.getOAuthAuthorizedInstance(
									"zHG9bsY9aTQL22EfWu4wvA",
									"5HS6Ap5S65h3JwpAQtr2UKXCe35jmvtZmefOE16TFo",
									accessToken);
					if (accessToken != null) {
						try {
							String subject = blablabla.toString() + " avg " + String.valueOf(ntimes) + " " + timer.getPuzzleType();
							Log.d("XXX", "sharing in tw, " + subject);
							String message = "Times:";
							Log.d("XXX", "sharing in tw, ntimes=" + String.valueOf(ntimes) + ", offset=" + String.valueOf(offset));
							for (int i = ntimes + 5 + offset - 1; i >= 5 + offset; i--) {
								StackMatTime time = times.getTime(i);
								message += " " + time.toString();
								Log.d("XXX", "sharing in tw, " + message);
							}
							Status status = twitter.updateStatus(subject
									+ " - " + message);
							System.out
									.println("Successfully updated the status to ["
											+ status.getText() + "].");
							timer.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast
											.makeText(
													timer,
													"Your "
															+ blablabla
																	.toString()
															+ " average of "
															+ String
																	.valueOf(ntimes)
															+ " " + timer.getPuzzleType() + " has been published in your Twitter wall.",
													Toast.LENGTH_LONG).show();
								}
							});
						} catch (final TwitterException e) {
							// TODO Auto-generated catch block
							Log.d(TwitterManager.class.getSimpleName(),
									"updateStatus error", e);
							timer.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(
											timer,
											"Sorry, there was an error. Check your internet connection and try again. "
													+ e.getMessage(),
											Toast.LENGTH_LONG).show();
								}
							});
						}
					}
					timer.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				}
			}.start();
		}
	}
}
