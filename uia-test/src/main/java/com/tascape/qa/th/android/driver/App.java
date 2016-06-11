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
package com.tascape.qa.th.android.driver;

import com.android.uiautomator.stub.IUiCollection;
import com.android.uiautomator.stub.IUiDevice;
import com.android.uiautomator.stub.IUiObject;
import com.android.uiautomator.stub.IUiScrollable;
import com.android.uiautomator.stub.Rect;
import com.google.common.collect.Lists;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.android.model.UIANode;
import com.tascape.qa.th.android.model.WindowHierarchy;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.exception.EntityDriverException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@SuppressWarnings("ProtectedField")
public abstract class App extends EntityDriver {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final String SYSPROP_APK_PATH = "qa.th.android.APK_PATH";

    public static final int NUMBER_OF_HOME_PAGE = 10;

    protected UiAutomatorDevice device;

    protected IUiDevice uiDevice;

    protected IUiObject uiObject;

    protected IUiCollection uiCollection;

    protected IUiScrollable uiScrollable;

    protected String version;

    public abstract String getPackageName();

    public abstract int getLaunchDelayMillis();

    @Override
    public String getVersion() {
        if (StringUtils.isBlank(version)) {
            try {
                version = device.getAppVersion(getPackageName());
            } catch (IOException | EntityDriverException ex) {
                LOG.warn(ex.getMessage());
                version = "";
            }
        }
        return version;
    }

    public UiAutomatorDevice getDevice() {
        return device;
    }

    public void setDevice(UiAutomatorDevice device) {
        this.device = device;
    }

    public void fetchUiaStubs() {
        uiDevice = device.getUiDevice();
        uiObject = device.getUiObject();
        uiCollection = device.getUiCollection();
        uiScrollable = device.getUiScrollable();
    }

    public void launch() throws IOException, InterruptedException {
        this.launch(true);
    }

    public void launch(boolean killExisting) throws IOException, InterruptedException {
        if (StringUtils.isBlank(this.getPackageName())) {
            return;
        }
        if (killExisting) {
            device.getAdb().shell(Lists.newArrayList("am", "force-stop", this.getPackageName()));
        }
        device.waitForIdle();
        device.getAdb().shell(Lists.newArrayList("monkey", "-p", this.getPackageName(), "1"));
        Utils.sleep(this.getLaunchDelayMillis(), "wait for app to launch");
    }

    public void launchFromUi(boolean killExisting) throws IOException, InterruptedException {
        if (killExisting) {
            device.getAdb().shell(Lists.newArrayList("am", "force-stop", this.getPackageName()));
        }
        device.backToHome();
        device.waitForIdle();
        if (device.descriptionExists("Apps")) {
            device.clickByDescription("Apps");
        }

        String name = getName();
        if (!device.textExists(name)) {
            int w = device.getScreenDimension().width;
            int h = device.getScreenDimension().height;
            device.dragHorizontally(w * NUMBER_OF_HOME_PAGE);
            for (int i = 0; i < NUMBER_OF_HOME_PAGE; i++) {
                if (device.textExists(name)) {
                    break;
                } else {
                    LOG.debug("swipe to next screen");
                    device.swipe(w / 2, h / 2, 0, h / 2, 5);
                    device.takeDeviceScreenshot();
                }
            }
        }
        device.clickByText(name);
        Utils.sleep(this.getLaunchDelayMillis(), "wait for app to launch");
        device.waitForIdle();
    }

    /**
     * The method starts a GUI to let an user inspect element tree and take screenshot when the user is interacting
     * with the app-under-test manually. Please make sure to set timeout long enough for manual interaction.
     *
     * @param timeoutMinutes timeout in minutes to fail the manual steps
     *
     * @throws Exception if case of error
     */
    public void interactManually(int timeoutMinutes) throws Exception {
        LOG.info("Start manual UI interaction");
        long end = System.currentTimeMillis() + timeoutMinutes * 60000L;

        AtomicBoolean visible = new AtomicBoolean(true);
        AtomicBoolean pass = new AtomicBoolean(false);
        String tName = Thread.currentThread().getName() + "m";
        SwingUtilities.invokeLater(() -> {
            JDialog jd = new JDialog((JFrame) null, "Manual Device UI Interaction - " + device.getProductDetail());
            jd.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            JPanel jpContent = new JPanel(new BorderLayout());
            jd.setContentPane(jpContent);
            jpContent.setPreferredSize(new Dimension(1088, 828));
            jpContent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel jpInfo = new JPanel();
            jpContent.add(jpInfo, BorderLayout.PAGE_START);
            jpInfo.setLayout(new BorderLayout());
            {
                JButton jb = new JButton("PASS");
                jb.setForeground(Color.green.darker());
                jb.setFont(jb.getFont().deriveFont(Font.BOLD));
                jpInfo.add(jb, BorderLayout.LINE_START);
                jb.addActionListener(event -> {
                    pass.set(true);
                    jd.dispose();
                    visible.set(false);
                });
            }
            {
                JButton jb = new JButton("FAIL");
                jb.setForeground(Color.red);
                jb.setFont(jb.getFont().deriveFont(Font.BOLD));
                jpInfo.add(jb, BorderLayout.LINE_END);
                jb.addActionListener(event -> {
                    pass.set(false);
                    jd.dispose();
                    visible.set(false);
                });
            }

            JLabel jlTimeout = new JLabel("xxx seconds left", SwingConstants.CENTER);
            jpInfo.add(jlTimeout, BorderLayout.CENTER);
            jpInfo.add(jlTimeout, BorderLayout.CENTER);
            new SwingWorker<Long, Long>() {
                @Override
                protected Long doInBackground() throws Exception {
                    while (System.currentTimeMillis() < end) {
                        Thread.sleep(1000);
                        long left = (end - System.currentTimeMillis()) / 1000;
                        this.publish(left);
                    }
                    return 0L;
                }

                @Override
                protected void process(List<Long> chunks) {
                    Long l = chunks.get(chunks.size() - 1);
                    jlTimeout.setText(l + " seconds left");
                    if (l < 850) {
                        jlTimeout.setForeground(Color.red);
                    }
                }
            }.execute();

            JPanel jpResponse = new JPanel(new BorderLayout());
            JPanel jpProgress = new JPanel(new BorderLayout());
            jpResponse.add(jpProgress, BorderLayout.PAGE_START);

            JTextArea jtaJson = new JTextArea();
            jtaJson.setEditable(false);
            jtaJson.setTabSize(4);
            Font font = jtaJson.getFont();
            jtaJson.setFont(new Font("Courier New", font.getStyle(), font.getSize()));

            JTree jtView = new JTree();

            JTabbedPane jtp = new JTabbedPane();
            jtp.add("tree", new JScrollPane(jtView));
            jtp.add("json", new JScrollPane(jtaJson));

            jpResponse.add(jtp, BorderLayout.CENTER);

            JPanel jpScreen = new JPanel();
            jpScreen.setMinimumSize(new Dimension(200, 200));
            jpScreen.setLayout(new BoxLayout(jpScreen, BoxLayout.PAGE_AXIS));
            JScrollPane jsp1 = new JScrollPane(jpScreen);
            jpResponse.add(jsp1, BorderLayout.LINE_START);

            JPanel jpJs = new JPanel(new BorderLayout());
            JTextArea jtaJs = new JTextArea();
            jpJs.add(new JScrollPane(jtaJs), BorderLayout.CENTER);

            JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpResponse, jpJs);
            jSplitPane.setResizeWeight(0.88);
            jpContent.add(jSplitPane, BorderLayout.CENTER);

            JPanel jpLog = new JPanel();
            jpLog.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            jpLog.setLayout(new BoxLayout(jpLog, BoxLayout.LINE_AXIS));

            JCheckBox jcbTap = new JCheckBox("Enable Click", null, false);
            jpLog.add(jcbTap);
            jpLog.add(Box.createHorizontalStrut(8));

            JButton jbLogUi = new JButton("Log Screen");
            jpResponse.add(jpLog, BorderLayout.PAGE_END);
            {
                jpLog.add(jbLogUi);
                jbLogUi.addActionListener((ActionEvent event) -> {
                    jtaJson.setText("waiting for screenshot...");
                    Thread t = new Thread(tName) {
                        @Override
                        public void run() {
                            LOG.debug("\n\n");
                            try {
                                WindowHierarchy wh = device.loadWindowHierarchy();
                                jtView.setModel(getModel(wh));

                                jtaJson.setText("");
                                jtaJson.append(wh.root.toJson().toString(2));
                                jtaJson.append("\n");

                                File png = device.takeDeviceScreenshot();
                                BufferedImage image = ImageIO.read(png);

                                int w = device.getDisplayWidth();
                                int h = device.getDisplayHeight();

                                BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2 = resizedImg.createGraphics();
                                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                g2.drawImage(image, 0, 0, w, h, null);
                                g2.dispose();

                                JLabel jLabel = new JLabel(new ImageIcon(resizedImg));
                                jpScreen.removeAll();
                                jsp1.setPreferredSize(new Dimension(w + 30, h));
                                jpScreen.add(jLabel);

                                jLabel.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        LOG.debug("clicked at {},{}", e.getPoint().getX(), e.getPoint().getY());
                                        if (jcbTap.isSelected()) {
                                            device.click(e.getPoint().x, e.getPoint().y);
                                            device.waitForIdle();
                                            jbLogUi.doClick();
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                LOG.error("Cannot log screen", ex);
                                jtaJson.append("Cannot log screen");
                            }
                            jtaJson.append("\n\n\n");
                            LOG.debug("\n\n");

                            jd.setSize(jd.getBounds().width + 1, jd.getBounds().height + 1);
                            jd.setSize(jd.getBounds().width - 1, jd.getBounds().height - 1);
                        }
                    };
                    t.start();
                });
            }
            jpLog.add(Box.createHorizontalStrut(38));
            {
                JButton jbLogMsg = new JButton("Log Message");
                jpLog.add(jbLogMsg);
                JTextField jtMsg = new JTextField(10);
                jpLog.add(jtMsg);
                jtMsg.addFocusListener(new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent pE) {
                    }

                    @Override
                    public void focusGained(final FocusEvent pE) {
                        jtMsg.selectAll();
                    }
                });
                jtMsg.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent e) {
                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                            jbLogMsg.doClick();
                        }
                    }
                });
                jbLogMsg.addActionListener(event -> {
                    Thread t = new Thread(tName) {
                        @Override
                        public void run() {
                            String msg = jtMsg.getText();
                            if (StringUtils.isNotBlank(msg)) {
                                LOG.info("{}", msg);
                                jtMsg.selectAll();
                            }
                        }
                    };
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                        LOG.error("Cannot take screenshot", ex);
                    }
                    jtMsg.requestFocus();
                });
            }
            jpLog.add(Box.createHorizontalStrut(38));
            {
                JButton jbClear = new JButton("Clear");
                jpLog.add(jbClear);
                jbClear.addActionListener(event -> {
                    jtaJson.setText("");
                });
            }

            JPanel jpAction = new JPanel();
            jpContent.add(jpAction, BorderLayout.PAGE_END);
            jpAction.setLayout(new BoxLayout(jpAction, BoxLayout.LINE_AXIS));
            jpJs.add(jpAction, BorderLayout.PAGE_END);

            jd.pack();
            jd.setVisible(true);
            jd.setLocationRelativeTo(null);

            jbLogUi.doClick();
        });

        while (visible.get()) {
            if (System.currentTimeMillis() > end) {
                LOG.error("Manual UI interaction timeout");
                break;
            }
            Thread.sleep(500);
        }

        if (pass.get()) {
            LOG.info("Manual UI Interaction returns PASS");
        } else {
            Assert.fail("Manual UI Interaction returns FAIL");
        }
    }

    private TreeModel getModel(WindowHierarchy wh) {
        DefaultMutableTreeNode rootNode = createNode(wh.root);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        return treeModel;
    }

    private DefaultMutableTreeNode createNode(UIANode uiNode) {
        Rect bounds = uiNode.getBounds();
        String s = uiNode.getKlass() + " " + uiNode.getContentDescription() + " "
            + String.format("[%d,%d][%d,%d]", bounds.left, bounds.top, bounds.right, bounds.bottom);
        DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(s);
        tNode.setUserObject(uiNode);

        for (UIANode n : uiNode.nodes()) {
            tNode.add(createNode(n));
        }
        return tNode;
    }
}
