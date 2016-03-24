/*
 * Copyright 2016 tascape.
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
import com.android.uiautomator.stub.UiWatcher;
import com.google.common.collect.Lists;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.android.comm.Adb;
import com.tascape.qa.th.android.model.UIA;
import com.tascape.qa.th.android.model.WindowHierarchy;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiAutomatorDevice extends AdbDevice implements IUiDevice {
    private static final Logger LOG = LoggerFactory.getLogger(UiAutomatorDevice.class);

    private static final long serialVersionUID = 1L;

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

    private String productDetail;

    private final String ip = "localhost";

    private int port = IUiDevice.UIAUTOMATOR_RMI_PORT;

    private ExecuteWatchdog uiautomatorDog;

    private Client client;

    private IUiDevice uiDevice;

    private IUiObject uiObject;

    private IUiCollection uiCollection;

    private IUiScrollable uiScrollable;

    private final Set<App> apps = new HashSet<>();

    private final Dimension screenDimension = new Dimension(0, 0);

    private static final Set<Integer> LOCAL_PORTS = Collections.synchronizedSet(new HashSet<>());

    public void start() throws IOException, InterruptedException {
        uiautomatorDog = this.setupUiAutomatorRmiServer();
        this.port = this.setupRmiPortForward();

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

        apps.forEach(app -> app.fetchUiaStubs());
    }

    public void stop() throws IOException {
        try {
            client.close();
        } catch (IOException ex) {
            LOG.warn("{}", ex.getMessage());
        }
        if (uiautomatorDog != null) {
            uiautomatorDog.stop();
            uiautomatorDog.killedProcess();
            this.killUiAutomatorProcess();
        }
    }

    public void install(App app) {
        app.setDevice(this);
        apps.add(app);
        app.fetchUiaStubs();
    }

    @Override
    public String getName() {
        return UiAutomatorDevice.class.getSimpleName();
    }

    /**
     * Throws UnsupportedOperationException.
     */
    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    public IUiDevice getUiDevice() {
        return uiDevice;
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

    public UiAutomatorDevice install(String apkPath) throws IOException, InterruptedException {
        this.backToHome();
        ExecuteWatchdog dog = this.getAdb().adbAsync(Lists.newArrayList("install", "-rg", apkPath), 60000);
        Utils.sleep(10000, "wait for app push");
        this.takeDeviceScreenshot();

        String pkg = uiDevice.getCurrentPackageName();
        if (pkg.equals("com.android.packageinstaller")) {
            Utils.sleep(10000, "wait for allow");
            if (this.resourceIdExists("android:id/button1")) {
                this.clickByResourceId("android:id/button1");
            }
        }
        pkg = uiDevice.getCurrentPackageName();
        if (pkg.equals("com.android.packageinstaller")) {
            Utils.sleep(10000, "wait for OK");
            if (this.resourceIdExists("com.android.packageinstaller:id/ok_button")) {
                this.clickByResourceId("com.android.packageinstaller:id/ok_button");
            }
        }
        if (dog.isWatching()) {
            dog.killedProcess();
        }
        return this;
    }

    public Dimension getScreenDimension() {
        return screenDimension;
    }

    public UiAutomatorDevice home() {
        pressHome();
        return this;
    }

    public UiAutomatorDevice back() {
        pressBack();
        return this;
    }

    public UiAutomatorDevice enter() {
        pressEnter();
        return this;
    }

    public UiAutomatorDevice backToHome() {
        int i = 0;
        while (pressBack() && i++ < 10) {
        }
        i = 0;
        while (pressHome() && i++ < 5) {
        }
        return this;
    }

    /**
     * Drags screen vertically from center.
     *
     * @param size positive to drag down, negative to drag up
     *
     * @return this
     */
    public UiAutomatorDevice dragVertically(int size) {
        LOG.debug("drag, from center, vertically");
        Dimension dimension = this.getScreenDimension();
        this.swipe(dimension.width / 2, dimension.height / 2, dimension.width / 2, dimension.height / 2 + size,
            Math.abs(size / 20 + 5));
        return this;
    }

    /**
     * Drags screen horizontally from center.
     *
     * @param size positive to drag right, negative to drag left
     *
     * @return this
     */
    public UiAutomatorDevice dragHorizontally(int size) {
        LOG.debug("drag, from center, horizontally");
        Dimension dimension = this.getScreenDimension();
        this.swipe(dimension.width / 2, dimension.height / 2, dimension.width / 2 + size, dimension.height / 2,
            Math.abs(size / 20 + 5));
        return this;
    }

    public UiAutomatorDevice dragHalfScreenUp() {
        LOG.debug("drag, from center, half screen up");
        Dimension dimension = this.getScreenDimension();
        this.swipe(dimension.width / 2, dimension.height / 2, dimension.width / 2, 0, 38);
        return this;
    }

    public UiAutomatorDevice dragHalfScreenDown() {
        LOG.debug("drag, from center, half screen down");
        Dimension dimension = this.getScreenDimension();
        this.swipe(dimension.width / 2, dimension.height / 2, dimension.width / 2, dimension.height, 38);
        return this;
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

    public boolean descriptionExists(String text) {
        LOG.debug("look for {}", text);
        uiObject.useUiObjectSelector(new UiSelector().description(text));
        return uiObject.exists();
    }

    public boolean waitForResourceId(String resouceId) {
        LOG.debug("wait {} for {} ms", resouceId, WAIT_FOR_EXISTS);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.waitForExists(WAIT_FOR_EXISTS);
        return uiObject.exists();
    }

    public boolean waitForText(String text) {
        LOG.debug("wait {} for {} ms", text, WAIT_FOR_EXISTS);
        uiObject.useUiObjectSelector(new UiSelector().text(text));
        uiObject.waitForExists(WAIT_FOR_EXISTS);
        return uiObject.exists();
    }

    public boolean waitForTextContains(String text) {
        LOG.debug("wait {} for {} ms", text, WAIT_FOR_EXISTS);
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

    public UiAutomatorDevice clickByText(String text) {
        LOG.debug("click {}", text);
        uiObject.useUiObjectSelector(new UiSelector().text(text));
        uiObject.click();
        uiDevice.waitForIdle();
        return this;
    }

    public UiAutomatorDevice clickByTextContains(String text) {
        LOG.debug("click {}", text);
        uiObject.useUiObjectSelector(new UiSelector().textContains(text));
        uiObject.click();
        uiDevice.waitForIdle();
        return this;
    }

    public UiAutomatorDevice clickByDescription(String text) {
        LOG.debug("click {}", text);
        uiObject.useUiObjectSelector(new UiSelector().description(text));
        uiObject.click();
        uiDevice.waitForIdle();
        return this;
    }

    public UiAutomatorDevice clearTextByResourceId(String resouceId) {
        LOG.debug("clear {}", resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.clearTextField();
        String text = uiObject.getText();
        if (text.isEmpty()) {
            return this;
        }
        uiObject.clickBottomRight();
        for (int i = 0; i < text.length(); i++) {
            uiDevice.pressDPadRight();
        }
        for (int i = 0; i < text.length(); i++) {
            uiDevice.pressDelete();
        }
        return this;
    }

    public UiAutomatorDevice setTextByResourceId(String resouceId, String text) {
        LOG.debug("type {} into {}", text, resouceId);
        uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
        uiObject.setText(text);
        uiObject.click();
        this.back();
        return this;
    }

    public String getTextByResourceId(String resouceId) {
        if (resourceIdExists(resouceId)) {
            uiObject.useUiObjectSelector(new UiSelector().resourceId(resouceId));
            return uiObject.getText();
        }
        return null;
    }

    public File takeDeviceScreenshot() throws IOException {
        try {
            return ss();
        } catch (ExecuteException ex) {
            LOG.warn(ex.getMessage());
            try {
                Utils.sleep(5000, "wait for device");
            } catch (InterruptedException ex1) {
                LOG.warn(ex.getMessage());
            }
            return ss();
        }
    }

    /**
     * Loads window hierarchy as an in-memory node tree.
     *
     * @return UI view hierarchy node tree
     *
     * @throws Exception cannot dump window hierarchy
     */
    public WindowHierarchy loadWindowHierarchy() throws Exception {
        String name = "uidump-" + UUID.randomUUID() + ".xml";
        uiDevice.dumpWindowHierarchy(name);
        File xml = this.getLogPath().resolve(name).toFile();
        this.getAdb().pull(IUiDevice.TMP_DIR + name, xml);
        LOG.debug("Save WindowHierarchy as {}", xml.getAbsolutePath());

        WindowHierarchy hierarchy = UIA.parseHierarchy(xml, this);
        return hierarchy;
    }

    @Override
    public void clearLastTraversedText() {
        this.uiDevice.clearLastTraversedText();
    }

    @Override
    public boolean click(int x, int y) {
        LOG.debug("click {}, {}", x, y);
        boolean ok = this.uiDevice.click(x, y);
        this.waitForIdle();
        return ok;
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
        LOG.debug("press back");
        boolean ok = this.uiDevice.pressBack();
        this.waitForIdle();
        return ok;
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
        LOG.debug("press delete");
        boolean ok = this.uiDevice.pressDelete();
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressEnter() {
        LOG.debug("press enter");
        boolean ok = this.uiDevice.pressEnter();
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressHome() {
        LOG.debug("press home");
        boolean ok = this.uiDevice.pressHome();
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressKeyCode(int keyCode) {
        LOG.debug("press key {}", keyCode);
        boolean ok = this.uiDevice.pressKeyCode(keyCode);
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressKeyCode(int keyCode, int metaState) {
        return this.uiDevice.pressKeyCode(keyCode, metaState);
    }

    @Override
    public boolean pressMenu() {
        LOG.debug("press menu");
        boolean ok = this.uiDevice.pressMenu();
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressRecentApps() throws LipeRMIException {
        LOG.debug("press recent apps");
        boolean ok = this.uiDevice.pressRecentApps();
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean pressSearch() {
        LOG.debug("press search");
        boolean ok = this.uiDevice.pressSearch();
        this.waitForIdle();
        return ok;
    }

    @Override
    public void registerWatcher(String name, UiWatcher watcher) {
        this.uiDevice.registerWatcher(name, watcher);
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
        LOG.debug("put device to sleep");
        this.uiDevice.sleep();
    }

    @Override
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        boolean ok = this.uiDevice.swipe(startX, startY, endX, endY, steps);
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean swipe(Point[] segments, int segmentSteps) {
        boolean ok = this.uiDevice.swipe(segments, segmentSteps);
        this.waitForIdle();
        return ok;
    }

    @Override
    public boolean takeScreenshot(String name) {
        return this.uiDevice.takeScreenshot(name);
    }

    @Override
    public boolean takeScreenshot(String name, float scale, int quality) {
        return this.uiDevice.takeScreenshot(name, scale, quality);
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
        this.waitForIdle();
    }

    @Override
    public void setCompressedLayoutHeirarchy(boolean compressed) {
        uiDevice.setCompressedLayoutHeirarchy(compressed);
    }

    @Override
    public boolean openNotification() {
        LOG.debug("open notification");
        return uiDevice.openNotification();
    }

    @Override
    public boolean openQuickSettings() {
        LOG.debug("open quick settings");
        return uiDevice.openQuickSettings();
    }

    @Override
    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        LOG.debug("drag from {},{} to {},{}", startX, startY, endX, endY);
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    private File ss() throws IOException {
        String name = "ss-" + UUID.randomUUID() + ".png";
        this.uiDevice.takeScreenshot(name);
        File png = this.getLogPath().resolve(name).toFile();
        this.getAdb().pull(IUiDevice.TMP_DIR + name, png);
        LOG.debug("Save screenshot as {}", png.getAbsolutePath());
        return png;
    }

    private ExecuteWatchdog setupUiAutomatorRmiServer() throws IOException, InterruptedException {
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
        ExecuteWatchdog dog = this.getAdb().shellAsync(cmdLine, Long.MAX_VALUE);

        Thread.sleep(5000);
        return dog;
    }

    private synchronized int setupRmiPortForward() throws IOException, InterruptedException {
        int remote = IUiDevice.UIAUTOMATOR_RMI_PORT;
        int local = IUiDevice.UIAUTOMATOR_RMI_PORT + 10000;
        while (LOCAL_PORTS.contains(local)) {
            local++;
        }
        this.getAdb().setupAdbPortForward(local, remote);
        LOCAL_PORTS.add(local);
        return local;
    }

    private void killUiAutomatorProcess() throws IOException {
        Optional<String> line = getAdb().shell(Lists.newArrayList("ps")).stream()
            .filter(l -> (l.startsWith("shell") && l.endsWith("uiautomator"))).findFirst();
        if (line.isPresent()) {
            String[] ss = StringUtils.split(line.get(), " ");
            getAdb().shell(Lists.newArrayList("kill", ss[1]));
        }
    }

    public static void main(String[] args) throws Exception {
        Map.Entry<String, String> entry = Adb.getSerialProduct().entrySet().iterator().next();
        Adb adb = new Adb(entry.getKey());
        UiAutomatorDevice device = new UiAutomatorDevice();
        device.setAdb(adb);
        device.setProductDetail(entry.getValue());
        device.start();

        try {
            LOG.debug(device.getProductDetail());
            device.dragVertically(-1000000);
            device.dragVertically(100000);
        } finally {
            device.stop();
            System.exit(0);
        }
    }
}
