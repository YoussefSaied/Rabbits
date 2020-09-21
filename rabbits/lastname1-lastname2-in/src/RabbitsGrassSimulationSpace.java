
import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author Group 69
 */

public class RabbitsGrassSimulationSpace {
    private final Object2DGrid grassSpace;
    private final Object2DGrid agentSpace;

    public RabbitsGrassSimulationSpace(int xSize, int ySize){
        grassSpace = new Object2DGrid(xSize, ySize);
        agentSpace = new Object2DGrid(xSize, ySize);

        for(int i = 0; i < xSize; i++){
            for(int j = 0; j < ySize; j++){
                grassSpace.putObjectAt(i,j,0);
            }
        }
    }

    public void spreadGrass(int grass){
        // Randomly place grass in grassSpace
        for(int i = 0; i < grass; i++){

            // Choose coordinates
            int x = (int)(Math.random()*(grassSpace.getSizeX()));
            int y = (int)(Math.random()*(grassSpace.getSizeY()));

            // Get the value of the object at those coordinates
            int currentValue = getGrassAt(x, y);
//            int I;
//            if(grassSpace.getObjectAt(x,y)!= null){
//                I = ((Integer)grassSpace.getObjectAt(x,y)).intValue();
//            }
//            else{
//                I = 0;
//            }
            // Replace the Integer object with another one with the new value
            grassSpace.putObjectAt(x,y,currentValue + 1);
        }
    }
    public int getGrassAt(int x, int y){
        int i;
        if(grassSpace.getObjectAt(x,y)!= null){
            i = (Integer)grassSpace.getObjectAt(x,y);
        }
        else{
            i = 0;
        }
        return i;
    }

    public Object2DGrid getCurrentGrassSpace(){
        return grassSpace;
    }

    public Object2DGrid getCurrentAgentSpace(){
        return agentSpace;
    }

    public boolean isCellOccupied(int x, int y){
        boolean retVal = false;
        if(agentSpace.getObjectAt(x, y)!=null) retVal = true;
        return retVal;
    }

    public boolean addAgent(RabbitsGrassSimulationAgent agent){
        boolean retVal = false;
        int count = 0;
        int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();

        while((!retVal) && (count < countLimit)){
            int x = (int)(Math.random()*(agentSpace.getSizeX()));
            int y = (int)(Math.random()*(agentSpace.getSizeY()));
            if(!isCellOccupied(x,y) ){
                agentSpace.putObjectAt(x,y,agent);
                agent.setXY(x,y);
                agent.setRabbitsGrassSimulationSpace(this);
                retVal = true;
            }
            count++;
        }

        return retVal;
    }

    public void removeAgentAt(int x, int y){
        agentSpace.putObjectAt(x, y, null);
    }

    public int takeGrassAt(int x, int y){
        int energy = getGrassAt(x, y);
        grassSpace.putObjectAt(x, y, 0);
        return energy;
    }

    public boolean moveAgentAt(int x, int y, int newX, int newY){
        boolean retVal = false;
        if(!isCellOccupied(newX, newY)){
            RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
            removeAgentAt(x,y);
            cda.setXY(newX, newY);
            agentSpace.putObjectAt(newX, newY, cda);
            retVal = true;
        }
        return retVal;
    }
    public int getTotalGrass(){
        int totalGrass = 0;
        for(int i = 0; i < agentSpace.getSizeX(); i++){
            for(int j = 0; j < agentSpace.getSizeY(); j++){
                totalGrass += getGrassAt(i,j);
            }
        }
        return totalGrass;
    }
}
