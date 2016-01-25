package com.vidmt.of.plugin.sub.pktfilter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.spi.ConnectionManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vidmt.of.plugin.AnnoPlugin;
import com.vidmt.of.plugin.utils.CommUtil;

/**
 * @author Administrator
 */
@AnnoPlugin
public class PktFilterPlugin extends IoFilterAdapter implements Plugin {
	private static final Logger log = LoggerFactory.getLogger(PktFilterPlugin.class);
	private Collection<IoSession> sessions = new ConcurrentLinkedQueue<IoSession>();

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		ConnectionManagerImpl connManager = (ConnectionManagerImpl) XMPPServer.getInstance().getConnectionManager();
		SocketAcceptor socketAcceptor = connManager.getSocketAcceptor();
		if (socketAcceptor != null) {
			socketAcceptor.getFilterChain().addBefore("xmpp", "rawDebugger", this);
		}
	}

	@Override
	public void destroyPlugin() {
		ConnectionManagerImpl connManager = (ConnectionManagerImpl) XMPPServer.getInstance().getConnectionManager();
		if (connManager.getSocketAcceptor() != null
				&& connManager.getSocketAcceptor().getFilterChain().contains("rawDebugger")) {
			connManager.getSocketAcceptor().getFilterChain().remove("rawDebugger");
		}
		this.shutdown();
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		if (log.isDebugEnabled()) {
			if (message instanceof IoBuffer) {
				IoBuffer byteBuffer = (IoBuffer) message;
				// Keep current position in the buffer
				int currentPos = byteBuffer.position();
				byte[] bytes = new byte[byteBuffer.limit()];
				byteBuffer.get(bytes);
				// byte[] bytes = byteBuffer.array();
				String msg = CommUtil.newString(bytes, "UTF-8");
				// Decode buffer
				// Charset encoder = Charset.forName("UTF-8");
				// Print buffer content
				log.debug("C2S - RECV (" + session.hashCode() + "): " + msg);
				// Reset to old position in the buffer
				byteBuffer.position(currentPos);
			}
		}
		// Pass the message to the next filter
		try {
			long start = System.currentTimeMillis();
			super.messageReceived(nextFilter, session, message);
			long end = System.currentTimeMillis();
			long timesec = (end - start) / 1000;
			if (timesec > 5) {
				log.warn("执行时间超过5秒,为{}秒：消息接收处理", timesec);
			} else {
				log.debug("执行时间{}秒:消息接收处理", timesec);
			}
		} catch (IOException e) {
			if ("Connection reset/closed by peer".equals(e.getMessage())) {
				log.warn("client closed");
			}
		}
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		if (log.isDebugEnabled()) {
			if (writeRequest.getMessage() instanceof IoBuffer) {
				IoBuffer iobuf = (IoBuffer) writeRequest.getMessage();
				// Keep current position in the buffer
				int currentPos = iobuf.position();
				iobuf.position(0);
				byte[] bytes = new byte[iobuf.limit()];
				iobuf.get(bytes);
				// byte[] bytes = byteBuffer.array();
				String msg = CommUtil.newString(bytes, "UTF-8");
				// Decode buffer
				// Charset encoder = Charset.forName("UTF-8");
				// Print buffer content
				log.debug("C2S - SENT (" + session.hashCode() + "): " + msg);
				// Reset to old position in the buffer
				iobuf.position(currentPos);
			}
		}

		// Pass the message to the next filter
		long start = System.currentTimeMillis();
		super.messageSent(nextFilter, session, writeRequest);
		long end = System.currentTimeMillis();
		long timesec = (end - start) / 1000;
		if (timesec > 5) {
			log.warn("执行时间超过5秒,为{}秒：消息发送处理", timesec);
		} else {
			log.debug("执行时间{}秒:消息发送处理", timesec);
		}
	}

	public void shutdown() {
		// Remove this filter from sessions that are using it
		for (IoSession session : sessions) {
			session.getFilterChain().remove("rawDebugger");
		}
		sessions = null;
	}

	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
		sessions.add(session);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
		sessions.remove(session);
		// Print that a session was closed
		System.out.println("CLOSED (" + session.hashCode() + ") ");

	}
}
