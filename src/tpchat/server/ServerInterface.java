package tpchat.server;

/**
 *
 * @author paul
 */
public interface ServerInterface {
    public String queryNewPseudo(String pseudo);
    public void onClientConnect(ClientThread client);
    public void onClientDisconnect(ClientThread client);
    public void onMessageReceived(ClientThread client, String message);
    public void onWhisperReceived(ClientThread from, String pseudoTo, String message);
    public void onPseudoChanged(ClientThread client, String oldPseudo);
}
