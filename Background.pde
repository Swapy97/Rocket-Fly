class Background {
  private int starAmount = 1000;  
  private ArrayList<Star> stars = new ArrayList(starAmount);

  Background() {
    for (int i=0; i<this.starAmount; i++) {
      stars.add(new Star());
    }
  }

  public void draw() {
    background(0);
    for (int i=0; i<stars.size(); i++) {
      stars.get(i).update();
      stars.get(i).draw();
    }

    /*int x = -player.x + width/2;
     int y = -player.y + height/2;    
     imageMode(CORNER);
     image(background, x, y, playFieldWidth, playFieldHeight);*/
  }

  private void drawBorder() {
    float x = -player.x + width/2;
    float y = -player.y + height/2;

    stroke(255);
    noFill();
    strokeWeight(2);
    rect(x, y, playFieldWidth, playFieldHeight);
  }
  
  public void drawOuterBackground(){
    float x = -player.x + width/2;
    float y = -player.y + height/2;

    fill(0);
    noStroke();
    rect( x                  - width/2, y                   - height/2,  playFieldWidth + width/2,                    height/2); // TOP
    rect( x + playFieldWidth          , y                   - height/2,                   width/2,  playFieldHeight + height/2); // RIGHT
    rect( x + playFieldWidth + width/2, y + playFieldHeight           , -playFieldWidth - width/2,                    height/2); // BOTTOM
    rect( x                           , y + playFieldHeight + height/2,                 - width/2, -playFieldHeight - height/2 ); // LEFT
    
    this.drawBorder();
  }
}