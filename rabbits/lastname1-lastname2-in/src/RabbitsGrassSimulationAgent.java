
import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;



/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	private int x;
	private int y;
	private int vX;
	private int vY;
	private int energy;
	private static int IDNumber = 0;
	private int ID;
	private RabbitsGrassSimulationSpace rgsSpace;


	public RabbitsGrassSimulationAgent(int minInitialEnergy, int maxInitialEnergy ){
		x = -1;
		y = -1;
		energy = (int)((Math.random() * (maxInitialEnergy - minInitialEnergy)) + minInitialEnergy);
		setVxVy();
		IDNumber++;
		ID = IDNumber;
	}

	private void setVxVy(){
		vX = 0;
		vY = 0;
		while((vX == 0) && ( vY == 0)){
			vX = (int)Math.floor(Math.random() * 3) - 1;
			vY = (int)Math.floor(Math.random() * 3) - 1;
		}
	}

	public void setRabbitsGrassSimulationSpace(RabbitsGrassSimulationSpace rgs){
		rgsSpace = rgs;
	}

	public void setXY(int newX, int newY){
		x = newX;
		y = newY;
	}

	public void draw(SimGraphics arg0) {
		if(energy < 10)
			arg0.drawFastRoundRect(Color.gray);
		else
			arg0.drawFastRoundRect(Color.white);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int i) {
		energy = i;
	}

	public String getID(){
		return "A-" + ID;
	}


	public void report(){
		System.out.println(getID() +
				" at " +
				x + ", " + y +
				" has " +
				getEnergy() + " [J]" );
	}

	public void step(){
		energy--;

		int newX = x + vX;
		int newY = y + vY;

		Object2DGrid grid = rgsSpace.getCurrentAgentSpace();
		newX = (newX + grid.getSizeX()) % grid.getSizeX();
		newY = (newY + grid.getSizeY()) % grid.getSizeY();

		if(tryMove(newX, newY)){
			energy += rgsSpace.takeGrassAt(x, y);
		}
		else{
			setVxVy();
		}

	}
	private boolean tryMove(int newX, int newY){
		return rgsSpace.moveAgentAt(x, y, newX, newY);
	}

}
