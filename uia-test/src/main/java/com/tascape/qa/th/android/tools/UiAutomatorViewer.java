/*
 * Copyright 2016 Nebula Bay.
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
package com.tascape.qa.th.android.tools;

import com.tascape.qa.th.SystemConfiguration;
import com.tascape.qa.th.android.driver.UiAutomatorDevice;
import com.tascape.qa.th.android.test.UiAutomatorTest;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiAutomatorViewer implements UiAutomatorTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UiAutomatorViewer.class);

    private UiAutomatorDevice device;

    private String appName = "F-Droid";

    private int debugMinutes = 30;

    private UiAutomatorViewer() {
        // todo: add ui for parameter input
    }

    private void start() throws Exception {
        device = new UiAutomatorDevice();
        // todo: launch app

        this.testManually(device, debugMinutes);
    }

    public static void main(String[] args) {
        SystemConfiguration.getInstance();

        UiAutomatorViewer debugger = new UiAutomatorViewer();
        try {
            debugger.start();
        } catch (Throwable ex) {
            LOG.error("Error", ex);
            System.exit(1);
        }
        System.exit(0);
    }
}