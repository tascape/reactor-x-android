#!/bin/bash -e

rm -fr libs || echo ""
mkdir libs
ls -l

cp ../lipermi/target/thx-android-lipermi-1.0.1.jar libs
cp ../uia-client/target/thx-android-uiac-1.0.0.jar libs

ant clean build

rm thx-android-*.jar

ls -l
cd bin
ls -l

echo "adb push bundle.jar /data/local/tmp"
echo "adb push uia-server.jar /data/local/tmp"
echo "adb shell uiautomator runtest uia-server.jar bundle.jar -c com.android.uiautomator.stub.UiAutomatorRmiServer"
echo "adb forward --remove tcp:8998"
echo "adb forward tcp:8998 tcp:8998"
