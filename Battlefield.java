package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Battlefield implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Tank> tanks = new ArrayList();

    public Battlefield(){

    }

    public void addTank(Tank tank) {
        this.tanks.add(tank);
    }

    public boolean removeTank(String name) {
        return this.tanks.removeIf((tank) -> {
            return tank.getName().equalsIgnoreCase(name);
        });
    }

    public String performAbilityTank(int index) {
        if (tanks == null || tanks.isEmpty()) {
            return "На поле боя нет ни одного танка.";
        }
        if (index < 0 || index >= tanks.size()) {
            return "Неверный индекс танка.";
        }
        Tank tank = tanks.get(index);
        return tank.useAbility();
    }

    public int getCount(){
        return  this.tanks.size();
    }

    public Tank getTank(int index) {
        return tanks.get(index);
    }

    public List<Tank> getTanks() {
        if (tanks == null) {
            tanks =new ArrayList<>();
        }
        return new ArrayList<>(tanks);
    }

    public void remove(int index) {
        this.tanks.remove(index);
    }
}
