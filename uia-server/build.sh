#!/bin/bash -xe

rm -fr bin
rm -fr libs

ls -l
mkdir bin
mkdir libs

# update this as needed
cp ../lipermi/target/thx-android-lipermi-1.0.3.jar libs
cp ../uia-client/target/thx-android-uiac-1.0.3.jar libs

ant clean build

rm thx-android-*.jar

ls -l
pushd bin
  ls -l

  echo "adb push bundle.jar /data/local/tmp"
  echo "adb push uia-server.jar /data/local/tmp"
  echo "adb shell uiautomator runtest uia-server.jar bundle.jar -c com.android.uiautomator.stub.UiAutomatorRmiServer"
  echo "adb forward --remove tcp:8998"
  echo "adb forward tcp:8998 tcp:8998"
popd

cp bin/bundle.jar     ../uia-test/src/main/resources/uias/
cp bin/uia-server.jar ../uia-test/src/main/resources/uias/
