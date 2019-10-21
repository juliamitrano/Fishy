//Created by Julia Mitrano and Thomas Gringle

import javalib.worldimages.*; // images, like RectangleImage or OverlayImages
import tester.Tester;
import javalib.funworld.*; // the abstract World class and the big-bang library
import java.awt.Color; // general colors (as triples of red,green,blue values)            
import java.util.Random;

interface IFishy {
  // Creates image of the fish
  WorldImage fishImage();

  // Moves the fish
  AFishy moveFishy(String ke);

  // Randomly moves the fish
  AFishy randomMove(int n);

  // Does this fish touch that one?
  boolean touches(AFishy that);

  // If fish is out of bounds in moves to opposite side
  AFishy outsideBounds(int width, int height);

  // Can this fish it that one?
  boolean eats(AFishy that);

  // Player eats a fish
  AFishy playerEat(ILoFishy others);
}

abstract class AFishy implements IFishy {
  int size;
  Color color;
  Posn pos;
  boolean alive;

  AFishy(int s, Color c, Posn p, boolean a) {
    this.size = s;
    this.color = c;
    this.pos = p;
    this.alive = a;
  }

  // Creates an image of the fish
  public WorldImage fishImage() {
    return new RectangleImage(this.size, this.size, "solid", this.color);
  }

  // Moves the fish
  public abstract AFishy moveFishy(String ke);

  // Randomly moves the fish
  public abstract AFishy randomMove(int n);

  // Does this fish touch that fish?
  public boolean touches(AFishy that) {
    return this.pos.x >= that.pos.x - that.size / 2 && this.pos.x <= that.pos.x + that.size / 2
        && this.pos.y >= that.pos.y - that.size / 2 && this.pos.y <= that.pos.y + that.size / 2;
  }

  // If its out of bounds it moves to opposite side
  public abstract AFishy outsideBounds(int width, int height);

  // Can this fish eat that one?
  public boolean eats(AFishy that) {
    return this.size > that.size && this.touches(that);
  }

  // The fish eats
  public abstract AFishy playerEat(ILoFishy others);
}

class Player extends AFishy {

  Player(int s, Color c, Posn p, boolean a) {
    super(s, c, p, a);
  }

  // Moves the player
  public AFishy moveFishy(String ke) {
    if (ke.equals("right")) {
      return new Player(this.size, this.color, new Posn(this.pos.x + 5, this.pos.y), this.alive);
    }
    else if (ke.equals("left")) {
      return new Player(this.size, this.color, new Posn(this.pos.x - 5, this.pos.y), this.alive);
    }
    else if (ke.equals("up")) {
      return new Player(this.size, this.color, new Posn(this.pos.x, this.pos.y - 5), this.alive);
    }
    else if (ke.equals("down")) {
      return new Player(this.size, this.color, new Posn(this.pos.x, this.pos.y + 5), this.alive);
    }
    else {
      return this;
    }
  }

  // Returns itself
  public AFishy randomMove(int n) {
    return this;
  }

  // If player is out of bounds it moves to opposite side
  public AFishy outsideBounds(int width, int height) {
    if (this.pos.x > width) {
      return new Player(this.size, this.color, new Posn(0, this.pos.y), this.alive);
    }
    else if (this.pos.x < 0) {
      return new Player(this.size, this.color, new Posn(width, this.pos.y), this.alive);
    }
    else if (this.pos.y > height) {
      return new Player(this.size, this.color, new Posn(this.pos.x, 0), this.alive);
    }
    else if (this.pos.y < 0) {
      return new Player(this.size, this.color, new Posn(this.pos.x, height), this.alive);
    }
    else {
      return this;
    }
  }

  // The player eats
  public AFishy playerEat(ILoFishy others) {
    return others.eat(this);
  }
}

class BackgroundFishy extends AFishy {

  BackgroundFishy(int s, Color c, Posn p, boolean a) {
    super(s, c, p, a);
  }

  // Creates an image of the fish
  public WorldImage fishImage() {
    return new RectangleImage(this.size, this.size, "solid", this.color);
  }

  // Returns itself
  public AFishy moveFishy(String ke) {
    return this;
  }

  // Randomly moves the fish
  public AFishy randomMove(int n) {
    return new BackgroundFishy(this.size, this.color,
        new Posn(this.pos.x + -(Math.abs(this.randomInt(n))), this.pos.y), this.alive);
  }

  // Returns random integer
  int randomInt(int n) {
    return -n + (new Random().nextInt(2 * n + 1));
  }

  // If fish is outside screen, starts at beginning
  public AFishy outsideBounds(int width, int height) {
    if (this.pos.x > width) {
      return new BackgroundFishy(this.size, this.color, new Posn(0, this.pos.y), this.alive);
    }
    else if (this.pos.x < 0) {
      return new BackgroundFishy(this.size, this.color, new Posn(width, this.pos.y), this.alive);
    }
    else if (this.pos.y > height) {
      return new BackgroundFishy(this.size, this.color, new Posn(this.pos.x, 0), this.alive);
    }
    else if (this.pos.y < 0) {
      return new BackgroundFishy(this.size, this.color, new Posn(this.pos.x, height), this.alive);
    }
    else {
      return this;
    }
  }

  // Returns itself
  public AFishy playerEat(ILoFishy others) {
    return this;
  }
}

interface ILoFishy {
  // Creates image of all fish
  WorldImage drawFishy();

  // Randomly moves all the fish
  ILoFishy randomMove(int n);

  // Do any of these fish touch that one?
  boolean touches(AFishy that);

  // Returns new player if it eats any these fish
  AFishy eat(AFishy player);

  // Is that fish bigger than all these fish?
  boolean isBiggest(AFishy player);

  // Returns all uneaten fish
  ILoFishy unEaten(AFishy player);

  // Was player eaten by one of these fish?
  boolean playerEaten(AFishy player);
}

class MtLoFishy implements ILoFishy {

  public WorldImage drawFishy() {
    return new EmptyImage();
  }

  // No fish to move
  public ILoFishy randomMove(int n) {
    return this;
  }

  // No fish to touch that one
  public boolean touches(AFishy that) {
    return false;
  }

  // No fish to eat
  public AFishy eat(AFishy player) {
    return player;
  }

  // No fish to compare
  public boolean isBiggest(AFishy player) {
    return true;
  }

  // No fish
  public ILoFishy unEaten(AFishy player) {
    return this;
  }

  // No fish to eat player
  public boolean playerEaten(AFishy player) {
    return false;
  }
}

class ConsLoFishy implements ILoFishy {
  AFishy first;
  ILoFishy rest;

  ConsLoFishy(AFishy f, ILoFishy r) {
    this.first = f;
    this.rest = r;
  }

  // Creates image of all these fish
  public WorldImage drawFishy() {
    return new OverlayImage(this.first.fishImage().movePinholeTo(this.first.pos),
        this.rest.drawFishy());
  }

  // Randomly moves all these fish
  public ILoFishy randomMove(int n) {
    return new ConsLoFishy(this.first.randomMove(n), this.rest.randomMove(n));
  }

  // Does on of these fish touch that one?
  public boolean touches(AFishy that) {
    return (that.touches(this.first) && that.size > this.first.size) || this.rest.touches(that);
  }

  // Player eats one of these fish
  public AFishy eat(AFishy player) {
    AFishy newPlayer = new Player(player.size + this.first.size, player.color, player.pos,
        player.alive);
    if (player.eats(this.first)) {
      return this.rest.eat(newPlayer);
    }
    else {
      return this.rest.eat(player);
    }
  }

  // Is player the biggest out of all these fish?
  public boolean isBiggest(AFishy player) {
    return player.size > this.first.size && this.rest.isBiggest(player);
  }

  // New list of all uneaten fish
  public ILoFishy unEaten(AFishy player) {
    if (player.eats(this.first)) {
      return this.rest.unEaten(player);
    }
    else {
      return new ConsLoFishy(this.first, this.rest.unEaten(player));
    }
  }

  // Do any of these fish eat player?
  public boolean playerEaten(AFishy player) {
    return this.first.eats(player) || this.rest.playerEaten(player);
  }
}

class FishyGame extends World {

  int width = 500;
  int height = 500;
  AFishy player;
  ILoFishy otherFish;

  FishyGame(AFishy p, ILoFishy of) {
    super();
    this.player = p;
    this.otherFish = of;
  }

  // If you press a key...
  public World onKeyEvent(String ke) {
    if (ke.equals("x")) {
      return this.endOfWorld("Byeeeee");
    }
    else {
      return new FishyGame(this.player.moveFishy(ke), this.otherFish);
    }
  }

  // Every tick...
  public World onTick() {
    return new FishyGame(
        this.player.playerEat(this.otherFish).outsideBounds(this.width, this.height),
        this.otherFish.randomMove(5));
  }

  // Scene
  public WorldScene makeScene() {
    return this.getEmptyScene()
        .placeImageXY(this.player.fishImage(), this.player.pos.x, this.player.pos.y)
        .placeImageXY(this.otherFish.unEaten(this.player).drawFishy(), 250, 250);
  }

  // Ending scene
  public WorldScene lastScene(String s) {
    return this.makeScene().placeImageXY(new TextImage(s, Color.red), 100, 40);
  }

  // Stopping game
  public WorldEnd worldEnds() {
    if (this.otherFish.playerEaten(this.player)) {
      return new WorldEnd(true, this.lastScene("Eaten!"));
    }
    else if (this.otherFish.isBiggest(this.player)) {
      return new WorldEnd(true, this.lastScene("You're the biggest fish!"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

class FishyExamples {

  Posn center = new Posn(250, 250);
  AFishy player = new Player(10, Color.CYAN, center, true);
  AFishy player2 = new Player(10, Color.ORANGE, center, true);
  AFishy playerOutRight = new Player(10, Color.CYAN, new Posn(550, 100), true);
  AFishy playerLeft = new Player(10, Color.CYAN, new Posn(0, 100), true);
  AFishy playerOutLeft = new Player(10, Color.CYAN, new Posn(-50, 100), true);
  AFishy playerRight = new Player(10, Color.CYAN, new Posn(500, 100), true);
  AFishy fishy1 = new BackgroundFishy(5, Color.GRAY, new Posn(100, 100), true);
  AFishy fishy2 = new BackgroundFishy(20, Color.GREEN, new Posn(400, 100), true);
  AFishy fishy3 = new BackgroundFishy(10, Color.BLUE, new Posn(225, 200), true);
  AFishy fishy4 = new BackgroundFishy(15, Color.MAGENTA, new Posn(100, 400), true);
  AFishy fishy5 = new BackgroundFishy(12, Color.RED, new Posn(275, 300), true);
  AFishy fishy6 = new BackgroundFishy(35, Color.PINK, new Posn(400, 400), true);

  ILoFishy otherFish = new ConsLoFishy(fishy1, new ConsLoFishy(fishy2, new ConsLoFishy(fishy3,
      new ConsLoFishy(fishy4, new ConsLoFishy(fishy5, new ConsLoFishy(fishy6, new MtLoFishy()))))));

  // Test fishImage
  boolean testFishImage(Tester t) {
    return t.checkExpect(player.fishImage(), new RectangleImage(10, 10, "solid", Color.CYAN))
        && t.checkExpect(fishy1.fishImage(), new RectangleImage(5, 5, "solid", Color.GRAY));
  }

  // Test moveFishy
  boolean testMoveFishy(Tester t) {
    return t.checkExpect(player.moveFishy("right"),
        new Player(10, Color.CYAN, new Posn(255, 250), true))
        && t.checkExpect(player.moveFishy("left"),
            new Player(10, Color.CYAN, new Posn(245, 250), true))
        && t.checkExpect(player.moveFishy("up"),
            new Player(10, Color.CYAN, new Posn(250, 245), true))
        && t.checkExpect(player.moveFishy("down"),
            new Player(10, Color.CYAN, new Posn(250, 255), true));
  }

  // Test touches
  boolean testTouches(Tester t) {
    return t.checkExpect(player.touches(player2), true)
        && t.checkExpect(player.touches(fishy1), false);
  }

  // Test outsideBounds
  boolean testOutsideBounds(Tester t) {
    return t.checkExpect(playerOutLeft.outsideBounds(500, 500), playerRight)
        && t.checkExpect(playerOutRight.outsideBounds(500, 500), playerLeft);
  }

  // Test eats
  boolean tesEats(Tester t) {
    return t.checkExpect(player.eats(fishy1), true) && t.checkExpect(player.eats(fishy2), false)
        && t.checkExpect(player.eats(fishy3), false);
  }

  public static void main(String[] argv) {
    Posn center = new Posn(250, 250);
    AFishy player = new Player(10, Color.CYAN, center, true);
    AFishy fishy1 = new BackgroundFishy(5, Color.GRAY, new Posn(100, 100), true);
    AFishy fishy2 = new BackgroundFishy(20, Color.GREEN, new Posn(400, 100), true);
    AFishy fishy3 = new BackgroundFishy(10, Color.BLUE, new Posn(225, 200), true);
    AFishy fishy4 = new BackgroundFishy(15, Color.MAGENTA, new Posn(100, 400), true);
    AFishy fishy5 = new BackgroundFishy(12, Color.RED, new Posn(275, 300), true);
    AFishy fishy6 = new BackgroundFishy(35, Color.PINK, new Posn(400, 400), true);

    ILoFishy otherFish = new ConsLoFishy(fishy1,
        new ConsLoFishy(fishy2, new ConsLoFishy(fishy3, new ConsLoFishy(fishy4,
            new ConsLoFishy(fishy5, new ConsLoFishy(fishy6, new MtLoFishy()))))));

    // run the tests - showing only the failed test results
    FishyExamples fe = new FishyExamples();
    Tester.runReport(fe, false, false);

    // run the game
    FishyGame w = new FishyGame(player, otherFish);
    w.bigBang(500, 500, 0.3);
  }
}
