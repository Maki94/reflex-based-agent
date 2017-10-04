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