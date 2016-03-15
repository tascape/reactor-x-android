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
import org.json.JSONObject;

/**
 *
 * @author wangl
 */
public class WindowHierarchy {

    public static final String TAG_NAME = "hierarchy";

    public final UIANode root;

    private String rotation;

    WindowHierarchy(UIANode root) {
        this.root = root;
    }

    public String getRotation() {
        return rotation;
    }

    public UIANode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return TAG_NAME + ", rotation=\"" + rotation + "\"";
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject()
            .put(TAG_NAME, new JSONObject()
                .put("rotation", rotation)
                .put(UIANode.TAG_NAME, root.toJson()));
        return json;
    }

    void setUiAutomatorDevice(UiAutomatorDevice device) {
        this.root.setUiAutomatorDevice(device);
    }

    void setRotation(String rotation) {
        this.rotation = rotation;
    }
}
