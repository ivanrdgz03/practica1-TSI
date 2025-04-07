package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;
import core.game.Observation;
import java.util.ArrayList;
import java.util.HashSet;
import core.game.StateObservation;

public class TableroCompeticion {
    private ArrayList<Observation>[][] grid;
    private Pair pos_inicial;
    private Pair salida;
    private HashSet<Pair> gemas;
    private HashSet<Pair> monstruos_iniciales;

    public TableroCompeticion(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();  // Obtenemos el grid de observaciones
        this.pos_inicial = null;
        this.salida = null;
        this.gemas = new HashSet<Pair>();
        this.monstruos_iniciales = new HashSet<Pair>();
        // Recorremos el grid y buscamos la posición inicial, la salida y las gemas
        for (int i = 0; i < this.grid.length; i++)
            for (int j = 0; j < this.grid[0].length; j++)
                if (!this.grid[i][j].isEmpty()){
                    int tipo = this.grid[i][j].get(0).itype;
                    switch (tipo) {
                        case 1: this.pos_inicial = new Pair(j, i); break;
                        case 5: this.salida = new Pair(j, i); break;
                        case 6: this.gemas.add(new Pair(j, i)); break;
                        case 11: 
                        case 12: this.monstruos_iniciales.add(new Pair(j, i)); break;
                    }
                }
    }
    public NodoCompeticion getNodoInicial() {
        return new NodoCompeticion(this.pos_inicial, 0);
    }
    public boolean esSalida(Pair pos) {
        return this.salida.equals(pos);
    }
    public boolean isTransitable(Pair pos){
        if(pos.x < 0 || pos.x >= this.grid[0].length || pos.y < 0 || pos.y >= this.grid.length) // Si la posición está fuera del grid, no es transitable
            return false;
        if (this.grid[pos.y][pos.x].isEmpty())  // Si la posición está vacía, es transitable
            return true;
        else {
            int tipo = this.grid[pos.y][pos.x].get(0).itype;
            return (tipo != 0 && tipo != 8); // Si no es un muro ni una trampa, es transitable
        }
    }
    public ArrayList<ACTIONS> getAcciones(Pair pos) {
        ArrayList<ACTIONS> acciones = new ArrayList<ACTIONS>();
        if (isTransitable(new Pair(pos.x, pos.y - 1))) // Arriba
            acciones.add(ACTIONS.ACTION_UP);
        if (isTransitable(new Pair(pos.x, pos.y + 1))) // Abajo
            acciones.add(ACTIONS.ACTION_DOWN);
        if (isTransitable(new Pair(pos.x - 1, pos.y))) // Izquierda
            acciones.add(ACTIONS.ACTION_LEFT);
        if (isTransitable(new Pair(pos.x + 1, pos.y))) // Derecha
            acciones.add(ACTIONS.ACTION_RIGHT);
        return acciones;
    }
    public ArrayList<NodoCompeticion> getHijos(NodoCompeticion nodo) {
        ArrayList<NodoCompeticion> hijos = new ArrayList<NodoCompeticion>();
        ArrayList<ACTIONS> acciones = getAcciones(nodo.pos);
        for (ACTIONS a : acciones) {
            Pair pos_hijo = null;
            switch (a) {
                case ACTION_UP: pos_hijo = new Pair(nodo.pos.x, nodo.pos.y - 1); break;
                case ACTION_DOWN: pos_hijo = new Pair(nodo.pos.x, nodo.pos.y + 1); break;
                case ACTION_LEFT: pos_hijo = new Pair(nodo.pos.x - 1, nodo.pos.y); break;
                case ACTION_RIGHT: pos_hijo = new Pair(nodo.pos.x + 1, nodo.pos.y); break;
            }
            NodoCompeticion hijo = new NodoCompeticion(pos_hijo, nodo, a);
            if(this.gemas.contains(pos_hijo) && !hijo.gemas_capturadas.contains(pos_hijo)) { // Si el hijo tiene una gema, la añadimos a las gemas capturadas
                hijo.gemas_capturadas.add(pos_hijo);
            }
            hijos.add(hijo);
        }
        return hijos;
    }
    public void update(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();  // Obtenemos el grid de observaciones
        this.gemas.clear();
        this.monstruos_iniciales.clear();
        // Recorremos el grid y buscamos la posición inicial, la salida y las gemas
        for (int i = 0; i < this.grid.length; i++)
            for (int j = 0; j < this.grid[0].length; j++)
                if (!this.grid[i][j].isEmpty()){
                    int tipo = this.grid[i][j].get(0).itype;
                    switch (tipo) {
                        case 6: this.gemas.add(new Pair(j, i)); break;
                        case 11: 
                        case 12: this.monstruos_iniciales.add(new Pair(j, i)); break;
                    }
                }
    }
    public Pair dangerous(Nodo actual){
        return null;
    }
    
};


/*
 * 0 = muro
 * 1 = pos_inicial
 * 5 = meta
 * 6 = gema
 * 8 = trampa
 * 11 = escorpion
 * 12 = murcielago
 */
