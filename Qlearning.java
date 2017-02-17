import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;


public class Qlearning{
    final static double alpha = 0.1;
    final static double gamma = 0.9;
    final static int maxstep = 10;
    final static int epsilon = 2;
    
    static int episode = 0;
	
    public static final int Q_BASE = 0; // Base
	public static final int Q_SIZE = 6; // Matriz 6x6
	public static final int numberActions = 4;

	public static Q_Table Qt = new Q_Table(6, 4);
	
	public static final State goal = new State(4, 5); // goal
	public static final State initial = new State(0, 0); // initial state 
	static Random  rand = new Random();

	public enum Actions
	{
		FORWARD, LEFT, RIGHT, BACKWARD;
	}

	private static void run(Move move)
	{
		int steps = 0;
		boolean greedy; // false = random, true = greedy
		for(int i=0;i<1000;i++)//train episodes
		{
			State iniState = new State(Q_BASE,Q_BASE);
			steps=0;
			while(!(iniState.x == goal.x && iniState.y == goal.y))
			{
		    		greedy = isGreedy();
				Actions act = newAction(greedy, iniState);
				steps++;
				State nextState = next(iniState, act);
				double q = Qtable(iniState, act);
				double max = maxQ(nextState);
				double r = reward(nextState);   			
				double value = q + alpha * (r + gamma * max - q);    			
				setQ(iniState, act, value);  			
				move.Doaction(act);
				iniState = nextState;
			}
			episode++;
			System.out.println("new episode " + episode + " steps " + steps);
		}
	}
	private static Actions newAction(boolean greedy,State iniState)
	{
		Actions act = Actions.FORWARD;
		if(greedy==false) //random action
		{
			do
			{
				int randIndex = rand.nextInt(numberActions);
				act = Actions.values()[randIndex];
			}while(!valid(iniState, act));
		}
		else
		{
			double maxValue = -100000;  // min Value
			int best=0;
			for(int i=0;i<numberActions;i++)
			{
				act = Actions.values()[i];
				if((Qt.Q[iniState.x][iniState.y][i]>maxValue)&&(valid(iniState,act)))
				{
					maxValue = Qt.Q[iniState.x][iniState.y][i];
					best=i;
				}
			}
			act = Actions.values()[best];
		}
		return act;
	}
	private static boolean isGreedy()
	{
		int randIndex = rand.nextInt(10);
		if(randIndex <= epsilon)
			return false;
		return true;
	}
	private static void setQ(State iniState, Actions act, double value) //OK
	{
		int Index = act.ordinal();
		Qt.Q[iniState.x][iniState.y][Index] = value;
	}
	
	private static double Qtable (State iniState, Actions act) //OK
	{
		int Index = act.ordinal();
		return Qt.Q[iniState.x][iniState.y][Index];
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
		State nextState = null;
		if(act==Actions.FORWARD)
			nextState = new State(iniState.x,iniState.y+1);
		else
		{
			if(act==Actions.LEFT)
				nextState = new State(iniState.x-1,iniState.y);
			else
			{
				if (act==Actions.RIGHT)
					nextState = new State(iniState.x+1,iniState.y);
				else
				{
					if(act==Actions.BACKWARD)
						nextState = new State(iniState.x,iniState.y-1);
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
	
	public static double reward(State fin) //OK
	{
		if(fin.x==goal.x && fin.y==goal.y)
			return 1000;
		return -0.1;
		
	}
	public static void loadInput(File data)
	{
		try
	    {
	      InputStream is = new FileInputStream(data);
	      DataInputStream din = new DataInputStream(is);
	      //int count=1;
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
	public static void newTable()
	{
		for(int i=0;i<Q_SIZE;i++)
			for(int j=0;j<Q_SIZE;j++)
				for(int k=0;k<numberActions;k++)
					Qt.Q[i][j][k]=0;
	}
	public static void main(String[] args) throws IOException
	{
	    Move move = new Move();
	    move.pilot = new DifferentialPilot(3.0f, 14.7f, Motor.A, Motor.B);
	    File data = new File("Qlog.dat");
	    loadInput(data);
	    //newTable();
	    Button.waitForAnyPress();
	    Delay.msDelay(5000);
	    run(move);
	    writeOutput(data);
	    Button.waitForAnyPress();
	}

}
