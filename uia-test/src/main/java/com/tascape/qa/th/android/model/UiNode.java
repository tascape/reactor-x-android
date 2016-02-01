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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author linsong wang
 */
public class UiNode {

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

    private Rectangle bounds;

    private final List<UiNode> elements = new ArrayList<>();

    private UiNode parent;

    public int index() {
        return index;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject().put(this.getClass().getSimpleName(), new JSONObject()
            .put("index", index)
            .put("text", text)
            .put("x", bounds.x)
            .put("y", bounds.y)
            .put("w", bounds.width)
            .put("h", bounds.height));
        if (!elements.isEmpty()) {
            JSONArray jarr = new JSONArray();
            json.put("elements", jarr);
            elements.forEach(n -> {
                jarr.put(n.toJson());
            });
        }
        return json;
    }

    public List<String> logElement() {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%s %d \"%s\" [x=%s,y=%s,w=%s,h=%s]", getClass().getSimpleName(), index, text,
            bounds.x, bounds.y, bounds.width, bounds.height));
        if (!elements.isEmpty()) {
            lines.add("elements: (" + elements.size() + ") {");
            elements.forEach((e) -> {
                e.logElement().forEach((l) -> {
                    lines.add("    " + l);
                });
            });
            lines.add("}");
        }
        return lines;
    }

    @Override
    public String toString() {
        return StringUtils.join(logElement(), "\n");
    }

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

    public boolean isLongClickable() {
        return longClickable;
    }

    public boolean isPassword() {
        return password;
    }

    public boolean isSelected() {
        return selected;
    }

    public Rectangle getBounds() {
        return bounds;
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

    public boolean isScrollable() {
        return scrollable;
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

    void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    void setIndex(int index) {
        this.index = index;
    }

    void addElement(UiNode element) {
        element.setIndex(elements.size());
        element.setParent(this);
        elements.add(element);
    }

    void setParent(UiNode parent) {
        this.parent = parent;
    }
}
