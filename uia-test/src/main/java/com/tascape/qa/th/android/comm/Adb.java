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
package com.tascape.qa.th.android.comm;

import com.tascape.qa.th.SystemConfiguration;
import com.tascape.qa.th.comm.EntityCommunication;
import com.tascape.qa.th.exception.EntityCommunicationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private final static String ADB = locateAdb();

    private static String locateAdb() {
        String sysAdb = SystemConfiguration.getInstance().getProperty(SYSPROP_ADB_EXECUTABLE);
        if (sysAdb != null) {
            return sysAdb;
        } else {
            String paths = System.getenv().get("PATH");
            if (paths != null) {
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

    public static List<String> getDeviceSerials() throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        cmdLine.addArgument("devices");
        LOG.debug("{}", cmdLine.toString());
        List<String> output = new ArrayList<>();
        Executor executor = new DefaultExecutor();
        executor.setStreamHandler(new AdbStreamHandler(output));
        if (executor.execute(cmdLine) != 0) {
            throw new IOException(cmdLine + " failed");
        }
        List<String> serials = new ArrayList<>();
        for (String line : output) {
            if (line.endsWith("device")) {
                serials.add(line.split("\\t")[0]);
            }
        }
        LOG.debug("{}", serials);
        return serials;
    }

    public Adb() throws IOException, EntityCommunicationException {
        this("");
    }

    public Adb(String serial) throws IOException, EntityCommunicationException {
        if (StringUtils.isEmpty(serial)) {
            List<String> output = this.adb(Arrays.asList(new Object[]{"devices"}));
            for (String line : output) {
                if (line.endsWith("device")) {
                    this.serial = line.split("\\t")[0];
                    break;
                }
            }
        } else {
            this.serial = serial;
        }
        LOG.debug("serial number '{}'", this.serial);
        if (StringUtils.isEmpty(this.serial)) {
            throw new EntityCommunicationException("Device serial number issue");
        }
    }

    @Override
    public void connect() throws Exception {
    }

    @Override
    public void disconnect() throws Exception {
    }

    public List<String> adb(final List<Object> arguments) throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        if (!this.serial.isEmpty()) {
            cmdLine.addArgument("-s");
            cmdLine.addArgument(serial);
        }
        for (Object arg : arguments) {
            cmdLine.addArgument(arg + "");
        }
        LOG.debug("{}", cmdLine.toString());
        List<String> output = new ArrayList<>();
        Executor executor = new DefaultExecutor();
        executor.setStreamHandler(new AdbStreamHandler(output));
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
        return shellAsync(arguments, timeoutMillis, null);
    }

    public ExecuteWatchdog shellAsync(final List<Object> arguments, long timeoutMillis, File output) throws IOException {
        List<Object> args = new ArrayList<>(arguments);
        args.add(0, "shell");
        return adbAsync(args, timeoutMillis, output);
    }

    public ExecuteWatchdog adbAsync(final List<Object> arguments, long timeoutMillis, File output) throws IOException {
        CommandLine cmdLine = new CommandLine(ADB);
        if (!this.serial.isEmpty()) {
            cmdLine.addArgument("-s");
            cmdLine.addArgument(serial);
        }
        for (Object arg : arguments) {
            cmdLine.addArgument(arg + "");
        }
        LOG.debug("{}", cmdLine.toString());
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMillis);
        Executor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
        executor.setStreamHandler(new AdbStreamToFileHandler(output));
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
    }

    public void setupAdbPortForward(int local, int remote) throws IOException, InterruptedException {
        List<Object> cmdLine = new ArrayList<>();
        cmdLine.add("forward");
        cmdLine.add("tcp:" + local);
        cmdLine.add("tcp:" + remote);

        this.adb(cmdLine);
        LOG.debug("Device of serial '{}' is at localhost:{}", this.serial, local);
    }

    private static class AdbStreamHandler implements ExecuteStreamHandler {
        private final List<String> output;

        AdbStreamHandler(List<String> output) {
            this.output = output;
        }

        @Override
        public void setProcessInputStream(OutputStream out) throws IOException {
            LOG.trace("setProcessInputStream");
        }

        @Override
        public void setProcessErrorStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            do {
                String line = bis.readLine();
                if (line == null) {
                    break;
                }
                LOG.debug(line);
            } while (true);
        }

        @Override
        public void setProcessOutputStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            do {
                String line = bis.readLine();
                if (line == null) {
                    break;
                }
                LOG.debug(line);
                output.add(line);
            } while (true);
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

    private class AdbStreamToFileHandler implements ExecuteStreamHandler {
        File output;

        AdbStreamToFileHandler(File output) {
            this.output = output;
        }

        @Override
        public void setProcessInputStream(OutputStream out) throws IOException {
            LOG.trace("setProcessInputStream");
        }

        @Override
        public void setProcessErrorStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            do {
                String line = bis.readLine();
                if (line == null) {
                    break;
                }
                LOG.warn(line);
            } while (true);
        }

        @Override
        public void setProcessOutputStream(InputStream in) throws IOException {
            BufferedReader bis = new BufferedReader(new InputStreamReader(in));
            if (this.output == null) {
                String line = "";
                while (line != null) {
                    LOG.trace(line);
                    line = bis.readLine();
                }

            } else {
                PrintWriter pw = new PrintWriter(this.output);
                LOG.debug("Log stdout to {}", this.output);
                String line = "";
                try {
                    while (line != null) {
                        pw.println(line);
                        pw.flush();
                        line = bis.readLine();
                    }
                } finally {
                    pw.flush();
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
        Adb.getDeviceSerials();
    }
}
