/*
 * Copyright 2015 - 2016 Nebula Bay.
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
package com.android.uiautomator.stub;

import android.os.RemoteException;
import java.io.File;

import com.android.uiautomator.core.UiDevice;
import net.sf.lipermi.exception.LipeRMIException;

/**
 *
 * @author linsong wang
 */
public class UiDeviceStub implements IUiDevice {
    private static final long serialVersionUID = 1L;

    private final UiDevice uiDevice = UiDevice.getInstance();

    @Override
    public void clearLastTraversedText() {
        this.uiDevice.clearLastTraversedText();
    }

    @Override
    public boolean click(int x, int y) {
        boolean ok = this.uiDevice.click(x, y);
        this.waitForIdle0();
        return ok;
    }

    @Override
    public void dumpWindowHierarchy(String fileName) {
        File f = new File(IUiDevice.TMP_DIR);
        f.mkdirs();
        this.uiDevice.dumpWindowHierarchy(fileName);
    }

    @Override
    public void freezeRotation() throws LipeRMIException {
        try {
            this.uiDevice.freezeRotation();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
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
        android.graphics.Point p = this.uiDevice.getDisplaySizeDp();
        return new Point(p.x, p.y);
    }

    @Override
    public int getDisplayWidthDp() {
        return this.uiDevice.getDisplaySizeDp().x;
    }

    @Override
    public int getDisplayHeightDp() {
        return this.uiDevice.getDisplaySizeDp().y;
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
        try {
            return this.uiDevice.isScreenOn();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    @Override
    public boolean pressBack() {
        boolean ok = this.uiDevice.pressBack();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDPadCenter() {
        boolean ok = this.uiDevice.pressDPadCenter();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDPadDown() {
        boolean ok = this.uiDevice.pressDPadDown();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDPadLeft() {
        boolean ok = this.uiDevice.pressDPadLeft();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDPadRight() {
        boolean ok = this.uiDevice.pressDPadRight();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDPadUp() {
        boolean ok = this.uiDevice.pressDPadUp();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressDelete() {
        boolean ok = this.uiDevice.pressDelete();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressEnter() {
        boolean ok = this.uiDevice.pressEnter();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressHome() {
        boolean ok = this.uiDevice.pressHome();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressKeyCode(int keyCode) {
        boolean ok = this.uiDevice.pressKeyCode(keyCode);
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressKeyCode(int keyCode, int metaState) {
        boolean ok = this.uiDevice.pressKeyCode(keyCode, metaState);
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressMenu() {
        boolean ok = this.uiDevice.pressMenu();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public boolean pressRecentApps() throws LipeRMIException {
        try {
            boolean ok = this.uiDevice.pressRecentApps();
            this.waitForIdle0();
            return ok;
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    @Override
    public boolean pressSearch() {
        boolean ok = this.uiDevice.pressSearch();
        this.waitForIdle0();
        return ok;
    }

    @Override
    public void registerWatcher(String name, final UiWatcher watcher) {
        com.android.uiautomator.core.UiWatcher w = new com.android.uiautomator.core.UiWatcher() {
            @Override
            public boolean checkForCondition() {
                return watcher.checkForCondition();
            }
        };
        this.uiDevice.registerWatcher(name, w);
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
        try {
            this.uiDevice.setOrientationLeft();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    @Override
    public void setOrientationNatural() throws LipeRMIException {
        try {
            this.uiDevice.setOrientationNatural();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    @Override
    public void setOrientationRight() throws LipeRMIException {
        try {
            this.uiDevice.setOrientationRight();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }

    }

    @Override
    public void sleep() throws LipeRMIException {
        try {
            this.uiDevice.sleep();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    @Override
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        return this.uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    @Override
    public boolean swipe(Point[] segments, int segmentSteps) {
        android.graphics.Point[] aSegments = new android.graphics.Point[segments.length];
        for (int i = 0; i < segments.length; i++) {
            aSegments[i] = new android.graphics.Point(segments[i].x, segments[i].y);
        }
        return this.uiDevice.swipe(aSegments, segmentSteps);
    }

    @Override
    public boolean takeScreenshot(String name) {
        return this.takeScreenshot(name, 1.0f, 90);
    }

    @Override
    public boolean takeScreenshot(String name, float scale, int quality) {
        File f = new File(IUiDevice.TMP_DIR);
        f.mkdirs();
        return this.uiDevice.takeScreenshot(new File(f, name), scale, quality);
    }

    @Override
    public void unfreezeRotation() throws LipeRMIException {
        try {
            this.uiDevice.unfreezeRotation();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
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
        try {
            this.uiDevice.wakeUp();
        } catch (RemoteException ex) {
            throw new LipeRMIException(ex);
        }
    }

    public static com.android.uiautomator.core.UiSelector convert(UiSelector selector) {
        com.android.uiautomator.core.UiSelector s = new com.android.uiautomator.core.UiSelector();
        if (selector.get(UiSelector.SELECTOR_CHECKABLE) != null) {
            s = s.checkable((boolean) selector.get(UiSelector.SELECTOR_CHECKABLE));
        }
        if (selector.get(UiSelector.SELECTOR_CHECKED) != null) {
            s = s.checked((boolean) selector.get(UiSelector.SELECTOR_CHECKED));
        }
        if (selector.get(UiSelector.SELECTOR_CLASS) != null) {
            s = s.className((String) selector.get(UiSelector.SELECTOR_CLASS));
        }
        if (selector.get(UiSelector.SELECTOR_CLASS_REGEX) != null) {
            s = s.classNameMatches((String) selector.get(UiSelector.SELECTOR_CLASS_REGEX));
        }

        if (selector.get(UiSelector.SELECTOR_CLICKABLE) != null) {
            s = s.clickable((boolean) selector.get(UiSelector.SELECTOR_CLICKABLE));
        }
        if (selector.get(UiSelector.SELECTOR_DESCRIPTION) != null) {
            s = s.description((String) selector.get(UiSelector.SELECTOR_DESCRIPTION));
        }
        if (selector.get(UiSelector.SELECTOR_CONTAINS_TEXT) != null) {
            s = s.descriptionContains((String) selector.get(UiSelector.SELECTOR_CONTAINS_TEXT));
        }
        if (selector.get(UiSelector.SELECTOR_DESCRIPTION_REGEX) != null) {
            s = s.descriptionMatches((String) selector.get(UiSelector.SELECTOR_DESCRIPTION_REGEX));
        }
        if (selector.get(UiSelector.SELECTOR_START_TEXT) != null) {
            s = s.descriptionStartsWith((String) selector.get(UiSelector.SELECTOR_START_TEXT));
        }
        if (selector.get(UiSelector.SELECTOR_ENABLED) != null) {
            s = s.enabled((boolean) selector.get(UiSelector.SELECTOR_ENABLED));
        }
        if (selector.get(UiSelector.SELECTOR_FOCUSABLE) != null) {
            s = s.focusable((boolean) selector.get(UiSelector.SELECTOR_FOCUSABLE));
        }
        if (selector.get(UiSelector.SELECTOR_FOCUSED) != null) {
            s = s.focused((boolean) selector.get(UiSelector.SELECTOR_FOCUSED));
        }
        if (selector.get(UiSelector.SELECTOR_INDEX) != null) {
            s = s.index((int) selector.get(UiSelector.SELECTOR_INDEX));
        }
        if (selector.get(UiSelector.SELECTOR_INSTANCE) != null) {
            s = s.instance((int) selector.get(UiSelector.SELECTOR_INSTANCE));
        }
        if (selector.get(UiSelector.SELECTOR_LONG_CLICKABLE) != null) {
            s = s.longClickable((boolean) selector.get(UiSelector.SELECTOR_LONG_CLICKABLE));
        }
        if (selector.get(UiSelector.SELECTOR_PACKAGE_NAME) != null) {
            s = s.packageName((String) selector.get(UiSelector.SELECTOR_PACKAGE_NAME));
        }
        if (selector.get(UiSelector.SELECTOR_PACKAGE_NAME_REGEX) != null) {
            s = s.packageNameMatches((String) selector.get(UiSelector.SELECTOR_PACKAGE_NAME_REGEX));
        }
        if (selector.get(UiSelector.SELECTOR_RESOURCE_ID) != null) {
            s = s.resourceId((String) selector.get(UiSelector.SELECTOR_RESOURCE_ID));
        }
        if (selector.get(UiSelector.SELECTOR_RESOURCE_ID_REGEX) != null) {
            s = s.resourceIdMatches((String) selector.get(UiSelector.SELECTOR_RESOURCE_ID_REGEX));
        }
        if (selector.get(UiSelector.SELECTOR_SCROLLABLE) != null) {
            s = s.scrollable((boolean) selector.get(UiSelector.SELECTOR_SCROLLABLE));
        }
        if (selector.get(UiSelector.SELECTOR_SELECTED) != null) {
            s = s.selected((boolean) selector.get(UiSelector.SELECTOR_SELECTED));
        }
        if (selector.get(UiSelector.SELECTOR_TEXT) != null) {
            s = s.text((String) selector.get(UiSelector.SELECTOR_TEXT));
        }
        if (selector.get(UiSelector.SELECTOR_CONTAINS_TEXT) != null) {
            s = s.textContains((String) selector.get(UiSelector.SELECTOR_CONTAINS_TEXT));
        }
        if (selector.get(UiSelector.SELECTOR_TEXT_REGEX) != null) {
            s = s.textMatches((String) selector.get(UiSelector.SELECTOR_TEXT_REGEX));
        }
        if (selector.get(UiSelector.SELECTOR_START_TEXT) != null) {
            s = s.textStartsWith((String) selector.get(UiSelector.SELECTOR_START_TEXT));
        }
        return s;
    }

    @Override
    public void setCompressedLayoutHeirarchy(boolean compressed) {
        uiDevice.setCompressedLayoutHeirarchy(compressed);
    }

    @Override
    public boolean openNotification() {
        return uiDevice.openNotification();
    }

    @Override
    public boolean openQuickSettings() {
        return uiDevice.openQuickSettings();
    }

    @Override
    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    private void waitForIdle0() {
        this.uiDevice.waitForIdle();
    }
}
