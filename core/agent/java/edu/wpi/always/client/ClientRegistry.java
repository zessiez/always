package edu.wpi.always.client;

import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.reeti.*;
import edu.wpi.always.cm.primitives.AudioFileRealizer;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class ClientRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      // NB: Realizers should *not* be cached, other components should
      container.as(Characteristics.CACHE).addComponent(new UIMessageDispatcherImpl(new TcpConnection(
            "localhost", 11000)));
      UIMessageDispatcher dispatcher = container.getComponent(UIMessageDispatcher.class);
      dispatcher.registerReceiveHandler(
            SkypeInterruptHandler.SKYPE_MESSAGE, 
            new SkypeInterruptHandler(dispatcher, container.getComponent(PeopleManager.class)));
      dispatcher.registerReceiveHandler(
              SkypeUserHandler.USER_MESSAGE, 
              new SkypeUserHandler());
      container.addComponent(GazeRealizer.class);
      container.addComponent(FaceExpressionRealizer.class);
      container.addComponent(IdleBehaviorRealizer.class);
      switch ( Always.getAgentType() ) {
         case UNITY:
            container.addComponent(FaceTrackerRealizer.class);
            break;
         case REETI:
         // note we cannot have virtual agent also tracking face
         // in this mode because it uses the gaze commands, which
         // Reeti will also see
         case MIRROR:
            container.addComponent(ReetiFaceTrackerRealizer.class);
            container.as(Characteristics.CACHE).addComponent(ReetiJsonConfiguration.class);
            break;
      }
      container.addComponent(SpeechRealizer.class);
      container.addComponent(AudioFileRealizer.class);
      container.addComponent(ClientMenuRealizer.class);
      container.as(Characteristics.CACHE).addComponent(ClientMenuPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(KeyboardMessageHandler.class);
      container.as(Characteristics.CACHE).addComponent(ClientProxy.class);
   }
}
