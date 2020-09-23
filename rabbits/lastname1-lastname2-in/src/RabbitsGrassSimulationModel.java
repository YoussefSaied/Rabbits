import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenHistogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;


/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {		
	  // Default Values
	  private static final int BIRTH_THRESHOLD = 21;
	  private static final int NUMAGENTS = 2;
	  private static final int WORLDXSIZE = 20;
	  private static final int WORLDYSIZE = 20;
	  private static final int INITIALGRASS = 5;
	  private static final int MAX_INIT_ENERGY = 20;
	  private static final int MIN_INIT_ENERGY = 10;
	  private static final int GRASS_GROWTH_RATE = 50;

	  private int birthThreshold = BIRTH_THRESHOLD;
	  private int numAgents = NUMAGENTS;
	  private int worldXSize = WORLDXSIZE;
	  private int worldYSize = WORLDYSIZE;
	  private int grass = INITIALGRASS;
	  private int maxInitEnergy = MAX_INIT_ENERGY;
	  private int minInitEnergy = MIN_INIT_ENERGY;
	  private int grassGrowthRate = GRASS_GROWTH_RATE;
	  private int newlyBorn = 0;
	  
	  private Schedule schedule;
	  
	  private RabbitsGrassSimulationSpace rgsSpace;
	  
	  private ArrayList agentList;
	  
	  private DisplaySurface displaySurf;
	  
	  private OpenSequenceGraph amountOfGrassInSpace;
	  private OpenHistogram agentEnergyDistribution;
	  
	  class grassInSpace implements DataSource, Sequence {

		    public Object execute() {
		      return new Double(getSValue());
		    }

		    public double getSValue() {
		      return (double) rgsSpace.getTotalGrass();
		    }
	  }
	  
	  class agentEnergy implements BinDataSource {
		    public double getBinValue(Object o) {
		    	RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)o;
		      return (double)rgsa.getEnergy();
		    }
		  }


		public static void main(String[] args) {
			
			System.out.println("Rabbit skeleton");

			SimInit init = new SimInit();
			RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
			// Do "not" modify the following lines of parsing arguments
			if (args.length == 0) // by default, you don't use parameter file nor batch mode 
				init.loadModel(model, "", false);
			else
				init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));
			
		}
		
		public void setup() {
			// TODO Auto-generated method sMotub
			System.out.println("Running setup");
			rgsSpace = null;
			agentList = new ArrayList();
			schedule = new Schedule(1);
			
			if (displaySurf != null){
			      displaySurf.dispose();
			}
			
		    displaySurf = null;
		    
		    if (amountOfGrassInSpace != null){
		        amountOfGrassInSpace.dispose();
		      }
		      amountOfGrassInSpace = null;

		      if (agentEnergyDistribution != null){
		    	  agentEnergyDistribution.dispose();
		      }
		      agentEnergyDistribution = null;
		      
		   // Create Displays
		      displaySurf = new DisplaySurface(this, "Rabbit Grass Model Window 1");
		      amountOfGrassInSpace = new OpenSequenceGraph("Amount Of Grass In Space", this);
		      agentEnergyDistribution = new OpenHistogram("Agent Energy", 8, 0);

		      // Register Displays
		      registerDisplaySurface("Rabbit Grass Model Window 1", displaySurf);
		      this.registerMediaProducer("Plot", amountOfGrassInSpace);

			
		}

		public void begin() {
			// TODO Auto-generated method stub
			buildModel();
		    buildSchedule();
		    buildDisplay();
		    
		    displaySurf.display();
		    amountOfGrassInSpace.display();
		    agentEnergyDistribution.display();
			
		}
		
		public void buildModel(){
			System.out.println("Running BuildModel");
			rgsSpace = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
			rgsSpace.spreadGrass(grass);
			
			for(int i = 0; i < numAgents; i++){
			      addNewAgent();
			    }
			
			for(int i = 0; i < agentList.size(); i++){
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
			      rgsa.report();
			    }
			}

		  public void buildSchedule(){
			  System.out.println("Running BuildSchedule");
			  
			  class RabbitsGrassSimulationStep extends BasicAction {
			      public void execute() {
			    	newlyBorn = 0;
			        SimUtilities.shuffle(agentList);
			        for(int i = 0; i < agentList.size(); i++){
			        	RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
			        	if(rgsa.getEnergy() > birthThreshold) {
			        		addNewAgent();
			        		newlyBorn++;
			        		rgsa.setEnergy(rgsa.getEnergy() - birthThreshold);
			        	}
			        	rgsa.step();
			        }
			        System.out.println("Number of newly born rabbits in this step : " + newlyBorn);
			        reapDeadAgents();
			        rgsSpace.spreadGrass(grassGrowthRate);
			        displaySurf.updateDisplay();
			      }
			    }

			    schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());
			    
		      class RabbitsGrassSimulationCountLiving extends BasicAction {
		         public void execute(){
		           countLivingAgents();
		         }
		      }

		        schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationCountLiving());
		        
	          class CarryDropUpdateGrassInSpace extends BasicAction {
	            public void execute(){
	              amountOfGrassInSpace.step();
	            }
	          }

	          schedule.scheduleActionAtInterval(10, new CarryDropUpdateGrassInSpace());

	          class CarryDropUpdateAgentEnergy extends BasicAction {
	            public void execute(){
	              agentEnergyDistribution.step();
	            }
	          }

	          schedule.scheduleActionAtInterval(10, new CarryDropUpdateAgentEnergy());
		        

		  }

		  public void buildDisplay(){
			 System.out.println("Running BuildDisplay");
			  
		     ColorMap map = new ColorMap();

		    for(int i = 16; i>=1; i--){
		      map.mapColor(i, new Color(0, (int)(i * 8 + 127), 0));
		    }
		    map.mapColor(0, Color.gray);

		    Value2DDisplay displayGrass =
		        new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);
		    
		    Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
		    displayAgents.setObjectList(agentList);

		    displaySurf.addDisplayable(displayGrass, "Grass");
		    displaySurf.addDisplayable(displayAgents, "Agents");
		    
		    amountOfGrassInSpace.addSequence("Grass In Space", new grassInSpace());
		    agentEnergyDistribution.createHistogramItem("Agent Energy",agentList,new agentEnergy());

		  }

		public String[] getInitParam() {
			// TODO Auto-generated method stub
			// Parameters to be set by users via the Repast UI slider bar
			// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want 
			String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "Grass", "InitEnergy"};
			return params;
		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}
		
	private void addNewAgent(){
		RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(minInitEnergy, maxInitEnergy);
	    agentList.add(a);
	    rgsSpace.addAgent(a);
	  }
	
	private void reapDeadAgents(){
	    for(int i = (agentList.size() - 1); i >= 0 ; i--){
	    	RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
	      if(rgsa.getEnergy() < 1){
	        rgsSpace.removeAgentAt(rgsa.getX(), rgsa.getY());
	        agentList.remove(i);
	      }
	    }
	  }
		
     private int countLivingAgents(){
	    int livingAgents = 0;
	    for(int i = 0; i < agentList.size(); i++){
	    	RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
	      if(rgsa.getEnergy() > 0) livingAgents++;
	    }
	    
	    System.out.println("Number of living agents is: " + livingAgents);

	    return livingAgents;
	  }

	 public Schedule getSchedule() {
		// TODO Auto-generated method stub
		return schedule;
	 }
	
	 public int getNumAgents(){
	    return numAgents;
	  }

	  public void setNumAgents(int na){
	    numAgents = na;
	  }
	  
	  public int getWorldXSize(){
		    return worldXSize;
	  }

	  public void setWorldXSize(int wxs){
	    worldXSize = wxs;
	  }

	  public int getWorldYSize(){
	    return worldYSize;
	  }

	  public void setWorldYSize(int wys){
	    worldYSize = wys;
	  }

	  public int getGrass() {
	    return grass;
	  }

	  public void setGrass(int i) {
	    grass = i;
	  }
	  
	  public int getMaxInitEnergy() {
		    return maxInitEnergy;
		  }

	  public int getMinInitEnergy() {
		    return minInitEnergy;
		  }

	  public void setMaxInitEnergy(int i) {
		  maxInitEnergy = i;
	  }

	  public void setMinInitEnergy(int i) {
		  minInitEnergy = i;
	  }

}
