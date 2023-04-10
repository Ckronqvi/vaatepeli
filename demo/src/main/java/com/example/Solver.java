package com.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
  int limiter = 2;

  public Solver(Integer[][] tab) {
    this.table = tab;
    setFocus(tab);
  }

  private void setFocus(Integer[][] table) {
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
    int originalFocus = itemToFocusOn;
    int originalFocus2 = itemToFocusOn; 
    for (int i = 0; i < 4; i++) {
      ArrayDeque<Point> movesStack = new ArrayDeque<>();
      Queue<Point> solvedGame = iterativeSolve(movesStack, table, 1, this.itemToFocusOn, true);
      if (solvedGame != null) {
        return new ArrayList<Point>(solvedGame);
      }
      System.out.println("Changing item of focus...");
      originalFocus++;
      if (originalFocus > 4) {
        originalFocus = 1;
      }
      this.itemToFocusOn = originalFocus;
    }
    originalFocus = originalFocus2;
    limiter = 6;
    for (int i = 0; i < 4; i++) {
      ArrayDeque<Point> movesStack = new ArrayDeque<>();
      Queue<Point> solvedGame = iterativeSolve(movesStack, table, 1, this.itemToFocusOn, false);
      if (solvedGame != null) {
        return new ArrayList<Point>(solvedGame);
      }
      System.out.println("Changing item of focus...");
      originalFocus++;
      if (originalFocus > 4) {
        originalFocus = 1;
      }
      this.itemToFocusOn = originalFocus;
    }

    return null;
  }

  private Queue<Point> iterativeSolve(ArrayDeque<Point> movesSoFar, Integer[][] table, int depth, int itemToFocusOn,
      boolean fastSolve) {
    try {
      // Copy the table so we don't mess things up
      Integer[][] tableCopy = Arrays.stream(table)
          .map(Integer[]::clone)
          .toArray(Integer[][]::new);
      ArrayList<Point> possibleMoves = getMoves(tableCopy);
      ArrayList<Point> bestMoves = new ArrayList<>(possibleMoves.size());
      for (Point p : possibleMoves) {
        Integer[][] tempTable = Arrays.stream(tableCopy)
            .map(Integer[]::clone)
            .toArray(Integer[][]::new);
        HashSet<Point> remove = new HashSet<>();
        remove.add(p);
        selectItem(tempTable, p.x, p.y, remove);
        addZeroesToRemovedPoints(tempTable, remove);
        tempTable = updateVirtualTable(tempTable);
        double tempScore = calculateScore(tempTable);
        if (tempScore > 0.8) {
          //System.out.println("DEPTH: " + depth + " SCORE: " + tempScore);
          p.score = tempScore;
          /* p.table = Arrays.stream(tempTable)
              .map(Integer[]::clone)
              .toArray(Integer[][]::new); */
          p.table = tempTable;
          p.depth = depth;
          bestMoves.add(p);
        }
      }
      bestMoves.sort(new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
          return (Double.compare(o1.score, o2.score));
        }
      });

      if (bestMoves.size() == 0) {
        return null;
      }
      // Solved
      if (bestMoves.get(0).score > 1000) {
        movesSoFar.push(bestMoves.get(0));
        return movesSoFar;
      }

      for (int i = 0; i < limiter && i < bestMoves.size(); i++) {
        movesSoFar.push(bestMoves.get(i));
        Queue<Point> tempMoves = iterativeSolve(movesSoFar, bestMoves.get(i).table, depth + 1, itemToFocusOn,
            fastSolve);
        if (null != tempMoves) {
          return movesSoFar;
        }
        if (this.itemToFocusOn != itemToFocusOn) {
          this.itemToFocusOn = itemToFocusOn;
        }
        // Was not a good move
        movesSoFar.pop();
        if (fastSolve) {
          if (depth >= 10 && depth <= 20) {
            return null;
          }
        }
        // Skip and try with different item of focus.
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage() + " CAUSE: " + e.getCause());
      System.exit(1);
    }
    return null;
  }

  private double calculateScore(Integer[][] table) {
    double score1 = calculateScore1(table);
    double score2 = calculateScore2(table);
    return score1 * score2;
  }

  private HashSet<Point> selectItem(
      Integer[][] table,
      int x,
      int y,
      HashSet<Point> visited) {
    if (y != ROWS - 1) {
      if (table[y][x] == table[y + 1][x] && !visited.contains(new Point(y + 1, x))) {
        visited.add(new Point(y + 1, x));
        selectItem(table, x, y + 1, visited);
      }
    }

    if (x != COLUMNS - 1) {
      if (table[y][x] == table[y][x + 1] && !visited.contains(new Point(y, x + 1))) {
        visited.add(new Point(y, x + 1));
        selectItem(table, x + 1, y, visited);
      }
    }

    if (y != 0) {
      if (table[y][x] == table[y - 1][x] && !visited.contains(new Point(y - 1, x))) {
        visited.add(new Point(y - 1, x));
        selectItem(table, x, y - 1, visited);
      }
    }
    if (x != 0) {
      if (table[y][x] == table[y][x - 1] && !visited.contains(new Point(y, x - 1))) {
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

  // Checks that no item gets left alone. Returns -1 or 1 or 100000 if solved;
  private double calculateScore1(Integer[][] table) {
    HashMap<Integer, Integer> numbersFreq = new HashMap<>(4);
    int counter = 0;
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] != 0) {
          counter++;
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
    // Solved
    if (counter == 0) {
      return 1000000;
    }
    // Check that there are no single values
    for (Map.Entry<Integer, Integer> entry : numbersFreq.entrySet()) {
      if (entry.getValue() == 1)
        return -1;
    }
    return 1;
  }

  private double calculateScore2(Integer[][] table) {
    double focusCounter = 0;
    double pairsFound = 0;
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] != itemToFocusOn) {
          int num = table[row][col];
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

    if (focusCounter == 1) {
      return -1;
    }
    if (focusCounter == 0) {
      setFocus(table);
      return 2;
    }
    if (focusCounter == pairsFound) {
      return 2;
    }
    double distanceScore = calcluateDistanceScore(table);
    return (pairsFound / focusCounter) + distanceScore;
  }

  private double calcluateDistanceScore(Integer[][] table) {
    // Lower is better;
    double totalDistance = 0;
    double counter = 0;
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLUMNS; col++) {
        if (table[row][col] != null && table[row][col] == itemToFocusOn) {
          totalDistance += findNearestMate(col, row, table);
          counter++;
        }
      }
    }
    if (counter == 0) {
      return 1;
    }
    double score = counter / totalDistance;
    return (score);
  }

  private double findNearestMate(int x, int y, Integer[][] table) {
    Point nearest = bfs(table, new Point(y, x), table[y][x]);
    if (nearest == null) {
      return -1;
    }
    return distance(x, y, nearest.x, nearest.y);
  }

  private Point bfs(Integer[][] table, Point start, int target) {

    boolean[][] visited = new boolean[ROWS][COLUMNS];
    Queue<Point> queue = new LinkedList<>();
    queue.offer(start);
    visited[start.y][start.x] = true;
    int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    while (!queue.isEmpty()) {
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        Point curr = queue.poll();
        if (table[curr.y][curr.x] != null && table[curr.y][curr.x] == target) {
          if (curr.y != start.y && curr.x != start.x) {
            return curr;
          }
        }
        for (int[] dir : dirs) {
          int x = curr.x + dir[0];
          int y = curr.y + dir[1];
          if (isValid(x, y, ROWS, COLUMNS) && !visited[y][x]) {
            if (Math.abs(start.x - x) <= 1) {
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

  private double distance(int x, int y, int x2, int y2) {
    return (Math.sqrt(Math.pow(Math.abs((x2 - x)), 2) + Math.pow((Math.abs(y2 - y)), 2)));
  }

  private void addZeroesToRemovedPoints(
      Integer[][] table,
      HashSet<Point> toRemove) {
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
