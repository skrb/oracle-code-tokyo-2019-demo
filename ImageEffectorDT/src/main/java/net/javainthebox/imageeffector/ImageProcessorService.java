package net.javainthebox.imageeffector;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageProcessorService {
    ImageProcessor seqProcessor;
    ImageProcessor vecProcessor;

    abstract class ImageProcessor {
	protected ImageViewerController controller;
	
	protected ImageProcessor(ImageViewerController controller) {
	    this.controller = controller;
	}

	abstract Task<Void> createTask();
	abstract Runnable createCallback(Image image);

	void applyEffect(ImageViewerController controller,
			 TriFunction function) {
	    IntStream.range(0, 10)
		.forEach(i -> {
			var image = new Image(this.getClass().getResource("image"+i+".jpg").toString());

			int width = (int) image.getWidth();
			int height = (int) image.getHeight();
		    
			PixelReader reader = image.getPixelReader();
	
			int[] buffer = new int[width * height];
			reader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);
			
			long s = System.nanoTime();
			int[] blured = function.apply(buffer, width, height);
			long e = System.nanoTime();
//			System.out.println(i + ": " + (e -s));
			
			WritableImage copy = new WritableImage(width, height);
			PixelWriter writer = copy.getPixelWriter();
			writer.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), blured, 0, width);
			
			Platform.runLater(createCallback(copy));
		    });
	}

    }
    
    class SequentialProcessor extends ImageProcessor {
	public SequentialProcessor(ImageViewerController controller) {
	    super(controller);
	}

	@Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
		    applyEffect(controller, SoftFocusEffector::softenLoop);
		    return null;
                }
            };
        }

	@Override
	public Runnable createCallback(Image image) {
	    return () -> {
		controller.updateImageSeq(image);
	    };
	}
    }

    public class VectorProcessor extends ImageProcessor {
	public VectorProcessor(ImageViewerController controller) {
	    super(controller);
	}

	@Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
		    applyEffect(controller, SoftFocusEffector::softenVector);
                    return null;
                }
            };
        }

	@Override
	public Runnable createCallback(Image image) {
	    return () -> {
		controller.updateImageVec(image);
	    };
	}
    }

    public ImageProcessorService(ImageViewerController controller) {
	seqProcessor = new SequentialProcessor(controller);
	vecProcessor = new VectorProcessor(controller);
    }

    public void start() {
	ExecutorService service = Executors.newFixedThreadPool​(2,
			       r -> {
			           var thread = new Thread(r);
				   thread.setDaemon(true);
				   return thread;
			       });
	service.submit(seqProcessor.createTask());
	service.submit(vecProcessor.createTask());
    }
}
