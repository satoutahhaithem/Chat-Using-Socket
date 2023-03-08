import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            int clientNumber = 0;
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, clientNumber);
                clients.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();

                clientNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 6000;
        Server server = new Server();
        server.start(port);
    }
}
