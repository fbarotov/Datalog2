package main;

public class Pair<S,T> {
    private S firstVal = null;
    private T secondVal = null;

    // no default constructor
    private Pair() {}

    public Pair(S firstVal, T secondVal){
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    public S getFirstVal() {
        return firstVal;
    }

    public T getSecondVal() {
        return secondVal;
    }
}
