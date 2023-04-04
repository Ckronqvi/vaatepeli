package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class Game {

  private static final int ROWS = 12;
  private static final int COLUMNS = 11;
  private static final double BIAS = 1;
  Integer[][] table;
  public int elements;
  int clothingToFocusOn = -1;

  public Game() {
      elements = ROWS*COLUMNS;
  }

  public Game(Integer[][] tab) {
    this.table = tab;
    elements = ROWS*COLUMNS;
  }

  public String getClothingNumber(int x, int y){
    switch(table[y][x]){
      case 1: return "Paita";
      case 2: return "Kenkä";
      case 3: return "Housut";
      case 4: return "Laukku";
    }
    return null;
  }

  public void newTable(Integer[][] tab) {
    table = tab;
  }

  private void setFocus(Integer[][] table){
    HashMap<Integer, Integer> numbersFreq = new HashMap<>(4);
    //finds the lonest clothing
     for (int row = 0; row < ROWS; row++) {
        for (int col = 0; col < COLUMNS; col++) {
          if (table[row][col] != null && table[row][col] != 0) {
            int num = table[row][col];
            if (numbersFreq.containsKey((Integer) num)) {
              int count = numbersFreq.get((Integer) num);
              numbersFreq.put((Integer) num, count + 1);
            } else {
              numbersFreq.put((Integer) num, 1);
            }
            int up = row > 0 && table[row - 1][col] != null
              ? table[row - 1][col] : -1;
            int down = row < ROWS - 1 && table[row + 1][col] != null
              ? table[row + 1][col] : -1;
            int left = col > 0 && table[row][col - 1] != null
              ? table[row][col - 1] : -1;
            int right = col < COLUMNS - 1 && table[row][col + 1] != null
              ? table[row][col + 1] : -1;
          if (!(num != up && num != down && num != left && num != right)) {
            if (numbersFreq.containsKey(num)) {
              int count = numbersFreq.get(num);
              numbersFreq.put(num, count + 1); //increment count of the number
            } else {
              numbersFreq.put(num, 1); //add the number to hashmap with count 1
            }
          }
        }
      }
     }
    int minCount = Integer.MAX_VALUE;
    int number = 1;
    for (Map.Entry<Integer, Integer> entry : numbersFreq.entrySet()) {
      int num = entry.getKey();
      int count = entry.getValue();
      if (count < minCount) { //check if the count is greater than the current max count
        minCount = count;
        number = num;
      }
    }
    this.clothingToFocusOn = number;
  }

  //Finds the least lonely clothing (1-4)
  public void setFocus() {
    HashMap<Integer, Integer> numbersFreq = new HashMap<>();
    //finds the lonest clothing
     for (int row = 0; row < ROWS; row++) {
        for (int col = 0; col < COLUMNS; col++) {
          if (table[row][col] != null && table[row][col] != 0) {
            int num = table[row][col];
            if (numbersFreq.containsKey((Integer) num)) {
              int count = numbersFreq.get((Integer) num);
              numbersFreq.put((Integer) num, count + 1);
            } else {
              numbersFreq.put((Integer) num, 1);
            }
            int up = row > 0 && table[row - 1][col] != null
              ? table[row - 1][col] : -1;
            int down = row < ROWS - 1 && table[row + 1][col] != null
              ? table[row + 1][col] : -1;
            int left = col > 0 && table[row][col - 1] != null
              ? table[row][col - 1] : -1;
            int right = col < COLUMNS - 1 && table[row][col + 1] != null
              ? table[row][col + 1] : -1;
          if (!(num != up && num != down && num != left && num != right)) {
            if (numbersFreq.containsKey(num)) {
              int count = numbersFreq.get(num);
              numbersFreq.put(num, count + 1); //increment count of the number
            } else {
              numbersFreq.put(num, 1); //add the number to hashmap with count 1
            }
          }
        }
      }
    }

    int minCount = Integer.MAX_VALUE;
    int number = 1;
    for (Map.Entry<Integer, Integer> entry : numbersFreq.entrySet()) {
      int num = entry.getKey();
      int count = entry.getValue();
      if (count < minCount) { //check if the count is greater than the current max count
        minCount = count;
        number = num;
      }
    }
    this.clothingToFocusOn = number;
  }

  public void updateTable() {
    // Handle drop.
    Integer[][] newTable = new Integer[ROWS][COLUMNS];
    int row = ROWS - 1;
    int col = COLUMNS - 1;

    for (int i = 0; i < COLUMNS; i++) {
      for (int j = ROWS - 1; j >= 0; j--) {
        if (table[j][i] != null && table[j][i] != 0) {
          newTable[row--][i] = table[j][i];
        }
      }
      row = ROWS - 1;
    }

    int elementCounter = 0;
    // Handle moving to right.
    Integer newTable2[][] = new Integer[ROWS][COLUMNS];
    for (int i = COLUMNS - 1; i >= 0; i--) {
      if (!(newTable[ROWS - 1][i] == null || newTable[ROWS - 1][i] == 0)) {
        for (int j = 0; j < ROWS; j++) {
          newTable2[j][col] = newTable[j][i];
          elementCounter++;
        }
        col--;
      }
    }
    this.table = newTable2;
    this.elements = elementCounter;
  }

  public void printTable() {
    System.out.format("_________________________________%n%n");
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (table[i][j] == null || table[i][j] == 0) System.out.print(
          "_"
        ); else System.out.print(table[i][j]);
      }
      System.out.format("%n");
    }
    System.out.println("_________________________________");
  }

  private HashSet<Point> selectItem(
    Integer[][] table,
    int x,
    int y,
    HashSet<Point> visited
  ) {
    if (y != ROWS - 1) {
      if (
        table[y][x] == table[y + 1][x] && !visited.contains(new Point(y + 1, x))
      ) {
        visited.add(new Point(y + 1, x));
        selectItem(table, x, y + 1, visited);
      }
    }

    if (x != COLUMNS - 1) {
      if (
        table[y][x] == table[y][x + 1] && !visited.contains(new Point(y, x + 1))
      ) {
        visited.add(new Point(y, x + 1));
        selectItem(table, x + 1, y, visited);
      }
    }

    if (y != 0) {
      if (
        table[y][x] == table[y - 1][x] && !visited.contains(new Point(y - 1, x))
      ) {
        visited.add(new Point(y - 1, x));
        selectItem(table, x, y - 1, visited);
      }
    }
    if (x != 0) {
      if (
        table[y][x] == table[y][x - 1] && !visited.contains(new Point(y, x - 1))
      ) {
        visited.add(new Point(y, x - 1));
        selectItem(table, x - 1, y, visited);
      }
    }
    return visited;
  }

  public void selectItem(int x, int y) {
    HashSet<Point> temp = new HashSet<Point>();
    temp.add(new Point(y, x));
    HashSet<Point> remove = selectItem(this.table, x, y, temp);
    addZeroesToRemovedPoints(this.table, remove);
    updateTable();
  }

  public Point getBestMove() {
    if (clothingToFocusOn == -1) {
      setFocus();
    }
    ArrayList<Point> movesList = getMoves(table); //Minimum set of all the possible moves.
    double highestScore = Integer.MIN_VALUE;
    Point bestMove = new Point(-1, -1);
    bestMove.score = -1;
    for (Point p : movesList) {
      Integer[][] virtualTable = Arrays
        .stream(this.table)
        .map(Integer[]::clone)
        .toArray(Integer[][]::new);
        HashSet<Point> tempSet = new HashSet<>();
        tempSet.add(p);
      HashSet<Point> toRemove = selectItem(
        virtualTable,
        p.x,
        p.y,
        tempSet
      );
      addZeroesToRemovedPoints(virtualTable, toRemove);
      virtualTable = updateVirtualTable(virtualTable);
      int digDeeper = 1;
      if(elements <= ((double)(COLUMNS*ROWS)) / 1.5){
        digDeeper = 5;
      } else if(elements <= ((double)(COLUMNS*ROWS))/2){
        if(elements <= ((double)(COLUMNS*ROWS)) / 4){
          digDeeper = 10;
        } else {
          digDeeper = 8;
        }
      }
      double temp = score(virtualTable, digDeeper);
      // from left to right
      if (temp >= highestScore) {
        if(temp == highestScore){
          if(p.x < bestMove.x){
            highestScore = temp;
            bestMove = p;
            bestMove.score = highestScore;
          }
        } else {
          highestScore = temp;
          bestMove = p;
          bestMove.score = highestScore;
        }
      }
    }
    return bestMove;
  }

  private Integer[][] updateVirtualTable(Integer[][] table) {
    // Handle drop.
    Integer[][] newTable = new Integer[ROWS][COLUMNS];
    int row = ROWS - 1;
    int col = COLUMNS - 1;

    for (int i = 0; i < COLUMNS; i++) {
      for (int j = ROWS - 1; j >= 0; j--) {
        if (table[j][i] != null && table[j][i] != 0) {
          newTable[row--][i] = table[j][i];
        }
      }
      row = ROWS - 1;
    }

    // Handle moving to right.
    Integer newTable2[][] = new Integer[ROWS][COLUMNS];
    for (int i = COLUMNS - 1; i >= 0; i--) {
      if (!(newTable[ROWS - 1][i] == null || newTable[ROWS - 1][i] == 0)) {
        for (int j = 0; j < ROWS; j++) {
          newTable2[j][col] = newTable[j][i];
        }
        col--;
      }
    }
    return newTable2;
  }

  private void addZeroesToRemovedPoints(
    Integer[][] table,
    HashSet<Point> toRemove
  ) {
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (toRemove.contains(new Point(i, j))) {
          table[i][j] = 0;
        }
      }
    }
  }

  private double score(Integer[][] table, int digDeeper){
    double score1 = calculateScore(table);
    double score2 = calculateScore2(table);
    double score3 = calculateScore3(table);
    double score4 = 1;
    if(digDeeper > 0){
       //score4 = seeTheFuture(table, digDeeper);
    }
    double overallScore = score1 * ((score2+score3)* (score4 > 0 ? score4 : (score1 >= 0 ? score4 : Math.abs(score4))));
    return overallScore;
  }

  // Score is calculated by the amount of non-single items in the table
  // focusing on the clothingToFocusOn
  private double calculateScore(Integer[][] table) {
    double counter = 0;
    double pairsFound = 0;
    HashMap<Integer, Integer> numbersFreq = new HashMap<>(4);
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] != 0) {

          int num = table[row][col];
          if (numbersFreq.containsKey((Integer) num)) {
            int count = numbersFreq.get((Integer) num);
            numbersFreq.put((Integer) num, count + 1);
          } else {
            numbersFreq.put((Integer) num, 1);
          }
          counter++;
          int up = row > 0 && table[row - 1][col] != null
                ? table[row - 1][col] : -1;
            int down = row < ROWS - 1 && table[row + 1][col] != null
                ? table[row + 1][col] : -1;
            int left = col > 0 && table[row][col - 1] != null
                ? table[row][col - 1] : -1;
            int right = col < COLUMNS - 1 && table[row][col + 1] != null
                ? table[row][col + 1] : -1;
          // found pair
          if (!(num != up && num != down && num != left && num != right)) {
            pairsFound++;
          }
        }
      }
    }

    double score1 = pairsFound/(counter != 0 ? counter : 1);
    //Check that there are no single values
    for (Map.Entry<Integer, Integer> entry : numbersFreq.entrySet()) {
      if (entry.getValue() == 1) score1 = -1;
    } 
    return score1;
  }

  private double seeTheFuture(Integer[][] table, int digDeeper){
  
    ArrayList<Point> moves = getMoves(table);
    if(moves.isEmpty() && getElements(table) == 0){
      return 2;
    }
    int digTemp = digDeeper;
    double highest = Integer.MIN_VALUE;
    Point bestMove = new Point(-1,-1);
    bestMove.score = -1;
    for(Point p : moves){
      Integer[][] virtualTable = Arrays
        .stream(this.table)
        .map(Integer[]::clone)
        .toArray(Integer[][]::new);
        HashSet<Point> tempSet = new HashSet<>();
        tempSet.add(p);
      HashSet<Point> toRemove = selectItem(
        virtualTable,
        p.x,
        p.y,
        tempSet
      );
      addZeroesToRemovedPoints(table, toRemove);
      updateVirtualTable(table);
      double tempScore = score(table, --digDeeper);
      if (tempScore > highest){
        highest = tempScore;
        bestMove = p;
        bestMove.score = tempScore;
      }
      digDeeper = digTemp;
    }
    return bestMove.score;  
  }

  private int getElements(Integer[][] table){
    int counter = 0;
    for(int row = 0; row < ROWS; row++){
      for(int col = 0; col < COLUMNS; col++){
        if(table[row][col] != null && table[row][col] != 0){
          counter++;
        }
      }
    }
    return counter;
  }

  // focuses on the item of focus
  private double calculateScore2(Integer[][] table) {
    double focusCounter = 0;
    double pairsFound = 0;
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] != 0) {
          int num = table[row][col];
          if(num == clothingToFocusOn){
            focusCounter++;
            int up = row > 0 && table[row - 1][col] != null
                ? table[row - 1][col]
                : -1;
            int down = row < ROWS - 1 && table[row + 1][col] != null
                ? table[row + 1][col]
                : -1;
            int left = col > 0 && table[row][col - 1] != null
                ? table[row][col - 1]
                : -1;
            int right = col < COLUMNS - 1 && table[row][col + 1] != null
                ? table[row][col + 1]
                : -1;
            if (!(num != up && num != down && num != left && num != right)) {
                pairsFound++;
            }
          }
        }
      }
    }
    if(focusCounter == 0){
        setFocus(table);
        return 2;
    }

    if(focusCounter == pairsFound){
      return 2;
    }
    return (pairsFound / focusCounter) + BIAS;
  }

  // Calculates how far away the nodes are from another node
  private double calculateScore3(Integer[][] table){
    //Lower is better;
    double totalDistance = 0;
    double counter = 0;
    for(int row = 0; row < ROWS; row++){
        for(int col = 0; col < COLUMNS; col++){
            if(table[row][col] != null && table[row][col] != 0){
              totalDistance += findNearestMate(row, col, table);
              counter++;
            }
        }
    }
    double bias = 0;
    if(elements >= ((double)(ROWS*COLUMNS))/2){
      bias = 50;
    }
    double score = counter/totalDistance;
    return (score + (score/100*bias));
  }
  private double findNearestMate(int x, int y, Integer[][] table){
    Point nearest = bfs(table, new Point(x, y), table[x][y]);
    return distance(x, y, nearest.x, nearest.y);
  }

  private Point bfs(Integer[][] table, Point start, int target) {

    boolean[][] visited = new boolean[ROWS][COLUMNS];
    Queue<Point> queue = new LinkedList<>();
    queue.offer(start);
    visited[start.y][start.x] = true;
    int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            Point curr = queue.poll();
            if (table[curr.y][curr.x] != null && table[curr.y][curr.x] == target) {
                return curr;
            }
            for (int[] dir : dirs) {
                int x = curr.x + dir[0];
                int y = curr.y + dir[1];
                if (isValid(x, y, ROWS, COLUMNS) && !visited[y][x]) {
                    queue.offer(new Point(x, y));
                    visited[y][x] = true;
                }
            }
        }
    }
    return null;
}

boolean isValid(int x, int y, int rows, int cols) {
    return x >= 0 && y >= 0 && x < cols && y < rows;
}

  private double distance(int x, int y, int x2, int y2){
    return (Math.sqrt(Math.pow(Math.abs((x2-x)), 2) + Math.pow((Math.abs(y2-y)), 2)));
  }
  private ArrayList<Point> getMoves(Integer[][] table) {
    HashSet<Point> moves = new HashSet<>();
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (!moves.contains(new Point(row, col))) {
          if (table[row][col] != null) {
            int num = table[row][col];
            int up = row > 0 && table[row - 1][col] != null
              ? table[row - 1][col]
              : -1;
            int down = row < ROWS - 1 && table[row + 1][col] != null
              ? table[row + 1][col]
              : -1;
            int left = col > 0 && table[row][col - 1] != null
              ? table[row][col - 1]
              : -1;
            int right = col < COLUMNS - 1 && table[row][col + 1] != null
              ? table[row][col + 1]
              : -1;
            if (num == up) {
              moves.add(new Point(row, col));
              moves.remove(new Point(row - 1, col));
            }
            if (num == down) {
              moves.add(new Point(row, col));
              moves.remove(new Point(row + 1, col));
            }
            if (num == left) {
              moves.add(new Point(row, col));
              moves.remove(new Point(row, col - 1));
            }
            if (num == right) {
              moves.add(new Point(row, col));
              moves.remove(new Point(row, col + 1));
            }
          }
        }
      }
    }
    return new ArrayList<Point>(moves);
  }
}
