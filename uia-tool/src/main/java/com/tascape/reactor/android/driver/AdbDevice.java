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
package com.tascape.reactor.android.driver;

import com.android.uiautomator.stub.IUiDevice;
import com.google.common.collect.Lists;
import com.tascape.reactor.android.comm.Adb;
import com.tascape.reactor.driver.EntityDriver;
import com.tascape.reactor.exception.EntityDriverException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
class AdbDevice extends EntityDriver {
    private static final Logger LOG = LoggerFactory.getLogger(AdbDevice.class);

    private Adb adb;

    private String version;

    public void setAdb(Adb adb) throws IOException {
        this.adb = adb;
    }

    public Adb getAdb() {
        return adb;
    }

    @Override
    public String getName() {
        try {
            return getPropValue("ro.product.brand") + "-" + getPropValue("ro.product.model");
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return "na";
        }
    }

    @Override
    public String getVersion() {
        if (StringUtils.isBlank(version)) {
            try {
                return getPropValue("ro.build.version.release") + "-" + getPropValue("ro.build.version.sdk ");
            } catch (IOException ex) {
                LOG.warn(ex.getMessage());
                version = "";
            }
        }
        return version;
    }

    public List<String> getProp() throws IOException {
        List<String> props = this.adb.shell(Lists.newArrayList("getprop"));
        props.forEach(p -> LOG.debug(p));
        return props;
    }

    public boolean grantPermission(String packageName, String permission) throws IOException {
        List<String> res = this.adb.shell(Lists.newArrayList("pm", packageName, permission));
        LOG.debug("{}", res);
        return res.stream().filter(l -> l.contains("Success")).findAny().isPresent();
    }

    public boolean uninstall(String packageName) throws IOException {
        List<String> res = this.adb.adb(Lists.newArrayList("uninstall", packageName));
        LOG.debug("{}", res);
        return res.stream().filter(l -> l.contains("Success")).findAny().isPresent();
    }

    public String getAppVersion(String packageName) throws IOException, EntityDriverException {
        List<String> res = this.adb.shell(Lists.newArrayList("dumpsys", "package", packageName));
        res.forEach(l -> LOG.debug(l));
        String versionName = "";
        String versionCode = "";
        for (int i = 0, j = res.size(); i < j; i++) {
            String line = res.get(i);
            if (line.contains("versionName")) {
                versionName = line.trim().split("=")[1];
            } else if (line.contains("versionCode")) {
                versionCode = line.trim().split(" ")[0].split("=")[1];
            }
            if (!versionName.isEmpty() && !versionCode.isEmpty()) {
                break;
            }
        }
        if (versionName.isEmpty() || versionCode.isEmpty()) {
            throw new EntityDriverException("Cannot find app version");
        }
        return versionName + "-" + versionCode;
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

    public String getPropValue(String name) throws IOException {
        List<String> res = this.getProp(name);
        return res.stream().filter(s -> !(s.startsWith("*") && s.endsWith("*"))).findFirst().get();
    }

    /**
     * Gets event output lines.
     *
     * @param device such as /dev/input/event0
     *
     * @return output event log lines
     *
     * @throws IOException in case of IO issue
     */
    public List<String> logTouchEvents(String device) throws IOException {
        return this.adb.shell(Arrays.asList(new Object[]{"getevent", "-lt", device}));
    }

    public String recordScreen(int seconds, int bitRate) throws IOException {
        String mp4 = "sr-" + UUID.randomUUID() + ".mp4";
        this.adb.shellAsync(Arrays.asList(new Object[]{"screenrecord", "--time-limit", seconds, IUiDevice.TMP_DIR + mp4,
            "--bit-rate", bitRate}), seconds * 1000L);
        return mp4;
    }

    public File getScreenRecord(String name) throws IOException {
        File mp4 = this.getLogPath().resolve(name).toFile();
        
        // todo
        this.getAdb().pull(IUiDevice.TMP_DIR + name, mp4);
        return mp4;
    }

    /**
     * Dumps window hierarchy into xml file. This does not work when 'uiautomator' process is running on device.
     *
     * @return the hierarchy xml file
     *
     * @throws IOException any error
     */
    public File dumpWindowHierarchy() throws IOException {
        String f = "/data/local/tmp/uidump.xml";
        adb.shell(Lists.newArrayList("rm", f)).forEach(l -> LOG.debug(l));
        adb.shell(Lists.newArrayList("uiautomator", "dump", f)).forEach(l -> LOG.debug(l));
        File xml = this.getLogPath().resolve("ui-" + System.currentTimeMillis() + ".xml").toFile();
        this.adb.pull(f, xml);
        LOG.debug("Save WindowHierarchy into {}", xml.getAbsolutePath());
        return xml;
    }

    /**
     * The input macro can emulate all sort of events, as described in its documentation.
     * <pre>
     * Usage: input [source] command [arg...]
     *
     * The sources are:
     *   trackball
     *   joystick
     *   touchnavigation
     *   mouse
     *   keyboard
     *   gamepad
     *   touchpad
     *   dpad
     *   stylus
     *   touchscreen
     *
     * The commands and default sources are:
     *   text 'string' (Default: touchscreen) [delay]
     *   keyevent [--longpress] 'key code number or name' ... (Default: keyboard)
     *   tap x y (Default: touchscreen)
     *   swipe x1 y1 x2 y2 [duration(ms)] (Default: touchscreen)
     *   press (Default: trackball)
     *   roll dx dy (Default: trackball)
     * </pre>
     *
     * @param arguments arguments
     *
     * @return adb stdout
     *
     * @throws IOException in case of any issue
     */
    public List<String> input(final List<Object> arguments) throws IOException {
        List<Object> args = new ArrayList<>(arguments);
        args.add(0, "input");
        return adb.shell(args);
    }

    /**
     * Sends keyboard key event to device. Shortcut to input(keyevent, ...).
     *
     * @param key key value, see class KeyEvent
     *
     * @return adb stdout
     *
     * @throws IOException in case of any issue
     */
    public List<String> inputKeyEvent(int key) throws IOException {
        return this.input(Lists.newArrayList("keyevent", key + ""));
    }

    /**
     * Sends text to device. Shortcut to input(text, ...).
     *
     * @param text text sent to device
     *
     * @return adb stdout
     *
     * @throws IOException in case of any issue
     */
    public List<String> inputText(String text) throws IOException {
        return this.input(Lists.newArrayList("text", text));
    }

    /**
     * Sends touchscreen tap event to device. Shortcut to input(tap, ...).
     *
     * @param x x
     * @param y y
     *
     * @return adb stdout
     *
     * @throws IOException in case of any issue
     */
    public List<String> inputTap(int x, int y) throws IOException {
        return this.input(Lists.newArrayList("tap", x, y));
    }

    /**
     * Emulates touchscreen interaction with sendevent in Android.
     *
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
    public void reset() throws Exception {
    }

    public String getSerial() {
        return this.adb.getSerial();
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
