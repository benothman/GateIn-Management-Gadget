/*
 *  Copyright (C) 2010 Red Hat, Inc. All rights reserved.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.management.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.Gadget.AllowHtmlQuirksMode;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.gadgets.client.Gadget.UseLongManifestName;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import java.util.logging.Logger;

/**
 * {@code Application}
 * 
 * Creates a gadget that will show the sites tree which allows to navigate
 * between different sites. This gadget allows the administrator of the portal
 * to export and/or import files/Apps.
 * 
 * Created on Dec 29, 2010, 8:01:18 PM
 * 
 * @author Nabil Benothman
 * @version 1.0
 */
@ModulePrefs(title = "GateIn Management", author = "Nabil Benothman", author_email = "nbenothm@redhat.com", description = "This gadget allows the administrator to export and import files/App")
@UseLongManifestName(true)
@AllowHtmlQuirksMode(true)
public class Application extends Gadget<UserPreferences> {

    private static final Logger logger = Logger.getLogger(Application.class.getName());
    // asycn services to get requests from the server through ajax.
    private final GateInServiceAsync gtnService = GWT.create(GateInService.class);
    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL()
            + "upload";
    // gui elements
    private SelectionHandler<TreeItem> selectionHandler;
    private OpenHandler<TreeItem> openHandler;
    private CloseHandler<TreeItem> closeHandler;
    private HTML header;
    private HTML details;

    @Override
    protected void init(UserPreferences preferences) {
        TreeImages images = GWT.create(TreeImages.class);

        RootPanel rootPanel = RootPanel.get();
        rootPanel.setSize("95%", "95%");

        DecoratedTabPanel decoratedTabPanel = new DecoratedTabPanel();
        decoratedTabPanel.setAnimationEnabled(true);
        rootPanel.add(decoratedTabPanel, 10, 10);
        decoratedTabPanel.setSize("790px", "383px");

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
        dockLayoutPanel.setSize("773px", "340px");
        StackPanel stackPanel = new StackPanel();
        dockLayoutPanel.addWest(stackPanel, 12.5);

        decoratedTabPanel.add(dockLayoutPanel, "Export file/app", false);
        stackPanel.setSize("98%", "95%");

        ScrollPanel scrollPanel = new ScrollPanel();
        stackPanel.add(scrollPanel, "Sites", false);
        scrollPanel.setSize("100%", "100%");
        scrollPanel.setWidget(getTree(images));

        StackPanel stackPanelEast = new StackPanel();
        dockLayoutPanel.addEast(stackPanelEast, 9.1);
        stackPanelEast.setSize("100%", "95%");

        AbsolutePanel absolutePanelImportLink = new AbsolutePanel();
        stackPanelEast.add(absolutePanelImportLink, "Import", false);
        absolutePanelImportLink.setSize("95%", "100%");

        final Anchor importAnchor = new Anchor("Import file");
        //importAnchor.setDirectionEstimator(true);
        absolutePanelImportLink.add(importAnchor, 10, 10);
        importAnchor.setWidth("90%");

        StackPanel stackPanelCenter = new StackPanel();
        dockLayoutPanel.add(stackPanelCenter);
        stackPanelCenter.setSize("99%", "95%");

        AbsolutePanel absolutePanel = new AbsolutePanel();
        stackPanelCenter.add(absolutePanel, "Item details", false);
        absolutePanel.setSize("97%", "100%");

        Label label = new Label("Select an item");
        label.setDirectionEstimator(true);
        absolutePanel.add(label, 10, 10);
        label.setSize("450px", "50px");

        InlineLabel inlineLabel = new InlineLabel("No item selected");
        absolutePanel.add(inlineLabel, 10, 76);
        inlineLabel.setSize("450px", "156px");

        HTML html = new HTML("<hr />", true);
        absolutePanel.add(html, 10, 43);
        html.setWidth("100%");

        Button button = new Button("Export site");
        absolutePanel.add(button, 10, 245);

        AbsolutePanel userManagementPanel = new AbsolutePanel();
        decoratedTabPanel.add(userManagementPanel, "User management", false);
        userManagementPanel.setSize("99%", "304px");

        Label label_1 = new Label("Enter a user name:");
        label_1.setDirectionEstimator(true);
        userManagementPanel.add(label_1, 10, 10);
        label_1.setSize("746px", "38px");

        SuggestBox suggestBox = new SuggestBox();
        userManagementPanel.add(suggestBox, 10, 40);
        suggestBox.setSize("215px", "13px");

        AbsolutePanel absolutePanel_1 = new AbsolutePanel();
        decoratedTabPanel.add(absolutePanel_1, "Dialog Box", false);
        absolutePanel_1.setSize("400px", "250px");

        Label label_2 = new Label("Select a site to import");
        label_2.setDirectionEstimator(true);
        absolutePanel_1.add(label_2, 10, 10);
        label_2.setSize("380px", "30px");

        FileUpload fileUpload = new FileUpload();
        absolutePanel_1.add(fileUpload, 10, 153);

        Button button_1 = new Button("Upload");
        absolutePanel_1.add(button_1, 257, 151);

        final DialogBox dialogBox = createDialogBox();
        importAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                importAnchor.setEnabled(false);
                dialogBox.center();
            }
        });


        decoratedTabPanel.selectTab(0);

    }

    /**
     * 
     * @return
     */
    private DialogBox createDialogBox() {
        // Create a dialog box and set the caption text
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Import site");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML boxDetails = new HTML("Details");
        dialogContents.add(boxDetails);
        dialogContents.setCellHorizontalAlignment(
                boxDetails, HasHorizontalAlignment.ALIGN_CENTER);

        // Add an image to the dialog
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("400px", "300px");
        dialogContents.add(absolutePanel);
        dialogContents.setCellHorizontalAlignment(
                absolutePanel, HasHorizontalAlignment.ALIGN_CENTER);

        // Add a close button at the bottom of the dialog
        Button closeButton = new Button("Close", new ClickHandler() {

            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        dialogContents.add(closeButton);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);

        } else {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        }

        // Return the dialog box
        return dialogBox;

    }

    /**
     * 
     * @param resources
     * @return
     */
    private Tree getTree(TreeImages resources) {
        Tree tree = new Tree(resources, true);
        tree.addCloseHandler(this.getCloseHandler());
        tree.addOpenHandler(this.getOpenHandler());
        tree.addSelectionHandler(this.getSelectionHandler());

        tree.setAnimationEnabled(true);
        tree.setSize("100%", "100%");
        //final TreeItem rootItem = tree.addItem(getItemString("Sites", resources.treeRoot()));
        final TreeItem rootItem = new TreeItem("Sites");
        tree.addItem(rootItem);

        gtnService.getRootNodes(new AsyncCallback<List<TreeNode>>() {

            public void onFailure(Throwable caught) {
                Window.alert("Loading tree failure " + caught);
            }

            public void onSuccess(List<TreeNode> result) {
                for (TreeNode tn : result) {
                    TreeItem ti = createItem(tn);
                    ti.addItem(new PendingItem());
                    rootItem.addItem(ti);
                }
            }
        });

        rootItem.setState(true);

        return tree;
    }

    /**
     * 
     * @param text
     * @param image
     * @return
     */
    private String getItemString(String text, ImageResource image) {
        // Add the image and text to a horizontal panel
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(0);
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.add(new Image(image));
        HTML headerText = new HTML(text);
        headerText.setStyleName("cw-StackPanelHeader");
        hPanel.add(headerText);

        // Return the HTML string for the panel
        return hPanel.getElement().getString();
    }

    /**
     *
     * @param tn
     * @return
     */
    private TreeItem createItem(TreeNode tn) {
        TreeItem item = new TreeItem(tn.getText());
        item.setUserObject(tn);

        return item;
    }

    /**
     * @return the selectionHandler
     */
    private SelectionHandler<TreeItem> getSelectionHandler() {
        if (this.selectionHandler == null) {
            this.selectionHandler = new SelectionHandler<TreeItem>() {

                public void onSelection(SelectionEvent<TreeItem> event) {
                    final TreeItem item = event.getSelectedItem();
                    TreeNode node = (TreeNode) item.getUserObject();
                    Application.this.header.setHTML(node.getPath());
                    Application.this.details.setHTML(node.getNodeInfo());
                }
            };
        }

        return this.selectionHandler;
    }

    /**
     * @return the openHandler
     */
    private OpenHandler<TreeItem> getOpenHandler() {
        if (this.openHandler == null) {
            this.openHandler = new OpenHandler<TreeItem>() {

                public void onOpen(OpenEvent<TreeItem> event) {

                    final TreeItem target = event.getTarget();
                    final TreeNode tn = (TreeNode) target.getUserObject();
                    String text = target.getText();
                    target.setText("Loading items");

                    int count = target.getChildCount();
                    if (count > 0) {
                        TreeItem it = target.getChild(0);
                        if (it instanceof PendingItem) {
                            target.removeItem(it);
                        }
                    }

                    if (target.getChildCount() == 0) {
                        gtnService.updateItem(tn,
                                new AsyncCallback<TreeNode>() {

                                    public void onFailure(Throwable caught) {
                                        Window.alert("Fail to update the tree items "
                                                + caught);
                                        Application.this.details.setHTML("Failed to load sub-tree");
                                    }

                                    public void onSuccess(TreeNode result) {

                                        for (TreeNode tnChild : result.getChildren()) {
                                            TreeItem it = Application.this.createItem(tnChild);
                                            tnChild.setPath(tn.getPath()
                                                    + " > " + it.getText());
                                            target.addItem(it);
                                        }
                                    }
                                });
                    }

                    target.setText(text);
                }
            };
        }

        return this.openHandler;
    }

    /**
     * @return the closeHandler
     */
    private CloseHandler<TreeItem> getCloseHandler() {
        if (this.closeHandler == null) {
            this.closeHandler = new CloseHandler<TreeItem>() {

                public void onClose(CloseEvent<TreeItem> event) {
                    GWT.log("closing item " + event.getTarget().getText());
                }
            };
        }

        return this.closeHandler;

    }
}
