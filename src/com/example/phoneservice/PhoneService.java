package com.example.phoneservice;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

public class PhoneService extends Service {
	private ITelephony iTelephony;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("method", "PhoneService->onCreate");
		phoner();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("method", "PhoneService->onStartCommand");

		String phoneNum = intent.getStringExtra("phoneNum");
		String name = getContactNameByPhoneNumber(PhoneService.this, phoneNum);
		try {

			// 睡眠2秒钟，自动挂断
			Thread.sleep(2000);
			iTelephony.endCall();

			// 提示自动挂机
			Toast.makeText(PhoneService.this, "因是会议时间，挂断电话：" + phoneNum,
					Toast.LENGTH_LONG).show();
			Log.i("system.out", "挂断电话：" + phoneNum);

			// 获取当前时间
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			String time = df.format(new Date());

			// 发送到主线程
			Log.i("system.out", "发送到主线程：" + time + "	" + name + "	" + phoneNum
					+ ": 来电");
			Intent i = new Intent();
			i.putExtra("msg", time + "	" + name + "	" + phoneNum + ": 来电");
			i.setAction("com.example.phonestatelistenertest");
			sendBroadcast(i);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	public void phoner() {
		Log.i("method", "PhoneService->phoner");
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			// 获取的是类自身声明的所有方法
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			// 取消java的权限控制检查
			getITelephonyMethod.setAccessible(true);
			// AIDL是公开服务接口的（Declared Method）
			iTelephony = (ITelephony) getITelephonyMethod.invoke(manager,
					(Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/*
	 * 根据电话号码取得联系人姓名
	 */
	public String getContactNameByPhoneNumber(Context context, String address) {

		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ address + "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.

		if (cursor == null) {
			return "未知联系人";
		}

		String name = null;
		if (cursor.moveToFirst()) {
			name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}
		cursor.close();

		if ("".equals(name) || name == null)
			return "未知联系人";
		return name;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("method", "PhoneService->onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("method", "PhoneService->onBind");
		return null;
	}

}
