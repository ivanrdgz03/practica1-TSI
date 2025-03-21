package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.ArrayList;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Nodo implements Comparable<Nodo> {
    public Pair pos;
    public int coste;
    public boolean capa_roja, capa_azul;
    public Nodo padre;
    public ACTIONS accion_padre;

    public Nodo(Pair pos, int coste, Nodo padre, ACTIONS accion_padre) {
        this.pos = pos;
        this.coste = coste;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.capa_azul = padre.capa_azul;
        this.capa_roja = padre.capa_roja;
    }

    public Nodo(Pair pos, int coste) {
        this.pos = pos;
        this.coste = coste;
        this.padre = null;
        this.capa_roja = false;
        this.capa_azul = false;
    }

    public boolean equals(Nodo n) {
        return (this.pos.x == n.pos.x && this.pos.y == n.pos.y && this.capa_azul == n.capa_azul && this.capa_roja == n.capa_roja);
    }

    @Override
    public int compareTo(Nodo n) {
        int compare = Integer.compare(this.coste, n.coste);
        if (compare == 0)
            compare = this.pos.compareTo(n.pos);
            if(compare == 0)
                compare = Boolean.compare(this.capa_azul, n.capa_azul);
                if(compare == 0)
                    compare = Boolean.compare(this.capa_roja, n.capa_roja);
        return compare;
    }

    public ArrayList<Nodo> getHijos(ArrayList<ACTIONS> acciones) {
        ArrayList<Nodo> hijos = new ArrayList<Nodo>();
        for (ACTIONS a : acciones) {
            Pair newPos = new Pair(this.pos);
            switch (a) {
                case ACTION_UP:
                    newPos.x -= 1;
                    break;
                case ACTION_DOWN:
                    newPos.x += 1;
                    break;
                case ACTION_LEFT:
                    newPos.y -= 1;
                    break;
                case ACTION_RIGHT:
                    newPos.y += 1;
                default:
                    break;
            }
            hijos.add(new Nodo(newPos, coste + 1, this, a));
        }
        return hijos;
    }

    public ArrayList<ACTIONS> getActions() {
        ArrayList<ACTIONS> acciones = new ArrayList<ACTIONS>();
        Nodo actual = this;
        while (actual.padre != null) {
            acciones.add(actual.accion_padre);
            actual = actual.padre;
        }
        Collections.reverse(acciones);
        return acciones;
    }
}
