package com.lin.beongclient.UI;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.lin.beongclient.R;
import com.lin.beongclient.adapter.StudentAdapter;
import com.lin.beongclient.entity.Student;
import com.lin.beongclient.server.ServiceRulesException;
import com.lin.beongclient.server.UserService;
import com.lin.beongclient.server.UserServiceImpl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.Toast;

public class StudentActivity extends Activity{

	private ListView listViewStudent;
	private List<Student> studentList;
	private UserService userService = new UserServiceImpl();
	
	private StudentAdapter adapter;
	private static ProgressDialog dialog;
	public final static String MSG_STUDENT_SUCCESS = "获取数据成功";
	public final static String MSG_STUDENT_ERROR = "获取数据错误";
	public final static int FLAG_STUDENT_SUCCESS = 1; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);
		
		this.listViewStudent = (ListView)this.findViewById(R.id.listview_student);
		
		this.studentList = new ArrayList<Student>();
//		studentList.add(new Student(100L, "Lin", 17));
//		studentList.add(new Student(101L, "Bo", 18));
//		studentList.add(new Student(102L, "Min", 19));
		
		
		
//		
//		this.adapter = new StudentAdapter(this, R.layout.student_item, this.studentList);
//		this.listViewStudent.setAdapter(adapter);
		
		if(dialog == null)
		{
			dialog = new ProgressDialog(StudentActivity.this);
		}
		dialog.setTitle("请等待");
		dialog.setMessage("等待中……");
		dialog.setCancelable(false);
		dialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					studentList = userService.GetStudents();
					handler.sendEmptyMessage(FLAG_STUDENT_SUCCESS);
				} catch (ServiceRulesException e) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", e.getMessage());
					msg.setData(data);
					handler.sendMessage(msg);
				} catch (Exception e) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", MSG_STUDENT_ERROR);
					msg.setData(data);
					handler.sendMessage(msg);
				} 
			}
		}).start();
	}
	
	private void reloadList()
	{
		showTip(MSG_STUDENT_SUCCESS);
		this.adapter = new StudentAdapter(this, R.layout.student_item, this.studentList);
		this.listViewStudent.setAdapter(adapter);
	}
	
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	private static class IHandler extends Handler{

		private final WeakReference<Activity> mActivity;
		
		public IHandler(StudentActivity activity){
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
				((StudentActivity)mActivity.get()).showTip(errorMsg);
				break;
			case FLAG_STUDENT_SUCCESS:
				((StudentActivity)mActivity.get()).reloadList();
				//((LoginActivity)mActivity.get()).jump();
				break;

			default:
				break;
			}
		}
		
	}
	
	private IHandler handler = new IHandler(this);
}
