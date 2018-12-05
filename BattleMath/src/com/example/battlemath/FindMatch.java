package com.example.battlemath;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class FindMatch extends Activity implements AdapterView.OnItemClickListener{

   /**
     * Member object for the BattleMath services
     */
	private static String Mac_Address;
	private ProgressBar progBar;
	private BluetoothAdapter mBluetoothAdapter;
	
	private ArrayList <String> deviceName; //Data source for PAIRED DEVICE
	private ArrayAdapter<String> mArrayAdapter; //Data source for OTHER DEVICE
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_match);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
		progBar = (ProgressBar)findViewById(R.id.progressBar);
		progBar.setVisibility(View.GONE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		playMusic(MainActivity.MusicON);
		
		// If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
        	mBluetoothAdapter.cancelDiscovery();
        }
        
        /*** START DISCOVERY ****/
			paired();
			available();
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
			playMusic(MainActivity.MusicON);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(backgroundMusic.isPlaying() && MainActivity.MusicON){
			backgroundMusic.stop();
			backgroundMusic.reset();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		soundClick(MainActivity.MusicON);
		
		TextView temp = (TextView) view;
		
		try{
			
			String address[] = temp.getText().toString().split("\n");
			Mac_Address = address[1];
			
			//Start Connecting
	 		connectDevice();
	 		
	 		//Stop
	 		mBluetoothAdapter.cancelDiscovery();
	 		mReceiver.abortBroadcast();
			finish();
			
		}catch(Exception e){
			Toast.makeText(getApplicationContext(),"NO DEVICE", Toast.LENGTH_SHORT).show();
			soundNoDevice(MainActivity.MusicON);
		}
	}

	/**** START CONNECTING ****/
	private void connectDevice() {
    	// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(Mac_Address);
		
		try{
			// Attempt to connect to the device
			MainActivity.mBattleMathService.connect(device);
		}catch(Exception e){
        	Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
        }
	}
	
	/**** Paired Device ****/
	private void paired(){

        deviceName = new ArrayList<String>(); //Data Source

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                deviceName.add(device.getName() + "\n" + device.getAddress());
            }
        }else{
        	deviceName.add("No Paired Device");
        }

        ArrayAdapter <String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.mylistview , deviceName);
        ListView list = (ListView) findViewById(R.id.listPaired);
        list.setAdapter(mArrayAdapter);
        list.setOnItemClickListener(this);;
    }

	/**** Available Device ****/
	private void available(){
				
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		mBluetoothAdapter.startDiscovery();
		mArrayAdapter = new ArrayAdapter<String>(this, R.layout.mylistview);
	    ListView list = (ListView) findViewById(R.id.listAvailable);
	    list.setAdapter(mArrayAdapter);
	    list.setOnItemClickListener(this);
	}
	
	/**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            // If it's already paired, skip it, because it's been listed already
	            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	    	    }
	        }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
               //discovery starts, we can show progress dialog or perform other tasks
	        	progBar.setVisibility(View.VISIBLE);
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismiss progress dialog
            	progBar.setVisibility(View.GONE);
            	if(mArrayAdapter.isEmpty()){
            		mArrayAdapter.add("No Device Found");
            	}
            }
	    }
	};
	
	/**** BACKGROUND SOUND ****/
	private MediaPlayer backgroundMusic;
	private void playMusic(boolean on){
		backgroundMusic = MediaPlayer.create(this, R.raw.battle);
		if(on){
			backgroundMusic.setLooping(true);
			backgroundMusic.start();
		}
	}
	
	/**** SOUND CLICK ****/
	private void soundClick(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.click);
			Music.start();
		}
	}
	
	/**** SOUND NO DEVICE ****/
	private void soundNoDevice(boolean on){
		if(on){
			MediaPlayer Music = MediaPlayer.create(this, R.raw.no_device);
			Music.start();
		}
	}
}