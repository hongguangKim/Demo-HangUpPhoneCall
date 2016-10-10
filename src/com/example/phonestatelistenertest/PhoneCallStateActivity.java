package com.example.phonestatelistenertest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.example.phoneservice.PhoneService;

public class PhoneCallStateActivity extends Activity {
	private TextView textView;
	private TelephonyManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonestate_main);
		textView = (TextView) findViewById(R.id.tv01);

		// ��ȡTelephonyManager
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(new MyPhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		// ��̬ע��㲥������
		MyReceiver receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter(
				"com.example.phonestatelistenertest");
		registerReceiver(receiver, filter);
	}

	class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			// �ֻ�����
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			// �绰����
			case TelephonyManager.CALL_STATE_RINGING:

				startService(incomingNumber);
				break;
			// �绰��������
			case TelephonyManager.CALL_STATE_OFFHOOK:
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			textView.setText(intent.getStringExtra("msg"));
		}
	}

	private void startService(String incomingNumber) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, PhoneService.class);
		i.putExtra("phoneNum", incomingNumber);
		startService(i);
	}

	private void stopService() {
		Intent i = new Intent(this, PhoneService.class);
		this.stopService(i);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopService();
		super.onDestroy();
	}
}
