package tpchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paul
 */
public class Client {
    
    private Thread thread;
    private MessageListener listener;  
    private Socket socket = null;
    private PrintStream out = null;

    private class Listener implements Runnable
    {
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while (true) {
                    line = in.readLine();
                    String [] tab = line.split(" ", 2);
                    if (tab[0].equals("msg")) {
                        String [] tab2 = tab[1].split(" ", 3);
                        
                    }
                    else if (tab[0].equals("wp")) {
                        
                    }
                    else if (tab[0].equals("hist")) {
                        
                    }
                    else if (tab[0].equals("info")) {
                        
                    }
                    else if (tab[0].equals("err")) {
                        
                    }
                    else if (tab[0].equals("warn")) {
                        
                    }
                }
            } catch (IOException e) {
                listener.onClientDisconnect();
            }
        }
    }
    
    public void connect (String address, int port, String pseudo) throws IOException {
        socket = new Socket(address, port);
        out = new PrintStream(socket.getOutputStream());
        out.println("/connect " + pseudo);
    }
    
    public void send (String message) throws IOException {
        out = new PrintStream(socket.getOutputStream());
        out.println(message);
    }
    
    public Client (MessageListener mListener) {
        listener = mListener;
    }
}
