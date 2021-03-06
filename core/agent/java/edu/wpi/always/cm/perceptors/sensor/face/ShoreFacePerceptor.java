package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;
import edu.wpi.disco.rt.perceptor.PerceptorBase;
import edu.wpi.disco.rt.util.NullArgumentException;
import edu.wpi.disco.rt.util.Utils;

public abstract class ShoreFacePerceptor extends PerceptorBase<FacePerception>
                      implements FacePerceptor {

   private long previousTime = 0;

   private final static long timeUnit = 220;

   private int faceCode;
   
   final protected FaceInfo getFaceInfo (int debug) {
      FaceInfo info = getFaceInfoCPP(debug);
      int code = info.isCode() ? info.getCode() : 0;
      if ( code != faceCode ) {
         // don't print code changes between -1 and 0 (lost face)
         if ( !((code == 0 && faceCode == -1) || (code == -1 && faceCode == 0)) )
            Utils.lnprint(System.out, "ShoreFacePerceptor.getFaceInfo() return code: "+code);
         faceCode = code;
      }
      return info;
   }
   
   protected FaceInfo getFaceInfoCPP (int debug) {
      return null;
   }

   protected FaceInfo info, prevInfo;

   private final long faceHorizontalDisplacementThreshold,
         faceVerticalDisplacementThreshold, faceAreaThreshold;

   protected ShoreFacePerceptor (int horz, int vert, int area, Object start) {
      faceHorizontalDisplacementThreshold = horz;
      faceVerticalDisplacementThreshold = vert;
      faceAreaThreshold = area;
      start(start);
   }
   
   // accessed by both schema and realizer threads
   protected volatile boolean running;

   /*
    * DESIGN NOTE: The logic below is tricky because it needs to be robust wrt
    * to both losing the face, isFace(), and also jumping to a non-real face,
    * isRealFace(). In the former case, latest should be set to null (for
    * efficiency in long-running with no face), whereas in the latter case,
    * latest should not change. However, in both cases, the value of
    * previousInfo should contain the most recently seen real face for eventual
    * proportional comparison. Note that if you don't see any real face for a long
    * time, then the proportional comparison is guaranteed to succeed because
    * the timeDifference has gotten huge.
    */

   @Override
   public void run () {
      if ( !running ) return;
      info = getFaceInfo(0);
      if ( info != null && info.isFace() ) {
         Long currentTime = System.currentTimeMillis();
         // cannot reject based on proportionality if no previous real face
         if ( prevInfo == null || isRealFace(currentTime - previousTime) ) {
            latest = new FacePerception(info.intTop,
                  info.intBottom, info.intLeft, info.intRight, info.intArea,
                  info.intCenter, info.intTiltCenter);
            prevInfo = info;
            previousTime = currentTime;
         } 
      } else latest = null;
   }

   private boolean isRealFace (long timeDifference) {
      return isProportionalPosition(timeDifference) && isProportionalArea(timeDifference);
   }

   private boolean isProportionalPosition (long timeDifference) {
      return ( Math.abs((long) (info.intLeft - prevInfo.intLeft)) / timeDifference
               <= faceHorizontalDisplacementThreshold / timeUnit ) && 
             ( Math.abs((long) (info.intTop - prevInfo.intTop)) / timeDifference
               <= faceVerticalDisplacementThreshold / timeUnit );
   }

   private boolean isProportionalArea (long timeDifference) {
      return Math.abs((long) (info.intArea - prevInfo.intArea)) / timeDifference
             <= faceAreaThreshold / timeUnit;
   }

   public synchronized void start (Object start) { // called on schema thread
      if ( !running ) {
         running = startEngine(start);
         if ( !running ) {
            latest = null;
            stopEngine();
         }
      }
   }

   @Override
   public synchronized void start () { start(null); }

   @Override
   public synchronized void stop () { // called on schema thread
      if ( running ) {
         latest = null;
         running = false; // before terminate
         stopEngine();
      }
   }

   private int startCode;
   
   /**
    * @return true iff running
    */
   final protected boolean startEngine (Object start) {
      int code = initEngine(start, 0);
      if ( code != startCode ) {
         Utils.lnprint(System.out, "ShoreFacePerceptor.startEngine() return code: " +code);
         startCode = code;
      }
      return code == 0;
   }
   
   abstract protected int initEngine (Object start, int debug);
   
   abstract protected void stopEngine (); 

   public static class Agent extends ShoreFacePerceptor {

      public Agent () { super(50, 50, 1700, null); }

      @Override
      protected int initEngine (Object start, int debug) {
         return CPPinterface.INSTANCE.initAgentShoreEngine(debug);
      }

      @Override
      protected void stopEngine () {
         CPPinterface.INSTANCE.terminateAgentShoreEngine(0);
      }

      @Override
      protected FaceInfo getFaceInfoCPP (int debug) {
         return CPPinterface.INSTANCE.getAgentFaceInfo(debug);
      }
   }

   public static class Reeti extends ShoreFacePerceptor {

      public Reeti (ReetiJsonConfiguration config) { 
         super(50, 50, 1700, config);
      }

      @Override
      protected FaceInfo getFaceInfoCPP (int debug) {
         FaceInfo info = CPPinterface.INSTANCE.getReetiFaceInfo(debug);
         if ( info.isRestart() ) { Always.restart(null, "Restart from FaceDetection.DLL"); }
         return info;
      }

      @Override
      protected int initEngine (Object start, int debug) {
         return CPPinterface.INSTANCE.initReetiShoreEngine(
               new String[] { ((ReetiJsonConfiguration) start).getIP() }, debug);
      }

      @Override
      protected void stopEngine () {
         CPPinterface.INSTANCE.terminateReetiShoreEngine(0);
      }
   }
}