package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Nodo implements Comparable<Nodo> {
    public static boolean HEURISTICA_ENABLED = true;
    public static int CONTADOR = 0;
    public Pair pos;
    public int coste, heuristica;
    public boolean capa_roja, capa_azul;
    public Nodo padre;
    public ACTIONS accion_padre;
    public HashSet<Pair> capas_usadas;
    private int id;

    public Nodo(Pair pos, int coste, Nodo padre, ACTIONS accion_padre) {
        this.pos = pos;
        this.coste = coste;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        this.capa_azul = padre.capa_azul;
        this.capa_roja = padre.capa_roja;
        if (padre.capas_usadas.size() > 0)
            this.capas_usadas = new HashSet<Pair>(padre.capas_usadas);
        else
            this.capas_usadas = new HashSet<Pair>();
        this.id = Nodo.CONTADOR;
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
        if (this == o)
            return true;
        if (!(o instanceof Nodo))
            return false;
        Nodo n = (Nodo) o;
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.capa_azul == n.capa_azul
                && this.capa_roja == n.capa_roja && this.capas_usadas.equals(n.capas_usadas));
    }

    @Override
    public int compareTo(Nodo n) {
        int cmp = Integer.compare((this.coste + this.heuristica), (n.coste + n.heuristica));
        if (cmp == 0)
            cmp = Integer.compare(this.coste, n.coste);
        if (cmp == 0)
            cmp = Integer.compare(this.id, n.id);
        return cmp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pos, this.capa_roja, this.capa_azul, this.capas_usadas.size());
    }

    public ArrayList<ACTIONS> getActions() {
        LinkedList<ACTIONS> acciones = new LinkedList<>();
        for (Nodo actual = this; actual.padre != null; actual = actual.padre)
            acciones.addFirst(actual.accion_padre);
        return new ArrayList<>(acciones);
    }

    public void calculateHeuristic(Pair salida) {
        if (Nodo.HEURISTICA_ENABLED)
            this.heuristica = Math.abs(this.pos.x - salida.x) + Math.abs(this.pos.y - salida.y);
    }
}
