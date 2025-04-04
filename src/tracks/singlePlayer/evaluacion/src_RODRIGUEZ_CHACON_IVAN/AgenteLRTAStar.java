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
    private long iteraciones;
    private long tiempoTotalms;

    public AgenteLRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tabla_hash = new HashMap<>();
        this.tablero = new Tablero(stateObs);
        this.actual = this.tablero.getNodoInicial();
        this.iteraciones = 0;
        this.tiempoTotalms = 0;
        Nodo.COSTE_ENABLED = false;
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        long tInicio = System.nanoTime();
        PriorityQueue<Nodo> hijos = new PriorityQueue<>();
        for(Nodo hijo : this.tablero.getHijos(this.actual)){
            if(!this.tabla_hash.containsKey(hijo)){
                hijo.calculateHeuristic(this.tablero.salida);
                this.tabla_hash.put(hijo,hijo.heuristica);
            }else{
                hijo.heuristica = this.tabla_hash.get(hijo);
            }
            hijos.add(hijo);
        }
        Nodo mejor = hijos.poll();
        if(mejor.heuristica+1>this.actual.heuristica){
            this.actual.heuristica = mejor.heuristica+1;
            this.tabla_hash.put(this.actual,this.actual.heuristica);
        }
        this.actual = mejor;
        long tFin = System.nanoTime();
        this.iteraciones++;
        this.tiempoTotalms += ((tFin - tInicio) / 1000000);
        if(this.tablero.esSalida(actual.pos)){
            System.out.println("Iteraciones: " + this.iteraciones);
            System.out.println("Tiempo medio: " + this.tiempoTotalms/this.iteraciones + " ms");
        }
        return mejor.accion_padre;
    }
};
