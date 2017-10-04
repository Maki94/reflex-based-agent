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