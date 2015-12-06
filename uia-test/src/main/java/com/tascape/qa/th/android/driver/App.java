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
import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.IUiScrollable;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.exception.EntityDriverException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@SuppressWarnings("ProtectedField")
public abstract class App extends EntityDriver {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final int NUMBER_OF_HOME_PAGE = 10;

    protected UiAutomatorDevice uiaDevice;

    protected IUiObject uiObject;

    protected IUiCollection uiCollection;

    protected IUiScrollable uiScrollable;

    public abstract String getPackageName();

    @Override
    public String getVersion() {
        try {
            return uiaDevice.getAppVersion(getPackageName());
        } catch (IOException | EntityDriverException ex) {
            LOG.warn(ex.getMessage());
            return "na";
        }
    }

    public void attachTo(UiAutomatorDevice device) {
        uiaDevice = device;
        uiObject = device.getUiObject();
        uiCollection = device.getUiCollection();
        uiScrollable = device.getUiScrollable();
    }

    public void launch() throws IOException, InterruptedException {
        String name = getName();
        for (int i = 0; i < NUMBER_OF_HOME_PAGE; i++) {
            if (uiaDevice.textExists(name)) {
                break;
            } else {
                LOG.debug("swipe to next screen");
                int w = uiaDevice.getScreenDimension().width;
                int h = uiaDevice.getScreenDimension().height;
                uiaDevice.swipe(w / 2, h / 2, 0, h / 2, 5);
                Utils.sleep(1000, "wait for next screen");
                uiaDevice.takeDeviceScreenshot();
            }
        }
        uiaDevice.clickByText(name);
        uiaDevice.waitForIdle();
    }
}
