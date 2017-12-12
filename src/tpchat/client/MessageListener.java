package tpchat.client;

import java.util.Date;
import java.util.List;

/**
 * Écouteur des messages envoyés par le serveur
 * @author paul, christophe
 */
public interface MessageListener {
    
    /**
     * Méthode appelée quand le message d'un client est reçu
     * @param date Date d'envoi du message
     * @param pseudo Pseudo de la personne qui a envoyé le message
     * @param message Message qui a été envoyé
     */
    public void onMessageReceived(Date date, String pseudo, String message);
    
    /**
     * Méthode appelée quand le chuchotement d'un client est reçu
     * @param date Date d'envoi du message
     * @param from Pseudo de la personne qui a envoyé le message
     * @param to Pseudo de la personne qui reçoit le message
     * @param message Message qui a été envoyé
     */
    public void onWhisperReceived(Date date, String from, String to, String message);
    
    /**
     * Méthode appelée quand une information venant du serveur est reçue
     * @param date Date d'envoi du message d'information
     * @param message Message d'information du serveur
     */
    public void onInfoReceived(Date date, String message);
    
    /**
     * Méthode appelée quand un avertissement venant du serveur est reçu
     * @param date Date d'envoi du message d'avertissement
     * @param message Message d'avertissement du serveur
     */
    public void onWarnReceived(Date date, String message);
    
    /**
     * Méthode appelée quand un message d'erreur venant du serveur est reçu
     * @param date Date d'envoi du message d'erreur
     * @param message Message d'erreur du serveur
     */
    public void onErrReceived(Date date, String message);
    
    /**
     * Méthode appelée quand un utilisateur vient de changer de pseudo
     * @param date Date d'envoi du message indiquant le changement de pseudo
     * @param pseudo Nouveau pseudo de l'utilisateur
     */
    public void onPseudoReceived(Date date, String pseudo);
    
    /**
     * Méthode appelée quand le client reçoit la liste des personnes présentes sur le chat
     * @param date Date d'envoi du message du serveur contenant la liste des utlisateurs du chat
     * @param pseudos Liste des pseudos
     */
    public void onPseudoListReceived(Date date, List<String> pseudos);
    
    /**
     * Méthode appelée quand un utilisateur se déconnecte du chat
     */
    public void onClientDisconnect();
}
