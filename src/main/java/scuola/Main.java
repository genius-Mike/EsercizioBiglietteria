package scuola;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> utentiLoggati = new ArrayList<String>();
        Biglietteria biglietteria = new Biglietteria();

        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("Server in ascolto sulla porta 3000");
        while(true){
            Socket socket = serverSocket.accept();
            ClientThread clientSocket = new ClientThread(socket, utentiLoggati, biglietteria);
            clientSocket.start();
        }
    }

}