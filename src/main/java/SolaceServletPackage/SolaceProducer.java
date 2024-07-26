package SolaceServletPackage;

import javax.jms.Connection;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class SolaceProducer {
	private static final String SOLACE_QUEUE_NAME = "QUEUE_NAME"; 
	private static final String SOLACE_CONNECTION_FACTORY = "CONNECTION_FACTORY_NAME"; 
	private InitialContext initContext = null;

	public void sentToSolace(Connection conn, Session session,String message) throws JMSException {
		Queue solaceQ = null;
		MessageProducer messageProducer = null;
		TextMessage textMessage = null;
		

		try {
		setInitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) initContext
				.lookup(SOLACE_CONNECTION_FACTORY);
		conn =  connectionFactory.createConnection();
		session = conn.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
		solaceQ = (Queue) initContext.lookup(SOLACE_QUEUE_NAME);
		messageProducer = session.createProducer(solaceQ);
		textMessage = session.createTextMessage(message);

		messageProducer.send(solaceQ, textMessage, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
				Message.DEFAULT_TIME_TO_LIVE);

		}catch(Exception e) {
			System.out.println("Failure while writing to solace Queue : " + solaceQ);

		}finally {
			if (messageProducer != null)
				messageProducer.close();
			if (session != null)
				session.close();
			if (conn != null)
				conn.close();
		}

	}
	
	public void setInitialContext() throws Exception {
		try {
			if (initContext == null) {
				initContext = new InitialContext();
			}
		} catch (Exception e) {
			System.out.println("Exception writing message to queue");
			throw e;
		}

	}
}
