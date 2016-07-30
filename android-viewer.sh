#!/bin/bash

mvn clean install

java -cp uia-tool/target/*:uia-tool/target/dependency/* com.tascape.reactor.android.tools.UiAutomatorViewer
