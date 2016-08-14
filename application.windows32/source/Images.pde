class Images {
  public PImage rocket_standby;
  public PImage rocket_action;
  public PImage comet;
  
  Images() {
    this.rocket_standby = loadImage("rocket_standby.png");
    this.rocket_action  = loadImage("rocket_action.png");
    this.comet = loadImage("comet.png");    
  }
}