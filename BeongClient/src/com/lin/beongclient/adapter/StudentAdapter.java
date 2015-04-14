package com.lin.beongclient.adapter;

import java.util.List;

import com.lin.beongclient.R;
import com.lin.beongclient.entity.Student;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudentAdapter extends ArrayAdapter<Student>{
	
	private LayoutInflater mInflater;
	private int mResource;

	/**
	 * 
	 * @param context иообнд
	 * @param resource student_list
	 * @param objects List<Student>
	 */
	public StudentAdapter(Context context, int resource, List<Student> objects) {
		super(context, resource, objects);
		this.mInflater = LayoutInflater.from(context);
		this.mResource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout view = null;
		
		if(convertView == null){
			view = (LinearLayout)mInflater.inflate(mResource, null);
			
		}else {
			view = (LinearLayout)convertView;
		}
		
		Student student = getItem(position);
		
		TextView txtId = (TextView) view.findViewById(R.id.txt_id);
		TextView txtName = (TextView) view.findViewById(R.id.txt_name);
		TextView txtAge = (TextView) view.findViewById(R.id.txt_age);
		
		txtId.setText(student.getId().toString());
		txtName.setText(student.getName());
		txtAge.setText(String.valueOf(student.getAge()));
		
		return view;
	}

}
