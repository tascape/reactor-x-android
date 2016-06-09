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
package com.tascape.qa.th.android.comm;

import com.google.common.collect.Lists;
import com.tascape.qa.th.SystemConfiguration;
import com.tascape.qa.th.comm.EntityCommunication;
import com.tascape.qa.th.exception.EntityCommunicationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public final class Adb extends EntityCommunication {
    private static final Logger LOG = LoggerFactory.getLogger(Adb.class);

    public static final String SYSPROP_ADB_EXECUTABLE = "qa.th.comm.ADB_EXECUTABLE";

    public static final String SYSPROP_SERIALS = "qa.th.comm.android.SERIALS";

    private static final List<String> SERIALS = new ArrayList<>();

    private static final Map<String, String> SERIAL_PRODUCT = new HashMap<>();

    private final static String ADB = locateAdb();

    private static String locateAdb() {
        String sysAdb = SystemConfiguration.getInstance().getProperty(SYSPROP_ADB_EXECUTABLE);
        if (sysAdb != null) {
            return sysAdb;
        } else {
            String paths = System.getenv().get("PATH");
            if (StringUtils.isBlank(paths)) {
                paths = System.getenv().get("Path");
            }
            if (StringUtils.isBlank(paths)) {
                paths = System.getenv().get("path");
            }
            if (StringUtils.isNotBlank(paths)) {
                String[] path = paths.split(System.getProperty("path.separator"));
                for (String p : path) {
                    LOG.debug("path {}", p);
                    File f = Paths.get(p, "adb").toFile();
                    if (f.exists()) {
                        return f.getAbsolutePath();
                    }
                }
            }
        }
        throw new RuntimeException("Cannot find adb based on system PATH. Please specify where adb executable is by"
            + " setting system property " + SYSPROP_ADB_EXECUTABLE + "=/path/to/your/sdk/platform-tools/adb");
    }

    private String serial = "";

    public static void reset() throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        cmdLine.addArgument("kill-server");
        LOG.debug("{}", cmdLine.toString());
        Executor executor = new DefaultExecutor();
        if (executor.execute(cmdLine) != 0) {
            throw new IOException(cmdLine + " failed");
        }
        cmdLine = new CommandLine(ADB);
        cmdLine.addArgument("devices");
        LOG.debug("{}", cmdLine.toString());
        executor = new DefaultExecutor();
        if (executor.execute(cmdLine) != 0) {
            throw new IOException(cmdLine + " failed");
        }
    }

    public static synchronized List<String> getAllSerials() {
        if (SERIALS.isEmpty()) {
            loadAllSerials();
        }
        return SERIALS;
    }

    public static synchronized Map<String, String> getSerialProduct() {
        if (SERIAL_PRODUCT.isEmpty()) {
            loadSerialProductMap();
        }
        return SERIAL_PRODUCT;
    }

    private static void loadAllSerials() {
        SERIALS.clear();
        String serials = SystemConfiguration.getInstance().getProperty(SYSPROP_SERIALS);
        if (null != serials) {
            LOG.info("Use specified devices from system property {}={}", SYSPROP_SERIALS, serials);
            SERIALS.addAll(Lists.newArrayList(serials.split(",")));
        } else {
            CommandLine cmdLine = new CommandLine(ADB);
            cmdLine.addArgument("devices");
            LOG.debug("{}", cmdLine.toString());
            List<String> output = new ArrayList<>();
            Executor executor = new DefaultExecutor();
            executor.setStreamHandler(new ESH(output));
            try {
                if (executor.execute(cmdLine) != 0) {
                    throw new RuntimeException(cmdLine + " failed");
                }
            } catch (IOException ex) {
                throw new RuntimeException(cmdLine + " failed", ex);
            }
            output.stream().filter((line) -> (line.endsWith("device"))).forEach((line) -> {
                String s = line.split("\\t")[0];
                LOG.info("serial {}", s);
                SERIALS.add(s);
            });
        }
        if (SERIALS.isEmpty()) {
            throw new RuntimeException("No device detected.");
        }
    }

    private static void loadSerialProductMap() {
        SERIAL_PRODUCT.clear();
        String serials = SystemConfiguration.getInstance().getProperty(SYSPROP_SERIALS);
        if (null != serials) {
            LOG.info("Use specified devices from system property {}={}", SYSPROP_SERIALS, serials);
            Lists.newArrayList(serials.split(",")).forEach(s -> SERIAL_PRODUCT.put(s, "na"));
        } else {
            CommandLine cmdLine = new CommandLine(ADB);
            cmdLine.addArgument("devices");
            cmdLine.addArgument("-l");
            LOG.debug("{}", cmdLine.toString());
            List<String> output = new ArrayList<>();
            Executor executor = new DefaultExecutor();
            executor.setStreamHandler(new ESH(output));
            try {
                if (executor.execute(cmdLine) != 0) {
                    throw new RuntimeException(cmdLine + " failed");
                }
            } catch (IOException ex) {
                throw new RuntimeException(cmdLine + " failed", ex);
            }
            output.stream()
                .map(line -> StringUtils.split(line, " ", 3))
                .filter(ss -> ss.length == 3 && ss[1].equals("device"))
                .forEach(ss -> {
                    LOG.info("device {} -> {}", ss[0], ss[2]);
                    SERIAL_PRODUCT.put(ss[0], ss[2]);
                });
        }
        if (SERIAL_PRODUCT.isEmpty()) {
            throw new RuntimeException("No device detected.");
        }
        SERIALS.addAll(SERIAL_PRODUCT.keySet());
    }

    public Adb() throws IOException, EntityCommunicationException {
        this("");
    }

    public Adb(String serial) throws IOException, EntityCommunicationException {
        this.serial = serial;
        LOG.debug("serial number '{}'", this.serial);
    }

    @Override
    public void connect() throws Exception {
        LOG.debug("NA'");
    }

    @Override
    public void disconnect() throws Exception {
        LOG.debug("NA'");
    }

    public List<String> adb(final List<Object> arguments) throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        if (!this.serial.isEmpty()) {
            cmdLine.addArgument("-s");
            cmdLine.addArgument(serial);
        }
        arguments.forEach((arg) -> {
            cmdLine.addArgument(arg + "");
        });
        LOG.debug("{}", cmdLine.toString());
        List<String> output = new ArrayList<>();
        Executor executor = new DefaultExecutor();
        executor.setStreamHandler(new ESH(output));
        if (executor.execute(cmdLine) != 0) {
            throw new IOException(cmdLine + " failed");
        }
        return output;
    }

    public List<String> shell(final List<Object> arguments) throws IOException {
        List<Object> args = new ArrayList<>(arguments);
        args.add(0, "shell");
        return adb(args);
    }

    public ExecuteWatchdog shellAsync(final List<Object> arguments, long timeoutMillis) throws IOException {
        List<Object> args = new ArrayList<>(arguments);
        args.add(0, "shell");
        return adbAsync(args, timeoutMillis);
    }

    public ExecuteWatchdog adbAsync(final List<Object> arguments, long timeoutMillis) throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        if (!this.serial.isEmpty()) {
            cmdLine.addArgument("-s");
            cmdLine.addArgument(serial);
        }
        arguments.forEach((arg) -> {
            cmdLine.addArgument(arg + "");
        });
        LOG.debug("{}", cmdLine.toString());
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMillis);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
        executor.setStreamHandler(new ESH());
        executor.execute(cmdLine, new DefaultExecuteResultHandler());

        return watchdog;
    }

    public void pull(String device, File local) throws IOException {
        if (local.exists() && !local.delete()) {
            throw new IOException("Cannot delete existing local file");
        }
        this.adb(Arrays.asList(new Object[]{"pull", device, local.getAbsolutePath()}));
        if (!local.exists()) {
            throw new IOException("Cannot pull file from device to local");
        }
        this.shell(Arrays.asList("rm", device));
    }

    public void setupAdbPortForward(int local, int remote) throws IOException, InterruptedException {
        List<Object> cmdLine = new ArrayList<>();
        cmdLine.add("forward");
        cmdLine.add("tcp:" + local);
        cmdLine.add("tcp:" + remote);
        this.adb(cmdLine);
        LOG.debug("Device of serial '{}' is at localhost:{}", this.serial, local);
    }

    public String getSerial() {
        return serial;
    }

    /**
     * Checks if a file exists in Android.
     *
     * @param path full path in Android
     *
     * @return true if file exists, false otherwise
     *
     * @throws IOException when error
     */
    public boolean fileExists(String path) throws IOException {
        List<String> lines = this.shell(Lists.newArrayList("ls", path));
        return lines.stream().filter(l -> l.equals(path)).findAny().isPresent();
    }

    private static class ESH implements ExecuteStreamHandler {
        private static final String PATTERN = ".+? KB/s \\(.+? bytes in .+?s\\)";

        private final List<String> list;

        ESH() {
            this.list = null;
        }

        ESH(List<String> list) {
            this.list = list;
        }

        @Override
        public void setProcessInputStream(OutputStream out) throws IOException {
            LOG.trace("setProcessInputStream");
        }

        @Override
        public void setProcessErrorStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = bis.readLine();
                if (line == null) {
                    break;
                }
                if (line.matches(PATTERN)) {
                    continue;
                } else {
                    LOG.warn(line);
                }
            }
        }

        @Override
        public void setProcessOutputStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = bis.readLine();
                if (line == null) {
                    break;
                }
                LOG.trace(line);
                if (list != null) {
                    list.add(line);
                }
            }
        }

        @Override
        public void start() throws IOException {
            LOG.trace("start");
        }

        @Override
        public void stop() {
            LOG.trace("stop");
        }
    }

    public static void main(String[] args) throws Exception {
        Adb.getAllSerials();
    }
}
