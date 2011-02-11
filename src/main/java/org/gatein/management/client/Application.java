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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gwtupload.client.MultiUploader;
import java.util.List;

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
@ModulePrefs(title = "GateIn Management", author = "Nabil Benothman", author_email = "nbenothm@redhat.com", description = "This gadget allows the administrator to export/import sites")
@UseLongManifestName(true)
@AllowHtmlQuirksMode(true)
public class Application extends Gadget<UserPreferences> {

    // asycn services to get requests from the server through ajax.
    private final GateInServiceAsync gtnService = GWT.create(GateInService.class);
    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "upload";
    private static final String DOWNLOAD_ACTION_URL = GWT.getModuleBaseURL() + "download";
    // gui elements
    private HTML header;
    private HTML details;
    private Button exportButton;
    private Frame frame;
    private String exportHref = "";

    @Override
    protected void init(UserPreferences preferences) {
        TreeImages images = GWT.create(TreeImages.class);

        RootPanel rootPanel = RootPanel.get();
        rootPanel.setSize("885px", "490px");
        rootPanel.addStyleName("rootpanelstyle");

        DecoratedTabPanel decoratedTabPanel = new DecoratedTabPanel();
        decoratedTabPanel.setAnimationEnabled(true);
        rootPanel.add(decoratedTabPanel, 10, 10);
        decoratedTabPanel.setSize("870px", "480px");

        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("847px", "425px");

        DecoratorPanel decoratorPanelWest = new DecoratorPanel();
        absolutePanel.add(decoratorPanelWest, 10, 10);
        decoratorPanelWest.setSize("240px", "400px");

        AbsolutePanel absolutePanel_1 = new AbsolutePanel();
        absolutePanel_1.setSize("230px", "395px");
        decoratorPanelWest.setWidget(absolutePanel_1);

        ScrollPanel treeScrollPanel = new ScrollPanel();
        absolutePanel_1.add(treeScrollPanel, 10, 10);
        treeScrollPanel.setSize("210px", "375px");

        final Tree tree = getTree(images);
        treeScrollPanel.setWidget(tree);

        final DecoratorPanel decoratorPanelCenter = new DecoratorPanel();

        absolutePanel.add(decoratorPanelCenter, 256, 10);
        decoratorPanelCenter.setSize("400px", "402px");

        AbsolutePanel centerAbsolutePanel = new AbsolutePanel();
        centerAbsolutePanel.setSize("97%", "100%");

        this.header = new HTML("Select an item");
        this.header.setDirectionEstimator(true);
        centerAbsolutePanel.add(this.header, 10, 10);
        this.header.setSize("450px", "50px");
        this.header.setStyleName("header-style");

        this.details = new HTML("No item selected");
        centerAbsolutePanel.add(this.details, 10, 76);
        this.details.setSize("450px", "156px");

        HTML html = new HTML("<hr />", true);
        centerAbsolutePanel.add(html, 10, 43);
        html.setSize("380px", "14px");

        this.frame = new NamedFrame("download-frame");
        frame.setStyleName("download-frame");
        rootPanel.add(frame);

        this.exportButton = new Button("Export site", new ClickHandler() {

            public void onClick(ClickEvent event) {
                frame.setUrl(exportHref);
            }
        });
        this.exportButton.setEnabled(false);
        centerAbsolutePanel.add(this.exportButton, 10, 359);
        decoratorPanelCenter.setWidget(centerAbsolutePanel);
        centerAbsolutePanel.setSize("400px", "393px");

        DecoratorPanel decoratorPanelEast = new DecoratorPanel();
        absolutePanel.add(decoratorPanelEast, 672, 10);
        decoratorPanelEast.setSize("165px", "405px");
        AbsolutePanel absolutePanelImportLink = new AbsolutePanel();
        absolutePanelImportLink.setSize("162px", "395px");

        final Anchor importAnchor = new Anchor("Import site");
        absolutePanelImportLink.add(importAnchor, 10, 10);
        importAnchor.setWidth("90%");

        decoratorPanelEast.setWidget(absolutePanelImportLink);

        decoratedTabPanel.add(absolutePanel, "Export/Import sites", false);
        Widget userManagementWidget = getUserManagementTab();
        decoratedTabPanel.add(userManagementWidget, "User management", false);

        final DialogBox dialogBox = createDialogBox();
        importAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dialogBox.setPopupPosition(267, 60);
                dialogBox.show();
            }
        });

        decoratedTabPanel.selectTab(0);
    }

    /**
     * Create and return the dialog box for the site upload.
     *
     * @return a {@code DialogBox}
     */
    private DialogBox createDialogBox() {
        // Create a dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Import site");
        dialogBox.setAnimationEnabled(true);
        dialogBox.setModal(true);
        dialogBox.setGlassEnabled(true);

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        Label label = new Label("Select a file to import : ");
        dialogContents.add(label);

        /*
        final FlowPanel panelImages = new FlowPanel();

        final OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {

        public void onLoad(PreloadedImage img) {
        img.setWidth("75px");
        panelImages.add(img);
        }
        };
        IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

        public void onFinish(IUploader uploader) {
        if (uploader.getStatus() == Status.SUCCESS) {
        new PreloadedImage(uploader.fileUrl(), showImage);
        }
        }
        };
         * 
         */

        final MultiUploader uploader = new MultiUploader();
        // Add a finish handler which will load the image once the upload finishes
        //uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
        //defaultUploader.setMaximumFiles(3);
        // You can add customized parameters to servlet call
        uploader.setServletPath(UPLOAD_ACTION_URL + "?overwrite=false");
        //defaultUploader.avoidRepeatFiles(true);

        dialogContents.add(uploader);

        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("400px", "50px");
        dialogContents.add(absolutePanel);
        dialogContents.setCellHorizontalAlignment(
                absolutePanel, HasHorizontalAlignment.ALIGN_LEFT);

        //absolutePanel.add(panelImages, 10, 10);

        final CheckBox overwriteBox = new CheckBox("Overwrite existing site");
        overwriteBox.setTitle("If you want to force overwriting an existing site, check this checkbox");
        overwriteBox.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (overwriteBox.getValue()) {
                    uploader.setServletPath(UPLOAD_ACTION_URL + "?overwrite=true");
                } else {
                    uploader.setServletPath(UPLOAD_ACTION_URL + "?overwrite=false");
                }
            }
        });
        absolutePanel.add(overwriteBox);

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

        return dialogBox;
    }

    /**
     * Create the user management content tab
     *
     * @return a {@code Widget} for the user management tab
     */
    private Widget getUserManagementTab() {
        AbsolutePanel userManagementPanel = new AbsolutePanel();
        userManagementPanel.setSize("855px", "304px");

        DecoratorPanel decoratorPanelEast = new DecoratorPanel();
        userManagementPanel.add(decoratorPanelEast, 0, 10);
        decoratorPanelEast.setSize("245px", "295px");

        AbsolutePanel absolutePanelEast = new AbsolutePanel();
        decoratorPanelEast.setWidget(absolutePanelEast);
        absolutePanelEast.setSize("235px", "285px");

        Label lblNewLabel = new Label("Enter a username :");
        lblNewLabel.setDirectionEstimator(true);
        absolutePanelEast.add(lblNewLabel, 10, 10);
        lblNewLabel.setSize("205px", "29px");

        final SuggestBox suggestBox = new SuggestBox(new ItemSuggestOracle());
        absolutePanelEast.add(suggestBox, 10, 45);
        suggestBox.setSize("210px", "21px");

        final InlineHTML userHeader = new InlineHTML("Select user");
        userHeader.setStyleName("header-style");
        final HTML userDetails = new HTML("No user selected", true);
        final Button exportBtn = new Button("Export site");

        Button showUserbtn = new Button("Show", new ClickHandler() {

            public void onClick(ClickEvent event) {
                String username = suggestBox.getValue();
                gtnService.getUserSite(username, new AsyncCallback<TreeNode>() {

                    public void onFailure(Throwable caught) {
                        exportBtn.setEnabled(false);
                        userHeader.setHTML("Failed to access remote server");
                        userDetails.setHTML(caught.getMessage());
                    }

                    public void onSuccess(TreeNode node) {
                        if (node.isExportable()) {
                            exportBtn.setEnabled(true);
                            Application.this.exportHref = DOWNLOAD_ACTION_URL + "?ownerType=" + node.getType() + "&ownerId=" + node.getSiteName();
                        } else {
                            exportBtn.setEnabled(false);
                            Application.this.exportHref = "#";
                        }

                        userHeader.setHTML(node.getSiteName());
                        userDetails.setHTML(node.getNodeInfo());
                    }
                });
            }
        });
        absolutePanelEast.add(showUserbtn, 69, 251);

        DecoratorPanel decoratorPanelCenter = new DecoratorPanel();
        userManagementPanel.add(decoratorPanelCenter, 251, 10);
        decoratorPanelCenter.setSize("584px", "295px");

        AbsolutePanel absolutePanelCenter = new AbsolutePanel();
        absolutePanelCenter.setSize("587px", "285px");
        decoratorPanelCenter.setWidget(absolutePanelCenter);

        exportBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                frame.setUrl(exportHref);
            }
        });
        exportBtn.setEnabled(false);
        absolutePanelCenter.add(exportBtn, 10, 251);

        absolutePanelCenter.add(userHeader, 10, 10);
        userHeader.setSize("567px", "29px");

        absolutePanelCenter.add(userDetails, 10, 101);
        userDetails.setSize("567px", "144px");

        InlineHTML nlnhtmlNewInlinehtml = new InlineHTML("<hr />");
        absolutePanelCenter.add(nlnhtmlNewInlinehtml, 10, 45);
        nlnhtmlNewInlinehtml.setSize("567px", "2px");

        return userManagementPanel;
    }

    /**
     * Create and initialize the site tree
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
        final TreeNode rootNode = new TreeNode("Sites");
        final TreeItem rootItem = createItem(rootNode);
        tree.addItem(rootItem);

        gtnService.getRootNodes(new AsyncCallback<List<TreeNode>>() {

            public void onFailure(Throwable caught) {
                Window.alert("Loading tree failure <br/>" + caught);
            }

            public void onSuccess(List<TreeNode> result) {
                for (TreeNode tn : result) {
                    TreeItem ti = createItem(tn);
                    ti.addItem(new PendingItem());
                    rootItem.addItem(ti);
                    rootNode.addChild(tn);
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
     * Create an {@code TreeItem} and set it's user object
     *
     * @param tn The user object of the {@code TreeItem}
     * @return {@code TreeItem}
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
        SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {

            public void onSelection(SelectionEvent<TreeItem> event) {
                final TreeItem item = event.getSelectedItem();
                TreeNode node = (TreeNode) item.getUserObject();
                Application.this.header.setHTML(node.getPath());
                Application.this.details.setHTML(node.getNodeInfo());

                if (node.isExportable()) {
                    Application.this.exportHref = DOWNLOAD_ACTION_URL + "?ownerType=" + node.getType() + "&ownerId=" + node.getSiteName();
                    Application.this.exportButton.setEnabled(true);
                } else {
                    Application.this.exportButton.setEnabled(false);
                    Application.this.exportHref = "#";
                }
            }
        };

        return selectionHandler;
    }

    /**
     * @return the openHandler
     */
    private OpenHandler<TreeItem> getOpenHandler() {
        OpenHandler<TreeItem> openHandler = new OpenHandler<TreeItem>() {

            public void onOpen(OpenEvent<TreeItem> event) {

                final TreeItem target = event.getTarget();
                final TreeNode tn = (TreeNode) target.getUserObject();
                String text = target.getText();
                target.setText("Loading items");

                if (target.getChildCount() > 0) {
                    TreeItem it = target.getChild(0);
                    if (it instanceof PendingItem) {
                        target.removeItem(it);
                    }
                }

                if (target.getChildCount() == 0) {
                    gtnService.updateItem(tn,
                            new AsyncCallback<TreeNode>() {

                                public void onFailure(Throwable caught) {
                                    Window.alert("Fail to update the tree items <br />"
                                            + caught);
                                    Application.this.details.setHTML("Failed to load sub-tree");
                                }

                                public void onSuccess(TreeNode result) {

                                    for (TreeNode tnChild : result.getChildren()) {
                                        TreeItem it = Application.this.createItem(tnChild);
                                        if (!tnChild.getChildren().isEmpty()) {
                                            it.addItem(new PendingItem());
                                        }
                                        target.addItem(it);
                                    }
                                }
                            });
                }

                target.setText(text);
            }
        };

        return openHandler;
    }

    /**
     * @return the closeHandler
     */
    private CloseHandler<TreeItem> getCloseHandler() {
        CloseHandler<TreeItem> closeHandler = new CloseHandler<TreeItem>() {

            public void onClose(CloseEvent<TreeItem> event) {
                GWT.log("closing item " + event.getTarget().getText());
            }
        };

        return closeHandler;

    }
}
