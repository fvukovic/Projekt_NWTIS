/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import org.foi.nwtis.fvukovic.sesije.SessionUtils;

/**
 *
 * @author filip
 */
@Named(value = "naredbeServer")
@SessionScoped
public class naredbeServer implements Serializable {

    /**
     * Creates a new instance of naredbeServer
     */
    private String odgovor;
    private String statusServer;
    private String odgovorMaster;
    
    
    public naredbeServer() {
         
    }

    public String getOdgovorMaster() {
        return odgovorMaster;
    }

    public void setOdgovorMaster(String odgovorMaster) {
        this.odgovorMaster = odgovorMaster;
    }

    
    
    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public String getStatusServer() {
        return statusServer;
    }

    public void setStatusServer(String statusServer) {
        this.statusServer = statusServer;
    } 
    
    /**
     * Naredba start, za startanje radne dretve
     */
    public void start(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
               String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; START;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovor=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    /**
     * salje naredbu za pauziranje radne dretve na serveru
     */
    public void pause(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; PAUSE;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovor=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    /**
     * Salje naredbu za stopiranje radne dretve, prekida primanje svih komandi
     */
    public void stop(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
               String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; STOP;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovor=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    /**
     * salje zahtjev za registriranje grupe
     */
    public void startMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master START;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    /**
     * salje zahtjev za aktiviranje grupe
     */
      public void workMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master WORK;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
      /**
       *Deregistrira grupu 
       */
      public void stopMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master STOP;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
      /**
       * pauzira grupu 
       */
      public void waitMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master WAIT;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
      
       /**
        * nije implementirano
        */
         public void pauseMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master PAUSE;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    
         
         /**
          * vraca status grupe sa servisa
          */
     public void statusMaster(){
    
        Socket socket = null; 
        System.out.println("STATUS funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master STATUS;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    
   /**
    * braca status radne dretve
    */  
    public void status(){
    
        Socket socket = null; 
        System.out.println("status funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; STATUS;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovor=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    
}
