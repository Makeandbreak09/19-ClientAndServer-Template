package view.Server;

import control.TestServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static control.TestServer.*;

/**
 * Created by Jean-Pierre on 18.09.2017.
 */
public class InteractionPanelHandlerServer {

    private TestServer server;

    private JTextField port;
    private JButton buttonOpen;
    private JButton buttonClose;
    private JTextField message;
    private JTextArea syslog;
    private JTextArea textAreaClients;
    private JPanel panel;
    private JButton buttonSend;

    public InteractionPanelHandlerServer() {
        port.setText("56789");
        message.setText("Ok!");

        textAreaClients.setText("Kein Client angemeldet.");

        createButtons();
    }

    private void createButtons(){
        buttonOpen.setVisible(true);
        buttonClose.setVisible(true);
        buttonOpen.setEnabled(true);
        buttonClose.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openServer();
            }
        });
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeServer();
            }
        });
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendToClients();
            }
        });
    }

    public JPanel getPanel(){
        return panel;
    }

	/**
     * Es wird dem Output Text hinzugefügt.
     * @param text
     */
    private void addToSyslog(String text){
        if(syslog.getText().isEmpty()){
            syslog.setText(text);
        }else{
            syslog.setText(syslog.getText() + "\n" + text);
        }
    }

	/**
     * Es wird ein neues TestServer-Objekt instanziiert.
     */
    private void openServer(){
        server = new TestServer(Integer.parseInt(port.getText()), this);
        switchTextFields();
    }

	/**
     * Das TestServer-Objekt schließt den Port.
     */
    private void closeServer(){
        server.close();
        textAreaClients.setText("Kein Client angemeldet.");
        switchTextFields();
    }

    /**
     * Die eingetragene Nachricht wird genau so wie sie eingetragen wird ohne weitere Zusätze an alle Clients versendet.
     */
    private void sendToClients(){
        server.sendToAll(nachricht+ split +LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))+ split +"Server"+ split +message.getText());
        addToSyslog("sendToAll: "+message.getText());
    }

	/**
     * Der Status der Knöpfe wird geändert.
     * Diese Methode sollte vom TestServer-Objekt aufgerufen werden, sobald dieses einen Port geöffnet geschlossen hat.
     */
    public void buttonSwitch(){
        buttonOpen.setEnabled(!buttonOpen.isEnabled());
        buttonClose.setEnabled(!buttonClose.isEnabled());
        buttonSend.setEnabled(!buttonSend.isEnabled());
    }

    /**
     * Der Status der TextFelder wird geändert.
     * Diese Methode sollte vom TestClient-Objekt aufgerufen werden, sobald sich dieser mit einem Server verbunden oder die Verbindung geschlossen hat.
     */
    public void switchTextFields(){
        port.setEnabled(!port.isEnabled());
    }

	/**
     * Methode wird vom TestServer-Objekt aufgerufen, sobald ein neuer Client sich angemeldet hat.
     * @param pClientIP
     * @param pClientPort
     */
    public void displayNewConnection(String pClientIP, int pClientPort){
        addToSyslog(new java.util.Date().toString() + " - " + "Neuer Client hat sich verbunden: " + pClientIP + ":" + pClientPort);
        updateConnections();
    }

	/**
     * Methode wird vom TestServer-Objekt aufgerufen, sobald ein Client eine Nachricht geschickt hat.
     * @param pClientIP
     * @param pClientPort
     * @param pMessage
     */
    public void showProcessMessageContent(String pClientIP, int pClientPort, String pMessage){
        addToSyslog(new java.util.Date().toString() + " - " + pClientIP + ":" + pClientPort +" - " + pMessage);
    }

    /**
     * Methode wird vom TestServer-Objekt aufgerufen, sobald es einen Fehler beim Starten des Servers gab.
     * @param pMessage
     */
    public void showErrorMessageContent(String pMessage){
        addToSyslog(new java.util.Date().toString() + " - " + pMessage);
    }

	/**
     * Methode gibt den Text zurück, der im Message-Textfield steht.
     * @return
     */
    public String getMessage(){
        return message.getText();
    }

	/**
     * Methode wird aufgerufen, sobald ein Client seine Verbindung zum Server geschlossen hat.
     * @param pClientIP
     * @param pClientPort
     */
    public void displayClosingConnection(String pClientIP, int pClientPort){
        addToSyslog(new java.util.Date().toString() + " - " + "Client hat sich abgemeldet: " + pClientIP + ":" + pClientPort);
        updateConnections();
    }

	/**
     * Methode wird aufgerufen, sobald ein neuer Client sich angemeldet oder ein alter Client sich abgemeldet hat.
     */
    private void updateConnections(){
        textAreaClients.setText("Kein Client angemeldet.");
        String[] connections = server.getClients();
        for(int i = 0; i < connections.length; i++){
            if(i == 0){
                textAreaClients.setText(connections[i]);
            }else{
                textAreaClients.setText(textAreaClients.getText() + "\n" + connections[i]);
            }
        }
    }
}
