/*
 * Copyright 2016 tascape.
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
package com.tascape.qa.th.android.model;

import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.Point;
import com.android.uiautomator.stub.PointerCoords;
import com.android.uiautomator.stub.Rect;
import com.android.uiautomator.stub.UiObjectNotFoundException;
import com.android.uiautomator.stub.UiSelector;
import com.tascape.qa.th.android.driver.UiAutomatorDevice;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiNode implements IUiObject {
    private static final Logger LOG = LoggerFactory.getLogger(UIA.class);

    public static final String TAG_NAME = "node";

    private int index = 0;

    private String text;

    private String resourceId;

    private String klass;

    private String pakkage;

    private String contentDesc;

    private boolean checkable;

    private boolean checked;

    private boolean clickable;

    private boolean enabled;

    private boolean focusable;

    private boolean focused;

    private boolean scrollable;

    private boolean longClickable;

    private boolean password;

    private boolean selected;

    private boolean naf;

    private Rectangle bounds;

    private final List<UiNode> nodes = new ArrayList<>();

    private UiNode parent = null;

    private UiAutomatorDevice device;

    public int getIndex() {
        return index;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getKlass() {
        return klass;
    }

    public String getPakkage() {
        return pakkage;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    @Override
    public boolean isCheckable() {
        return checkable;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isFocusable() {
        return focusable;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean isScrollable() {
        return scrollable;
    }

    @Override
    public boolean isLongClickable() {
        return longClickable;
    }

    public boolean isPassword() {
        return password;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public boolean isNaf() {
        return naf;
    }

    public Rectangle getBoundsOf() {
        return bounds;
    }

    public UiNode getParent() {
        return parent;
    }

    public UiNode[] nodes() {
        return nodes.toArray(new UiNode[0]);
    }

    /**
     * Clicks on the center of this node.
     *
     * @return adb stdout
     *
     * @throws IOException in case of ADB issue
     */
    public List<String> clickOn() throws IOException {
        int x = bounds.x + bounds.width / 2;
        int y = bounds.y + bounds.height / 2;
        return this.device.inputTap(x, y);
    }

    public UiNode findByResourceId(String resourceId) {
        if (this.resourceId.equals(resourceId)) {
            return this;
        }
        for (UiNode n : nodes) {
            UiNode node = n.findByResourceId(resourceId);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public UiNode findByResourceText(String text) {
        if (this.text.equals(text)) {
            return this;
        }
        for (UiNode n : nodes) {
            UiNode node = n.findByResourceText(text);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public JSONObject toJson() {
        JSONObject node = new JSONObject()
            .put("text", getText())
            .put("resource-id", getResourceId())
            .put("class", getKlass())
            .put("package", getPakkage())
            .put("content-desc", getContentDesc())
            .put("checkable", isCheckable())
            .put("checked", isChecked())
            .put("clickable", isClickable())
            .put("enabled", isEnabled())
            .put("focusable", isFocusable())
            .put("focused", isFocused())
            .put("scrollable", isScrollable())
            .put("long-clickable", isLongClickable())
            .put("password", isPassword())
            .put("selected", isSelected())
            .put("NAF", isNaf())
            .put("bounds", String.format("[%d,%d][%d,%d]", bounds.x, bounds.y, bounds.width, bounds.height))
            .put("index", getIndex());

        if (!nodes.isEmpty()) {
            JSONArray ns = new JSONArray();
            nodes.forEach(n -> ns.put(n.toJson()));
            node.put("nodes", ns);
        }
        return node;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.index;
        hash = 41 * hash + Objects.hashCode(this.text);
        hash = 41 * hash + Objects.hashCode(this.resourceId);
        hash = 41 * hash + Objects.hashCode(this.klass);
        hash = 41 * hash + Objects.hashCode(this.pakkage);
        hash = 41 * hash + Objects.hashCode(this.contentDesc);
        hash = 41 * hash + (this.checkable ? 1 : 0);
        hash = 41 * hash + (this.checked ? 1 : 0);
        hash = 41 * hash + (this.clickable ? 1 : 0);
        hash = 41 * hash + (this.enabled ? 1 : 0);
        hash = 41 * hash + (this.focusable ? 1 : 0);
        hash = 41 * hash + (this.focused ? 1 : 0);
        hash = 41 * hash + (this.scrollable ? 1 : 0);
        hash = 41 * hash + (this.longClickable ? 1 : 0);
        hash = 41 * hash + (this.password ? 1 : 0);
        hash = 41 * hash + (this.selected ? 1 : 0);
        hash = 41 * hash + (this.naf ? 1 : 0);
        hash = 41 * hash + Objects.hashCode(this.bounds);
        hash = 41 * hash + Objects.hashCode(this.nodes);
        hash = 41 * hash + Objects.hashCode(this.parent);
        hash = 41 * hash + Objects.hashCode(this.device);
        return hash;
    }

    @Override
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UiNode other = (UiNode) obj;
        if (this.index != other.index) {
            return false;
        }
        if (this.checkable != other.checkable) {
            return false;
        }
        if (this.checked != other.checked) {
            return false;
        }
        if (this.clickable != other.clickable) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.focusable != other.focusable) {
            return false;
        }
        if (this.focused != other.focused) {
            return false;
        }
        if (this.scrollable != other.scrollable) {
            return false;
        }
        if (this.longClickable != other.longClickable) {
            return false;
        }
        if (this.password != other.password) {
            return false;
        }
        if (this.selected != other.selected) {
            return false;
        }
        if (this.naf != other.naf) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.resourceId, other.resourceId)) {
            return false;
        }
        if (!Objects.equals(this.klass, other.klass)) {
            return false;
        }
        if (!Objects.equals(this.pakkage, other.pakkage)) {
            return false;
        }
        if (!Objects.equals(this.contentDesc, other.contentDesc)) {
            return false;
        }
//        if (!Objects.equals(this.bounds, other.bounds)) {
//            return false;
//        }
//        if (!Objects.equals(this.nodes, other.nodes)) {
//            return false;
//        }
//        if (!Objects.equals(this.parent, other.parent)) {
//            return false;
//        }
//        if (!Objects.equals(this.device, other.device)) {
//            return false;
//        }
        return true;
    }

    @Override
    public void clearUiObjectNotFoundException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UiObjectNotFoundException getUiObjectNotFoundException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasUiObjectNotFoundException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void useUiObjectSelector(UiSelector selector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearTextField() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clickAndWaitForNewWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clickAndWaitForNewWindow(long timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clickBottomRight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clickTopLeft() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean dragTo(IUiObject destObj, int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean dragTo(int destX, int destY, int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectChild(UiSelector selector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean selectFromParent(UiSelector selector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPackageName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rect getVisibleBounds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean longClick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean longClickBottomRight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean longClickTopLeft() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean performMultiPointerGesture(PointerCoords[]... touches) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean performTwoPointerGesture(Point startPoint1, Point startPoint2, Point endPoint1, Point endPoint2,
        int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean pinchIn(int percent, int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean pinchOut(int percent, int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean swipeDown(int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean swipeLeft(int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean swipeRight(int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean swipeUp(int steps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean waitForExists(long timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean waitUntilGone(long timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean click() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rect getBounds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setText(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setUiAutomatorDevice(UiAutomatorDevice device) {
        this.device = device;
        this.nodes.forEach(node -> node.setUiAutomatorDevice(device));
    }

    UiNode setAttribute(String name, String value) throws UiException {
        switch (name) {
            case "text":
                setText(value);
                break;
            case "resource-id":
                setResourceId(value);
                break;
            case "class":
                setKlass(value);
                break;
            case "package":
                setPakkage(value);
                break;
            case "content-desc":
                setContentDesc(value);
                break;
            case "checkable":
                setCheckable(Boolean.parseBoolean(value));
                break;
            case "checked":
                setChecked(Boolean.parseBoolean(value));
                break;
            case "clickable":
                setClickable(Boolean.parseBoolean(value));
                break;
            case "enabled":
                setEnabled(Boolean.parseBoolean(value));
                break;
            case "focusable":
                setFocusable(Boolean.parseBoolean(value));
                break;
            case "focused":
                setFocused(Boolean.parseBoolean(value));
                break;
            case "scrollable":
                setScrollable(Boolean.parseBoolean(value));
                break;
            case "long-clickable":
                setLongClickable(Boolean.parseBoolean(value));
                break;
            case "password":
                setPassword(Boolean.parseBoolean(value));
                break;
            case "selected":
                setSelected(Boolean.parseBoolean(value));
                break;
            case "bounds":
                setBounds(UIA.parseBounds(value));
                break;
            case "index":
                setIndex(Integer.parseInt(value));
                break;
            case "NAF":
                setNaf(Boolean.parseBoolean(value));
                break;
            default:
                throw new UiException("Unknown node attribute " + name);
        }
        return this;
    }

    void setTextOf(String text) {
        this.text = text;
    }

    void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    void setKlass(String klass) {
        this.klass = klass;
    }

    void setPakkage(String pakkage) {
        this.pakkage = pakkage;
    }

    void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    void setChecked(boolean checked) {
        this.checked = checked;
    }

    void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    void setFocused(boolean focused) {
        this.focused = focused;
    }

    void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    void setPassword(boolean password) {
        this.password = password;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    void setNaf(boolean naf) {
        this.naf = naf;
    }

    void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    void setIndex(int index) {
        this.index = index;
    }

    void addNode(UiNode node) {
        node.setParent(this);
        nodes.add(node);
    }

    void setParent(UiNode parent) {
        this.parent = parent;
    }
}
