package animation;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;

public class ListeningChemObjectBuilder implements IChemObjectBuilder {
    
    private IFrameTrigger trigger;
    
    public ListeningChemObjectBuilder(IFrameTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public <T extends ICDKObject> T newInstance(
            Class<T> clazz, Object... params) throws IllegalArgumentException {
        if (IAtomContainer.class.isAssignableFrom(clazz)) {
            return (T) new ListeningAtomContainer(trigger);
        } else if (IAtom.class.isAssignableFrom(clazz)) {
            if (params.length == 0) return (T) new ListeningAtom();
            if (params.length == 1) {
                if (params[0] instanceof String) {
                    return (T) new ListeningAtom((String)params[0]);
                }
                if (params[0] instanceof IElement) {
                    return (T) new ListeningAtom((IElement)params[0]);
                }
            } else if (params.length == 2 && params[0] instanceof String) {
                if (params[1] instanceof Point2d) {
                    return (T) new ListeningAtom((String)params[0], (Point2d)params[1]);
                }
                if (params[1] instanceof Point3d) {
                    return (T) new ListeningAtom((String)params[0], (Point3d)params[1]);
                }
            }
        }
        return null;
    }

}
