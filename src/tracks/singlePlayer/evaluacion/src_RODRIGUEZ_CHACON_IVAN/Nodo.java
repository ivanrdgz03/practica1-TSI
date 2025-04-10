package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import ontology.Types.ACTIONS;

public class Nodo implements Comparable<Nodo> {
    public static boolean HEURISTICA_ENABLED = true;    // Habilitar o deshabilitar la heurística
    public static boolean COSTE_ENABLED = true;   // Habilitar o deshabilitar el coste
    private static int CONTADOR = 0;
    private int id;
    Pair pos;
    int coste, heuristica;
    boolean capa_roja, capa_azul;
    Nodo padre;
    ACTIONS accion_padre;
    HashSet<Pair> capas_usadas;

    public Nodo(Pair pos, int coste, Nodo padre, ACTIONS accion_padre) {
        this.pos = pos;
        this.coste = coste;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        this.capa_azul = padre.capa_azul;
        this.capa_roja = padre.capa_roja;
        if (padre.capas_usadas.size() > 0)  // Si el padre tiene capas usadas, las copiamos
            this.capas_usadas = padre.capas_usadas; //Se hace copia de la referencia para optimizar, si se agrega alguna antes se realiza una copia y así optimizamos en espacio y tiempo
        else
            this.capas_usadas = new HashSet<Pair>();
        this.id = Nodo.CONTADOR;    // Asignamos un id único al nodo
        Nodo.CONTADOR++;    
    }

    public Nodo(Pair pos, int coste) {
        this.pos = pos;
        this.heuristica = 0;
        this.coste = coste;
        this.padre = null;
        this.capa_roja = false;
        this.capa_azul = false;
        this.capas_usadas = new HashSet<Pair>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  // Si son el mismo objeto, devolvemos true
            return true;
        if (!(o instanceof Nodo))   // Si no es un nodo, devolvemos false
            return false;
        Nodo n = (Nodo) o;

        // Comparamos las posiciones, las capas actuales y las capas usadas
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.capa_azul == n.capa_azul
                && this.capa_roja == n.capa_roja && this.capas_usadas.equals(n.capas_usadas));
    }

    @Override
    public int compareTo(Nodo n) {
        int cmp = 0;
        if(Nodo.COSTE_ENABLED){ // Si el coste está habilitado, lo comparamos
            cmp = Integer.compare((this.coste + this.heuristica), (n.coste + n.heuristica));
            if (cmp == 0)
                cmp = Integer.compare(this.coste, n.coste);
        }else{  // Si el coste no está habilitado, solo comparamos la heurística
            cmp = Integer.compare(this.heuristica, n.heuristica);
        }
        if (cmp == 0)
            cmp = Integer.compare(this.id, n.id);   //El id nos indica la antiguedad
        return cmp;
    }

    @Override
    public int hashCode() {
        // Usamos el hash de las capas usadas, la posición y las capas actuales
        return Objects.hash(this.pos, this.capa_roja, this.capa_azul, this.capas_usadas.size());
    }
    /**
     * Devuelve la lista de acciones que se han realizado para llegar a este nodo desde el nodo inicial
     * @return  Lista de acciones realizadas
     */
    public ArrayList<ACTIONS> getActions() {
        LinkedList<ACTIONS> acciones = new LinkedList<>();
        //Mientras que el nodo padre no sea null, añadimos la acción padre a la lista de acciones en la primera posicon
        for (Nodo actual = this; actual.padre != null; actual = actual.padre)
            acciones.addFirst(actual.accion_padre);
        return new ArrayList<>(acciones);
    }

    /**
     * Calcula la heurística del nodo en base a la posición de salida solo si la heuristica está habilitada
     * @param salida
     */
    void calculateHeuristic(Pair salida) {
        if (Nodo.HEURISTICA_ENABLED)
            this.heuristica = Math.abs(this.pos.x - salida.x) + Math.abs(this.pos.y - salida.y);
    }
}
