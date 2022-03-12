package view.Client;

import control.TestClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static control.TestServer.*;

/**
 * Created by Jean-Pierre on 13.09.2017.
 */
public class InteractionPanelHandlerClient {

    private TestClient client;

    private JPanel panel;
    private JTextField serverIP;
    private JTextField serverPort;
    private JTextField message;
    private JButton buttonConnect, buttonDisconnect, buttonSend;
    private JTextArea output;
    private JTextField name;
    private JComboBox selectPersonComboBox;

    public InteractionPanelHandlerClient() {
        createButtons();

        name.setText("General Kenobi");
        serverIP.setText("127.0.0.1");
        serverPort.setText("56789");
        message.setText("Hier können sie Nachrichten senden.");

        addToOutput("Willkommen beim Test-Client.");
        addToOutput("Tragen Sie eine IP-Adresse eines Test-Servers samt passenden Port oben ein. Die Nachricht können Sie überarbeiten.");
        addToOutput("Die Nachricht kann an den Server gesendet werden, der Server kann mit einer beliebingen Antwort antworten.");
        addToOutput("-----------------------------------------------------------------------------------");

    }

    private void createButtons(){
        buttonConnect.setVisible(true);
        buttonDisconnect.setVisible(true);
        buttonSend.setVisible(true);

        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);

        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               closeConnection();
            }
        });

        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               send();
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
    private void addToOutput(String text){
        if(output.getText().isEmpty()){
            output.setText(text);
        }else{
            output.setText(output.getText() + "\n" + text);
        }
    }

	/**
     * Es wird ein Objekt der Klasse TestClient instanziiert.
     */
    private void connect(){
        client = new TestClient(serverIP.getText(), Integer.parseInt(serverPort.getText()), name.getText(), this);
    }

	/**
     * Die Verbindung zum Server wird geschlossen.
     */
    private void closeConnection(){
        client.close();
    }

	/**
     * An den Server wird die Nachricht geschickt, die sich im TextField message befindet. Diese wird über das TestClient-Objekt client gesendet.
     */
    private void send(){
        if(getSelected().equals("Alle")) {
            client.send(nachricht + split + message.getText());
        }else{
            client.send(fluester + split + getSelected() + split + message.getText());
        }

        message.setText("");
    }
	
	/**
     * Der Status der TextFelder wird geändert.
     * Diese Methode sollte vom TestClient-Objekt aufgerufen werden, sobald sich dieser mit einem Server verbunden oder die Verbindung geschlossen hat.
     */
    public void switchTextFields(){
        serverIP.setEnabled(!serverIP.isEnabled());
        serverPort.setEnabled(!serverPort.isEnabled());
        name.setEnabled(!name.isEnabled());
    }

    /**
     * Der Status der Knöpfe wird geändert.
     * Diese Methode sollte vom TestClient-Objekt aufgerufen werden, sobald sich dieser mit einem Server verbunden oder die Verbindung geschlossen hat.
     */
    public void switchButtons(){
        buttonConnect.setEnabled(!buttonConnect.isEnabled());
        buttonDisconnect.setEnabled(!buttonDisconnect.isEnabled());
        buttonSend.setEnabled(!buttonSend.isEnabled());
    }

	/**
     * Methode wird vom TestClient-Objekt aufgerufen, sobald dieser eine Nachricht erhalten und gefiltert hat.
     * @param text
     */
    public void textReceived(String text){
        addToOutput(text);
    }

    public void updateComboBox(String[] allClients){
        selectPersonComboBox.removeAllItems();

        for(int i = 0; i<allClients.length; i++){
            selectPersonComboBox.addItem(allClients[i]);
        }
    }

    public String getSelected(){
        return selectPersonComboBox.getSelectedItem().toString();
    }
}
