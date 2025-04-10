package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class NodoCompeticion implements Comparable<NodoCompeticion> {
    static final int GEMAS_NECESARIAS = 9; // Número de gemas necesarias para salir
    public Pair pos;
    public int coste, heuristica;
    public NodoCompeticion padre;
    public HashSet<Pair> gemas_capturadas;
    public ACTIONS accion_padre;
    public ACTIONS vista;

    /**
     * Constructor de un nodo
     * @param pos Posicion del nodo
     * @param padre Padre del nodo
     * @param accion_padre Accion del padre
     * @param vista_hijo Hacia donde mira el hijo
     */
    public NodoCompeticion(Pair pos, NodoCompeticion padre, ACTIONS accion_padre, ACTIONS vista_hijo) {
        this.pos = pos;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        this.coste = padre.coste + 1;
        this.vista = vista_hijo;
        if (!padre.gemas_capturadas.isEmpty()) // Si el padre tiene gemas capturadas, las referenciamos
            this.gemas_capturadas = padre.gemas_capturadas;
        else
            this.gemas_capturadas = new HashSet<Pair>();
    }

    /**
     * Constructor de un nodo
     * @param pos Posicion del nodo
     * @param coste Coste del nodo
     */
    public NodoCompeticion(Pair pos, int coste) {
        this.pos = pos;
        this.heuristica = 0;
        this.padre = null;
        this.coste = coste;
        this.vista = ACTIONS.ACTION_RIGHT;  //Por defecto a la derecha
        this.gemas_capturadas = new HashSet<Pair>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) // Si son el mismo objeto, devolvemos true
            return true;
        if (!(o instanceof NodoCompeticion)) // Si no es un nodo, devolvemos false
            return false;
        NodoCompeticion n = (NodoCompeticion) o;

        // Comparamos las posiciones, las capas actuales y las gemas obtenidas
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.gemas_capturadas.equals(n.gemas_capturadas)
                && this.vista == n.vista);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pos.x, this.pos.y, this.gemas_capturadas.size(), this.vista);
    }

    @Override
    public int compareTo(NodoCompeticion o) {
        int cmp = Integer.compare(this.heuristica + this.coste, o.heuristica + o.coste);
        if (cmp == 0)
            cmp = Integer.compare(this.coste, o.coste);
        return cmp;
    }
    /**
     * Selecciona la gema más cercana a la posición actual que aun no fue cogida
     * @param start Posición actual
     * @param allGems   Conjunto de gemas
     * @return La gema más cercana a la posición actual
     */
    private Pair selectBestGem(Pair start, Set<Pair> allGems) {
        int minDist = Integer.MAX_VALUE;
        Pair best = null;
        for (Pair gem : allGems) {
            if (!this.gemas_capturadas.contains(gem)) { // Si la gema no ha sido cogida
                int dist = Math.abs(start.x - gem.x) + Math.abs(start.y - gem.y);
                if (dist < minDist) {
                    minDist = dist;
                    best = gem;
                }
            }
        }
        return best;
    }
    /**
     * Calcula la heurística del nodo
     * @param salida Posición de salida
     * @param gemas Conjunto de todas las gemas
     */
    public void calculateHeuristica(Pair salida, Set<Pair> gemas) {
        Pair posicion = this.pos;
        Pair gema_cercana = selectBestGem(posicion, gemas);
        if (gemas.size() < NodoCompeticion.GEMAS_NECESARIAS && gema_cercana != null) { //Si no tenemos todas las gemas se calcula en funcion de la gema mas cercana si es que quedan gemas libres
            this.heuristica = Math.abs(posicion.x - gema_cercana.x) + Math.abs(posicion.y - gema_cercana.y);
        } else {    // Si tenemos todas las gemas se calcula en funcion de la salida
            this.heuristica = Math.abs(posicion.x - salida.x) + Math.abs(posicion.y - salida.y);
        }
    }
    /**
     * Genera un nuevo nodo hijo a partir de la accion y las gemas existentes (requeridas para la heuristica)
     * @param accion Accion a realizar
     * @param gemas Conjunto de gemas
     * @return  Nodo hijo
     */
    public NodoCompeticion applyAction(ACTIONS accion, Set<Pair> gemas) {
        Pair pos_hijo = null;
        ACTIONS vista_hijo = this.vista;
        Pair delta = Direcciones.direcciones.get(accion);
        if(this.vista == accion)
            pos_hijo = new Pair(this.pos.x + delta.x, this.pos.y + delta.y);
        else{
            pos_hijo = new Pair(this.pos.x, this.pos.y);
            vista_hijo = accion;
        }
        NodoCompeticion nodo_hijo = new NodoCompeticion(pos_hijo, this, accion, vista_hijo);
        if (gemas.contains(pos_hijo) && !this.gemas_capturadas.contains(pos_hijo)) {
            nodo_hijo.gemas_capturadas = new HashSet<Pair>(this.gemas_capturadas);
            nodo_hijo.gemas_capturadas.add(pos_hijo);
        }
        return nodo_hijo;
    }
    /**
     * Devuelve la lista de acciones desde el nodo inicial hasta el nodo actual
     * @return  Lista de acciones
     */
    public LinkedList<ACTIONS> getActions() {
        LinkedList<ACTIONS> actions = new LinkedList<>();
        NodoCompeticion nodo = this;
        while (nodo.padre != null) {
            actions.addFirst(nodo.accion_padre);
            nodo = nodo.padre;
        }
        return actions;
    }
};