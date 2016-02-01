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

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author linsong wang
 */
public class UIA {
    private static final Logger LOG = LoggerFactory.getLogger(UIA.class);

    public static UiHierarchy parseHierarchy(File file) throws UiException {
        return null;
    }

    public static void main(String[] args) throws Exception {
        InputStream in = UIA.class.getResourceAsStream("hierarchy.xml");
        LOG.debug("{}", in);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);

        Element root = document.getDocumentElement();
        LOG.debug("{}", root.getTagName());

        NodeList nl = root.getChildNodes();
        LOG.debug("{}", nl.getLength());
    }
}
