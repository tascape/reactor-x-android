#!/bin/bash -xe

rm -fr bin
rm -fr libs

ls -l
mkdir bin
mkdir libs

# update this as needed
cp ../../th-lipermi/target/th-lipermi-1.0.0.jar libs/th-lipermi.jar
cp ../uia-client/target/thx-android-uiac-1.1.2.jar libs/thx-android-uiac.jar

ant clean build

rm th-lipermi-*.jar
rm thx-android-uiac-*.jar

ls -l
pushd bin
  ls -l

  echo "adb push bundle.jar /data/local/tmp"
  echo "adb push uia-server.jar /data/local/tmp"
  echo "adb shell uiautomator runtest uia-server.jar bundle.jar -c com.android.uiautomator.stub.UiAutomatorRmiServer"
  echo "adb forward --remove tcp:8998"
  echo "adb forward tcp:8998 tcp:8998"
popd

cp bin/bundle.jar     ../uia-tool/src/main/resources/uias/
cp bin/uia-server.jar ../uia-tool/src/main/resources/uias/
