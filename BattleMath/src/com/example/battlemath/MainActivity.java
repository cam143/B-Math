package com.example.battlemath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
	
	/**** MUSIC MODE ****/
	public static boolean MusicON = true;
	
	/**** START GAME TIMER ****/
	private CountDownTimer mCountDownTimer;
	
	/**** BattleMathService ****/
	protected static BattleMathService mBattleMathService = null;
		
	/**** BlueTooth Adapter ****/
	private BluetoothAdapter mBluetoothAdapter;
	
	/**** Enable BlueTooth Request ****/
	private static final int REQUEST_ENABLE_BT = 0;
	
	/**** Text Design ****/
	private TextView myStatus;
	private Typeface tf;
	
	private ProgressBar progBar;
	private Switch mSwitch;
	private ImageView visible;
	
	/**** Button ****/
	private Button startOnePlayer;
	private Button startTwoPlayer;
	private Button findMatch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myStatus = (TextView)findViewById(R.id.txtStatus);
		progBar = (ProgressBar)findViewById(R.id.progressBar);
		mSwitch = (Switch) findViewById (R.id.mSwitch);
		
		if(mSwitch.isChecked()){
			MainActivity.MusicON = true;
			playMusic(MainActivity.MusicON);
		}else{
			MainActivity.MusicON = false;
		}
		
		startOnePlayer = (Button) findViewById (R.id.btnStartGame1player);
		startTwoPlayer = (Button) findViewById (R.id.btnStartGame2player);
			 findMatch = (Button) findViewById (R.id.btnFindMatch);
			 
		visible = (ImageView) findViewById (R.id.visible_eye);
		
		progBar.setVisibility(View.GONE);
		
		// Text Animation
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		setTextDesign();
		
		// OnClick Listener
	    startOnePlayer.setOnClickListener((View.OnClickListener)this);
		startTwoPlayer.setOnClickListener((View.OnClickListener)this);
		findMatch.setOnClickListener((View.OnClickListener)this);
	    visible.setOnClickListener((View.OnClickListener)this);
	    mSwitch.setOnCheckedChangeListener(this);
	    
	    BluetoothMusic = MediaPlayer.create(this, R.raw.bluetooth);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		soundClick(true);
		
	    switch (buttonView.getId()){
	        case R.id.mSwitch:

            if(isChecked == true) {
                	MusicON = true;
                	
                	playMusic(MainActivity.MusicON);
            }else{
                 	MusicON = false;
                 	
                 	backgroundMusic.stop();
        			backgroundMusic.reset();
            	}
            break;
         }
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		if(!backgroundMusic.isPlaying() && MainActivity.MusicON){
			playMusic(MainActivity.MusicON);
		}
		
		/** If BT is not on, request that it be enabled **/ 
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the BattleMath session
        } else if (mBattleMathService == null) {
        	setupBattleMathActivity();
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.stop();
			backgroundMusic.reset();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(!backgroundMusic.isPlaying() && MainActivity.MusicON){
			playMusic(MainActivity.MusicON);
		}
		
		/* Performing this check in onResume() covers the case in which BT was
		 * not enabled during onStart(), so we were paused to enable it...
		 * onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		 */
        if (mBattleMathService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBattleMathService.getState() == BattleMathService.STATE_NONE) {
                // Start the BattleMath services
            	mBattleMathService.start();
            }
        }
    }
	
	@Override
	protected void onDestroy() {
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.stop();
			backgroundMusic.reset();
			backgroundMusic.release();
		}
		
		if (mBattleMathService != null) {
			mBattleMathService.stop();
        }
		while(BluetoothMusic.isPlaying()){}
		System.exit(0); // Close All Threads
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case REQUEST_ENABLE_BT:
		
				if(resultCode == RESULT_CANCELED){
					Toast.makeText(MainActivity.this, "Please Enable BlueTooth", Toast.LENGTH_LONG).show();
					soundBluetooth(MainActivity.MusicON);
					finish();
				}else{
					setupBattleMathActivity();
				}
			break;
		}
	}
	
	/**** ONCLICK LISTENER ****/
	@Override
	public void onClick(View v) {
		
		soundClick(MainActivity.MusicON);
		
		switch(v.getId()){
		
		case R.id.btnStartGame1player:
			StartGame.SinglePlayer = true;
			startActivity();
		break;
		
		
		case R.id.btnStartGame2player:
			/**** Check that we're actually connected before trying anything ****/
	        if (mBattleMathService.getState() != BattleMathService.STATE_CONNECTED) {
	            Toast.makeText(getApplicationContext(), "NOT CONNECTED", Toast.LENGTH_SHORT).show();
	            soundNotConnected(MainActivity.MusicON);
	            return;
	        }
	        /**** Setup Question ****/
	        soundGameStart(MainActivity.MusicON);
	        generateQuestion();
	        timer();
		break;
		
		
		case R.id.btnFindMatch:
			Intent intent = new Intent(this, FindMatch.class);
			startActivity(intent);
		break;
	
		
		case R.id.visible_eye:
			if (mBluetoothAdapter.getScanMode() !=
            	BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
			}else{
				Toast.makeText(getApplicationContext(), "YOUR DEVICE IS VISIBLE", Toast.LENGTH_SHORT).show();
				soundVisible(MainActivity.MusicON);
			}
		break;
		}
	}

	/**** Link @StarGame.class (QUESTIONS) ****/
	protected final void generateQuestion(){
		
		// Generate 30 Questions Link @Game.class
		for(int x = 0 ; x < 30 ; x++){
			
			Game game = new Game();
			String Quest = Constants.QUESTION + "\n" + game.Get_Expression() + "\n" + game.Get_Operator();
			
			// For Server
			Message msg = mHandler.obtainMessage(Constants.MESSAGE_QUESTION);
	        Bundle bundle = new Bundle();
	        bundle.putString(Constants.QUESTION, Quest);
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);
	        
	        // For Client
	        String message = Quest;
	        byte[] send = message.getBytes();
	        mBattleMathService.write(send);
	    }
	}

	/**** 4 Seconds CountDown Timer to Start The Game ****/
	private final void timer(){
		// 60 Seconds Timer
		mCountDownTimer = new CountDownTimer(4000, 1000) {
			public void onTick(long millisUntilFinished) {
				Toast.makeText(MainActivity.this, "GAME STARTS IN: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
				soundTick(MainActivity.MusicON);
			}

			public void onFinish() {
				
				// Start GAME
				
				String message = Constants.START_GAME;
				byte[] send = message.getBytes();
				mBattleMathService.write(send);
				
				StartGame.SinglePlayer = false;
				startActivity();
				
				mCountDownTimer.cancel();
			}
		}.start();
	}
	
	/**** Initialize Text Designs @assets/fonts ****/
	private final void setTextDesign(){
		tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/digital.ttf");
		myStatus.setTypeface(tf);
	}
	
	/**** Initialize the BattleMathService to perform BlueTooth connections ****/
    private final void setupBattleMathActivity() {
		mBattleMathService = new BattleMathService(mHandler);
	}

	/**** Start Game Activity ****/
	private final void startActivity(){
		Intent myIntent = new Intent(MainActivity.this, StartGame.class);
        startActivity(myIntent);
	}
    
	/**** The Handler that gets information back from the BattleMathService.class ****/
    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
        private String mConnectedDeviceName = null;

		@Override
        public void handleMessage(Message msg) {
            
        	switch (msg.what) {
        	
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BattleMathService.STATE_CONNECTED:
                            	myStatus.setText("STATUS: Connected to " + mConnectedDeviceName);
                            	progBar.setVisibility(View.GONE);
                            	soundReady(MainActivity.MusicON);
                            	
                            break;
                        case BattleMathService.STATE_CONNECTING:
                        		myStatus.setText("STATUS: Connecting");
                        		progBar.setVisibility(View.VISIBLE);
                            break;
                        case BattleMathService.STATE_LISTEN:
                        case BattleMathService.STATE_NONE:
                        		myStatus.setText("STATUS: Not Connected");
                        		progBar.setVisibility(View.GONE);
                            break;
                    }
                break;
                    
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                   
                    	if(readMessage.equals(Constants.START_GAME)){
                    		try{
                    			StartGame.endStartGameActivity();
                          	   	TwoPlayerResult.endTwoPlayerResult();
                    		}catch(Exception e){}
                    		
                    		StartGame.SinglePlayer = false;
                    		startActivity();
                    		
                    	}else if(readMessage.equals(Constants.GAME_OVER)){
                    		try{			
                         	   StartGame.endStartGameActivity();
                            }catch(Exception e){}
                    	
                    	}else{
                    		// Client Link @StartGame.class arrayList
                        	
                    		try{
                    			
                    			String tempQuestion[] = readMessage.split("\n");
                    			if(tempQuestion[0].equals(Constants.QUESTION)){
                    				
                    				try{
                    					StartGame.arrayList.add(readMessage);
                    				}catch(Exception e){
                    					Toast.makeText(getApplicationContext(), "ERROR 666" ,
                                       		   Toast.LENGTH_SHORT).show();
                    				}
                    			}
                    			
                    			String tempAnswer[] = readMessage.split("\t");
                    			if(tempAnswer[0].equals(Constants.ANSWER)){
                    				try{
                    					TwoPlayerResult.enemyStat.add(tempAnswer[1]);
                    				}catch(Exception e){
                    					Toast.makeText(getApplicationContext(), "ERROR 999" ,
                                        		   Toast.LENGTH_SHORT).show();
                     				}
                    			}
                    			
                    		}catch(Exception e){
                    			Toast.makeText(getApplicationContext(), "ERROR 101" ,
                               		   Toast.LENGTH_SHORT).show();
                    		}
                    	
                    	}
                    	
                break;
                    
                case Constants.MESSAGE_DEVICE_NAME:
                    	// save the connected device's name
                    	mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    	TwoPlayerResult.enemyName = mConnectedDeviceName;
                break;
                    
                case Constants.MESSAGE_TOAST:
                       Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                    		   Toast.LENGTH_SHORT).show();
                      
                       if(msg.getData().getString(Constants.TOAST).equals("CONNECTION LOST")){
                    	   soundConnectionLost(MainActivity.MusicON);
                       }else{
                    	   soundUnableConnect(MainActivity.MusicON);
                       }
                       
                       try{			
                    	   StartGame.endStartGameActivity();
                    	   TwoPlayerResult.endTwoPlayerResult();
                       }catch(Exception e){}
                   
                break;
                    
                case Constants.MESSAGE_QUESTION:
                    	// Server Link @StartGame.class arrayList
                		StartGame.arrayList.add(msg.getData().getString(Constants.QUESTION));
        		break;
        	}
        }
    };
    
    /**** SOUND BACKGRAOUND ****/
	private MediaPlayer backgroundMusic;
	private void playMusic(boolean on){
		backgroundMusic = MediaPlayer.create(this, R.raw.menu);
		if(on){
			backgroundMusic.setLooping(true);
			backgroundMusic.start();
		}
	}
	
	/**** SOUND READY ****/
	private void soundReady(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.ready);
			Music.start();
		}
	}
	
	/**** SOUND TICK ****/
	private void soundTick(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.tick);
			Music.start();
		}
	}
	
	/**** SOUND NO CONNECTION ****/
	private void soundNotConnected(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.not_connected);
			Music.start();
		}
	}
	
	/**** SOUND LOST CONNECTION ****/
	private void soundConnectionLost(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.conn_lost);
			Music.start();
		}
	}
	
	/**** SOUND UNABLE CONNECTION ****/
	private void soundUnableConnect(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.unable_connect);
			Music.start();
		}
	}
		
	/**** SOUND START GAME ****/
	private void soundGameStart(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.game_starting);
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
	
	/**** SOUND VISIBLE ****/
	private void soundVisible(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.device_visible);
			Music.start();
		}
	}
	
	/**** SOUND BLUETOOTH ****/
	MediaPlayer BluetoothMusic;
	private void soundBluetooth(boolean on){
		if(on){
			BluetoothMusic.start();
		}
	}
}