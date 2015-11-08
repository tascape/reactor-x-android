/*
 * Copyright 2015.
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
package tascape.qa.th.driver;

import com.android.uiautomator.stub.IUiCollection;
import com.android.uiautomator.stub.IUiDevice;
import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.IUiScrollable;
import com.android.uiautomator.stub.UiSelector;
import com.google.common.collect.Lists;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class AndroidUiAutomatorDevice extends AndroidAdbDevice {
    private static final Logger LOG = LoggerFactory.getLogger(AndroidUiAutomatorDevice.class);

    public static final String UIA_SERVER = "uia-server.jar";

    public static final String UIA_BUNDLE = "bundle.jar";

    public static final String uiaServer;

    public static final String uiaBundle;

    public static final long WAIT_FOR_EXISTS = 30000;

    static {
        try {
            File server = Paths.get(File.createTempFile("uias", ".jar").getParent(), UIA_SERVER).toFile();
            File bundle = Paths.get(File.createTempFile("uias", ".jar").getParent(), UIA_BUNDLE).toFile();
            uiaServer = server.getAbsolutePath();
            uiaBundle = bundle.getAbsolutePath();
            LOG.debug("uia server {}", uiaServer);
            LOG.debug("uia bundle {}", uiaBundle);
            server.createNewFile();
            bundle.createNewFile();

            OutputStream out = new FileOutputStream(server);
            IOUtils.copy(AndroidUiAutomatorDevice.class.getResourceAsStream("/uias/" + UIA_SERVER), out);
            out = new FileOutputStream(bundle);
            IOUtils.copy(AndroidUiAutomatorDevice.class.getResourceAsStream("/uias/" + UIA_BUNDLE), out);
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

    public AndroidUiAutomatorDevice(int port) throws IOException, InterruptedException {
        this.port = port;
    }

    public void init() throws IOException, InterruptedException {
        this.setupUiAutomatorRmiServer();
        this.adb.setupAdbPortForward(port, IUiDevice.UIAUTOMATOR_RMI_PORT);

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

    @Override
    public String getName() {
        return AndroidUiAutomatorDevice.class.getSimpleName();
    }

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

    public File dumpWindowHierarchy() throws IOException {
        String f = "/data/local/tmp/uidump.xml";
        adb.shell(Lists.newArrayList("uiautomator", "dump", f));
        File xml = this.getLogPath().resolve("ui-" + System.currentTimeMillis() + ".xml").toFile();
        this.adb.pull(f, xml);
        LOG.debug("Save WindowHierarchy to {}", xml.getAbsolutePath());
        return xml;
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
        this.adb.pull(f, png);
        LOG.debug("Save screenshot to {}", png.getAbsolutePath());
        return png;
    }

    private void setupUiAutomatorRmiServer() throws IOException, InterruptedException {
        List<Object> cmdLine = new ArrayList<>();
        cmdLine.add("push");
        cmdLine.add(uiaServer);
        cmdLine.add("/data/local/tmp/");
        adb.adb(cmdLine);

        cmdLine = new ArrayList<>();
        cmdLine.add("push");
        cmdLine.add(uiaBundle);
        cmdLine.add("/data/local/tmp/");
        adb.adb(cmdLine);

        cmdLine = new ArrayList();
        cmdLine.add("uiautomator");
        cmdLine.add("runtest");
        cmdLine.add(UIA_SERVER);
        cmdLine.add(UIA_BUNDLE);
        cmdLine.add("-c");
        cmdLine.add("com.android.uiautomator.stub.UiAutomatorRmiServer");
        this.adb.shellAsync(cmdLine, Long.MAX_VALUE);

        Thread.sleep(5000);
    }
}
