package com.lin.beongclient.UI;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.lin.beongclient.R;
import com.lin.beongclient.R.id;
import com.lin.beongclient.R.layout;
import com.lin.beongclient.R.menu;
import com.lin.beongclient.server.ServiceRulesException;
import com.lin.beongclient.server.UserService;
import com.lin.beongclient.server.UserServiceImpl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter.LengthFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText txtLoginName;
	private EditText txtLoginPassword;
	private Button btnLogin;
	private Button btnReset;
	private UserService userService = new UserServiceImpl();
	
	private static final int FLAG_LOGIN_SUCCESS = 1;
	private static final String MSG_LOGIN_ERROR = "登陆出错";
	private static final String MSG_LOGIN_SUCCESS = "登陆成功";
	public static final String MSG_LOGIN_FAILED = "登录名或密码出错";
	public static final String HTTP_SERVER_ERROT = "请求服务错误";
	public static final String MSG_REQUEST_TIMEOUT = "请求服务器连接超时";
	public static final String MSG_RESPONSE_TIMEOUT = "请求服务器响应超时";
	private static ProgressDialog dialog;
	
	private void init() {
		this.txtLoginName = (EditText) findViewById(R.id.txt_login_name);
		this.txtLoginPassword = (EditText) findViewById(R.id.txt_login_password);
		this.btnLogin = (Button) findViewById(R.id.btn_login);
		this.btnReset = (Button) findViewById(R.id.btn_password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		// 初始化
		this.init();

		// 点击“登陆”
		this.btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				final String loginName = txtLoginName.getText().toString();
				final String loginPassword = txtLoginPassword.getText()
						.toString();

//				Toast.makeText(view.getContext(), "登录名" + loginName,
//						Toast.LENGTH_SHORT).show();
//				Toast.makeText(view.getContext(), "登陆密码" + loginPassword,
//						Toast.LENGTH_SHORT).show();
			
				/**
				 * 输入判断，输入是否为空，输入长度等
				 */

				/**
				 * loading
				 */
				if(dialog == null)
				{
					dialog = new ProgressDialog(LoginActivity.this);
				}
				dialog.setTitle("请等待");
				dialog.setMessage("等待中……");
				dialog.setCancelable(false);
				dialog.show();
				
				// 副线程
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							userService.UserLogin(loginName, loginPassword);
							handler.sendEmptyMessage(FLAG_LOGIN_SUCCESS);
						} catch (ConnectTimeoutException e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", MSG_REQUEST_TIMEOUT);
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (SocketTimeoutException e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", MSG_RESPONSE_TIMEOUT);
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (ServiceRulesException e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", e.getMessage());
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (Exception e) {
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("ErrorMsg", MSG_LOGIN_ERROR);
							msg.setData(data);
							handler.sendMessage(msg);
						} 
					}
				});
				thread.start();
			}
		});

		// 点击“重置”
		this.btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				txtLoginName.setText("");
				txtLoginPassword.setText("");

			}
		});
	}
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	private void jump()
	{
		Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
		startActivity(intent);
	}
	private static class IHandler extends Handler{

		private final WeakReference<Activity> mActivity;
		
		public IHandler(LoginActivity activity){
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
				((LoginActivity)mActivity.get()).showTip(errorMsg);
				break;
			case FLAG_LOGIN_SUCCESS:
				((LoginActivity)mActivity.get()).showTip(MSG_LOGIN_SUCCESS);
				//((LoginActivity)mActivity.get()).jump();
				break;

			default:
				break;
			}
		}
		
	}
	
	private IHandler handler = new IHandler(this);

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
