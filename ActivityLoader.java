// rm bin/*
// javac -target 1.6 -source 1.6 -d bin -classpath /home/ziad/downloads/android-sdk/platforms/android-23/android.jar /home/ziad/ActivityLoader.java
// /home/ziad/downloads/android-sdk/build-tools/23.0.1/dx --dex  --output=use.dex bin
// adb push use.dex /sdcard/

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.content.Context;
import android.content.Intent;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Field;
import android.widget.Toast;

public class ActivityLoader  {
    public ClassLoader ORIGINAL_LOADER;
    public ClassLoader CUSTOM_LOADER = null;
    
    public String zaz (Context ctx, String name, String path) {
	    Toast.makeText(ctx, "start", Toast.LENGTH_SHORT).show();

	try {
	    File dex = ctx.getDir("dex", Context.MODE_PRIVATE);
	    dex.mkdir();
	    File f = new File(dex, name);
	    FileInputStream fis = new FileInputStream(path);
	    FileOutputStream fos = new FileOutputStream(f);
	    byte[] buffer = new byte[0xFF];
	    int len;
	    while ((len = fis.read(buffer)) > 0) {
		fos.write(buffer, 0, len);
	    }
	    fis.close();
	    fos.close();

	    File fo = ctx.getDir("outdex", Context.MODE_PRIVATE);
	    fo.mkdir();
	    DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
						    fo.getAbsolutePath(), null,
						    java.lang.ClassLoader.getSystemClassLoader());
	    CUSTOM_LOADER = dcl;
	    Toast.makeText(ctx, String.valueOf(dcl) + "loaded, try launch again", Toast.LENGTH_LONG).show();

	} catch (Exception e) {
	    Toast.makeText(ctx, String.valueOf(e), Toast.LENGTH_LONG).show();
	    CUSTOM_LOADER = null;
	    return String.valueOf(e);
	}
	try {
	    Context mBase = new Smith<Context>(ctx, "mBase").get();

	    Object mPackageInfo = new Smith<Object>(mBase, "mPackageInfo")
		.get();

	    Smith<ClassLoader> sClassLoader = new Smith<ClassLoader>(
								     mPackageInfo, "mClassLoader");
	    ClassLoader mClassLoader = sClassLoader.get();
	    ORIGINAL_LOADER = mClassLoader;

	    MyClassLoader cl = new MyClassLoader(mClassLoader);
	    sClassLoader.set(cl);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return String.valueOf(ORIGINAL_LOADER);
    }

    class MyClassLoader extends ClassLoader {
	public MyClassLoader(ClassLoader parent) {
	    super(parent);
	}

	@Override
	public Class<?> loadClass(String className)
	    throws ClassNotFoundException {
	    if (CUSTOM_LOADER != null) {
		if (className.startsWith("net.ziad.")) {
		}
		try {
		    Class<?> c = CUSTOM_LOADER.loadClass(className);
		    if (c != null)
			return c;
		} catch (ClassNotFoundException e) {
		}
	    }
	    return super.loadClass(className);
	}
    }



    class Smith<T> {
	private Object obj;
	private String fieldName;

	private boolean inited;
	private Field field;

	public Smith(Object obj, String fieldName) {
	    if (obj == null) {
		throw new IllegalArgumentException("obj cannot be null");
	    }
	    this.obj = obj;
	    this.fieldName = fieldName;
	}

	private void prepare() {
	    if (inited)
		return;
	    inited = true;

	    Class<?> c = obj.getClass();
	    while (c != null) {
		try {
		    Field f = c.getDeclaredField(fieldName);
		    f.setAccessible(true);
		    field = f;
		    return;
		} catch (Exception e) {
		} finally {
		    c = c.getSuperclass();
		}
	    }
	}

	public T get() throws NoSuchFieldException, IllegalAccessException,
			      IllegalArgumentException {
	    prepare();

	    if (field == null)
		throw new NoSuchFieldException();

	    try {
		@SuppressWarnings("unchecked")
		    T r = (T) field.get(obj);
		return r;
	    } catch (ClassCastException e) {
		throw new IllegalArgumentException("unable to cast object");
	    }
	}

	public void set(T val) throws NoSuchFieldException, IllegalAccessException,
				      IllegalArgumentException {
	    prepare();

	    if (field == null)
		throw new NoSuchFieldException();

	    field.set(obj, val);
	}
    }
}


