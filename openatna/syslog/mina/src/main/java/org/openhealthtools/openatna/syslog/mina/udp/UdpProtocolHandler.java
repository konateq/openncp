/**
 * Copyright (c) 2009-2011 University of Cardiff and others
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
package org.openhealthtools.openatna.syslog.mina.udp;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.openhealthtools.openatna.syslog.SyslogException;
import org.openhealthtools.openatna.syslog.SyslogMessage;
import org.openhealthtools.openatna.syslog.SyslogMessageFactory;
import org.openhealthtools.openatna.syslog.mina.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Aug 19, 2009: 2:43:15 PM
 * @date $Date:$ modified by $Author:$
 */
public class UdpProtocolHandler extends IoHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger("org.openhealthtools.openatna.syslog.mina.udp.UdpProtocolHandler");

    private Notifier server;
    private int mtu;

    public UdpProtocolHandler(Notifier server, int mtu) {
        this.server = server;
        this.mtu = mtu;
    }

    @Override
    public void sessionCreated(IoSession session) {
        log.info("Enter");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        log.info("Enter");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error("exceptionCaught: '{}'", cause.getMessage(), cause);
        session.close();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        log.info("Enter");

        if (!(message instanceof ByteBuffer)) {
            return;
        }
        ByteBuffer buff = (ByteBuffer) message;
        if (buff.limit() > mtu) {
            log.info("message is too long: '{}'. It exceeds config MTU of '{}'", buff.limit(), mtu);
            SyslogException e = new SyslogException("Packet exceeds MTU of " + mtu);
            e.setSourceIp(((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress());
            buff.rewind();
            byte[] bytes = new byte[buff.limit()];
            buff.get(bytes);
            e.setBytes(bytes);
            session.close();
            server.notifyException(e);
            return;
        }
        try {
            InputStream in = buff.asInputStream();
            SyslogMessageFactory factory = SyslogMessageFactory.getFactory();
            SyslogMessage msg = factory.read(in);
            msg.setSourceIp(((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress());
            server.notifyMessage(msg);
        } catch (SyslogException e) {
            e.setSourceIp(((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress());
            buff.rewind();
            byte[] bytes = new byte[buff.limit()];
            buff.get(bytes);
            e.setBytes(bytes);
            session.close();
            server.notifyException(e);
        }
    }
}
