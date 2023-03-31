package com.example;

public class Point {
    int x;
        int y;
        double score = 0;
        Point(int y, int x){
             this.x = x;
             this.y = y;
        }

        @Override
        public int hashCode(){
            return (x*10) + (y);
        }

        @Override
        public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
        }
        
        @Override
        public String toString(){
            return x + ", " + y;
        }
}

