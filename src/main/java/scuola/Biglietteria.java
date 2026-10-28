package scuola;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Biglietteria {
    private List<Integer> biglietti = new ArrayList<>();
    public Biglietteria() {
        //CONTENITORI DATI
        Random rand = new Random();
        biglietti.add(100-rand.nextInt(101));
        biglietti.add(100-biglietti.get(0)-rand.nextInt(101-biglietti.get(0)));
        biglietti.add(100-biglietti.get(0)-biglietti.get(1));
    }
    public List<Integer> getArrayList() {
        return biglietti;
    }
    public synchronized boolean acquista(int indice, int quantita) {
        int disponibili = biglietti.get(indice);
        if (quantita > disponibili) {
            return false;
        }
        biglietti.set(indice, disponibili - quantita);
        return true;
    }
    public synchronized String getDisponibili() {
        return "Gold:" + biglietti.get(0)
             + " Pit:" + biglietti.get(1)
             + " Parterre:" + biglietti.get(2);
    }
}
