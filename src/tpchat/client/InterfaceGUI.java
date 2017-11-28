package tpchat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author paul
 */
public class InterfaceGUI extends JFrame implements ActionListener, MessageListener {

    private Client client;
    
    private JTextPane chat;
    private JTextField message;
    private JList<String> pseudos;
    
    private String pseudo;
    
    private Style defaultStyle;
    private Style infoStyle;
    private Style warnStyle;
    private Style errStyle;
    private Style whisperStyle;
    
    public InterfaceGUI() throws HeadlessException {
        super("Chat");
        
        client = new Client(this);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        chat = new JTextPane();
        chat.setEditable(false);
        
        message = new JTextField();
        message.addActionListener(this);
        
        pseudos = new JList<>(new DefaultListModel<>());
        
        setLayout(new BorderLayout());
        add(new JScrollPane(chat), BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        add(pseudos, BorderLayout.EAST);
        
        setMinimumSize(new Dimension(800, 600));
        pack();
        
        defaultStyle = chat.addStyle("default", null);
        infoStyle = chat.addStyle("info", null);
        StyleConstants.setForeground(infoStyle, Color.blue);
        warnStyle = chat.addStyle("warn", null);
        StyleConstants.setForeground(warnStyle, Color.orange);
        errStyle = chat.addStyle("err", null);
        StyleConstants.setForeground(errStyle, Color.red);
        whisperStyle = chat.addStyle("whisper", null);
        StyleConstants.setForeground(whisperStyle, Color.pink);
    }

        
    public void connect(String address, int port) {
        try {
            client.connect(address, port);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Impossible de se connecter à " + address + ":" + port + " !", "Connexion impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            client.send(message.getText());
            java.awt.EventQueue.invokeLater(() -> message.setText(""));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Impossible d'envoyer le message !", "Envoi impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onClientDisconnect() {
        java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(this, "Vous avez été déconnecté", "Déconnecté", JOptionPane.INFORMATION_MESSAGE));
        pseudo = null;
    }
    
    @Override
    public void onMessageReceived(Date date, String pseudo, String message) {
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] " + pseudo + " : " + message + "\n", defaultStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onWhisperReceived(Date date, String from, String to, String message) {
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] " + from + " -> " + to + " : " + message + "\n", whisperStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public void onInfoReceived(Date date, String message) {
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] Info : " + message + "\n", infoStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
        
        if (pseudo != null) {
            try {
                client.send("/list");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onWarnReceived(Date date, String message) {
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] Attention : " + message + "\n", warnStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onErrReceived(Date date, String message) {
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] Erreur : " + message + "\n", errStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onPseudoReceived(Date date, String pseudo) {
        this.pseudo = pseudo;
        StyledDocument doc = chat.getStyledDocument();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), "[" + date + "] Votre pseudo est : " + pseudo + "\n", infoStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void onPseudoListReceived(Date date, List<String> pseudos) {
        DefaultListModel listModel = (DefaultListModel) this.pseudos.getModel();
        listModel.clear();
        for (String pseudo : pseudos) {
            listModel.addElement(pseudo);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterfaceGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfaceGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfaceGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfaceGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterfaceGUI gui = new InterfaceGUI();
                gui.setVisible(true);
                gui.connect("127.0.0.1", 8000);
            }
        });
    }
    
}
