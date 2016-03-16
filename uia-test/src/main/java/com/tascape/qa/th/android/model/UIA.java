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

import com.android.uiautomator.stub.Rect;
import com.tascape.qa.th.android.driver.UiAutomatorDevice;
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

    public static WindowHierarchy parseHierarchy(File file, UiAutomatorDevice device) throws IOException,
        SAXException,
        ParserConfigurationException {
        try (InputStream in = FileUtils.openInputStream(file)) {
            return parseHierarchy(in, device);
        }
    }

    public static WindowHierarchy parseHierarchy(InputStream in, UiAutomatorDevice device) throws SAXException,
        ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);

        Element doc = document.getDocumentElement();
        NodeList nl = doc.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            UIANode uiNode = parseNode(node);
            if (uiNode != null) {
                WindowHierarchy hierarchy = new WindowHierarchy(uiNode);
                hierarchy.setRotation(doc.getAttribute("rotation"));
                LOG.debug("{}", hierarchy);
                hierarchy.setUiAutomatorDevice(device);
                return hierarchy;
            }
        }
        throw new UIAException("Cannot parse view hierarchy");
    }

    public static UIANode parseNode(Node node) {
        if (!node.getNodeName().equals(UIANode.TAG_NAME)) {
            return null;
        }

        NamedNodeMap map = node.getAttributes();
        String klass = map.getNamedItem("class").getNodeValue();
        UIANode uiNode = newNode(klass);

        for (int i = 0, j = map.getLength(); i < j; i++) {
            Node attr = map.item(i);
            uiNode.setAttribute(attr.getNodeName(), attr.getNodeValue());
        }

        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            UIANode n = parseNode(nl.item(i));
            if (n == null) {
                continue;
            }
            uiNode.addNode(n);
        }

        return uiNode;
    }

    private static UIANode newNode(String klass) {
        switch (klass) {
            case ActionMenuView.CLASS_ANME:
                return new ActionMenuView();
            case AutoCompleteTextView.CLASS_ANME:
                return new AutoCompleteTextView();
            case Button.CLASS_ANME:
                return new Button();
            case CalendarView.CLASS_ANME:
                return new CalendarView();
            case CheckBox.CLASS_ANME:
                return new CheckBox();
            case Chronometer.CLASS_ANME:
                return new Chronometer();
            case CompoundButton.CLASS_ANME:
                return new CompoundButton();
            case DatePicker.CLASS_ANME:
                return new DatePicker();
            case EditText.CLASS_ANME:
                return new EditText();
            case FrameLayout.CLASS_ANME:
                return new FrameLayout();
            case GridLayout.CLASS_ANME:
                return new GridLayout();
            case GridView.CLASS_ANME:
                return new GridView();
            case HorizontalScrollView.CLASS_ANME:
                return new HorizontalScrollView();
            case ImageButton.CLASS_ANME:
                return new ImageButton();
            case ImageView.CLASS_ANME:
                return new ImageView();
            case LinearLayout.CLASS_ANME:
                return new LinearLayout();
            case ListView.CLASS_ANME:
                return new ListView();
            case NumberPicker.CLASS_ANME:
                return new NumberPicker();
            case OverScroller.CLASS_ANME:
                return new OverScroller();
            case PopupMenu.CLASS_ANME:
                return new PopupMenu();
            case PopupWindow.CLASS_ANME:
                return new PopupWindow();
            case ProgressBar.CLASS_ANME:
                return new ProgressBar();
            case QuickContactBadge.CLASS_ANME:
                return new QuickContactBadge();
            case RadioButton.CLASS_ANME:
                return new RadioButton();
            case RadioGroup.CLASS_ANME:
                return new RadioGroup();
            case RatingBar.CLASS_ANME:
                return new RatingBar();
            case RecyclerView.CLASS_ANME:
                return new RecyclerView();
            case RelativeLayout.CLASS_ANME:
                return new RelativeLayout();
            case RemoteViews.CLASS_ANME:
                return new RemoteViews();
            case ScrollView.CLASS_ANME:
                return new ScrollView();
            case Scroller.CLASS_ANME:
                return new Scroller();
            case SearchView.CLASS_ANME:
                return new SearchView();
            case SeekBar.CLASS_ANME:
                return new SeekBar();
            case Space.CLASS_ANME:
                return new Space();
            case Spinner.CLASS_ANME:
                return new Spinner();
            case Switch.CLASS_ANME:
                return new Switch();
            case TabHost.CLASS_ANME:
                return new TabHost();
            case TabWidget.CLASS_ANME:
                return new TabWidget();
            case TableLayout.CLASS_ANME:
                return new TableLayout();
            case TableRow.CLASS_ANME:
                return new TableRow();
            case TextClock.CLASS_ANME:
                return new TextClock();
            case TextSwitcher.CLASS_ANME:
                return new TextSwitcher();
            case TextView.CLASS_ANME:
                return new TextView();
            case TimePicker.CLASS_ANME:
                return new TimePicker();
            case Toast.CLASS_ANME:
                return new Toast();
            case ToggleButton.CLASS_ANME:
                return new ToggleButton();
            case Toolbar.CLASS_ANME:
                return new Toolbar();
            case VideoView.CLASS_ANME:
                return new VideoView();
            case View.CLASS_ANME:
                return new View();
            case ViewAnimator.CLASS_ANME:
                return new ViewAnimator();
            case ViewFlipper.CLASS_ANME:
                return new ViewFlipper();
            case ViewGroup.CLASS_ANME:
                return new ViewGroup();
            case ViewPager.CLASS_ANME:
                return new ViewPager();
            case ViewSwitcher.CLASS_ANME:
                return new ViewSwitcher();
            case ZoomControls.CLASS_ANME:
                return new ZoomControls();
            default:
                LOG.trace("Unkown node type {}, use " + UIANode.class.getSimpleName(), klass);
                return new UIANode();
        }
    }

    public static Rect parseBounds(String bounds) {
        Pattern pattern = Pattern.compile("\\[(\\d+?),(\\d+?)\\]\\[(\\d+?),(\\d+?)\\]");
        Matcher matcher = pattern.matcher(bounds);
        if (matcher.matches()) {
            Rect rect = new Rect();
            rect.left = Integer.parseInt(matcher.group(1));
            rect.top = Integer.parseInt(matcher.group(2));
            rect.right = Integer.parseInt(matcher.group(3));
            rect.bottom = Integer.parseInt(matcher.group(4));

            return rect;
        }
        throw new UIAException("Cannot parse bounds " + bounds);
    }

    public static void main(String[] args) throws Exception {
        Rect r = UIA.parseBounds("[0,0][1080,1812]");
        LOG.debug("{}", r);
        InputStream in = UIA.class.getResourceAsStream("hierarchy.xml");
        LOG.debug("{}", in);

        WindowHierarchy hierarchy = parseHierarchy(in, null);
        LOG.debug("{}", hierarchy.toString());
        LOG.debug("\n{}", hierarchy.toJson().toString(2));
    }
}
