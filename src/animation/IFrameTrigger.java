package animation;

/**
 * Interface for a trigger for frame events. A frame is the complete state of a 
 * chem object container (like IAtomContainer) that might represent a micro-
 * change from the previous state or a change to all its sub-objects.
 * 
 * Micro-events are posted to the trigger, which accumulates them until it 
 * decides to fire a frame event.
 * 
 * 
 * @author maclean
 *
 */
public interface IFrameTrigger {
    
    public void postLowLevelEvent(MicroEvent event);

}
