import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public void start(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            input = new BufferedReader(new InputStreamReader(System.in));
            output = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to server on port " + port);
            System.out.println("Enter your name:");

            String name = input.readLine();
            output.println(name);

            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();

            String message;
            while ((message = input.readLine()) != null) {
                output.println(message);
            }

            output.close();
            input.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Client exception: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 6000;
        Client client = new Client();
        client.start(serverAddress, port);
    }

    private class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(message);
                }

                input.close();
            } catch (IOException ex) {
                System.out.println("ClientThread exception: " + ex.getMessage());
            }
        }
    }
}
