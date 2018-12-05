package com.example.battlemath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartGame extends Activity implements View.OnClickListener {
	
	/**** Activity StarGame ****/
	private static Activity activity = null;
	
	/**** Single Player if True ****/
	protected static boolean SinglePlayer = true;
	
	/**** Operators ****/
	private final String operator[] = {"+","-","*","/"};
	
	/**** Score / Attempt ****/
	private int gameScore;
	
	/**** Text Design ****/
	private Typeface tf;
	private TextView score;
	private TextView highScore;
	private TextView question;
	private TextView txtTime;
	
	/**** Moving Buttons ****/
	private Button button[] = new Button[6];
	private CountDownTimer mGameCountDownTimer;
	private RelativeLayout relative;
	
	/**** Single Player Variables ****/
	private ImageView hp[] = new ImageView[3];
	private Game game;
	private Random generator;
	private int high_Score;
	private int healthBar;
	
	/**** Two Player Variables ****/
	protected static ArrayList<String> arrayList = new ArrayList<String>();
    private CountDownTimer mCountDownTimer;
    private String answer;
	private String expression;
	private int rank;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_game);
		
		playMusic(MainActivity.MusicON);
		
		activity = StartGame.this;
		tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/digital.ttf");
			
		// Initialize ID
		question = (TextView) findViewById (R.id.txtQuestion);
		relative = (RelativeLayout) findViewById (R.id.relativeLayout);
		score = (TextView) findViewById (R.id.txtScore);
		highScore = (TextView) findViewById (R.id.txtHighScore);
		txtTime = (TextView) findViewById (R.id.txtTimer);
		
		button[0] = (Button) findViewById (R.id.Button1);
		button[1] = (Button) findViewById (R.id.Button2);
		button[2] = (Button) findViewById (R.id.Button3);
		
		button[3] = (Button) findViewById (R.id.Button4);
		button[4] = (Button) findViewById (R.id.Button5);
		button[5] = (Button) findViewById (R.id.Button6);
		
		hp[0] = (ImageView) findViewById (R.id.imageView1);
		hp[1] = (ImageView) findViewById (R.id.imageView2);
		hp[2] = (ImageView) findViewById (R.id.imageView3);
		
		// Add on Click Listener
		button[0].setOnClickListener((View.OnClickListener)this);
		button[1].setOnClickListener((View.OnClickListener)this);
		button[2].setOnClickListener((View.OnClickListener)this);
		button[3].setOnClickListener((View.OnClickListener)this);
		button[4].setOnClickListener((View.OnClickListener)this);
		button[5].setOnClickListener((View.OnClickListener)this);
		
		question.setTypeface(tf);
		txtTime.setTypeface(tf);
		score.setTypeface(tf);
		highScore.setTypeface(tf);
		
		// Start Moving Layout
		GameTimer();
		mGameCountDownTimer.start();
		
		// Set Score to Zero
		gameScore = 0;
			 rank = 0;
		
		// Set Health Bar to Zero
		healthBar = 3;
		
		// Game Mode
		if(SinglePlayer){
			readFile();
			game = new Game();
			question.setText(game.Get_Expression());
			OnePlayerGame();
		}else{
			TwoPlayerUpdate();
			TwoPlayerGame();
			timer();
			
			// Link @TwoPlayerResult
			TwoPlayerResult.myStat = new ArrayList<String>();
			TwoPlayerResult.enemyStat = new ArrayList<String>();
		}
	}

	@Override
	protected void onStop() {
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.pause();
		}
		
		try{
			mGameCountDownTimer.cancel();
			mGameCountDownTimer.cancel();
		}catch(Exception e){}
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		
		if(!backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.start();
		}
		
		mGameCountDownTimer.start();
		mGameCountDownTimer.start();
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.stop();
			backgroundMusic.release();
		}
		
		try{
			mGameCountDownTimer.cancel();
			mGameCountDownTimer.cancel();
			
			if(!SinglePlayer){
				mCountDownTimer.cancel();
			}
		}catch(Exception e){}
		
		arrayList = new ArrayList<String>();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if(!SinglePlayer){
			soundDontQuit(MainActivity.MusicON);
			Toast.makeText(StartGame.this, "Don't Quit", Toast.LENGTH_LONG).show();
		}else{
			if(MainActivity.MusicON){
				backgroundMusic.stop();
				backgroundMusic.reset();
			}super.onBackPressed();
		}
	}
	
	/**** OnClick Listener ****/
	@Override
	public void onClick(View v) {
		
	    switch(v.getId()){

	      case R.id.Button1:
	        	button[0].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 0 , SinglePlayer);
	      break;
	      
	      case R.id.Button2:
	        	button[1].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 1 , SinglePlayer);
	      break;
	      
	      case R.id.Button3:
	        	button[2].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 2 , SinglePlayer);
	      break;
	      
	      case R.id.Button4:
	        	button[3].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 3 , SinglePlayer);
	      break;
	      
	      case R.id.Button5:
	        	button[4].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 4 , SinglePlayer);
	      break;
	      
	      case R.id.Button6:
	        	button[5].setVisibility(View.GONE);
	        	isCheckSinglePlayer(button, 5 , SinglePlayer);
	      break;
	    }
	}
	
	/**** Count Down Timer for Moving Layout ****/
	private final void GameTimer(){
			
		// Screen Dimension
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
			
		int speed = 1;
			
		if(height <= 350 && width <= 250){
			speed = 40;
			
			for(int x = 0 ; x < 6 ; x++){
				button[x].setTextSize(20);
			}
		}
			
		mGameCountDownTimer =	new CountDownTimer(999999999, speed) {
		     public void onTick(long millisUntilFinished) {
			    	 
		    	 moveButton(button, 0);
		    	 moveButton(button, 1);
		    	 moveButton(button, 2);
		    	 moveButton(button, 3);
		    	 moveButton(button, 4);
		 		 moveButton(button, 5);
		     }
			     
		     public void moveButton(Button button[], int index){
		    	 if(button[index].getY() >= relative.getHeight()){
		    		  	generator = new Random();
		    		 	button[index].setText(operator[randomNumber(0,4)]);
		    		  	button[index].setY(0);
		    		  	
		    		  	button[index].setVisibility(View.VISIBLE);
		    		  	
		    		  	if(rank >= 30 && !SinglePlayer)
		    		  		button[index].setVisibility(View.GONE);
		    		  	
		 			}else{
		 				button[index].setY(button[index].getY()+1);
		 			}
		     }
			     
 		     public void onFinish() {
		    	 Toast.makeText(StartGame.this, "666", Toast.LENGTH_LONG).show();
		     }
		  }.start();
	}
	
	/**** Stop Activity StartGame****/
	public static final void endStartGameActivity(){
		activity.finish();
	}
		
	/**** Check Answers ****/
	private final void isCheckSinglePlayer(Button button[], int index , boolean SinglePlayer){
		
		//Single Player
		if(SinglePlayer){
			if(button[index].getText().toString().equals(game.Get_Operator())){
				
				soundCorrect(MainActivity.MusicON);
				
				gameScore++;
				game = new Game();
				question.setText(game.Get_Expression());
			}else{
				
				soundWrong(MainActivity.MusicON);
				
				gameScore--;
				healthBar--;
				
				String hScore = "false";
				
				if(healthBar == 0){
					if(gameScore > high_Score){
						writeFile(Integer.toString(gameScore));
						hScore = "true";
					}
					
					finish();
					//Send Value @ SinglePlayerResult.class
					Intent myIntent = new Intent(getApplicationContext(), SinglePlayerResult.class);
			        myIntent.putExtra("score", Integer.toString(gameScore));
			        myIntent.putExtra("high_score",hScore);
			        startActivity(myIntent);
				}
			}
			OnePlayerGame();
		
		// Two Player
		}else{
			
			if(button[index].getText().toString().equals(answer)){
				
				soundCorrect(MainActivity.MusicON);
				String me = expression + "\n" + "Attempt: " + gameScore;
				
				// For Me
				TwoPlayerResult.myStat.add(me);
				
		        // For Client
				String Ans = Constants.ANSWER + "\t" + me;
				byte[] send = Ans.getBytes();
		        MainActivity.mBattleMathService.write(send);
				
				rank++;
				TwoPlayerUpdate();
				
				/*** DONE IF RANK IS GREATER THAN OR EQUAL 30 ****/
				if(rank >= 30){
					question.setText("WAITING FOR OTHER PLAYER...");
					for(int x = 0 ; x < 6 ; x++){
						button[x].setVisibility(View.GONE);
					}
				}
				
			}else{
				soundWrong(MainActivity.MusicON);
				gameScore++;
			}
			TwoPlayerGame();
		}
	}
	
	/*********************************************** ONE PLAYER ***********************************************/
	
	/**** Random Number Generator ****/
	private final int randomNumber(int min, int max) {
        return (int) (min + (generator.nextDouble() * (max - min)));
    }
	
	/**** Write High Score to TextFile high_score.txt ****/
	private final void writeFile(String empty) {
		// add-write text into file
		try {
			FileOutputStream fileout=openFileOutput("high_score.txt", MODE_PRIVATE);
			OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
			outputWriter.write(empty);
			outputWriter.close();
					
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**** Read Data from high_score.txt ****/
	private static final int READ_BLOCK_SIZE = 100;
	private final void readFile() {
		//reading text from file
		try {
			FileInputStream fileIn=openFileInput("high_score.txt");
			InputStreamReader InputRead= new InputStreamReader(fileIn);
					
			char[] inputBuffer= new char[READ_BLOCK_SIZE];
			String s="";
			int charRead;
					
			while ((charRead=InputRead.read(inputBuffer))>0) {
				// char to string conversion
				String readstring=String.copyValueOf(inputBuffer,0,charRead);
				s +=readstring;					
			}
				
			InputRead.close();
			highScore.setText("High Score: " + s);
			high_Score = Integer.parseInt(s);
			
		} catch (Exception e) {
			writeFile("0");
			readFile();
		}
	}

	/**** OnePlayer Health Bar ****/
	private final void OnePlayerGame(){
		txtTime.setVisibility(View.GONE);
		score.setText("SCORE: " + gameScore);
		for(int x = 0 ; x < 3 ; x++){
			if(x < healthBar){
				hp[x].setVisibility(View.VISIBLE);
			}else{
				hp[x].setVisibility(View.GONE);
			}
		}
	}
	
	/*********************************************** TWO PLAYER ***********************************************/
	
	private final void timer(){
		
		// 60 Seconds Timer
		mCountDownTimer = new CountDownTimer(60000, 1000) {
			public void onTick(long millisUntilFinished) {
				txtTime.setText("TIME REMAINING: " + millisUntilFinished / 1000);
			}

			public void onFinish() {
				Intent intent = new Intent(StartGame.this, TwoPlayerResult.class);
				startActivity(intent);
				
				mCountDownTimer.cancel();
				finish();
			}
		}.start();
	}
	
	/**** Update gameScore(Attempt), Expression, Answer ****/
	private final void TwoPlayerUpdate(){
		String temp[] = arrayList.get(rank).split("\n");
		expression = temp[1];
			answer = temp[2];
		question.setText(expression);
		gameScore = 1;
	}
	
	private final void TwoPlayerGame(){
		score.setText("ATTEMPT: " + gameScore);
		hp[0].setVisibility(View.GONE);
		hp[1].setVisibility(View.GONE);
		hp[2].setVisibility(View.GONE);
		highScore.setVisibility(View.GONE);
	}
	
	/********************************************* SOUND **********************************************/
	
	/**** BACKGROUND SOUND ****/
	private MediaPlayer backgroundMusic;
	private void playMusic(boolean on){
		backgroundMusic = MediaPlayer.create(this, R.raw.start_game);
		if(on){
			backgroundMusic.setLooping(true);
			backgroundMusic.start();
		}
	}
	
	/**** SOUND CORRECT ****/
	private MediaPlayer correctMusic;
	private void soundCorrect(boolean on){
		if(on){
			try{
				wrongMusic.stop();
				wrongMusic.release();
				wrongMusic.reset();
				
				correctMusic.stop();
				correctMusic.release();
				wrongMusic.reset();
			}catch(Exception e){}
			
			correctMusic = MediaPlayer.create(this, R.raw.correct);
			correctMusic.start();
		}
	}
	
	/**** SOUND WRONG ****/
	private MediaPlayer wrongMusic;
	private void soundWrong(boolean on){
		if(on){
			try{
				wrongMusic.stop();
				wrongMusic.release();
				wrongMusic.reset();
				
				correctMusic.stop();
				correctMusic.release();
				wrongMusic.reset();
			}catch(Exception e){}
			wrongMusic = MediaPlayer.create(this, R.raw.wrong);
			wrongMusic.start();
		}
	}
	
	/**** SOUND DON'T QUIT ****/
	private void soundDontQuit(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.dont_quit);
			Music.start();
		}
	}
}