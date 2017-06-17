package com.neusoft.legou.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.legou.R;
import com.neusoft.legou.ui.base.BaseActivity;
import com.neusoft.legou.utils.CommonTools;
import com.neusoft.legou.utils.ExitView;
import com.neusoft.legou.widgets.CustomScrollView;

public class PersonalCenterActivity extends BaseActivity implements OnClickListener {

	private ImageView mBackgroundImageView = null;
	private Button mLoginButton, mMoreButton, mExitButton;
	private CustomScrollView mScrollView = null;
	private Intent mIntent = null;
	private ExitView exit;
	private LinearLayout Ly_login, Ly_Other;
	private RelativeLayout Ly_personalInfo,waitPay,allPay,personalinfo;
	private TextView username;
	private int LOGIN_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mBackgroundImageView = (ImageView) findViewById(R.id.personal_background_image);
//		mLoginButton = (Button) this.findViewById(R.id.personal_login_button);
		mScrollView = (CustomScrollView) findViewById(R.id.personal_scrollView);
		mMoreButton = (Button) findViewById(R.id.personal_more_button);
		mExitButton = (Button) this.findViewById(R.id.personal_exit);
		waitPay = (RelativeLayout) findViewById(R.id.waitpay);
		allPay=(RelativeLayout) findViewById(R.id.allpay);
//		Ly_login = (LinearLayout) findViewById(R.id.login);
		Ly_personalInfo = (RelativeLayout) findViewById(R.id.personal);
		Ly_Other = (LinearLayout) findViewById(R.id.other_layout);
		username = (TextView) findViewById(R.id.username);
		personalinfo=(RelativeLayout) findViewById(R.id.personalinfo);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mScrollView.setImageView(mBackgroundImageView);
		
//		mLoginButton.setOnClickListener(this);
		mMoreButton.setOnClickListener(this);
		mExitButton.setOnClickListener(this);
		waitPay.setOnClickListener(this);
		allPay.setOnClickListener(this);
		mExitButton.setOnClickListener(this);
		personalinfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// CommonTools.showShortToast(PersonalActivity.this, "稍后开放");
		switch (v.getId()) {
//		case R.id.personal_login_button:
//			mIntent = new Intent(PersonalCenterActivity.this,LoginActivity.class);
//			startActivity(mIntent);
//			// startActivityForResult(mIntent, LOGIN_CODE);
//			break;

		/*case R.id.personal_more_button:
			mIntent = new Intent(PersonalActivity.this, MoreActivity.class);
			startActivity(mIntent);
			break;*/
			
		case R.id.waitpay:
			mIntent = new Intent(PersonalCenterActivity.this, OrderWaitPayrActivity.class);
			startActivity(mIntent);
			break;
		
		case R.id.allpay:
			mIntent = new Intent(PersonalCenterActivity.this,OrderAllPayrActivity.class);
			startActivity(mIntent);
			break;	

		case R.id.personal_exit:

			// 实例化SelectPicPopupWindow
			exit = new ExitView(PersonalCenterActivity.this, itemsOnClick);
			// 显示窗口
			exit.showAtLocation(
					PersonalCenterActivity.this.findViewById(R.id.layout_personal_login),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

			break;
		case R.id.personalinfo:
			mIntent = new Intent(PersonalCenterActivity.this,PersonalInfoActivity.class);
			startActivity(mIntent);
			break;
		default:
			break;
		}

	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == 20) {
			// String name=data.getExtras().getString("username");
			// Log.i("name", name);
			// username.setText(name);
			if (Ly_login.isShown()) {
				Ly_personalInfo.setVisibility(View.VISIBLE);
				Ly_login.setVisibility(View.GONE);
				Ly_Other.setVisibility(View.VISIBLE);
			}
			Ly_personalInfo.setVisibility(View.VISIBLE);
			Ly_login.setVisibility(View.GONE);
			Ly_Other.setVisibility(View.VISIBLE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
*/
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_exit:
				//CommonTools.showShortToast(PersonalCenterActivity.this, "退出程序");
				//读取user文件
				SharedPreferences perf = getSharedPreferences("user", 0);
				
				Editor editor = perf.edit();			
				editor.remove("username");
				editor.commit();
				
                //Intent intent = new Intent(PersonalCenterActivity.this,HomeActivity.class);
				
                //Toast.makeText(PersonalCenterActivity.this, "退出成功", 3000).show();
                
                PersonalCenterActivity.this.finish();
                //startActivity(intent);
				break;
			case R.id.btn_cancel:
				PersonalCenterActivity.this.dismissDialog(R.id.btn_cancel);

				break;
			default:
				break;
			}
		}
	};

}
