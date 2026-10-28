package scuola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        //CONTENITORI DATI
        List<String> usernames = new ArrayList<>();
        Random rand = new Random();
        List<Integer> biglietti = new ArrayList<>();
        biglietti.add(100-rand.nextInt(101));
        biglietti.add(100-biglietti.get(0)-rand.nextInt(101-biglietti.get(0)));
        biglietti.add(100-biglietti.get(0)-biglietti.get(1));
        
        // INIZIALIZZAZIONE SERVER
        System.out.println("Hello world!");
        ServerSocket ss = new ServerSocket(3000);
        Socket mySocket = ss.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);

        out.println("WELCOME");
        
        //INTERAZIONE UTENTE
        String loginUsername;
        while (true) {
            loginUsername = in.readLine();
            String[] parti = loginUsername.split(" ");
            //CONTROLLO SINTASSI COMANDO
            if(parti.length==2 && parti[0].equals("LOGIN")){
                if(!usernames.contains(parti[1])){
                    usernames.add(parti[1]);
                    break;
                }
                out.println("ERR USERINUSE");
            }else{
                //NEL CASO IL COMANDO "LOGIN" NON CONTENGA "username"   
                if(parti[0].equals("LOGIN")){
                    out.println("ERR SYNTAX");
                }else{
                    //NEL CASO IL COMANDO NON ESISTA
                    out.println("ERR UNKNOWNCMD");
                }
            }
        }
        out.println("OK");
        
        boolean quit = false;
        do{
            String comandoClient = in.readLine();
            switch (comandoClient) {
                case "BUY":
                    String ordine = in.readLine();
                    String[] parti = ordine.split(" ",2);
                    //andrebbe inserito dentro i singoli cases, perche se invio un altro comando 
                    //invece che un tipo deve dare ERR UNKNOWNCOMAND, poi se ce un tipo senza valore da ERR SYNTAX
                    if(parti.length==2){
                        int qtaOrdine = Integer.parseInt(parti[1]);
                        try {
                        } catch (Exception e) {
                            qtaOrdine = Integer.parseInt(parti[1]);
                            out.println("ERR SYNTAX");
                            break;
                        }
                        int calcoloBiglietti = 0;
                        //CONTROLLO TIPO DI BIGLIETTO
                        switch (parti[0]) {
                            case "Gold":
                                calcoloBiglietti = biglietti.get(0)-qtaOrdine;
                                if(calcoloBiglietti >= 0){
                                    biglietti.set(0, calcoloBiglietti);
                                    out.println("OK");
                                }else{
                                    out.println("KO");
                                }
                                break;
                            case "Pit":
                                calcoloBiglietti = biglietti.get(0)-qtaOrdine;
                                if(calcoloBiglietti >= 0){
                                    biglietti.set(1, calcoloBiglietti);
                                    out.println("OK");
                                }else{
                                    out.println("KO");
                                }
                                break;
                            case "Parterre":
                                calcoloBiglietti = biglietti.get(0)-qtaOrdine;
                                if(calcoloBiglietti >= 0){
                                    biglietti.set(2, calcoloBiglietti);
                                    out.println("OK");
                                }else{
                                    out.println("KO");
                                }
                                break;
                            default:
                                out.println("ERR UNKNOWNCMD");
                                break;
                        }
                        break;
                    }
                    out.println("ERR SYNTAX");
                    break;
                case "N":
                    out.println("AVAIL Gold:" + biglietti.get(0) + " Pit:" + biglietti.get(1) + " Parterre:" + biglietti.get(2));
                    break;
                case "QUIT":
                    out.println("BYE");
                    quit = true;
                    break;
                default:
                    out.println("ERR UNKNOWNCMD");
                    break;
            }
        }while(quit == false);
        usernames.remove(usernames.indexOf(loginUsername));
    }
}