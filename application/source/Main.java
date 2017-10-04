import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {

DronAgent dronAgent;

public void setup() {
  
  dronAgent = new DronAgent();

  
}

public void draw() {
  dronAgent.display();
}

public void mousePressed() {
    switch (dronAgent.drone.state){
      case 1: dronAgent.drone.state = 2; break;
      case 2: dronAgent.drone.state = 1; break;
    }
    println("mousePressed()");
}
public class Box<T>{
  public T object;
  public Box(T obj){
    object = obj;
  }
}
public class DronAgent {
  public Drone drone;
  PVector A;
  PVector B;
  Sensor sensor;
  Environment environment;
  Obstacle[] obstacles;
  
  boolean DEBUG = true;
  
  public DronAgent(){
    obstacles = new Obstacle[]
    {
      new Obstacle(new PVector(380, 150), 48), 
      new Obstacle(new PVector(413, 115), 48), 
      new Obstacle(new PVector(350, 350), 48), 
      new Obstacle(new PVector(432, 37), 48)
    };
    environment = new Environment(obstacles);
    sensor = new Sensor(environment , 100);
    
    A = new PVector(750, 200);
    B = new PVector(106.0f, 105.0f);
    
    Box box = new Pepper(5);
    drone = new Drone(A.x, A.y, A, B, sensor, box);
  }
  
  public void display(){
    background(255);
    //PVector mouse = new PVector(mouseX, mouseY);
    //println(mouse);
    
    if (DEBUG)
      drone.displayTemorary();
    environment.display();
    drone.run();
  } 
}
public class Drone {
  PVector location;
  PVector velocity;
  PVector acceleration;

  float r = 6;
  float maxforce = .1f;    // Maximum steering force
  float maxspeed = 4;    // Maximum speed

  PVector home; // home
  PVector destination; // goal position
  PVector temoraryDestination; // goal position

  Sensor sensor;
  public int state = 1; // 1 - wait, 2 - running
  boolean start = true;

  int defaultBackoff = 200;
  int backoff = defaultBackoff;
  float escapeFactor = .4f; // 33f
  int waitingTime = 2000;
  boolean goHome = false;
  
  Box box;

  Drone(float x, float y, PVector _home, PVector _destination, Sensor _sensor, Box _box) {
    acceleration = new PVector(0, 0);
    velocity = new PVector(0, 0);
    location = new PVector(x, y);
    home = _home;
    destination = _destination;
    sensor = _sensor;
    box = _box;
  }

  public void run() {
    if (start || state != 1) {
      if (sensor.isSafe(location))
      {
        if (temoraryDestination != null && backoff-- > 0)
          arrive(temoraryDestination);
        else
          arrive(destination);
        update();
        println("backoff: " + backoff);
        println("TTTTTTTTTTTT");
      } else {
        float force = (goHome ? -escapeFactor : escapeFactor);
        println("goHome = " + goHome);
        println("force: " + force);
        PVector forceEsacape = new PVector(force, 0);
        applyForce(forceEsacape);
        temoraryDestination = sensor.getNewDestination(location, destination);
        arrive(temoraryDestination);
        update();
        backoff = defaultBackoff;
        println("++++++++++++++++");
      }
    }
    display();
    start = false;
  }
  public void displayTemorary() {
    if (temoraryDestination != null && backoff-- > 0)
    {
      noFill();
      stroke(0);
      strokeWeight(2);
      ellipse(temoraryDestination.x, temoraryDestination.y, 48, 48);
    }
  }
  public void update() {
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    location.add(velocity);
    acceleration.mult(0);
  }


  public void applyForce(PVector force) {
    acceleration.add(force);
  }

  // A method that calculates a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  public void arrive(PVector target) {
    PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target
    float d = desired.mag();
    // Normalize desired and scale with arbitrary damping within 100 pixels
    desired.normalize();
    if (d < 100) {
      if (d <= 2) {
        state = 2;
        if (destination == home) {
          state = 1;
        } else {
        }
        delay(waitingTime);
        start = true;
        destination = home;
        goHome = true;
      }
      float m = map(d, 0, 100, 0, maxspeed);
      desired.mult(m);
    } else {
      desired.mult(maxspeed);
    }

    // Steering = Desired minus Velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    applyForce(steer);
  }

  public void display() {
    //println(location);
    fill(255, 200, 200);
    stroke(0);
    strokeWeight(2);
    ellipse(destination.x, destination.y, 48, 48);

    fill(84, 158, 232);
    stroke(0);
    strokeWeight(2);
    ellipse(home.x, home.y, 48, 48);

    // Draw a triangle rotated in the direction of velocity
    float theta = velocity.heading() + PI/2;
    fill(127);
    stroke(0);
    strokeWeight(1);
    pushMatrix();
    translate(location.x, location.y);
    rotate(theta);
    beginShape();
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape(CLOSE);
    popMatrix();


    noFill();
    stroke(0);
    strokeWeight(2);
    ellipse(location.x, location.y, sensor.range, sensor.range);
  }
}
public class Environment {
  Obstacle[] obstacles;
  
  public Environment(int n){
    obstacles = new Obstacle[n];
    
    for (int i = 0; i < obstacles.length; i++){
      PVector p = new PVector(random(0, width), random(0, height));
      obstacles[i] = new Obstacle(p, 48);
    }
  }
  
  public Environment(Obstacle[] obs){
    obstacles = obs.clone();
  }
  
  public boolean troubleOnR(PVector location, float R){
    for (int i = 0; i < obstacles.length; i++){
        if (obstacles[i].isOverlapped(location, R))
          return true;
    }
    return false;
  }
  
  public void display() {
    for (int i = 0; i < obstacles.length; i++){
      obstacles[i].display();
    }
  }
}
public class Obstacle{
  private float R;
  private PVector pVector;
  private float _color;
  
  public Obstacle(PVector _pVector, float _R){
    pVector = _pVector;
    R = _R;
    _color = random(0, 255);
  }
  public float getR(){ 
    return R;
  }
  public PVector getPVector()
  {
    return pVector;
  }
  public boolean isOverlapped(PVector location, float r){
    float d1 = (location.x - pVector.x) * (location.x - pVector.x);
    float d2 = (location.y - pVector.y) * (location.y - pVector.y);
    
    float distance = PVector.dist(location, pVector);
    float cDistance = (r + R) / 2;
    print("distance: " + distance + "\t");
    print("r + R: " + cDistance);
    println();
    return distance <= cDistance;
  }
  public void display(){
    fill(_color);
    stroke(0);
    strokeWeight(2);
    ellipse(pVector.x, pVector.y, R, R);
  }
}
public class Pepper extends Box<String>{
  int quantity;
  public Pepper(int _quantity){
    super("Pepper");
    quantity = _quantity;
  }
}
public class Sensor {
  public float range;
  private Environment environment;
  
  private int state = 1;
  
  public Sensor(Environment _environment, float _range){
    range = _range;
    environment = _environment;
  }
  
  public boolean isSafe(PVector location){
    //println(location.heading());
    return !environment.troubleOnR(location, range);
  }
  
  public PVector getNewDestination(PVector location, PVector destination){
    //location.x = -location.x;
    //location.y = -location.y;
    PVector p;
    float h;
    
    do
    {
      switch (state){
        case 1: h = 0; break;
        case 2: h = height;
        case 3: h = height/4;
        case 4: h = height/2; break;
        case 5: h = 3 * height/4; break;
        case 7: h = height/3 ; break;
        case 8: h = 2 * height/3 ; break;
        default: 
          h = random(0, height);
      }
      state = (state + 1) % 10;
      p = new PVector(destination.x, h);
    } while(!isSafe(p));
    
    return p;
  }
}
  public void settings() {  size(800, 400);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
