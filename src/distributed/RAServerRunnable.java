package distributed;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by joshuasmith on 3/28/17.
 */
public class RAServerRunnable implements Runnable {
    private RAServer server;
    private Socket socket;

    public RAServerRunnable(RAServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());
            String msg = din.readUTF().trim();
            System.out.println("[DEBUG]: Received message \"" + msg + "\"");
            interpretMessage(msg);
        } catch (IOException e) {
            System.out.println("[ERROR]: IOException while reading message");
        }
    }

    private void interpretMessage(String msg) {
        String[] tokens = msg.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < tokens.length - 1; i++) {
            builder.append(tokens[i]);
            builder.append(" ");
        }
        builder.append(tokens[tokens.length - 1]);
        String msgBody = builder.toString();

        if (tokens[0].equals(RAClient.TAG)) {

        }

        else if (tokens[0].equals(RAServer.TAG)) {

        }

        else {
            System.out.println("[ERROR]: Invalid message tag \"" + tokens[0] + "\"");
        }
    }
}
