package org.gatein.management.server;

import org.exoplatform.container.ExoContainer;
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

    public static <T> T doInRequest(String containerName, ContainerCallback<T> containerCallback) throws Exception
    {
        //ExoContainer container = ExoContainerContext.getContainerByName(containerName);
        RootContainer rootContainer = RootContainer.getInstance();
        PortalContainer container = rootContainer.getPortalContainer(containerName);
        if ( container == null )
        {
            throw new RuntimeException("Invalid container name " + containerName);
        }
        ExoContainerContext.setCurrentContainer(container);
        RequestLifeCycle.begin(container, true);
        try
        {
            return containerCallback.doInContainer(container);
        }
        finally {
            try {
                RequestLifeCycle.end();
            }
            catch (IllegalStateException e){
                log.warn("Illegal state exception ending RequestLifeCycle", e);
            }
        }
    }
}
