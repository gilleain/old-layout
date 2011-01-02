package animation.gifanim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import animation.BaseAnimationTest;
import animation.FrameEvent;
import animation.FrameListener;
import animation.IFrameTrigger;
import animation.ListeningChemObjectBuilder;
import animation.MicroEvent;

public class GifAnimationFrameOutputTest extends BaseAnimationTest {
    
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
    public void basicTest() throws FileNotFoundException, IOException {
        String filename = "anim.gif";
        ImageOutputStream outputStream = 
            new FileImageOutputStream(new File(filename));
        GifAnimationFrameOutput animator = 
            new GifAnimationFrameOutput(outputStream);
        IFrameTrigger trigger = new OneFramePerEventTrigger();
        IChemObjectBuilder listeningBuilder = 
            new ListeningChemObjectBuilder(trigger);
        trigger.addFrameListener(animator);
        
        IAtomContainer atomContainer = randomPointStructure(listeningBuilder);
        dummyAlgorithm(atomContainer);
        animator.stop();
        outputStream.close();
    }

}
