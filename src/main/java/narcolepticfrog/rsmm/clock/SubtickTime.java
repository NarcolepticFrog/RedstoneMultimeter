package narcolepticfrog.rsmm.clock;

public class SubtickTime implements Comparable<SubtickTime> {

    private int tick;
    private int subtickIndex;

    public SubtickTime(int tick, int subtickIndex) {
        this.tick = tick;
        this.subtickIndex = subtickIndex;
    }

    public int getTick() {
        return tick;
    }

    public int getSubtickIndex() {
        return subtickIndex;
    }

    @Override
    public int compareTo(SubtickTime o) {
        if (this.tick != o.tick) {
            return this.tick - o.tick;
        } else {
            return this.subtickIndex - o.subtickIndex;
        }
    }

    public boolean equals(Object o) {
        if (o instanceof SubtickTime) {
            SubtickTime ot = (SubtickTime) o;
            return compareTo(ot) == 0;
        }
        return false;
    }

    public String toString() {
        return "SubtickTime[tick = " + tick + ", subtickIndex = " + subtickIndex + "]";
    }

}
