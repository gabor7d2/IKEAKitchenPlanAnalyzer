package net.gabor6505.java.ikeaplananalyzer;

import net.gabor6505.java.ikeaplananalyzer.helper.Selectable;

public class Property<T extends Selectable, V> {

    private final T owner;
    private final String name;
    private V value;

    public Property(T owner, String name, V value) {
        this.owner = owner;
        this.name = name;
        this.value = value;
    }

    public Property(T owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public T getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
