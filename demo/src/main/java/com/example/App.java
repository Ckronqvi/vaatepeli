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
      Game game = new Game(ComputerVision.getTable());
      game.setFocus2();
      boolean ended = false;
      Clicker click = new Clicker();

      System.out.println("PELI ALKAA 5 SEKUNNIN KULUTTUA, AVAA GOSUPERMODEL IKKUNA!!!");
      //Thread.sleep(5000);
      while (!ended) {
        Point move = game.getBestMove();
        System.out.println("FOCUS: " + game.clothingToFocusOn);
        game.printTable();
        System.out.println("BEST MOVE: " + (move.x + 1) + " " + (move.y + 1));
        System.out.println(game.getClothingNumber(move.x, move.y));
        if(move.x == -1){
          ended = true;
          if(game.elements != 0){
            System.out.println("EI ONNISTUNUT, VALITSE UUSI PELI! (ANTEEKSI OLEN VIELÄ MELKO PASKA)");
          }
        } else {
          game.selectItem(move.x, move.y);
          //click.clickCoordinates(move.x, move.y);
          System.out.format("%n%n");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
