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

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import java.io.Serializable;

/**
 * {@code GateInTreeItem}
 *
 * Created on Jan 4, 2011, 2:13:04 PM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public class GateInTreeItem extends TreeItem implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8455720414654394258L;

    /**
     * Create a new instance of {@code GateInTreeItem}
     */
    public GateInTreeItem() {
        super();
    }

    /**
     * Constructs a tree item with the given HTML.
     *
     * @param html the item's HTML
     */
    public GateInTreeItem(String html) {
        super(html);
    }

    /**
     * Constructs a tree item with the given HTML.
     *
     * @param html the item's HTML
     */
    public GateInTreeItem(SafeHtml html) {
        super(html);
    }

    /**
     * Constructs a tree item with the given <code>Widget</code>.
     *
     * @param widget the item's widget
     */
    public GateInTreeItem(Widget widget) {
        super(widget);
    }
}
