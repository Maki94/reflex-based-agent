DronAgent dronAgent;

void setup() {
  size(800, 400);
  dronAgent = new DronAgent();

  smooth();
}

void draw() {
  dronAgent.display();
}

void mousePressed() {
    switch (dronAgent.drone.state){
      case 1: dronAgent.drone.state = 2; break;
      case 2: dronAgent.drone.state = 1; break;
    }
    println("mousePressed()");
}