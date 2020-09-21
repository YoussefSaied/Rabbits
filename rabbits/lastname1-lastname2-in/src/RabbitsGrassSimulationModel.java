

import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.analysis.*;
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
 * @author Group 69
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {

	// Default Values
	private static final int NUMINITRABBITS = 50;
	private static final int WORLDXSIZE = 40;
	private static final int WORLDYSIZE = 40;
	private static final int NUMINITGRASS = 500;
	private static final int MIN_INITIAL_ENERGY = 30;
	private static final int MAX_INITIAL_ENERGY = 50;


	private int GridSize;
	private int minInitialEnergy = MIN_INITIAL_ENERGY;
	private int maxInitialEnergy = MAX_INITIAL_ENERGY;
	private int worldXSize = WORLDXSIZE;
	private int worldYSize = WORLDYSIZE;
	private int NumInitRabbits = NUMINITRABBITS;
	private int NumInitGrass = NUMINITGRASS;
	private int GrassGrowthRate;
	private int BirthThreshold;

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
			return (double)rgsSpace.getTotalGrass();
		}
	}

	class agentEnergy implements BinDataSource{
		public double getBinValue(Object o) {
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) o;
			return (double) rgsa.getEnergy();
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
		// TODO Auto-generated method stub
		System.out.println("Running setup");
		rgsSpace = null;
		agentList = new ArrayList();
		schedule = new Schedule(1);

		// Tear down Displays
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


		displaySurf = new DisplaySurface(this, "Rabbits Model Window 1");
		amountOfGrassInSpace = new OpenSequenceGraph("Amount Of Grass In Space",this);
		agentEnergyDistribution = new OpenHistogram("Agent Energy", 8, 0);
		this.registerMediaProducer("Plot", amountOfGrassInSpace);


		registerDisplaySurface("Rabbits Model Window 1", displaySurf);

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
		rgsSpace.spreadGrass(NumInitGrass);

		for(int i = 0; i < NumInitRabbits; i++) {
			addNewAgent();
		}

		for(int i = 0; i < agentList.size(); i++){
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
			rgsa.report();
		}
	}

	public void buildSchedule(){
		System.out.println("Running BuildSchedule");

		class RabbitsGrassStep extends BasicAction	 {

			public void execute() {

				SimUtilities.shuffle(agentList);
				for(int i =0; i < agentList.size(); i++){
					RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
					rgsa.step();
				}

				int deadAgents = reapDeadAgents();

				displaySurf.updateDisplay();
			}
		}

		schedule.scheduleActionBeginning(0, new RabbitsGrassStep());

		class RabbitsGrassSimulationCountLiving extends BasicAction {
			public void execute(){
				countLivingAgents();
			}
		}

		schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationCountLiving());

		class RabbitsGrassSimulationUpdateGrassInSpace extends BasicAction {
			public void execute(){
				amountOfGrassInSpace.step();
			}
		}

		schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationUpdateGrassInSpace());

		class RabbitsGrassSimulationUpdateAgentEnergy extends BasicAction {
			public void execute(){
				agentEnergyDistribution.step();
			}
		}

		schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationUpdateAgentEnergy());
	}

	public void buildDisplay(){
		System.out.println("Running BuildDisplay");

		ColorMap map = new ColorMap();

		for(int i = 1; i<16; i++){
			map.mapColor(i, new Color((int)(i * 8 + 127), 0, 0));
		}
		map.mapColor(0, Color.white);

		Value2DDisplay displayGrass =
				new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
		displayAgents.setObjectList(agentList);

		displaySurf.addDisplayable(displayGrass, "Grass");
		displaySurf.addDisplayable(displayAgents, "Rabbits");

		amountOfGrassInSpace.addSequence("Grass In Space", new grassInSpace());
		agentEnergyDistribution.createHistogramItem("Agent Energy", agentList, new agentEnergy());
	}

	public String[] getInitParam() {
		// TODO Auto-generated method stub
		// Parameters to be set by users via the Repast UI slider bar
		// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want
		String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "WorldXSize", "WorldYSize"};
		return params;
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


	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addNewAgent(){
		RabbitsGrassSimulationAgent r = new RabbitsGrassSimulationAgent(minInitialEnergy, maxInitialEnergy);
		agentList.add(r);
		rgsSpace.addAgent(r);
	}

	private int reapDeadAgents(){
		int count = 0;
		for(int i = (agentList.size() - 1); i >= 0 ; i--){
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
			if(rgsa.getEnergy() < 1){
				rgsSpace.removeAgentAt(rgsa.getX(), rgsa.getY());
				agentList.remove(i);
				count++;
			}
		}
		return count;
	}

	private int countLivingAgents(){
		int livingAgents = 0;
		for(int i = 0; i < agentList.size(); i++){
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
			if(rgsa.getEnergy() > 0) livingAgents++;
		}

		System.out.println("Number of living rabbits is: " + livingAgents);

		return livingAgents;
	}

	public Schedule getSchedule() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getNumRabbits(){
		return NumInitRabbits;
	}

	public void setNumRabbits(int na){
		NumInitRabbits = na;
	}

	public int getGrass() {
		return NumInitGrass;
	}

	public void setGrass(int i) {
		NumInitGrass = i;
	}

}
