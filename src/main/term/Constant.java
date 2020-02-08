package main.term;

public class Constant<T> implements Term<T> {

    private T value;

    // no default constructor
    private Constant() {};

    public Constant(T value){
        this.value = value;
    }


    // COPY CONSTRUCTOR !!!
    public Constant(Constant constant){
        this.setValue((T)constant.getValue());
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public T setValue(T newVal) {
        T previousVal = this.value;
        this.value = newVal;
        return previousVal;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Constant){
            return ((Constant) obj).getValue().equals(this.getValue());
        }
        return false;
    }
}
