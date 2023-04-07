package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Solver {

  private static final int ROWS = 12;
  private static final int COLUMNS = 11;

  Integer[][] table;
  int itemToFocusOn = -1;

  public Solver(Integer[][] tab) {
    this.table = tab;
    setFocus();
  }

  private void setFocus() {
    double smallestDistance = Integer.MAX_VALUE;
    int focus = 1;

    for (int num = 1; num < 5; num++) {
      double totalDistance = 0;
      for (int row = 0; row < ROWS; row++) {
        for (int col = 0; col < COLUMNS; col++) {
          if (table[row][col] != null && table[row][col] == num) {
            totalDistance += findNearestMate(col, row, table);
          }
        }
      }
      if (totalDistance < smallestDistance && totalDistance != 0) {
        smallestDistance = totalDistance;
        focus = num;
      }
    }
    itemToFocusOn = focus;
  }

  public ArrayList<Point> solve() {
    return null;
  }

  private HashSet<Point> getMovesThatAffectItemOfFocus() {
    ArrayList<Point> moves = getMoves(table);
  }

  private boolean needToUpdateFocus(){
    int focusCounter = 0;
    for(int i = 0; i<ROWS; i++){
      for(int j = 0; j<COLUMNS; j++){
        if(this.table[i][j] != null && this.table[i][j] == itemToFocusOn){
          focusCounter++;
        }
      }
    }
    return focusCounter == 0;
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

  // Checks that no item gets left alone. Returns -1 or 1;
  private double calculateScore1(Integer[][] table) {
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
        }
      }
    }
    //Check that there are no single values
    for (Map.Entry<Integer, Integer> entry : numbersFreq.entrySet()) {
      if (entry.getValue() == 1) return -1;
    } 
    return 1;
  }

  private double calculateScore2(Integer[][] table) {
    double focusCounter = 0;
    double pairsFound = 0;
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] != 0) {
          int num = table[row][col];
          if(num == itemToFocusOn){
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

    if(focusCounter == 1){
      return -1;
    }
    if(focusCounter == 0){
        return 2;
    }
    if(focusCounter == pairsFound){
      return 2;
    }
    double distanceScore = calcluateDistanceScore(table);  
    return (pairsFound / focusCounter) + distanceScore;
  }

  //
  private double calcluateDistanceScore(Integer[][] table){
    //Lower is better;
    double totalDistance = 0;
    double counter = 0;
    for(int row = 0; row < ROWS; row++){
        for(int col = 0; col < COLUMNS; col++){
            if(table[row][col] != null && table[row][col] == itemToFocusOn){
              totalDistance += findNearestMate(col, row, table);
              counter++;
            }
        }
    }
    if(counter == 0){
      return 2;
    }
    double score = counter/totalDistance;
    return (score);
  }

  private double findNearestMate(int x, int y, Integer[][] table){
    Point nearest = bfs(table, new Point(y, x), table[y][x]);
    if(nearest == null){
      return -1;
    }
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
                if(curr.y != start.y && curr.x != start.x){
                  return curr;
                }
            }
            for (int[] dir : dirs) {
                int x = curr.x + dir[0];
                int y = curr.y + dir[1];
                if (isValid(x, y, ROWS, COLUMNS) && !visited[y][x]) {
                  if(Math.abs(start.x - x) <= 1){
                    queue.offer(new Point(y, x));
                    visited[y][x] = true;
                  }
                }
            }
        }
    }
    return null;
}

private boolean isValid(int x, int y, int rows, int cols) {
    return x >= 0 && y >= 0 && x < cols && y < rows;
}

  private double distance(int x, int y, int x2, int y2){
    return (Math.sqrt(Math.pow(Math.abs((x2-x)), 2) + Math.pow((Math.abs(y2-y)), 2)));
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
}
