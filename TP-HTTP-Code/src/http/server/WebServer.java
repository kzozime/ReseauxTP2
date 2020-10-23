///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**Chemin relatif du repertoire de ressources du serveur*/
	protected static final String RESOURCE_DIRECTORY = "doc";
	/**Chemin relatif de la page web e envoyer en cas d'erreur 404*/
	protected static final String FILE_NOT_FOUND = "doc/notfound.html";
	/**Chemin relatif de la page d'acceuil du serveur*/
	protected static final String INDEX = "doc/index.html";

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
          System.out.println("__________________________________________");
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String headers = new String();
        String str = ".";
        while (str != null && !str.equals("")){ //differencier les methodes
          str = in.readLine();
          //System.out.println(str);
          headers += str;
         }

        String[] words = headers.split(" ");
        String methode = words[0];
        String url = words[1].substring(1);
        System.out.println(methode);
        System.out.println(url);

        if (url.isEmpty()) {
	      System.out.println("page d'accueil");
          httpGET(out, INDEX);
        }else if(url.startsWith(RESOURCE_DIRECTORY)){
            switch (methode) {
                case "GET" :
                    httpGET(out, url);
                    System.out.println("get");
                    break;
                case "POST":
                    httpPOST(in, out, url);
                    System.out.println("post");
                    break;
                case "HEAD":
                    httpHEAD(out, url);
                    System.out.println("head");
                    break;
                case "PUT":
                    httpPUT(in, out, url);
                    System.out.println("put");
                    break;
                case "DELETE":
                    httpDELETE(out, url);
                    System.out.println("delete"); 
                    break;
                default :
                    out.println("501 Not Implemented");
                    out.flush();
                    break;
            }
        }else{
            out.println("403 Forbidden");
            out.flush();
        }


      } catch (Exception e) {
          e.printStackTrace();

      }
    }
  }
  //Methode GET implementation
  protected void httpGET(PrintWriter out, String filename) {
      System.out.println("GET " + filename);
          File resource = new File(filename);
          if (resource.exists()) {
              out.println("HTTP/1.0 200 OK");
              out.println(typeOfFile(filename));
              out.println("Content-length: " + resource.length());
              out.println("Server: Bot");
              out.println("");
          }else {
              resource = new File(FILE_NOT_FOUND);
              out.println("HTTP/1.0 404 Not Found");
              out.println(typeOfFile(filename));
              out.println("Content-length: " + resource.length());
              out.println("Server: Bot");
              out.println("");
          }
          try {
              BufferedReader fileResponse = new BufferedReader(new FileReader(resource));
              String line;
              while ((line = fileResponse.readLine()) != null) {
                  out.println(line);
              }
              fileResponse.close();
          } catch (IOException e) {
              System.out.println("Error : file can't be read");
          }
          out.flush();
  }

    protected void httpHEAD(PrintWriter out, String filename) {
        System.out.println("HEAD " + filename);
            File resource = new File(filename);
            if (resource.exists()) {
                out.println("HTTP/1.0 200 OK");
                out.println(typeOfFile(filename));
                out.println("Content-length: " + resource.length());
                out.println("Server: Bot");
                out.println("");
            } else {
                resource = new File(FILE_NOT_FOUND);
                out.println("HTTP/1.0 404 Not Found");
                out.println(typeOfFile(filename));
                out.println("Content-length: " + resource.length());
                out.println("Server: Bot");
                out.println("");
            }

            out.flush();
    }
    protected void httpPOST(BufferedReader in, PrintWriter out, String filename) {
        System.out.println("POST " + filename);
            File resource = new File(filename);
            boolean existed = resource.exists();
            try {
                PrintWriter fileOut = new PrintWriter(new FileOutputStream(resource, existed));
                String line;
                try {
                    char[] buffer = new char[256];
                    while(in.ready()) {
                        int nbRead = in.read(buffer);
                        fileOut.write(buffer, 0, nbRead);
                    }

                } catch (IOException e) {
                    System.out.println("No : Data sent");
                }
                fileOut.flush();

                fileOut.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error : fichier introuvable");
            }
            if(existed) {
                out.println("HTTP/1.0 200 OK");
                out.println(typeOfFile(filename));
                out.println("Content-length: "+resource.length());
                out.println("Server: Bot");
                out.println("");
            } else {

                out.println("HTTP/1.0 201 Created");
                out.println(typeOfFile(filename));
                out.println("Content-length: "+resource.length());
                out.println("Server: Bot");
                out.println("");
            }
            out.flush();

    }

    protected void httpPUT(BufferedReader in, PrintWriter out, String filename) {
        System.out.println("PUT " + filename);
        File resource = new File(filename);
        boolean existed = resource.exists();
        System.out.println(existed);

        if(existed) {
            resource.delete();
        }
        try {
            resource.createNewFile();
        }catch (IOException e) {
            System.out.println("Error : during the process of creating FILE");
        }
        try {
            PrintWriter fileOut = new PrintWriter(new FileOutputStream(resource, existed));

            // String line ;
            // System.out.println("debut while");
            // line= in.readLine();
            // System.out.println(line);
            // while (line != null) {
            //     System.out.println("boucle");
            //     fileOut.append(line + "\n");
            //     line= in.readLine();
            // }
            
            //Lecture du body du
            char[] buffer = new char[256];
            while(in.ready()) {
                int nbRead = in.read(buffer);
                fileOut.write(buffer, 0, nbRead);
            }

            fileOut.flush();
            fileOut.close();
        }catch (FileNotFoundException e) {
            System.out.println("Error : fichier introuvable");
        }catch (IOException e) {
            System.out.println("No : Data sent");
        }


        if(existed) {
            out.println("HTTP/1.0 200 OK");
            out.println(typeOfFile(filename));
            out.println("Content-length: "+resource.length());
            out.println("Server: Bot");
            out.println("");
        } else {
            out.println("HTTP/1.0 201 Created");
            out.println(typeOfFile(filename));
            out.println("Content-length: "+resource.length());
            out.println("Server: Bot");
            out.println("");
        }
        out.flush();

        System.out.println("fin de la methode");

    }

    protected void httpDELETE(PrintWriter out, String filename) {
        System.out.println("DELETE " + filename);
        try {
            File resource = new File(filename);
            boolean deleted = false;
            boolean existed = false;
            if((existed = resource.exists())) {
                deleted = resource.delete();
            }

            if(deleted) {
                out.println("HTTP/1.0 200 OK");
                out.println(typeOfFile(filename));
                out.println("Content-length: "+resource.length());
                out.println("Server: Bot");
                out.println("");
            } else if (!existed) {
                out.println("HTTP/1.0 404 Not Found");
                out.println(typeOfFile(filename));
                out.println("Content-length: "+resource.length());
                out.println("Server: Bot");
                out.println("");
            } else {
                out.println("HTTP/1.0 403 Forbideen");
                out.println(typeOfFile(filename));
                out.println("Content-length: "+resource.length());
                out.println("Server: Bot");
                out.println("");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                out.println("HTTP/1.0 500 Internal Server Error");
                out.println(typeOfFile(filename));
                out.println("Server: Bot");
                out.println("");
                out.flush();
            } catch (Exception e2) {};
        }
    }

    protected String typeOfFile(String filename) {
        String type="";
        if(filename.endsWith(".html") || filename.endsWith(".htm"))
            type= "Content-Type: text/html";
        else if(filename.endsWith(".mp4"))
            type= "Content-Type: video/mp4";
        else if(filename.endsWith(".png"))
            type= "Content-Type: image/png";
        else if(filename.endsWith(".jpeg") || filename.endsWith(".jpg"))
            type= "Content-Type: image/jpg";
        else if(filename.endsWith(".mp3"))
            type= "Content-Type: audio/mp3";
        else if(filename.endsWith(".avi"))
            type= "Content-Type: video/x-msvideo";
        else if(filename.endsWith(".css"))
            type= "Content-Type: text/css";
        else if(filename.endsWith(".pdf"))
            type= "Content-Type: application/pdf";
        else if(filename.endsWith(".odt"))
            type= "Content-Type: application/vnd.oasis.opendocument.text";
        return type;
    }
    /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
