package tpchat.server;

/**
 *
 * @author paul
 */
public interface AdminCommand extends UserCommand {
    default public boolean isAdminNeeded() {
        return true;
    }
        
    static public class KickCommand implements AdminCommand {
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            String[] args2 = args.split(" ", 2);
            if (args2.length >= 1) {
                String pseudo = args2[0];
                String reason = args2.length == 2 ? args2[1] : "";
                
                ClientThread to = server.getClient(pseudo);
        
                if (to == null) {
                    client.sendErr(pseudo + " not found.");
                } else {
                    if (!reason.isEmpty())
                        server.sendInfo(pseudo + " is kicked (Reason: " + reason + ")");
                    else
                        server.sendInfo(pseudo + " is kicked.");
                    
                    to.disconnect();
                }
            } else
                client.sendErr("Usage : " + command + " pseudo [reason]");
        }
    }
}
