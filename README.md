# sdroid

This is a <a href="jscheme https://norvig.com/jscheme.html">jscheme</a> interpreter and a java compiler for android.
It has the ability to  interpret and compile java code to class ->  dex/jar using ecj compiler and dx - the java compilance level
however is at 1.5 (problems to have java  8 or 9 compilation directly on the android platform are well known).


### features
 <li>Scheme interpreter</li>
 <li>Access to methods, contructors, and fields of java/android classes</li>
 <li>Compilation of java code</li>
 <li>run a foreground server</li>
 <li>an associated editor</li>

## Dependencies and installation

  ecj.jar and dx.jar are included and the compilation.
  An external editor app should be installed.
  I use com.aor.droidedit.pro, if you like to use another
  editor simply replace your package new in here:
  Scheme.context.getPackageManager().getLaunchIntentForPackage("com.aor.droidedit.pro");

To install, create an apk (either from command line, for example see build-instructions.sh,
or using the studio).
After installing the apk, create a folder on your phone:
/sdcard/Download/java and within three subfolders code, compile and libs (this is for the apps you will create)
You will need a android.jar used for compilation, put it in and as: /sdcard/Download/java/android.jar


## Use Instructions and Examples

Several examples to make android applications to be added soon. This below shows how to compile a java program and load it to use.

<img src="/res/screen.png" alt="jscheme" width="200">

This is a chess GUI for playing and teaching app that uses stockfish running on the android device

<img src="/res/chess.png" alt="jscheme" width="200">

