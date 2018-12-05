package com.example.battlemath;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TwoPlayerResult extends Activity{
	
	/**** Activity TwoPlayerResult ****/
	private static Activity activity = null;
	
	/**** End TwoPlayerResult Activity ****/
	protected static final void endTwoPlayerResult(){
		activity.finish();
	}
	
	/**** Data Source = ME ****/
	protected static ArrayList <String> myStat;
	
	/**** Data Source = ENEMY ****/
	protected static ArrayList <String> enemyStat;
	
	/**** Enemy Name ****/
	protected static String enemyName = null;
	
	private int myScore;
	private int enemyScore;
	
	private TextView txtStat;
	
	private boolean isWinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_player_result);
		
		/**** ACTIVITY ****/
		activity = TwoPlayerResult.this;
		
		/**** STATS ****/
		txtStat = (TextView) findViewById (R.id.txtStat);
		
		/**** SCORES ****/
		myScore = 0;
		enemyScore = 0;
		
		Button btnMainMenu = (Button) findViewById (R.id.btnMain);
		
		btnMainMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				soundClick(MainActivity.MusicON);
				finish();
			}
		});

		/**** SET STATS ****/
			setStat();
			enemyStats();
			myStats();
			
		Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Minecrafter.Reg.ttf");
		txtStat.setTypeface(tf);
		
		/**** BACKGROUND MUSIC ****/
		playMusic(MainActivity.MusicON , isWinner);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.two_player_result, menu);
		return false;
	}
	
	@Override
	public void onBackPressed() {}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		backgroundMusic.stop();
		backgroundMusic.release();
	}

	private void myStats(){

		ArrayAdapter <String> mArrayAdapter = new ArrayAdapter<String>(this,R.layout.mytextview , myStat);
        ListView list = (ListView) findViewById(R.id.listViewME);
        list.setAdapter(mArrayAdapter);
    }
	
	private void enemyStats(){
		
		TextView textViewEMENY = (TextView) findViewById (R.id.textViewEMENY);
		textViewEMENY.setText(enemyName);
		
		ArrayAdapter <String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.mytextview , enemyStat);
        ListView list = (ListView) findViewById(R.id.listViewENEMY);
        list.setAdapter(mArrayAdapter);
    }
	
	private void setStat(){
		
		if((!myStat.isEmpty()) && (!enemyStat.isEmpty())){
			
			int me = myStat.size();
			int enemy = enemyStat.size();
			
			int max;
			int min;
			
			if(me > enemy){
				max = me;
				min = enemy;
				
				calculate(max, min , myStat , enemyStat , true);
				
			}else{
				max = enemy;
				min = me; 
				
				calculate(max, min , enemyStat , myStat , false);
			}
		
		}else if ((!myStat.isEmpty()) && (enemyStat.isEmpty())){
			myScore++;
			
		}else if ((myStat.isEmpty()) && (!enemyStat.isEmpty())){
			enemyScore++;
		}
		
		if(myScore == enemyScore){
			txtStat.setText("DRAW");
			draw(MainActivity.MusicON);
			isWinner = false;
			
		}else if(myScore > enemyScore){
			txtStat.setText("YOU WIN");
			winner(MainActivity.MusicON);
			isWinner = true;
			
		}else{
			txtStat.setText("YOU LOSE");
			looser(MainActivity.MusicON);
			isWinner = false;
		}
	}
	
	/**** BACKGROUND SOUND ****/
	private MediaPlayer backgroundMusic;
	private void playMusic(boolean on , boolean win){
		try{	backgroundMusic.release();	}catch(Exception e){}
		
		if(win)
			backgroundMusic = MediaPlayer.create(this, R.raw.victory);
		else
			backgroundMusic = MediaPlayer.create(this, R.raw.lose);
		
		if(on){
			backgroundMusic.setLooping(true);
			backgroundMusic.start();
		}
	}
	
	/**** ME HIGH = Max = ME ****/
	private void calculate(int max, int min , ArrayList <String> highStat , ArrayList <String> lowStat, boolean meHigh){
		
		for(int x = 0 ; x < max ; x++){
			
			String tempX[] = highStat.get(x).toString().split("\n");
			String tempY[] = tempX[1].split(" ");
			
			int high = Integer.parseInt(tempY[1]);
			int low;
			
			if(x < min){
				
				tempX = lowStat.get(x).toString().split("\n");
				tempY = tempX[1].split(" ");
				
				low = Integer.parseInt(tempY[1]);
			
				if(high < low && high != low){
					
					/**** MY SCORE > ENEMY SCORE && meHigh(true) ****/
					if(meHigh){
						myScore++;
					}else{
					
					/**** MY SCORE > ENEMY SCORE && meHigh(false) ****/	
						enemyScore++;
					}
					
				}else{
					
					/**** MY SCORE < ENEMY SCORE && meHigh(true) ****/
					if(meHigh){
						enemyScore++;
					}else{
					
					/**** MY SCORE < ENEMY SCORE && meHigh(false) ****/
						myScore++;
					}
				}
			
			}else{
				
				/**** MY SCORE INDEX > ENEMY SCORE INDEX && meHigh(true) ****/
				if(meHigh){
					myScore++;
					
				}else{
				/**** MY SCORE INDEX > ENEMY SCORE INDEX && meHigh(false) ****/
					enemyScore++;
				}
			}
		}
	}
	
	/**** SOUND WIN ****/
	private void winner(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.you_win);
			Music.start();
			
			MediaPlayer Music1 = MediaPlayer.create(this, R.raw.victory);
			Music1.start();
		}
	}
	
	/**** SOUND LOSE ****/
	private void looser(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.you_lose);
			Music.start();
			
			MediaPlayer Music1 = MediaPlayer.create(this, R.raw.lose);
			Music1.start();
		}
	}
	
	/**** SOUND DRAW ****/
	private void draw(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.draw);
			Music.start();
			
			MediaPlayer Music1 = MediaPlayer.create(this, R.raw.lose);
			Music1.start();
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