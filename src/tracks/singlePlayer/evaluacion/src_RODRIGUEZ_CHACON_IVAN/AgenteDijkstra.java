package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.List;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class AgenteDijkstra extends AbstractPlayer {
    private boolean solution;
    private List<ACTIONS> actions;
    private Tablero tablero;

    public AgenteDijkstra(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.solution = false;
        this.actions = null;
        this.tablero = new Tablero(stateObs);
        Nodo.HEURISTICA_ENABLED = false;
    }

    private void doDijkstra() {
        long tInicio = System.nanoTime();
        HashSet<Nodo> visitados = new HashSet<>();
        PriorityQueue<Nodo> pendientes = new PriorityQueue<Nodo>();
        Nodo actual = this.tablero.getNodoInicial();
        pendientes.add(actual);

        while (!pendientes.isEmpty()) {
            actual = pendientes.poll();

            if (visitados.contains(actual)) continue;   // Si ya ha sido visitado, lo ignoramos
            
            visitados.add(actual);  // Añadimos a cerrados
            
            if (this.tablero.esSalida(actual.pos))  // Si es la salida, salimos
            break;
            
            // Si no es la salida, añadimos los hijos a abiertos
            for (Nodo nodo : this.tablero.getHijos(actual))
                if (!visitados.contains(nodo))
                    pendientes.add(nodo);
        }
        // Si hemos llegado a la salida, guardamos la solución e imprimimos los datos requeridos
        if (this.tablero.esSalida(actual.pos)) {
            long tFin = System.nanoTime();
            long tiempoTotalms = (tFin - tInicio) / 1000000;
            this.solution = true;
            this.actions = actual.getActions();
            System.out.println("Cerrados: " + visitados.size());
            System.out.println("Tiempo total: " + tiempoTotalms + " ms");
            System.out.println("Tamaño de la ruta: " + this.actions.size());
        }
    }
        
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (!this.solution) //Si no tenemos un plan lo generamos
            doDijkstra();
        if (this.solution && !this.actions.isEmpty()) { //Si tenemos un plan y no hemos llegado al final lo seguimos
            ACTIONS a = this.actions.get(0);
            this.actions.remove(0);
            return a;
        }
        return ACTIONS.ACTION_NIL;
    }
};