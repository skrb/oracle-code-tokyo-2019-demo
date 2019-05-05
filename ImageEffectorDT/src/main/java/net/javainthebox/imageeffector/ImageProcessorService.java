package net.javainthebox.imageeffector;

import java.util.List;
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
        private List<Image> images;

        protected ImageProcessor(ImageViewerController controller, List<Image> images) {
            this.controller = controller;
            this.images = images;
        }

        abstract Task<Void> createTask();

        abstract Runnable createCallback(int index, Image image, long duration);

        void applyEffect(ImageViewerController controller,
                TriFunction function) {
            IntStream.range(0, images.size())
                    .forEach(i -> {
                        var image = images.get(i);
                        int width = (int) image.getWidth();
                        int height = (int) image.getHeight();

                        PixelReader reader = image.getPixelReader();

                        int[] buffer = new int[width * height];
                        reader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);

                        long start = System.nanoTime();
                        int[] blured = function.apply(buffer, width, height);
                        long end = System.nanoTime();

                        WritableImage copy = new WritableImage(width, height);
                        PixelWriter writer = copy.getPixelWriter();
                        writer.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), blured, 0, width);

                        Platform.runLater(createCallback(i, copy, end - start));
                    });
        }
    }

    class SequentialProcessor extends ImageProcessor {

        public SequentialProcessor(ImageViewerController controller, List<Image> images) {
            super(controller, images);
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
        public Runnable createCallback(int index, Image image, long duration) {
            return () -> {
                controller.updateImageSeq(index, image, duration);
            };
        }
    }

    public class VectorProcessor extends ImageProcessor {

        public VectorProcessor(ImageViewerController controller, List<Image> images) {
            super(controller, images);
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
        public Runnable createCallback(int index, Image image, long duration) {
            return () -> {
                controller.updateImageVec(index, image, duration);
            };
        }
    }

    public ImageProcessorService(ImageViewerController controller, List<Image> images) {
        seqProcessor = new SequentialProcessor(controller, images);
        vecProcessor = new VectorProcessor(controller, images);
    }

    public void start() {
        ExecutorService service = Executors.newFixedThreadPool​(2,
                r -> {
                    var thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                });
        service.submit(vecProcessor.createTask());
        service.submit(seqProcessor.createTask());
    }
}
