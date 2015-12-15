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
package com.tascape.qa.th.android.suite;

import com.android.uiautomator.stub.IUiDevice;
import com.tascape.qa.th.android.comm.Adb;
import com.tascape.qa.th.android.driver.UiAutomatorDevice;
import com.tascape.qa.th.exception.EntityCommunicationException;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This test suite supports plug-n-play for multiple devices.
 *
 * @author linsong wang
 */
public interface UiAutomatorTestSuite {

    BlockingQueue<String> SERIALS = new ArrayBlockingQueue<>(Adb.getAllSerials().size(), true, Adb.getAllSerials());

    default UiAutomatorDevice getAvailableDevice() throws IOException, InterruptedException,
        EntityCommunicationException {
        String serial = SERIALS.poll(10, TimeUnit.SECONDS);
        try {
            Adb adb = new Adb(serial);
            UiAutomatorDevice device = new UiAutomatorDevice(IUiDevice.UIAUTOMATOR_RMI_PORT);
            device.setAdb(adb);
            device.start();
            return device;
        } finally {
            SERIALS.offer(serial);
        }
    }
}
