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
package tascape.qa.th.demo;

import tascape.qa.th.comm.Adb;
import tascape.qa.th.driver.AndroidAdbDevice;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class TouchablitityTests {
    private static final Logger LOG = LoggerFactory.getLogger(TouchablitityTests.class);

    private AndroidAdbDevice adbDevice;

    public void setup() throws Exception {
        Adb adb = new Adb();
        this.adbDevice = new AndroidAdbDevice();
        this.adbDevice.setAdb(adb);
    }

    public void testOne() throws Exception {
        int seconds = 10;

        File log = this.adbDevice.logTouchEvents(seconds);
        String mp4 = this.adbDevice.recordScreen(seconds, 512000);

        LOG.info("Please interact with touch screen for {} seconds", seconds);
        Thread.sleep(seconds * 1100L);

        LOG.info("Done recording");

        LOG.debug("{}", log);

        List<Long> events = this.adbDevice.getTouchEvents(log);
//        List<Long> updates = this.adbDevice.getScreenUpdates(mp4);

        LOG.info("Touch Events: {}", events);
//        LOG.info("Screen Updates: {}", updates);

        // todo
        // data presentation and analysis
    }

    public static void main(String[] args) {
        try {
            TouchablitityTests tests = new TouchablitityTests();
            tests.setup();
            tests.testOne();
            System.exit(0);
        } catch (Throwable ex) {
            LOG.error("", ex);
        } finally {
            System.exit(0);
        }
    }
}
