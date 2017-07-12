//import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;

public class Snake extends PApplet {

	Point[] snakePoints = new Point[1000];
	ArrayList<Point> foodPoints = new ArrayList<Point>();
	int snakeSize;
	int headx;
	int heady;
	int speed;
	int dirx;
	int diry;
	int windowSize = 500;
	int time;
	boolean paused = false;


	public void settings() {
		size(500,500);
	}

	public void setup() {
		//initialize all variables
		size(windowSize,windowSize);
		for (int i = 0; i < 1000; i++) snakePoints[i] = null;
		headx = 250;
		heady = 250;
		speed = 10;
		dirx = 1;
		diry = 0;
		snakeSize = 0;
		newFood();
		time = 0;
	}

	public void draw() {
		time++;
		if (time % 2 == 0 && !paused) {
			background(0,200,200);
			checkFoodCollision();
			updateFoodDrawings();
			move();
			makeMove();
			drawHead();
			drawBody();
			scooch();
			time = 0;
		}
	}

	public void newFood() {
		Random rand = new Random();
		boolean good = false; //boolean used to make sure we don't spawn a new apple on an index occupied by the snake's body
		int x = 0;
		int y = 0;
		while (good == false) {
			good = true;
			x= rand.nextInt(windowSize/10) * 10;
			y=rand.nextInt(windowSize/10) * 10;
			if (snakeSize == 0) good = true;
			for (int i = 0; i < snakeSize; i++) {
				if (snakePoints[i].x == x && snakePoints[i].y == y) good = false;
				if (foodPoints.get(0).x == x && foodPoints.get(0).y == y) good = false;
			}
		}

		foodPoints.add(new Point(x,y));
	}

	public void updateFoodDrawings() {
		for (Point x : foodPoints) {
			fill(255,0,0);
			rect(x.x, x.y, 10,10);
		}
	}

	public void addNewBody() {
		snakeSize++;
		scooch();
	}

	public void scooch() {
		for (int i = snakeSize - 1; i > 0; i--) {
			snakePoints[i] = snakePoints[i - 1];
		}
		snakePoints[0] = new Point(headx, heady);
	}

	public void checkFoodCollision() {
		int indexRemove = -1;
		for (int i = 0; i < foodPoints.size(); i++) {
			Point x = foodPoints.get(i);
			if (x.x == headx && x.y == heady) {
				indexRemove = i;
				newFood();
				addNewBody();
			}
		}
		if (indexRemove != -1) foodPoints.remove(indexRemove);
	}

	public void move() {
		headx += (dirx * speed);
		heady += (diry * speed);
	}

	public void drawHead() {
		fill(0,0,0);
		if (headx < 0) headx = width - 10;
		if (heady < 0) heady = height - 10;
		if (headx > width - 10) headx = 0;
		if (heady > height - 10) heady = 0;
		for (int i = 1; i < snakeSize; i++) {
			if (headx == snakePoints[i].x && heady == snakePoints[i].y) {
				for (int j = 0; j < 1000; j++) snakePoints[i] = null;
				snakeSize = 0;
			}
		}
		rect(headx, heady, 10, 10);
	}

	public void drawBody() {
		for (int i = 0; i < snakeSize; i++) {
			fill(0,300,i*3);
			rect(snakePoints[i].x, snakePoints[i].y, 10,10);
		}
	}


	
	public void keyPressed() {
		if(key == CODED)
		{
			if (keyCode == LEFT && dirx != 1){
				dirx = -1;
				diry = 0;
			}
			else if(keyCode == RIGHT && dirx != -1){
				dirx = 1;
				diry = 0;
			}
			else if (keyCode == UP && diry != 1) {
				diry = -1;
				dirx = 0;
			}
			else if (keyCode == DOWN && diry != -1) {
				diry = 1;
				dirx = 0;
			}
		}
		else paused = !paused;
	}
	 

	public void makeMove() {
		Point food = foodPoints.get(0);
		boolean leftInv = false;
		boolean rightInv = false;
		boolean upInv = false;
		boolean downInv = false;
		
		int val = headx - 10;
		if (val < 0) val = width;
		for (int i = 0; i < snakeSize; i++) {
			if (snakePoints[i].x == val && snakePoints[i].y == heady) {
				leftInv = true;
			}
		} 
		val = headx + 10;
		if (val > width) val = 0;
		for (int i = 0; i < snakeSize; i++) {
			if (snakePoints[i].x == val && snakePoints[i].y == heady) {
				rightInv = true;
			}
		}
		
		val = heady + 10;
		if (val > height) val = 0;
		for (int i = 0; i < snakeSize; i++) {
			if (snakePoints[i].y == val && snakePoints[i].x == headx) {
				downInv = true;
			}
			
		}
		
		val = heady - 10;
		if (val < 0) val = height;
		for (int i = 0; i < snakeSize; i++) {
			if (snakePoints[i].y == val && snakePoints[i].x == headx){
				upInv = true;
			}
		}
		//if up is invalid and we're going up or down is invalid and we're going down
		if ((upInv && diry == -1) || (downInv && diry == 1)) {
			if (!rightInv) turnRight();
			else turnLeft();
		}
		//right is invalid and we're going right or left is invalid and we're going left
		else if ((rightInv && dirx == 1) || (leftInv && dirx == -1)) {
			if (!upInv) turnUp();
			else turnDown();
		}
		//just go where the food is
		else {
			if (food.x - headx < 0 && !leftInv &&  dirx != 1) {
				dirx = -1;
				diry = 0;
			}
			else if (food.x - headx > 0 && !rightInv && dirx != -1) {
				dirx = 1;
				diry = 0;
			}
			else if (food.y - heady < 0 && !upInv && diry != 1) {
				diry = -1;
				dirx = 0;
			}
			else if (food.y - heady > 0 && !downInv && diry != -1) {
				diry = 1;
				dirx = 0;
			}
		}
		
	}
	
	public  void turnRight() {
		dirx = 1;
		diry = 0;
	}
	
	public  void turnUp() {
		diry = -1;
		dirx = 0;
	}
	
	public  void turnDown() {
		diry = 1;
		dirx = 0;
	}
	
	public  void turnLeft() {
		dirx = -1;
		diry = 0;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PApplet.main("Snake");
	}
}








class Point {
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
