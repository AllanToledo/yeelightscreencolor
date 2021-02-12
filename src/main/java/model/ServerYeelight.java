package model;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class ServerYeelight implements Runnable {

    private String host;
    private int port;

    private double x1, y1, x2, y2;

    private Boolean exit = false;

    public ServerYeelight(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void config(javafx.scene.shape.Shape area) {
        double x1 = area.getLayoutX();
        double y1 = area.getLayoutY();
        double x2 = area.getLayoutX() + area.getLayoutBounds().getWidth();
        double y2 = area.getLayoutY() + area.getLayoutBounds().getHeight();
        double sh = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        double sw = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.x1 = (x1 / 160) * sw;
        this.x2 = (x2 / 160) * sw;
        this.y1 = (y1 / 90) * sh;
        this.y2 = (y2 / 90) * sh;
    }

    public void stopEffect() {
        this.exit = true;
    }

    @Override
    public void run() {
        this.exit = false;
        try (Socket clientSocket = new Socket(host, port)) {
            Robot robot = new Robot();
            String modifiedSentence;
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            long id = 0;
            int lastRGB = 0;
            while (!exit) {
                int rgbInteger = ImageDominantColor.getIntegerColor(robot.createScreenCapture(screenRectangle), x1, y1, x2, y2);
                System.out.println(rgbInteger);
                if (lastRGB != rgbInteger) {
                    lastRGB = rgbInteger;
                    id++;
                    outToServer.writeBytes("{\"id\":" + id + ",\"method\":\"set_rgb\",\"params\":[" + rgbInteger + ",\"smooth\",200]}\r\n");
                    modifiedSentence = inFromServer.readLine();
                    System.out.println("FROM SERVER: " + modifiedSentence);
                }
                TimeUnit.MILLISECONDS.sleep(1500);
            }
        }catch (AWTException | IOException | InterruptedException e) {
            e.printStackTrace();
            stopEffect();
        }
    }

    private int rgbToInteger(int red, int green, int blue) {
        int rgb = blue;
        rgb += green * 256;
        rgb += red * 256 * 256;
        return rgb;
    }
}
