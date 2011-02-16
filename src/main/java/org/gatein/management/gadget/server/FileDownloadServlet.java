/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.management.gadget.server;

import org.exoplatform.container.ExoContainer;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.management.gadget.server.util.PortalService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import static org.gatein.management.gadget.server.ContainerRequestHandler.doInRequest;

/**
 * {@code FileDownloadServlet}
 * <p>
 * The file download servlet. Used for export sites.
 * </p>
 * Created on Feb 3, 2011, 3:49:16 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 * @version 1.0
 */
public class FileDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(FileDownloadServlet.class);

    /**
     * Create a new instance of {@code FileDownloadServlet}
     */
    public FileDownloadServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String type = request.getParameter("ownerType");
        final String name = request.getParameter("ownerId");
        String portalContainerName = request.getParameter("pc");
        response.setContentType("application/octet-stream; charset=UTF-8");
        String filename = portalContainerName + "-" + type + "_" + name.replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_") + "_" + getTimestamp() + ".zip";
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");

        final OutputStream os = response.getOutputStream();
        try {
            doInRequest(portalContainerName, new ContainerCallback<Void>() {

                @Override
                public Void doInContainer(ExoContainer container) throws Exception {
                    PortalService service = PortalService.create(container);
                    service.exportSite(type, name, os);
                    return null;
                }
            });
            os.flush();
        } catch (Exception e) {
            log.error("Error during download", e);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @return a timestamp with format YYYYMMDDHHMM
     */
    private String getTimestamp() {
        StringBuilder sb = new StringBuilder("");

        Calendar calendar = Calendar.getInstance();
        sb.append(calendar.get(Calendar.YEAR));

        int month = calendar.get(Calendar.MONTH);
        sb.append(month < 10 ? "0" + month : month);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        sb.append(day < 10 ? "0" + day : day);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        sb.append(hour < 10 ? "0" + hour : hour);

        int minute = calendar.get(Calendar.MINUTE);
        sb.append(minute < 10 ? "0" + minute : minute);

        return sb.toString();
    }
}
