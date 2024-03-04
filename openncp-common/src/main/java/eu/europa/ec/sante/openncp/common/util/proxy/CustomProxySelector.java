package eu.europa.ec.sante.openncp.common.util.proxy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomProxySelector extends ProxySelector {

    private final ProxySelector defaultSelector;
    private Logger logger = LoggerFactory.getLogger(CustomProxySelector.class);
    private HashMap<SocketAddress, InnerProxy> proxies = new HashMap<>();

    public CustomProxySelector(ProxySelector def, ProxyCredentials credentials) {

        this.defaultSelector = def;
        Authenticator.setDefault(new ProxyAuthenticator(credentials.getUsername(), credentials.getPassword()));
        InnerProxy i = new InnerProxy(new InetSocketAddress(credentials.getHostname(), Integer.parseInt(credentials.getPort())));
        proxies.put(i.address(), i);
    }

    @Override
    public List<Proxy> select(URI uri) {

        if (logger.isDebugEnabled()) {
            logger.debug("select for URL : " + uri);
        }

        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }

        String protocol = uri.getScheme();
        // Get protocol and manage Http or Https proxy configuration.
        if (StringUtils.equalsIgnoreCase("http", protocol) || StringUtils.equalsIgnoreCase("https", protocol)) {

            List<Proxy> proxyList = new ArrayList<>();
            for (InnerProxy p : proxies.values()) {
                proxyList.add(p.toProxy());
            }
            // Return configured Proxy
            return proxyList;
        }

        /*
         * For others protocols (could be SOCKS or FTP etc.) return the default selector.
         */
        if (defaultSelector != null) {
            return defaultSelector.select(uri);
        } else {
            List<Proxy> proxyList = new ArrayList<>();
            proxyList.add(Proxy.NO_PROXY);
            return proxyList;
        }
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

        if (uri == null || sa == null || ioe == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }

        InnerProxy p = proxies.get(sa);
        if (p != null) {

            if (p.failed() >= 3) {
                proxies.remove(sa);
            }
        } else {

            if (defaultSelector != null) {
                defaultSelector.connectFailed(uri, sa, ioe);
            }
        }
    }

    /*
     * Inner class representing a Proxy.
     */
    class InnerProxy {

        Proxy proxy;
        SocketAddress addr;
        int failedCount = 0;

        InnerProxy(InetSocketAddress a) {
            addr = a;
            proxy = new Proxy(Proxy.Type.HTTP, a);
        }

        SocketAddress address() {
            return addr;
        }

        Proxy toProxy() {
            return proxy;
        }

        int failed() {
            return ++failedCount;
        }
    }
}
