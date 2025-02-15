package api.gossip.uz.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bird extends Animal {

    private boolean walks;


    public Bird() {
        super("bird");
    }

    public Bird(String name, boolean walks) {
        super(name);
        setWalks(walks);
    }

    @Override
    protected String getSound() {
        return "";
    }

    public Bird(String name) {
        super(name);
    }

    public boolean walks() {
        return walks;
    }

    @Override
    public String eats() {
        return "";
    }
}
