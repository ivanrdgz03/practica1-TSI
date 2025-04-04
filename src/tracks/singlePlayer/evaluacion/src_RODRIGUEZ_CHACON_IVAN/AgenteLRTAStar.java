package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteLRTAStar extends AbstractPlayer {
    private HashMap<Nodo, Integer> tabla_hash;
    private Tablero tablero;
    private Nodo actual;
    private int iteraciones;
    private long tiempoTotalms;

    public AgenteLRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tabla_hash = new HashMap<>();
        tablero = new Tablero(stateObs);
        this.actual = new Nodo(tablero.pos_inicial, 0);
        this.iteraciones = 0;
        this.tiempoTotalms = 0;
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        long tInicio = System.nanoTime();
        int hayCapa = tablero.hayCapa(actual.pos);
        if(actual.pos == this.tablero.salida){
            System.out.println("Iteraciones: " + this.iteraciones);
            System.out.println("Tiempo medio: " + this.tiempoTotalms/this.iteraciones + " ms");
            return ACTIONS.ACTION_NIL;
        }
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
            if(mejor.heuristica>actual.heuristica){
            actual.heuristica = mejor.heuristica;
            tabla_hash.put(actual,actual.heuristica);
        }
        actual = mejor;
        long tFin = System.nanoTime();
        this.iteraciones++;
        this.tiempoTotalms += (tFin - tInicio) / 1000000;
        return mejor.accion_padre;
    }
};
