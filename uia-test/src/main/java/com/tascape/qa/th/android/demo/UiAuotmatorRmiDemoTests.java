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
package com.tascape.qa.th.android.demo;

import com.android.uiautomator.stub.IUiCollection;
import com.android.uiautomator.stub.IUiDevice;
import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.IUiScrollable;
import com.android.uiautomator.stub.Point;
import com.android.uiautomator.stub.Rect;
import com.android.uiautomator.stub.UiSelector;
import com.tascape.qa.th.android.comm.Adb;
import com.tascape.qa.th.android.driver.UiAutomatorDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiAuotmatorRmiDemoTests {
    private static final Logger LOG = LoggerFactory.getLogger(UiAuotmatorRmiDemoTests.class);

    private UiAutomatorDevice device;

    private IUiObject uiObject;

    private IUiCollection uiCollection;

    private IUiScrollable uiScrollable;

    private IUiScrollable uiScrollableStub;

    public void setup() throws Exception {
        Adb adb = new Adb();
        device = new UiAutomatorDevice(IUiDevice.UIAUTOMATOR_RMI_PORT);
        device.setAdb(adb);
        device.start();

        uiObject = device.getUiObject();
        uiCollection = device.getUiCollection();
        uiScrollable = device.getUiScrollable();
    }

    public void testUiDevice() throws Exception {
        device.pressHome();
        device.waitForIdle();
        device.click(500, 500);

        LOG.debug(device.getDisplayWidth() + "/" + device.getDisplayHeight());
        Point p = device.getDisplaySizeDp();
        LOG.debug(p.x + "/" + p.y);
        device.swipe(100, 0, 100, 500, 2);
        LOG.debug(device.getCurrentActivityName());

        device.swipe(new Point[]{new Point(100, 500), new Point(100, 0)}, 2);
        device.swipe(100, 500, 100, 0, 2);
        LOG.debug(device.getCurrentActivityName());
    }

    public void testUiObject() throws Exception {
        device.pressHome();
        device.waitForIdle();

        for (String app : new String[]{"电子邮件", "设置", "应用商店"}) {
            LOG.debug(app);
            this.device.pressHome();
            this.uiObject.useUiObjectSelector(
                new UiSelector().resourceId("com.miui.home:id/cell_layout"));
            this.uiObject.useUiObjectSelector(new UiSelector().text(app));
            Rect rect = this.uiObject.getBounds();
            LOG.debug("{}", rect);
            this.uiObject.swipeLeft(10);
            this.uiObject.swipeRight(10);
            this.uiObject.click();
            this.device.waitForIdle();
        }
    }

    public void testUiObjectNegative() throws Exception {
        device.pressHome();
        device.waitForIdle();

        LOG.debug("Book");
        uiObject.useUiObjectSelector(new UiSelector().text("Book"));
        uiObject.click();
        LOG.debug("hasUiObjectNotFoundException = {}", uiObject.hasUiObjectNotFoundException());
        LOG.debug("Exception!", uiObject.getUiObjectNotFoundException());
        LOG.debug("hasUiObjectNotFoundException = {}", uiObject.hasUiObjectNotFoundException());
    }

    public void testUiCollection() throws Exception {
        device.pressHome();
        device.waitForIdle();

        this.uiCollection.useUiCollectionSelector(new UiSelector().resourceId(
            "com.amazon.kindle.otter:id/library_selector_layout"));
        int n = this.uiCollection.getChildCount(new UiSelector().className("android.widget.Button"));
        LOG.debug("buttons {}", n);
        for (int i = 0; i < n; i++) {
            device.pressHome();
            this.uiCollection.selectChildByInstance(new UiSelector().className("android.widget.Button"), i);
            LOG.debug("text {}, rect {}", this.uiCollection.getText(), this.uiCollection.getBounds());
            this.uiCollection.click();
            device.waitForIdle();
        }
    }

    public void testUiCollection2() throws Exception {
        device.pressHome();
        device.waitForIdle();

        this.uiCollection.useUiCollectionSelector(new UiSelector().resourceId(
            "com.amazon.kindle.otter:id/library_selector_layout"));
        this.uiCollection.swipeLeft(100);

        int n = this.uiCollection.getChildCount(new UiSelector().className("android.widget.Button"));
        LOG.debug("buttons {}", n);
        for (int i = 0; i < n; i++) {
            device.pressHome();
            this.uiCollection.selectChildByInstance(new UiSelector().className("android.widget.Button"), i);
            LOG.debug("text {}, rect {}", this.uiCollection.getText(), this.uiCollection.getBounds());
            this.uiCollection.click();
            device.waitForIdle();
        }
    }

    public void testUiScrollable() throws Exception {
        device.pressHome();
        device.waitForIdle();
        this.uiCollection.useUiCollectionSelector(new UiSelector().resourceId(
            "com.amazon.kindle.otter:id/library_selector_layout"));
        this.uiCollection.swipeRight(100);

        this.uiObject.useUiObjectSelector(new UiSelector().text("Books"));
        this.uiObject.click();
        this.uiScrollable.useUiScrollableSelector(new UiSelector().scrollable(true));
        this.uiScrollable.scrollToBeginning(100);
        this.uiScrollable.scrollForward(100);
        this.uiObject.useUiObjectSelector(new UiSelector().descriptionStartsWith("The Blind Side"));
        this.uiObject.click();
        this.device.waitForIdle();
    }

    public static void main(String[] args) {
        try {
            while (true) {
                UiAuotmatorRmiDemoTests t = new UiAuotmatorRmiDemoTests();
                t.setup();
                t.testUiDevice();
                t.testUiObject();
                t.testUiObjectNegative();
                t.testUiCollection();
                t.testUiCollection2();
                t.testUiScrollable();
            }
        } catch (Exception ex) {
            LOG.error("", ex);
        } finally {
            System.exit(0);
        }
    }
}
