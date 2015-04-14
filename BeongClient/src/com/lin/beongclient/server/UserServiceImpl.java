package com.lin.beongclient.server;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lin.beongclient.UI.DeviceListActivity;
import com.lin.beongclient.UI.LoginActivity;
import com.lin.beongclient.UI.RegisterActivity;
import com.lin.beongclient.entity.Student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.inputmethod.InputBinding;

public class UserServiceImpl implements UserService {

	private String TAG = "UserServerImpl";

	@Override
	public void UserLogin(String loginName, String loginPassword)
			throws Exception {

		Log.d(TAG, loginName);
		Log.d(TAG, loginPassword);
		// ---------------------GET方法--------------------------------
		// HttpClient client = new DefaultHttpClient();
		// /**
		// * uri: URL:http://192.168.3.9:8080/belong/login.do
		// *
		// * IP:使用localhost可能不行，直接写ip地址
		// *
		// * GET 传值（传参） 实质：URL？参数名=参数值&参数名=参数值&……
		// *
		// * 真机需要修改IP地址
		// */
		// String uri = "http://192.168.3.9:8080/belong/login.do?LoginName="
		// + loginName + "&LoginPassword=" + loginPassword;
		//
		// HttpGet get = new HttpGet(uri);
		//
		// HttpResponse response = client.execute(get);

		// ---------------------------POST方法 设定连接参数----------------------------
		HttpParams params = new BasicHttpParams();
		// 通过params设置请求字符集
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// 通过客户端和服务器连接超时的时间-->3秒 --》ConnectTimeoutException
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		// 通过服务器响应超时的时间-->3秒--》SocketTimeoutException
		HttpConnectionParams.setSoTimeout(params, 3000);

		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schreg.register(new Scheme("https", PlainSocketFactory
				.getSocketFactory(), 433));

		ClientConnectionManager conman = new ThreadSafeClientConnManager(
				params, schreg);

		HttpClient client = new DefaultHttpClient(conman, params);
		// -------------------------HTTP POST 正常创建方式-------------------------
		// HttpClient client = new DefaultHttpClient();
		// 根据服务器写入IP地址
		String uri = "http://192.168.3.9:8080/belong/login.do";

		HttpPost post = new HttpPost(uri);

		NameValuePair paramLoginName = new BasicNameValuePair("LoginName",
				loginName);
		NameValuePair paramLoginPassword = new BasicNameValuePair(
				"LoginPassword", loginPassword);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(paramLoginName);
		parameters.add(paramLoginPassword);

		post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

		HttpResponse response = client.execute(post);

		// -------------------------------我是分割线-------------------------

		// 获得http响应码（404、500等）200代表成功OK
		int statusCode = response.getStatusLine().getStatusCode();
		// 不成功抛出异常
		if (statusCode != HttpStatus.SC_OK) {
			throw new ServiceRulesException(LoginActivity.HTTP_SERVER_ERROT);
		}
		// 使用工具获取返回的数据
		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

		if (result.equals("SUCCESS")) {

		} else {
			throw new ServiceRulesException(LoginActivity.MSG_LOGIN_FAILED);
		}

	}

	@Override
	public void UserRegister(String RegisterName, List<String> Interesting)
			throws Exception {
		// TODO Auto-generated method stub
		// Thread.sleep(3000);

		HttpClient client = new DefaultHttpClient();
		String uri = "http://192.168.3.9:8080/belong/register.do";
		HttpPost post = new HttpPost(uri);

		JSONObject object = new JSONObject();
		object.put("LoginName", RegisterName);
		JSONArray array = new JSONArray();
		if (Interesting != null) {
			for (String str : Interesting) {
				array.put(str);
			}
		}
		object.put("Interesting", array);

		NameValuePair parameter = new BasicNameValuePair("Data",
				object.toString());

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(parameter);

		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(post);

		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {
			throw new ServiceRulesException(RegisterActivity.HTTP_SERVER_ERROR);
		}
		// 使用工具获取返回的数据
		String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

		/**
		 * 解析JSON数据
		 */
		JSONObject jsonResult = new JSONObject(results);

		String result = jsonResult.getString("result");

		if (result.equals("SUCCESS")) {

		} else {

			String errorMsg = jsonResult.getString("errorMsg");
			// Log.d("REGISTER", errorMsg);
			throw new ServiceRulesException(errorMsg);
		}
	}

	@Override
	public List<Student> GetStudents() throws Exception {
		// TODO Auto-generated method stub

		List<Student> students = new ArrayList<Student>();

		HttpClient client = new DefaultHttpClient();
		String uri = "http://192.168.3.9:8080/belong/GetStudent.do";
		HttpGet get = new HttpGet(uri);
		HttpResponse response = client.execute(get);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new ServiceRulesException(LoginActivity.HTTP_SERVER_ERROT);
		}

		String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

		JSONArray array = new JSONArray(result);

		for (int i = 0; i < array.length(); i++) {
			JSONObject jsonStudent = array.getJSONObject(i);
			Long id = Long.parseLong(jsonStudent.getString("id"));
			String name = jsonStudent.getString("name");
			int age = jsonStudent.getInt("age");
			students.add(new Student(id, name, age));

		}

		return students;
	}
	//构造POST参数
	private static StringBuffer setPostPassParams(Map<String, String> params) throws UnsupportedEncodingException{
		StringBuffer stringBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			stringBuffer.append(entry.getKey())
			.append("=")
			.append(URLEncoder.encode(entry.getValue(), "UTF-8"))
			.append("&");	
		}
		
		stringBuffer.deleteCharAt(stringBuffer.length()-1);
		
		return stringBuffer;
	}
	
	
	@Override
	public Bitmap getImage() throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Bitmap bitmap = null;
		URL url = null;
		HttpURLConnection urlConnection = null;
		byte[] data = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", "y");
			data = setPostPassParams(params).toString().getBytes();
			
			/**
			 * POST方法
			 */
			url = new URL("http://192.168.3.9:8080/belong/GetImage.jpeg");
			urlConnection = (HttpURLConnection) url.openConnection();
			//设置请求的超时时间
			urlConnection.setConnectTimeout(3000);
			//设置响应的超时时间
			urlConnection.setReadTimeout(3000);
			
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			
			urlConnection.setRequestMethod("POST");
			//取消缓存
			urlConnection.setUseCaches(false);
			
			urlConnection.connect();
			
			
			outputStream = urlConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			
			int responseCode = urlConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new ServiceRulesException("请求服务器出错");
			}
			//装饰器
			inputStream = new BufferedInputStream(urlConnection.getInputStream());
			if(inputStream != null){
				bitmap = BitmapFactory.decodeStream(inputStream);
			}
			
			
			/**
			 * get方法
			 */
//			url = new URL("http://192.168.3.9:8080/belong/GetImage.jpeg?id=g");
//			urlConnection = (HttpURLConnection) url.openConnection();
//			// 设置可以从服务器读取数据，图片流
//			urlConnection.setDoInput(true);
//			urlConnection.setRequestMethod("GET");
//			// connect
//			urlConnection.connect();
//			int responseCode = urlConnection.getResponseCode();
//			if (responseCode != HttpURLConnection.HTTP_OK) {
//				throw new ServiceRulesException("请求服务器出错");
//			}
//
//			inputStream = urlConnection.getInputStream();
//			if(inputStream != null){
//				bitmap = BitmapFactory.decodeStream(inputStream);
//			}

		} finally {
			if(inputStream != null){
				inputStream.close();
			}
			if(urlConnection != null){
				urlConnection.disconnect();
			}
		}

		return bitmap;
	}

}
