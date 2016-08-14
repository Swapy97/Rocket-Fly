boolean isNiceSpawn(float x, float y, int radius) {
  boolean collision;

  // COMETS
  collision = isCollidingComet(x, y, radius);
  if (collision) {
    return false;
  }

  // PLAYER
  collision = isCollidingPlayer(x, y, radius);
  if (collision) {
    return false;
  }

  // BULLETS
  collision = isCollidingBullet(x, y, radius);
  if (collision) {
    return false;
  }
  
  return true;
}


boolean testCollision(float x1, float y1, int radius1, float x2, float y2, int radius2) {
  return testCollision(x1, y1, radius1, round(x2), round(y2), radius2);
}
boolean testCollision(float x1, float y1, int radius1, int x2, int y2, int radius2) {
  float dx = x1 - x2;
  float dy = y1 - y2;

  float distance = sqrt( pow(dx, 2) + pow(dy, 2) );

  if (dx == 0 && dy == 0) { // make sure the object does not collide with itself
    return false;
  }

  return distance < radius1+radius2;
}

boolean isCollidingComet(float x, float y, int radius) {
  return isCollidingComet(round(x), round(y), radius);
}
boolean isCollidingComet(int x, int y, int radius) {
  for (int i=0; i<comets.size(); i++) {
    Comet comet = comets.get(i);

    boolean collision = testCollision(x, y, radius, comet.x, comet.y, comet.radius);
    if (collision) {
      return true;
    }
  }
  return false;
}

boolean isCollidingPlayer(float x, float y, int radius) {
  if (player != null) {
    boolean collision = testCollision(x, y, radius, player.x, player.y, player.radius);
    return collision;
  }
  return false;
}


boolean isCollidingBullet(float x, float y, int radius) {
  for (int i=0; i<bullets.size(); i++) {
    Bullet bullet = bullets.get(i);

    boolean collision = testCollision(x, y, radius, bullet.x, bullet.y, bullet.radius);
    if (collision) {
      return true;
    }
  }
  return false;
}