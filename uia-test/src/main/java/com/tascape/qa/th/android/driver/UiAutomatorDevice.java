/*
 * Copyright 2015 tascape.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.qa.th.android.driver;

import com.android.uiautomator.stub.IUiCollection;
import com.android.uiautomator.stub.IUiDevice;
import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.IUiScrollable;
import com.android.uiautomator.stub.Point;
import com.android.uiautomator.stub.UiSelector;
import com.google.common.collect.Lists;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.android.comm.Adb;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiAutomatorDevice extends AdbDevice implements IUiDevice {
    private static final Logger LOG = LoggerFactory.getLogger(UiAutomatorDevice.class);

    private static final long serialVersionUID = 5043985021L;

    public static final String UIA_SERVER_JAR = "uia-server.jar";

    public static final String UIA_BUNDLE_JAR = "bundle.jar";

    private static final String UIA_SERVER_PATH;

    private static final String UIA_BUNDLE_PATH;

    public static final long WAIT_FOR_EXISTS = 30000;

    static {
        try {
            File server = Paths.get(File.createTempFile("uias", ".jar").getParent(), UIA_SERVER_JAR).toFile();
            File bundle = Paths.get(File.createTempFile("uias", ".jar").getParent(), UIA_BUNDLE_JAR).toFile();
            UIA_SERVER_PATH = server.getAbsolutePath();
            UIA_BUNDLE_PATH = bundle.getAbsolutePath();
            LOG.debug("uia server {}", UIA_SERVER_PATH);
            LOG.debug("uia bundle {}", UIA_BUNDLE_PATH);
            server.createNewFile();
            bundle.createNewFile();

            OutputStream out = new FileOutputStream(server);
            IOUtils.copy(UiAutomatorDevice.class.getResourceAsStream("/uias/" + UIA_SERVER_JAR), out);
            out = new FileOutputStream(bundle);
            IOUtils.copy(UiAutomatorDevice.class.getResourceAsStream("/uias/" + UIA_BUNDLE_JAR), out);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot get uia server/bundle jar files", ex);
        }
    }

    private final String ip = "localhost";

    private int port = IUiDevice.UIAUTOMATOR_RMI_PORT;

    private Client client;

    private IUiDevice uiDevice;

    private IUiObject uiObject;

    private IUiCollection uiCollection;

    private IUiScrollable uiScrollable;

    private final Dimension screenDimension = new Dimension(0, 0);

    public UiAutomatorDevice(int port) throws IOException, InterruptedException {
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        this.setupUiAutomatorRmiServer();
        this.getAdb().setupAdbPortForward(port, IUiDevice.UIAUTOMATOR_RMI_PORT);

        CallHandler callHandler = new CallHandler();
        this.client = new Client(this.ip, this.port, callHandler);
        this.uiDevice = IUiDevice.class.cast(client.getGlobal(IUiDevice.class));
        this.uiObject = IUiObject.class.cast(client.getGlobal(IUiObject.class));
        this.uiCollection = IUiCollection.class.cast(client.getGlobal(IUiCollection.class));
        this.uiScrollable = IUiScrollable.class.cast(client.getGlobal(IUiScrollable.class));
        LOG.debug("Device product name '{}'", this.uiDevice.getProductName());

        screenDimension.width = uiDevice.getDisplayWidth();
        screenDimension.height = uiDevice.getDisplayHeight();
        LOG.debug("Device screen dimension '{}'", screenDimension);
    }
    
    public void stop() {
        try {
            client.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public String getName() {
        return UiAutomatorDevice.class.getSimpleName();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    public IUiObject getUiObject() {
        return uiObject;
    }

    public IUiCollection getUiCollection() {
        return uiCollection;
    }

    public IUiScrollable getUiScrollable() {
        return uiScrollable;
    }

    public void install(String apkPath) throws IOException, InterruptedException {
        this.backToHome();
        ExecuteWatchdog dog = this.getAdb().adbAsync(Lists.newArrayList("install", "-rg", apkPath), 60000, null);
        Utils.sleep(10000, "wait for app push");
        this.takeDeviceScreenshot();

        String pkg = uiDevice.getCurrentPackageName();
        if (pkg.equals("com.android.packageinstaller")) {
            Utils.sleep(10000, "wait for allow");
            this.clickByResourceId("android:id/button1");
        }
        pkg = uiDevice.getCurrentPackageName();
        if (pkg.equals("com.android.packageinstaller")) {
            this.clickByResourceId("com.android.packageinstaller:id/ok_button");
        }
        if (dog.isWatching()) {
            dog.killedProcess();
        }
    }

    public Dimension getScreenDimension() {
        return screenDimension;
    }

    public void home() {
        LOG.debug("press home");
        uiDevice.pressHome();
    }

    public void back() {
        LOG.debug("press back");
        uiDevice.pressBack();
    }

    public void enter() {
        LOG.debug("press enter");
        uiDevice.pressEnter();
    }

    public void backToHome() {
        for (int i = 0; i < 5; i++) {
            back();
        }
        home();
        home();
    }

    public boolean resourceIdExists(String resouceId) {
        LOG.debug("look for {}", resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        return uiObject.exists();
    }

    public boolean textExists(String text) {
        LOG.debug("look for {}", text);
        uiObject.useUiObjectSelector(new UiSelector().text(text));
        return uiObject.exists();
    }

    public boolean waitForResourceId(String resouceId) {
        LOG.debug("wait for {}", resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.waitForExists(WAIT_FOR_EXISTS);
        return uiObject.exists();
    }

    public boolean waitForText(String text) {
        LOG.debug("wait for {}", text);
        uiObject.useUiObjectSelector(new UiSelector().text(text));
        uiObject.waitForExists(WAIT_FOR_EXISTS);
        return uiObject.exists();
    }

    public boolean waitForTextContains(String text) {
        LOG.debug("wait for {}", text);
        uiObject.useUiObjectSelector(new UiSelector().textContains(text));
        uiObject.waitForExists(WAIT_FOR_EXISTS);
        return uiObject.exists();
    }

    public void clickByResourceId(String resouceId) {
        LOG.debug("click {}", resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.click();
        uiDevice.waitForIdle();
    }

    public void clickByText(String text) {
        LOG.debug("click {}", text);
        uiObject.useUiObjectSelector(new UiSelector().text(text));
        uiObject.click();
        uiDevice.waitForIdle();
    }

    public void clickByTextContains(String text) {
        LOG.debug("click {}", text);
        uiObject.useUiObjectSelector(new UiSelector().textContains(text));
        uiObject.click();
        uiDevice.waitForIdle();
    }

    public void clearTextByResourceId(String resouceId) {
        LOG.debug("clear {}", resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.clearTextField();
        String text = uiObject.getText();
        if (text.isEmpty()) {
            return;
        }
        uiObject.clickBottomRight();
        for (int i = 0; i < text.length(); i++) {
            uiDevice.pressDelete();
        }
    }

    public void setTextByResourceId(String resouceId, String text) {
        LOG.debug("type {} into {}", text, resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        clearTextByResourceId(resouceId);
        uiObject.setText(text);
        this.back();
    }

    public String getTextByResourceId(String resouceId) {
        if (resourceIdExists(resouceId)) {
            uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
            return uiObject.getText();
        }
        return null;
    }

    public File takeDeviceScreenshot() throws IOException {
        String f = "/data/local/tmp/ff.png";
        this.uiDevice.takeScreenshot(new File(f));
        File png = this.getLogPath().resolve("ss-" + System.currentTimeMillis() + ".png").toFile();
        this.getAdb().pull(f, png);
        LOG.debug("Save screenshot to {}", png.getAbsolutePath());
        return png;
    }

    @Override
    public void clearLastTraversedText() {
        this.uiDevice.clearLastTraversedText();
    }

    @Override
    public boolean click(int x, int y) {
        return this.uiDevice.click(x, y);
    }

    @Override
    public void dumpWindowHierarchy(String fileName) {
        this.uiDevice.dumpWindowHierarchy(fileName);
    }

    @Override
    public void freezeRotation() throws LipeRMIException {
        this.uiDevice.freezeRotation();
    }

    @Override
    public String getCurrentActivityName() {
        return this.uiDevice.getCurrentActivityName();
    }

    @Override
    public String getCurrentPackageName() {
        return this.uiDevice.getCurrentPackageName();
    }

    @Override
    public int getDisplayHeight() {
        return this.uiDevice.getDisplayHeight();
    }

    @Override
    public int getDisplayRotation() {
        return this.uiDevice.getDisplayRotation();
    }

    @Override
    public Point getDisplaySizeDp() {
        return this.uiDevice.getDisplaySizeDp();
    }

    @Override
    public int getDisplayWidthDp() {
        return this.uiDevice.getDisplayWidthDp();
    }

    @Override
    public int getDisplayHeightDp() {
        return this.uiDevice.getDisplayHeightDp();
    }

    @Override
    public int getDisplayWidth() {
        return this.uiDevice.getDisplayWidth();
    }

    @Override
    public String getLastTraversedText() {
        return this.uiDevice.getLastTraversedText();
    }

    @Override
    public String getProductName() {
        return this.uiDevice.getProductName();
    }

    @Override
    public boolean hasAnyWatcherTriggered() {
        return this.uiDevice.hasAnyWatcherTriggered();
    }

    @Override
    public boolean hasWatcherTriggered(String watcherName) {
        return this.uiDevice.hasWatcherTriggered(watcherName);
    }

    @Override
    public boolean isNaturalOrientation() {
        return this.uiDevice.isNaturalOrientation();
    }

    @Override
    public boolean isScreenOn() throws LipeRMIException {
        return this.uiDevice.isScreenOn();
    }

    @Override
    public boolean pressBack() {
        return this.uiDevice.pressBack();
    }

    @Override
    public boolean pressDPadCenter() {
        return this.uiDevice.pressDPadCenter();
    }

    @Override
    public boolean pressDPadDown() {
        return this.uiDevice.pressDPadDown();
    }

    @Override
    public boolean pressDPadLeft() {
        return this.uiDevice.pressDPadLeft();
    }

    @Override
    public boolean pressDPadRight() {
        return this.uiDevice.pressDPadRight();
    }

    @Override
    public boolean pressDPadUp() {
        return this.uiDevice.pressDPadUp();
    }

    @Override
    public boolean pressDelete() {
        return this.uiDevice.pressDelete();
    }

    @Override
    public boolean pressEnter() {
        return this.uiDevice.pressEnter();
    }

    @Override
    public boolean pressHome() {
        return this.uiDevice.pressHome();
    }

    @Override
    public boolean pressKeyCode(int keyCode) {
        return this.uiDevice.pressKeyCode(keyCode);
    }

    @Override
    public boolean pressKeyCode(int keyCode, int metaState) {
        return this.uiDevice.pressKeyCode(keyCode, metaState);
    }

    @Override
    public boolean pressMenu() {
        return this.uiDevice.pressMenu();
    }

    @Override
    public boolean pressRecentApps() throws LipeRMIException {
        return this.uiDevice.pressRecentApps();
    }

    @Override
    public boolean pressSearch() {
        return this.uiDevice.pressSearch();
    }

    @Override
    public void removeWatcher(String name) {
        this.uiDevice.removeWatcher(name);
    }

    @Override
    public void resetWatcherTriggers() {
        this.uiDevice.resetWatcherTriggers();
    }

    @Override
    public void runWatchers() {
        this.uiDevice.runWatchers();
    }

    @Override
    public void setOrientationLeft() throws LipeRMIException {
        this.uiDevice.setOrientationLeft();
    }

    @Override
    public void setOrientationNatural() throws LipeRMIException {
        this.uiDevice.setOrientationNatural();
    }

    @Override
    public void setOrientationRight() throws LipeRMIException {
        this.uiDevice.setOrientationRight();
    }

    @Override
    public void sleep() throws LipeRMIException {
        this.uiDevice.sleep();
    }

    @Override
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        return this.uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    @Override
    public boolean swipe(Point[] segments, int segmentSteps) {
        return this.uiDevice.swipe(segments, segmentSteps);
    }

    @Override
    public boolean takeScreenshot(File storePath) {
        return this.uiDevice.takeScreenshot(storePath);
    }

    @Override
    public boolean takeScreenshot(File storePath, float scale, int quality) {
        return this.uiDevice.takeScreenshot(storePath, scale, quality);
    }

    @Override
    public void unfreezeRotation() throws LipeRMIException {
        this.uiDevice.unfreezeRotation();
    }

    @Override
    public void waitForIdle() {
        this.uiDevice.waitForIdle();
    }

    @Override
    public void waitForIdle(long time) {
        this.uiDevice.waitForIdle(time);
    }

    @Override
    public boolean waitForWindowUpdate(String packageName, long timeout) {
        return this.uiDevice.waitForWindowUpdate(packageName, timeout);
    }

    @Override
    public void wakeUp() throws LipeRMIException {
        this.uiDevice.wakeUp();
    }

    private void setupUiAutomatorRmiServer() throws IOException, InterruptedException {
        List<Object> cmdLine = new ArrayList<>();
        cmdLine.add("push");
        cmdLine.add(UIA_SERVER_PATH);
        cmdLine.add("/data/local/tmp/");
        this.getAdb().adb(cmdLine);

        cmdLine = new ArrayList<>();
        cmdLine.add("push");
        cmdLine.add(UIA_BUNDLE_PATH);
        cmdLine.add("/data/local/tmp/");
        this.getAdb().adb(cmdLine);

        cmdLine = new ArrayList<>();
        cmdLine.add("uiautomator");
        cmdLine.add("runtest");
        cmdLine.add(UIA_SERVER_JAR);
        cmdLine.add(UIA_BUNDLE_JAR);
        cmdLine.add("-c");
        cmdLine.add("com.android.uiautomator.stub.UiAutomatorRmiServer");
        this.getAdb().shellAsync(cmdLine, Long.MAX_VALUE);

        Thread.sleep(5000);
    }

    public static void main(String[] args) throws Exception {
        Adb adb = new Adb();
        UiAutomatorDevice device = new UiAutomatorDevice(IUiDevice.UIAUTOMATOR_RMI_PORT);
        device.setAdb(adb);
        device.start();

        device.uninstall("com.mykaishi.xinkaishi");
        device.install("/opt/app-debug.apk");
        device.stop();
    }
}
