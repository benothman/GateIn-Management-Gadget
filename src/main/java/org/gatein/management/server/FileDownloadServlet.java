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

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gatein.management.server.util.PortalService;
import org.gatein.management.server.util.ProcessException;

/**
 * {@code FileDownloadServlet}
 *
 * Created on Feb 3, 2011, 3:49:16 PM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public class FileDownloadServlet extends HttpServlet {

    /**
     * Create a new instance of {@code FileDownloadServlet}
     */
    public FileDownloadServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String type = request.getParameter("ownerType");
        String name = request.getParameter("ownerId");
        System.out.println("ownerType : " + type + ", ownerId : " + name);
        response.setContentType("application/octet-stream");
        String filename = type + "_" + name + ".zip";
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");

        PortalService portalService = PortalService.getInstance();
        OutputStream os = response.getOutputStream();
        try {
            portalService.exportSite(type, name, os);
            os.flush();
            os.close();
        } catch (IOException exp) {
            throw exp;
        } catch (ProcessException ex) {
            Logger.getLogger(FileDownloadServlet.class.getName()).log(Level.SEVERE, "", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
