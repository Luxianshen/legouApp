package com.neusoft.legou.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.entity.User;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.NetUtils;


public class RegisterBormalActivity extends BaseActivity {

	private EditText username, email, password, password1;
	private Button btnregister;
	String Name, Email, Pass,Pass1;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(RegisterBormalActivity.this, "1", 3000).show();
				break;
			case 2:
				Toast.makeText(RegisterBormalActivity.this, "2", 3000).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_normal);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.ed_username);
		email = (EditText) findViewById(R.id.ed_email);
		password = (EditText) findViewById(R.id.ed_password);
		password1 = (EditText) findViewById(R.id.ed_password1);
		btnregister = (Button) findViewById(R.id.btn_regsiter);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		btnregister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				zhuce();
			}
		});
	}

	private void zhuce() {

		Name = username.getText().toString();
		Email = email.getText().toString();
		Pass = password.getText().toString();
        Pass1=password1.getText().toString();
		if (Name == null) {
			username.setError("请输入用户名");
			username.requestFocus();
		} else if (Email == null) {
			email.setError("请输入用户名");
			email.requestFocus();
		} else if (Pass == null) {
			password.setError("请输入密码");
			password.requestFocus();
		} else if (Pass1 == null) {
			password1.setError("请输入密码");
			password1.requestFocus();
		} else {
			if (Pass==Pass1) {
				password1.setError("两次输入的密码不一样");
				password1.requestFocus();
			} else {

				// TODO Auto-generated method stub
				if (!NetUtils.check(RegisterBormalActivity.this)) {
					Toast.makeText(RegisterBormalActivity.this, "网络故障。。。",
							Toast.LENGTH_SHORT).show();
					return; // 后续代码不执行
				}
				new Thread() {

					public void run() {
						Message msg = new Message();
						HttpPost post = new HttpPost(CONSTANT.HOST
								+ "/register");
 
						Gson gson = new Gson();
						User user = new User();
						user.setUserName(Name);
						user.setEmail(Email);
						user.setUserPwd(Pass);
						String newuser =gson.toJson(user);

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("action", "regsiter"));
						params.add(new BasicNameValuePair("user", newuser));
						Log.d("...", "我进来了。。");
						DefaultHttpClient client = new DefaultHttpClient();

						HttpEntity entity;

						try {
							entity = new UrlEncodedFormEntity(params, "UTF-8");
							post.setEntity(entity);
							HttpResponse response = client.execute(post);
                             
							if (response.getStatusLine().getStatusCode() == 200) {

								Log.d("....", "我进来了。。");
								String data = EntityUtils.toString(response
										.getEntity());
								String result = gson.fromJson(data,
										String.class);

								msg.what = 1;
								msg.arg1 = Integer.parseInt(result);

							} else {
								msg.what = 2;
							}

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					};

				}.start();
			}
		}
	}
}
