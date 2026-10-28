package scuola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class Biglietteria extends Thread {
    Random rand = new Random();
    int goldBiglietti = rand.nextInt(101);
    int pitBiglietti = rand.nextInt(101-goldBiglietti);
    int parterreBiglietti = rand.nextInt(101-goldBiglietti-pitBiglietti);
    Socket mySocket;
    List<String> utentiLoggati;
    String usernameUtente;
    boolean loggedIn = false;
    boolean quit = false;
    BufferedReader in;
    PrintWriter out;
    public Biglietteria(Socket socket, List<String> users){
        this.mySocket = socket;
        this.utentiLoggati = users;
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
        String[] partiComando = inputComando.split(" ", 2);
        if(partiComando[0].equals("QUIT")){
            terminaCollegamento();
            quit = true;
        }
        if(loggedIn == false && !partiComando[0].equals("LOGIN")){
            out.println("ERR LOGINREQUIRED");
        } else {
            switch(partiComando[0]){
                case "LOGIN":
                    if(loggedIn == true) {
                        out.println("ERR ALREADYLOGGEDIN");
                    } else {
                        loggedIn = login(partiComando[1]);
                    }
                    break;
                case "N":
                    stampaNumeroBigliettiDisponibili();
                    break;
                case "BUY":
                    compraBiglietti();
                    break;
                default:
                    out.println("ERR UNKNOWNCOMMAND");
            }
        }
    }

    public synchronized boolean login(String username) throws IOException {
        if(utentiLoggati.contains(username)) {
            out.println("ERR USERINUSE");
            return false;
        } else {
            utentiLoggati.add(username);
            usernameUtente = username;
            out.println("OK");
            return true;
        }

    }
    public void stampaNumeroBigliettiDisponibili() {
        out.println("AVAIL Gold: " + goldBiglietti + " Pit: " + pitBiglietti + " Parterre: " + parterreBiglietti);
    }

    public synchronized void compraBiglietti() throws IOException {
        String input = in.readLine();
        String[] partiInput = input.split(" ", 2);
        String tipoBiglietto = partiInput[0];
        int numeroBiglietti = Integer.parseInt(partiInput[1]);
        switch(tipoBiglietto){
            case "Gold":
                if(numeroBiglietti <= goldBiglietti){
                    goldBiglietti -= numeroBiglietti;
                    out.println("OK");
                } else {
                    out.println("KO");
                }
                break;
            case "Pit":
                if(numeroBiglietti <= pitBiglietti){
                    pitBiglietti -= numeroBiglietti;
                    out.println("OK");
                } else {
                    out.println("KO");
                }
                break;
            case "Parterre":
                if(numeroBiglietti <= parterreBiglietti){
                    parterreBiglietti -= numeroBiglietti;
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