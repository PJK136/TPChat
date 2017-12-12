package tpchat.client;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Interface en ligne de commande du client
 * @author paul, christophe
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
    
    /**
     * Affiche le message envoyé par un client à tout les autres utilisateurs du chat
     * @param date Date du message
     * @param pseudo Pseudo de la personne qui a envoyé le message
     * @param message Message qui a été envoyé
     */
    @Override
    public void onMessageReceived(Date date, String pseudo, String message) {
        System.out.println("[" + date + "] " + pseudo + " : " + message);
    }
    
    /**
     * Affiche le message privé envoyé par un client à un autre
     * @param date Date du message
     * @param from Pseudo de la personne qui a envoyé le message
     * @param to Pseudo de la personne qui reçoit le message
     * @param message Message qui a été envoyé
     */
    @Override
    public void onWhisperReceived(Date date, String from, String to, String message) {
        System.out.println("[" + date + "] " + from + " -> " + to + " : " + message);
    }
    
    /**
     * Affiche un message d'information du serveur 
     * @param date Date du message d'information
     * @param message Message d'information du serveur
     */
    @Override
    public void onInfoReceived(Date date, String message) {
        System.out.println("[" + date + "] Info : " + message);
    }
    
    /**
     * Affiche un message d'avertissement du serveur
     * @param date Date d'envoi du message d'avertissement
     * @param message Message d'avertissement du serveur
     */
    @Override
    public void onWarnReceived(Date date, String message) {
        System.out.println("[" + date + "] Warn : " + message);
    }

    /**
     * Affiche un message d'erreur du serveur
     * @param date Date d'envoi du message d'erreur
     * @param message Message d'erreur du serveur
     */
    @Override
    public void onErrReceived(Date date, String message) {
        System.out.println("[" + date + "] Erreur : " + message);
    }
    
    /**
     * Affiche un message indiquant le nouveau pseudo de l'utilisateur
     * @param date Date d'envoi du message indiquant le changement de pseudo
     * @param pseudo Nouveau pseudo de l'utilisateur
     */
    @Override
    public void onPseudoReceived(Date date, String pseudo) {
        System.out.println("[" + date + "] Votre pseudo est : " + pseudo);
    }
    
    /**
     * Affiche un message indiquant la liste de toutes les personnes présentent sur le chat
     * @param date Date d'envoi du message du serveur contenant la liste des utlisateurs du chat
     * @param pseudos Liste des pseudos 
     */
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
    
    /**
     * Affiche un message indiquant qu'un utilisateur s'est déconnecté
     */
    @Override
    public void onClientDisconnect() {
        System.out.println("Vous avez été déconnecté");
        stop = true;
    }
    
    /**
     * Point d'entré de l'application
     * @param args Non utilisé
     */
    public static void main(String[] args) {
        Interface inter = new Interface();
        inter.run();
    }
    

}
