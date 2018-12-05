package com.example.battlemath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SinglePlayerResult extends Activity implements View.OnClickListener{

	private TextView HighScore;
	private TextView YourScore;
	private Button btnPlayAgain;
	private Button btnMainMenu;
	
	private boolean isHighScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_player_result);
		
		btnPlayAgain = (Button) findViewById (R.id.btnPlayAgain);
		btnMainMenu = (Button) findViewById (R.id.btnMainMenu);
		HighScore = (TextView) findViewById (R.id.txtHighScore);
		YourScore = (TextView) findViewById (R.id.txtYourScore);
		
		btnPlayAgain.setOnClickListener((View.OnClickListener)this);
		btnMainMenu.setOnClickListener((View.OnClickListener)this);
		
		Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Minecrafter.Reg.ttf");
		HighScore.setTypeface(tf);
		YourScore.setTypeface(tf);
		
		String score = null;
		String hScore = null;
		
		// Get Values @ StartGame.class
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    score = extras.getString("score");
		    hScore = extras.getString("high_score");
		}
		
		isHighScore = false;
		YourScore.setText("YOUR SCORE: " + score);
		if(hScore.equals("false")){
			HighScore.setVisibility(View.GONE);
			isHighScore = true;
		}
		
		gameOver(MainActivity.MusicON);
		playMusic(MainActivity.MusicON);
	
	}
	
	@Override
	public void onBackPressed() {}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.stop();
			backgroundMusic.release();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(!backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.start();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.pause();
		}
	}


	@Override
	public void onClick(View v) {
		soundClick(MainActivity.MusicON);
		Intent myIntent;
		switch(v.getId()){
		case R.id.btnPlayAgain:
				myIntent = new Intent(this, StartGame.class);
				startActivity(myIntent);
			break;
		}
		finish();
	}
	
	/**** BACKGROUND SOUND ****/
	private MediaPlayer backgroundMusic;
	private void playMusic(boolean on){
		
		if(isHighScore){
			backgroundMusic = MediaPlayer.create(this, R.raw.victory);
		}else{
			backgroundMusic = MediaPlayer.create(this, R.raw.lose);
		}
		
		if(on){
			backgroundMusic.setLooping(false);
			backgroundMusic.start();
		}
	}
	
	/**** SOUND GAME OVER ****/
	private void gameOver(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.game_over);
			Music.start();
		}
	}
	
	/**** SOUND CLICK ****/
	private void soundClick(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.click);
			Music.start();
		}
	}
}
