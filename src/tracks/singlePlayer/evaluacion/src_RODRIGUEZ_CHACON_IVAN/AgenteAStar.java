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
            actual = pendientes.iterator().next();
            pendientes.remove(actual);
            if(visitados.contains(actual))
                continue;

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
            for (Nodo nodo : actual.getHijos(tablero.getAvailableActions(actual)))
                if(!visitados.contains(nodo)){
                    nodo.calculateHeuristic(tablero.salida);
                    pendientes.add(nodo);
                }
        }
        if (actual.pos.equals(tablero.salida)) {
            this.solution = true;
            this.actions = actual.getActions();
        }
    }
    
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if(!this.solution)
            doAStar();
        if (this.solution && !this.actions.isEmpty()) {
            ACTIONS a = actions.get(0);
            actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
}
