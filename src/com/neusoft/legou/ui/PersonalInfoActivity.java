package com.neusoft.legou.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.google.gson.Gson;

import com.neusoft.legou.R;
import com.neusoft.legou.entity.Product;
import com.neusoft.legou.entity.User;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.Md5Utils;
import com.neusoft.legou.utils.NetUtils;

public class PersonalInfoActivity extends BaseActivity implements OnClickListener {
	
	private ListView personalinfo_listview;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> data;
	private ProgressDialog pDialog;
	private SimpleAdapter adapter;
	private CatergorAdapter catergorAdapter;
	
	// 适配显示的图片数组
	
	// 主线程负责修改UI
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);
			// 清空data
			if(pDialog!=null){
				pDialog.dismiss();
			}
			
			data.clear();
			
			switch (msg.what) {
			case 1:
		            User user = (User) msg.obj;
		            int age=user.getAge();
		            String ageString=String.valueOf(age);
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("username",user.getUserName());
					row.put("email", user.getEmail());
					row.put("realname",user.getRealName() );
					row.put("tel",user.getTel());
					row.put("age",ageString);
					row.put("sex", user.getSex());
					row.put("address", user.getAddress());
					row.put("poatalcode", user.getPostalcode());
					data.add(row);
					catergorAdapter.notifyDataSetChanged();
				break;
			case 2:
				Toast.makeText(PersonalInfoActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(PersonalInfoActivity.this, "请重新登录",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}

	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalinfo_listview);
		findViewById();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 检查网络是否连通
		if (!NetUtils.check(PersonalInfoActivity.this)) {
			Toast.makeText(PersonalInfoActivity.this, "服务器连接异常", 3000).show();
			return;
		}
	
		  pDialog = ProgressDialog.show(PersonalInfoActivity.this,
		  null, "正在加载中...请稍候", false, true);
		 
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 获取message
				SharedPreferences pref = getSharedPreferences("user",Context.MODE_PRIVATE);
				String userName=pref.getString("username","");//""的意思是：拿不到就拿下一个
				
				Message msg = handler.obtainMessage();
				// 访问服务器文件
				HttpPost post = new HttpPost(CONSTANT.HOST + "/findoneuser");
				// 发送请求
				DefaultHttpClient client = new DefaultHttpClient();
				// 设置参数
				
				Gson userGson = new Gson();
			    String stringUser = userGson.toJson(userName);
				// 设置参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("userName",stringUser));

				// 超时设置
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT,
						CONSTANT.REQUEST_TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);
				UrlEncodedFormEntity entity;
				try {
					entity = new UrlEncodedFormEntity(params, "UTF-8");

					post.setEntity(entity);

					HttpResponse response = client.execute(post);

					// 处理结果
					if (response.getStatusLine().getStatusCode() == 200) {

						String json = EntityUtils
								.toString(response.getEntity());
						
						Gson gson=new Gson();
						User user=gson.fromJson(json,User.class);
						Log.d("person", json);
						// 发送消息
						msg.obj = user;
						// msg.arg1 = Integer.parseInt(result);
						msg.what = 1;
						handler.sendMessage(msg);
					} else {
						msg.what = 2;
						handler.sendMessage(msg);
					}
					// 关闭连接
					client.getConnectionManager().shutdown();

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		personalinfo_listview = (ListView) findViewById(R.id.personalinfo_listview);
		
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		data = new ArrayList<Map<String, Object>>();

		catergorAdapter = new CatergorAdapter();
		personalinfo_listview.setAdapter(catergorAdapter);
		

	}

	private class CatergorAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("null")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = new ViewHolder();
			layoutInflater = LayoutInflater.from(PersonalInfoActivity.this);

			// 组装数据
			if (convertView == null) {

				convertView = layoutInflater.inflate(
						R.layout.activity_personalinfo, null);
				holder.etusername =  (EditText) convertView
						.findViewById(R.id.etusername);
				holder.etemail =  (EditText) convertView
						.findViewById(R.id.etemail);
				holder.etrealname = (EditText) convertView
						.findViewById(R.id.etrealname);
				holder.ettel =  (EditText) convertView
						.findViewById(R.id.ettel);
				holder.etage = (EditText) convertView
						.findViewById(R.id.etage);
				holder.etsex =  (EditText) convertView
						.findViewById(R.id.etsex);
				holder.etaddress =  (EditText) convertView
						.findViewById(R.id.etaddress);
				holder.etpostalcode =  (EditText) convertView
						.findViewById(R.id.etpostalcode);

				// 使用tag存储数据
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.etusername.setText(data.get(position).get("username").toString());
			holder.etemail.setText(data.get(position).get("email").toString());
			holder.etrealname.setText(data.get(position).get("realname").toString());
			holder.ettel.setText(data.get(position).get("tel").toString());
			holder.etage.setText(data.get(position).get("age").toString());
			holder.etsex.setText(data.get(position).get("sex").toString());
			holder.etaddress.setText(data.get(position).get("address").toString());
			holder.etpostalcode.setText(data.get(position).get("poatalcode").toString());
			// holder.title.setText(array[position]);

			return convertView;

		}

	}
	
	
	public static class ViewHolder {
		EditText etusername;
		EditText etemail;
		EditText etrealname;
		EditText ettel;
		EditText etage;
		EditText etsex;
		EditText etaddress;
		EditText etpostalcode;
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
		}
		
	
	
	
	
	