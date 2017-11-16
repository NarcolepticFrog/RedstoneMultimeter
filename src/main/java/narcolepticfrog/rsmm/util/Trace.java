package narcolepticfrog.rsmm.util;

import java.util.function.Function;

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

    /**
     * Returns the smallest index {@code i} such that {@code by(get(i)) <= v}. Assumes that the elements
     * of the trace are in decreasing order (note: since the element at index i is the ith most recent
     * thing pushed into the trace, this requires that the elements are pushed into the trace in
     * increasing order).
     */
    public <C extends Comparable<C>> int binarySearch(C v, Function<T,C> by) {
        if (this.size() == 0) {
            return -1;
        }
        C smallest = by.apply(this.get(this.size() - 1));
        if (v.compareTo(smallest) < 0) {
            return -1;
        }

        int high = this.size()-1;
        int low = 0;
        while (high > low) {
            int mid = low + (high - low)/2;
            int comp = by.apply(this.get(mid)).compareTo(v);
            if (comp <= 0) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

}
