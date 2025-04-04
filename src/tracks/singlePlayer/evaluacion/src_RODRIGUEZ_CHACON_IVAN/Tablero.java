package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;
import java.util.*;
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
    public HashSet<Pair> capa_roja;
    public HashSet<Pair> capa_azul;

    public Tablero(StateObservation stateObs) {
        this.grid = stateObs.getObservationGrid();
        this.pos_inicial = null;
        this.salida = null;
        this.capa_roja = new HashSet<Pair>();
        this.capa_azul = new HashSet<Pair>();
        for (int i = 0; i < this.grid.length; i++)
            for (int j = 0; j < this.grid[0].length; j++)
                if (!this.grid[i][j].isEmpty()){
                    int tipo = this.grid[i][j].get(0).itype;
                    switch (tipo) {
                        case 4: this.salida = new Pair(j, i); break;
                        case 10: this.pos_inicial = new Pair(j, i); break;
                        case 8: this.capa_roja.add(new Pair(j, i)); break;
                        case 9: this.capa_azul.add(new Pair(j, i)); break;
                    }
                }
    }
    boolean isTransitable(Pair pos, boolean capa_azul, boolean capa_roja){
        if(pos.x < 0 || pos.x >= grid[0].length || pos.y < 0 || pos.y >= grid.length)   //Se sale del mapa
            return false;

        if(grid[pos.y][pos.x].isEmpty())    //Si no hay información sobre la casilla
            return true;
        int itype = grid[pos.y][pos.x].get(0).itype;
        return !(itype == 3 || itype == 5 || (itype== 6 && !capa_roja) || (itype==7 && !capa_azul));    //Si no son trampas ni muros (ni muros azules si no tiene capa azul ni muros rojos si no tiene capa roja)
    }
    private ArrayList<ACTIONS> getAviablesActions(Pair pos, boolean capa_azul, boolean capa_roja){
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

    ArrayList<ACTIONS> getAviablesActions(Nodo nodo){
        return getAviablesActions(nodo.pos, nodo.capa_azul, nodo.capa_roja);
    }
    public int hayCapa(Pair pos){
        if(this.capa_azul.contains(pos))
            return 1;
        if(this.capa_roja.contains(pos))
            return -1;
        return 0;
    }
    public boolean esSalida(Pair pos){
        if(pos.equals(this.salida))
            return true;
        if(pos.x<0 || pos.x>=grid[0].length || pos.y<0 || pos.y>=grid.length)
            return false;
        if(this.grid[pos.y][pos.x].isEmpty())
            return false;
        return this.grid[pos.y][pos.x].get(0).itype == 4;
    }
    private void updateCapas(Nodo nodo){
        int capa = this.hayCapa(nodo.pos);
        if (capa < 0 && !nodo.capas_usadas.contains(nodo.pos)) {
            nodo.capa_roja = true;
            nodo.capa_azul = false;
            nodo.capas_usadas.add(nodo.pos);
        }
        if (capa > 0 && !nodo.capas_usadas.contains(nodo.pos)) {
            nodo.capa_azul = true;
            nodo.capa_roja = false;
            nodo.capas_usadas.add(nodo.pos);
        }
    }
    public ArrayList<Nodo> getHijos(Nodo padre) {
        ArrayList<ACTIONS> acciones = this.getAviablesActions(padre);
        ArrayList<Nodo> hijos = new ArrayList<Nodo>();
        for (ACTIONS a : acciones) {
            Pair newPos = null;
            switch (a) {
                case ACTION_UP:
                newPos = new Pair(padre.pos.x - 1, padre.pos.y);
                break;
                case ACTION_DOWN:
                    newPos = new Pair(padre.pos.x + 1, padre.pos.y);
                    break;
                case ACTION_LEFT:
                    newPos = new Pair(padre.pos.x, padre.pos.y - 1);
                    break;
                case ACTION_RIGHT:
                    newPos = new Pair(padre.pos.x, padre.pos.y + 1);
                default:
                    break;
            }
            Nodo hijo = new Nodo(newPos, padre.coste + 1, padre, a);
            this.updateCapas(hijo);
            hijos.add(hijo);
        }
        return hijos;
    }

    public Nodo getNodoInicial(){
        Nodo inicial = new Nodo(this.pos_inicial, 0);
        this.updateCapas(inicial);

        return inicial;
    }

};

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