package projekt;

import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ControlPanel extends JFrame {
    private final Renderer renderer;
    private int posX, posY;
    private CopyOnWriteArrayList<Ball> balls;
    private float oldVal;
    private Cube cube;
    private Dimension size;
    private JPanel panel = new JPanel();
    private JSlider ballMassSlider, ballRadiusSlider, ballCorSlider, cubeSizeSlider;
    private JLabel ballMassLabel, ballRadiusLabel, ballCorLabel, cubeSizeLabel;

    private JLabel massValueLabel, radiusValueLabel, corValueLabel, sizeValueLabel;

    private JButton addBallButton, removeBallButton, resetSceneButton;


    public ControlPanel(CopyOnWriteArrayList<Ball> balls, Renderer renderer, Cube cube) throws HeadlessException {
        this.renderer = renderer;
        this.cube = cube;
        GL.createCapabilities();
        this.balls = balls;
        this.posX = renderer.getWindowPosX() + renderer.getWidth() / 2 + 20;
        this.posY = renderer.getWindowPosY() - 35;
        System.out.println(this.posX + " " + this.posX);
        this.setLocation(posX, posY);
        setUndecorated(true);
        this.size = size;
        initLabels();
        initSliders();
        initButtons();
        initPanel();
        setSize(300, 575);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusableWindowState(false);
        setAutoRequestFocus(false);
        add(panel);


    }

    private void initButtons() {
        addBallButton = new JButton("Přidat míček");
        addBallButton.addActionListener(e -> {
            if (balls.size() < 10) {
                balls.add(new Ball());
                ballRadiusSlider.setValue(10);
                ballCorSlider.setValue(10);
                ballMassSlider.setValue(1);
            }
        });
        removeBallButton = new JButton("Odebrat míček");
        removeBallButton.addActionListener(e -> {
            if (balls.size() > 0) {
                balls.remove(balls.size() - 1);
                if(balls.size() > 1){
                    ballRadiusSlider.setValue((int) balls.get(balls.size() - 1).getRadius() * 10);
                    ballCorSlider.setValue((int) balls.get(balls.size() - 1).getCorConstant() * 10);
                    ballMassSlider.setValue((int) balls.get(balls.size() - 1).getMass());
                } else {
                    ballRadiusSlider.setValue(10);
                    ballCorSlider.setValue(10);
                    ballMassSlider.setValue(1);
                }

            }
        });
        resetSceneButton = new JButton("Reset");
        resetSceneButton.addActionListener(e -> renderer.resetScene());

    }

    private void initSliders() {
        ballMassSlider = new JSlider(1, 10, 1);
        ballMassSlider.setPaintTicks(true);
        massValueLabel.setText(String.valueOf(1));
        ballMassSlider.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        ballMassSlider.addChangeListener(e -> {
            int massSliderValue = ballMassSlider.getValue();
            massValueLabel.setText(String.valueOf(massSliderValue));
            balls.get(balls.size() - 1).setMass((float) massSliderValue);
        });

        float ballRadiusMinValue = 0.5f;
        float ballRadiusMaxValue = 5;
        float scale = 10.0f;
        int minRadiusSliderValue = (int) (ballRadiusMinValue * scale);
        int maxRadiusSliderValue = (int) (ballRadiusMaxValue * scale);
        ballRadiusSlider = new JSlider();
        ballRadiusSlider.setMaximum(maxRadiusSliderValue);
        ballRadiusSlider.setMinimum(minRadiusSliderValue);
        ballRadiusSlider.setValue(10);
        radiusValueLabel.setText(String.valueOf((float) 1.0f));
        ballRadiusSlider.setMajorTickSpacing(5);
        ballRadiusSlider.setMinorTickSpacing(1);
        ballRadiusSlider.setPaintTicks(false);
        ballRadiusSlider.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        ballRadiusSlider.addChangeListener(e -> {
            int radiusSliderValue = ballRadiusSlider.getValue();
            radiusValueLabel.setText(String.valueOf((float) radiusSliderValue / 10));
            float value = (float) radiusSliderValue / scale;
            balls.get(balls.size() - 1).setRadius(value);
        });

        ballCorSlider = new JSlider();
        float ballCorMinValue = 0.1f;
        float ballCorMaxValue = 1;
        float scaleCor = 10.0f;
        int minCorSliderValue = (int) (ballCorMinValue * scaleCor);
        int maxCorSliderValue = (int) (ballCorMaxValue * scaleCor);
        ballCorSlider.setMaximum(maxCorSliderValue);
        ballCorSlider.setMinimum(minCorSliderValue);
        ballCorSlider.setValue(10);
        corValueLabel.setText(String.valueOf(1.0f));
        ballCorSlider.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        ballCorSlider.addChangeListener(e -> {
            int corSlidervalue = ballCorSlider.getValue();
            corValueLabel.setText(String.valueOf((float) corSlidervalue / 10));
            float value = (float) corSlidervalue / scaleCor;
            balls.get(balls.size() - 1).setCorConstant(value);
        });
        cubeSizeSlider = new JSlider();
        int cubeScaleMinValue = 1;
        int cubeScaleMaxValue = 10;
        cubeSizeSlider.setValue(1);
        sizeValueLabel.setText(String.valueOf(1.0f));
        cubeSizeSlider.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        cubeSizeSlider.setMinimum(cubeScaleMinValue);
        cubeSizeSlider.setMaximum(cubeScaleMaxValue);
        cubeSizeSlider.setMajorTickSpacing(1);
        cubeSizeSlider.setMinorTickSpacing(0);
        cubeSizeSlider.setPaintTicks(true);
        cubeSizeSlider.addChangeListener(e -> {
            float sizeSliderValue = (float) cubeSizeSlider.getValue();
            sizeValueLabel.setText(String.valueOf(sizeSliderValue));
            float convertedValue = (float) (Math.log(sizeSliderValue) / Math.log(cubeScaleMaxValue));
            float scaleValue = 1.0f + 0.2f * convertedValue;
            renderer.setCubeScale(scaleValue);
        });
    }

    private void initPanel(){
        Font font = new Font("Verdana", Font.BOLD, 14);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createHorizontalGlue());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ballMassLabel.setFont(font);
        massValueLabel.setFont(font);
        panel.add(ballMassLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(ballMassSlider);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(massValueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        ballRadiusLabel.setFont(font);
        radiusValueLabel.setFont(font);
        panel.add(ballRadiusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(ballRadiusSlider);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(radiusValueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        ballCorLabel.setFont(font);
        corValueLabel.setFont(font);
        panel.add(ballCorLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(ballCorSlider);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(corValueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        cubeSizeLabel.setFont(font);
        sizeValueLabel.setFont(font);
        panel.add(cubeSizeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(cubeSizeSlider);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(sizeValueLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        addBallButton.setPreferredSize(new Dimension(150, 30));
        addBallButton.setFont(font);
        panel.add(addBallButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        removeBallButton.setFont(font);
        removeBallButton.setPreferredSize(new Dimension(150, 30));
        panel.add(removeBallButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        resetSceneButton.setFont(font);
        resetSceneButton.setPreferredSize(new Dimension(150, 30));
        panel.add(resetSceneButton);

    }





    private void initLabels(){
        ballMassLabel = new JLabel("Hmotnost");
        ballRadiusLabel = new JLabel("Poloměr");
        ballCorLabel = new JLabel("Koeficient restituce");
        cubeSizeLabel = new JLabel("Velikost kostky");
        corValueLabel = new JLabel();
        massValueLabel = new JLabel();
        radiusValueLabel = new JLabel();
        sizeValueLabel = new JLabel();
    }

    public JSlider getBallMassSlider() {
        return ballMassSlider;
    }

    public void setBallMassSlider(JSlider ballMassSlider) {
        this.ballMassSlider = ballMassSlider;
    }

    public JSlider getBallRadiusSlider() {
        return ballRadiusSlider;
    }

    public void setBallRadiusSlider(JSlider ballRadiusSlider) {
        this.ballRadiusSlider = ballRadiusSlider;
    }

    public JSlider getBallCorSlider() {
        return ballCorSlider;
    }

    public void setBallCorSlider(JSlider ballCorSlider) {
        this.ballCorSlider = ballCorSlider;
    }

    public JSlider getCubeSizeSlider() {
        return cubeSizeSlider;
    }

    public void setCubeSizeSlider(JSlider cubeSizeSlider) {
        this.cubeSizeSlider = cubeSizeSlider;
    }
}
