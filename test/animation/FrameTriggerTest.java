package animation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

public class FrameTriggerTest extends BaseAnimationTest {
    
    private class OneFramePerEventTrigger implements IFrameTrigger {
        
        private FrameListener listener;
        
        public OneFramePerEventTrigger() {
            listener = null;
        }

        @Override
        public void postLowLevelEvent(MicroEvent event) {
            // make a frame for every micro-event
            listener.frameCreated(new FrameEvent());
        }

        @Override
        public void addFrameListener(FrameListener listener) {
            this.listener = listener;
        }
        
    }
    
    @Test
    public void oneFramePerEventTest() {
        int numberOfAtoms = 5;
        int totalSteps = 5;
        
        OneFramePerEventTrigger frameTrigger = new OneFramePerEventTrigger();
        IChemObjectBuilder builder = new ListeningChemObjectBuilder(frameTrigger);
        IAtomContainer atomContainer = randomPointStructure(builder, numberOfAtoms);
        final List<FrameEvent> frameEvents = new ArrayList<FrameEvent>();
        frameTrigger.addFrameListener(new FrameListener() {

            @Override
            public void frameCreated(FrameEvent frameEvent) {
                frameEvents.add(frameEvent);
            }
            
        });
        dummyAlgorithm(atomContainer, totalSteps);
        Assert.assertEquals(totalSteps * numberOfAtoms, frameEvents.size());
    }
}
