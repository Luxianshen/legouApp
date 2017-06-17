package com.neusoft.legou.ui;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.R.drawable;
import com.neusoft.legou.entity.Order;
import com.neusoft.legou.entity.Product;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.HistoryHelper;

public class OrderActivity extends BaseActivity{

	
	private TextView consignee,telephone,conname,describe,price,
	                 total,sum,sumcon,num,stock;
	private ImageView remove,add,submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_order);
		findViewById();
		orderdata(null);
		initView();
	}
	
	private void orderdata(String updatenum) {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		String aa=intent.getStringExtra("promsg");
		Gson gson=new Gson();
		Product product=gson.fromJson(aa, Product.class);
		price.setText(product.getProPrice().toString());
		describe.setText(product.getProdescribe());
		stock.setText(product.getStock().toString());
		if(updatenum==null){
			BigDecimal answer= product.getProPrice().multiply(new BigDecimal(product.getProNum()));
			String sumAll=answer.toString();
			num.setText(product.getProNum().toString());
			total.setText("共"+product.getProNum().toString()+"件商品");
			sum.setText(sumAll);
			sumcon.setText(sumAll);
		}else{
			BigDecimal answer= product.getProPrice().multiply(new BigDecimal(Integer.parseInt(updatenum)));
			String sumAll=answer.toString();
			num.setText(updatenum);
			total.setText("共"+updatenum+"件商品");
			sum.setText(sumAll);
			sumcon.setText(sumAll);
		}
		
		
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		consignee=(TextView) findViewById(R.id.consignee);
		telephone=(TextView) findViewById(R.id.telephone);
		conname=(TextView) findViewById(R.id.conname);
		describe=(TextView) findViewById(R.id.describe);
		price=(TextView) findViewById(R.id.price);
		num=(TextView) findViewById(R.id.num);
		total=(TextView) findViewById(R.id.total);
		stock=(TextView) findViewById(R.id.stock);
		sum=(TextView) findViewById(R.id.sum);
		sumcon=(TextView) findViewById(R.id.sumcon);
		submit=(ImageView) findViewById(R.id.submit);
		add=(ImageView) findViewById(R.id.add);
		remove=(ImageView) findViewById(R.id.remove);
		
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		add.setOnClickListener(new click());
		remove.setOnClickListener(new click());
		submit.setOnClickListener(new click());
		
		
	}
	
	class click implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.add:
				int kuncun=Integer.parseInt(stock.getText().toString());
				Integer addnum=Integer.parseInt(num.getText().toString());
				if (kuncun > addnum) {

					String updatenum = String.valueOf(addnum + 1);
					orderdata(updatenum);
				}else{
					Toast.makeText(OrderActivity.this,
							"没那么多商品..", 500).show();
				}
               
				break;
			case R.id.remove:
				 Integer delnum=Integer.parseInt(num.getText().toString());
				if(delnum>1){
					String updatenum = String.valueOf(delnum-1);
					orderdata(updatenum);
				}else{
					orderdata("1");
				}
				 
				break;
			case R.id.submit:
				
				//异步提交数据到服务器
				Ordernew order=new Ordernew();
				order.execute();
				
				//跳转到相应的activity转带我的订单
//				Intent intent=new Intent(OrderActivity.this,OrderAllPayrActivity.class);
//			    startActivity(intent);
				Toast.makeText(OrderActivity.this, "提交订单成功",
				3000).show();

				
				break;
			
			default:
				break;
			}
		}
		
	}
	
	class Ordernew extends AsyncTask<String, String, Object> {

		@Override
		protected Object doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findorder");
			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();

			//获取userID
			Intent intent=getIntent();
			String aa=intent.getStringExtra("promsg");
			Gson gson=new Gson();
			Product product=gson.fromJson(aa, Product.class);
			
			
			Date curDate = new Date(System.currentTimeMillis());
			
			  Order data=new Order();
			 SharedPreferences preferences=getSharedPreferences("user",
						Context.MODE_PRIVATE);
			 String userid=String.valueOf(preferences.getInt("userid", 0));
			 
			  data.setUserID(Integer.parseInt(userid));
			  data.setProId(product.getProId());
			  data.setOrderTime(curDate);
			  data.setOrderNum(product.getProNum());
			  data.setOrderIsDel("1");
			  data.setOrderPay("0");
			  data.setOrderPrice(product.getProPrice());
			  String order=gson.toJson(data);
			// 设置参数
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("action", "ordernew"));
			params.add(new BasicNameValuePair("order", order));
  
			// 超时设置
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT,
					CONSTANT.REQUEST_TIMEOUT);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					CONSTANT.SO_TIMEOUT);
			UrlEncodedFormEntity entity;
			String result = "";
			try {
				entity = new UrlEncodedFormEntity(params, "UTF-8");

				post.setEntity(entity);

				HttpResponse response = client.execute(post);

				// 处理结果
				if (response.getStatusLine().getStatusCode() == 200) {

					String json = EntityUtils.toString(response.getEntity());
					
				    result = gson.fromJson(json, String.class);
					

				} else {
					/*Toast.makeText(OrderActivity.this, "服务器错误...",
							3000).show();*/

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
			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//Toast.makeText(OrderActivity.this, "chenggong", 2000).show();
		    
	}
	}
}
