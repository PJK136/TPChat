package tpchat.server;

/**
 *
 * @author paul
 */
public interface ClientListener {
    public void onClientConnect(ClientThread client);
    public void onClientDisconnect(ClientThread client);
    public void onMessageReceived(ClientThread client, String message);
    public void onWhisperReceived(ClientThread client, String pseudoTo, String message);
    public void onPseudoChanged(ClientThread client, String oldPseudo);
}
