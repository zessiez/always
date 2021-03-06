package edu.wpi.always.cm.perceptors.sensor.speech;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import edu.wpi.disco.rt.perceptor.*;

public abstract class JMSMessagePerceptor<T extends Perception> 
      extends PerceptorBase<T> implements
      MessageListener, Perceptor<T>, BufferablePerceptor<T>, AsyncPerceptor<T> {

   private ConnectionFactory connectionFactory;
   private Connection connection;
   private Session session;
   private Topic topic;
   private MessageConsumer consumer;

   public JMSMessagePerceptor (String url, String topicName) {
      try {
         connectionFactory = new ActiveMQConnectionFactory(url);
         connection = connectionFactory.createConnection();
         connection.start();
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         topic = session.createTopic(topicName);
         consumer = session.createConsumer(topic);
         consumer.setMessageListener(this);
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onMessage (Message message) {
      try {
         if ( message instanceof TextMessage ) {
            TextMessage textMessage = (TextMessage) message;
            latest = handleMessage(textMessage.getText());
            firePerceptorListeners(latest);
            bufferManager.pushPerception(latest);
         }
      } catch (JMSException e) {
         e.printStackTrace();
      }
   }

   public abstract T handleMessage (String content);

   private final PerceptorBufferManager<T> bufferManager = new PerceptorBufferManager<T>();

   @Override
   public PerceptorBuffer<T> newBuffer () {
      return bufferManager.newBuffer();
   }

   private final List<AsyncPerceptorListener<T>> listeners = new CopyOnWriteArrayList<AsyncPerceptorListener<T>>();

   @Override
   public void addPerceptorListener (AsyncPerceptorListener<T> listener) {
      listeners.add(listener);
   }

   @Override
   public void removePerceptorListener (AsyncPerceptorListener<T> listener) {
      listeners.remove(listener);
   }

   protected void firePerceptorListeners (T perception) {
      for (AsyncPerceptorListener<T> listener : listeners)
         listener.onPerception(perception);
   }
}
