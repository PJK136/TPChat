package tpchat.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tpchat.server.Command.*;
import tpchat.server.UserCommand.*;
import tpchat.server.AdminCommand.*;

/**
 *
 * @author paul
 */
public class Server implements Runnable, ClientListener {   
    private boolean stop;
    private final int port;
    private ServerSocket listenSocket;
    private final List<ClientThread> clients;
    private final Map<String, ClientThread> pseudos;
    private final Map<String, Command> commands;
    private final LinkedList<String> history;
    private final int HISTORY_SIZE = 10;
    private final String historyFile;
    private final PrintWriter historyWriter;
    
    public Server(int port, String hist) throws FileNotFoundException {
        stop = false;
        this.port = port;
        listenSocket = null;
        clients = new LinkedList<>();
        pseudos = new HashMap<>();
        commands = new HashMap<>();
        history = new LinkedList<>();
        historyFile = hist;
        historyWriter = new PrintWriter(historyFile);
        
        addCommand("login", new LoginCommand());
        addCommand("disconnect", new DisconnectCommand());
        addCommand("w", new WhisperCommand());
        addCommand("pseudo", new PseudoChangeCommand());
        addCommand("list", new ListPseudosCommand());
        addCommand("auth", new AuthCommand());
        addCommand("kick", new KickCommand());
    }
       
    @Override
    public void run() {
        try {
            stop = false;
            listenSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port + " and writing into " + historyFile + " ...");
            while (!stop) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connection from: " + clientSocket.getInetAddress().getCanonicalHostName() + ":" + clientSocket.getPort());
                ClientThread client = new ClientThread(clientSocket, this);
                synchronized (clients) {
                    clients.add(client);
                }
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            for (ClientThread client : clients)
                client.disconnect();
            historyWriter.flush();
        }
    }
    
    public void stop() throws IOException {
        System.out.println("Server shuting down...");
        stop = true;
        listenSocket.close();
    }
    
    public void addCommand(String name, Command command) {
        commands.put("/" + name, command);
    }
    
    public LinkedList<String> getHistory() {
        LinkedList<String> copyHistory;
        synchronized (history) {
            copyHistory = new LinkedList<>(history);
        }
        
        return copyHistory;
    }
    
    public void sendRaw(String message) {
        List<ClientThread> copyClients;
        synchronized (clients) {
            copyClients = new LinkedList<>(clients);
        }
        
        for (ClientThread client : copyClients) {
            if (client.isLogged())
                client.sendRawMessage(message);
        }
        
        System.out.println(message);
        synchronized (history) {
            history.add(message);
            while (history.size() > HISTORY_SIZE)
                history.poll();
            historyWriter.println(message);
        }
    }
    
    public void sendInfo(String message) {
        message = ClientThread.toInfo(new Date(), message);
        sendRaw(message);
    }
    
    public void sendMessage(Date date, String pseudo, String message) {
        message = ClientThread.toMessage(date, pseudo, message);
        sendRaw(message);
    }

    public boolean setNewPseudo(ClientThread client, String pseudo) {
        pseudo = pseudo.replace(' ', '_');
        
        synchronized (pseudos) {
            if (!pseudo.equals(client.getPseudo()) && pseudos.containsKey(pseudo)) {
                int i = 2;
                String newPseudo = pseudo+i;
                while (!newPseudo.equals(client.getPseudo()) && pseudos.containsKey(newPseudo)) {
                    i++;
                    newPseudo = pseudo+i;
                }
                pseudo = newPseudo;
            }

            if (pseudo.equals(client.getPseudo()))
                return false;

            pseudos.put(pseudo, client);
            if (client.getPseudo() != null)
                pseudos.remove(client.getPseudo());
        }
        
        client.setPseudo(pseudo);
        return true;
    }
    
    @Override
    public void onClientDisconnect(ClientThread client) {
        System.out.println("Disconnection from: " + client.getSocket().getInetAddress().getCanonicalHostName() + ":" + client.getSocket().getPort());
        
        if (client.isLogged()) {
            sendInfo(client.getPseudo() + " has disconnected.");
            synchronized (pseudos) {
                pseudos.remove(client.getPseudo());
            }
            
            synchronized (clients) {
                clients.remove(client);
            }
        }
    }

    @Override
    public void onMessageReceived(ClientThread client, String message) {
        if (message.startsWith("/")) {
            String[] args = message.split(" ", 2);
            if (args.length == 0)
                return;
            
            args[0] = args[0].trim();
            
            Command command = commands.get(args[0]);
            
            if (command != null) {
                if (command.isLoginNeeded() && !client.isLogged()) {
                    client.sendErr("You're not logged !");
                } else if (command.isAdminNeeded() && !client.isAdmin()) {
                    client.sendErr("You're not admin !");
                } else if (args.length == 2)
                    command.onCommand(this, client, args[0], args[1].trim());
                else
                    command.onCommand(this, client, args[0], "");
            } else
                client.sendErr("Unknown command !");
        } else if (client.isLogged()) {
            if (!message.trim().isEmpty()) {
                sendMessage(new Date(), client.getPseudo(), message);
            }
        } else {
            client.sendErr("You are not logged !");
        }
    }
    
    public ClientThread getClient(String pseudo) {
        synchronized (pseudos) {
            return pseudos.get(pseudo);
        }
    }
    
    public List<String> getPseudoList() {
        synchronized (pseudos) {
            return new LinkedList<>(pseudos.keySet());
        }
    }
       
    public static void main(String[] args) throws FileNotFoundException {
        final String historyFile = "history.txt";

        Server server;
        if (args.length >= 1)
            server = new Server(Integer.parseInt(args[0]), historyFile);
        else
            server = new Server(8000, historyFile);
        new Thread(server).start();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = reader.readLine();
            while (!line.startsWith("stop") && !line.startsWith("exit"))
                line = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            server.stop();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
