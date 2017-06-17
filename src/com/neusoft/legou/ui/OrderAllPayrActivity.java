package com.neusoft.legou.ui;


import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.neusoft.legou.R;
import com.neusoft.legou.entity.Order;

import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.NetUtils;

public class OrderAllPayrActivity extends BaseActivity{

	ListView order_list;
	private LayoutInflater layoutInflater;
	 List<Map<String, Object>> data;
	 
	Handler handler = new Handler(){
             
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 清空data
//			if(pDialog!=null){
//				pDialog.dismiss();
//			}
			
			data.clear();
			
			switch (msg.what) {
			case 1:

				// Passenger[] => data
				Order[] listorder = (Order[]) msg.obj;
				// Log.d("pro", products[0].toString());
				int i = 0;
				for (Order passenger : listorder) {
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("orderprice",  passenger.getOrderPrice());
					row.put("number",passenger.getOrderNum());
					row.put("ordertime", passenger.getOrderTime());
				    row.put("orderId", passenger.getOrderId());
				    row.put("price", passenger.getOrderPay());
					data.add(row);
					i++;
					Log.d("products", listorder.toString());
				}

				// CatergorAdapter.notifyDataSetChanged();

				break;
			case 2:
				Toast.makeText(OrderAllPayrActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(OrderAllPayrActivity.this, "请重新登录",
						Toast.LENGTH_SHORT).show();
				break;
			}

		
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_order_list);
		findViewById();
		onResume();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (!NetUtils.check(OrderAllPayrActivity.this)) {
			Toast.makeText(OrderAllPayrActivity.this, "服务器连接异常", 3000).show();
			return;
		}
		/*
		 * // 进度对话框 pDialog = ProgressDialog.show(CategoryItemActivity.this,
		 * null, "正在加载中...", false, true);
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 获取message
				SharedPreferences pref = getSharedPreferences("user",Context.MODE_PRIVATE);
				String userId=String.valueOf(pref.getInt("userid",0));//""的意思是：拿不到就拿下一个
				
				Message msg = handler.obtainMessage();
				// 访问服务器文件
				HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findorder");
				// 发送请求
				
				
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("action", "orderfind"));
				params.add(new BasicNameValuePair("userID", userId));
				// 设置参数
				
				UrlEncodedFormEntity entity;
				try {
					entity = new UrlEncodedFormEntity(params, "UTF-8");
					
					
					DefaultHttpClient client = new DefaultHttpClient();
					post.setEntity(entity);
	             	// 超时设置
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONSTANT.REQUEST_TIMEOUT);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);
					HttpResponse response = client.execute(post);
			//		Log.d("products", listorder.toString());
					// 处理结果
					if (response.getStatusLine().getStatusCode() == 200) {

						String json = EntityUtils.toString(response.getEntity());
						Gson gson = new Gson();
						Order[] listorder = gson.fromJson(json,
								Order[].class);
						
						Log.d("listorder", listorder.toString());
//						Toast.makeText(OrderAllPayrActivity.this,listorder.toString(),
//								Toast.LENGTH_SHORT).show();
						// 发送消息
						msg.obj = listorder;
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
	ImageView goodspic;
	
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

		order_list = (ListView) this.findViewById(R.id.order_list);
		  goodspic =  (ImageView) this.findViewById(R.id.goodspic);
		
	}
    
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		data = new ArrayList<Map<String, Object>>();
		CatergorAdapter catergorAdapter = new CatergorAdapter();
		order_list.setAdapter(catergorAdapter);
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
	//	private LayoutInflater layoutInflater;
		@SuppressWarnings("null")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = new ViewHolder();
			layoutInflater = LayoutInflater.from(OrderAllPayrActivity.this);

			// 组装数据
			if (convertView == null) {

				convertView = layoutInflater.inflate(
						R.layout.activity_all_order, null);
				holder.tvOrderPrice = (TextView) convertView.findViewById(R.id.tvOrderPrice);
				holder.number = (TextView) convertView.findViewById(R.id.number);
				holder.ordertime = (TextView) convertView.findViewById(R.id.tvOrderDateFrom);
                holder.orderId = (TextView) convertView.findViewById(R.id.tvOrderId);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.tvOrderDateFrom = (TextView) convertView.findViewById(R.id.tvOrderDateFrom);
                holder.describe = (TextView) convertView.findViewById(R.id.describe);
                holder.weight = (TextView) convertView.findViewById(R.id.weight);
				// 使用tag存储数据
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
		//	holder.weight.setText(data.get(position).get("weight").toString());
			holder.price.setText(data.get(position).get("price").toString());
		//	holder.describe.setText(data.get(position).get("describe").toString());
			holder.number.setText(data.get(position).get("number").toString());
		//	holder.tvOrderDateFrom.setText(data.get(position).get("tvOrderDateFrom").toString());
			holder.tvOrderPrice.setText(data.get(position).get("orderprice").toString());
			holder.ordertime.setText(data.get(position).get("ordertime").toString());
			holder.orderId.setText(data.get(position).get("orderId").toString());
			// holder.title.setText(array[position]);

			return convertView;

		}
	}
	public static class ViewHolder {
		
		
		TextView number;
		TextView tvOrderPrice;
		TextView ordertime;
		TextView orderId;
		TextView price;
		TextView tvOrderDateFrom;
		TextView describe;
		TextView weight;
	}
}
