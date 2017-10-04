public class Drone {
  PVector location;
  PVector velocity;
  PVector acceleration;

  float r = 6;
  float maxforce = .1;    // Maximum steering force
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

  void run() {
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
  void displayTemorary() {
    if (temoraryDestination != null && backoff-- > 0)
    {
      noFill();
      stroke(0);
      strokeWeight(2);
      ellipse(temoraryDestination.x, temoraryDestination.y, 48, 48);
    }
  }
  void update() {
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    location.add(velocity);
    acceleration.mult(0);
  }


  void applyForce(PVector force) {
    acceleration.add(force);
  }

  // A method that calculates a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  void arrive(PVector target) {
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

  void display() {
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