package com.XMPP.Activity.Login;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.XMPP.R;
import com.XMPP.Activity.Mainview.MainviewActivity;
import com.XMPP.Service.GroupProfile;
import com.XMPP.Service.LoginService;
import com.XMPP.Service.LoginService.LocalBinder;
import com.XMPP.smack.Smack;
import com.XMPP.util.Constants;
import com.XMPP.util.Test;

public class LoginActivity extends Activity implements OnClickListener {

	private String username;
	private String password;
	private TextView submitLogin;
	private TextView forget;
	private Smack smack;

	LoginService mService;
	boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		init();
		// Bind to LocalService
		Intent intent = new Intent(this, LoginService.class);
		intent.putExtra("server", Constants.SERVER_IP);
		intent.putExtra("port", Constants.SERVER_PORT);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void init() {
		submitLogin = (TextView) findViewById(R.id.submitLogin);
		forget = (TextView) findViewById(R.id.forget);
		submitLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submitLogin:
			if (mBound) {
				// Call a method from the LoginService.
				// However, if this call were something that might hang, then
				// this request should
				// occur in a separate thread to avoid slowing down the activity
				// performance.
				username = ((EditText) findViewById(R.id.username)).getText()
						.toString();
				password = ((EditText) findViewById(R.id.password)).getText()
						.toString();
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final int LoginResult = mService.login(username,
								password);
						final ArrayList<GroupProfile> groupList = mService
									.getGroupList();
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								switch (LoginResult) {
								case Constants.LOGIN_SUCCESS:
									Intent intent = new Intent(
											LoginActivity.this,
											MainviewActivity.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("GroupList", groupList);
									intent.putExtras(bundle);
									LoginActivity.this.startActivity(intent);
									break;
								case Constants.LOGIN_CONNECT_FAIL:
									Toast.makeText(
											getApplicationContext(),
											getResources().getString(
													R.string.connect_fail),
											Toast.LENGTH_SHORT).show();
									break;
								case Constants.LOGIN_USERNAME_PSW_ERROR:
									Toast.makeText(
											getApplicationContext(),
											getResources().getString(
													R.string.name_psw_wrong),
											Toast.LENGTH_SHORT).show();
								}

							}
						});

					}
				}).start();

			}

			break;
		case R.id.forget:

			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LoginService, cast the IBinder and get
			// LoginService instance
			Test.outputCertainString("to here ", "LoginActivity onServiceConnected");
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

}
