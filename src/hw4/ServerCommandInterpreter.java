package hw4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshuasmith on 3/22/17.
 */
public class ServerCommandInterpreter implements Runnable {
    private Socket inSocket;
    private Server server;

    public ServerCommandInterpreter(Server server, Socket inSocket) {
        this.server = server;
        this.inSocket = inSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream din = new DataInputStream(inSocket.getInputStream());
            //DataOutputStream dout = new DataOutputStream(inSocket.getOutputStream());

            String command = din.readUTF();
            String source = command.toLowerCase().trim().split(" ")[0];
            String body = command.substring(command.indexOf(" ") + 1);
            System.out.println("[DEBUG]: Received command: \"" + command + "\"");

            interpretCommand(command);

        } catch (IOException e) {
            System.err.println("IOException in ServerCommandInterpreter.run: ");
            e.printStackTrace();
        }
    }

    /**
     * Interprets a command received from either a server or client node
     * Input command only has params if its from a server node
     * Also removes source token from command string
     * @param command   Has form: "<source> <params> <command>"
     */
    private void interpretCommand(String command) {
        String source = command.toLowerCase().trim().split(" ")[0];
        String body = command.substring(command.indexOf(" ") + 1);
        String response = null;

        if (source.equals(Client.TAG)) {
            server.sendRequest(body);
            response = server.releaseAndGetResponse(body);
            System.out.println("[DEBUG]: Response to be sent to client: \"" + response + "\"");
            try {
                DataOutputStream dout = new DataOutputStream(inSocket.getOutputStream());
                dout.writeUTF(response);
                dout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (source.equals(Server.TAG)) {
            server.processCommandFromServerNode(body);
        }
    }
}
