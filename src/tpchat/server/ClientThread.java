package tpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

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
        synchronized (out) {
            out.println(message);
        }
    }
        
    public static String toMessage(Date date, String pseudo, String message) {
        return new StringJoiner(" ")
                .add("msg").add(String.valueOf(date.getTime()))
                .add(pseudo).add(message).toString();
    }
    
    public static String toWhisper(Date date, String pseudoFrom, String pseudoTo, String message) {
        return new StringJoiner(" ")
                .add("wp").add(String.valueOf(date.getTime()))
                .add(pseudoFrom).add(pseudoTo).add(message).toString();
    }
    
    public static String toInfo(Date date, String message) {
        return new StringJoiner(" ")
                .add("info").add(String.valueOf(date.getTime()))
                .add(message).toString();
    }

    public static String toWarn(Date date, String message) {
        return new StringJoiner(" ")
                .add("warn").add(String.valueOf(date.getTime()))
                .add(message).toString();
    }

    public static String toErr(Date date, String message) {
        return new StringJoiner(" ")
                .add("err").add(String.valueOf(date.getTime()))
                .add(message).toString();
    }
    
    public static String toPseudo(Date date, String pseudo) {
        return new StringJoiner(" ")
                .add("pseudo").add(String.valueOf(date.getTime()))
                .add(pseudo).toString();
    }
    
    public static String toList(Date date, List<String> pseudos) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("list");
        joiner.add(String.valueOf(date.getTime()));
        for (String pseudo : pseudos) {
            joiner.add(pseudo);
        }
        
        return joiner.toString();
    }
    
    public void sendInfo(String message) {
        sendRawMessage(toInfo(new Date(), message));
    }
    
    public void sendWarn(String message) {
        sendRawMessage(toWarn(new Date(), message));
    }

    public void sendErr(String message) {
        sendRawMessage(toErr(new Date(), message));
    }
    
    private void sendPseudo(String pseudo) {
        sendRawMessage(toPseudo(new Date(), pseudo));
    }
    
    public void sendList(List<String> pseudos) {
        sendRawMessage(toList(new Date(), pseudos));
    }
}
