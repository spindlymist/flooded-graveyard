package a4;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DriftBehavior extends Behavior {

    public static final float DISTANCE_THRESHOLD = .2f;

    private final Vector3f velocity = new Vector3f();
    private final Vector3f desiredVelocity = new Vector3f();
    private final Vector3f steering = new Vector3f();
    private final Vector3f movement = new Vector3f();
    private final Vector3f target = new Vector3f();
    private float currentAngle = 0f;
    private float targetAngle;
    private float maxVelocity = .1f;
    private float angularSpeed = 22.5f;
    private boolean enabled = true;

    public DriftBehavior(Entity owner) {
        super(owner);
        chooseTarget();
    }

    private void updateHeading(Vector3fc direction, double dt) {
        float angleBetween = (float) MathUtil.angleBetween(currentAngle, targetAngle);
        float deltaTheta = (float) MathUtil.clamp(angularSpeed * dt, 0.0, Math.abs(angleBetween));
        currentAngle += Math.copySign(deltaTheta, angleBetween);
        currentAngle = (float) MathUtil.clampAngle(currentAngle);
        getOwner().setEulerAngles(0f, currentAngle, 0f);
    }

    @Override
    public void update(double dt) {
        if(!enabled) return;

        maxVelocity = (float) Math.sin(Application.getGameTime() / 4.0) * .5f + .75f;

        if(atTarget()) {
            chooseTarget();
        }
        seekTarget(dt);
    }

    private void chooseTarget() {
        target.set(getOwner().getPosition());

        float dist;
        if(target.length() > 20f) { // Too far from center of scene
            target.set(0f, target.y, 0f);
            targetAngle = (float) RandUtil.nextAngleInDegrees();
            dist = RandUtil.nextRangedFloat(0.0f, 5.0f);
        }
        else {
            targetAngle = (float) MathUtil.clampAngle(currentAngle + RandUtil.nextRangedFloat(-135f, 135f));
            dist = RandUtil.nextRangedFloat(5.0f, 15.0f);
        }

        target.add(
                dist * (float) Math.sin(Math.toRadians(targetAngle)),
                0f,
                dist * (float) Math.cos(Math.toRadians(targetAngle))
        );
    }

    private boolean atTarget() {
        return getOwner().getPosition().distance(target) < DISTANCE_THRESHOLD;
    }

    private void seekTarget(double dt) {
        desiredVelocity.set(target);
        desiredVelocity.sub(getOwner().getPosition());
        desiredVelocity.y = 0f;
        desiredVelocity.normalize(maxVelocity);

        steering.set(desiredVelocity);
        steering.sub(velocity);
        steering.mul((float) dt);

        velocity.add(steering);
        if(velocity.length() > maxVelocity) {
            velocity.normalize(maxVelocity);
        }

        movement.set(velocity);
        movement.mul((float) dt);
        getOwner().translate(movement);
        updateHeading(movement, dt);

        Vector3f pos = new Vector3f(getOwner().getPosition());
        pos.y = (float) Math.sin(Application.getGameTime()) * .33f + 3.0f;
        getOwner().setPosition(pos);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
