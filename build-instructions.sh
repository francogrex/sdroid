export ANDROID_HOME=/home/ziad/downloads/android-sdk
export DEV_HOME=/home/ziad/downloads/javadev/sdroid

## IMPORTANT!: untar ecj.jar, take org folder do: find . -name "*.class" -type f -delete !!! and move the org folder into bin/!
## Eclipse Compiler for Java(TM) v20160516-2131, 3.12.0, Copyright IBM Corp 2000, 2015. All rights reserved.
rm -rf $DEV_HOME/bin/*.dex
rm -rf $DEV_HOME/bin/*.apk
rm -rf $DEV_HOME/obj/net
rm -rf $DEV_HOME/obj/jsint
rm -rf $DEV_HOME/obj/jscheme
rm $DEV_HOME/src/net/ziad/droid/R.java

$ANDROID_HOME/build-tools/23.0.1/aapt package -f -m -S $DEV_HOME/res -J $DEV_HOME/src -M $DEV_HOME/AndroidManifest.xml -I $ANDROID_HOME/platforms/android-23/android.jar

javac -source 1.7 -target 1.7 -d $DEV_HOME/obj -classpath $ANDROID_HOME/platforms/android-23/android.jar:$DEV_HOME/obj:$DEV_HOME/obj/android-support-v4.jar:$DEV_HOME/obj:$DEV_HOME/obj/ecj.jar:$DEV_HOME/obj:$DEV_HOME/obj/dx.jar -sourcepath $DEV_HOME/src $DEV_HOME/src/net/ziad/droid/*.java

java -Xmx1024m -jar $ANDROID_HOME/build-tools/23.0.1/lib/dx.jar --multi-dex --min-sdk-version=24 --dex --output=$DEV_HOME/bin/ $DEV_HOME/obj

$ANDROID_HOME/build-tools/23.0.1/aapt package -f -M $DEV_HOME/AndroidManifest.xml -S $DEV_HOME/res -I $ANDROID_HOME/platforms/android-23/android.jar -F $DEV_HOME/bin/sdroid_unsigned.apk $DEV_HOME/bin

jarsigner -keystore $DEV_HOME/AndroidTest.keystore -storepass password -keypass YOURPASSWORD -signedjar $DEV_HOME/bin/sdroid_signed.apk $DEV_HOME/bin/sdroid_unsigned.apk YOURKEY -sigalg MD5withRSA -digestalg SHA1

$ANDROID_HOME/platform-tools/adb uninstall net.ziad.droid
$ANDROID_HOME/platform-tools/adb install $DEV_HOME/bin/sdroid_signed.apk

