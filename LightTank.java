package model;

import java.util.Random;

public class LightTank extends Tank {
    private static final long serialVersionUID = 1L;
    private int viewRange;
    private String classTank = "Лёгкий танк";
    private int numberDetectedEnemies=0;

    public LightTank (int id, String name, int HP, int viewRange){
        super(name, HP);
        this.viewRange=viewRange;
    }

    public String toString(){
        return getName() + ": " + getHPTank() + ": " + getViewRange() + ": ";
    }

    public int getViewRange(){
        return viewRange;
    }

    public void setViewRange(int viewRange){
        this.viewRange = viewRange;
    }

    public int getNumberDetectedEnemies(){
        return numberDetectedEnemies;
    }

    public String useAbility(){
        Random random = new Random();
        int distanceToTank = random.nextInt((viewRange+(viewRange/2)) + 1 - (viewRange-(viewRange/2))) + (viewRange-(viewRange/2));
        if (viewRange>=distanceToTank) {
            numberDetectedEnemies += 1;
            return "Танк " + getName() + " обнаружил врага!";
        } else {
            return "Танк " + getName() + " никого не нашёл!";
        }
    }
}
