package org.chat;
import java.net.*;
import java.io.*;
import java.util.stream.Stream;

public class Client {
    static BufferedReader in;
    public static void main(String[] args) {
        String host = "localhost";
        if(args.length >= 1){
            host = args[0];
        }
        int port = 7777;
        if(args.length >= 2){
            port = Integer.parseInt(args[1]);
            Stream<String> s;
        }
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread listener = new ListenFromServer();
            listener.start();
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if(userInput.equals("server disconnect")){
                    socket.close();
                    System.exit(0);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }
    static class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = in.readLine();
                    System.out.println(msg);
                }
                catch(IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}