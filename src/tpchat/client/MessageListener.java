package tpchat.client;

import java.util.Date;
import java.util.List;

/**
 *
 * @author paul
 */
public interface MessageListener {
    public void onMessageReceived(Date date, String pseudo, String message);
    public void onWhisperReceived(Date date, String from, String to, String message);
    public void onInfoReceived(Date date, String message);
    public void onWarnReceived(Date date, String message);
    public void onErrReceived(Date date, String message);
    public void onPseudoReceived(Date date, String pseudo);
    public void onPseudoListReceived(Date date, List<String> pseudos);
    public void onClientDisconnect();
}
