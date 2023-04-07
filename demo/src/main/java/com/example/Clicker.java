package com.example;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

public class Clicker {
    private static final double startingX = 832;
    private static final double startingY = 325;
    Robot robot;
    public Clicker() throws AWTException{
        robot = new Robot();
    }

    public void clickCoordinates(int x, int y) throws Exception {
        double clickX = startingX + ((double)x * 33.636);
        double clickY = startingY + ((double)y * 33.5);
        // Move the mouse pointer to the desired coordinate in a more human-like way
        Random rand = new Random();
        robot.mouseMove((int) clickX + rand.nextInt(2), (int) clickY + rand.nextInt(2));
        Thread.sleep(500 + rand.nextInt(50));
        // Simulate a left-click on the coordinate
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1000 + rand.nextInt(100));
    }
    
}
