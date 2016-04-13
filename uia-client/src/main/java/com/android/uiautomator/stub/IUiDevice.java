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

import java.io.Serializable;
import net.sf.lipermi.exception.LipeRMIException;

/**
 *
 * @author linsong wang
 */
public interface IUiDevice extends Serializable {

    int UIAUTOMATOR_RMI_PORT = 8998;

    String TMP_DIR = "/data/local/tmp/local/tmp/";

    /**
     * Enables or disables layout hierarchy compression.
     *
     * If compression is enabled, the layout hierarchy derived from the Acessibility
     * framework will only contain nodes that are important for uiautomator
     * testing. Any unnecessary surrounding layout nodes that make viewing
     * and searching the hierarchy inefficient are removed.
     *
     * @param compressed true to enable compression; else, false to disable
     *
     * @since API Level 18
     */
    void setCompressedLayoutHeirarchy(boolean compressed);

    /**
     * Clears the text from the last UI traversal event. See
     * {@link #getLastTraversedText()}.
     */
    void clearLastTraversedText();

    /**
     * Perform a click at arbitrary coordinates specified by the user
     *
     * @param x
     *          coordinate
     * @param y
     *          coordinate
     *
     * @return true if the click succeeded else false
     */
    boolean click(int x, int y);

    /**
     * Helper method used for debugging to dump the current window's layout
     * hierarchy. The file root location is /data/local/tmp
     *
     * @param fileName name
     */
    void dumpWindowHierarchy(String fileName);

    /**
     * Disables the sensors and freezes the device rotation at its current
     * rotation state.
     *
     * @throws LipeRMIException for any rmi issue
     */
    void freezeRotation() throws LipeRMIException;

    /**
     * Retrieves the last activity to report accessibility events.
     *
     * @return String name of activity
     */
    String getCurrentActivityName();

    /**
     * Retrieves the name of the last package to report accessibility events.
     *
     * @return String name of package
     */
    String getCurrentPackageName();

    /**
     * Gets the height of the display, in pixels. The size is adjusted based on
     * the current orientation of the display.
     *
     * @return height in pixels or zero on failure
     */
    int getDisplayHeight();

    /**
     * Returns the current rotation of the display, as defined in Surface
     *
     * @return int
     */
    int getDisplayRotation();

    /**
     * Returns the display size in dp (device-independent pixel)
     *
     * The returned display size is adjusted per screen rotation
     *
     * @return a Point containing the display size in dp
     */
    public Point getDisplaySizeDp();

    /**
     * Returns the display width in dp (device-independent pixel)
     *
     * The returned display width is adjusted per screen rotation
     *
     * @return width in dp
     */
    int getDisplayWidthDp();

    /**
     * Returns the display height in dp (device-independent pixel)
     *
     * The returned display height is adjusted per screen rotation
     *
     * @return height in dp
     */
    int getDisplayHeightDp();

    /**
     * Gets the width of the display, in pixels. The width and height details
     * are reported based on the current orientation of the display.
     *
     * @return width in pixels or zero on failure
     */
    int getDisplayWidth();

    /**
     * Retrieves the text from the last UI traversal event received.
     *
     * You can use this method to read the contents in a WebView container
     * because the accessibility framework fires events as each text is
     * highlighted. You can write a test to perform directional arrow presses to
     * focus on different elements inside a WebView, and call this method to get
     * the text from each traversed element. If you are testing a view container
     * that can return a reference to a Document Object Model (DOM) object, your
     * test should use the view's DOM instead.
     *
     * @return text of the last traversal event, else return an empty string
     */
    String getLastTraversedText();

    /**
     * Retrieves the product name of the device.
     *
     * This method provides information on what type of device the test is
     * running on. If you are trying to test for different types of UI screen
     * sizes, your test should use {@link IUiDevice#getDisplaySizeDp()} instead.
     * This value is the same returned by invoking #adb shell getprop
     * ro.product.name.
     *
     * @return product name of the device
     */
    String getProductName();

    /**
     * Checks if any registered UiWatcher have triggered.
     *
     * See registerWatcher(String, UiWatcher). See hasWatcherTriggered(String)
     *
     * @return boolean
     */
    boolean hasAnyWatcherTriggered();

    /**
     * Checks if a specific registered UiWatcher has triggered. See
     * registerWatcher(String, UiWatcher). If a UiWatcher runs and its
     * UiWatcher#checkForCondition() call returned <code>true</code>,
     * then the UiWatcher is considered triggered. This is helpful if a watcher
     * is detecting errors from ANR or crash dialogs and the test needs to know
     * if a UiWatcher has been triggered.
     *
     * @param watcherName name
     *
     * @return true if triggered else false
     */
    boolean hasWatcherTriggered(String watcherName);

    /**
     * Check if the device is in its natural orientation. This is determined by
     * checking if the orientation is at 0 or 180 degrees.
     *
     * @return true if it is in natural orientation
     */
    boolean isNaturalOrientation();

    /**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     *
     * @throws LipeRMIException for any rmi issue
     */
    boolean isScreenOn() throws LipeRMIException;

    /**
     * Simulates a short press on the BACK button.
     *
     * @return true if successful, else return false
     */
    boolean pressBack();

    /**
     * Simulates a short press on the CENTER button.
     *
     * @return true if successful, else return false
     */
    boolean pressDPadCenter();

    /**
     * Simulates a short press on the DOWN button.
     *
     * @return true if successful, else return false
     */
    boolean pressDPadDown();

    /**
     * Simulates a short press on the LEFT button.
     *
     * @return true if successful, else return false
     */
    boolean pressDPadLeft();

    /**
     * Simulates a short press on the RIGHT button.
     *
     * @return true if successful, else return false
     */
    boolean pressDPadRight();

    /**
     * Simulates a short press on the UP button.
     *
     * @return true if successful, else return false
     */
    boolean pressDPadUp();

    /**
     * Simulates a short press on the DELETE key.
     *
     * @return true if successful, else return false
     */
    boolean pressDelete();

    /**
     * Simulates a short press on the ENTER key.
     *
     * @return true if successful, else return false
     */
    boolean pressEnter();

    /**
     * Simulates a short press on the HOME button.
     *
     * @return true if successful, else return false
     */
    boolean pressHome();

    /**
     * Simulates a short press using a key code.
     *
     * @param keyCode code
     *
     * @return true if successful, else return false
     */
    boolean pressKeyCode(int keyCode);

    /**
     * Simulates a short press using a key code.
     *
     * @param keyCode
     *                  the key code of the event.
     * @param metaState
     *                  an integer in which each bit set to 1 represents a pressed
     *                  meta key
     *
     * @return true if successful, else return false
     */
    boolean pressKeyCode(int keyCode, int metaState);

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     *
     * @since API Level 18
     */
    boolean openNotification();

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     *
     * @since API Level 18
     */
    boolean openQuickSettings();

    /**
     * Simulates a short press on the MENU button.
     *
     * @return true if successful, else return false
     */
    boolean pressMenu();

    /**
     * Simulates a short press on the Recent Apps button.
     *
     * @return true if successful, else return false
     *
     * @throws LipeRMIException is any rmi issue
     */
    boolean pressRecentApps() throws LipeRMIException;

    /**
     * Simulates a short press on the SEARCH button.
     *
     * @return true if successful, else return false
     */
    boolean pressSearch();

    /*
     * Registers a {@link UiWatcher} to run automatically when the testing
     * framework is unable to find a match using a {@link UiSelector}. See
     * {@link #runWatchers()}
     *
     * @param name
     * to register the UiWatcher
     * @param watcher
     * {@link UiWatcher}
     */
    void registerWatcher(String name, UiWatcher watcher);

    /**
     * Removes a previously registered UiWatcher.
     *
     * See registerWatcher(String, UiWatcher)
     *
     * @param name
     *             used to register the UiWatcher
     */
    void removeWatcher(String name);

    /**
     * Resets a UiWatcher that has been triggered. If a UiWatcher runs
     * and its UiWatcher#checkForCondition() call returned
     * <code>true</code>, then the UiWatcher is considered triggered. See
     * registerWatcher(String, UiWatcher)
     */
    void resetWatcherTriggers();

    /**
     * This method forces all registered watchers to run. See
     * registerWatcher(String, UiWatcher)
     */
    void runWatchers();

    /**
     * Simulates orienting the device to the left and also freezes rotation by
     * disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors see
     * {@link #unfreezeRotation()}.
     *
     * @throws LipeRMIException for any rmi issue
     */
    void setOrientationLeft() throws LipeRMIException;

    /**
     * Simulates orienting the device into its natural orientation and also
     * freezes rotation by disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors see
     * unfreezeRotation().
     *
     * @throws LipeRMIException for any rmi issue
     */
    void setOrientationNatural() throws LipeRMIException;

    /**
     * Simulates orienting the device to the right and also freezes rotation by
     * disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors see
     * unfreezeRotation().
     *
     * @throws LipeRMIException for any rmi issue
     */
    void setOrientationRight() throws LipeRMIException;

    /**
     * This method simply presses the power button if the screen is ON else it
     * does nothing if the screen is already OFF.
     *
     * @throws LipeRMIException for any rmi issue
     */
    void sleep() throws LipeRMIException;

    /**
     * Performs a swipe from one coordinate to another using the number of steps
     * to determine smoothness and speed. Each step execution is throttled to
     * 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to
     * complete.
     *
     * @param startX x
     * @param startY y
     * @param endX   x
     * @param endY   y
     * @param steps
     *               is the number of move steps sent to the system
     *
     * @return false if the operation fails or the coordinates are invalid
     */
    boolean swipe(int startX, int startY, int endX, int endY, int steps);

    /**
     * Performs a swipe between points in the Point array. Each step execution
     * is throttled to 5ms per step. So for a 100 steps, the swipe will take
     * about 1/2 second to complete
     *
     * @param segments
     *                     is Point array containing at least one Point object
     * @param segmentSteps
     *                     steps to inject between two Points
     *
     * @return true on success
     */
    boolean swipe(Point[] segments, int segmentSteps);

    /**
     * Performs a swipe from one coordinate to another coordinate. You can control
     * the smoothness and speed of the swipe by specifying the number of steps.
     * Each step execution is throttled to 5 milliseconds per step, so for a 100
     * steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of steps for the swipe action
     *
     * @return true if swipe is performed, false if the operation fails
     *         or the coordinates are invalid
     *
     * @since API Level 18
     */
    boolean drag(int startX, int startY, int endX, int endY, int steps);

    /**
     * Takes a screenshot of current window and store it as PNG
     *
     * Default scale of 1.0f (original size) and 90% quality is used
     *
     * @param name name of the PNG, in default data directory
     *
     * @return boolean
     */
    boolean takeScreenshot(String name);

    /**
     * Takes a screenshot of current window and store it as PNG
     *
     * The screenshot is adjusted per screen rotation;
     *
     * @param name    name of the PNG, in default data directory
     * @param scale   scale the screenshot down if needed; 1.0f for original size
     * @param quality quality of the PNG compression; range: 0-100
     *
     * @return boolean
     */
    boolean takeScreenshot(String name, float scale, int quality);

    /**
     * Re-enables the sensors and un-freezes the device rotation allowing its
     * contents to rotate with the device physical rotation. During a test
     * execution, it is best to keep the device frozen in a specific orientation
     * until the test case execution has completed.
     *
     * @throws LipeRMIException for any rmi issue
     */
    void unfreezeRotation() throws LipeRMIException;

    /**
     * Waits for the current application to idle. Default wait timeout is 10 seconds
     */
    void waitForIdle();

    /**
     * Waits for the current application to idle.
     *
     * @param time ms
     */
    void waitForIdle(long time);

    /**
     * Waits for a window content update event to occur.
     *
     * If a package name for the window is specified, but the current window
     * does not have the same package name, the function returns immediately.
     *
     * @param packageName
     *                    the specified window package name (can be <code>null</code>).
     *                    If <code>null</code>, a window update from any front-end
     *                    window will end the wait
     * @param timeout
     *                    the timeout for the wait
     *
     * @return true if a window update occurred, false if timeout has elapsed or
     *         if the current window does not have the specified package name
     */
    boolean waitForWindowUpdate(final String packageName, long timeout);

    /**
     * This method simulates pressing the power button if the screen is OFF else
     * it does nothing if the screen is already ON.
     *
     * If the screen was OFF and it just got turned ON, this method will insert
     * a 500ms delay to allow the device time to wake up and accept input.
     *
     * @throws LipeRMIException for any rmi issue
     */
    void wakeUp() throws LipeRMIException;
}
