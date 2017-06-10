/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.ws.klijenti;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author filip
 * Ova klasa sluzi za povezivanje i korištenje operacija web servisa(Soap i REST)
 */
public class MeteoWSKlijent {

       /**
        * POziva SOAP servis operaciju daj sve uredaje
        * @return 
        */ 
    
    
  
    /**
     * Metoda za poziv upisa uredaja preko REST servisa
     * @param naziv
     * @param adresa
     * @return 
     */
//    public static String getUsersREST() {
//        MeteoRESTResourceContainer_JerseyClient mrsc = new MeteoRESTResourceContainer_JerseyClient();
//        return MeteoRESTResourceContainer_JerseyClient.odgovor;
//    }
    
    
        public static String updateUredajREST(Object json){
         MeteoRESTResourceContainer_JerseyClient novi =new MeteoRESTResourceContainer_JerseyClient();
         
        return novi.azurirajUredaj(json);
        
        }
        
      public static  String upodateUserREST(Object json) {
         UsersServersResource_JerseyClient novi = new UsersServersResource_JerseyClient();
         JsonObjectBuilder job = Json.createObjectBuilder();  
       String a= novi.azurirajKorisnika(json);
       return a;
    }
      public static String dohvatiSveUredajeREST(Object json){
        MeteoRESTResourceContainer_JerseyClient novi =new MeteoRESTResourceContainer_JerseyClient();
        
      String jsona=novi.getJson();
      return jsona;
      }
      public static String dohvatiSveUsereREST(){
           UsersServersResource_JerseyClient novi = new UsersServersResource_JerseyClient();
          String a = novi.getJson();
          System.out.println("OVO JE JSON :"+a);
          return a;
      }
      
        public static String dodajUredajREST(Object json){
          MeteoRESTResourceContainer_JerseyClient novi =new MeteoRESTResourceContainer_JerseyClient();
          return novi.dodajUredaj(json);
        }
      
         public static  String registracijaREST(String username) {
        UsersServersResource_JerseyClient novi = new UsersServersResource_JerseyClient();
             System.out.println("USERNAME: "+username);
       String b= novi.getUsersServerResource(username);
             
       return b;
    }
           public static  String registrirajREST(Object json) {
        UsersServersResource_JerseyClient novi = new UsersServersResource_JerseyClient();
         
       String b= novi.dodajKorisnika(json);
               System.err.println("OVO VRACA:"+ b);   
       return b;
    }
      
        
     

    static class MeteoRESTResourceContainer_JerseyClient {

        private WebTarget webTarget;
        private Client client;
        private   String BASE_URI = "http://localhost:8080/fvukovic_aplikacija_1/webresources";

        public MeteoRESTResourceContainer_JerseyClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("meteoREST");
        }
        
        

        public String getMeteoRESTResource(String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String dodajUredaj(Object requestEntity) throws ClientErrorException {
            return webTarget.path("dodaj").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String azurirajUredaj(Object requestEntity) throws ClientErrorException {
            return webTarget.path("azuiraj").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

    static class UsersServersResource_JerseyClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8080/fvukovic_aplikacija_1/webresources";

        public UsersServersResource_JerseyClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("uss");
        }

        public String azurirajKorisnika(Object requestEntity) throws ClientErrorException {
            return webTarget.path("azuiraj").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String dodajKorisnika(Object requestEntity) throws ClientErrorException {
            return webTarget.path("dodaj").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getUsersServerResource(String korisnickoIme) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{korisnickoIme}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

    public  static String geocoder(java.lang.String id) {
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service service = new org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service();
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS port = service.getGeoMeteoWSPort();
        System.out.println("ISPIS VARIJABLE: "+id);
        return port.geocoder(id);
    }

    public static String zadnjiPreuzetiMeteo(int id) {
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service service = new org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service();
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.zadnjiPreuzetiMeteo(id);
    }

    public static String zadnjihNMeteo(java.lang.String n, java.lang.String id) {
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service service = new org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service();
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.zadnjihNMeteo(n, id);
    }

    public static String vazeciMeteo(java.lang.String id) {
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service service = new org.foi.nwtis.fvukovic.ws.GeoMeteoWS_Service();
        org.foi.nwtis.fvukovic.ws.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.vazeciMeteo(id);
    }
    
    

}
