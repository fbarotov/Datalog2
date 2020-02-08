package main;

import java.util.List;

public class Choice extends NonVRule implements Rule{

    public Choice(Predicate head, List<Predicate> body) {
        super(head, body);
    }
}
