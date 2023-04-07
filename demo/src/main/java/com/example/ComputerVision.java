package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;

public class ComputerVision {
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  static String housut = ".\\pictures\\templates\\pantsTest.png";
  static String kenka = ".\\pictures\\templates\\shoeTest.png";
  static String laukku = ".\\pictures\\templates\\bagTest.png";
  static String paita = ".\\pictures\\templates\\shirtTest.png";
  static String poyta = ".\\pictures\\jonna.png";

  //paita = 1, kenkä = 2. housut = 3, laukku = 4.
  public static Integer[][] getTable() {
    Mat table = Imgcodecs.imread(poyta);
    Mat templateImageShoe = Imgcodecs.imread(kenka);
    Mat templateImagePants = Imgcodecs.imread(housut);
    Mat templateImageBag = Imgcodecs.imread(laukku);
    Mat templateImageShirt = Imgcodecs.imread(paita);

    Mat tableGray = new Mat();
    Mat templateGrayShoe = new Mat();
    Mat templateGrayPants = new Mat();
    Mat templateGrayBag = new Mat();
    Mat templateGrayShirt = new Mat();

    Imgproc.cvtColor(table, tableGray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.cvtColor(
      templateImageShoe,
      templateGrayShoe,
      Imgproc.COLOR_BGR2GRAY
    );
    Imgproc.cvtColor(
      templateImagePants,
      templateGrayPants,
      Imgproc.COLOR_BGR2GRAY
    );
    Imgproc.cvtColor(templateImageBag, templateGrayBag, Imgproc.COLOR_BGR2GRAY);
    Imgproc.cvtColor(
      templateImageShirt,
      templateGrayShirt,
      Imgproc.COLOR_BGR2GRAY
    );

    // Perform template matching
    Mat resultShoe = new Mat();
    Mat resultPants = new Mat();
    Mat resultBag = new Mat();
    Mat resultShirt = new Mat();

    Imgproc.matchTemplate(
      tableGray,
      templateGrayShoe,
      resultShoe,
      Imgproc.TM_CCOEFF_NORMED
    );
    Imgproc.matchTemplate(
      tableGray,
      templateGrayPants,
      resultPants,
      Imgproc.TM_CCOEFF_NORMED
    );
    Imgproc.matchTemplate(
      table,
      templateImageBag,
      resultBag,
      Imgproc.TM_CCOEFF_NORMED
    );
    Imgproc.matchTemplate(
      table,
      templateImageShirt,
      resultShirt,
      Imgproc.TM_CCOEFF_NORMED
    );

    // Normalize
    Core.normalize(
      resultShirt,
      resultShirt,
      0,
      1,
      Core.NORM_MINMAX,
      -1,
      new Mat()
    );
    Core.normalize(resultBag, resultBag, 0, 1, Core.NORM_MINMAX, -1, new Mat());
    Core.normalize(
      resultShoe,
      resultShoe,
      0,
      1,
      Core.NORM_MINMAX,
      -1,
      new Mat()
    );
    Core.normalize(
      resultPants,
      resultPants,
      0,
      1,
      Core.NORM_MINMAX,
      -1,
      new Mat()
    );

    int minY = Math.min(
      Math.min(resultBag.rows(), resultPants.rows()),
      Math.min(resultShirt.rows(), resultShoe.rows())
    );
    int minX = Math.min(
      Math.min(resultBag.cols(), resultPants.cols()),
      Math.min(resultShirt.cols(), resultShoe.cols())
    );

    // Define a threshold value to filter out weak matches
    double threshold = 0.95;
    List<Rect> rectangles = new ArrayList<Rect>();
    try {
      for (int y = 0; y < minY; y++) {
        for (int x = 0; x < minX; x++) {
          double[] matchValueShoe = resultShoe.get(y, x);
          double[] matchValuePants = resultPants.get(y, x);
          double[] matchValueShirt = resultShirt.get(y, x);
          double[] matchValueBag = resultBag.get(y, x);
          if (
            matchValueShirt[0] > threshold ||
            matchValueShoe[0] > threshold ||
            matchValuePants[0] > threshold ||
            matchValueBag[0] > threshold
          ) {
            rectangles.add(
              new Rect(
                x,
                y,
                templateImagePants.cols(),
                templateImagePants.rows()
              )
            );
            // add it twice for the grouping to work properly
            rectangles.add(
              new Rect(
                x,
                y,
                templateImagePants.cols(),
                templateImagePants.rows()
              )
            );
          }
        }
      }
    } catch (Exception e) {
      System.out.println("ERROR - :[" + e.getMessage());
    }

    // Group the rectangles to get rid of duplicates
    MatOfRect matRect = new MatOfRect();
    matRect.fromList(rectangles);

    // create a mat of int
    MatOfInt weights = new MatOfInt();
    weights.create(matRect.rows(), 1, CvType.CV_32SC1);

    int groupThreshold = 1;
    Objdetect.groupRectangles(matRect, weights, groupThreshold, 0.2);
    List<Rect> groupedRectangles = matRect.toList();

    //Sort the list
    groupedRectangles.sort(
      new Comparator<Rect>() {
        @Override
        public int compare(Rect r1, Rect r2) {
          int diff = Math.abs(r1.y - r2.y);
          int result;
          if (diff <= 10) {
            result = Integer.compare(r1.x, r2.x);
          } else {
            result = Integer.compare(r1.y, r2.y);
          }
          return result;
        }
      }
    );
    System.out.println("SIZE (MUST BE 132): " + groupedRectangles.size());
    Integer[][] grid = new Integer[12][11];
    int row = 0;
    int col = 0;
    double newThreshold = 0.8;
    for (Rect r : groupedRectangles) {
      double[] matchValueShoe = resultShoe.get(r.y, r.x);
      double[] matchValuePants = resultPants.get(r.y, r.x);
      double[] matchValueShirt = resultShirt.get(r.y, r.x);
      double[] matchValueBag = resultBag.get(r.y, r.x);

      if (matchValueShirt[0] > newThreshold) {
        grid[row][col++] = 1;
        Imgproc.rectangle(table, r.tl(), r.br(), new Scalar(0, 0, 255), 2);
      }
      if (matchValueShoe[0] > newThreshold) {
        grid[row][col++] = 2;
        Imgproc.rectangle(table, r.tl(), r.br(), new Scalar(0, 255, 0), 2);
      }
      if (matchValuePants[0] > newThreshold) {
        grid[row][col++] = 3;
        Imgproc.rectangle(table, r.tl(), r.br(), new Scalar(255, 0, 0), 2);
      }
      if (matchValueBag[0] > newThreshold) {
        grid[row][col++] = 4;
        Imgproc.rectangle(table, r.tl(), r.br(), new Scalar(100, 100, 20), 2);
      }
      if (col >= 11) {
        col = 0;
        row++;
      }
    }
    return grid;
  }
}
