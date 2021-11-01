package net.ziad.droid;

import android.app.Service;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.app.PendingIntent;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import jscheme.JScheme;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextWatcher;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.text.Editable;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;
import android.os.PowerManager;

public class Scheme extends Activity {
    public static EditText entry;
    public static String sentry;
    public static EditText console;
    private JScheme js;
    String text;
    ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);
    ForegroundColorSpan fcsb = new ForegroundColorSpan(Color.BLUE); 
    private static Context context;
    String po = "(";
    String pc = ")";
    PrintWriter str;
    public static String fname = "Test";
    int chx = 0;
    String tmpname;
    public static Boolean serviceRunning = false;

    public static void deleteFolder(File folder) throws IOException {
	File[] files = folder.listFiles();
	if(files!=null) { //some JVMs return null for empty dirs
	    for(File f: files) {
		f.delete();
	    }
	}
    }

    public static void move (String from, String to) {
        File file = new File(from); 
        if (file.renameTo (new File(to))) {
            file.delete(); 
        }    
    }

    public static Context getAppContext() {
	return Scheme.context;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(baos);
	System.setOut(ps);
        js = new JScheme();   
	entry = (EditText)findViewById(R.id.entry);
	console = (EditText)findViewById(R.id.console);
	Scheme.context = getApplicationContext();
	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");

	Button evb = (Button) findViewById(R.id.evb);
	Button evline = (Button) findViewById(R.id.evline);
	Button compb = (Button) findViewById(R.id.compb);
	Button rt = (Button) findViewById(R.id.rt);
	Button delb = (Button) findViewById(R.id.delb);
	Button clear = (Button) findViewById(R.id.clear);
	
	evb.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    String code = "(begin " + entry.getText().toString() + ")";
		    String resp;	    
		    try {
			
			Object o = js.eval(code);
			resp = jsint.U.stringify(o);

		    } catch (jscheme.SchemeException e) {
			resp = e.getMessage();
		    } catch (jsint.BacktraceException e) {
			resp = e.getMessage();
		    } catch (Exception e) {
			resp = "Generic Error: " + e.toString();
		    }
			    
		    if (baos.size() == 0) {
			console.append("\n> " + resp);
		    }
		    else {	
			console.append("\n> " + baos.toString() + "\n"  + resp);
		    }
		    baos.reset();
		}
	    });

	evline.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    String code = entry.getText().toString();
		    int index = entry.getSelectionEnd();
		    String line=code.substring(0, index);
		    String resp;	    
		    try {
		    	Object o = js.eval(line);
		    	resp = jsint.U.stringify(o);

		    } catch (jscheme.SchemeException e) {
		    	resp = e.getMessage();
		    } catch (jsint.BacktraceException e) {
		    	resp = e.getMessage();
		    } catch (Exception e) {
		    	resp = "Generic Error: " + e.toString();
		    }
			    
		    if (baos.size() == 0) {
		    	console.append("\n> " + resp);
		    }
		    else {	
		    	console.append("\n> " + baos.toString() + "\n"  + resp);
		    }
		    baos.reset(); 
		    entry.setText(code.substring(index));
		}
	    });

	compb.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    
		    org.eclipse.jdt.internal.compiler.batch.Main ecjMain = new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(System.out), new PrintWriter(System.err), false, null);

		    String[] argv = {"-encoding", "UTF-8", "-1.5" ,"-time", "-proceedOnError", "-verbose","-progress","-log","/sdcard/Download/java/ecjlog.txt", "-bootclasspath", "/sdcard/Download/java/android.jar", "-classpath", "/sdcard/Download/java/compiled", "/sdcard/Download/java/compiled/" + fname + ".java"};
		    boolean result = ecjMain.compile(argv);
		    move("/sdcard/Download/java/compiled/" + fname + ".java", "/sdcard/Download/java/code/bu_" + fname + ".java");
		    
		    com.android.dx.command.Main.main(new String[] {"--dex", "--output=/sdcard/Download/java/compiled/" + fname + ".jar", "/sdcard/Download/java/compiled" });

		    console.append("\n> " + Boolean.toString(result));
		    entry.setText("(define loader (dalvik.system.DexClassLoader. \"/sdcard/Download/java/compiled/" + fname + ".jar\" \"/data/data/net.ziad.droid\" #null (ClassLoader.getSystemClassLoader))) \n (define iclass (.loadClass loader \"" + fname + "\")) \n (define zclass (vector-ref (.getDeclaredConstructors iclass) 0)) \n (define zclassn (.newInstance zclass #null))");
		}
	    });


	rt.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    if(serviceRunning == false) {
			Toast.makeText(Scheme.this,"started",Toast.LENGTH_LONG).show();
			sentry = entry.getText().toString();
			startService(new Intent(Scheme.this, SServ.class));
			wl.acquire();
			serviceRunning = true;
		    }
		    else {
			new AlertDialog.Builder(Scheme.this)
			    .setMessage("Kill Service?")
			    .setCancelable(false)
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
					Toast.makeText(Scheme.this,"stopped",Toast.LENGTH_LONG).show();
					stopService(new Intent(Scheme.this, SServ.class));
					if (wl.isHeld())
					    {
						wl.release();
					    }
					serviceRunning = false;
				    }
				})
			    .setNegativeButton("No", null)
			    .show();
		    }
		}
	    });

	delb.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    tmpname = entry.getText().toString();
		    if (tmpname.indexOf("_") == 0) {
			fname =tmpname.substring(1);
			try {
			    deleteFolder(new File("/sdcard/Download/java/compiled"));
			}
			catch (IOException IO) {
			}
		    }
		    Intent inte = Scheme.context.getPackageManager().getLaunchIntentForPackage("com.aor.droidedit.pro");
		    Scheme.context.startActivity(inte);
		}
	    });

	clear.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    entry.setText("");
		    console.setText("");
		}
	    });

	entry.addTextChangedListener(new TextWatcher() {
		@Override
		public void beforeTextChanged (CharSequence text, int start, int before, int count)  {
		}

		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count)  { 
		}

		@Override
		public void afterTextChanged(Editable s) { 
		    String s1 = s.toString();
		    String s2 = s.toString();
		    int in=0;
		    int in2=0;
		    while ((in = s1.indexOf(po,in)) >= 0) {
			s.setSpan(
				  fcs,
				  in,
				  in + po.length(),
				  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			in += po.length();
		    }

		    while ((in2 = s2.indexOf(pc,in2)) >= 0) {
			s.setSpan(
				  fcsb,
				  in2,
				  in2 + pc.length(),
				  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			in2 += pc.length();
		    }

		}	
	    });

    }

    @Override
    public void onBackPressed() {
	new AlertDialog.Builder(this)
	    .setMessage("exit jscheme?")
	    .setCancelable(false)
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			finish();
		    }
		})
	    .setNegativeButton("No", null)
	    .show();
    }
}
