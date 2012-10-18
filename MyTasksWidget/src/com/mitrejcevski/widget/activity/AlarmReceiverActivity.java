package com.mitrejcevski.widget.activity;

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

import java.io.IOException;

/**
 * This activity is used for the alarms created from the application.
 * 
 * @author jovche.mitrejchevski
 * 
 */
// TODO It`s not fully implemented! Broadcast receiver is needed!
public class AlarmReceiverActivity extends Activity implements OnTouchListener {

	private MediaPlayer mMediaPlayer;
	private TextView mTextView;
	private Button mDismissButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_layout);
		setupSize();
		initialize();
		playSound(this, getAlarmUri());
	}

	/**
	 * Initializes the layout of the activity.
	 */
	private void initialize() {
		Intent intent = getIntent();
		String message = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
		mTextView = (TextView) findViewById(R.id.alarm_message);
		mTextView.setText(message);
		mDismissButton = (Button) findViewById(R.id.stop_alarm_button);
		mDismissButton.setOnTouchListener(this);
	}

	/**
	 * Setup the size of the dialog in the screen.
	 */
	private void setupSize() {
		LayoutParams params = getWindow().getAttributes();
		params.width = getScreenSize().widthPixels - 100;
		getWindow().setAttributes(params);
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

	/**
	 * Starts to play sound.
	 * 
	 * @param context
	 * @param alert
	 */
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

	/**
	 * Get the default alarm/ring tone notification sound.
	 * 
	 * @return
	 */
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mMediaPlayer.stop();
		finish();
		return false;
	}
}
