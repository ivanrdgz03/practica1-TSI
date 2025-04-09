package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import ontology.Types.ACTIONS;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class NodoCompeticion implements Comparable<NodoCompeticion> {
    public Pair pos;
    public int coste, heuristica;
    public NodoCompeticion padre;
    public HashSet<Pair> gemas_capturadas;
    public ACTIONS accion_padre;
    public ACTIONS vista;

    public NodoCompeticion(Pair pos, NodoCompeticion padre, ACTIONS accion_padre, ACTIONS vista_hijo) {
        this.pos = pos;
        this.padre = padre;
        this.accion_padre = accion_padre;
        this.heuristica = 0;
        this.coste = padre.coste + 1;
        this.vista = vista_hijo;
        if (!padre.gemas_capturadas.isEmpty()) // Si el padre tiene gemas capturadas, las copiamos
            this.gemas_capturadas = padre.gemas_capturadas;
        else
            this.gemas_capturadas = new HashSet<Pair>();
    }

    public NodoCompeticion(Pair pos, int coste) {
        this.pos = pos;
        this.heuristica = 0;
        this.padre = null;
        this.coste = coste;
        this.vista = ACTIONS.ACTION_RIGHT;
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

    private Pair selectBestGem(Pair start, Set<Pair> allGems) {
        int minDist = Integer.MAX_VALUE;
        Pair best = null;
        for (Pair gem : allGems) {
            if (!this.gemas_capturadas.contains(gem)) {
                int dist = Math.abs(start.x - gem.x) + Math.abs(start.y - gem.y);
                if (dist < minDist) {
                    minDist = dist;
                    best = gem;
                }
            }
        }
        return best;
    }

    public void calculateHeuristica(Pair salida, Set<Pair> gemas) {
        Pair posicion = this.pos;
        if (gemas.size() < 9) {
            Pair gema_cercana = selectBestGem(posicion, gemas);
            this.heuristica = Math.abs(posicion.x - gema_cercana.x) + Math.abs(posicion.y - gema_cercana.y);
        } else {
            this.heuristica = Math.abs(posicion.x - salida.x) + Math.abs(posicion.y - salida.y);
        }
    }

    public NodoCompeticion applyAction(ACTIONS accion, Set<Pair> gemas) {
        Pair pos_hijo = null;
        ACTIONS vista_hijo = this.vista;
        switch (accion) {
            case ACTION_UP:
                if (this.vista == ACTIONS.ACTION_UP)
                    pos_hijo = new Pair(this.pos.x - 1, this.pos.y);
                else {
                    pos_hijo = new Pair(this.pos.x, this.pos.y);
                    vista_hijo = ACTIONS.ACTION_UP;
                }
                break;
            case ACTION_DOWN:
                if (this.vista == ACTIONS.ACTION_DOWN)
                    pos_hijo = new Pair(this.pos.x + 1, this.pos.y);
                else {
                    pos_hijo = new Pair(this.pos.x, this.pos.y);
                    vista_hijo = ACTIONS.ACTION_DOWN;
                }
                break;
            case ACTION_LEFT:
                if (this.vista == ACTIONS.ACTION_LEFT)
                    pos_hijo = new Pair(this.pos.x, this.pos.y - 1);
                else {
                    pos_hijo = new Pair(this.pos.x, this.pos.y);
                    vista_hijo = ACTIONS.ACTION_LEFT;
                }
                break;
            case ACTION_RIGHT:
                if (this.vista == ACTIONS.ACTION_RIGHT)
                    pos_hijo = new Pair(this.pos.x, this.pos.y + 1);
                else {
                    pos_hijo = new Pair(this.pos.x, this.pos.y);
                    vista_hijo = ACTIONS.ACTION_RIGHT;
                }
                break;
            case ACTION_NIL:
                return this;
        }
        NodoCompeticion nodo_hijo = new NodoCompeticion(pos_hijo, this, accion, vista_hijo);
        if (gemas.contains(pos_hijo) && !this.gemas_capturadas.contains(pos_hijo)) {
            nodo_hijo.gemas_capturadas = new HashSet<Pair>(this.gemas_capturadas);
            nodo_hijo.gemas_capturadas.add(pos_hijo);
        }
        return nodo_hijo;
    }

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