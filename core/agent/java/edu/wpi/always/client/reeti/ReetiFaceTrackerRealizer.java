package edu.wpi.always.client.reeti;

import edu.wpi.always.client.ClientProxy;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;
import java.awt.Point;

enum Directions {
   xDIRECTION, yDIRECTION, bothDIRECTIONS
}

public class ReetiFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FacePerceptor perceptor;

   private final Directions trackingDirections = Directions.bothDIRECTIONS;

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 2000;

   private boolean searchFlag = false;

   private final ReetiPIDMessages reetiPIDOutput;

   private final ReetiCommandSocketConnection client;

   private String lastMessage = "";

   public ReetiFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, CollaborationManager cm, 
         ReetiJsonConfiguration config) {

      super(params);
      this.perceptor = perceptor;
      reetiPIDOutput = new ReetiPIDMessages(config);
      client = cm.getReetiSocket();
   }

   @Override
   public void run () {

      FacePerception perception = perceptor.getLatest();

      if ( perception != null ) {

         Point point = perception.getPoint();

         if ( point != null ) {

            // following is useful for debugging
            // java.awt.Toolkit.getDefaultToolkit().beep();
            currentTime = System.currentTimeMillis();
            send(reetiPIDOutput.Track(perception.getCenter(),
                                      perception.getTiltCenter(),
                                      trackingDirections));
            searchFlag = true;
            
         } else {
            
            currentLosingTime = System.currentTimeMillis();
            // waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if ( ((currentLosingTime - currentTime) > acceptableLosingTime) && searchFlag ) {
               send(reetiPIDOutput.faceSearch(false));
               searchFlag = false;
            }
         }
      }
   }
   
   private void send (String message) {
      // making sure that the PID controller has returned different
      // control command.
      if ( !lastMessage.equals(message) ) client.send(message);
      lastMessage = message;
   }
}