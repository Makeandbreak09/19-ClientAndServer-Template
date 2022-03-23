package control;

import model.List;
import view.Server.InteractionPanelHandlerServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**Aus Gründen der Vereinfachung gibt es eine "Verzahnung" (gegenseitige Kennt-Beziehung --> Assoziation) zwischen TestServer und InteractionsPanelHandlerServer.
 *  Im fertigen Programm existiert jeweils ein Objekt. Beide Objekte kennen sich gegenseitig.
 * Created by AOS on 18.09.2017.
 * Updated by AOS on 13.02.2022.
 */
public class TestServer extends Server{

    private class Client{

        private String ip;
        private int port;
        private String name;

        private Client(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        private void setName(String name){
            this.name = name;
        }
    }

    public static final String gibName = "GIBNAME";
    public static final String name = "NAME";
    public static final String verbunden = "VERBUNDEN";
    public static final String alleClients = "ALLECLIENTS";
    public static final String neuerName = "NEUERNAME";
    public static final String nachricht = "NACHRICHT";
    public static final String anAlle = "ANALLE";
    public static final String fluester = "FLUESTER";
    public static final String anEinen = "ANEINEN";
    public static final String nichtVerbunden = "NICHTVERBUNDEN";
    public static final String getrennt = "GETRENNT";
    public static final String split = "§§";

    private InteractionPanelHandlerServer panelHandler;
    private List<Client> clients;

    public TestServer(int pPort, InteractionPanelHandlerServer panel) {
        super(pPort);
        clients = new List<>();
        this.panelHandler = panel;

        if(isOpen()) {
            panelHandler.buttonSwitch();
        }else{
            panelHandler.showErrorMessageContent("Es gab ein Problem beim Starten des Servers.");
        }
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        //Es wird ein neuer String in die Liste clients angehängt, welcher aus der ClientIP und dem ClientPort besteht.
        clients.append(new Client(pClientIP, pClientPort));
        panelHandler.displayNewConnection(pClientIP,pClientPort);

        send(pClientIP, pClientPort, gibName);
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        //Es wird der Inhalt der Message von einem Client mit den Daten des Clients ausgegeben.
        panelHandler.showProcessMessageContent(pClientIP,pClientPort,pMessage);
        String[] mArray = pMessage.split(split);
        if(mArray.length>0) {
            if (mArray[0].equals(name)) {
                if (mArray.length > 1) {
                    boolean nameFrei = true;

                    clients.toFirst();
                    while (clients.hasAccess()) {
                        if (clients.getContent().name != null && clients.getContent().name.equals(mArray[1])) {
                            nameFrei = false;
                        }
                        clients.next();
                    }

                    if (nameFrei) {
                        clients.toFirst();
                        while (clients.hasAccess()) {
                            if (clients.getContent().ip.equals(pClientIP) && clients.getContent().port == pClientPort && clients.getContent().name==null) {
                                clients.getContent().setName(mArray[1]);
                                sendToAll(verbunden + split + getTime() + split + mArray[1]);
                            }
                            clients.next();
                        }

                        String a = "";
                        clients.toFirst();
                        while (clients.hasAccess()) {
                            a = a + split + clients.getContent().name;
                            clients.next();
                        }
                        send(pClientIP, pClientPort, alleClients + a);
                    } else {
                        send(pClientIP, pClientPort, neuerName);
                    }
                }
            } else if (mArray[0].equals(nachricht)) {
                if (mArray.length > 1) {
                    clients.toFirst();
                    while (clients.hasAccess() && (!clients.getContent().ip.equals(pClientIP) || clients.getContent().port != pClientPort)) {
                        clients.next();
                    }
                    if (clients.hasAccess() && clients.getContent().name != null) {
                        sendToAll(anAlle + split + getTime() + split + clients.getContent().name + split + mArray[1]);
                    } else {
                        sendToAll(anAlle + split + getTime() + split + pClientIP + split + mArray[1]);
                    }
                }
            } else if (mArray[0].equals(fluester)) {
                if (mArray.length > 2) {
                    Client von = null;
                    Client an = null;

                    clients.toFirst();
                    while (clients.hasAccess() && clients.getContent().name!=null) {
                        if (clients.getContent().name.equals(mArray[1])) {
                            an = clients.getContent();
                        }
                        if (clients.getContent().ip.equals(pClientIP) && clients.getContent().port == pClientPort) {
                            von = clients.getContent();
                        }
                        clients.next();
                    }

                    if (von != null && an != null) {
                        send(an.ip, an.port, anEinen + split + getTime() + split + von.name + split + mArray[2]);
                        send(von.ip, von.port, anEinen + split + getTime() + split + von.name + split + mArray[2]);
                    } else {
                        send(pClientIP, pClientPort, nichtVerbunden);
                    }
                }
            }
        }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        //Jeder String in der Liste clients, welcher die IP von den Parametern enthält, wird entfernt.
        clients.toFirst();
        while (clients.hasAccess()){
            if(clients.getContent().ip.equals(pClientIP) && clients.getContent().port == pClientPort && clients.getContent().name != null){
                sendToAll(getrennt+split+getTime()+split+clients.getContent().name);
                clients.remove();
            }else if(clients.getContent().ip.equals(pClientIP) && clients.getContent().port == pClientPort && clients.getContent().name== null){
                clients.remove();
            }else{
                clients.next();
            }
        }

        panelHandler.displayClosingConnection(pClientIP, pClientPort);
    }

    /**
     * Sobald der Server geschlossen wird, werden die meisten Knöpfe wieder deaktiviert.
     */
    @Override
    public void close(){
        super.close();
        panelHandler.buttonSwitch();
    }

	/**
     * Jeder Client wird in der Liste clients geführt.
     * Diese Methode gibt eine Darstellung aller Clients in der Form "IP:Port" als String-Array zurück.
     * @return String-Array mit Client-Informationen
     */
    public String[] getClients(){
        int count = 0;
        clients.toFirst();
        while (clients.hasAccess()){
            count++;
            clients.next();
        }
        if(count>0) {
            String[] o = new String[count];
            clients.toFirst();
            for (int i = 0; clients.hasAccess(); i++) {
                o[i] = clients.getContent().ip+ ": " + clients.getContent().port+ ": " + clients.getContent().name;
                clients.next();
            }
            return o;
        }

        return new String[]{"0000:0000"};
    }

    public static String getTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
