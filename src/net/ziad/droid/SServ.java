package net.ziad.droid;

import android.app.Service;
import android.os.IBinder;
import java.lang.reflect.Method;
import android.content.Intent;
import java.lang.ClassLoader;
import java.lang.ClassNotFoundException;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import android.widget.Toast;
import android.content.Context; 
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.widget.EditText;
import java.lang.reflect.Constructor;

public class SServ extends Service {
    Context ctx = net.ziad.droid.Scheme.getAppContext();
    String ssentry = net.ziad.droid.Scheme.sentry;
    public static EditText txtv = net.ziad.droid.Scheme.console;
    public static EditText etxt = net.ziad.droid.Scheme.entry;
    NotificationManager notificationManager;
    NotificationCompat.Builder notification;
    PendingIntent pendingIntent;
    int pid = android.os.Process.myPid();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
	android.os.Process.killProcess(pid);
	super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    Intent notificationIntent = new Intent(this, Scheme.class);
    notificationIntent.setAction(Intent.ACTION_MAIN);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notification = new NotificationCompat.Builder(this);
    notification.setContentTitle("sdroid");				
    notification.setContentText("server");
    notification.setSmallIcon(R.drawable.ic_launcher);
    notification.setContentIntent(pendingIntent);
    notification.setOngoing(true);   
    startForeground(1337,notification.build());
    notificationManager.notify(1337, notification.build());
    dalvik.system.DexClassLoader loader = new dalvik.system.DexClassLoader("/sdcard/Download/java/libs/" + Scheme.fname + ".jar", "/data/data/net.ziad.droid", null, ClassLoader.getSystemClassLoader());   
	try{
	    Class iclass = Class.forName(Scheme.fname, true, loader);
	    Toast.makeText(SServ.this,"C=" + iclass.getName(),Toast.LENGTH_LONG).show();
	    Constructor cc = iclass.getDeclaredConstructors()[0];
	    Object object    = cc.newInstance();
	    Method me = iclass.getDeclaredMethod("loader", Context.class, EditText.class, EditText.class, String.class);
	    me.invoke(object, ctx, txtv, etxt, ssentry);
	}
	catch (ClassNotFoundException ex){}
	catch(NoSuchMethodException ex){}
	catch(InstantiationException ex) {}
	catch(InvocationTargetException ex){}
	catch(IllegalAccessException ex){}
        return START_STICKY;
    }
}
