package scuola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread {
    private Biglietteria biglietteria;
    private Socket mySocket;
    private List<String> utentiLoggati;
    private String usernameUtente;
    private boolean loggedIn = false;
    private boolean quit = false;
    private BufferedReader in;
    private PrintWriter out;
    public ClientThread(Socket socket, List<String> users, Biglietteria biglietteria) {
        this.mySocket = socket;
        this.utentiLoggati = users;
        this.biglietteria = biglietteria;
    }
    public void run(){
        try {
            this.in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            this.out = new PrintWriter(mySocket.getOutputStream(), true);
            while(!quit){
                comandiUtente();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Process the input from the client
    }


    public void comandiUtente() throws IOException {
        String inputComando = in.readLine();
        if (inputComando == null) {          // il client si è disconnesso senza QUIT
            quit = true;
            return;
        }
        String[] partiComando = inputComando.split(" ");
        String comando = partiComando[0];
        if(comando.equals("QUIT")){
            terminaCollegamento();
            quit = true;
        }
        if (!loggedIn) {
            switch (comando) {
                case "LOGIN":
                    loggedIn = login(partiComando);
                    break;
                case "N":
                case "BUY":
                    out.println("ERR LOGINREQUIRED");
                    break;
                default:
                    out.println("ERR UNKNOWNCMD");
            }
            return;
        }
            switch(comando){
                case "LOGIN":
                    if(loggedIn == true) {
                        out.println("ERR ALREADYLOGGEDIN");
                    }
                    break;
                case "N":
                    if(partiComando.length==1){
                        stampaNumeroBigliettiDisponibili();
                    } else {
                        out.println("ERR SYNTAX");
                    }
                    break;
                case "BUY":
                    if(partiComando.length==1){
                        compraBiglietti();
                    } else {
                        out.println("ERR SYNTAX");
                    }
                    break;
                default:
                    out.println("ERR UNKNOWNCOMMAND");
            }
        
    }
    public synchronized boolean login(String[] partiComando) throws IOException {

        //CONTROLLO SINTASSI COMANDO
        if(partiComando.length!=2){
            //NEL CASO IL COMANDO "LOGIN" NON CONTENGA "username"   
            if(partiComando[0].equals("LOGIN")){
                out.println("ERR SYNTAX");
            }else{
                //NEL CASO IL COMANDO NON ESISTA
                out.println("ERR UNKNOWNCMD");
            }
        }
        String username = partiComando[1];
        synchronized(utentiLoggati){
            if(utentiLoggati.contains(username)) {
                out.println("ERR USERINUSE");
                return false;
            } else {
                utentiLoggati.add(username);
            }
        }
        usernameUtente = username;
        out.println("OK");
        return true;        
    }
    public void stampaNumeroBigliettiDisponibili() {
        out.println(biglietteria.getDisponibili());
    }

    public synchronized void compraBiglietti() throws IOException {
        String ordine = in.readLine();
        if (ordine == null) {
            quit = true;
            return;
        }
        String[] partiInput = ordine.split(" ", 2);
        String tipoBiglietto = partiInput[0];
        int numeroBiglietti = Integer.parseInt(partiInput[1]);
        switch(tipoBiglietto){
            case "Gold":
                if(biglietteria.acquista(0, numeroBiglietti)){
                    out.println("OK");
                } else {
                    out.println("KO");
                }
                break;
            case "Pit":
                if(biglietteria.acquista(1, numeroBiglietti)){
                    out.println("OK");
                } else {
                    out.println("KO");
                }
                break;
            case "Parterre":
                if(biglietteria.acquista(2, numeroBiglietti)){
                    out.println("OK");
                } else {
                    out.println("KO");
                }
                break;
            default:
                out.println("ERR SYNTAX");
        }
    }
    public void terminaCollegamento() throws IOException {
        out.println("BYE");
        if (loggedIn == true) {
            utentiLoggati.remove(utentiLoggati.indexOf(usernameUtente));
        }
        mySocket.close();
    }
}