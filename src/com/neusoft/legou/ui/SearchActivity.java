package com.neusoft.legou.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.R.drawable;

import com.neusoft.legou.entity.Product;
import com.neusoft.legou.entity.User;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.ui.category.CategoryActivityDetail;
import com.neusoft.legou.ui.category.CategoryHomeApActivity;
import com.neusoft.legou.ui.category.CategoryHomeApActivity.ViewHolder;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.CommonTools;
import com.neusoft.legou.utils.NetUtils;
import com.neusoft.legou.widgets.AutoClearEditText;


public class SearchActivity extends BaseActivity {

	private AutoClearEditText mEditText = null;
	private ImageButton mImageButton = null;
	private ListView productinfo_listview;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> data;
	private ProgressDialog pDialog;
	private SimpleAdapter adapter;
	private ImageView catergory_image;
	private CatergorAdapter catergorAdapter;
	private String etString;
	Integer value;
	
	
	
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
					Toast.makeText(SearchActivity.this, etString, 3000).show();
					// Passenger[] => data
					Product[] products = (Product[]) msg.obj;
					// Log.d("pro", products[0].toString());
					int i = 0;
					for (Product passenger : products) {
						
						String sImage =passenger.getProSrc(); 
						Class<drawable> cls = R.drawable.class;
					
							 try {
								value = cls.getDeclaredField(sImage).getInt(null);
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							String imageString=String.valueOf(value);
							//Toast.makeText(SearchActivity.this,imageString,3000).show();
							//Toast.makeText(SearchActivity.this,sImage,3000).show();
							//Toast.makeText(SearchActivity.this,sImage,3000).show();
						Map<String, Object> row = new HashMap<String, Object>();
						row.put("proSrc",value);
						row.put("prodescribe", passenger.getProdescribe());
						row.put("proPrice", "价格:" + passenger.getProPrice());
						row.put("proalrePay", passenger.getProalrePay() + "人已付款");
						row.put("proFare",passenger.getProFare());
						row.put("proAddress", passenger.getProAddress());
						data.add(row);
						i++;
					}

					catergorAdapter.notifyDataSetChanged();

					break;
				case 2:
					Toast.makeText(SearchActivity.this, "服务器错误，请重试",
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(SearchActivity.this, "请重新登录",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}

		};
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		findViewById();
		initView();
	}
	
	

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mEditText = (AutoClearEditText) findViewById(R.id.search_edit);
		mImageButton = (ImageButton) findViewById(R.id.search_button);
		productinfo_listview = (ListView) findViewById(R.id.productinfo_listview);
		catergory_image = (ImageView) findViewById(R.id.catergory_image);

	}

	@Override
	protected void initView() {
				data = new ArrayList<Map<String, Object>>();
				catergorAdapter = new CatergorAdapter();
				productinfo_listview.setAdapter(catergorAdapter);
				mImageButton.setOnClickListener(new OnClickListener(){
					
					
					@Override
					public void onClick(View arg0) {
						
						etString=mEditText.getText().toString();
						if (!NetUtils.check(SearchActivity.this)) {
							Toast.makeText(SearchActivity.this, "服务器连接异常", 3000).show();
							return;
						}
					
						  pDialog = ProgressDialog.show(SearchActivity.this,
						  null, "正在加载中...请稍候", false, true);
						  Toast.makeText(SearchActivity.this,etString,3000).show();

						new Thread(new Runnable() {

							@Override
							public void run() {
								
								//Looper.prepare();
								// TODO Auto-generated method stub
								// 获取message
								Message msg = handler.obtainMessage();
								// 访问服务器文件
								HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findproduct");
								// 发送请求
								DefaultHttpClient client = new DefaultHttpClient();
								// 设置参数
								
								Gson userGson = new Gson();
							    
								List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
								params.add(new BasicNameValuePair("action", "search"));
								params.add(new BasicNameValuePair("aa", etString));

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
										Gson gson = new Gson();
										Product[] products = gson.fromJson(json,
												Product[].class);
										Log.d("products", json);
										// 发送消息
										msg.obj = products;
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
								//Looper.loop();
							}
						}).start();
					}
					
				});
			
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
			layoutInflater = LayoutInflater.from(SearchActivity.this);

			// 组装数据
			if (convertView == null) {

				convertView = layoutInflater.inflate(
						R.layout.activity_productinfo, null);
				holder.image = (ImageView) convertView
						.findViewById(R.id.catergory_image);
				holder.title = (TextView) convertView
						.findViewById(R.id.catergoryitem_title);
				holder.price = (TextView) convertView
						.findViewById(R.id.catergoryitem_content);
				holder.proFare = (TextView) convertView
						.findViewById(R.id.yunfei);
				holder.proalrePay = (TextView) convertView
						.findViewById(R.id.alreadypay);
				holder.address = (TextView) convertView
						.findViewById(R.id.productaddress);

				// 使用tag存储数据
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			

			holder.image.setImageResource((Integer)data.get(position).get("proSrc"));
			holder.title.setText(data.get(position).get("prodescribe").toString());
			holder.price.setText(data.get(position).get("proPrice").toString());
			holder.proFare.setText(data.get(position).get("proFare").toString());
			holder.proalrePay.setText(data.get(position).get("proalrePay").toString());
			holder.address.setText(data.get(position).get("proAddress").toString());
			return convertView;

		}

		

	}
	public static class ViewHolder {
		ImageView image;
		TextView title;
		TextView price;
		TextView proFare;
		TextView proalrePay;
		TextView address;
		
	}
	
}
