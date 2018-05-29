/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Iterator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author sandhyamurali
 */



public class Assignment1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  
    {
       /*
        Main class to take input and call respective API's
        
        */
      
        Assignment1 assignment_1=new Assignment1();
        Scanner sc=new Scanner(System.in);
         
        System.out.println("Enter current city"); // enter current city
        String current_location=sc.nextLine();
        System.out.println();
        
       System.out.println("---------WEATHER NEAR YOU----------"); //displays weather near you
       System.out.println();
       String weather=assignment_1.get_weather(current_location);
       assignment_1.display_weather(weather);
       System.out.println();
       
       String s=assignment_1.get_resturants(current_location); //gets famous resturants in the city
       System.out.println("-----------LIST OF NEARBY RESTURANTS----------");
       System.out.println();
       assignment_1.display_resturants(s);
       
       System.out.println();
       System.out.println(" Enter current location landmark : "); //enter start location
       String landmark=sc.nextLine();
       System.out.println(" Enter resturant you want to visit"); //enter end location
       String resturant=sc.nextLine();
       System.out.println();
       
        String output=assignment_1.get_lat_long(landmark,resturant); //get latitude and longitude of location
        String[] lat_long=assignment_1.display_lat_long(output);
        System.out.println();
        
      
        String rides=assignment_1.call_uber(lat_long[0],lat_long[1]); //dipslays uber rides available
        assignment_1.display_uber_available(rides);
        
  
    }
    
    public void display_weather(String weather)
    {
        /*
        Displays weather by parsing JSON and use SOAP web service to convert kelvin to celcius.
        @param: weather : JSON file of weather information in string format.
        
        
        */
        JSONParser parser = new JSONParser();
       JSONObject obj = null;
       
        try {
            obj = (JSONObject) parser.parse(weather);
        } catch (ParseException ex) {
            Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
        }
       
     JSONArray msg = (JSONArray) obj.get("weather");
      Iterator<?> iterator = msg.iterator();
      JSONObject a;
      
      while(iterator.hasNext())
      {
          a=(JSONObject)iterator.next();
          System.out.println("Description : "+a.get("description"));
      }
      
      JSONObject main = (JSONObject) obj.get("main");
      int temp=(int)convertTemp(Double.valueOf(main.get("temp").toString()),net.webservicex.TemperatureUnit.KELVIN,net.webservicex.TemperatureUnit.DEGREE_CELSIUS);
      int temp_min=(int)convertTemp(Double.valueOf(main.get("temp_min").toString()),net.webservicex.TemperatureUnit.KELVIN,net.webservicex.TemperatureUnit.DEGREE_CELSIUS);
      int temp_max=(int)convertTemp(Double.valueOf(main.get("temp_max").toString()),net.webservicex.TemperatureUnit.KELVIN,net.webservicex.TemperatureUnit.DEGREE_CELSIUS);
      System.out.println("temperature is : "+main.get("temp")+"K / "+temp+"C");
      System.out.println("minimum temperature is : "+main.get("temp_min")+"K / "+temp_min+"C");
      System.out.println("maximum temperature is : "+main.get("temp_max")+"K / "+temp_max+"C");
        
    }
    
    public String get_weather(String location)
    {
         /*
        Gets weather information using OpenWeather Map REST API
        @param: location : current city in string format
        
        
        */
       MultivaluedMap queryParams = new MultivaluedMapImpl();
       
       queryParams.add("q",location);
       queryParams.add("APPID","4631159c666aa0dd1157a6f386648dcb");
       
       
       Client client = Client.create();
       WebResource webResource = client.resource("https://api.openweathermap.org/data/2.5/weather");
       ClientResponse response = webResource.queryParams(queryParams)
               .accept("application/json")
                   .get(ClientResponse.class);
     
       String weather = response.getEntity(String.class);
       return weather;
    }
    
    public void display_uber_available(String rides)
    {
         /*
        Displays uber rides available
        @param: weather : JSON file of rides information in string format.
        */
       JSONParser parser = new JSONParser();
       JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(rides);
        } catch (ParseException ex) {
            Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       JSONArray msg = (JSONArray) obj.get("times");
       Iterator<?> iterator = msg.iterator();
       JSONObject a;
       while(iterator.hasNext())
       {
           a=(JSONObject)iterator.next();
           System.out.print("estimate of arrival is : "+Integer.parseInt(a.get("estimate").toString())/60+" seconds");
           System.out.print("  car type is : "+a.get("display_name"));
           System.out.println();
       }
        
    }
    public String call_uber(String latitude,String longitude)
    {
         /*
        Gets uber rides available using Uber REST API
        @param: latitude : latitude of destination.
        @param: longitude: longitude of destination.
        */
        MultivaluedMap queryParams = new MultivaluedMapImpl();
       queryParams.add("start_latitude",latitude);
       queryParams.add("start_longitude",longitude);
       
       Client client = Client.create();
       WebResource webResource = client.resource("https://api.uber.com/v1.2/estimates/time");
       ClientResponse response = webResource.queryParams(queryParams)
               .header("authorization", "Bearer KA.eyJ2ZXJzaW9uIjoyLCJpZCI6IldrOXRDR1d6UmlPZCtzYjNCNVlTdVE9PSIsImV4cGlyZXNfYXQiOjE1MjI0NTAxNTMsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.NXf1vVFNOCW_-Jif2v_Tb_FTj2BdeftYguRvHmf8R6w")
               .accept("application/json")
                   .get(ClientResponse.class);
     
       String rides = response.getEntity(String.class);
      
       
       return rides;
        
    }
    
    public String[] display_lat_long(String output)
    {
         /*
        Displays total distance and time taken to reach the destination  by parsing JSON file in string format
        @param: output : JSON file of reaching the destination in string format.
        
        
        */
       JSONParser parser = new JSONParser();
       JSONObject obj = null;
       String[] result=new String[2];
        try {
            obj = (JSONObject) parser.parse(output);
        } catch (ParseException ex) {
            Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       JSONArray msg = (JSONArray) obj.get("routes");
       Iterator<?> iterator = msg.iterator();
       JSONObject a,a1 ;
       
        while(iterator.hasNext())
        {
             a=(JSONObject)iterator.next();
             
             JSONArray legs = (JSONArray) a.get("legs");
         
             Iterator<?> itr = legs.iterator();
             while(itr.hasNext())
             {
                 a1=(JSONObject)itr.next();
                 JSONObject distance = (JSONObject) a1.get("distance");
                 JSONObject duration = (JSONObject) a1.get("duration");
                 JSONObject end_location = (JSONObject) a1.get("end_location");
                 System.out.println("Total Distance is : "+distance.get("text")+" and Total time taken is : "+duration.get("text"));
 
                 result[0]=end_location.get("lat").toString();
                 result[1]=end_location.get("lng").toString();
  
             }
            
            
        }
        
        return result;
        
    }
    
    public String get_lat_long(String current_location,String resturant)
    {
         /*
        Gets Latitude and longitude using Google Map REST API
        @param: current_location : Start Location.
        @param: resturant: end location
   
        */
       MultivaluedMap queryParams = new MultivaluedMapImpl();
       queryParams.add("origin",current_location);
       queryParams.add("destination", resturant);
       queryParams.add("key","AIzaSyDOR9MnFSk4VTBhfoaWgAtKy5IvPq8TL5w");
       Client client = Client.create();
       WebResource webResource = client.resource("https://maps.googleapis.com/maps/api/directions/json?");
       ClientResponse response = webResource.queryParams(queryParams)
               .accept("application/json")
                .get(ClientResponse.class);
     
      
     String output = response.getEntity(String.class);
     
     return output;
    }
    
    
    
    
    public void display_resturants(String s)
    {
         /*
        Displays famous resturants  in the city by parsing JSON file
        @param: s : JSON file of resturant information in string format.
   
        */
       JSONParser parser = new JSONParser();
       JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(s);
        } catch (ParseException ex) {
            Logger.getLogger(Assignment1.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
       JSONArray msg = (JSONArray) obj.get("businesses");
       Iterator<?> iterator = msg.iterator();
       JSONObject a;
       while(iterator.hasNext()) 
       {
           a=(JSONObject)iterator.next();
           
           JSONObject location = (JSONObject) a.get("location");
           System.out.println("Name : "+a.get("name")+"  Is_Closed : "+a.get("is_closed")+"  Rating : "+a.get("rating")+"  Phone : "+a.get("phone")+"  Address : "+location.get("address1")+"  Zip Code : "+location.get("zip_code"));
   
       }
       
       
       
        
    }
    
    public String get_resturants(String current_location)
    {
         /*
        Gets famous resturants in the city by using Yelp REST API
        @param: current_location : current city name in String format.
        
        
        */
       MultivaluedMap queryParams = new MultivaluedMapImpl();
       queryParams.add("location",current_location);
     
       Client client = Client.create();
       WebResource webResource = client.resource("https://api.yelp.com/v3/businesses/search");
       ClientResponse response = webResource.queryParams(queryParams)
               .header("authorization", "Bearer sEIBdM7tmT4Byc-RuyBEqOVWQfb6aj9t5OJJiMrI5TI-X92hcV-ISJkbX9LCwA4jSZ9r7car_2isNquJ_tK_cYoGjSznQ_UptGt99OvBYf34jDgjBbVZWlpVaq-VWnYx")
               .accept("application/json")
                   .get(ClientResponse.class);
     
       String s = response.getEntity(String.class);
       
       return s;
        
    }

   
    private double convertTemp(double temperature, net.webservicex.TemperatureUnit fromUnit, net.webservicex.TemperatureUnit toUnit) 
    {
         /*
        Converts weather information from one temperature unit to another temperature unit. This is a SOAP API
        @param: fromUnit : temperature  unit to convert from
        @param toUnit: temperature unit to convert to
        @param temperature: temperature value

        */
        net.webservicex.ConvertTemperature service = new net.webservicex.ConvertTemperature();
        net.webservicex.ConvertTemperatureSoap port = service.getConvertTemperatureSoap();
        return port.convertTemp(temperature, fromUnit, toUnit);
    }

    

  

       
   

   

    

   
    
    
    
    
}
