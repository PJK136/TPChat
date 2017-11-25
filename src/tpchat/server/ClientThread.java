package tpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import tpchat.client.MessageType;

/**
 *
 * @author paul
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private ServerInterface server;
    
    private boolean connected;
    private String pseudo;
    
    private PrintStream out;
            
    public ClientThread(Socket socket, ServerInterface server) throws IOException {
        this.socket = socket;
        this.server = server;
        
        this.connected = false;
        this.pseudo = null;
        this.out = new PrintStream(socket.getOutputStream());
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public String getPseudo() {
        return pseudo;
    }

    @Override
    public void run() {
        try { 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("/")) {
                    String[] args = line.split(" ", 2);
                    if (args.length == 0)
                        continue;

                    if (args[0].equals("/connect")) {
                        if (connected)
                            sendErr("You are already connected !");
                        else if (args.length == 2) {
                            pseudo = server.queryNewPseudo(args[1]);
                            connected = true;
                            server.onClientConnect(this);
                        }
                    } else if (!connected) {
                        sendErr("You are not connected !");
                    } else if (args[0].equals("/pseudo")) {
                        if (args.length == 2) {
                            if (!pseudo.equals(args[1])) {
                                String oldPseudo = pseudo;
                                pseudo = server.queryNewPseudo(args[1]);
                                server.onPseudoChanged(this, oldPseudo);
                            }
                        } else
                            sendErr("Usage : /pseudo <pseudo>");
                    } else if (args[0].equals("/w")) {
                        String[] args2 = args[1].split(" ", 2);
                        if (args.length == 2 && args2.length == 2)
                            server.onWhisperReceived(this, args2[0], args2[1]);
                        else
                            sendErr("Usage : /w pseudo message");
                    } else
                        sendErr("Unknown command !");
                } else if (connected) {
                    if (!line.trim().isEmpty())
                        server.onMessageReceived(this, line);
                } else {
                    sendErr("You are not connected !");
                }
            }
        } catch (IOException ex) {
            
        } finally {
            server.onClientDisconnect(this);
            connected = false;
        }
    }
    
    public void sendRawMessage(String message) {
        out.println(message);
    }
        
    public static String toMessage(MessageType type, Date date, String pseudo, String message) {
        return type + " " + date.getTime() + " " + pseudo + " " + message;
    }
    
    public static String toWhisper(Date date, String pseudoFrom, String pseudoTo, String message) {
        return "wp " + date.getTime() + " " + pseudoFrom + " " + pseudoTo + " " + message;
    }
    
    public static String toInfo(String message) {
        return "info " + message;
    }

    public static String toWarn(String message) {
        return "warn " + message;
    }

    public static String toErr(String message) {
        return "err " + message;
    }

    public void sendMessage(MessageType type, Date date, String pseudo, String message) {
        sendRawMessage(toMessage(type, date, pseudo, message));
    }
    
    public void sendErr(String message) {
        sendRawMessage(toErr(message));
    }

}
