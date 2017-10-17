package narcolepticfrog.rsmm;

/**
 * A Ring-Buffer based data structure for storing the most recent elements of a stream.
 * @param <T>
 */
public class Trace<T>
{

    private T[] values;
    private int head;
    private int used;

    /**
     * Constructs a new trace with the given capacity.
     */
    @SuppressWarnings("unchecked")
    public Trace(int capacity) {
        values = (T[])new Object[capacity];
        head = -1;
        used = 0;
    }

    /** Adds a new value into the trace */
    public void push(T v) {
        if (used < values.length) {
            used += 1;
        }
        head = (head + 1) % values.length;
        values[head] = v;
    }

    /**
     * Returns the ith most recently added value to the trace, starting from 0.
     */
    public T get(int i) {
        // we add values.length to head-i to avoid negative indices
        return values[(head-i+values.length) % values.length];
    }

    /**
     * Returns then number of values available in the trace.
     */
    public int size() {
        return used;
    }

    /**
     * Removes all items from the trace.
     */
    public void clear() {
        used = 0;
        head = -1;
    }

    public int capacity() {
        return values.length;
    }

    /**
     * Creates a new trace with the same contents but larger capacity.
     */
    public Trace<T> copyWithCapacity(int cap) {
        Trace<T> trace = new Trace<>(cap);
        for (int i = Math.min(cap, size()) - 1; i >= 0; i--) {
            trace.push(get(i));
        }
        return trace;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Trace[");
        for (int j = 0; j < size(); j++) {
            if (j != 0) b.append(", ");
            b.append(get(j));
        }
        b.append("]");
        return b.toString();
    }

}
