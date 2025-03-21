package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.ArrayList;
import java.util.Collections;
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
    public Pair pos;
    public int coste;
    public int heuristica;
    public boolean capa_roja, capa_azul;
    public Nodo padre;
    public ACTIONS accion_padre;

    public Nodo(Pair pos, int coste, Nodo padre, ACTIONS accion_padre) {
        this.pos = pos;
        this.coste = coste;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        this.capa_azul = padre.capa_azul;
        this.capa_roja = padre.capa_roja;
    }

    public Nodo(Pair pos, int coste) {
        this.pos = pos;
        this.heuristica = 0;
        this.coste = coste;
        this.padre = null;
        this.capa_roja = false;
        this.capa_azul = false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nodo)) return false;
        Nodo n = (Nodo) o;
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.capa_azul == n.capa_azul && this.capa_roja == n.capa_roja);
    }

    @Override
    public int compareTo(Nodo n) {
        int cmp = Integer.compare((this.coste+(this.heuristica*(Nodo.HEURISTICA_ENABLED?1:0))), (n.coste+(n.heuristica*(Nodo.HEURISTICA_ENABLED?1:0))));
        if (cmp == 0)
            cmp = Integer.compare(this.pos.x, n.pos.x);
            if (cmp == 0)
                cmp = Integer.compare(this.pos.y, n.pos.y);
                if (cmp == 0)
                    cmp = Boolean.compare(this.capa_roja, n.capa_roja);
                    if (cmp == 0)
                        cmp = Boolean.compare(this.capa_azul, n.capa_azul);
        return cmp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, capa_roja, capa_azul);
    }


    public ArrayList<Nodo> getHijos(ArrayList<ACTIONS> acciones) {
        ArrayList<Nodo> hijos = new ArrayList<Nodo>();
        for (ACTIONS a : acciones) {
            Pair newPos = null;
            switch (a) {
                case ACTION_UP:
                    newPos = new Pair(pos.x - 1, pos.y);
                    break;
                case ACTION_DOWN:
                    newPos = new Pair(pos.x + 1, pos.y);
                    break;
                case ACTION_LEFT:
                    newPos = new Pair(pos.x, pos.y - 1);
                    break;
                case ACTION_RIGHT:
                    newPos = new Pair(pos.x, pos.y + 1);
                default:
                    break;
            }
            hijos.add(new Nodo(newPos, coste + 1, this, a));
        }
        return hijos;
    }

    public ArrayList<ACTIONS> getActions() {
        LinkedList<ACTIONS> acciones = new LinkedList<>();
        for (Nodo actual = this; actual.padre != null; actual = actual.padre)
            acciones.addFirst(actual.accion_padre);
        return new ArrayList<>(acciones);
    }
    public void calculateHeuristic(Pair salida){
        if (Nodo.HEURISTICA_ENABLED)
        this.heuristica = Math.abs(this.pos.x - salida.x) + Math.abs(this.pos.y - salida.y);
    }
}
