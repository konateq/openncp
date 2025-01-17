/**
 * Copyright (c) 2009-2010 University of Cardiff and others
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * <p>
 * Contributors:
 * University of Cardiff - initial API and implementation
 * -
 */

package org.openhealthtools.openatna.syslog.core.test.tls;

import org.openhealthtools.openatna.syslog.SyslogException;
import org.openhealthtools.openatna.syslog.SyslogMessage;
import org.openhealthtools.openatna.syslog.core.test.tls.ssl.AuthSSLSocketFactory;
import org.openhealthtools.openatna.syslog.core.test.tls.ssl.KeystoreDetails;
import org.openhealthtools.openatna.syslog.transport.SyslogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Aug 18, 2009: 3:02:18 PM
 * @date $Date:$ modified by $Author:$
 */

public class TlsTestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlsTestServer.class);

    public static void main(String[] args) {

        URL u = Thread.currentThread().getContextClassLoader().getResource("keys/serverKeyStore");
        KeystoreDetails key = new KeystoreDetails(u.toString(), "serverStorePass", "myServerCert", "password");
        URL uu = Thread.currentThread().getContextClassLoader().getResource("keys/clientKeyStore");
        KeystoreDetails trust = new KeystoreDetails(uu.toString(), "clientStorePass", "myClientCert");
        try {
            AuthSSLSocketFactory f = new AuthSSLSocketFactory(key, trust);

            TlsConfig c = new TlsConfig();
            c.setSocketFactory(f);
            c.setHost("localhost");
            c.setRequireClientAuth(true);
            TlsServer server = new TlsServer();
            server.configure(c);
            server.addSyslogListener(new Listener());
            server.start();
        } catch (IOException e) {
            LOGGER.error("{}: '{}'", e.getClass(), e.getMessage(), e);
        }
    }

    static class Listener implements SyslogListener {

        public void messageArrived(SyslogMessage message) {
            LOGGER.info("serialized message: '{}'", message.toString());
            LOGGER.info("application message: '{}'", message.getMessage().getMessageObject());
        }

        public void exceptionThrown(SyslogException e) {
            LOGGER.error("{}: '{}'", e.getClass(), e.getMessage(), e);
        }
    }
}
