package com.example;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class App {

  public static void main(String[] args) {
    try (Scanner sc = new Scanner(System.in)) {
      Game game = new Game();
      /* BufferedImage img = new Robot()
        .createScreenCapture(
          new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
        );
      ImageIO.write(img, "png", new File(".\\screenshot.png")); */
      game.setFocus();
      while (true) {
        game.printTable();

        Point move = game.getBestMove();
        System.out.println("BEST MOVE: " + move.x + " " + move.y);
        if(move.x == -1){
          System.exit(-1);
        }
        game.selectItem(move.x, move.y);
        System.out.format("%n%n%n%n%n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}