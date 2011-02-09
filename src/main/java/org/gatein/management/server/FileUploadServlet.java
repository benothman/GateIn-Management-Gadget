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

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.gatein.management.server.context.CustomContext;
import org.gatein.management.server.util.PortalService;
import org.gatein.management.server.util.ProcessException;

/**
 * {@code FileUploadServlet}
 *
 * Created on Jan 3, 2011, 3:43:36 PM
 *
 * @author Nabil Benothman
 * @version 1.0
 */
public class FileUploadServlet extends UploadAction {

    private static final long serialVersionUID = 1L;
    private Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
    /**
     * Maintain a list with received files and their content types.
     */
    private Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            super.doGet(request, response);
        } catch (Exception exp) {
            // used just for debug -> will be removed
            logger.error("doGet error -> " + exp.getMessage());
            exp.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            super.doPost(request, response);
        } catch (Exception exp) {
            // used just for debug -> will be removed 
            logger.error("doPost error -> " + exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * Override executeAction to save the received files in a custom place
     * and delete this items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        String response = "";
        int cont = 0;
        for (FileItem item : sessionFiles) {
            //if (false == item.isFormField()) {
            if (!item.isFormField()) {
                cont++;
                try {
                    /// Create a new file based on the remote file name in the client
                    String saveName = item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_");
                    /// Create a temporary file placed in the default system temp folder
                    File file = File.createTempFile(saveName, ".zip");
                    item.write(file);
                    /// Save a list with the received files
                    receivedFiles.put(item.getFieldName(), file);
                    receivedContentTypes.put(item.getFieldName(), item.getContentType());

                    // process the uploaded file
                    processImport(new FileInputStream(file));
                    /// Compose a xml message with the full file information which can be parsed in client side
                    response += "<file-" + cont + "-field>" + item.getFieldName() + "</file-" + cont + "-field>\n";
                    response += "<file-" + cont + "-name>" + item.getName() + "</file-" + cont + "-name>\n";
                    response += "<file-" + cont + "-size>" + item.getSize() + "</file-" + cont + "-size>\n";
                    response += "<file-" + cont + "-type>" + item.getContentType() + "</file-" + cont + "type>\n";
                } catch (ProcessException e) {
                    throw new UploadActionException(e);
                } catch (Exception e) {
                    throw new UploadActionException(e);
                }
            }
        }

        /// Remove files from session because we have a copy of them
        removeSessionFileItems(request);

        /// Send information of the received files to the client.
        return "<response>\n" + response + "</response>\n";
    }

    /**
     * Get the content of an uploaded file.
     */
    @Override
    public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fieldName = request.getParameter(PARAM_SHOW);
        File f = receivedFiles.get(fieldName);
        if (f != null) {
            response.setContentType(receivedContentTypes.get(fieldName));
            FileInputStream is = new FileInputStream(f);
            copyFromInputStreamToOutputStream(is, response.getOutputStream());
        } else {
            renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
        }
    }

    /**
     * Remove a file when the user sends a delete request.
     */
    @Override
    public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
        File file = receivedFiles.get(fieldName);
        receivedFiles.remove(fieldName);
        receivedContentTypes.remove(fieldName);
        if (file != null) {
            file.delete();
        }
    }

    /**
     * Try to import the site from the zip file opened by the given input stream
     *
     * @param in the input stream pointing to the zip file
     * @throws Exception 
     */
    private void processImport(InputStream in) throws Exception {
        try {
            PortalService portalService = CustomContext.getInstance().getPortalService();
            portalService.importSite(in);
        } catch (Exception ex) {
            logger.error("process import error -> " + ex.getMessage());
            throw new ProcessException("Import process failed", ex);
        }
    }
}
