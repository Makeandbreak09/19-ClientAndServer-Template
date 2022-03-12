package control;

import model.List;
import view.Client.InteractionPanelHandlerClient;

import static control.TestServer.*;

/** Aus Gründen der Vereinfachung gibt es eine "Verzahnung" (gegenseitige Kennt-Beziehung --> Assoziation) zwischen TestClient und InteractionsPanelHandlerClient.
 *  Im fertigen Programm existiert jeweils ein Objekt. Beide Objekte kennen sich gegenseitig.
 * Created by AOS on 18.09.2017.
 * Updated by AOS on 13.02.2022.
 */
public class TestClient extends Client{

    private InteractionPanelHandlerClient panelHandler;

    private String name;
    private List<String> allClients;

    public TestClient(String pServerIP, int pServerPort, String name, InteractionPanelHandlerClient panelHandler) {
        super(pServerIP, pServerPort);
        this.panelHandler = panelHandler;
        this.name = name;
        this.allClients = new List<>();

        if(isConnected()){
            panelHandler.switchButtons();
            panelHandler.switchTextFields();
        }else{
            panelHandler.textReceived("Es gab ein Verbindungsproblem! Bitte überprüfen Sie die IP und den Port.");
        }
    }

    @Override
    public void processMessage(String pMessage) {
        String[] mArray = pMessage.split(split);
        if(mArray[0].equals(gibName)) {
            send(TestServer.name+split+name);
        }else if(mArray[0].equals(verbunden)) {
            if(!mArray[1].isEmpty() && !mArray[2].isEmpty()){
                panelHandler.textReceived(mArray[2]+" ist dem Chat beigetreten am "+mArray[1]+".");

                if(!mArray[2].equals(name)) {
                    allClients.append(mArray[2]);
                }
                updateComboBox();
            }
        }else if(mArray[0].equals(alleClients)) {
            if(mArray.length>1){
                for(int i = 1; i<mArray.length; i++) {
                    if(!mArray[i].equals(name)) {
                        allClients.append(mArray[i]);
                    }
                }
                updateComboBox();
            }
        }else if(mArray[0].equals(neuerName)) {
            panelHandler.textReceived("Bitte wähle einen anderen Namen aus.");
            this.close();
        }else if(mArray[0].equals(anAlle)) {
            if(!mArray[1].isEmpty() && !mArray[2].isEmpty() && !mArray[3].isEmpty()){
                panelHandler.textReceived(mArray[1]+" - "+mArray[2]+": "+mArray[3]);
            }
        }else if(mArray[0].equals(anEinen)) {
            if(!mArray[1].isEmpty() && !mArray[2].isEmpty() && !mArray[3].isEmpty()){
                panelHandler.textReceived("[Flüstert] "+mArray[1]+" - "+mArray[2]+": "+mArray[3]);
            }
        }else if(mArray[0].equals(nichtVerbunden)){
            panelHandler.textReceived("Niemand mit diesem Namen verbunden.");
        }else if(mArray[0].equals(nachricht)){
            if(!mArray[1].isEmpty() && !mArray[2].isEmpty()){
                panelHandler.textReceived(mArray[1] + " - " + "Server: " + mArray[2]);
            }
        }else if(mArray[0].equals(getrennt)){
            if(!mArray[1].isEmpty()) {
                panelHandler.textReceived(mArray[2] + " hat den Chat verlassen am " + mArray[1] + ".");

                allClients.toFirst();
                while (allClients.hasAccess()) {
                    if (allClients.getContent().equals(mArray[2])) {
                        allClients.remove();
                    } else {
                        allClients.next();
                    }
                }
                updateComboBox();
            }
        }
    }

    @Override
    public void close(){
        super.close();
        panelHandler.switchButtons();
        panelHandler.switchTextFields();
    }

    private void updateComboBox(){
        int count = 1;
        allClients.toFirst();
        while (allClients.hasAccess()){
            count++;
            allClients.next();
        }

        String[] clients = new String[count];
        clients[0] = "Alle";
        allClients.toFirst();
        for(int i = 1; i<clients.length && allClients.hasAccess(); i++){
            clients[i] = allClients.getContent();
            allClients.next();
        }

        panelHandler.updateComboBox(clients);
    }
}
