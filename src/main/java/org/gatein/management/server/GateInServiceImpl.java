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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.exoplatform.portal.config.model.PortalConfig;
import org.gatein.management.client.GateInService;
import org.gatein.management.client.TreeNode;
import org.gatein.management.server.util.PortalService;
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

    private static final Logger logger = Logger.getLogger(GateInService.class.getName());

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
        PortalService portalService = PortalService.getInstance();

        // TODO


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
     * @throws Exception
     */
    public List<TreeNode> getRootNodes() throws Exception {

        PortalService portalService = PortalService.getInstance();

        Collection<PortalConfig> portalSites = portalService.getPortalConfigs(PortalConfig.PORTAL_TYPE);
        Collection<PortalConfig> groupSites = portalService.getPortalConfigs(PortalConfig.GROUP_TYPE);
        Collection<PortalConfig> userSites = portalService.getPortalConfigs(PortalConfig.USER_TYPE);
        // create root nodes
        TreeNode portalNode = getRootNode(PortalConfig.PORTAL_TYPE, "Portal sites", portalSites);
        TreeNode groupNode = getRootNode(PortalConfig.GROUP_TYPE, "Group sites", groupSites);
        TreeNode userNode = getRootNode(PortalConfig.USER_TYPE, "User sites", userSites);
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        nodes.add(portalNode);
        nodes.add(groupNode);
        nodes.add(userNode);

        return nodes;
    }

    /**
     *
     * @param name
     * @param configs
     * @return
     */
    private TreeNode getRootNode(String type, String name, Collection<PortalConfig> configs) {

        TreeNode tn = new TreeNode(name);
        tn.setType(type);
        for (PortalConfig pc : configs) {
            TreeNode child = new TreeNode(pc.getName());
            child.setType(type);
            child.setExportable(true);
            child.setSiteName(pc.getName());
            StringBuilder sb = new StringBuilder("<ul>");
            sb.append("<li> Name : ").append(pc.getName()).append("</li>");
            sb.append("<li> Type : ").append(pc.getType()).append("</li>");
            sb.append("<li> Skin : ").append(pc.getSkin()).append("</li>");
            sb.append("<li> Edit permission : ").append(pc.getEditPermission()).append("</li>");
            sb.append("<li> Access permissions : <ul>");
            for (String s : pc.getAccessPermissions()) {
                sb.append("<li>").append(s).append("</li>");
            }
            sb.append("</ul></li></ul>");
            child.setNodeInfo(sb.toString());
            tn.addChild(child);
        }

        return tn;
    }

    /**
     * 
     * @param type
     * @param name
     */
    public void exportSite(String type, String name) {
        PortalService portalService = PortalService.getInstance();

        try {
            HttpServletResponse resp = getThreadLocalResponse();
            ServletOutputStream os = resp.getOutputStream();
            ServletContext context = getServletConfig().getServletContext();
            resp.setContentType("application/octet-stream");
            //resp.setContentLength((int) f.length());
            portalService.exportSite(type, name, os);

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error while exporting site : type = {0}, name = {1}, error message = {2}",
                    new String[]{type, name, ex.getMessage()});
            ex.printStackTrace();
        }
    }

    public void importSite(Site site) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
