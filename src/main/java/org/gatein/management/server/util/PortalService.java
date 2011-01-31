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
package org.gatein.management.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.pom.config.POMSession;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.gatein.mop.api.workspace.ObjectType;
import org.gatein.mop.api.workspace.Site;
import org.gatein.mop.api.workspace.Workspace;

/**
 * {@code PortalService}
 *
 * Created on Jan 5, 2011, 9:14:19 AM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public final class PortalService {

    private static final Logger logger = Logger.getLogger(PortalService.class.getName());
    private static final ThreadLocal<PortalService> instance = new ThreadLocal<PortalService>() {

        @Override
        protected PortalService initialValue() {
            return new PortalService();
        }
    };
    private ExoContainer container;
    private POMSessionManager mgr;
    private POMSession pomSession;

    /**
     * Create a new instance of {@code PortalService}
     */
    private PortalService() {
        super();
        this.initService();
    }

    /**
     * Initialize the service
     *
     * @throws Exception
     */
    protected void initService() {
        this.container = ExoContainerContext.getContainerByName("portal");
        this.mgr = (POMSessionManager) container.getComponentInstanceOfType(POMSessionManager.class);
        //POMSessionManager mgr = (POMSessionManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(POMSessionManager.class);
        this.pomSession = mgr.getSession() != null ? mgr.getSession() : mgr.openSession();
    }

    /**
     * @return the instance
     */
    public static PortalService getInstance() {
        return instance.get();
    }

    /**
     * Retrieve the list of sites corresponding to the given type
     *
     * @param type The sites type
     * @return The list of sites corresponding to the given type
     */
    public Collection<Site> getSites(ObjectType<Site> type) {
        try {
            Workspace workspace = this.pomSession.getWorkspace();
            Collection<Site> sites = workspace.getSites(type);
            return sites;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Retrieving the list of sites for type {0} -> Exception " + exp.getMessage(), type.toString());
        }

        return new ArrayList<Site>();
    }

    /**
     * Retrieve the site having the given type and type
     *
     * @param type The site type
     * @param name The site name
     * @return the site having the given name and type
     */
    public Site getSiteByName(ObjectType<Site> type, String name) {
        try {
            Workspace workspace = this.pomSession.getWorkspace();
            Site site = workspace.getSite(type, name);
            return site;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Retrieving the list of sites for type {0} -> Exception " + exp.getMessage(), type.toString());
        }

        return null;
    }
}
