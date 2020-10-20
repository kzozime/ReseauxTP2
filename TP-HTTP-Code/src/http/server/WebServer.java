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
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String headers = ".";
        while (headers != null && !headers.equals("")){ //differencier les methodes
          headers = in.readLine();
          System.out.println(headers);
         }
        // Send the response
        // Send the headers
//        out.println("HTTP/1.0 200 OK");
//        out.println("Content-Type: text/html");
//        out.println("Server: Bot");
        // this blank line signals the end of the headers
//        out.println("");
        // Send the HTML page
//        out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
//        out.flush();
//        remote.close();
        httpGET(out, INDEX);
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }
  //Methode GET implementation
  protected void httpGET(PrintWriter out, String filename) {
    System.out.println("GET " +filename);
      File resource = new File(filename);
      if(resource.exists()) {
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-length: "+resource.length());
        out.println("Server: Bot");
        out.println("");
      }
      try {
        BufferedReader fileResponse = new BufferedReader(new FileReader(resource));
        String line;
        while((line = fileResponse.readLine()) != null) {
         out.println(line);
        }
        fileResponse.close();
      }catch (IOException e) {
        System.out.println("Error : file can't be read");
      }
      out.flush();
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
