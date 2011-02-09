package org.gatein.management.server.context;

import org.exoplatform.container.ExoContainer;
import org.gatein.management.server.util.PortalService;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public abstract class CustomContext {

    private static ThreadLocal<CustomContext> instance = new ThreadLocal<CustomContext>() {
        protected CustomContext initialValue() {
            return null;
        }
    };

    public static CustomContext getInstance() {
        return instance.get();
    }

    protected static void setInstance(CustomContext context) {
        if (context == null) {
            instance.remove();
        } else {
            instance.set(context);
        }
    }

    public abstract ExoContainer getContainer();

    public abstract void start();

    public abstract void end();

    public <T> T getComponent(Class<T> componentClass) {
        return componentClass.cast(getContainer().getComponentInstanceOfType(componentClass));
    }

    public abstract PortalService getPortalService();
}
