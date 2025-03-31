package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteAStar extends AbstractPlayer {
    private boolean solution;
    private List<ACTIONS> actions;
    private Tablero tablero;

    public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        solution = false;
        actions = null;
        tablero = new Tablero(stateObs);
    }

    public void doAStar() {
        Set<Nodo> visitados = new HashSet<>();
        PriorityQueue<Nodo> pendientes = new PriorityQueue<Nodo>();
        Nodo actual = new Nodo(tablero.pos_inicial, 0);
        pendientes.add(actual);
        while (!pendientes.isEmpty()) {
            actual = pendientes.poll();
            if (visitados.contains(actual)) continue;

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
            
            if (tablero.esSalida(actual.pos)) {
                break;
            }

            for (Nodo nodo : actual.getHijos(tablero.getAviablesActions(actual)))
                if (!visitados.contains(nodo)) {
                    nodo.calculateHeuristic(tablero.salida);
                    pendientes.add(nodo);
                }
        }
        if (tablero.esSalida(actual.pos)) {
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
            doAStar();
        if (this.solution && !this.actions.isEmpty()) {
            ACTIONS a = actions.get(0);
            actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
}
