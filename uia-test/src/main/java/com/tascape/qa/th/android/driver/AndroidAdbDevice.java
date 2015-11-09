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
package com.tascape.qa.th.android.driver;

import com.google.common.collect.Lists;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.android.comm.Adb;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class AndroidAdbDevice extends EntityDriver {
    private static final Logger LOG = LoggerFactory.getLogger(AndroidAdbDevice.class);

    protected Adb adb;

    public void setAdb(Adb adb) throws IOException {
        this.adb = adb;

        List<String> props = this.adb.shell(Lists.newArrayList("getprop"));
        props.forEach(p -> {
            LOG.debug(p);
        });
    }

    public String getSystemLanguage() throws IOException {
        List<String> res = this.getProp("persist.sys.language");
        res.addAll(this.getProp("ro.product.locale.language"));
        return res.stream().filter(s -> StringUtils.isNotBlank(s)).findFirst().get();
    }

    public List<String> getProp(String name) throws IOException {
        List<String> res = this.adb.shell(Lists.newArrayList("getprop", name));
        LOG.debug("{}", res);
        return res;
    }

    public File logTouchEvents(int seconds) throws IOException {
        File log = File.createTempFile("TouchEvents", ".log");
        this.adb.shellAsync(Arrays.asList(new Object[]{"getevent", "-lt", "/dev/input/event2"}), seconds * 1000L, log);
        return log;
    }

    public String recordScreen(int seconds, int bitRate) throws IOException {
        String mp4 = "/sdcard/ScreenRecording.mp4";
        this.adb.shellAsync(Arrays.asList(new Object[]{"screenrecord", "--time-limit", seconds, mp4, "--bit-rate",
            bitRate}), seconds * 1000L);
        return mp4;
    }

    /**
     *
     * @param eventLogFile log
     *
     * @return event timestamps
     *
     * @throws java.io.FileNotFoundException if no file found
     * @throws java.io.IOException           if io issue
     */
    public List<Long> getTouchEvents(File eventLogFile) throws FileNotFoundException, IOException {
        List<Long> events = new ArrayList<>();
        BufferedReader bis = new BufferedReader(new FileReader(eventLogFile));
        String line = "";

        Pattern patternEvent = Pattern.compile("\\[(.+?)\\].+");
        while (line != null) {
            Matcher matcherEvent = patternEvent.matcher(line);
            if (matcherEvent.matches()) {
                LOG.trace("start time {}", line);
                String ts = matcherEvent.group(1);
                Double d = Double.parseDouble(ts);
                events.add((long) (d * 1000000));
                break;
            }
            line = bis.readLine();
        }

        Pattern patternButtonUp = Pattern.compile("\\[(.+?)\\] EV_KEY.+?BTN_TOUCH.+?UP.+");
        while (line != null) {
            Matcher matcherUp = patternButtonUp.matcher(line);
            if (matcherUp.matches()) {
                String ts = matcherUp.group(1);
                Double d = Double.parseDouble(ts);
                events.add((long) (d * 1000000));
            }
            line = bis.readLine();
        }

        if (!events.isEmpty()) {
            long start = events.get(0);
            for (int i = 0; i < events.size(); i++) {
                long time = events.get(i);
                events.set(i, time - start);
            }
            events.remove(0);
        }
        return events;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void reset() throws Exception {
    }

    public File getScreenRecord(String path) throws IOException {
        File mp4 = File.createTempFile("ScreenRecording", ".mp4");
        this.adb.pull(path, mp4);
        return mp4;
    }

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        boolean equal = true;
        for (int x = 100; x < img1.getWidth() - 100; x++) {
            for (int y = 100; y < img1.getHeight() - 100; y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    equal = false;
                }
            }
        }
        return equal;
    }

    public static void main(String[] args) throws Exception {
        Adb adb = new Adb();
        AndroidAdbDevice device = new AndroidAdbDevice();
        device.setAdb(adb);
    }
}