package api.gossip.uz.entity;

import api.gossip.uz.repository.Eating;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Animal implements Eating {

    public static String CATEGORY = "domestic";
    private String name;

    protected abstract String getSound();

    public Animal(String name) {
        this.name = name;
    }

}
