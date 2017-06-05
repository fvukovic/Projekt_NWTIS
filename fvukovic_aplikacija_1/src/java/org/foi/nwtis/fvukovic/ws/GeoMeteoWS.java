/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.fvukovic.web.podaci.MeteoPodaci;

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
    @WebMethod(operationName = "odDoMeteo")
    public String odDoMeteo(@WebParam(name = "od") long od, @WebParam(name = "parameter1") long parameter1) {
        //TODO write your implementation code here:
        return null;
    }

}
