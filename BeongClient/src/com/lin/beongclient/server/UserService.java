package com.lin.beongclient.server;

import java.util.List;

import android.graphics.Bitmap;

import com.lin.beongclient.entity.Student;

public interface UserService {
	public void UserLogin(String LoginName, String LoginPassword)throws Exception;
	
	public void UserRegister(String RegisterName, List<String> Interesting)throws Exception;
	
	public List<Student> GetStudents() throws Exception;
	
	public Bitmap getImage() throws Exception;
}
