package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;

import java.util.HashSet;
import java.util.Objects;

public class NodoCompeticion implements Comparable<NodoCompeticion> {
    public Pair pos;
    public int coste, heuristica;
    public NodoCompeticion padre;
    public HashSet<Pair> gemas_capturadas;
    public ACTIONS accion_padre;

    public NodoCompeticion(Pair pos, NodoCompeticion padre, ACTIONS accion_padre) {
        this.pos = pos;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        if(!padre.gemas_capturadas.isEmpty())  // Si el padre tiene gemas capturadas, las copiamos
            this.gemas_capturadas = new HashSet<Pair>(padre.gemas_capturadas);
        else
            this.gemas_capturadas = new HashSet<Pair>();
    }
    public NodoCompeticion(Pair pos, int coste) {
        this.pos = pos;
        this.heuristica = 0;
        this.padre = null;
        this.gemas_capturadas = new HashSet<Pair>();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)  // Si son el mismo objeto, devolvemos true
            return true;
        if (!(o instanceof NodoCompeticion))   // Si no es un nodo, devolvemos false
            return false;
        NodoCompeticion n = (NodoCompeticion) o;

        // Comparamos las posiciones, las capas actuales y las gemas obtenidas
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.gemas_capturadas.equals(n.gemas_capturadas));
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.pos.x, this.pos.y, this.gemas_capturadas);
    }
    @Override
    public int compareTo(NodoCompeticion o) {
        int cmp = Integer.compare(this.heuristica, o.heuristica);
        return cmp;
    }
    
};