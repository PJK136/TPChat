package tpchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
                    String[] msg = line.split(" ", 3);
                    if (msg.length < 3)
                        continue;
                    
                    /* 0 : type
                       1 : timestamp
                       2 : le reste
                    */
                    
                    Date date = new Date(Long.valueOf(msg[1]));
                    String[] args = null;
                    
                    switch (msg[0]) {
                        case "msg":
                            args = msg[2].split(" ", 2);
                            if (args.length == 2)
                                listener.onMessageReceived(date, args[0], args[1]);
                            break;
                        case "wp":
                            args = msg[2].split(" ", 3);
                            if (args.length == 3)
                                listener.onWhisperReceived(date, args[0], args[1], args[2]);
                            break;
                        case "info":
                            listener.onInfoReceived(date, msg[2]);
                            break;
                        case "warn":
                            listener.onWarnReceived(date, msg[2]);
                            break;
                        case "err":
                            listener.onErrReceived(date, msg[2]);
                            break;
                        case "pseudo":
                            listener.onPseudoReceived(date, msg[2]);
                            break;
                        case "list":
                            args = msg[2].split(" ");
                            List<String> pseudos = new LinkedList<>();
                            for (int i = 0; i < args.length; i++) {
                                pseudos.add(args[i]);
                            }
                            listener.onPseudoListReceived(date, pseudos);
                            break;
                        default:
                            break;
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
        thread = new Thread(new Listener());
        thread.start();
        
        send("/login " + pseudo);
    }
    
    public void send (String message) throws IOException {
        out.println(message);
    }
    
    public Client (MessageListener mListener) {
        listener = mListener;
    }
}
