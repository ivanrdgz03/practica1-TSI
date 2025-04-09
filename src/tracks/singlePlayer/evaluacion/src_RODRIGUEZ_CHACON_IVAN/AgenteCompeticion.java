package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;


public class AgenteCompeticion extends AbstractPlayer {
    private boolean solution;
    private List<ACTIONS> actions;
    private TableroCompeticion tablero;
    private NodoCompeticion actual;

    public AgenteCompeticion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.solution = false;
        this.actions = null;
        this.tablero = new TableroCompeticion(stateObs);
        this.actual = new NodoCompeticion(this.tablero.pos_inicial, 0);
        this.actual.calculateHeuristica(this.tablero.pos_inicial, new ArrayList<Pair>(this.tablero.gemas));
    }
    private ACTIONS reaccionarAEnemigos() {
        ArrayList<ACTIONS> acciones = this.tablero.getAcciones(this.actual);
        if (acciones.isEmpty()) return ACTIONS.ACTION_NIL;
    
        Pair[] deltas = {
            new Pair(-1, 0),
            new Pair(1, 0),
            new Pair(0, -1),
            new Pair(0, 1),
        };
    
        ACTIONS[] direcciones = {
            ACTIONS.ACTION_UP,
            ACTIONS.ACTION_DOWN,
            ACTIONS.ACTION_LEFT,
            ACTIONS.ACTION_RIGHT,
        };
    
        int minCosto = Integer.MAX_VALUE;
        ACTIONS mejorOpcion = ACTIONS.ACTION_NIL;
    
        for (ACTIONS dir: acciones) {
            int i = 0;
            for(i = 0; i < deltas.length; i++) {
                if (dir == direcciones[i]) {
                    dir = direcciones[i];
                    break;
                }
            }
            Pair nuevaPos = new Pair(this.actual.pos.x + deltas[i].x, this.actual.pos.y + deltas[i].y);
    
            int peligro = calcularPeligro(nuevaPos);
            int penalizacionGiro = (this.actual.vista != dir) ? 1 : 0;  // 1 turno extra si hay que girar
            int costoTotal = peligro + penalizacionGiro * 50; // puedes ajustar este valor
    
            if (costoTotal < minCosto) {
                minCosto = costoTotal;
                mejorOpcion = dir;
            }
        }
    
        return mejorOpcion;
    }

    private int calcularPeligro(Pair pos) {
        int peligro = 0;
        for (Pair enemigo : this.tablero.monstruos) {
            int distancia = Math.abs(pos.x - enemigo.x) + Math.abs(pos.y - enemigo.y);
            if (distancia == 0) peligro += 1000000; // Colisión inminente
            else if (distancia == 1) peligro += 10000;
            else if (distancia == 2) peligro += 500;
            else if (distancia == 3) peligro += 100;
        }
        return peligro;
    }

    private void planificador() {
        HashSet<NodoCompeticion> visitados = new HashSet<NodoCompeticion>();
        PriorityQueue<NodoCompeticion> pendientes = new PriorityQueue<NodoCompeticion>();
        NodoCompeticion nodo_actual = this.tablero.getNodoInicial(this.actual, this.actual.vista);
        pendientes.add(nodo_actual);

        while (!pendientes.isEmpty()) {
            nodo_actual = pendientes.poll();

            if (visitados.contains(nodo_actual)) continue;   // Si ya ha sido visitado, lo ignoramos

            visitados.add(nodo_actual);  // Añadimos a cerrados
            
            // Si es la salida, salimos
            if ((nodo_actual.padre != null && nodo_actual.gemas_capturadas.size() > nodo_actual.padre.gemas_capturadas.size() && nodo_actual.gemas_capturadas.size()<9) || this.tablero.esSalida(nodo_actual)) {
                break;
            }
            
            // Si no es la salida, añadimos los hijos a abiertos
            for (NodoCompeticion nodo : this.tablero.getHijos(nodo_actual))
                if (!visitados.contains(nodo))
                    pendientes.add(nodo);
        }
        // Si hemos llegado a la salida, guardamos la solución e imprimimos los datos requeridos
        if  ((nodo_actual.padre != null && nodo_actual.gemas_capturadas.size() > nodo_actual.padre.gemas_capturadas.size() && nodo_actual.gemas_capturadas.size()<9) || this.tablero.esSalida(nodo_actual)) {
            this.solution = true;
            this.actions = nodo_actual.getActions();
        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tablero.update(this.actual, stateObs);
        if (this.calcularPeligro(this.actual.pos) > 0) {
            ACTIONS a = reaccionarAEnemigos();
            this.actual = this.actual.applyAction(a, new ArrayList<>(this.tablero.gemas));
            this.solution = false;
            this.actions = null;
            return a;
        }
        if (!this.solution) { // Si no tenemos un plan lo generamos
            planificador();
        }
        if (this.actions != null && !this.actions.isEmpty()) {
            ACTIONS action = this.actions.get(0);
            this.actions.remove(0);
            if(this.actions.isEmpty()) {
                this.solution = false;
            }
            NodoCompeticion nuevo = this.actual.applyAction(action, new ArrayList<>(this.tablero.gemas));
            if(!this.tablero.isTransitable(nuevo.pos)){
                this.actions = null;
                System.out.println("Accion no transitable");
                return ACTIONS.ACTION_NIL;
            }else{
                this.actual = nuevo;
            }
            return action;
        }
        return ACTIONS.ACTION_NIL;
    }
};
