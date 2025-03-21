package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;
import java.util.ArrayList;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Tablero {
    public ArrayList<Observation>[][] grid;
    public Pair pos_inicial;
    public Pair salida;
    public Pair capa_roja;
    public Pair capa_azul;

    public Tablero(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();
        this.pos_inicial = null;
        this.salida = null;
        this.capa_roja = null;
        this.capa_azul = null;
        for(int i = 0; i < this.grid.length && (this.pos_inicial == null || this.salida == null || this.capa_roja == null || this.capa_azul == null); i++){
            for(int j = 0; j < this.grid[0].length && (this.pos_inicial == null || this.salida == null || this.capa_roja == null || this.capa_azul == null); j++){
                if(this.grid[i][j].size() > 0){
                    if(this.grid[i][j].get(0).itype == 4)
                        this.salida = new Pair(j,i);
                    if(this.grid[i][j].get(0).itype == 10)
                        this.pos_inicial = new Pair(j,i);
                    if(this.grid[i][j].get(0).itype == 8)
                        this.capa_roja = new Pair(j,i);
                    if(this.grid[i][j].get(0).itype == 9)
                        this.capa_azul = new Pair(j,i);
                }
            }
        }
    }
    boolean isTransitable(Pair pos, boolean capa_azul, boolean capa_roja){
        if(pos.x < 0 || pos.x >= grid[0].length || pos.y < 0 || pos.y >= grid.length){
            System.err.println("Posicion fuera de los limites del tablero");
            return false;
        }
        if(grid[pos.y][pos.x].isEmpty())
            return true;
        int itype = grid[pos.y][pos.x].get(0).itype;
        return !(itype == 3 || itype == 5 || (itype== 6 && !capa_roja) || (itype==7 && !capa_azul));
    }
    ArrayList<ACTIONS> getAvailableActions(Pair pos, boolean capa_azul, boolean capa_roja){
        ArrayList<ACTIONS> aviables = new ArrayList<ACTIONS>();
        if(isTransitable(new Pair(pos.x, pos.y+1), capa_azul, capa_roja))
            aviables.add(ACTIONS.ACTION_RIGHT);
        if(isTransitable(new Pair(pos.x, pos.y-1), capa_azul, capa_roja))
            aviables.add(ACTIONS.ACTION_LEFT);
        if(isTransitable(new Pair(pos.x-1, pos.y), capa_azul, capa_roja))
            aviables.add(ACTIONS.ACTION_UP);
        if(isTransitable(new Pair(pos.x+1, pos.y), capa_azul, capa_roja))
            aviables.add(ACTIONS.ACTION_DOWN);

        return aviables;
    }

    ArrayList<ACTIONS> getAvailableActions(Nodo nodo){
        return getAvailableActions(nodo.pos, nodo.capa_azul, nodo.capa_roja);
    }
}


/*
* 3- trampa
* 4- meta
 * 5- obstaculo
 * 6- obstaculo rojo
 * 7- obstaculo azul
 * 8- caperuza roja
 * 9- azul
 * 10- pos inicial
 */