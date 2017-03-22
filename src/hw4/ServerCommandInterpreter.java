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
            DataOutputStream dout = new DataOutputStream(inSocket.getOutputStream());

            String command = din.readUTF();
            System.out.println("Received command: " + command);
            interpretCommand(command);

        } catch (IOException e) {
            System.err.println("IOException in ServerCommandInterpreter.run: ");
            e.printStackTrace();
        }
    }

    private void interpretCommand(String command) {
        String source = command.toLowerCase().trim().split(" ")[0];
        String body = command.substring(command.indexOf(" ") + 1);

    }
}
