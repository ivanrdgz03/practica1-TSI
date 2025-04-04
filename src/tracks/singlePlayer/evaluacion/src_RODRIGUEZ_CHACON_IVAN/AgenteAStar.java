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
        this.solution = false;
        this.actions = null;
        this.tablero = new Tablero(stateObs);
    }

    public void doAStar() {
        long tInicio = System.nanoTime();

        Set<Nodo> visitados = new HashSet<>();
        PriorityQueue<Nodo> pendientes = new PriorityQueue<Nodo>();
        Nodo actual = this.tablero.getNodoInicial();
        pendientes.add(actual);

        while (!pendientes.isEmpty()) {
            actual = pendientes.poll();
            if (visitados.contains(actual)) continue;

            visitados.add(actual);
            
            if (this.tablero.esSalida(actual.pos)) {
                break;
            }
            
            for (Nodo nodo : this.tablero.getHijos(actual))
                if (!visitados.contains(nodo)) {
                    nodo.calculateHeuristic(this.tablero.salida);
                    pendientes.add(nodo);
                }
        }
        if (this.tablero.esSalida(actual.pos)) {
            long tFin = System.nanoTime();
            long tiempoTotalms = (tFin - tInicio)/1000000;
            this.solution = true;
            this.actions = actual.getActions();
            System.out.println("Abiertos: " + pendientes.size());
            System.out.println("Cerrados: " + visitados.size());
            System.out.println("Tiempo total: " + tiempoTotalms + " ms");
            System.out.println("Tamaño de la ruta: " + this.actions.size());
        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (!this.solution)
            doAStar();
        if (this.solution && !this.actions.isEmpty()) {
            ACTIONS a = this.actions.get(0);
            this.actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
}
