package com.liveplatform.qalivestuff;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Data.IConstants;
import SupportClasses.CommonUI;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordAudio extends Activity {

	/* all Views attached with java codes */
	@BindView(R.id.start)
	Button startBtn;
	@BindView(R.id.save)
	Button saveBtn;
	@BindView(R.id.imganim)
	WebView imganim;
	@BindView(R.id.rl)
	RelativeLayout rl;
	@BindView(R.id.play)
	Button playBtn;
	@BindView(R.id.micImage)
	ImageView recordim;
	@BindView(R.id.text1)
	TextView text;


	/* variables to be used by this class */
	private CommonUI ci;
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	private Boolean isrecording = false;
	private Boolean isplaying = false;
	private Intent returnresp;
	SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
	String currentDateandTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);
		ci=new CommonUI(RecordAudio.this);
		returnresp = this.getIntent();
		ButterKnife.bind(this);
		currentDateandTime = sdf.format(new Date());
		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + IConstants._AFN + "_"
				+ currentDateandTime + IConstants._AFNEXT;

	}

	/*this method will used to trigger another method which will put response data in returning class*/
	@OnClick(R.id.save)
	public void saveF() {
		returnresultToIntent(outputFile);
	}
	/*method will either stop or start playing by toggling the condition*/
	@OnClick(R.id.play)
	public void startP() {
		if (!isplaying) {
			isplaying = true;
			playBtn.setText("Stop Playing");
			imganim.setVisibility(View.VISIBLE);
			play();
		} else {
			isplaying = false;
			playBtn.setText("Play");
			imganim.setVisibility(View.GONE);
			stopPlay();
		}
	}
	/*method will either start or stop recording*/
	@OnClick(R.id.start)
	public void startR() {
		if (!isrecording) {
			isrecording = true;
			startBtn.setText("Stop Recording");
			start();
			imganim.loadDataWithBaseURL("file:///android_res/drawable/", "<img src='audio.gif' />", "text/html",
					"utf-8", null);
			imganim.setVisibility(View.VISIBLE);
		} else {
			isrecording = false;
			startBtn.setText("Record");
			stop();
			imganim.setVisibility(View.GONE);
		}
	}
	/*method will start recording */
	@SuppressWarnings("deprecation")
	public void start() {
		if (myRecorder != null) {
			myRecorder.release();
		}
		myRecorder = new MediaRecorder();
		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myRecorder.setOutputFile(outputFile);

		try {
			myRecorder.prepare();
			myRecorder.start();
		} catch (IOException e) {
			ci.logit(IConstants._LOGERROR, "io problems while preparing " + e.getMessage());
		} catch (IllegalStateException e) {
			ci.logit(IConstants._LOGERROR, "io problems while preparing " + e.getMessage());
		}

		text.setText("Recording...");

		rl.setBackgroundColor(getResources().getColor(R.color.white));
		text.setTextColor(Color.RED);
		playBtn.setEnabled(false);
		recordim.setBackgroundResource(R.drawable.record);
	}

	/* will stop recording if recording is going on */
	@SuppressWarnings("deprecation")
	public void stop() {
		try {
			myRecorder.stop();
			myRecorder.release();
			myRecorder = null;
			startBtn.setText("Record");
			isrecording = false;
			playBtn.setEnabled(true);
			text.setText("Stopped recording");
			text.setTextColor(getResources().getColor(R.color.LScolor));
			//rl.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
			recordim.setBackgroundResource(R.drawable.record);
			saveBtn.setEnabled(true);
		} catch (IllegalStateException e) {
			// it is called before start()
			if (IConstants.ILE)
				e.printStackTrace();
		} catch (RuntimeException e) {
			// no valid audio/video data has been received
			if (IConstants.ILE)
				e.printStackTrace();
		}
	}
	/* will play recorded audio file*/
	@SuppressWarnings("deprecation")
	public void play() {
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(outputFile);
			myPlayer.prepare();
			myPlayer.start();
			myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlay();
				}
			});
			startBtn.setEnabled(false);
			text.setText("Playing...");
			text.setTextColor(Color.GREEN);
			rl.setBackgroundColor(getResources().getColor(R.color.white));
			recordim.setBackgroundResource(R.drawable.record);
		} catch (Exception e) {
			if (IConstants.ILE)
				e.printStackTrace();
		}
	}
	/* will stop playing the audio file if already playing */
	@SuppressWarnings("deprecation")
	public void stopPlay() {
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				myPlayer.release();
				myPlayer = null;
				playBtn.setText("PLay");
				imganim.setVisibility(View.GONE);
				isplaying = false;
				startBtn.setEnabled(true);
				text.setText("Stopped playing");
				text.setTextColor(getResources().getColor(R.color.LScolor));
				//rl.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
				recordim.setBackgroundResource(R.drawable.record);

			}
		} catch (Exception e) {
			if (IConstants.ILE)
				e.printStackTrace();
		}
	}

	/*
	 * this method will used to put the response data back to the calling class
	 */
	private void returnresultToIntent(String data) {
		ci.logit(IConstants._LOGAPPNAME, data);
		returnresp.putExtra(IConstants._RES, data);
		this.setResult(Activity.RESULT_OK, returnresp);
		finish();
	}
}
