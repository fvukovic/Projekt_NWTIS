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
        
      public static  String upodateUserREST() {
        MeteoRESTResourceContainer_JerseyClient mrsc = new MeteoRESTResourceContainer_JerseyClient();
         JsonObjectBuilder job = Json.createObjectBuilder();  
        job.add("username", "placljivko");
              job.add("password", "placljivko");
              job.add("email", "placljivko");
       String a= mrsc.dodajUredaj(job.build());
       return a;
    }
        
       /**
     *Poziv SOAP operacije za dohvat svih meteo podatkaa za odredeni uredaj i u odredenom intervalu 
     *
     * @param id
     * @return 
     */
//    public static String getMeteoPodatkeREST(String id) {
//        MeteoRESTResourceContainer_JerseyClient mrsc = new MeteoRESTResourceContainer_JerseyClient(id);
//        return MeteoRESTResourceContainer_JerseyClient.odgovor2;
//    }
//    
    /**
     * Interna klasa za spajanje na REST servis
     */
//    static class MeteoRESTResourceContainer_JerseyClient {
//
//        private WebTarget webTarget;
//        private Client client;
//        public static String odgovor;
//        public static String odgovor2;
//        private static final String BASE_URI = "http://localhost:8080/fvukovic_aplikacija_1/webresources/uss/";
//        
//        /**
//         * kontruktor za pozivanje oepracije dodaj uredaje u bazu
//         * @param naziv
//         * @param adresa 
//         */
//        
//        public MeteoRESTResourceContainer_JerseyClient(String b) {
//
//              client = javax.ws.rs.client.ClientBuilder.newClient();
//            webTarget = client.target(BASE_URI).path("azuriraj");
//            // String odgovor = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
//            // Response response = webTarget.request().put(Entity.json("{'naziv':'UPISIME','adresa':'Varazdin'"));
//             
//              
//            
//            JsonObjectBuilder job = Json.createObjectBuilder(); 
//            job.add("id", "1");
//            job.add("username", "placljivko");
//             job.add("password", "placljivko");
//              job.add("email", "placljivko");
//            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
//            Response response = invocationBuilder.post(Entity.entity(job.build(), MediaType.APPLICATION_JSON));
//            String responseString = response.readEntity(String.class);
//            response.close(); 
//
//        }
//        
        
        
        /**
         * KOntruktor za poziv operacije dohvat svih meteo podataka prema id uredaja
         * @param id 
         */
        
        
        
        
//        public MeteoRESTResourceContainer_JerseyClient(int id) {
//
//      client = javax.ws.rs.client.ClientBuilder.newClient();
//            webTarget = client.target(BASE_URI).path("meteoREST/"+id);
//        String odgovor = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
//        MeteoRESTResourceContainer_JerseyClient.odgovor=odgovor;
//            System.out.println("JSON U SERVISU: " + odgovor);
//        }
//           public MeteoRESTResourceContainer_JerseyClient() {
//
//      client = javax.ws.rs.client.ClientBuilder.newClient();
//            webTarget = client.target(BASE_URI).path("");
//        String odgovor = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
//        MeteoRESTResourceContainer_JerseyClient.odgovor=odgovor;
//            System.out.println("JSON U SERVISU: " + odgovor);
//        }
//
//        public Response postJson(String requestEntity) throws ClientErrorException {
//            System.err.println("JSON: " + requestEntity);
//            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//        }
//
//        public String getJson() throws ClientErrorException {
//            WebTarget resource = webTarget;
//            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
//        }
//
//        public void close() {
//            client.close();
//        }
//    }

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

}
