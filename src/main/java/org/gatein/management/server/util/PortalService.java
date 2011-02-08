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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.gatein.management.portalobjects.exportimport.api.ExportContext;
import org.gatein.management.portalobjects.exportimport.api.ExportHandler;
import org.gatein.management.portalobjects.exportimport.api.ImportContext;
import org.gatein.management.portalobjects.exportimport.api.ImportHandler;
import org.gatein.mop.api.workspace.Site;

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
    private DataStorage dataStorage;
    private ExportHandler exportHandler;
    private ImportHandler importHandler;

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
        //this.container = ExoContainerContext.getCurrentContainer();
        this.container = PortalContainer.getInstance();
        this.checkSession();
        this.dataStorage = (DataStorage) container.getComponentInstanceOfType(DataStorage.class);
        this.exportHandler = (ExportHandler) container.getComponentInstanceOfType(ExportHandler.class);
        this.importHandler = (ImportHandler) container.getComponentInstanceOfType(ImportHandler.class);

    }

    /**
     * @return the instance
     */
    public static PortalService getInstance() {
        return instance.get();
    }

    /**
     * 
     */
    public static void remove() {
        instance.remove();
    }


    /**
     * Retrieve the {@code PortalConfig} having the given type
     *
     * @param type The type of {@code PortalConfig} (ownerType)
     * @return a collection of {@code PortalConfig}
     */
    public List<PortalConfig> getPortalConfigs(String type) {
        return getPortalConfigs(type, null);
    }

    /**
     * Retrieve the list of {@code PortalConfig} having the given type and name
     *
     * @param type The portal type (ownerType)
     * @param name The site name (ownerId)
     * @return a list of {@code PortalConfig}
     */
    public List<PortalConfig> getPortalConfigs(String type, String name) {

        try {
            List<Page> pages = getPages(type, name);
            return getPortalConfigs(pages);
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Problem occurs when retrieving the list of sites for type {0} and name {1} -> {2}",
                    new String[]{type, name, exp.getMessage()});
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Retrieve the list of {@code PortalConfig} given their pages
     * 
     * @param pages the list of pages of a portal
     * @return a list of {@code PortalConfig}
     */
    public List<PortalConfig> getPortalConfigs(List<Page> pages) {
        Map<String, PortalConfig> pConfigs = new HashMap<String, PortalConfig>();
        try {
            PortalConfig pc = null;
            String key = null;
            for (Page page : pages) {
                pc = dataStorage.getPortalConfig(page.getOwnerType(), page.getOwnerId());
                key = page.getOwnerType() + "::" + page.getOwnerId();
                if (pConfigs.get(key) == null && pc != null) {
                    pConfigs.put(key, pc);
                }
            }
            return new ArrayList<PortalConfig>(pConfigs.values());
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Problem occurs when retrieving the list of sites : {0}", exp.getMessage());
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Retrieve pages having the given portal type
     *
     * @param type the portal type (ownerType)
     * @return a collection of {@code Page}
     */
    public List<Page> getPages(String type) {
        return getPages(type, null);
    }

    /**
     * Retrieve pages having the given portal type and site name
     *
     * @param type the portal type (ownerType)
     * @param name the site name (ownerId)
     * @return a collection of {@code Page}
     */
    public List<Page> getPages(String type, String name) {
        this.checkSession();
        try {
            Query<Page> query = new Query<Page>(type, name, Page.class);
            LazyPageList<Page> results = dataStorage.find(query);
            return results.getAll();
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Retrieving the list of pages for type: {0} -> Exception " + exp.getMessage(), type + ", name : " + name);
            exp.printStackTrace();
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Retrieve page navigations having the given portal type
     *
     * @param type the portal type (ownerType)
     * @return a collection of {@code PageNavigation}
     */
    public List<PageNavigation> getPageNavigations(String type) {
        return getPageNavigations(type, null);
    }

    /**
     * Retrieve page navigations having the given portal type and site name
     *
     * @param type the portal type (ownerType)
     * @param name the site name (ownerId)
     * @return a collection of {@code PageNavigation}
     */
    public List<PageNavigation> getPageNavigations(String type, String name) {
        this.checkSession();
        try {
            Query<PageNavigation> query = new Query<PageNavigation>(type, name, PageNavigation.class);
            LazyPageList<PageNavigation> results = dataStorage.find(query);
            return results.getAll();
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Retrieving the list of page navigations for type: {0} -> Exception " + exp.getMessage(), type + ", name : " + name);
            exp.printStackTrace();
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Retrieve the site having the given type and name
     *
     * @param type The site type (ownerType)
     * @param name The site name (ownerId)
     * @return the site having the given name and type
     */
    public Site getSiteByName(String type, String name) {
        try {
            Query<Site> query = new Query<Site>(type, name, Site.class);
            LazyPageList<Site> results = dataStorage.find(query);
            return results.getAll().isEmpty() ? null : results.getAll().get(0);
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Retrieving the list of sites for type {0} -> Exception " + exp.getMessage(), type);
        }

        return null;
    }

    /**
     * Export the site given by it type and name
     *
     * @param type the portal type (ownerType)
     * @param name the site name (ownerId)
     * @param os the output stream in what the export file will be written
     * @throws IOException
     */
    public void exportSite(String type, String name, OutputStream os) throws IOException, ProcessException {
        // check whether the session is null or not
        this.checkSession();
        ExportContext context = exportHandler.createExportContext();

        List<Page> pages = getPages(type, name);
        List<PortalConfig> portalConfigs = getPortalConfigs(pages);
        if (portalConfigs.isEmpty()) {
            throw new ProcessException("No entry with type : " + type + " and name : " + name);
        }

        List<PageNavigation> pageNavigations = getPageNavigations(type, name);

        // Add portal configs to the context
        for (PortalConfig pc : portalConfigs) {
            context.addToContext(pc);
        }
        // // Add pages to the context
        context.addToContext(pages);
        //for (List<Page> pages : pagesForExport) {
        //    context.addToContext(pages);
        //}
        // // Add page navigations to the context
        for (PageNavigation navigation : pageNavigations) {
            context.addToContext(navigation);
        }

        // export the site
        this.exportHandler.exportContext(context, os);

    }

    /**
     * Import the site to the portal. The site is given by file opened with the
     * {@code java.io.InputStream}.
     *
     * @param in the input stream pointing to the file containing the data of the
     * site to import to the portal.
     */
    public void importSite(InputStream in) throws Exception {
        this.checkSession();
        ImportContext context = importHandler.createContext(in);
        this.importHandler.importContext(context);
    }

    /**
     * Check whether the current session is null or not. If there is no session
     * already opened, a new session will be opened.
     */
    protected void checkSession() {
        POMSessionManager mgr = (POMSessionManager) this.container.getComponentInstanceOfType(POMSessionManager.class);
        if (mgr.getSession() == null) {
            mgr.openSession();
        }
    }
}
