package com.android.uiautomator.stub;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

/**
 * cd <eclipse-project-folder>
 * <android-sdk>/tools/android create uitest-project -n uia-server -t 1 -p .
 * ant build
 * adb push bin/uia-server.jar /data/local/tmp/
 * adb push bin/bundle.jar /data/local/tmp/
 * adb shell uiautomator runtest uia-server.jar bundle.jar -c com.android.uiautomator.stub.UiAutomatorRmiServer
 *
 * adb forward --remove tcp:local_port
 * adb forward tcp:local_port tcp:8998
 *
 * @author linsong wang
 */
public class UiAutomatorRmiServer extends UiAutomatorTestCase {
    private static final CallHandler callHandler = new CallHandler();

    static {
        Server server = new Server();
        try {
            server.bind(IUiDevice.UIAUTOMATOR_RMI_PORT, callHandler);

            callHandler.registerGlobal(IUiDevice.class, new UiDeviceStub());
            callHandler.registerGlobal(IUiObject.class, new UiObjectStub());
            callHandler.registerGlobal(IUiCollection.class, new UiCollectionStub());
            callHandler.registerGlobal(IUiScrollable.class, new UiScrollableStub());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testRmiServer() throws Exception {
        while (true) {
            System.out.println("UiAutomator RMI Server is running");
            Thread.sleep(60000);
        }
    }
}
