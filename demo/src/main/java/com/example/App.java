package com.example;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class App {

  public static void main(String[] args) throws Exception {
    System.out.println("BOTTI ALOITTAA ALKAA 7 SEKUNNIN KULUTTUA, AVAA GOSUPERMODEL");
    Thread.sleep(7000);
    System.out.format("%n%n");
    int counter = 0;
    int limit = 10;
    if (args.length > 0) {
      limit = Math.abs(Integer.valueOf(args[0]));
    }
    do {
      counter++;
      boolean pictureSucceed = false;
      Integer[][] grid = new Integer[12][11];

      Point start = new Point(264, 503); //WORKS WITH LAPTOP
      Point end = new Point(681, 887); //WORKS WITH LAPTOP

      while (!pictureSucceed) {
        takeSS(start, end);
        grid = ComputerVision.getTable();
        if (grid != null) {
          pictureSucceed = true;
        }
      }
      Clicker click = new Clicker();
      Solver solver = new Solver(grid);
      ArrayList<Point> movesToSolve = solver.solve();
      if (movesToSolve == null) {
        System.out.println("OHO, EI ONNISTUNUT! WAAT"); // should be rare.
        // click.exit(); //TODO Change with reload
      }
      for (int i = movesToSolve.size() - 1; i >= 0; i--) {
        if (i == 0) {
          Thread.sleep(5000);
        }
        click.clickCoordinates(movesToSolve.get(i).x, movesToSolve.get(i).y);
      }
      click.moveMouseAway();
      Thread.sleep(3000);
    } while (counter < limit);

  }

  static void takeSS(Point upperLeft, Point lowerRight) throws Exception {
    Robot robot = new Robot();
    int x = upperLeft.x;
    int y = upperLeft.y;
    int height = lowerRight.y - y;
    int width = lowerRight.x - x;
    Rectangle rect = new Rectangle(x, y, width, height);
    BufferedImage screenshot = robot.createScreenCapture(rect);
    File file = new File(".\\pictures\\jonna.png");
    ImageIO.write(screenshot, "png", file);
  }
}
