package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.*;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

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
        PriorityQueue<Nodo> hijos = new PriorityQueue<>();  // Cola de prioridad para ordenarlos por su heurística

        // Generamos los hijos del nodo actual
        for(Nodo hijo : this.tablero.getHijos(this.actual)){
            if(!this.tabla_hash.containsKey(hijo))  // Si no existe en la tabla hash, lo añadimos
                this.tabla_hash.put(hijo,hijo.heuristica);
            else    // Si ya existe, lo obtenemos de la tabla
                hijo.heuristica = this.tabla_hash.get(hijo);
            
            hijos.add(hijo);
        }
        if(hijos.isEmpty())    // Si no hay hijos, no podemos avanzar y nos quedamos quietos
            return ACTIONS.ACTION_NIL;
            
        Nodo mejor = hijos.poll();
        
        //Actualizamos la heurística del nodo actual en base a la del mejor
        if((mejor.heuristica+1)>this.actual.heuristica){
            this.actual.heuristica = (mejor.heuristica+1);
            this.tabla_hash.put(this.actual,this.actual.heuristica);
        }
        this.actual = mejor;
        
        long tFin = System.nanoTime();
        this.iteraciones++;
        this.tiempoTotalms += (tFin - tInicio);
        if(this.tablero.esSalida(actual.pos)){
            System.out.println("Iteraciones: " + this.iteraciones);
            System.out.println("Tiempo medio: " + this.tiempoTotalms / 1000000 + " ms");
        }
        return mejor.accion_padre;
    }
};
