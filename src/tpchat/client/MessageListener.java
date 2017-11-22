package tpchat.client;

import java.util.Date;

/**
 *
 * @author paul
 */
public interface MessageListener {
    public void onMessageReceived(MessageType type, Date date, String message);
    public void onInfoReceived(String message);
    public void onWarnReceived(String message);
    public void onErrReceived(String message);
}
