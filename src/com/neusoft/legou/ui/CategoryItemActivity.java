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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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


public class CategoryItemActivity extends BaseActivity {

	
	private ListView electrical_listview;
	private LayoutInflater layoutInflater;
    private List<HashMap<String, Object>> data=null;
    ProgressDialog pDialog ;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				
				data = new ArrayList<HashMap<String, Object>>();
				data.clear();
				Product[] typepro = (Product[]) msg.obj;
				for (Product product : typepro) {
					HashMap<String, Object> row = new HashMap<String, Object>();
					row.put("proId", product.getProId());
					row.put("mImageIds", product.getProSrc());
					row.put("prodescribe", product.getProdescribe());
					row.put("proPrice", "价格:" + product.getProPrice());
					row.put("proalrePay", product.getProalrePay() + "人已付款");
					row.put("proFare", product.getProFare());
					row.put("proAddress", product.getProAddress());
					data.add(row);
					Log.d("data", data.toString());
				}	
				Log.d("data", data.toString());
				initView();
				break;
			case 2:
				Toast.makeText(CategoryItemActivity.this, "服务器错误..", 3000).show();
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
		setContentView(R.layout.activity_special);
		findViewById();
		//Toast.makeText(CategoryItemActivity.this, "请稍后..", 5000).show();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tread();
	}
	
	public void tread(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				// 获取message
				Message msg = handler.obtainMessage();
				// 访问服务器文件
				HttpPost post = new HttpPost(CONSTANT.HOST + "/lg/findproduct");
				// 发送请求
				DefaultHttpClient client = new DefaultHttpClient();
				// 设置参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("action", "findtypepro"));
				params.add(new BasicNameValuePair("type", "家电"));

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
						
					} else {
						msg.what = 2;
						
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
				handler.sendMessage(msg);
			}
			
		}).start();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		electrical_listview = (ListView) findViewById(R.id.electrical_listview);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	
		electrical_listview.setAdapter(new CatergorAdapter());
		electrical_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int parent, long id) {
				Intent intent = new Intent(CategoryItemActivity.this,
						CategoryItemDetailActivity.class);
				intent.putExtra("proId", data.get(parent).get("proId").toString());
				startActivity(intent);
				Toast.makeText(CategoryItemActivity.this,
						"你点击了第" + parent + 1 + "商品", 1).show();
				
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
			return 0;
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
			layoutInflater = LayoutInflater.from(CategoryItemActivity.this);

			// 组装数据
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.activity_category_liebiao, null);
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
			//赋值
			Class<drawable> cls = R.drawable.class;
	        try {
	        	String img=data.get(position).get("mImageIds").toString();
	            Integer value = cls.getDeclaredField(img).getInt(null);
	            holder.image.setImageResource(value);
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        holder.title.setText(data.get(position).get("prodescribe").toString());
			holder.price.setText(data.get(position).get("proPrice").toString());
			holder.proFare.setText(data.get(position).get("proFare").toString());
			holder.proalrePay.setText(data.get(position).get("proalrePay").toString());
			holder.address.setText(data.get(position).get("proAddress").toString());
			// holder.title.setText(array[position]);

			return convertView;

		}

	}

	// 适配显示的图片数组
	/*private Integer[] mImageIds = { R.drawable.catergory_appliance,
			R.drawable.catergory_book, R.drawable.catergory_cloth,
			R.drawable.catergory_deskbook, R.drawable.catergory_digtcamer,
			R.drawable.catergory_furnitrue, R.drawable.catergory_mobile,
			R.drawable.catergory_skincare };*/
	// 给照片添加文字显示(Title)
	/*private String[] mTitleValues = { "电饭锅", "空调", "冰箱", "电吹风", "电磁炉", "电视",
			"洗衣机","洗衣机"};

	private String[] mContentValues = { "价格", "价格",
			"价格", "价格", "价格", "价格",
			"价格","价格"};*/

	public static class ViewHolder {
		ImageView image;
		TextView title;
		TextView price;
		TextView proFare;
		TextView proalrePay;
		TextView address;
		
	}
	
	


}
