package tpchat.server;

import java.util.Date;
import java.util.List;

/**
 *
 * @author paul
 */
public interface UserCommand extends Command {

    @Override
    default public boolean isLoginNeeded() {
        return true;
    }

    static public class WhisperCommand implements UserCommand {
        @Override
        public void onCommand(Server server, ClientThread from, String command, String args) {
            String[] args2 = args.split(" ", 2);
            if (args2.length == 2) {
                String pseudoTo = args2[0];
                String message = args2[1];
                
                ClientThread to = server.getClient(pseudoTo);
        
                if (to == null) {
                    from.sendErr(pseudoTo + " not found.");
                } else {
                    String raw = ClientThread.toWhisper(new Date(), from.getPseudo(), pseudoTo, message);
                    to.sendRawMessage(raw);
                    from.sendRawMessage(raw);
                }
            } else
                from.sendErr("Usage : " + command + " pseudo message");
        }
    }


    static public class PseudoChangeCommand implements UserCommand {       
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            if (!args.isEmpty()) {
                String oldPseudo = client.getPseudo();
                boolean changed = server.setNewPseudo(client, args);
                if (changed)
                    server.sendInfo(oldPseudo + " renamed to " + client.getPseudo());
            } else
                client.sendErr("Usage : " + command + " nickname");
        }
        
    }
    
    static public class ListPseudosCommand implements UserCommand {
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            List<String> pseudos = server.getPseudoList();
            client.sendList(pseudos);
        }
    }
    
    static public class AuthCommand implements UserCommand {
        @Override
        public void onCommand(Server server, ClientThread client, String command, String args) {
            if (!args.isEmpty()) {
                if (args.equals("somuchpower")) {
                    client.setAdmin(true);
                    client.sendInfo("You're now admin !");
                } else {
                    client.sendErr("Wrong password !");
                }
            } else
                client.sendErr("Usage : " + command + " password");
        }
    }
}
