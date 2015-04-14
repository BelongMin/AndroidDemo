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
		// ---------------------GET����--------------------------------
		// HttpClient client = new DefaultHttpClient();
		// /**
		// * uri: URL:http://192.168.3.9:8080/belong/login.do
		// *
		// * IP:ʹ��localhost���ܲ��У�ֱ��дip��ַ
		// *
		// * GET ��ֵ�����Σ� ʵ�ʣ�URL��������=����ֵ&������=����ֵ&����
		// *
		// * �����Ҫ�޸�IP��ַ
		// */
		// String uri = "http://192.168.3.9:8080/belong/login.do?LoginName="
		// + loginName + "&LoginPassword=" + loginPassword;
		//
		// HttpGet get = new HttpGet(uri);
		//
		// HttpResponse response = client.execute(get);

		// ---------------------------POST���� �趨���Ӳ���----------------------------
		HttpParams params = new BasicHttpParams();
		// ͨ��params���������ַ���
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// ͨ���ͻ��˺ͷ��������ӳ�ʱ��ʱ��-->3�� --��ConnectTimeoutException
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		// ͨ����������Ӧ��ʱ��ʱ��-->3��--��SocketTimeoutException
		HttpConnectionParams.setSoTimeout(params, 3000);

		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schreg.register(new Scheme("https", PlainSocketFactory
				.getSocketFactory(), 433));

		ClientConnectionManager conman = new ThreadSafeClientConnManager(
				params, schreg);

		HttpClient client = new DefaultHttpClient(conman, params);
		// -------------------------HTTP POST ����������ʽ-------------------------
		// HttpClient client = new DefaultHttpClient();
		// ���ݷ�����д��IP��ַ
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

		// -------------------------------���Ƿָ���-------------------------

		// ���http��Ӧ�루404��500�ȣ�200����ɹ�OK
		int statusCode = response.getStatusLine().getStatusCode();
		// ���ɹ��׳��쳣
		if (statusCode != HttpStatus.SC_OK) {
			throw new ServiceRulesException(LoginActivity.HTTP_SERVER_ERROT);
		}
		// ʹ�ù��߻�ȡ���ص�����
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
		// ʹ�ù��߻�ȡ���ص�����
		String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

		/**
		 * ����JSON����
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
	//����POST����
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
			 * POST����
			 */
			url = new URL("http://192.168.3.9:8080/belong/GetImage.jpeg");
			urlConnection = (HttpURLConnection) url.openConnection();
			//��������ĳ�ʱʱ��
			urlConnection.setConnectTimeout(3000);
			//������Ӧ�ĳ�ʱʱ��
			urlConnection.setReadTimeout(3000);
			
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			
			urlConnection.setRequestMethod("POST");
			//ȡ������
			urlConnection.setUseCaches(false);
			
			urlConnection.connect();
			
			
			outputStream = urlConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			
			int responseCode = urlConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new ServiceRulesException("�������������");
			}
			//װ����
			inputStream = new BufferedInputStream(urlConnection.getInputStream());
			if(inputStream != null){
				bitmap = BitmapFactory.decodeStream(inputStream);
			}
			
			
			/**
			 * get����
			 */
//			url = new URL("http://192.168.3.9:8080/belong/GetImage.jpeg?id=g");
//			urlConnection = (HttpURLConnection) url.openConnection();
//			// ���ÿ��Դӷ�������ȡ���ݣ�ͼƬ��
//			urlConnection.setDoInput(true);
//			urlConnection.setRequestMethod("GET");
//			// connect
//			urlConnection.connect();
//			int responseCode = urlConnection.getResponseCode();
//			if (responseCode != HttpURLConnection.HTTP_OK) {
//				throw new ServiceRulesException("�������������");
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
