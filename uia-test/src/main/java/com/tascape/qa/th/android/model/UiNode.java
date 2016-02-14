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

import com.tascape.qa.th.android.driver.UiAutomatorDevice;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class UiNode {
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

    public boolean isCheckable() {
        return checkable;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public boolean isFocused() {
        return focused;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public boolean isLongClickable() {
        return longClickable;
    }

    public boolean isPassword() {
        return password;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isNaf() {
        return naf;
    }

    public Rectangle getBounds() {
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
    public List<String> click() throws IOException {
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
            .put("checkable", isCheckable() + "")
            .put("checked", isChecked() + "")
            .put("clickable", isClickable() + "")
            .put("enabled", isEnabled() + "")
            .put("focusable", isFocusable() + "")
            .put("focused", isFocused() + "")
            .put("scrollable", isScrollable() + "")
            .put("long-clickable", isLongClickable() + "")
            .put("password", isPassword() + "")
            .put("selected", isSelected() + "")
            .put("NAF", isNaf() + "")
            .put("bounds", String.format("[%d,%d][%d,%d]", bounds.x, bounds.y, bounds.width, bounds.height))
            .put("index", getIndex());

        if (!nodes.isEmpty()) {
            JSONArray ns = new JSONArray();
            nodes.forEach(n -> ns.put(n.toJson()));
            node.put("nodes", ns);
        }
        return node;
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

    void setText(String text) {
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
