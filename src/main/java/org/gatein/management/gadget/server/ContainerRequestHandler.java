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

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public class ContainerRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ContainerRequestHandler.class);

    public static <T> T doInRequest(String containerName, ContainerCallback<T> containerCallback) throws Exception {
        //ExoContainer container = ExoContainerContext.getContainerByName(containerName);
        RootContainer rootContainer = RootContainer.getInstance();
        PortalContainer container = rootContainer.getPortalContainer(containerName);
        if (container == null) {
            throw new RuntimeException("Invalid container name " + containerName);
        }
        ExoContainerContext.setCurrentContainer(container);
        RequestLifeCycle.begin(container, true);
        try {
            return containerCallback.doInContainer(container);
        } finally {
            try {
                RequestLifeCycle.end();
            } catch (IllegalStateException e) {
                log.warn("Illegal state exception ending RequestLifeCycle", e);
            }
        }
    }
}
