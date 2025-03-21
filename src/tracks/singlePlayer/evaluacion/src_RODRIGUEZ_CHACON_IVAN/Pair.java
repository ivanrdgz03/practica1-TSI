package tracks.singlePlayer.evaluacion.src_RODRIGUEZ_CHACON_IVAN;

import java.util.Objects;

public class Pair implements Comparable<Pair> {
    public int x;
    public int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair(Pair pair) {
        this.x = pair.x;
        this.y = pair.y;
    }

    @Override
    public int compareTo(Pair p) {
        int compare = Integer.compare(this.x, p.x);
        if (compare == 0)
           compare = Integer.compare(this.y, p.y);
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair p = (Pair) o;
            return ((this.x == p.x) && (this.y == p.y));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
