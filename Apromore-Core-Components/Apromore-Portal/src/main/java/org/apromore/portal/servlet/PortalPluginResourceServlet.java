package org.apromore.portal.servlet;

import com.google.common.io.ByteStreams;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apromore.plugin.portal.PortalPlugin;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.util.Locales;

/**
 * Serves resources with <code>.png</code> or <code>.svg</code> extensions
 * from {@link PortalPlugin} bundles.
 *
 * The required path format is <code>portalPluginResource/<var>groupLabel</var>/<var>label</var>/<var>path</var>.<var>extension</var></code>
 * where:
 * <ul>
 * <li><var>groupLabel</var> and <var>label</var> identify the {@link PortalPlugin}</li>
 * <li><var>path</var>.<var>extension</var> names a resource within the {@link PortalPlugin} bundle</li>
 * <li><var>extension</var> is either <code>png</code> or <code>svg</code></li>
 * </ul>
 *
 * The path subfields are URL encoded.  Particularly, this is required to allow the path separator character '/' to occur.  It means that '+' will be treated as space, however.
 *
 * Only GET requests are supported.  The content encoding will match the extension (PNG or SVG).
 */
public class PortalPluginResourceServlet extends HttpServlet {

    private Map<String, String> contentTypeMap = new HashMap<>();

    @Override
    public void init() {
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("svg", "image/svg+xml");
    }

    @Override
    public void destroy() {
        contentTypeMap.clear();
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        //log("Pathinfo " + req.getPathInfo());
        Pattern p = Pattern.compile("/(?<groupLabel>[^/]+)/(?<label>[^/]+)/(?<resource>[^\\.]*\\.(?<extension>[^/\\.]*))");
        Matcher m = p.matcher(req.getPathInfo());
        if (m.find()) {
            String groupLabel = URLDecoder.decode(m.group("groupLabel"), "utf-8");
            String label = URLDecoder.decode(m.group("label"), "utf-8");
            String resource = URLDecoder.decode(m.group("resource"), "utf-8");
            String extension = URLDecoder.decode(m.group("extension"), "utf-8");

            //log("Group label " + groupLabel);
            //log("Label " + label);
            //log("Resource " + resource);
            //log("Extension " + extension);

            AutowireCapableBeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getAutowireCapableBeanFactory();
            
            for (PortalPlugin portalPlugin: (List<PortalPlugin>) beanFactory.getBean("portalPlugins")){
                if (groupLabel.equals(portalPlugin.getGroupLabel(Locales.getCurrent())) &&
                    label.equals(portalPlugin.getLabel(Locales.getCurrent()))) {

                    //log("Portal plugin " + portalPlugin);
                    
                    try (InputStream in = portalPlugin.getResourceAsStream(resource)) {
                        if (in == null) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to find resource for " + req.getPathInfo());
                            return;
                        }

                        String contentType = contentTypeMap.get(extension.toLowerCase());
                        if (contentType == null) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unsupported resource extension \"" + extension + "\" for " + req.getPathInfo());
                            return;
                        }

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType(contentType);
                        try (OutputStream out = resp.getOutputStream()) {
                            ByteStreams.copy(in, out);
                        }
                    }
                    return;
                }
            }

            // Fall through
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to find portal plugin for " + req.getPathInfo());

        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to parse path " + req.getPathInfo());
        }
    }
}