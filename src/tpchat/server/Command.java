package tpchat.server;

/**
 *
 * @author paul
 */
public interface Command {
    default public boolean isLoginNeeded() {
        return false;
    }
    
    default public boolean isAdminNeeded() {
        return false;
    }
    
    public void onCommand(Server server, ClientThread client, String command, String args);
    
    static public class LoginCommand implements Command {
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            if (client.isLogged())
                client.sendErr("You're already connected !");
            else if (!args.isEmpty()) {
                for (String h : server.getHistory()) {
                    client.sendRawMessage(h);
                }
                server.setNewPseudo(client, args);
                client.setLogged(true);
                server.sendInfo(client.getPseudo() + " has connected.");
            } else {
                client.sendErr("You must choose a pseudo !");
            }
        }       
    }
    
    static public class DisconnectCommand implements Command {
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            client.disconnect();
        }       
    }
}
