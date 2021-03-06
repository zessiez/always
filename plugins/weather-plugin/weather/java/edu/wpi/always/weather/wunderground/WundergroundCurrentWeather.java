package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.provider.CurrentWeather;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class WundergroundCurrentWeather implements CurrentWeather {

   private final transient WundergroundHelper helper;
   private final String weatherCondition;
   private final String temperature;
   private final String humidity;
   private final String locationName;
   private final String zip;
   private String comment;
   
   @Override
   public void setComment (String comment) { this.comment = comment; }

   WundergroundCurrentWeather (String zip) throws IOException,
         ParserConfigurationException, SAXException, XPathExpressionException {
      helper = new WundergroundHelper("conditions", zip);
      this.zip = zip;
      this.comment = comment;
      weatherCondition = currentCondition("weather");
      temperature = currentCondition("temp_f");
      humidity = currentCondition("relative_humidity");
      locationName = locationName();
   }

   private String currentCondition (String catagory)
         throws XPathExpressionException {
      String pathString = "/response/current_observation/" + catagory
         + "/text()";
      return helper.getData(pathString);
   }

   private String locationName () throws XPathExpressionException {
      String pathString = "/response/current_observation/display_location/full/text()";
      return helper.getData(pathString);
   }

   @Override
   public String getZip () {
      return zip;
   }

   @Override
   public String getWeatherCondition () {
      return weatherCondition;
   }

   @Override
   public String getTemperature () {
      return temperature;
   }

   @Override
   public String getHumidity () {
      return humidity;
   }

   @Override
   public String getLocationName () {
      return locationName;
   }
}
