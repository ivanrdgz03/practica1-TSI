package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;


public class AgenteCompeticion extends AbstractPlayer {
    private boolean solution;
    private LinkedList<ACTIONS> actions;
    private TableroCompeticion tablero;
    private NodoCompeticion actual;

    public AgenteCompeticion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.solution = false;
        this.actions = null;
        this.tablero = new TableroCompeticion(stateObs);
        this.actual = new NodoCompeticion(this.tablero.pos_inicial, 0);
        this.actual.calculateHeuristica(this.tablero.pos_inicial, this.tablero.gemas);
    }
private ACTIONS reaccionarAEnemigos() {
    ArrayList<ACTIONS> accionesDisponibles = this.tablero.getAcciones(this.actual);
    if (accionesDisponibles.isEmpty()) return ACTIONS.ACTION_NIL;

    // Mapeo directo de acciones a desplazamientos
    Map<ACTIONS, Pair> direcciones = new HashMap<>();
    direcciones.put(ACTIONS.ACTION_UP, new Pair(-1,0));
    direcciones.put(ACTIONS.ACTION_DOWN, new Pair(1,0));
    direcciones.put(ACTIONS.ACTION_LEFT, new Pair(0,-1));
    direcciones.put(ACTIONS.ACTION_RIGHT, new Pair(0,1));

    // Sistema de ponderación de peligro mejorado
    Map<ACTIONS, Double> puntuaciones = new HashMap<>();
    double maxPeligro = Double.MIN_VALUE;
    double minPeligro = Double.MAX_VALUE;

    for (ACTIONS accion : accionesDisponibles) {
        Pair delta = direcciones.get(accion);
        Pair nuevaPos = new Pair(
            this.actual.pos.x + delta.x,
            this.actual.pos.y + delta.y
        );

        // Calcular peligro con perspectiva de 2 pasos adelante
        double peligroInmediato = calcularPeligroDinamico(nuevaPos);
        double peligroFuturo = predecirPeligroFuturo(nuevaPos, delta);
        
        // Penalización dinámica por giro basada en peligro actual
        double penalizacionGiro = (this.actual.vista != accion) ? 
            Math.max(10, 100 - peligroInmediato) : 0;

        double puntuacionTotal = peligroInmediato * 0.7 + peligroFuturo * 0.3 + penalizacionGiro;
        
        puntuaciones.put(accion, puntuacionTotal);
        
        if (puntuacionTotal > maxPeligro) maxPeligro = puntuacionTotal;
        if (puntuacionTotal < minPeligro) minPeligro = puntuacionTotal;
    }

    // Normalizar y seleccionar mejor acción
    ACTIONS mejorAccion = ACTIONS.ACTION_NIL;
    double mejorPuntuacion = Double.MAX_VALUE;

    for (Map.Entry<ACTIONS, Double> entry : puntuaciones.entrySet()) {
        double puntuacionNormalizada = (entry.getValue() - minPeligro) / (maxPeligro - minPeligro);
        if (puntuacionNormalizada < mejorPuntuacion) {
            mejorPuntuacion = puntuacionNormalizada;
            mejorAccion = entry.getKey();
        }
    }

    return mejorAccion;
}

private double calcularPeligroDinamico(Pair pos) {
    double peligroTotal = 0;
    for (Pair enemigo : this.tablero.monstruos) {
        double distancia = Math.hypot(pos.x - enemigo.x, pos.y - enemigo.y);
        
        // Función de peligro exponencial decreciente
        peligroTotal += 100000 * Math.exp(-distancia * 0.8) / (1 + distancia);
        
        // Penalización adicional si está en línea recta
        if (pos.x == enemigo.x || pos.y == enemigo.y) {
            peligroTotal *= 1.5;
        }
    }
    return peligroTotal;
}

private double predecirPeligroFuturo(Pair posActual, Pair direccion) {
    // Simular 2 movimientos adelante en la misma dirección
    Pair posFutura = new Pair(
        posActual.x + direccion.x * 2,
        posActual.y + direccion.y * 2
    );
    
    // Verificar si la posición futura es válida
    if (!this.tablero.isTransitable(posFutura)) {
        return Double.MAX_VALUE;
    }
    
    return calcularPeligroDinamico(posFutura);
}

    private void planificador(ElapsedCpuTimer elapsedTimer) {
        HashSet<NodoCompeticion> visitados = new HashSet<NodoCompeticion>();
        PriorityQueue<NodoCompeticion> pendientes = new PriorityQueue<NodoCompeticion>();
        NodoCompeticion nodo_actual = this.tablero.getNodoInicial(this.actual, this.actual.vista);
        pendientes.add(nodo_actual);

        while (!pendientes.isEmpty()) {
            nodo_actual = pendientes.poll();

            if (visitados.contains(nodo_actual)) continue;   // Si ya ha sido visitado, lo ignoramos

            visitados.add(nodo_actual);  // Añadimos a cerrados
            
            int numGemas = nodo_actual.gemas_capturadas.size();
            if ((nodo_actual.padre != null && numGemas > nodo_actual.padre.gemas_capturadas.size() && numGemas < 9) || this.tablero.esSalida(nodo_actual)) {
                break;
            }
            if(elapsedTimer.remainingTimeMillis() < 10){
                break;
            }
            // Si no es la salida, añadimos los hijos a abiertos
            for (NodoCompeticion nodo : this.tablero.getHijos(nodo_actual))
                if (!visitados.contains(nodo))
                    pendientes.add(nodo);
        }
        int numGemas = nodo_actual.gemas_capturadas.size();
        if ((nodo_actual.padre != null && numGemas > nodo_actual.padre.gemas_capturadas.size() && numGemas < 9) || this.tablero.esSalida(nodo_actual)||elapsedTimer.remainingTimeMillis() < 10) {
            this.solution = true;
            this.actions = nodo_actual.getActions();
        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tablero.update(this.actual, stateObs);
        if (this.calcularPeligroDinamico(this.actual.pos) > 1000.0) {
            ACTIONS a = reaccionarAEnemigos();
            this.actual = this.actual.applyAction(a, this.tablero.gemas);
            this.solution = false;
            this.actions = null;
            return a;
        }
        if (!this.solution) { // Si no tenemos un plan lo generamos
            planificador(elapsedTimer);
        }
        if (this.actions != null && !this.actions.isEmpty()) {
            ACTIONS action = this.actions.poll();
            if(this.actions.isEmpty()) {
                this.solution = false;
            }
            NodoCompeticion nuevo = this.actual.applyAction(action, this.tablero.gemas);
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
