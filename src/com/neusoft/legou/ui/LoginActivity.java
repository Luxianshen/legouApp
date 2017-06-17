package com.neusoft.legou.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.entity.User;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.Md5Utils;
import com.neusoft.legou.utils.NetUtils;


public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText loginaccount, loginpassword;
	private Button loginBtn, register;
	private Intent mIntent;
	private ImageView loginLogo;
	String username;
	String password;
	String getpassword;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				String jsessionid = (String) msg.obj;
				int result = msg.arg1;

				if (0 == result) {
					loginaccount.selectAll();
					loginaccount.setError("用户名或密码错误");
					loginaccount.requestFocus();
				} else if (1 == result) {
					SharedPreferences pref = getSharedPreferences("user",
							Context.MODE_PRIVATE);
					Editor editor = pref.edit();
                    editor.putString("username", username);
					// 记录JSESSIONID
					editor.putString("Cookie", jsessionid);

					editor.commit();

					// 显示意图
					Intent intent = new Intent(LoginActivity.this,
							HomeActivity.class);
					startActivity(intent);

					// 关闭
					LoginActivity.this.finish();
				}

				break;
			case 2:
				Toast.makeText(LoginActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		loginLogo = (ImageView) this.findViewById(R.id.logo);
		loginaccount = (EditText) this.findViewById(R.id.loginaccount);
		loginpassword = (EditText) this.findViewById(R.id.loginpassword);
		loginBtn = (Button) this.findViewById(R.id.login);
		register = (Button) this.findViewById(R.id.register);

		getpassword = loginpassword.getText().toString();
	}

	@Override
	protected void initView() {

		register.setOnClickListener(this);
		loginBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			mIntent = new Intent(LoginActivity.this,
					RegisterBormalActivity.class);
			startActivity(mIntent);

			break;

		case R.id.login:
			saveuserid();
			userlogin();

			break;

		default:
			break;
		}

	}
	
	private void saveuserid() {
		
		if (!NetUtils.check(LoginActivity.this)) {
			Toast.makeText(LoginActivity.this, "网络故障。。。",
					Toast.LENGTH_SHORT).show();
			return; // 后续代码不执行
		}
		new Thread() {

			public void run() {
				Message msg = handler.obtainMessage();

				// 访问服务器端，验证用户名/密码
				HttpPost post = new HttpPost(CONSTANT.HOST + "/findoneuser");
				// 设置参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("userName", loginaccount
						.getText().toString()));
				
				UrlEncodedFormEntity entity;

				try {
					entity = new UrlEncodedFormEntity(params, "UTF-8");

					post.setEntity(entity);
					// 发送请求
					DefaultHttpClient client = new DefaultHttpClient();
					// 超时设置
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONSTANT.REQUEST_TIMEOUT);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT,
							CONSTANT.SO_TIMEOUT);
					HttpResponse response = client.execute(post);

					// 处理结果
					if (response.getStatusLine().getStatusCode() == 200) {

						String data = EntityUtils.toString(response
								.getEntity());
						Gson gson = new Gson();
						User user = gson.fromJson(data, User.class);
						int userid=user.getUserID();
						SharedPreferences pref = getSharedPreferences("user",
								Context.MODE_PRIVATE);
						Editor editor = pref.edit();
	                    editor.putInt("userid", userid);
	                    editor.commit();
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

	// 之前的方式太繁瑣了
	private void userlogin() {
		username = loginaccount.getText().toString().trim();
		password = loginpassword.getText().toString().trim();
		
		if (username.equals("")) {
			DisplayToast("用户名不能为空!");
		}
		if (password.equals("")) {
			DisplayToast("密码不能为空!");
		}

		else {
			if (!NetUtils.check(LoginActivity.this)) {
				Toast.makeText(LoginActivity.this, "服务器配置失败",
						Toast.LENGTH_SHORT).show();
				return; // 后续代码不执行
			}
			
			new Thread() {
				public void run() {
					Message msg = handler.obtainMessage();

					// 访问服务器端，验证用户名/密码
					HttpPost post = new HttpPost(CONSTANT.HOST + "/login");
					// 设置参数
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("username", loginaccount
							.getText().toString()));
					params.add(new BasicNameValuePair("password", Md5Utils
							.MD5(loginpassword.getText().toString())));

					Log.d("Mywanggou",
							params.toString()
									+ ","
									+ Md5Utils.MD5(loginpassword.getText()
											.toString()));
					UrlEncodedFormEntity entity;
					try {
						entity = new UrlEncodedFormEntity(params, "UTF-8");

						post.setEntity(entity);
						// 发送请求
						DefaultHttpClient client = new DefaultHttpClient();
						// 超时设置
						client.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT,
								CONSTANT.REQUEST_TIMEOUT);
						client.getParams().setParameter(
								CoreConnectionPNames.SO_TIMEOUT,
								CONSTANT.SO_TIMEOUT);
						HttpResponse response = client.execute(post);

						// 处理结果
						if (response.getStatusLine().getStatusCode() == 200) {

							String data = EntityUtils.toString(response
									.getEntity());
							Gson gson = new Gson();
							String result = gson.fromJson(data, String.class);

							// 记录JSESSIONID
							String value = "";
							List<Cookie> cookies = client.getCookieStore()
									.getCookies();
							for (Cookie cookie : cookies) {
								if ("JSESSIONID".equals(cookie.getName())) {
									value = cookie.getValue();
									Log.d("Mywanggou", "JSESSIONID:" + value);
									break;
								}
							}

							// 发送消息
							msg.what = 1;
							msg.arg1 = Integer.parseInt(result);
							msg.obj = "JSESSIONID=" + value;
						} else {
							msg.what = 2;
						}

						// 关闭连接
						client.getConnectionManager().shutdown();

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 2;
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 2;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 2;
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 2;
					}

					handler.sendMessage(msg);
				};
			}.start();
		}
	}

}
