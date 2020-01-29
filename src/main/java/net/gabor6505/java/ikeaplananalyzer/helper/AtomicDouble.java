package net.gabor6505.java.ikeaplananalyzer.helper;

public class AtomicDouble {

    private double value = 0;

    public AtomicDouble() {

    }

    public AtomicDouble(double initialValue) {
        value = initialValue;
    }

    public double get() {
        return value;
    }

    public void set(double newValue) {
        this.value = newValue;
    }

    public double addAndGet(double value) {
        this.value += value;
        return value;
    }

    public double getAndAdd(double value) {
        double oldValue = this.value;
        this.value += value;
        return oldValue;
    }
}
