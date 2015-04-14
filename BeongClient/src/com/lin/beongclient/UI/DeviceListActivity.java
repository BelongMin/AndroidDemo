package com.lin.beongclient.UI;



import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.lin.beongclient.R;
import com.lin.beongclient.R.id;
import com.lin.beongclient.R.layout;
import com.lin.beongclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.widget.TextView;
import android.widget.Toast;

public class DeviceListActivity extends Activity {
	
	
	private TextView txtList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);

		//initial
		txtList = (TextView)findViewById(R.id.txt_list);
		
		
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					HttpClient client = new DefaultHttpClient();
					String uri = "http://192.168.3.9:8080/belong/Update.do";
					HttpGet Get = new HttpGet(uri);
					HttpResponse response = client.execute(Get);

					int status = response.getStatusLine().getStatusCode();
					if (status == HttpStatus.SC_OK) {
						String result = EntityUtils.toString(
								response.getEntity(), HTTP.UTF_8);

						//Log.d("net.kojec.arthur_for_hub.UI", result);
						txtList.setText(result);
					} else {
						txtList.setText("错误消息"+response.getStatusLine().toString());
						//Log.d("net.kojec.arthur_for_hub.UI", "错误消息"+ response.getStatusLine().toString());
					}

					Toast.makeText(DeviceListActivity.this, "Over",
							Toast.LENGTH_SHORT).show();

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}).start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_list, menu);
		return true;
	}

}
