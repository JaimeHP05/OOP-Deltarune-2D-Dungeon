package code.model.interfaces;
import code.model.entities.Entity;

public interface Interactable extends java.io.Serializable {
    boolean onStep(Entity entity);
    boolean isSolid(); 
}