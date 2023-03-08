import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private int clientNumber;
    private String clientName;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients, int clientNumber) {
        this.clientSocket = socket;
        this.clients = clients;
        this.clientNumber = clientNumber;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // Ask for client name
            out.println("Enter your name:");
            clientName = in.readLine();
            out.println("Welcome, " + clientName + "!");
            System.out.println(clientName + " has joined the chat.");

            // Send notification to all clients that a new client has joined
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(clientName + " has joined the chat.");
                }
            }

            // Listen for messages from the client
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send the message to all clients except the sender
                for (ClientHandler client : clients) {
                    if (client != this) {
                        client.sendMessage(clientName + ": " + inputLine);
                    }
                }
            }

            // Send notification to all clients that the client has left
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(clientName + " has left the chat.");
                }
            }

            // Remove the client handler from the list of clients
            clients.remove(this);

            // Close the socket and streams
            out.close();
            in.close();
            clientSocket.close();

            System.out.println(clientName + " hasleft the chat.");
        } catch (IOException e) {
            System.out.println("Error in clientHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
