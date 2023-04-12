package projekt;

import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ControlPanel extends JFrame {
    private final Renderer renderer;
    int posX, posY;

    ArrayList<Ball> balls;
    float oldVal;
    Cube cube;
    Dimension size;
    JPanel panel;
    JSlider ballMassSlider, ballRadiusSlider, ballCorSlider, cubeSizeSlider;
    JLabel ballMassLabel, ballRadiusLabel, ballCorLabel, cubeSizeLabel;


    public ControlPanel(int posX, int posY, Dimension size, ArrayList<Ball> balls, Renderer renderer, Cube cube) throws HeadlessException {
        this.renderer = renderer;
        this.cube = cube;
        GL.createCapabilities();
        this.balls = balls;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        initSliders();
        initLabels();
        initPanel();
        setSize((int) size.getWidth() / 2, (int) size.getHeight() - 50);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusableWindowState(false);
        add(panel);


    }

    private void initSliders(){

        ballMassSlider = new JSlider();
        ballMassSlider.setMaximum(10);
        ballMassSlider.setMinimum(1);
        ballMassSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            int value = source.getValue();
            for(Ball ball: balls){
                ball.setMass(value);
            }
        });

        float ballRadiusMinValue = 0.5f;
        float ballRadiusMaxValue = 5;
        float scale = 10.0f;
        int minRadiusSliderValue = (int) (ballRadiusMinValue * scale);
        int maxRadiusSliderValue = (int) (ballRadiusMaxValue * scale);

        ballRadiusSlider = new JSlider();
        ballRadiusSlider.setMaximum(maxRadiusSliderValue);
        ballRadiusSlider.setMinimum(minRadiusSliderValue);
        ballRadiusSlider.setMajorTickSpacing(10);
        ballRadiusSlider.setPaintTicks(false);
        ballRadiusSlider.addChangeListener(e -> {
            int sliderValue = ballRadiusSlider.getValue();
            float value = (float) sliderValue/scale;
            for(Ball ball: balls){
                ball.setRadius(value);
            }
        });

        ballCorSlider = new JSlider();
        float ballCorMinValue = 0.1f;
        float ballCorMaxValue = 1;
        float scaleCor = 10.0f;
        int minCorSliderValue = (int) (ballCorMinValue * scaleCor);
        int maxCorSliderValue = (int) (ballCorMaxValue * scaleCor);
        ballCorSlider.setMaximum(maxCorSliderValue);
        ballCorSlider.setMinimum(minCorSliderValue);
        ballCorSlider.addChangeListener(e -> {
            int slidervalue = ballCorSlider.getValue();
            float value = (float) slidervalue / scaleCor;
            for(Ball ball: balls){
                ball.setCorConstant(value);
            }
        });
        cubeSizeSlider = new JSlider();
        int cubeScaleMinValue = 1;
        int cubeScaleMaxValue = 10;
        cubeSizeSlider.setValue(1);
        cubeSizeSlider.setMinimum(cubeScaleMinValue);
        cubeSizeSlider.setMaximum(cubeScaleMaxValue);
        cubeSizeSlider.setMajorTickSpacing(1);
        cubeSizeSlider.setMinorTickSpacing(0);
        cubeSizeSlider.setPaintTicks(true);
        cubeSizeSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            float value = (float) source.getValue();
            float convertedValue = (float) (Math.log(value) / Math.log(cubeScaleMaxValue));
            float scaleValue = 1.0f + 0.2f * convertedValue;
            System.out.println(scaleValue);
            renderer.setCubeScale(scaleValue);
        });
    }

    private void initPanel(){
        panel = new JPanel();
        panel.setSize(300, 600);
        panel.setVisible(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(ballMassLabel);
        panel.add(ballMassSlider);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(ballRadiusLabel);
        panel.add(ballRadiusSlider);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(ballCorLabel);
        panel.add(ballCorSlider);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(cubeSizeLabel);
        panel.add(cubeSizeSlider);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
    }

    private void initLabels(){
        ballMassLabel = new JLabel("Hmotnost");
        ballRadiusLabel = new JLabel("Poloměr");
        ballCorLabel = new JLabel("Koeficient restituce");
        cubeSizeLabel = new JLabel("Velikost kostky");
    }

}
