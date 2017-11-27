package tpchat.server;

/**
 *
 * @author paul
 */
public interface ClientListener {
    public void onClientDisconnect(ClientThread client);
    public void onMessageReceived(ClientThread client, String message);
}
