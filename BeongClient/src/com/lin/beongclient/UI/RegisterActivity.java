package com.lin.beongclient.UI;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import com.lin.beongclient.R;
import com.lin.beongclient.R.layout;
import com.lin.beongclient.R.menu;
import com.lin.beongclient.server.ServiceRulesException;
import com.lin.beongclient.server.UserService;
import com.lin.beongclient.server.UserServiceImpl;

import android.media.MediaRouter.UserRouteInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private final static int FLAG_REGISTER_SUCCESS = 1;
	private final static String MSG_REGISTER_SUCCESS = "注册成功";
	private final static String MSG_REGISTER_ERROR = "注册代码异常";
	public final static String HTTP_SERVER_ERROR = "服务请求错误";
	public final static String MSG_REGISTER_FAILED = "注册失败";
	
	
	
	
	private EditText txtRegister;
	private CheckBox chkMusic;
	private CheckBox chkGame;
	private CheckBox chkSprots;
	private Button btnRegister;
	
	private static ProgressDialog dialog; 
	private UserService userRegister = new UserServiceImpl();
	
	
	
	private void init(){
		txtRegister = (EditText)findViewById(R.id.txt_register_name);
		chkGame = (CheckBox)findViewById(R.id.chk_game);
		chkMusic = (CheckBox)findViewById(R.id.chk_music);
		chkSprots = (CheckBox)findViewById(R.id.chk_sports);
		btnRegister = (Button)findViewById(R.id.btn_register);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		//控件初始化
		init();
		
		
		
		this.btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
				final String registerName = txtRegister.getText().toString();
				final List<String> interesting = new ArrayList<String>();
				
				if(chkGame.isChecked()){
					interesting.add(chkGame.getText().toString());
				}
				if(chkMusic.isChecked()){
					interesting.add(chkMusic.getText().toString());
	 			}
				if(chkSprots.isChecked()){
					interesting.add(chkSprots.getText().toString());
				}
				
				dialog = new ProgressDialog(RegisterActivity.this);
				dialog.setTitle("请等待");
				dialog.setMessage("注册中……");
				dialog.setCancelable(false);
				dialog.show();
				
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							//执行注册方法
							userRegister.UserRegister(registerName, interesting);
							handler.sendEmptyMessage(FLAG_REGISTER_SUCCESS);
						} catch (ConnectTimeoutException e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", LoginActivity.MSG_REQUEST_TIMEOUT);
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (SocketTimeoutException e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", LoginActivity.MSG_RESPONSE_TIMEOUT);
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (ServiceRulesException e){

							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", e.getMessage());
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (Exception e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", MSG_REGISTER_ERROR);
							msg.setData(data);
							handler.sendMessage(msg);
						}
						
						
					}
				});
				thread.start();
				
				
				
			}
		});
	
	}
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	
	private static class IHandler extends Handler{
		private WeakReference<Activity> mActivity;
		
		private IHandler(RegisterActivity activity){
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if(dialog != null)
			{
				dialog.dismiss();
			}
			
			
			int flag = msg.what;
			
			switch (flag) {
			case 0:
				String errorMsg = msg.getData().getSerializable("ErrorMsg").toString();
				((RegisterActivity)mActivity.get()).showTip(errorMsg);
				break;
			case FLAG_REGISTER_SUCCESS:
				((RegisterActivity)mActivity.get()).showTip(MSG_REGISTER_SUCCESS);
				
				break;

			default:
				break;
			}
		}
		
		
	}
	
	IHandler handler = new IHandler(this);

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
