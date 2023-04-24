package a4;

public abstract class Behavior {

    private final Entity owner;

    public Behavior(Entity owner) {
        this.owner = owner;
    }

    public Entity getOwner() {
        return owner;
    }

    public abstract void update(double dt);

}
