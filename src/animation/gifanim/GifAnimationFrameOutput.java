package animation.gifanim;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.stream.ImageOutputStream;

import animation.FrameEvent;
import animation.FrameListener;
import animation.IAnimator;

public class GifAnimationFrameOutput implements FrameListener, IAnimator {

    public final int DEFAULT_TIME_BETWEEN_FRAMES = 1000;
    
    private GifSequenceWriter gifSequenceWriter;
    
    public GifAnimationFrameOutput(ImageOutputStream outputStream) throws IIOException, IOException {
        gifSequenceWriter = new GifSequenceWriter(
                outputStream, 
                BufferedImage.TYPE_4BYTE_ABGR, 
                DEFAULT_TIME_BETWEEN_FRAMES, false);
    }
    
    public RenderedImage makeImage(FrameEvent frameEvent) {
        return null;
    }
    
    @Override
    public void frameCreated(FrameEvent frameEvent) {
        try {
            gifSequenceWriter.writeToSequence(makeImage(frameEvent));
        } catch (IOException ioe) {
            // TODO
            ioe.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            gifSequenceWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
