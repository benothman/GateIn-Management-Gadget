package org.gatein.management.server.filter;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.gatein.management.portalobjects.exportimport.api.ExportHandler;
import org.gatein.management.portalobjects.exportimport.api.ImportHandler;
import org.gatein.management.server.context.CustomContext;
import org.gatein.management.server.util.PortalService;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public class CustomContextFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // TODO: This always returns default container, figure out a way to get the correct container name
        PortalContainer container = PortalContainer.getInstance();
        CustomContextImpl context = new CustomContextImpl(container);
        DataStorage ds = context.getComponent(DataStorage.class);
        ExportHandler exportHandler = context.getComponent(ExportHandler.class);
        ImportHandler importHandler = context.getComponent(ImportHandler.class);
        context.setService(new PortalService(ds, exportHandler, importHandler));

        context.start();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            context.end();
        }
    }

    public void destroy() {
        if (CustomContext.getInstance() != null) {
            CustomContext.getInstance().end();
        }
    }

    private static class CustomContextImpl extends CustomContext {
        private ExoContainer container;
        private PortalService service;

        public CustomContextImpl(ExoContainer container) {
            this.container = container;
            setInstance(this);
        }

        @Override
        public ExoContainer getContainer() {
            return container;
        }

        @Override
        public PortalService getPortalService() {
            return service;
        }

        private void setService(PortalService service) {
            this.service = service;
        }

        @Override
        public void start() {
            RequestLifeCycle.begin(container, true);
            POMSessionManager mgr = getComponent(POMSessionManager.class);
            if (mgr.getSession() == null) {
                mgr.openSession();
            }
        }

        @Override
        public void end() {
            RequestLifeCycle.end();
            setInstance(null);
        }
    }
}
