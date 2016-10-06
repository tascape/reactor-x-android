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
package com.tascape.reactor.android.task;

import com.tascape.reactor.android.driver.App;

/**
 * This interface provides default methods to be called in cases.
 *
 * @author linsong wang
 */
public interface UiAutomatorCase {

    default void runManully(App app) throws Exception {
        this.runManully(app, 30);
    }

    default void runManully(App app, int minutes) throws Exception {
        app.interactManually(minutes);
    }
}