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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.List;

/**
 * {@code GateInServiceAsync}
 *
 * Created on Jan 3, 2011, 12:29:53 PM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public interface GateInServiceAsync {

    /**
     * Update the Tree item asynchronously
     *
     * @param item The item to be updated
     * @param asyncCallback
     */
    public void updateItem(TreeNode item, AsyncCallback<TreeNode> asyncCallback);

    /**
     * 
     * @param item
     * @param asyncCallback
     */
    public void updateNodeInfo(TreeNode item, AsyncCallback<String> asyncCallback);

    /**
     * 
     * @param asyncCallback
     */
    public void updateHeader(TreeItem item, AsyncCallback<String> asyncCallback);

    /**
     * 
     * @param asyncCallback
     */
    public void upload(AsyncCallback<String> asyncCallback);

    /**
     * 
     * @param asyncCallback
     */
    public void getRootNodes(AsyncCallback<List<TreeNode>> asyncCallback);

    /**
     * 
     * @param type
     * @param name
     * @param asyncCallback
     */
    public void exportSite(String type, String name, AsyncCallback<Void> asyncCallback);

}
