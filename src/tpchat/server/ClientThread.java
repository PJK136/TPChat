package tpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import tpchat.client.MessageType;

/**
 *
 * @author paul
 */
public class ClientThread implements Runnable {
    private final Socket socket;
    private final ClientListener listener;
    
    private boolean logged;
    private boolean admin;
    private String pseudo;
    
    private final PrintStream out;
            
    public ClientThread(Socket socket, ClientListener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;
        
        this.logged = false;
        this.pseudo = null;
        this.out = new PrintStream(socket.getOutputStream());
    }
    
    Socket getSocket() {
        return socket;
    }
    
    public boolean isLogged() {
        return logged;
    }
    
    public void setLogged(boolean logged) {
        this.logged = logged;
    }
    
    public boolean isAdmin() {
        return admin;
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    public String getPseudo() {
        return pseudo;
    }
    
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        sendPseudo(pseudo);
    }

    @Override
    public void run() {
        try { 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                listener.onMessageReceived(this, line);
            }
        } catch (IOException ex) {
            
        } finally {
            listener.onClientDisconnect(this);
        }
    }
    
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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
    
    public static String toPseudo(String pseudo) {
        return "pseudo " + pseudo;
    }
    
    public static String toList(List<String> pseudos) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("list");
        for (String pseudo : pseudos) {
            joiner.add(pseudo);
        }
        
        return joiner.toString();
    }
    
    public void sendWarn(String message) {
        sendRawMessage(toWarn(message));
    }

    public void sendErr(String message) {
        sendRawMessage(toErr(message));
    }
    
    private void sendPseudo(String pseudo) {
        sendRawMessage(toPseudo(pseudo));
    }
    
    public void sendList(List<String> pseudos) {
        sendRawMessage(toList(pseudos));
    }
}
