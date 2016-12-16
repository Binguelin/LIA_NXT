import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;


public class Qlearning {
    final static double alpha = 0.1;
    final static double gamma = 0.9;
    final static int maxstep = 10;
    
    static int episode = 1;
	
    public static final int Q_BASE = 0; // Base
	public static final int Q_SIZE = 6; // Matriz 6x6
	public static final int numberActions = 4;

	public static Q_Table Qt = new Q_Table(6, 4);
	
	public static final State goal = new State(4, 5); // goal
	public static final State initial = new State(0, 0); // initial state 
	
//	private static double Q[][][] = new double[Q_SIZE][Q_SIZE][numberActions]; // Q-table

	public enum Actions
	{
		FORWARD, LEFT, RIGHT, BACKWARD;
	}

	private static void run(Move move)
    {
		int steps = 0;
    	for(int i=0;i<10;i++)//train episodes
    	{
    		State iniState = new State(Q_BASE,Q_BASE);
    		System.out.println("new episode " + episode + " steps " + steps);
    		steps=0;
//    		Button.waitForAnyPress(); // new episode
//    		LCD.clear();
    		steps++;
    		while(!(iniState.x == goal.x && iniState.y == goal.y))
    		{
    	    	Random rand = new Random();
    			int randIndex = rand.nextInt(numberActions);
    			
    			Actions act = Actions.values()[randIndex];
    			if(valid(iniState, act))
    			{
    				steps++;
//    				System.out.println(iniState.x + " " + iniState.y + " " + act);
//    				if(steps%maxstep==0) // correction for moving errors
//    				{	
//    					//Button.waitForAnyPress();
//    				}
    				State nextState = next(iniState, act);
    				double q = Qtable(iniState, randIndex);
    				double max = maxQ(nextState);
    				double r = reward(iniState, act);   			
    				double value = q + alpha * (r + gamma * max - q);    			
    				setQ(iniState, randIndex, value);  			
    				move.Doaction(act);	
    				iniState = nextState;
    			}
    		}
    		episode++;
    	}
    }

	private static void setQ(State iniState, int act, double value) //OK
	{
		Qt.Q[iniState.x][iniState.y][act] = value;
	}
	
	private static double Qtable (State iniState, int act) //OK
	{
		return Qt.Q[iniState.x][iniState.y][act];
	}
	private static boolean valid(State state, Actions act) //OK
	{
			if((act==Actions.FORWARD && state.y == Q_SIZE - 1) || (act==Actions.LEFT && state.x == Q_BASE ) ||
					(act==Actions.RIGHT && state.x == Q_SIZE - 1 ) || (act==Actions.BACKWARD && state.y == Q_BASE))
				return false;
			else
				return true;
	}
	private static State next (State iniState, Actions act) //OK
	{
		State nextState = iniState;
		if(act==Actions.FORWARD)
			nextState.y += 1;
		else
		{
			if(act==Actions.LEFT)
				nextState.x -= 1;
			else
			{
				if (act==Actions.RIGHT)
					nextState.x += 1;
				else
				{
					if(act==Actions.BACKWARD)
						nextState.y -= 1;
				}
			}	
		}
		return nextState;
	}
	private static double maxQ(State state) //OK
	{
		double maxValue = -1000;  // min Value
		for(int i=0;i<numberActions;i++)
		{
			if(Qt.Q[state.x][state.y][i]>maxValue)
			{
				maxValue = Qt.Q[state.x][state.y][i];
			}
		}
		return maxValue;
	}
	
	public static double reward(State iniState, Actions act) //OK
	{
		if(iniState==goal)
			return 1000;
		return -0.1;
	}
	public static void loadInput(File data)
	{
		try
	    {
	      InputStream is = new FileInputStream(data);
	      DataInputStream din = new DataInputStream(is);

	      for(int i=0;i<Q_SIZE;i++)
	      {
	      		for(int j=0;j<Q_SIZE;j++)
	      		{
	      			for(int k=0;k<numberActions;k++)
	      			{
	      				float x = din.readFloat();
	      				Qt.Q[i][j][k] = x;
	      			}
	      		}
	      }
	      System.out.println("OK");
	      din.close();
	    } 
	    catch (IOException ioe)
	    {
	      System.err.println("Read Exception");
	    }
	}
	public static void writeOutput(File data) throws IOException
	{
		FileOutputStream out = null; // declare outside the try block
	    if ( !data.exists() )
	    {
            data.createNewFile();
        }
	    try
	    {
	        out = new FileOutputStream(data);
	    }
	    catch(IOException e)
	    {
	      	System.err.println("Failed to create output stream");
	      	Button.waitForAnyPress();
	    }
	    DataOutputStream dataOut = new DataOutputStream(out);
	    try// write
	    {
	    	for(int i=0;i<Q_SIZE;i++)
	      	{
	      		for(int j=0;j<Q_SIZE;j++)
	      		{
	      			for(int k=0;k<numberActions;k++)
	      			{
	      				dataOut.writeFloat((float) Qt.Q[i][j][k]);
	      			}
	      		}
	      	}
	        out.close(); // flush the buffer and write the file
	    } 
	    catch (IOException e)
	    {
	        System.err.println("Failed to write to output stream");
	    }
	}
	public static void main(String[] args) throws IOException
	{
	    Move move = new Move();
	    move.pilot = new DifferentialPilot(3.0f, 14.7f, Motor.A, Motor.B);
	    File data = new File("Qlog.dat");
	    loadInput(data);
	    Button.waitForAnyPress();
	    run(move);
	    writeOutput(data);
	    Button.waitForAnyPress();
	}

}
