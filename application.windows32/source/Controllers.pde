int CometID = 0;

void CometsSetup() {
  for (int i=0; i<cometAmount; i++) {
    comets.add(new Comet());
  }
}

void CometsUpdate() {
  if( frameCount % 250 == 0 ){
    cometAmount += round(cometAmount*0.05);
  }
  
  while(comets.size() < cometAmount){
    comets.add(new Comet());
  }
  
  for (int i = 0; i < comets.size(); i++) {
    Comet comet = comets.get(i);
    
    if(comet.xSpd == 0 && comet.ySpd == 0){
      comet.remove = true;
    }
    
    comet.update();
    comet.draw();
    
    if (comet.remove) {
      comets.remove(i);
    }
  }
}






void BulletsUpdate(){
  for (int i = 0; i < bullets.size(); i++) {
    Bullet bullet = bullets.get(i);
    
    bullet.update();
    bullet.draw();
    
    if(bullet.remove || bullet.lifespan == 0){
      bullets.remove(i);
    }
  }
}