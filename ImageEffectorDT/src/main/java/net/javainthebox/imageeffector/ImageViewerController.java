package net.javainthebox.imageeffector;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ImageViewerController implements Initializable {

    @FXML
    private Button startButton;
    
    @FXML
    private ImageView imageViewSeq;
    @FXML
    private AnchorPane thumbSeqPane;

    @FXML
    private ImageView imageViewVec;
    @FXML
    private AnchorPane thumbVecPane;

    private List<Image> images;
    private List<Node> thumbSeqImageViews;
    private List<Node> thumbVecImageViews;
    
    @FXML
    private Label timeSeqLabel;
    @FXML
    private Label timeVecLabel;

    private long totalDurationSeq = 0L;
    private long totalDurationVec = 0L;

    @FXML
    private void showContextMenu(ContextMenuEvent e) {
        var item = new MenuItem("Quit");
        item.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        item.setOnAction(event -> Platform.exit());

        var menu = new ContextMenu(item);
        menu.show((Node) e.getSource(), e.getX(), e.getY());
    }
    
    public void updateImageSeq(int index, Image image, long duration) {
        if (index > 0) {
            thumbSeqImageViews.get(index-1).setScaleX(1.0);
            thumbSeqImageViews.get(index-1).setScaleY(1.0);
        }
        
        var animation = new ScaleTransition(Duration.millis(100), thumbSeqImageViews.get(index));
        animation.setToX(1.25);
        animation.setToY(1.25);
        animation.play();
        thumbSeqImageViews.get(index).toFront();

        imageViewSeq.setImage(image);
        
        totalDurationSeq += duration;
        timeSeqLabel.setText("Sequential: " + NumberFormat.getInstance().format(totalDurationSeq/1_000_000) + "ms");
    }

    public void updateImageVec(int index, Image image, long duration) {
        if (index > 0) {
            thumbVecImageViews.get(index-1).setScaleX(1.0);
            thumbVecImageViews.get(index-1).setScaleY(1.0);
        }

        var animation = new ScaleTransition(Duration.millis(100), thumbVecImageViews.get(index));
        animation.setToX(1.25);
        animation.setToY(1.25);
        animation.play();
        thumbVecImageViews.get(index).toFront();

        imageViewVec.setImage(image);
        
        totalDurationVec += duration;
        timeVecLabel.setText("Vector: " + NumberFormat.getInstance().format(totalDurationVec/1_000_000) + "ms");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        thumbSeqImageViews = new ArrayList<>(thumbSeqPane.getChildren());
        thumbVecImageViews = new ArrayList<>(thumbVecPane.getChildren());
        
        images = IntStream.range(0, 24)
                .mapToObj(i -> {
                    var image = new Image(this.getClass().getResource("images/image" + i + ".jpg").toString());
                    ((ImageView) thumbSeqPane.getChildren().get(i)).setImage(image);
                    ((ImageView) thumbVecPane.getChildren().get(i)).setImage(image);
                    return image;
                }).collect(Collectors.toList());

        ImageProcessorService service = new ImageProcessorService(this, images);
        startButton.setOnAction(e -> {
            startButton.setVisible(false);
            service.start();
        });
        
        timeSeqLabel.setEffect(new DropShadow(2.0, 2.0, 2.0, Color.WHITE));
        timeVecLabel.setEffect(new DropShadow(2.0, 2.0, 2.0, Color.WHITE));
    }

}
