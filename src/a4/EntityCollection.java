package a4;

import com.jogamp.opengl.GL4;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class EntityCollection implements Collection<Entity>, Iterable<Entity> {

    private final Collection<Entity> entities = new ArrayList<Entity>();

    public EntityCollection() {
    }

    public void init(GL4 gl) {
        for(Entity entity : entities) {
            entity.init(gl);
        }
    }

    public void update(double dt) {
        for(Entity entity : entities) {
            entity.update(dt);
        }
    }

    public void render(RenderPass renderPass, Camera camera, Camera shadowCamera, Matrix4fStack modelMatStack) {
        for(Entity entity : entities) {
            entity.render(renderPass, camera, shadowCamera, modelMatStack);
        }
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return entities.contains(o);
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }

    @Override
    public void forEach(Consumer<? super Entity> action) {
        entities.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return entities.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return entities.toArray(a);
    }

    @Override
    public boolean add(Entity entity) {
        return entities.add(entity);
    }

    @Override
    public boolean remove(Object o) {
        return entities.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return entities.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Entity> c) {
        return entities.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return entities.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return entities.retainAll(c);
    }

    @Override
    public void clear() {
        entities.clear();
    }

}
