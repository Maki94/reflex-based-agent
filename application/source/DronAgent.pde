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