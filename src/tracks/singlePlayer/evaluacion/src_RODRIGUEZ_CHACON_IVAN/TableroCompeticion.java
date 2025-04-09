package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;
import core.game.Observation;
import java.util.ArrayList;
import java.util.HashSet;
import core.game.StateObservation;

public class TableroCompeticion {
    private ArrayList<Observation>[][] grid;
    public Pair pos_inicial;
    private Pair salida;
    public HashSet<Pair> gemas;
    public HashSet<Pair> monstruos;

    /**
     * Constructor de la clase TableroCompeticion
     * @param stateObs Estado de la observación
     */
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
    /**
     * Genera el nodo inicial para la busqueda de A*
     * @param inicial Nodo actual
     * @return  Nodo inicial para la busqueda (copia con coste reseteado, heuristica calculada y padre nulo)
     */
    public NodoCompeticion getNodoInicial(NodoCompeticion inicial) {
        NodoCompeticion nodo = new NodoCompeticion(inicial.pos, 0);
        nodo.vista = inicial.vista; // Asignamos la vista al nodo inicial
        nodo.gemas_capturadas = inicial.gemas_capturadas;
        nodo.calculateHeuristica(inicial.pos, this.gemas); // Calculamos la heurística del nodo inicial
        return nodo;
    }
    /**
     * Comprueba si el nodo es la salida y si se tiene el requisito para salir (tenemos todas las gemas)
     * @param nodo  Nodo a comprobar
     * @return  true si el nodo es la salida y se tienen todas las gemas necesarias, false en caso contrario
     */
    public boolean esSalida(NodoCompeticion nodo) {
        return (this.salida.equals(nodo.pos) && nodo.gemas_capturadas.size()>=NodoCompeticion.GEMAS_NECESARIAS); // Comprobamos si el nodo es la salida y si ha recogido todas las gemas
    }
    /**
     * Comprueba si la posición es transitable
     * @param pos Posición a comprobar
     * @return true si la posición es transitable, false en caso contrario
     */
    public boolean isTransitable(Pair pos){
        if(pos.x < 0 || pos.x >= this.grid[0].length || pos.y < 0 || pos.y >= this.grid.length) // Si la posición está fuera del grid, no es transitable
            return false;
        if (this.grid[pos.y][pos.x].isEmpty())  // Si la posición está vacía, es transitable
            return true;
        int tipo = this.grid[pos.y][pos.x].get(0).itype;
        return (tipo != 0 && tipo != 8); // Si no es un muro ni una trampa, es transitable
    }
    /**
     * Comprueba si la posición es transitable en función de la acción
     * @param pos Posición a comprobar
     * @param accion Acción a realizar
     * @return true si la posición es transitable, false en caso contrario
     */
    public boolean isTransitable(Pair pos, ACTIONS accion) {
        Pair delta = Direcciones.direcciones.get(accion);
        return isTransitable(new Pair(pos.x + delta.x, pos.y + delta.y));
    }
    /**
     * Devuelve la lista de acciones posibles desde el nodo actual
     * @param nodo Nodo actual
     * @return Lista de acciones posibles
     */
    public ArrayList<ACTIONS> getAcciones(NodoCompeticion nodo) {
        ArrayList<ACTIONS> acciones = new ArrayList<ACTIONS>(4);
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
    /**
     * Devuelve la lista de nodos hijos a partir del nodo padre
     * @param nodo Nodo padre
     * @return Lista de nodos hijos
     */
    public ArrayList<NodoCompeticion> getHijos(NodoCompeticion nodo) {
        ArrayList<NodoCompeticion> hijos = new ArrayList<NodoCompeticion>();
        ArrayList<ACTIONS> acciones = getAcciones(nodo);
        for (ACTIONS a : acciones) {
            NodoCompeticion hijo = nodo.applyAction(a, this.gemas); // Aplicamos la acción al nodo padre
            hijos.add(hijo);
        }
        return hijos;
    }

    /**
     * Actualiza el grid de observaciones, las gemas y los monstruos
     * @param stateObs Estado de la observación
     */
    public void update(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();  // Obtenemos el grid de observaciones
        this.monstruos.clear();
        this.gemas.clear();

        // Recorremos el grid y buscamos la posición inicial, la salida y las gemas
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
    
};

// Esta es una lista de los itypes de los objetos que pueden aparecer en el grid
/*
 * 0 = muro
 * 1 = pos_inicial
 * 5 = meta
 * 6 = gema
 * 8 = trampa
 * 11 = escorpion
 * 12 = murcielago
 */
