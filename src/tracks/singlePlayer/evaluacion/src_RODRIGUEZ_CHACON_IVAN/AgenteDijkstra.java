package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.Set;
import java.util.TreeSet;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteDijkstra extends AbstractPlayer {
    private boolean solution;
    private List<ACTIONS> actions;
    private Tablero tablero;

    public AgenteDijkstra(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        solution = false;
        actions = null;
        tablero = new Tablero(stateObs);
        doDijkstra(tablero.pos_inicial);
    }

    public void doDijkstra(Pair inicial) {
        List<Nodo> visitados = new ArrayList<Nodo>();
        Set<Nodo> pendientes = new TreeSet<Nodo>();
        Nodo actual = new Nodo(inicial, 0);
        pendientes.add(actual);
        while (!pendientes.isEmpty()) {
            actual = pendientes.iterator().next();
            pendientes.remove(actual);
            visitados.add(actual);

            if (actual.pos.equals(tablero.capa_roja)) {
                actual.capa_roja = true;
                actual.capa_azul = false;
            }
            if (actual.pos.equals(tablero.capa_azul)) {
                actual.capa_azul = true;
                actual.capa_roja = false;
            }
            if (actual.pos.equals(tablero.salida)) {
                break;
            }
            ArrayList<Nodo> nuevos_hijos = actual.getHijos(tablero.getAvailableActions(actual));
            for (Nodo nodo : nuevos_hijos) {
                for (Nodo visitado : visitados)
                    if (visitado.equals(nodo)) {
                        if (visitado.coste > nodo.coste) {
                            visitados.remove(visitado);
                            pendientes.add(nodo);
                        }
                    } else if (pendientes.contains(nodo)) {
                        for (Nodo n : pendientes)
                            if (n.equals(nodo) && n.coste > nodo.coste) {
                                pendientes.remove(n);
                                pendientes.add(nodo);
                            }
                    } else {
                        pendientes.add(nodo);
                    }
            }
        }
        if (actual.pos == tablero.salida) {
            solution = true;
            this.actions = actual.getActions();
        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (solution && !actions.isEmpty()) {
            ACTIONS a = actions.get(0);
            actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
}
