package code.model.objects;
import code.model.entities.Entity;

public class Berry extends Item {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    public Berry() { 
        super("Antidote"); 
    }

    @Override
    public void use(Entity entity) {
        System.out.println("You sprayed the " + getName() + " onto yourself.");
        if (entity.isPoisoned()) {
            System.out.println("The poison fades away.");
            entity.setPoisoned(false);
        }
    }
}