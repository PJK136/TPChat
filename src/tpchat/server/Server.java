package tpchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import tpchat.client.MessageType;

/**
 *
 * @author paul
 */
public class Server implements Runnable, ServerInterface {
    private int port;
    private List<ClientThread> clients;
    
    public Server(int port) {
        this.port = port;
        clients = new LinkedList<>();
    }
    
    @Override
    public void run() {
        try {
            ServerSocket listenSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from: " + clientSocket.getInetAddress());
                ClientThread client = new ClientThread(clientSocket, this);
                clients.add(client);
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void sendRaw(String message) {
        for (ClientThread client : clients) {
            if (client.isConnected())
                client.sendRawMessage(message);
        }
    }
    
    private void sendInfo(String message) {
        message = ClientThread.toInfo(message);
        sendRaw(message);
    }
    
    private void sendMessage(MessageType type, Date date, String pseudo, String message) {
        message = ClientThread.toMessage(type, date, pseudo, message);
        sendRaw(message);
    }
    
    private ClientThread findClient(String pseudo) {
        for (ClientThread client : clients) {
            if (client.isConnected() && client.getPseudo().equals(pseudo)) {
                return client;
            }
        }
        
        return null;
    }

    @Override
    public String queryNewPseudo(String pseudo) {
        pseudo = pseudo.replace(' ', '_');
        if (findClient(pseudo) == null)
            return pseudo;
        
        int i = 2;
        while (findClient(pseudo+i) != null)
            i++;
        return pseudo+i;
    }
    
    @Override
    public void onClientConnect(ClientThread client) {
        sendInfo(client.getPseudo() + " has connected.");
    }

    @Override
    public void onClientDisconnect(ClientThread client) {
        if (client.isConnected()) {
            sendInfo(client.getPseudo() + " has disconnected.");
            clients.remove(client);
        }
    }

    @Override
    public void onMessageReceived(ClientThread client, String message) {
        sendMessage(MessageType.MSG, new Date(), client.getPseudo(), message);
    }

    @Override
    public void onWhisperReceived(ClientThread from, String pseudoTo, String message) {
        ClientThread to = findClient(pseudoTo);
        
        if (to == null) {
            from.sendErr(pseudoTo + " not found.");
        } else {
            String raw = ClientThread.toWhisper(new Date(), from.getPseudo(), pseudoTo, message);
            to.sendRawMessage(raw);
            from.sendRawMessage(raw);
        }
    }

    @Override
    public void onPseudoChanged(ClientThread client, String oldPseudo) {
        sendInfo(oldPseudo + " renamed to " + client.getPseudo());
    }
    
    public static void main(String[] args) {
        Server server = new Server(8080);
        server.run();
    }
}
