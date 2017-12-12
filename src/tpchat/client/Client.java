package tpchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe qui gère l'envoi et la réception des messages avec le serveur
 * @author paul, christophe
 */
public class Client {
    
    // ATTRIBUTS
    private Thread thread;
    private MessageListener listener;  
    private Socket socket = null;
    private PrintWriter out = null;
    
    
    /**
     * Lit les messages qui arrivent du serveur et les dispatchent
     */
    private class Listener implements Runnable
    {
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    String[] msg = line.split(" ", 3);
                    if (msg.length < 3)
                        continue;
                    
                    /* 0 : Type
                       1 : Timestamp
                       2 : Le reste du message
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

            } finally {
                listener.onClientDisconnect();
            }
        }
    }

    /**
     * Se connecte au serveur à l'adresse et au port spécifiés
     * @param address Adresse du serveur
     * @param port Port du serveur
     * @throws IOException Erreur d'entrée/sortie
     */
    public void connect (String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream());
        thread = new Thread(new Listener());
        thread.start();
    }
    
    /**
     * Se connecte au serveur à l'adresse et au port spécifiés avec le pseudo fourni
     * Équivalent à : connect(address, port) puis send("/login " + pseudo)
     * @param address Adresse du serveur
     * @param port Port du serveur
     * @param pseudo Pseudo du client à donner au serveur
     * @throws IOException Erreur d'entrée/sortie
     */
    public void connect (String address, int port, String pseudo) throws IOException {
        connect(address, port);
        send("/login " + pseudo);
    }
    
    /**
     * Envoie un message au serveur
     * @param message Message à envoyer au serveur
     * @throws IOException Erreur d'entrée/sortie
     */
    public void send (String message) throws IOException {
        out.println(message);
    }
    
    /**
     * Constructeur de Client
     * @param mListener Écouteur pour les messages reçus depuis le serveur
     */
    public Client (MessageListener mListener) {
        listener = mListener;
    }
}
