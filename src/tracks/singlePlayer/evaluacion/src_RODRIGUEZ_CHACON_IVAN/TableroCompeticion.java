package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;
import core.game.Observation;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import core.game.StateObservation;

public class TableroCompeticion {
    private ArrayList<Observation>[][] grid;
    public Pair pos_inicial;
    private Pair salida;
    public HashSet<Pair> gemas;
    public HashSet<Pair> monstruos;

    public TableroCompeticion(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();  // Obtenemos el grid de observaciones
        this.pos_inicial = null;
        this.salida = null;
        this.gemas = new HashSet<Pair>();
        this.monstruos = new HashSet<Pair>();
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
                        case 12: this.monstruos.add(new Pair(j, i)); break;
                    }
                }
    }
    public NodoCompeticion getNodoInicial(NodoCompeticion inicial, ACTIONS vista) {
        NodoCompeticion nodo = new NodoCompeticion(inicial.pos, 0);
        nodo.vista = vista; // Asignamos la vista al nodo inicial
        nodo.gemas_capturadas = inicial.gemas_capturadas;
        nodo.calculateHeuristica(inicial.pos, new ArrayList<Pair>(this.gemas)); // Calculamos la heurística del nodo inicial
        return nodo;
    }
    public boolean esSalida(NodoCompeticion nodo) {
        return (this.salida.equals(nodo.pos) && nodo.gemas_capturadas.size()>=9); // Comprobamos si el nodo es la salida y si ha recogido todas las gemas
    }
    public boolean isTransitable(Pair pos){
        if(pos.x < 0 || pos.x >= this.grid[0].length || pos.y < 0 || pos.y >= this.grid.length) // Si la posición está fuera del grid, no es transitable
            return false;
        if (this.grid[pos.y][pos.x].isEmpty())  // Si la posición está vacía, es transitable
            return true;
        int tipo = this.grid[pos.y][pos.x].get(0).itype;
        return (tipo != 0 && tipo != 8); // Si no es un muro ni una trampa, es transitable
    }
    public boolean isTransitable(Pair pos, ACTIONS accion) {
        Pair pos_hijo = null;
        switch (accion) {
            case ACTION_UP: pos_hijo = new Pair(pos.x - 1, pos.y); break;
            case ACTION_DOWN: pos_hijo = new Pair(pos.x + 1, pos.y); break;
            case ACTION_LEFT: pos_hijo = new Pair(pos.x, pos.y - 1); break;
            case ACTION_RIGHT: pos_hijo = new Pair(pos.x, pos.y + 1); break;
        }
        return isTransitable(pos_hijo);
    }
    public ArrayList<ACTIONS> getAcciones(NodoCompeticion nodo) {
        ArrayList<ACTIONS> acciones = new ArrayList<ACTIONS>();
        if (isTransitable(nodo.pos, ACTIONS.ACTION_RIGHT))
            acciones.add(ACTIONS.ACTION_RIGHT);
        if (isTransitable(nodo.pos, ACTIONS.ACTION_LEFT))
            acciones.add(ACTIONS.ACTION_LEFT);
        if (isTransitable(nodo.pos, ACTIONS.ACTION_UP)) 
            acciones.add(ACTIONS.ACTION_UP);
        if (isTransitable(nodo.pos, ACTIONS.ACTION_DOWN))
            acciones.add(ACTIONS.ACTION_DOWN);
        return acciones;
    }
    public ArrayList<NodoCompeticion> getHijos(NodoCompeticion nodo) {
        ArrayList<NodoCompeticion> hijos = new ArrayList<NodoCompeticion>();
        ArrayList<ACTIONS> acciones = getAcciones(nodo);
        for (ACTIONS a : acciones) {
            NodoCompeticion hijo = nodo.applyAction(a, new ArrayList<>(this.gemas)); // Aplicamos la acción al nodo padre
            hijos.add(hijo);
        }
        return hijos;
    }
    public void update(NodoCompeticion nodo, StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();  // Obtenemos el grid de observaciones
        this.monstruos.clear();
        this.gemas.clear();

        for (int i = 1; i < this.grid.length; i++)
            for (int j = 1; j < this.grid[0].length; j++)
                if (!this.grid[i][j].isEmpty()){
                    int tipo = this.grid[i][j].get(0).itype;
                    switch (tipo) {
                        case 6: this.gemas.add(new Pair(j, i)); break;
                        case 11: 
                        case 12: this.monstruos.add(new Pair(j, i)); break;
                    }
                }
    }
    public Pair dangerous(NodoCompeticion actual){
        for(Pair monstruo : this.monstruos){
            if(Math.abs(monstruo.x - actual.pos.x) + Math.abs(monstruo.y - actual.pos.y) == 2){
                return monstruo;
            }
        }
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
