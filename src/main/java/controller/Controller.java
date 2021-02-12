package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jfxtras.labs.util.event.MouseControlUtil;
import model.Callback;
import model.DragResizeMod;
import model.ServerYeelight;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;


public class Controller {
    private ServerYeelight server = new ServerYeelight("192.168.0.104", 55443);

    @FXML
    private ToggleButton turnOn;

    @FXML
    private javafx.scene.control.Button defaultButton;

    @FXML
    private javafx.scene.control.Button refreshButton;

    @FXML
    private Pane frame;

    Shape area = new Rectangle(160, 90);

    @FXML
    public void initialize() {
        area.setId("area");
        DragResizeMod.makeResizable(area, new Callback() {
            @Override
            public void callback() {
                server.config(area);
            }
        });
        frame.getChildren().add(area);
        new Thread() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                    setImageBackground();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @FXML
    void turnOnEffect(ActionEvent event) {

        if (turnOn.isSelected()) {
            startThread();
        } else {
            stopThread();
        }
    }

    @FXML
    void onRefresh(ActionEvent event){
        setImageBackground();
    }

    @FXML
    void setDefault(ActionEvent event){
    }

    private void setImageBackground() {
        Image image = null;
        try {
            image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize())).getScaledInstance(160, 90, Image.SCALE_SMOOTH);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        javafx.scene.image.Image imageFx = SwingFXUtils.toFXImage(toBufferedImage(image), null);
        frame.setBackground(new Background(new BackgroundImage(imageFx,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        System.gc();

    }

    private void startThread() {
        turnOn.setText("Desligar");
        server.config(area);
        new Thread(server).start();
    }

    private void stopThread() {
        turnOn.setText("Ligar");
        server.stopEffect();
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}