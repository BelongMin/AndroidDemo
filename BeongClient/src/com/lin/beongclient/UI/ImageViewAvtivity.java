package com.lin.beongclient.UI;

import com.lin.beongclient.R;
import com.lin.beongclient.server.ServiceRulesException;
import com.lin.beongclient.server.UserService;
import com.lin.beongclient.server.UserServiceImpl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageViewAvtivity extends Activity{
	
	
	private ImageView imageview_simple;
	
	private UserService userService = new UserServiceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imageview);
		
		imageview_simple = (ImageView)findViewById(R.id.imageview_simple);
		
//		imageview_simple.setImageResource(R.drawable.b);
		//imageview_simple.setImageDrawable(this.getResources().getDrawable(R.drawable.b));
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					final Bitmap bitmap = userService.getImage();
					
					if(bitmap != null){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								imageview_simple.setImageBitmap(bitmap);
							}
						});
					}
					
				} catch(final ServiceRulesException e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(ImageViewAvtivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(ImageViewAvtivity.this, "»ñÈ¡Í¼Æ¬³ö´í", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
		
	}
}
