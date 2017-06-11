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
    
    
    public naredbeServer() {
         
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
    
    
    public void start(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; START;";
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
    public void pause(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; PAUSE;";
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
    public void stop(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; STOP;";
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
    
    public void startMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER fvukovic; PASSWD 123456; IoT_Master START;";
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
    
      public void workMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER fvukovic; PASSWD 123456; IoT_Master WORK;";
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
      public void stopMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER fvukovic; PASSWD 123456; IoT_Master STOP;";
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
      public void waitMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER fvukovic; PASSWD 123456; IoT_Master WAIT;";
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
     public void statusMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER fvukovic; PASSWD 123456; IoT_Master STATUS;";
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
    
    public void status(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; STATUS;";
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
