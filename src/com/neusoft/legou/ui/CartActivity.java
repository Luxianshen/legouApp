package com.neusoft.legou.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.neusoft.legou.R;
import com.neusoft.legou.R.drawable;
import com.neusoft.legou.entity.Product;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CONSTANT;
import com.neusoft.legou.utils.HistoryHelper;


public class CartActivity extends BaseActivity {

    private List<HashMap<String,Object>> data;
	private LayoutInflater layoutInflater;
	private ImageView goodspic;
	private TextView describe,price,number,totalprice;
	private ListView catergory_listview;
	private Button bt_submit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		findViewById();
		List<String> carid=findcar();
		Log.d("test", carid.toString());
		if(carid.size()>0){
			
			Gson gson = new Gson();
			String proIds = gson.toJson(carid);
			// String proIds=carid.toString();
			Log.d("2222", proIds);
			Findpro findpro=new Findpro();
			findpro.execute(proIds);
		}else{
			Toast.makeText(CartActivity.this, "购物车是空的！", 2000).show();
		}
		
	}
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	List<String> carid=findcar();
		Log.d("test", carid.toString());
		if(carid.size()>0){
			
			Gson gson = new Gson();
			String proIds = gson.toJson(carid);
			// String proIds=carid.toString();
			Log.d("2222", proIds);
			Findpro findpro=new Findpro();
			findpro.execute(proIds);
		}else{
			Toast.makeText(CartActivity.this, "购物车是空的！", 2000).show();
		}
    }
    
	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		catergory_listview=(ListView) findViewById(R.id.catergory_listview);
		totalprice=(TextView) findViewById(R.id.totalprice);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		catergory_listview.setAdapter(new CartAdapter());
		catergory_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int parent, long id) {
				/*Intent intent = new Intent(CategoryItemActivity.this,
						CategoryItemDetailActivity.class);
				intent.putExtra("proId", data.get(parent).get("proId").toString());
				startActivity(intent);
				Toast.makeText(CategoryItemActivity.this,
						"你点击了第" + parent + 1 + "商品", 1).show();*/
				
			}
		});
	}

	public List<String> findcar() {
		// 查询历史记录
		HistoryHelper helper = new HistoryHelper(CartActivity.this, "cate.db",
				null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();

		String sql = "select * from cate";
		Cursor cursor = db.rawQuery(sql, null);

		List<String> carid = new ArrayList<String>();
		List<String> numcar = new ArrayList<String>();
		while (cursor.moveToNext()) {

			String proId = cursor.getString(1);
			String pronum = cursor.getString(2);
			carid.add(proId);
			numcar.add(pronum);
			Log.d("3333", proId);
		}

		
		// 释放资源
		cursor.close();
		db.close();
		helper.close();
		return carid;
	}
	
	private class CartAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("null")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = new ViewHolder();
			layoutInflater = LayoutInflater.from(CartActivity.this);

			// 组装数据
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.activity_cart_item, null);
				holder.goodspic = (ImageView) convertView
						.findViewById(R.id.goodspic);
				holder.describe = (TextView) convertView
						.findViewById(R.id.describe);
				holder.price = (TextView) convertView
						.findViewById(R.id.price);
				holder.number = (TextView) convertView
						.findViewById(R.id.number);
				holder.btn_radio = (CheckBox) convertView
						.findViewById(R.id.btn_radio);
			/*	holder.deletepricture=(Button) convertView.
						findViewById(R.id.delete)*/;
		
				// 使用tag存储数据
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			//赋值
			Class<drawable> cls = R.drawable.class;
	        try {
	        	String img=data.get(position).get("mImageIds").toString();
	            Integer value = cls.getDeclaredField(img).getInt(null);
	            holder.goodspic.setImageResource(value);
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        holder.describe.setText(data.get(position).get("prodescribe").toString());
			holder.price.setText(data.get(position).get("proPrice").toString());
			holder.number.setText(data.get(position).get("proNum").toString());
			/*holder.deletepricture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					HistoryHelper helper = new HistoryHelper(CartActivity.this, "cate.db",
							null, 1);
					SQLiteDatabase db = helper.getReadableDatabase();
					String proId = String.valueOf(data.get(position).get(
							"proId"));
					String sql = "delete from cate where proId=?";
					Cursor cursor = db.rawQuery(sql, new String[] { proId });
					String s = String.valueOf(cursor.getCount());
					Toast.makeText(CartActivity.this, s, 2000).show();
					Toast.makeText(CartActivity.this, proId, 2000).show();
					Toast.makeText(CartActivity.this, "已删除！", 2000).show();
					db.close();
					helper.close();
					data.remove(position);
					notifyDataSetChanged();
				}
			});*/
			holder.btn_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton checkButton, boolean isCheck) {
					// TODO Auto-generated method stub
					if(isCheck){
						int pronum=Integer.parseInt(data.get(position).get("proNum").toString());
						int proprice=Integer.parseInt(data.get(position).get("proPrice").toString());
						int total =pronum*proprice;
						data.get(position).put("total", total);
						int zongjia=0;
						for(int i=0;i<10;i++){
							if(i<data.size()){
								if(data.get(i).get("total")==null){
									zongjia=zongjia+0;
								}else{
									int mony=Integer.parseInt(data.get(i).get("total").toString());
									zongjia=zongjia+mony;
								}
							}
							
						}
						totalprice.setText(String.valueOf(zongjia));
						
						
					}else{
						data.get(position).put("total", 0);
						int zongjia=0;
						for(int i=0;i<10;i++){
							if(i<data.size()){
								if(data.get(i).get("total")==null){
									zongjia=zongjia+0;
								}else{
									int mony=Integer.parseInt(data.get(i).get("total").toString());
									zongjia=zongjia+mony;
								}
							}
							
						}
						totalprice.setText(String.valueOf(zongjia));
						
					}
				}
			});

			return convertView;

		}

	}
	public static class ViewHolder {
		ImageView goodspic;
		TextView describe;
		TextView price;
		TextView number;
		CheckBox btn_radio;
		//Button deletepricture;
		
	}
	
	
	
	class Findpro extends AsyncTask<String, String, Object> {

		@Override
		protected Object doInBackground(String... proIds) {
			// TODO Auto-generated method stub

			HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findproduct");
			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();

			String[] a=proIds;
			// 设置参数
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("action", "findcarpro"));
			params.add(new BasicNameValuePair("listid", a[0]));

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
					Gson gson=new Gson();
					Product[] products = gson.fromJson(json, Product[].class);
					Log.d("products", json);
					return products;

				} else {
					Toast.makeText(CartActivity.this, "服务器错误...",
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
			
			//准备数据data
			
			
			if (result instanceof Product[]) {
				Product[] products = (Product[]) result;
				data = new ArrayList<HashMap<String, Object>>();
				data.clear();
				
				for (Product product : products) {
					HashMap<String, Object> row = new HashMap<String, Object>();
					row.put("proId", product.getProId());
					row.put("mImageIds", product.getProSrc());
					row.put("prodescribe", product.getProdescribe());
					row.put("proPrice", product.getProPrice());
					row.put("proNum", product.getProNum());
					data.add(row);
					Log.d("data", data.toString());
				}
				
				// 查询历史记录
				HistoryHelper helper = new HistoryHelper(CartActivity.this, "cate.db",
						null, 1);
				SQLiteDatabase db = helper.getReadableDatabase();
				String sql = "select * from cate";
				Cursor cursor = db.rawQuery(sql, null);

				List<String> numcar = new ArrayList<String>();
				while (cursor.moveToNext()) {
					
					String pronum = cursor.getString(2);
					numcar.add(pronum);
			
				}
				// 释放资源
				cursor.close();
				db.close();
				helper.close();
				int i=0;
				for (String string : numcar) {	
					data.get(i).put("proNum", string);
					i++;
				}
				
				Log.d("data", data.toString());
				initView();
			}
		}
	}

}
