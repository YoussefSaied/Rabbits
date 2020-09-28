/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */
import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;


public class RabbitsGrassSimulationAgent implements Drawable {
    
    private int x;
    private int y;

    private int vX;
    private int vY;

    private int energy;

    private static int IDNumber = 0;
    private int ID;

    private RabbitsGrassSimulationSpace rgsSpace;

    public RabbitsGrassSimulationAgent(int maxInitEnergy, int minIntEnergy) {
        x = -1;
        y = -1;

        energy = (int)((Math.random() * (maxInitEnergy - minIntEnergy)) + minIntEnergy);

        IDNumber++;
        ID = IDNumber;

        setVxVy();
    }
      
    private void setVxVy() {
        vX = 0;
        vY = 0;

        while ((vX == 0) && ( vY == 0)) {
            int randomStep = (int)Math.floor(Math.random() * 3) - 1;

            if (Math.random() < 0.5) {
                vY = randomStep;
            } else {
                vX = randomStep;
            }
        }
    }
    
    public void setXY(int newX, int newY  ) {
        x = newX;
        y = newY;
    }
      
    public void setRabbitGrassSimulationSpace(RabbitsGrassSimulationSpace rgss) {
        rgsSpace = rgss;
    }

      
    public String getID() {
        return "A-" + ID;
    }

    public int getEnergy() {
        return energy;
    }
      
    public void setEnergy(int givenEnergy) {
        energy = givenEnergy;
    }

      public void report() {
        System.out.println(getID() +
                           " at " +
                           x + ", " + y +
                           " has " +
                           getEnergy() + " energy.");
    }

    public void draw(SimGraphics arg0) {
        // TODO Auto-generated method stub
        arg0.drawFastRoundRect(Color.white);
    }

    public int getX() {
        // TODO Auto-generated method stub
        return x;
    }

    public int getY() {
        // TODO Auto-generated method stub
        return y;
    }
    
    public void step() {
        int newX = x + vX;
        int newY = y + vY;

        Object2DGrid grid = rgsSpace.getCurrentAgentSpace();

        newX = (newX + grid.getSizeX()) % grid.getSizeX();
        newY = (newY + grid.getSizeY()) % grid.getSizeY();

        if (tryMove(newX, newY)) {
            energy += rgsSpace.takeGrassAt(x, y);
        }

        setVxVy();
        
        energy--;
    }
    
    private boolean tryMove(int newX, int newY) {
        return rgsSpace.moveAgentAt(x, y, newX, newY);
    }
}

