package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.*;
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
        Nodo.HEURISTICA_ENABLED = false;
    }

    public void doDijkstra(Pair inicial) {
        Set<Nodo> visitados = new HashSet<>();
        PriorityQueue<Nodo> pendientes = new PriorityQueue<Nodo>();
        Nodo actual = new Nodo(inicial, 0);
        pendientes.add(actual);
        while (!pendientes.isEmpty()) {
            actual = pendientes.poll();
            if (visitados.contains(actual))
                continue;

            int capa = tablero.hayCapa(actual.pos);
            if (capa < 0 && !actual.capas_usadas.contains(actual.pos)) {
                actual.capa_roja = true;
                actual.capa_azul = false;
                actual.capas_usadas.add(actual.pos);
            }
            if (capa > 0 && !actual.capas_usadas.contains(actual.pos)) {
                actual.capa_azul = true;
                actual.capa_roja = false;
                actual.capas_usadas.add(actual.pos);
            }
            
            visitados.add(actual);

            if (actual.pos.equals(tablero.salida))
                break;

            for (Nodo nodo : actual.getHijos(tablero.getAviablesActions(actual)))
                if (!visitados.contains(nodo))
                    pendientes.add(nodo);
        }
        if (actual.pos.equals(tablero.salida)) {
            this.solution = true;
            this.actions = actual.getActions();
            System.out.println("Abiertos: " + pendientes.size());
            System.out.println("Cerrados: " + visitados.size());
            System.out.println("Tamaño de la ruta: " + this.actions.size());
        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (!this.solution)
            doDijkstra(this.tablero.pos_inicial);
        if (this.solution && !this.actions.isEmpty()) {
            ACTIONS a = actions.get(0);
            actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
}
