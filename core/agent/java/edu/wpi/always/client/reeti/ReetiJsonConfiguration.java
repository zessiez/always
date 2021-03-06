package edu.wpi.always.client.reeti;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import edu.wpi.always.user.UserUtils;
import edu.wpi.disco.rt.util.Utils;
import org.json.simple.*;
import java.io.*;

public class ReetiJsonConfiguration {

   private String IP;

   private String name;

   private double leftEar;

   private double rightEar;

   private double rightLC;

   private double leftLC;

   private double leftEyeLid;

   private double rightEyeLid;

   private double leftEyeTilt;

   private double rightEyeTilt;

   private double leftEyePan;

   private double rightEyePan;

   private double bottomLip;

   private double topLip;

   private double neckRotat;

   private double neckTilt;

   private double neckPan;

   public String getIP () {
      return this.IP;
   }

   public String getName () {
      return this.name;
   }

   public double getLeftEar () {
      return this.leftEar;
   }

   public double getRightEar () {
      return this.rightEar;
   }

   public double getRightLC () {
      return this.rightLC;
   }

   public double getLeftLC () {
      return this.leftLC;
   }

   public double getLeftEyeLid () {
      return this.leftEyeLid;
   }

   public double getRightEyeLid () {
      return this.rightEyeLid;
   }

   public double getLeftEyeTilt () {
      return this.leftEyeTilt;
   }

   public double getRightEyeTilt () {
      return this.rightEyeTilt;
   }

   public double getLeftEyePan () {
      return this.leftEyePan;
   }

   public double getRightEyePan () {
      return this.rightEyePan;
   }

   public double getBottomLip () {
      return this.bottomLip;
   }

   public double getTopLip () {
      return this.topLip;
   }

   public double getNeckRotat () {
      return this.neckRotat;
   }

   public double getNeckTilt () {
      return this.neckTilt;
   }

   public double getNeckPan () {
      return this.neckPan;
   }

   public ReetiJsonConfiguration () {

      try {

         File config = new java.io.File(UserUtils.USER_DIR+"/Reeti.json");
         Utils.lnprint(System.out, "Loading Reeti config file from: "+config);
         JSONObject jsonObject = 
               (JSONObject) JSONValue.parse(new InputStreamReader(new FileInputStream(config)));

         this.IP = (String) jsonObject.get("IP");

         this.name = (String) jsonObject.get("name");
         
         Utils.lnprint(System.out, "Reeti name = "+name);

         this.leftEar = Double.valueOf((String) jsonObject.get("leftEar"));

         this.rightEar = Double.valueOf((String) jsonObject.get("rightEar"));

         this.rightLC = Double.valueOf((String) jsonObject.get("rightLC"));

         this.leftLC = Double.valueOf((String) jsonObject.get("leftLC"));

         this.leftEyeLid = Double
               .valueOf((String) jsonObject.get("leftEyeLid"));

         this.rightEyeLid = Double.valueOf((String) jsonObject
               .get("rightEyeLid"));

         this.leftEyeTilt = Double.valueOf((String) jsonObject
               .get("leftEyeTilt"));

         this.rightEyeTilt = Double.valueOf((String) jsonObject
               .get("rightEyeTilt"));

         this.leftEyePan = Double
               .valueOf((String) jsonObject.get("leftEyePan"));

         this.rightEyePan = Double.valueOf((String) jsonObject
               .get("rightEyePan"));

         this.bottomLip = Double.valueOf((String) jsonObject.get("bottomLip"));

         this.topLip = Double.valueOf((String) jsonObject.get("topLip"));

         this.neckRotat = Double.valueOf((String) jsonObject.get("neckRotat"));

         this.neckTilt = Double.valueOf((String) jsonObject.get("neckTilt"));

         this.neckPan = Double.valueOf((String) jsonObject.get("neckPan"));

      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

   public static void main (String[] args) {
      new ReetiJsonConfiguration();
   }

}
