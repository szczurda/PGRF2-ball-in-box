package projekt;

import lvl0fixpipeline.global.LwjglWindow;

import javax.sound.sampled.LineUnavailableException;

public class App {
    public static void main(String[] args) throws LineUnavailableException {
        new LwjglWindow(new Renderer(), false);
    }
}
