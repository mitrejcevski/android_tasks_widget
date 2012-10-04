package com.mitrejcevski.widget.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.mitrejcevski.widget.R;

public class AlarmReceiverActivity extends Activity {
	private MediaPlayer mMediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_layout);
		setupSize();
		Intent intent = getIntent();
		String message = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
		TextView text = (TextView) findViewById(R.id.alarm_message);
		text.setText(message);
		Button stopAlarm = (Button) findViewById(R.id.stop_alarm_button);
		stopAlarm.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mMediaPlayer.stop();
				finish();
				return false;
			}
		});
		playSound(this, getAlarmUri());
	}

	/**
	 * Setup the size of the dialog in the screen.
	 */
	private void setupSize() {
		LayoutParams params = getWindow().getAttributes();
		params.width = getScreenSize().widthPixels - 100;
		getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) params);
	}

	/**
	 * Get the actual screen size of the device.
	 * 
	 * @return DisplayMetrics Object that contains all the metrics for the
	 *         actual screen.
	 */
	private DisplayMetrics getScreenSize() {
		// TODO Need some more logic if the screen is from tablet, because that
		// case
		// the dialog will be very big.
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics;
	}

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("OOPS");
		}
	}

	// Get an alarm sound. Try for an alarm. If none set, try notification,
	// Otherwise, ringtone.
	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}
}
