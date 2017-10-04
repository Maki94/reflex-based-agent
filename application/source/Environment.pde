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