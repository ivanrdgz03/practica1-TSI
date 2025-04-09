package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.Map;
import ontology.Types.ACTIONS;

/**
 * Diccionario de direcciones util para toda la practica y la competicion
 */
public final class Direcciones {
    public static final Map<ACTIONS, Pair> direcciones = Map.of(
            ACTIONS.ACTION_UP, new Pair(-1,0),
            ACTIONS.ACTION_DOWN, new Pair(1,0),
            ACTIONS.ACTION_LEFT, new Pair(0,-1),
            ACTIONS.ACTION_RIGHT, new Pair(0,1));

    Direcciones() {}
};
