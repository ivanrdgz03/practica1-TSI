package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteRTAStar extends AbstractPlayer {
    private HashMap<Nodo, Integer> tabla_hash;
    private Tablero tablero;
    private Nodo actual;

    public AgenteRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tabla_hash = new HashMap<>();
        tablero = new Tablero(stateObs);
        this.actual = new Nodo(tablero.pos_inicial, 0);
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        int hayCapa = tablero.hayCapa(actual.pos);
        if (hayCapa < 0 && !actual.capas_usadas.contains(actual.pos)) {
            actual.capa_roja = true;
            actual.capa_azul = false;
            actual.capas_usadas.add(actual.pos);
        }
        if (hayCapa > 0 && !actual.capas_usadas.contains(actual.pos)) {
            actual.capa_azul = true;
            actual.capa_roja = false;
            actual.capas_usadas.add(actual.pos);
        }
        PriorityQueue<Nodo> hijos = new PriorityQueue<>();
        for(Nodo hijo : actual.getHijos(tablero.getAviablesActions(actual))){
            if(!tabla_hash.containsKey(hijo)){
                hijo.calculateHeuristic(tablero.salida);
                tabla_hash.put(hijo,hijo.heuristica);
            }else{
                hijo.heuristica = tabla_hash.get(hijo);
            }
            hijos.add(hijo);
        }
        Nodo mejor = hijos.poll();
        Nodo segundo_mejor = null;
        if(!hijos.isEmpty())
            segundo_mejor = hijos.poll();
            if(segundo_mejor != null && segundo_mejor.heuristica>actual.heuristica){
            actual.heuristica = segundo_mejor.heuristica;
            tabla_hash.put(actual,actual.heuristica);
        }
        actual = mejor;
        return mejor.accion_padre;
    }
};
