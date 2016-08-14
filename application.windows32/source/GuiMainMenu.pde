class GuiMainMenu extends Gui {
  
  private GuiText textTitle;
  
  private GuiButton buttonStart;
  private GuiButton buttonEnd;
  
  GuiMainMenu() {
    this.textTitle = this.addCenteredText(0, "Rocket Fly" , width/2, 100, 64);
    
    this.buttonStart = this.addButton(1, "Play Game" , 200, 250, 200, 25);
    this.buttonEnd = this.addButton(2, "Exit", 200, 300, 200, 25);
  }
  
  public void buttonPressed(GuiButton button){
    if(button.id == this.buttonStart.id){
      gameStarted = true;
      player.god = false;
    }
    
    if(button.id == this.buttonEnd.id){
      exit();
    }
  }
}