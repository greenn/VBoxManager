package com.kedzie.vbox.event;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineActivity;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventNotificationService extends IntentService {

	private static final String TAG = EventNotificationService.class.getSimpleName();
	private static final int NOTIFICATION_ID=1;
	
	public EventNotificationService() {
		super("Event Notification Service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Sending notification");
		IMachine eventMachine = BundleBuilder.getProxy(intent, IMachine.BUNDLE, IMachine.class);
		Intent i = new Intent(EventNotificationService.this, MachineActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)eventMachine.getAPI());
		Utils.cacheProperties(eventMachine);
		BundleBuilder.addProxy(i, IMachine.BUNDLE, eventMachine);
		String title = getString(R.string.notification_title, eventMachine.getName(), eventMachine.getState());
		Notification n =  new NotificationCompat.Builder(EventNotificationService.this)
				.setContentTitle(title)
				.setContentText(getString(R.string.notification_text, eventMachine.getName(), eventMachine.getState()))
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_notif_vbox)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), ((VBoxApplication)getApplication()).getDrawable(eventMachine.getState())))
				.setContentIntent(PendingIntent.getActivity(EventNotificationService.this, 0, i, 0))
				.setTicker(title)
				.setAutoCancel(true)
				.build();
		((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, n);
	}
}
