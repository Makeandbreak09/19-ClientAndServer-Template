package control;

import view.Client.InteractionPanelHandlerClient;

/** Aus Gründen der Vereinfachung gibt es eine "Verzahnung" (gegenseitige Kennt-Beziehung --> Assoziation) zwischen TestClient und InteractionsPanelHandlerClient.
 *  Im fertigen Programm existiert jeweils ein Objekt. Beide Objekte kennen sich gegenseitig.
 * Created by AOS on 18.09.2017.
 * Updated by AOS on 13.02.2022.
 */
public class TestClient extends Client{

    private InteractionPanelHandlerClient panelHandler;

    public TestClient(String pServerIP, int pServerPort, InteractionPanelHandlerClient panelHandler) {
        //TODO 01 Es muss eine Verbindung mit dem anvisierten Server erstellt werden. Dazu muss der Super-Konstruktor passend aufgerufen werden.
        super(pServerIP, pServerPort);
        this.panelHandler = panelHandler;

        //TODO 05 Falls eine Verbindung vorhanden ist, müssen die Knöpfe im panelHandler aktiviert werden mit der Methode switchButtons. Ansonsten muss eine Ausgabe erfolgen, die dem Nutzer mitteilt, dass es ein Verbindungsproblem gab und er bitte IP und Port prüft.
        if(isConnected()){
            panelHandler.switchButtons();
        }else{
            panelHandler.textReceived("Es gab ein Verbindungsproblem! Bitte überprüfen Sie die IP und den Port.");
        }
    }

    @Override
    public void processMessage(String pMessage) {
        //TODO 07 Die empfangene Nachricht wird einfach in der Client-Oberfläche ausgegeben. Eine eventuelle Auswertung kann danach hier in der Methode intern stattfinden.
        panelHandler.textReceived(pMessage);
    }

    @Override
    public void close(){
        //TODO 08 Die Verbindung muss geschlossen und die Knöpfe im Panel abgeschaltet werden.
        super.close();
        panelHandler.switchButtons();
    }
}
