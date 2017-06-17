package com.neusoft.legou.ui;

import java.io.IOException;
import java.math.BigDecimal;
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

import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.R.drawable;
import com.neusoft.legou.entity.Product;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.HistoryHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryItemDetailActivity extends BaseActivity {

	private TextView nowPrice, num, describe,stock;
	private ImageView addtocar, buynow, prodetail, remove, add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category__detail);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		nowPrice = (TextView) findViewById(R.id.nowPrice);
		num = (TextView) findViewById(R.id.buynum);
		stock = (TextView) findViewById(R.id.stock);
		describe = (TextView) findViewById(R.id.describe);
		addtocar = (ImageView) findViewById(R.id.addtocar);
		buynow = (ImageView) findViewById(R.id.buynow);
		prodetail = (ImageView) findViewById(R.id.prodetail);
		remove = (ImageView) findViewById(R.id.remove);
		add = (ImageView) findViewById(R.id.add);

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		//异步获取数据
        pro name=new pro();
        name.execute();
        //点击事件
        add.setOnClickListener(new click());
        remove.setOnClickListener(new click());
        buynow.setOnClickListener(new click());
        addtocar.setOnClickListener(new click());
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
					num.setText(updatenum);
				}else{
					Toast.makeText(CategoryItemDetailActivity.this,
							"没那么多商品..", 500).show();
				}
               
				break;
			case R.id.remove:
				 Integer delnum=Integer.parseInt(num.getText().toString());
				if(delnum>1){
					String updatenum1=String.valueOf(delnum-1);
					num.setText(updatenum1);
				}else{
					num.setText("1");
				}
				 
				break;
			case R.id.buynow:
				
				//跳转页面
				
				//传递参数
				Intent intent1=getIntent();
				String proId1 = intent1.getStringExtra("proId");
				Product pro=new Product();
				pro.setProId(Integer.parseInt(proId1));
				pro.setProNum(Integer.parseInt(num.getText().toString()));
				pro.setStock(Integer.parseInt(stock.getText().toString()));
				BigDecimal decimal=new BigDecimal(Integer.parseInt(nowPrice.getText().toString()));
				pro.setProPrice(decimal);
				pro.setProdescribe(describe.getText().toString());
	
				Gson gson=new Gson();
				String pro1=gson.toJson(pro);
				
				intent1.putExtra("promsg", pro1);
				intent1.setClass(CategoryItemDetailActivity.this, OrderActivity.class);
				startActivity(intent1);
				
				
				break;
			case R.id.addtocar:
                
				// 记录历史
				HistoryHelper helper = new HistoryHelper(CategoryItemDetailActivity.this,
						"cate.db", null, 1);
				SQLiteDatabase db = helper.getWritableDatabase();
				ContentValues values = new ContentValues();
	            Intent intent=getIntent();
				String proId = intent.getStringExtra("proId");
				String pronum=num.getText().toString();
				Log.d("1111", proId);
				String sql="select * from cate where proId=?";
				Cursor cursor=db.rawQuery(sql,new String[]{proId});
				if (cursor.getCount()!=0) {
					Log.d("show", "已有商品");
					values.put("pronum", pronum);
					db.update("cate", values, "_id=proId", null);
				} else {
					values.put("proId", proId);
					values.put("pronum", pronum);
					db.insert("cate", null, values);
				}
				
				db.close();
				helper.close();
				Toast.makeText(CategoryItemDetailActivity.this, 
						"成功加入购物车", 2000).show();
				break;
			default:
				break;
			}
		}
		
	}
	
	
	class pro extends AsyncTask<String, String, Object> {

		@Override
		protected Object doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findproduct");
			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();

			Intent intent = getIntent();
			String proId = intent.getStringExtra("proId");
			Log.d("proId", proId);
			// 设置参数
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("action", "findonepro"));
			params.add(new BasicNameValuePair("proId", proId));
  
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
					Gson gson = new Gson();
					Product product = gson.fromJson(json, Product.class);
					Log.d("products", json);
					return product;

				} else {
					Toast.makeText(CategoryItemDetailActivity.this, "服务器错误...",
							3000).show();

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
			if (result instanceof Product) {
				Product product = (Product) result;
				nowPrice.setText(product.getProPrice().toString());
				stock.setText(product.getStock().toString());
				describe.setText(product.getProdescribe().toString());

				Class<drawable> cls = R.drawable.class;
				try {
					String img = product.getProSrc();
					Integer value = cls.getDeclaredField(img).getInt(null);
					prodetail.setImageResource(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

}
