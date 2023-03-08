import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private ArrayList<ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
        this.clientName = "Client " + (clients.size() + 1);

        try {
            this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error getting input/output streams: " + ex.getMessage());
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.output.println(clientName + ": " + message);
            }
        }
    }

    @Override
    public void run() {
        try {
            output.println("Enter your name:");
            clientName = input.readLine();
            output.println("Welcome, " + clientName + "!");

            while (true) {
                String message = input.readLine();
                if (message == null) {
                    break;
                }
                sendMessage(message);
            }

            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Error handling client " + clientName + ": " + ex.getMessage());
        }
    }
}
