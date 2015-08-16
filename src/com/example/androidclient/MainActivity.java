package com.example.androidclient;


import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//import java.net.SocketException;
//import java.net.URL;
//import java.net.URLConnection;
import java.net.UnknownHostException;

import java.net.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import test.MainActivity;





public class MainActivity extends Activity implements SensorEventListener{
	String value;
	float sensor;
	int UDP_SERVER_PORT = 12345,motorSpeed = 0;
	TextView textResponse;
	//EditText editTextAddress; //editTextPort; 
	//Button buttonConnect, buttonClear;
	Button buttonCh1,buttonCh2,buttonCh3,buttonCh4;
	//MyClientTask myClientTask;
	private float mLastX, mLastY, mLastZ;
	private boolean sendData = false;
	private boolean mInitialized; private SensorManager mSensorManager; private Sensor mAccelerometer; private final float NOISE = (float) 2.0;
	/** Called when the activity is first created. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		 	
			          buttonCh1 = (Button)findViewById(R.id.buttonCh1);
			          buttonCh2 = (Button)findViewById(R.id.buttonCh2);
			          //buttonCh3 = (Button)findViewById(R.id.buttonCh3);
			         // buttonCh4 = (Button)findViewById(R.id.buttonCh4);
			          textResponse = (TextView)findViewById(R.id.textView1);
			          
			          mInitialized = false;
			          mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			          mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			          mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			          /*
			          editTextAddress = (EditText)findViewById(R.id.address);
			          editTextPort = (EditText)findViewById(R.id.port);
			          buttonConnect = (Button)findViewById(R.id.connect);
			          buttonClear = (Button)findViewById(R.id.clear);
			          textResponse = (TextView)findViewById(R.id.response);
			          */
			          buttonCh1.setOnClickListener(buttonCh1OnClickListener);// On
			          buttonCh2.setOnClickListener(buttonCh2OnClickListener);//Off
			         // buttonCh3.setOnClickListener(buttonCh3OnClickListener);
			         // buttonCh4.setOnClickListener(buttonCh4OnClickListener);
			          textResponse.setText("Off");
			      
			         }// end onCret
	
	//acc code
	
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}
	
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// can be safely ignored for this demo
	}
		
	@Override
		public void onSensorChanged(SensorEvent event) {
		/*TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		ImageView iv = (ImageView)findViewById(R.id.image);
		*/
		float deltaX = 0;
		float deltaY = 0;
		float deltaZ = 0;
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		/*tvX.setText("0.0");
		tvY.setText("0.0");
		tvZ.setText("0.0");*/
		mInitialized = true;
		} else {
		//float deltaX = Math.abs(mLastX - x);
		//float deltaY = Math.abs(mLastY - y);
		//float deltaZ = Math.abs(mLastZ - z);
		
		 deltaX = Math.abs(mLastX - x);
		 deltaY = Math.abs(mLastY - y);
		 deltaZ = Math.abs(mLastZ - z);
		if (deltaX < NOISE) deltaX = (float)0.0;
		if (deltaY < NOISE) deltaY = (float)0.0;
		if (deltaZ < NOISE) deltaZ = (float)0.0;
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		/*tvX.setText(Float.toString(deltaX));
		tvY.setText(Float.toString(deltaY));
		tvZ.setText(Float.toString(deltaZ));
		iv.setVisibility(View.VISIBLE);*/
	
		}
		if(sendData == true){ // Start send data when buttonCh1 cilck 
			String dataSends =  "x"+Float.toString(mLastX)+"y"+Float.toString(mLastY)+"z"+Float.toString(mLastZ)+"!";
			MyClientTask myClientTask = new MyClientTask(dataSends);//send data to UDP
		       
		     myClientTask.execute();
		}
		
		}
	//acc code end
			         
			         OnClickListener buttonCh1OnClickListener = //ch1 On send data
			           new OnClickListener(){

			            @Override
			            public void onClick(View arg0) {
			            	sendData = true;
			            	textResponse.setText("On");
			            }};
			            
			            OnClickListener buttonCh2OnClickListener = //ch2 Off send data
						           new OnClickListener(){

						            @Override
						            public void onClick(View arg0) {
						            	sendData = false;
						            	textResponse.setText("Off");
						            }};
						            /*
						            OnClickListener buttonCh3OnClickListener = //ch3
									           new OnClickListener(){

									            @Override
									            public void onClick(View arg0) {
									            	MyClientTask myClientTask = new MyClientTask("120");
												       
												     myClientTask.execute();
									            }};
									            
									            OnClickListener buttonCh4OnClickListener = // ch4
												           new OnClickListener(){

												            @Override
												            public void onClick(View arg0) {
												            	MyClientTask myClientTask = new MyClientTask("255");
															       
															     myClientTask.execute();
												            }};*/
		
	
	
			         // }
	
          

	
	public class MyClientTask extends AsyncTask<Void, Void, Void> {
		  
		  //String dstAddress;
		String num;
		  //String response = "";
		  
		  MyClientTask(String value){
		  // dstAddress = addr;
		   //dstPort = port;
			  num = value;
		  }

		  @Override
		  protected Void doInBackground(Void... arg0) {
		   
		   //Socket socket = null;
			  String udpMsg = num;
			  DatagramSocket ds = null;
			
				
			
		   try {
		    /*socket = new Socket(dstAddress, dstPort);
		    
		    ByteArrayOutputStream byteArrayOutputStream = 
		                  new ByteArrayOutputStream(1024);
		    byte[] buffer = new byte[1024];
		    
		    int bytesRead;
		    InputStream inputStream = socket.getInputStream();
		    
		    /*
		     * notice:
		     * inputStream.read() will block if no data return
		     */
		            /* while ((bytesRead = inputStream.read(buffer)) != -1){
		                 byteArrayOutputStream.write(buffer, 0, bytesRead);
		                 response += byteArrayOutputStream.toString("UTF-8");
		             }*/
			 //start UDP server
		       
		        ds = new DatagramSocket();
		        InetAddress serverAddr = InetAddress.getByName("192.168.4.1");
		        //DatagramSocket ds = new DatagramSocket();
		       // DatagramPacket dp;
		        DatagramPacket dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr,80);
		       // for(int i = 0;i<10;i++){
		      
		       // ds.connect(serverAddr, UDP_SERVER_PORT);
		       // ds.setBroadcast(true);
		       /* dp.setData(udpMsg.getBytes());
		        dp.setLength(udpMsg.length());
		        dp.setAddress(serverAddr);
		        dp.setPort(80);
		        */
		        //ds.connect(serverAddr, 80);
		        
		       // Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",Toast.LENGTH_SHORT).show();
		        //for(int i = 0;i<10;i++){
		        ds.send(dp);
		       // }
		       // Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",Toast.LENGTH_SHORT).show();
		       // }
		       // ds.close();
		       // System.out.println("test d'execution");
		       // Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",
		        //		   Toast.LENGTH_SHORT).show();
		        
		    	//return;
			   

		   } catch (UnknownHostException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    //response = "UnknownHostException: " + e.toString();
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   //response = "IOException: " + e.toString();
		   }finally{
			   ds.close();
		   }
		   return null;
		  }

		  @Override
		  protected void onPostExecute(Void result) {
		  // textResponse.setText(response);
			  //Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)",Toast.LENGTH_SHORT).show();
			  motorSpeed = Math.abs((int)Math.round( mLastX*20));
			  if(motorSpeed > 100){
				  motorSpeed = 100; 
			  }
			  if(motorSpeed < 2){
				  motorSpeed = 0; 
			  }
			  textResponse.setText("Motor speed "+motorSpeed+"%");
		   super.onPostExecute(result);
		//super.isCancelled();
		  }
		  
		 }

	
	
	
}



