class Keys {
  public boolean W     = false;
  public boolean A     = false;
  public boolean S     = false;
  public boolean D     = false;
  public boolean SPACE = false;
  public boolean H     = false;
  public boolean J     = false;

  public void pressed(char key) {
    switch(key) {
    case 'w':
      this.W = true;
      break;
    case 'a':
      this.A = true;
      break;
    case 's':
      this.S = true;
      break;
    case 'd':
      this.D = true;
      break;
    case ' ':
      this.SPACE = true;
      break;

    case 'h':
      debugScreen = debugScreen ? false : true;
      break;
    case 'j':
      print("You pressed J!");
      break;
    }
  }

  public void released(char key) {
    switch(key) {
    case 'w':
      this.W = false;
      break;
    case 'a':
      this.A = false;
      break;
    case 's':
      this.S = false;
      break;
    case 'd':
      this.D = false;
      break;
    case ' ':
      this.SPACE = false;
      break;
    }
  }
}