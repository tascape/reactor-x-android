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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author linsong wang
 */
public class UIA {
    private static final Logger LOG = LoggerFactory.getLogger(UIA.class);

    public static UiHierarchy parseHierarchy(File file) throws UiException, IOException, SAXException,
        ParserConfigurationException {
        try (InputStream in = FileUtils.openInputStream(file)) {
            return parseHierarchy(in);
        }
    }

    public static UiHierarchy parseHierarchy(InputStream in) throws UiException, SAXException,
        ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);

        Element doc = document.getDocumentElement();
        NodeList nl = doc.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            LOG.debug("{} {}", node.getNodeName(), node.getNodeValue());
            UiNode uiNode = parseNode(node);
            if (uiNode != null) {
                UiHierarchy hierarchy = new UiHierarchy(uiNode);
                hierarchy.setRotation(doc.getAttribute("rotation"));
                LOG.debug("{}", hierarchy);
                return hierarchy;
            }
        }
        throw new UiException("Cannot parse view hierarchy");
    }

    public static UiNode parseNode(Node node) throws UiException {
        if (!node.getNodeName().equals(UiNode.TAG_NAME)) {
            return null;
        }

        UiNode un = new UiNode();
        NamedNodeMap map = node.getAttributes();
        LOG.debug("{}", map.getLength());
        for (int i = 0, j = map.getLength(); i < j; i++) {
            Node attr = map.item(i);
            un.setAttribute(attr.getNodeName(), attr.getNodeValue());
        }

        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            UiNode n = parseNode(nl.item(i));
            if (n == null) {
                continue;
            }
            un.addNode(n);
        }

        return un;
    }

    public static Rectangle parseBounds(String bounds) throws UiException {
        Pattern pattern = Pattern.compile("\\[(\\d+?),(\\d+?)\\]\\[(\\d+?),(\\d+?)\\]");
        Matcher matcher = pattern.matcher(bounds);
        if (matcher.matches()) {
            Rectangle rect = new Rectangle();
            rect.x = Integer.parseInt(matcher.group(1));
            rect.y = Integer.parseInt(matcher.group(2));
            rect.width = Integer.parseInt(matcher.group(3));
            rect.height = Integer.parseInt(matcher.group(4));

            return rect;
        }
        throw new UiException("Cannot parse bounds " + bounds);
    }

    public static void main(String[] args) throws Exception {
        Rectangle r = UIA.parseBounds("[0,0][1080,1812]");
        LOG.debug("{}", r);
        InputStream in = UIA.class.getResourceAsStream("hierarchy.xml");
        LOG.debug("{}", in);

        UiHierarchy hierarchy = parseHierarchy(in);
        LOG.debug("{}", hierarchy.toString());
        LOG.debug("\n{}", hierarchy.toJson().toString(2));
    }
}
