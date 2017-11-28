package tpchat.client;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author paul
 */
public class Interface implements Runnable, MessageListener {
    private boolean stop;

    public Interface() {
        stop = false;
    }
    
    @Override
    public void run() {
        stop = false;
        Client client = new Client(this);
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez entrer votre adresse IP.");
        String adresseIp = sc.nextLine();
        System.out.println("Veuillez entrer le port du serveur.");
        int port = parseInt(sc.nextLine(), 10);
        System.out.println("Veuillez entrer votre pseudo.");
        String pseudo = sc.nextLine();
        try {
            client.connect(adresseIp, port, pseudo);
        } catch (IOException ex) {
            System.err.println("Impossible de se connecter.");
            ex.printStackTrace();
            return;
        }
        
        while(!stop) {
            String line = sc.nextLine();
            try {
                client.send(line);
            } catch (IOException ex) {
                System.err.println("Impossible d'envoyer le message.");
            }
        }
    }
    
    @Override
    public void onClientDisconnect() {
        System.out.println("Vous avez été déconnecté");
        stop = true;
    }
    
    @Override
    public void onMessageReceived(Date date, String pseudo, String message) {
        System.out.println("[" + date + "] " + pseudo + " : " + message);
    }

    @Override
    public void onWhisperReceived(Date date, String from, String to, String message) {
        System.out.println("[" + date + "] " + from + " -> " + to + " : " + message);
    }
    
    @Override
    public void onInfoReceived(Date date, String message) {
        System.out.println("[" + date + "] Info : " + message);
    }

    @Override
    public void onWarnReceived(Date date, String message) {
        System.out.println("[" + date + "] Warn : " + message);
    }

    @Override
    public void onErrReceived(Date date, String message) {
        System.out.println("[" + date + "] Erreur : " + message);
    }

    @Override
    public void onPseudoReceived(Date date, String pseudo) {
        System.out.println("[" + date + "] Votre pseudo est : " + pseudo);
    }

    @Override
    public void onPseudoListReceived(Date date, List<String> pseudos) {
        System.out.print("[" + date + "] Les personnes présentes sur ce chat sont : ");
        int i = 0;
        for (String pseudo : pseudos) {
            System.out.print(pseudo);
            if (i != pseudos.size()-1)
                System.out.print(", ");
            else
                System.out.print(".");
            
            i++;
        }
        
        System.out.println();
    }
    
    public static void main(String[] args) {
        Interface inter = new Interface();
        inter.run();
    }
}
