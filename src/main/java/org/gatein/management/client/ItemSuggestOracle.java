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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * {@code ItemSuggestOracle}
 *
 * Created on Feb 10, 2011, 12:47:44 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 * @version 1.0
 */
public class ItemSuggestOracle extends SuggestOracle {

    /**
     * Create a new instance of {@code ItemSuggestOracle}
     */
    public ItemSuggestOracle() {
        super();
    }

    @Override
    public boolean isDisplayStringHTML() {
        return true;
    }

    /**
     * 
     * @param req
     * @param callback
     */
    public void requestSuggestions(SuggestOracle.Request req, SuggestOracle.Callback callback) {
        GateInServiceAsync gtnService = GWT.create(GateInService.class);
        gtnService.getUsername(getPortalContainerName(), req, new ItemSuggestCallback(req, callback));
    }

    /**
     * 
     * @return
     */
    public native String getPortalContainerName()/*-{
    return parent.eXo.env.portal.context.substring(1); // remove leading '/'
    }-*/;

    /**
     * 
     */
    static class ItemSuggestCallback implements AsyncCallback {

        private SuggestOracle.Request req;
        private SuggestOracle.Callback callback;

        public ItemSuggestCallback(SuggestOracle.Request _req,
                SuggestOracle.Callback _callback) {
            req = _req;
            callback = _callback;
        }

        public void onFailure(Throwable error) {
            callback.onSuggestionsReady(req, new SuggestOracle.Response());
        }

        public void onSuccess(Object retValue) {
            callback.onSuggestionsReady(req,
                    (SuggestOracle.Response) retValue);
        }
    }
}
