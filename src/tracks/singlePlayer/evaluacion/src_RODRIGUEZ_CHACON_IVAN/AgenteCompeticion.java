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
import java.util.Queue;
import java.util.Set;

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

    /**
     * Reacciona a los enemigos en el tablero, eligiendo la mejor acción,se podría
     * considerar como el agente reactivo
     * 
     * @return la mejor acción a realizar
     */
    private ACTIONS reaccionarAEnemigos() {
        ArrayList<ACTIONS> accionesDisponibles = this.tablero.getAcciones(this.actual); // Obtenemos las acciones
                                                                                        // disponibles
        if (accionesDisponibles.isEmpty()) // Si no hay acciones disponibles, devolvemos nil
            return ACTIONS.ACTION_NIL;

        Map<ACTIONS, Double> puntuaciones = new HashMap<>();

        for (ACTIONS accion : accionesDisponibles) {
            Pair delta = Direcciones.direcciones.get(accion);
            Pair nuevaPos = new Pair(this.actual.pos.x + delta.x, this.actual.pos.y + delta.y);

            // Calcular peligro con las posiciones actuales y con la prediccion en dos
            // turnos
            double peligroInmediato = calcularPeligroInmediato(nuevaPos);
            double peligroFuturo = predecirPeligroFuturo(nuevaPos, delta);

            // Penalizacion por giro en funcion de la situacion actual, a peor sea la
            // situacion mas penaliza girar
            double penalizacionGiro = (this.actual.vista != accion) ? 3000 : 0;

            // Calcular puntuacion total en base a los peligros y la penalizacion
            // Por ahora los porcentajes que mejor se ajustan son estos, aunque
            // automatizando la búsqueda de parámetros pienso que se podría mejorar
            double puntuacionTotal = peligroInmediato * 0.7 + peligroFuturo * 0.3 + penalizacionGiro;

            puntuaciones.put(accion, puntuacionTotal); // Guardamos la puntuacion de cada accion
        }

        // Seleccionar mejor acción
        ACTIONS mejorAccion = ACTIONS.ACTION_NIL;
        double mejorPuntuacion = Double.MAX_VALUE;
        for (Map.Entry<ACTIONS, Double> entry : puntuaciones.entrySet()) {
            double puntuacion = entry.getValue();
            if (puntuacion < mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorAccion = entry.getKey();
            }
        }

        return mejorAccion;
    }

    /**
     * Calcula el peligro inmediato de una posición dada, en función de la distancia
     * a los enemigos y si está cerca de zonas peligrosas
     * 
     * @param pos
     * @return
     */
    private double calcularPeligroInmediato(Pair pos) {
        double peligro = 0;
        double distanciaMinima = Double.MAX_VALUE;

        for (Pair enemigo : this.tablero.monstruos) {
            // Calcular distancia Manhattan
            int dx = Math.abs(pos.x - enemigo.x);
            int dy = Math.abs(pos.y - enemigo.y);
            
            // Ignorar enemigos más allá de 3 casillas de distancia Manhattan
            if (dx > 3 || dy > 3) {
                continue;
            }
            
            double distancia = dx + dy;
            // Actualizar distancia mínima
            distanciaMinima = Math.min(distanciaMinima, distancia);
            
            // Calcular contribución de peligro para este enemigo
            double contribucion = 100000 * Math.exp(-distancia * 0.8) / (1 + distancia);
            
            // Bonus si está en la misma fila o columna (dx o dy es 0)
            if (dx == 0 || dy == 0) {
                contribucion +=2000;
            }
            
            int posibles_movimientos = this.tablero.getAcciones(pos).size();
            if (posibles_movimientos <= 2) {    //Si el jugador se arrincona aumenta el peligro
                double penalizacionSalida = (posibles_movimientos == 1) ? 6000 : 4000;
                contribucion += penalizacionSalida;
            }



            peligro += contribucion;
        }

        return peligro;
    }

    /**
     * Predice las posiciones peligrosas a las que los monstruos pueden llegar en un
     * número determinado de turnos
     * 
     * @param profundidad Número de turnos a predecir
     * @return Conjunto de posiciones peligrosas
     */
    private Set<Pair> predecirPosicionesPeligrosas(int profundidad) {
        Set<Pair> peligros = new HashSet<>();
        // Realizamos una búsqueda en anchura para encontrar las posiciones al alcance
        // de los monstruos
        for (Pair monstruo : this.tablero.monstruos) {
            Queue<Pair> cola = new LinkedList<>();
            Set<Pair> visitados = new HashSet<>();
            cola.add(monstruo);
            visitados.add(monstruo);

            int nivel = 0;
            while (!cola.isEmpty() && nivel < profundidad) {
                int size = cola.size();
                // Recorremos todos los nodos del nivel actual y los añadimos a peligros
                for (int i = 0; i < size; i++) {
                    Pair actual = cola.poll();
                    peligros.add(actual);
                    // Añadimos los vecinos a la cola si no han sido visitados y son transitables
                    for (Pair dir : Direcciones.direcciones.values()) {
                        Pair vecino = new Pair(actual.x + dir.x, actual.y + dir.y);
                        if (!visitados.contains(vecino) && this.tablero.isTransitable(vecino)) {
                            visitados.add(vecino);
                            cola.add(vecino);
                        }
                    }
                }
                nivel++;
            }
        }

        return peligros;
    }

    /**
     * Predice el peligro de la casilla dos posiciones hacia delante, si no es
     * transitable se devuelve el maximo
     * 
     * @param posActual Posición actual
     * @param direccion Direccion en la que se mueve
     * @return Peligro de la posición futura
     */
    private double predecirPeligroFuturo(Pair posActual, Pair direccion) {
        // Simular 2 movimientos adelante en la misma dirección
        Pair posFutura = new Pair(
                posActual.x + direccion.x * 2,
                posActual.y + direccion.y * 2);

        // Verificar si la posición futura es válida
        Pair siguiente_posicion = new Pair(posActual.x + direccion.x, posActual.y + direccion.y);
        if (!this.tablero.isTransitable(posFutura)) {
            if(this.tablero.isTransitable(siguiente_posicion))
                return calcularPeligroInmediato(siguiente_posicion); // Si la posición futura no es transitable, devolvemos el máximo
            else
                return 1000;
        }

        return (calcularPeligroInmediato(posFutura)+2*calcularPeligroInmediato(siguiente_posicion))/3;
    }

    private void planificador(ElapsedCpuTimer elapsedTimer) {
        HashSet<NodoCompeticion> visitados = new HashSet<NodoCompeticion>();
        PriorityQueue<NodoCompeticion> pendientes = new PriorityQueue<NodoCompeticion>();
        NodoCompeticion nodo_actual = this.tablero.getNodoInicial(this.actual);
        pendientes.add(nodo_actual);

        while (!pendientes.isEmpty()) {
            nodo_actual = pendientes.poll();

            if (visitados.contains(nodo_actual))
                continue; // Si ya ha sido visitado, lo ignoramos

            visitados.add(nodo_actual); // Añadimos a cerrados

            int numGemas = nodo_actual.gemas_capturadas.size();
            if ((nodo_actual.padre != null && numGemas > nodo_actual.padre.gemas_capturadas.size() && numGemas < NodoCompeticion.GEMAS_NECESARIAS)
                    || this.tablero.esSalida(nodo_actual)) {
                break;
            }
            if (elapsedTimer.remainingTimeMillis() < 3) {
                break;
            }
            // Si no es la salida, añadimos los hijos a abiertos
            for (NodoCompeticion nodo : this.tablero.getHijos(nodo_actual))
                if (!visitados.contains(nodo))
                    pendientes.add(nodo);
        }

        int numGemas = nodo_actual.gemas_capturadas.size();
        if ((nodo_actual.padre != null && numGemas > nodo_actual.padre.gemas_capturadas.size() && numGemas < NodoCompeticion.GEMAS_NECESARIAS)
                || this.tablero.esSalida(nodo_actual) || elapsedTimer.remainingTimeMillis() < 10) {
            this.solution = true;
            this.actions = nodo_actual.getActions();
        }
    }

    /**
     * Se encarga de ejecutar la acción en el juego
     */
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tablero.update(stateObs); // Actualizamos el tablero
        final int UMBRAL_PELIGRO = 500; // Valor umbral para el peligro
        // Si el agente está en una posición peligrosa, reaccionamos
        if (this.calcularPeligroInmediato(this.actual.pos) > UMBRAL_PELIGRO) {
            ACTIONS a = reaccionarAEnemigos();
            this.actual = this.actual.applyAction(a, this.tablero.gemas); // Aplicamos la acción
            this.solution = false; // Borramos el plan realizado para recalcular la ruta
            this.actions = null;
            return a;
        }
        if (!this.solution) { // Si no tenemos un plan lo generamos
            planificador(elapsedTimer);
        }
        if (this.actions != null && !this.actions.isEmpty()) { // Si tenemos un plan lo ejecutamos
            ACTIONS action = this.actions.poll();
            if (this.actions.isEmpty()) {
                this.solution = false; // Si se termina el plan, lo señalizamos
            }
            // Aplicamos la acción al nodo actual
            this.actual = this.actual.applyAction(action, this.tablero.gemas);

            return action;
        }
        return ACTIONS.ACTION_NIL;  // Si no tenemos ningun plan ni estamos en peligro, no hacemos nada (esto no deberia pasar)
    }
};
