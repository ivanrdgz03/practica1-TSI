package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import java.util.*;

public class AgenteCompeticion extends AbstractPlayer {
    private boolean solution;
    private List<ACTIONS> actions;
    private TableroCompeticion tablero;
    private Nodo actual;

    public AgenteCompeticion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.solution = false;
        this.actions = null;
        this.tablero = new TableroCompeticion(stateObs);
    }

    private void reactivo(){
        //Implementar reactivo
    }

    private void planificador(){
        //Implementar planificador
    }
    
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.tablero.update(stateObs);
        Pair peligro = this.tablero.dangerous(this.actual);
        if(peligro != null){
            reactivo();
        }
        else if(!this.solution) { //Si no tenemos un plan lo generamos
            planificador();
        }
        return ACTIONS.ACTION_NIL;
    }
};
