package a4;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Moves an Entity along a path defined by lines between a series of points.
 */
public class FollowWaypointsBehavior extends Behavior {

    ////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////

    public static final float DISTANCE_THRESHOLD = .1f;

    ////////////////////////////////////////////////////////////
    // Fields
    ////////////////////////////////////////////////////////////

    private final float speed;
    private final List<Vector3fc> waypoints = new ArrayList<Vector3fc>();
    private int currentWPIdx = 0;
    private Vector3fc currentWaypoint;
    private float angleBias = 0f;

    ////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////

    public FollowWaypointsBehavior(Entity owner, Vector3fc start, float speed) {
        super(owner);
        this.speed = speed;
        this.waypoints.add(new Vector3f(start));
        currentWaypoint = start;
    }

    ////////////////////////////////////////////////////////////
    // Public Interface
    ////////////////////////////////////////////////////////////

    public void addWaypoint(Vector3fc waypoint) {
        waypoints.add(waypoint);
    }

    @Override
    public void update(double dt) {
        if(atNextWaypoint()) {
            nextWaypoint();
        }

        Vector3f direction = getDirection();
        updateHeading(direction);
        updatePosition(direction, dt);
    }

    public void setAngleBias(float bias) {
        this.angleBias = bias;
    }

    ////////////////////////////////////////////////////////////
    // Internal Methods
    ////////////////////////////////////////////////////////////

    private boolean atNextWaypoint() {
        return getOwner().getPosition().distance(currentWaypoint) < DISTANCE_THRESHOLD;
    }

    private void nextWaypoint() {
        currentWPIdx = (currentWPIdx + 1) % waypoints.size();
        currentWaypoint = waypoints.get(currentWPIdx);
    }

    private Vector3f getDirection() {
        Vector3f direction = new Vector3f();
        currentWaypoint.sub(getOwner().getPosition(), direction);
        direction.normalize();

        return direction;
    }

    private void updateHeading(Vector3fc direction) {
        float angle = (float) Math.toDegrees(MathUtil.VEC3_FORWARD.angleSigned(direction, MathUtil.VEC3_UP));
        getOwner().setEulerAngles(new Vector3f(0f, angle + angleBias, 0f));
    }

    private void updatePosition(Vector3f direction, double dt) {
        direction.mul((float) (speed * dt));
        getOwner().translate(direction);
    }

}
