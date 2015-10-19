package com.android.uiautomator.stub;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiScrollable;
import java.io.Serializable;

/**
 *
 * @author linsong wang
 */
public interface IUiObject extends Serializable {

    /**
     * Clears the existing UiObjectNotFoundException (set to null).
     */
    void clearUiObjectNotFoundException();

    /**
     * Gets and clears the existing UiObjectNotFoundException.
     *
     * @return
     */
    UiObjectNotFoundException getUiObjectNotFoundException();

    /**
     * Checks if there is existing UiObjectNotFoundException (not null).
     *
     * @return
     */
    boolean hasUiObjectNotFoundException();

    void useUiObjectSelector(UiSelector selector);

    /**
     * Clears the existing text contents in an editable field.
     *
     * The {@link UiSelector} of this object must reference a UI element that is editable.
     *
     * When you call this method, the method first sets focus at the start edge of the field.
     * The method then simulates a long-press to select the existing text, and deletes the
     * selected text.
     *
     * If a "Select-All" option is displayed, the method will automatically attempt to use it
     * to ensure full text selection.
     *
     * Note that it is possible that not all the text in the field is selected; for example,
     * if the text contains separators such as spaces, slashes, at symbol etc.
     * Also, not all editable fields support the long-press functionality.
     *
     * @since API Level 16
     */
    void clearTextField();

    /**
     * Performs a click at the center of the visible bounds of the UI element represented
     * by this UiObject.
     *
     * @return true if successful else false
     *
     * @since API Level 16
     */
    boolean click();

    /**
     * Waits for window transitions that would typically take longer than the
     * usual default timeouts.
     * See {@link #clickAndWaitForNewWindow(long)}
     *
     * @return true if the event was triggered, else false
     *
     * @since API Level 16
     */
    boolean clickAndWaitForNewWindow();

    /**
     * Performs a click at the center of the visible bounds of the UI element represented
     * by this UiObject and waits for window transitions.
     *
     * This method differ from {@link UiObject#click()} only in that this method waits for a
     * a new window transition as a result of the click. Some examples of a window transition:
     * <li>launching a new activity</li>
     * <li>bringing up a pop-up menu</li>
     * <li>bringing up a dialog</li>
     *
     * @param timeout timeout before giving up on waiting for a new window
     *
     * @return true if the event was triggered, else false
     *
     * @since API Level 16
     */
    boolean clickAndWaitForNewWindow(long timeout);

    /**
     * Clicks the bottom and right corner of the UI element
     *
     * @return true on success
     *
     * @since API Level 16
     */
    boolean clickBottomRight();

    /**
     * Clicks the top and left corner of the UI element
     *
     * @return true on success
     *
     * @since API Level 16
     */
    boolean clickTopLeft();

    /**
     * Drags this object to a destination UiObject. (not supported yet)
     * The number of steps specified in your input parameter can influence the
     * drag speed, and varying speeds may impact the results. Consider
     * evaluating different speeds when using this method in your tests.
     *
     * @param destObj the destination UiObject.
     * @param steps   usually 40 steps. You can increase or decrease the steps to change the speed.
     *
     * @return true if successful
     *
     * @since API Level 18
     */
    boolean dragTo(IUiObject destObj, int steps);

    /**
     * Drags this object to arbitrary coordinates.
     * The number of steps specified in your input parameter can influence the
     * drag speed, and varying speeds may impact the results. Consider
     * evaluating different speeds when using this method in your tests.
     *
     * @param destX the X-axis coordinate.
     * @param destY the Y-axis coordinate.
     * @param steps usually 40 steps. You can increase or decrease the steps to change the speed.
     *
     * @return true if successful
     *
     * @since API Level 18
     */
    boolean dragTo(int destX, int destY, int steps);

    /**
     * Check if view exists.
     *
     * This methods performs a {@link #waitForExists(long)} with zero timeout. This
     * basically returns immediately whether the view represented by this UiObject
     * exists or not. If you need to wait longer for this view, then see
     * {@link #waitForExists(long)}.
     *
     * @return true if the view represented by this UiObject does exist
     *
     * @since API Level 16
     */
    boolean exists();

    /**
     * Returns the view's <code>bounds</code> property. See {@link #getVisibleBounds()}
     *
     * @return Rect
     *
     * @since API Level 16
     */
    Rect getBounds();

    /**
     * Creates a new UiObject for a child view that is under the present UiObject.
     *
     * @param selector for child view to match
     *
     * @return
     *
     * @since API Level 16
     */
    boolean selectChild(UiSelector selector);

    /**
     * Counts the child views immediately under the present UiObject.
     *
     * @return the count of child views.
     *
     * @since API Level 16
     */
    int getChildCount();

    /**
     * Retrieves the <code>className</code> property of the UI element.
     *
     * @return class name of the current node represented by this UiObject
     *
     * @since API Level 18
     */
    String getClassName();

    /**
     * Reads the <code>content_desc</code> property of the UI element
     *
     * @return value of node attribute "content_desc"
     *
     * @since API Level 16
     */
    String getContentDescription();

    /**
     * Creates a new UiObject for a sibling view or a child of the sibling view,
     * relative to the present UiObject.
     *
     * @param selector for a sibling view or children of the sibling view
     * @return 
     *
     * @since API Level 16
     */
    boolean selectFromParent(UiSelector selector);

    /**
     * Reads the view's <code>package</code> property
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    String getPackageName();

    /**
     * Reads the <code>text</code> property of the UI element
     *
     * @return text value of the current node represented by this UiObject
     *
     * @since API Level 16
     */
    String getText();

    /**
     * Returns the visible bounds of the view.
     *
     * If a portion of the view is visible, only the bounds of the visible portion are
     * reported.
     *
     * @return Rect
     *
     * @see {@link #getBounds()}
     * @since API Level 17
     */
    Rect getVisibleBounds();

    /**
     * Checks if the UI element's <code>checkable</code> property is currently true.
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isCheckable();

    /**
     * Check if the UI element's <code>checked</code> property is currently true
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isChecked();

    /**
     * Checks if the UI element's <code>clickable</code> property is currently true.
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isClickable();

    /**
     * Checks if the UI element's <code>enabled</code> property is currently true.
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isEnabled();

    /**
     * Check if the UI element's <code>focusable</code> property is currently true.
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isFocusable();

    /**
     * Check if the UI element's <code>focused</code> property is currently true
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isFocused();

    /**
     * Check if the view's <code>long-clickable</code> property is currently true
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isLongClickable();

    /**
     * Check if the view's <code>scrollable</code> property is currently true
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isScrollable();

    /**
     * Checks if the UI element's <code>selected</code> property is currently true.
     *
     * @return true if it is else false
     *
     * @since API Level 16
     */
    boolean isSelected();

    /**
     * Long clicks the center of the visible bounds of the UI element
     *
     * @return true if operation was successful
     *
     * @since API Level 16
     */
    boolean longClick();

    /**
     * Long clicks bottom and right corner of the UI element
     *
     * @return true if operation was successful
     *
     * @since API Level 16
     */
    boolean longClickBottomRight();

    /**
     * Long clicks on the top and left corner of the UI element
     *
     * @return true if operation was successful
     *
     * @since API Level 16
     */
    boolean longClickTopLeft();

    /**
     * Performs a multi-touch gesture. You must specify touch coordinates for
     * at least 2 pointers. Each pointer must have all of its touch steps
     * defined in an array of {@link PointerCoords}. You can use this method to
     * specify complex gestures, like circles and irregular shapes, where each
     * pointer may take a different path.
     *
     * To create a single point on a pointer's touch path:
     * <code>
     *       PointerCoords p = new PointerCoords();
     *       p.x = stepX;
     *       p.y = stepY;
     *       p.pressure = 1;
     *       p.size = 1;
     * </code>
     *
     * @param touches represents the pointers' paths. Each {@link PointerCoords}
     *                array represents a different pointer. Each {@link PointerCoords} in an
     *                array element represents a touch point on a pointer's path.
     *
     * @return <code>true</code> if all touch events for this gesture are injected successfully,
     *         <code>false</code> otherwise
     *
     * @since API Level 18
     */
    boolean performMultiPointerGesture(PointerCoords[]... touches);

    /**
     * Generates a two-pointer gesture with arbitrary starting and ending points.
     *
     * @param startPoint1 start point of pointer 1
     * @param startPoint2 start point of pointer 2
     * @param endPoint1   end point of pointer 1
     * @param endPoint2   end point of pointer 2
     * @param steps       the number of steps for the gesture. Steps are injected
     *                    about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     *
     * @return <code>true</code> if all touch events for this gesture are injected successfully,
     *         <code>false</code> otherwise
     *
     * @since API Level 18
     */
    boolean performTwoPointerGesture(Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2, int steps);

    /**
     * Performs a two-pointer gesture, where each pointer moves diagonally
     * toward the other, from the edges to the center of this UiObject .
     *
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected
     *                about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     *
     * @return <code>true</code> if all touch events for this gesture are injected successfully,
     *         <code>false</code> otherwise
     *
     * @since API Level 18
     */
    boolean pinchIn(int percent, int steps);

    /**
     * Performs a two-pointer gesture, where each pointer moves diagonally
     * opposite across the other, from the center out towards the edges of the
     * this UiObject.
     *
     * @param percent percentage of the object's diagonal length for the pinch gesture
     * @param steps   the number of steps for the gesture. Steps are injected
     *                about 5 milliseconds apart, so 100 steps may take around 0.5 seconds to complete.
     *
     * @return <code>true</code> if all touch events for this gesture are injected successfully,
     *         <code>false</code> otherwise
     *
     * @since API Level 18
     */
    boolean pinchOut(int percent, int steps);

    /**
     * Sets the text in an editable field, after clearing the field's content.
     *
     * The {@link UiSelector} selector of this object must reference a UI element that is editable.
     *
     * When you call this method, the method first simulates a {@link #click()} on
     * editable field to set focus. The method then clears the field's contents
     * and injects your specified text into the field.
     *
     * If you want to capture the original contents of the field, call {@link #getText()} first.
     * You can then modify the text and use this method to update the field.
     *
     * @param text string to set
     *
     * @return true if operation is successful
     *
     * @since API Level 16
     */
    boolean setText(String text);

    /**
     * Performs the swipe down action on the UiObject.
     * The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * See also:
     * <ul>
     * <li>{@link UiScrollable#scrollToBeginning(int)}</li>
     * <li>{@link UiScrollable#scrollToEnd(int)}</li>
     * <li>{@link UiScrollable#scrollBackward()}</li>
     * <li>{@link UiScrollable#scrollForward()}</li>
     * </ul>
     *
     * @param steps indicates the number of injected move steps into the system. Steps are
     *              injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     *
     * @return true if successful
     *
     * @since API Level 16
     */
    boolean swipeDown(int steps);

    /**
     * Performs the swipe left action on the UiObject.
     * The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * See also:
     * <ul>
     * <li>{@link UiScrollable#scrollToBeginning(int)}</li>
     * <li>{@link UiScrollable#scrollToEnd(int)}</li>
     * <li>{@link UiScrollable#scrollBackward()}</li>
     * <li>{@link UiScrollable#scrollForward()}</li>
     * </ul>
     *
     * @param steps indicates the number of injected move steps into the system. Steps are
     *              injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     *
     * @return true if successful
     *
     * @since API Level 16
     */
    boolean swipeLeft(int steps);

    /**
     * Performs the swipe right action on the UiObject.
     * The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * See also:
     * <ul>
     * <li>{@link UiScrollable#scrollToBeginning(int)}</li>
     * <li>{@link UiScrollable#scrollToEnd(int)}</li>
     * <li>{@link UiScrollable#scrollBackward()}</li>
     * <li>{@link UiScrollable#scrollForward()}</li>
     * </ul>
     *
     * @param steps indicates the number of injected move steps into the system. Steps are
     *              injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     *
     * @return true if successful
     *
     * @since API Level 16
     */
    boolean swipeRight(int steps);

    /**
     * Performs the swipe up action on the UiObject.
     * See also:
     * <ul>
     * <li>{@link UiScrollable#scrollToBeginning(int)}</li>
     * <li>{@link UiScrollable#scrollToEnd(int)}</li>
     * <li>{@link UiScrollable#scrollBackward()}</li>
     * <li>{@link UiScrollable#scrollForward()}</li>
     * </ul>
     *
     * @param steps indicates the number of injected move steps into the system. Steps are
     *              injected about 5ms apart. So a 100 steps may take about 1/2 second to complete.
     *
     * @return true of successful
     *
     * @since API Level 16
     */
    boolean swipeUp(int steps);

    /**
     * Waits a specified length of time for a view to become visible.
     *
     * This method waits until the view becomes visible on the display, or
     * until the timeout has elapsed. You can use this method in situations where
     * the content that you want to select is not immediately displayed.
     *
     * @param timeout the amount of time to wait (in milliseconds)
     *
     * @return true if the view is displayed, else false if timeout elapsed while waiting
     *
     * @since API Level 16
     */
    boolean waitForExists(long timeout);

    /**
     * Waits a specified length of time for a view to become undetectable.
     *
     * This method waits until a view is no longer matchable, or until the
     * timeout has elapsed.
     *
     * A view becomes undetectable when the {@link UiSelector} of the object is
     * unable to find a match because the element has either changed its state or is no
     * longer displayed.
     *
     * You can use this method when attempting to wait for some long operation
     * to compete, such as downloading a large file or connecting to a remote server.
     *
     * @param timeout time to wait (in milliseconds)
     *
     * @return true if the element is gone before timeout elapsed, else false if timeout elapsed
     *         but a matching element is still found.
     *
     * @since API Level 16
     */
    boolean waitUntilGone(long timeout);

}
