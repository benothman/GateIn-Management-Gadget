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
package org.gatein.management.server;

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.gatein.management.client.GateInService;
import org.gatein.management.client.TreeNode;
import org.gatein.management.server.util.PortalService;
import org.gatein.mop.api.workspace.Navigation;
import org.gatein.mop.api.workspace.ObjectType;
import org.gatein.mop.api.workspace.Page;
import org.gatein.mop.api.workspace.Site;

/**
 * {@code GateInServiceImpl}
 *
 * Created on Jan 3, 2011, 12:30:45 PM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public class GateInServiceImpl extends RemoteServiceServlet implements GateInService {

    private static final String UPLOAD_DIRECTORY = "/tmp/";
    private static final Logger logger = Logger.getLogger(GateInService.class.getName());
    public static final int PORTAL_SITES_INDEX = 0;
    public static final int GROUP_SITES_INDEX = 1;
    public static final int USER_SITES_INDEX = 2;

    /**
     * Create a new instance of {@code GateInServiceImpl}
     */
    public GateInServiceImpl() {
        super();
    }

    /**
     * Update the Tree item asynchronously
     *
     * @param tn The item to be updated
     */
    public TreeNode updateItem(TreeNode tn) {
        String name = tn.getText();
        ObjectType<Site> type = getSiteType(tn.getType());
        PortalService portalService = PortalService.getInstance();
        



        return tn;
    }

    /**
     * 
     * @param item
     * @throws Exception
     */
    public String updateNodeInfo(TreeNode tn) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * 
     * @param item
     * @return
     * @throws Exception
     */
    public String updateHeader(TreeItem item) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @return
     */
    public String upload() throws Exception {
        HttpServletRequest request = getThreadLocalRequest();

        // process only multipart requests
        if (ServletFileUpload.isMultipartContent(request)) {

            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            try {
                List<FileItem> items = upload.parseRequest(request);
                for (FileItem item : items) {
                    // process only file upload - discard other form item types
                    if (item.isFormField()) {
                        continue;
                    }

                    String fileName = item.getName();
                    // get only the file name not whole path
                    if (fileName != null) {
                        fileName = FilenameUtils.getName(fileName);
                    }

                    File uploadedFile = new File(UPLOAD_DIRECTORY, fileName);
                    if (uploadedFile.createNewFile()) {
                        item.write(uploadedFile);
                    } else {
                        throw new IOException("The file already exists in repository.");
                    }
                }
            } catch (Exception e) {
                return "An error occurred while creating the file : " + e.getMessage();
            }

        } else {
            return "Request contents type is not supported by the servlet.";
        }

        return "The file has been uploaded with success";
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public List<TreeNode> getRootNodes() throws Exception {

        PortalService portalService = PortalService.getInstance();

        Collection<Site> portalSites = portalService.getSites(ObjectType.PORTAL_SITE);
        Collection<Site> groupSites = portalService.getSites(ObjectType.GROUP_SITE);
        //Collection<Site> userSites = portalService.getSites(ObjectType.USER_SITE);
        // create root nodes
        TreeNode portalNode = getRootNode("Portal sites", PORTAL_SITES_INDEX, portalSites);
        TreeNode groupNode = getRootNode("Group sites", GROUP_SITES_INDEX, groupSites);
        //TreeNode userNode = getRootNode("User sites", USER_SITES_INDEX, userSites);
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        nodes.add(portalNode);
        nodes.add(groupNode);
        //nodes.add(userNode);

        return nodes;
    }

    /**
     *
     * @param name
     * @param sites
     * @return
     */
    private TreeNode getRootNode(String name, int index, Collection<Site> sites) {

        TreeNode tn = new TreeNode(name);
        tn.setType(index);
        for (Site site : sites) {
            TreeNode child = new TreeNode(site.getName());
            child.setType(index);
            String info = "<ul>";
            Page rootPage = site.getRootPage();
            Navigation navigation = site.getRootNavigation();
            info += "<li> Root page : " + rootPage.getName() + "</li>";
            info += "<li> Root navigation : " + navigation.getName() + "</li>";
            child.setNodeInfo(info + "</ul>");
            tn.addChild(child);
        }

        return tn;
    }

    /**
     * 
     * @param index
     * @return
     */
    private ObjectType<Site> getSiteType(int index) {
        switch (index) {
            case PORTAL_SITES_INDEX:
                return ObjectType.PORTAL_SITE;
            case GROUP_SITES_INDEX:
                return ObjectType.GROUP_SITE;
            case USER_SITES_INDEX:
                return ObjectType.USER_SITE;
            default:
                return null;
        }
    }

    /**
     * 
     * @param type
     * @return
     */
    private int getSiteTypeIndex(ObjectType<Site> type) {
        if (type == ObjectType.PORTAL_SITE) {
            return PORTAL_SITES_INDEX;
        }
        if (type == ObjectType.GROUP_SITE) {
            return GROUP_SITES_INDEX;
        }
        if (type == ObjectType.USER_SITE) {
            return USER_SITES_INDEX;
        }

        return 0;
    }
}
