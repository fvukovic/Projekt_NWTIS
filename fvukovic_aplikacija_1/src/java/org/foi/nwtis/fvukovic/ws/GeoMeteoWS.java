/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.ws;

import java.io.StringReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.klijenti.GMRESTHelper;
import org.foi.nwtis.fvukovic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.fvukovic.web.podaci.MeteoPodaci;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
/**
 *
 * @author filip
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    private Connection c;
    private ResultSet rs;
    private Statement s;
    public static ServletContext sc;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operacija koja vrača zadnje meteo podatke za odredeni uredaj
     */
    @WebMethod(operationName = "zadnjiPreuzetiMeteo")
    public String zadnjiPreuzetiMeteo(@WebParam(name = "id") int id) {
        //TODO write your implementation code here:
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        String query = "select * from meteo where id=" + id + " ORDER BY preuzeto DESC limit 1;";
        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {

                job.add("vlaga", rs.getString("vlaga"));
                job.add("temp", rs.getString("temp"));
                job.add("tlak", rs.getString("tlak"));
                job.add("vjetar", rs.getString("vjetar"));
                job.add("vrijeme", rs.getString("vrijemeOpis"));
                jab.add(job);

            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jab.build().toString();

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "zadnjihNMeteo")
    public String operation(@WebParam(name = "n") String n, @WebParam(name = "id") String id) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        String query = "select * from meteo where id=" + id + "  ORDER BY preuzeto DESC limit " + n;
        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {

                job.add("vlaga", rs.getString("vlaga"));
                job.add("temp", rs.getString("temp"));
                job.add("tlak", rs.getString("tlak"));
                job.add("vjetar", rs.getString("vjetar"));
                job.add("vrijeme", rs.getString("vrijemeOpis"));
                jab.add(job);

            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jab.build().toString();

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "vazeciMeteo")
    public String vazeciMeteo(@WebParam(name = "id") String id) {

        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        String query = "select * from uredaji";
        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("id") == Integer.parseInt(id)) {
                    MeteoPodaci mp = novi.getRealTimeWeather(rs.getString("latitude"), rs.getString("longitude"));
                    job.add("vlaga", mp.getHumidityValue());
                    job.add("temp", mp.getTemperatureValue());
                    job.add("tlak", mp.getPressureValue());
                    job.add("vjetar", mp.getWindSpeedValue());
                    job.add("vrijeme", mp.getWeatherValue());
                    jab.add(job);
                     return jab.build().toString();
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Web service operation
     */
 @WebMethod(operationName = "dajSveMeteoPodatkeZaUredjaj")
    public List<MeteoPodaci> dajSveMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        List<MeteoPodaci> mp = new ArrayList<>();
        try {

              Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }
            
            

            //TODO dovršiti preuzimanje meteo podataka iz baze podataka
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fromDate = new Date(from);
            String strDate = sdfDate.format(fromDate);
            Date toDate = new Date(to);
            String strToDate = sdfDate.format(toDate);
            System.out.println("DATUMI :" + strDate + "  " + strToDate);
            String query = "Select * from meteo where id=" + id + " and preuzeto >'" + strDate + "' and preuzeto <'" + strToDate + "'";
              s = c.createStatement();
             rs = s.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getInt("id"));
                mp.add(new MeteoPodaci(new Date(), new Date(), 19.1f, 5.2f, 25.5f, "C", 55.8f, "%", 998.8f, "hPa", 0.0f, "", 0.0f, "", "", 1, "", "ok", 0.0f, "", "", 7, "", "", new Date()));

            }
 
            return mp;
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        mp.add(new MeteoPodaci(new Date(), new Date(), 19.1f, 5.2f, 25.5f, "C", 55.8f, "%", 998.8f, "hPa", 0.0f, "", 0.0f, "", "", 1, "", "ok", 0.0f, "", "", 7, "", "", new Date()));

        return mp;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "Geocoder")
    public String Geocoder(@WebParam(name = "id") String id) {
        
              Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        String query = "select * from uredaji";
        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
            System.err.println("OVO JE ID: "+id);
            while (rs.next()) {
                 if(rs.getInt("id")==Integer.parseInt(id)){
                     
            Client client = ClientBuilder.newClient(); 
            WebTarget webResource = client.target(GMRESTHelper.getGM_BASE_URI())
                    .path("maps/api/geocode/json");
            webResource = webResource.queryParam("latlng", URLEncoder.encode(rs.getString("latitude")+","+rs.getString("longitude")));
            webResource = webResource.queryParam("sensor", "false");
              String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
              JsonReader reader = Json.createReader(new StringReader(odgovor));
              JsonObject jo = reader.readObject();
             
              JsonObject obj = jo.getJsonArray("results").getJsonObject(0).getJsonArray("address_components").getJsonObject(2);
            return obj.getString("long_name");
                 }

            }
        }catch (SQLException ex) {
            System.out.println(ex);
        }

        return "";
        
          
        
       
    }
        //TODO write your implementation code here:
        
}