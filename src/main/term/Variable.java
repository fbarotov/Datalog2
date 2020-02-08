package main.term;

public class Variable implements Term<String> {

    private String value;

    // no default constructor
    private Variable() {};

    public Variable(String value){
        this.value = value;
    }

    // COPY CONSTRUCTOR !!!
    public Variable(Term term){
        this.setValue( ((Variable) term).getValue() );
    }



    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String val) {
        String previousVal = this.value;
        this.value = val;
        return previousVal;
    }

    @Override
    public String toString(){
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Variable){
            return ((Variable) obj).getValue().equals(this.getValue());
        }
        return false;
    }
}
