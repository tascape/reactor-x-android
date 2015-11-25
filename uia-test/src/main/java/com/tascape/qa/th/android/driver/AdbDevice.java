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
public class AdbDevice extends EntityDriver {
    private static final Logger LOG = LoggerFactory.getLogger(AdbDevice.class);

    private Adb adb;

    public void setAdb(Adb adb) throws IOException {
        this.adb = adb;

        List<String> props = this.adb.shell(Lists.newArrayList("getprop"));
        props.forEach(p -> {
            LOG.debug(p);
        });
    }

    public Adb getAdb() {
        return adb;
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

    public File dumpWindowHierarchy() throws IOException {
        String f = "/data/local/tmp/uidump.xml";
        adb.shell(Lists.newArrayList("uiautomator", "dump", f));
        File xml = this.getLogPath().resolve("ui-" + System.currentTimeMillis() + ".xml").toFile();
        this.adb.pull(f, xml);
        LOG.debug("Save WindowHierarchy to {}", xml.getAbsolutePath());
        return xml;
    }

    /**
     * 0 --- "KEYCODE_UNKNOWN"
     * 1 --- "KEYCODE_MENU"
     * 2 --- "KEYCODE_SOFT_RIGHT"
     * 3 --- "KEYCODE_HOME"
     * 4 --- "KEYCODE_BACK"
     * 5 --- "KEYCODE_CALL"
     * 6 --- "KEYCODE_ENDCALL"
     * 7 --- "KEYCODE_0"
     * 8 --- "KEYCODE_1"
     * 9 --- "KEYCODE_2"
     * 10 --- "KEYCODE_3"
     * 11 --- "KEYCODE_4"
     * 12 --- "KEYCODE_5"
     * 13 --- "KEYCODE_6"
     * 14 --- "KEYCODE_7"
     * 15 --- "KEYCODE_8"
     * 16 --- "KEYCODE_9"
     * 17 --- "KEYCODE_STAR"
     * 18 --- "KEYCODE_POUND"
     * 19 --- "KEYCODE_DPAD_UP"
     * 20 --- "KEYCODE_DPAD_DOWN"
     * 21 --- "KEYCODE_DPAD_LEFT"
     * 22 --- "KEYCODE_DPAD_RIGHT"
     * 23 --- "KEYCODE_DPAD_CENTER"
     * 24 --- "KEYCODE_VOLUME_UP"
     * 25 --- "KEYCODE_VOLUME_DOWN"
     * 26 --- "KEYCODE_POWER"
     * 27 --- "KEYCODE_CAMERA"
     * 28 --- "KEYCODE_CLEAR"
     * 29 --- "KEYCODE_A"
     * 30 --- "KEYCODE_B"
     * 31 --- "KEYCODE_C"
     * 32 --- "KEYCODE_D"
     * 33 --- "KEYCODE_E"
     * 34 --- "KEYCODE_F"
     * 35 --- "KEYCODE_G"
     * 36 --- "KEYCODE_H"
     * 37 --- "KEYCODE_I"
     * 38 --- "KEYCODE_J"
     * 39 --- "KEYCODE_K"
     * 40 --- "KEYCODE_L"
     * 41 --- "KEYCODE_M"
     * 42 --- "KEYCODE_N"
     * 43 --- "KEYCODE_O"
     * 44 --- "KEYCODE_P"
     * 45 --- "KEYCODE_Q"
     * 46 --- "KEYCODE_R"
     * 47 --- "KEYCODE_S"
     * 48 --- "KEYCODE_T"
     * 49 --- "KEYCODE_U"
     * 50 --- "KEYCODE_V"
     * 51 --- "KEYCODE_W"
     * 52 --- "KEYCODE_X"
     * 53 --- "KEYCODE_Y"
     * 54 --- "KEYCODE_Z"
     * 55 --- "KEYCODE_COMMA"
     * 56 --- "KEYCODE_PERIOD"
     * 57 --- "KEYCODE_ALT_LEFT"
     * 58 --- "KEYCODE_ALT_RIGHT"
     * 59 --- "KEYCODE_SHIFT_LEFT"
     * 60 --- "KEYCODE_SHIFT_RIGHT"
     * 61 --- "KEYCODE_TAB"
     * 62 --- "KEYCODE_SPACE"
     * 63 --- "KEYCODE_SYM"
     * 64 --- "KEYCODE_EXPLORER"
     * 65 --- "KEYCODE_ENVELOPE"
     * 66 --- "KEYCODE_ENTER"
     * 67 --- "KEYCODE_DEL"
     * 68 --- "KEYCODE_GRAVE"
     * 69 --- "KEYCODE_MINUS"
     * 70 --- "KEYCODE_EQUALS"
     * 71 --- "KEYCODE_LEFT_BRACKET"
     * 72 --- "KEYCODE_RIGHT_BRACKET"
     * 73 --- "KEYCODE_BACKSLASH"
     * 74 --- "KEYCODE_SEMICOLON"
     * 75 --- "KEYCODE_APOSTROPHE"
     * 76 --- "KEYCODE_SLASH"
     * 77 --- "KEYCODE_AT"
     * 78 --- "KEYCODE_NUM"
     * 79 --- "KEYCODE_HEADSETHOOK"
     * 80 --- "KEYCODE_FOCUS"
     * 81 --- "KEYCODE_PLUS"
     * 82 --- "KEYCODE_MENU"
     * 83 --- "KEYCODE_NOTIFICATION"
     * 84 --- "KEYCODE_SEARCH"
     * 85 --- "TAG_LAST_KEYCODE"
     *
     * @param key key value
     *
     * @throws IOException in case of any issue
     */
    public void inputKeyEvent(int key) throws IOException {
        this.adb.shell(Lists.newArrayList("input", "keyevent", key + ""));
    }

    /**
     * @param text text sent to device
     *
     * @throws IOException in case of any issue
     */
    public void inputText(String text) throws IOException {
        this.adb.shell(Lists.newArrayList("input", "text", text));
    }

    /**
     * Emulates touchscreen interaction with sendevent in Android.
     * http://ktnr74.blogspot.com/2013/06/emulating-touchscreen-interaction-with.html.
     * busybox usleep 50000: wait at least 50 milliseconds
     *
     * @param device such as /dev/input/event0
     * @param type   For touch events only 2 event types are used:
     *               EV_ABS (3)
     *               EV_SYN (0)
     * @param code   Touching the display (in case of Type A protocol) will result in an input report (sequence of input
     *               events) containing the following event codes:
     *               ABS_MT_TRACKING_ID (57) - ID of the touch (important for multi-touch reports), value -1 to release
     *               ABS_MT_POSITION_X (53) - x coordinate of the touch
     *               ABS_MT_POSITION_Y (54) - y coordinate of the touch
     *               ABS_MT_TOUCH_MAJOR (48) - basically width of your finger tip in pixels, use 5
     *               ABS_MT_PRESSURE (58) - pressure of the touch, value 50
     *               SYN_MT_REPORT (2) - end of separate touch data, value 0
     *               SYN_REPORT (0) - end of report, value 0
     * @param value  event value
     *
     * @throws IOException in case of any issue
     */
    public void sendEvent(String device, int type, int code, int value) throws IOException {
        this.adb.shell(Lists.newArrayList("sendevent", "" + type, "" + code, "" + value));
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
        AdbDevice device = new AdbDevice();
        device.setAdb(adb);
    }
}
