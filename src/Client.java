import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void startConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    public static void main(String[] args) {
        Client client = new Client();

        Scanner scanner = new Scanner(System.in);
        String name = "";

        try {
            String ip = "localhost";
            int port = 6000;

            client.startConnection(ip, port);
            System.out.println(client.receiveMessage());

            while (!name.equals("exit")) {
                //System.out.println("Enter your name: ");
                name = scanner.next();

                if (!name.equals("exit")) {
                    client.sendMessage(name);
                    System.out.println(client.receiveMessage());

                    // Listen for messages from the server
                    new Thread(() -> {
                        try {
                            String serverMessage;
                            while ((serverMessage = client.receiveMessage()) != null) {
                                System.out.println(serverMessage);
                            }
                        } catch (IOException e) {
                            System.err.println("Error receiving message from server: " + e.getMessage());
                        }
                    }).start();

                    // Send messages to the server
                    String clientMessage;
                    do {
                        System.out.print("> ");
                        clientMessage = scanner.nextLine();
                        client.sendMessage(clientMessage);
                    } while (!clientMessage.equalsIgnoreCase("exit"));

                    client.stopConnection();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
