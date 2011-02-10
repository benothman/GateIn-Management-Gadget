package org.gatein.management.server;

import org.exoplatform.container.ExoContainer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public abstract class ContainerCallback<T> {

    public abstract T doInContainer(ExoContainer container) throws Exception;

    public <C> C getComponent(ExoContainer container, Class<C> componentClass)
    {
        return componentClass.cast(container.getComponentInstanceOfType(componentClass));
    }
}
