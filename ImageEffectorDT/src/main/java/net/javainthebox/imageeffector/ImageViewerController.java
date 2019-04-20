package net.javainthebox.imageeffector;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewerController implements Initializable {

    @FXML
    private ImageView imageViewSeq;
    @FXML
    private ImageView imageViewVec;

    public void updateImageSeq(Image image) {
        imageViewSeq.setImage(image);
    }

    public void updateImageVec(Image image) {
        imageViewVec.setImage(image);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ImageProcessorService service = new ImageProcessorService(this);
        service.start();
    }

}
