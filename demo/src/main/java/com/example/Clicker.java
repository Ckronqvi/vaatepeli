package com.example;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.Random;

public class Clicker {
    private static double startingX = 520;//832; //Valid for 1920x1080
    private static double startingY = 284;//325;
    private static double xMultiplier = 33.727; //33.636;
    private static double yMultiplier = 33.25;//33.5;
    Robot robot;
    static boolean running;
    public Clicker() throws AWTException{
        robot = new Robot();
        running = true;
        adjustResolution();
    }

    private void adjustResolution(){
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screen.getWidth();
        int height = (int) screen.getHeight();
        //TODO invalid change this
        if(width != 1920 && height != 1080){
            System.out.println("THIS HAPPENED");
            startingX = 520;
            startingY = 284;
            xMultiplier = 33.727;
            yMultiplier = 33.25;
        }

    }

    public void clickCoordinates(int x, int y) throws Exception {
        double clickX = startingX + ((double)x * xMultiplier);
        double clickY = startingY + ((double)y * yMultiplier);
    
        Random rand = new Random();
        robot.mouseMove((int) clickX + rand.nextInt(4), (int) clickY + rand.nextInt(3));
        Thread.sleep(600 + rand.nextInt(100));
        // Simulate a left-click on the coordinate
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(400 + rand.nextInt(300));
    }

    public void moveMouseAway() throws Exception {
        robot.mouseMove((int)startingX-100, (int)startingY-100);
        Thread.sleep(2000);
    }

    public void exit() {
        int x = 1254;
        int y = 22;
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
