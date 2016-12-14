//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.util.Random;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.Motor;

public class Qlearning {
    final static double alpha = 0.1;
    final static double gamma = 0.9;
    final static int maxstep = 10;
    
    static int episode = 1;
	
    public static final int Q_BASE = 0; // Base
	public static final int Q_SIZE = 6; // Matriz 6x6
	public static final int numberActions = 4;

//	public static Q_Table Qt = new Q_Table(6, 4);
	
	public static final State goal = new State(4, 5); // goal
	public static final State initial = new State(0, 0); // initial state 
	
	private static double Q[][][] = new double[Q_SIZE][Q_SIZE][numberActions]; // Q-table

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
    				System.out.println(iniState.x + " " + iniState.y + " " + act);
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
		Q[iniState.x][iniState.y][act] = value;
	}
	
	private static double Qtable (State iniState, int act) //OK
	{
		return Q[iniState.x][iniState.y][act];
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
			if(Q[state.x][state.y][i]>maxValue)
			{
				maxValue = Q[state.x][state.y][i];
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
	public static void main(String[] args)
	{
	    Move move = new Move();
	    move.pilot = new DifferentialPilot(3.0f, 14.7f, Motor.A, Motor.B);
//	    File arq = new File("Entrada");
//        try
//        {
//            FileInputStream arquivoLeitura = new FileInputStream(arq);
//            ObjectInputStream objLeitura = new ObjectInputStream(arquivoLeitura);
//            Q_Table Qt = (Q_Table) objLeitura.readObject();
//            objLeitura.close();
//            arquivoLeitura.close();
//        }
//        catch( Exception e ) // Set Q_table in the first exemple
//        {
//            for(int i=0;i<Q_SIZE;i++)
//            {
//            	for(int j=0;j<Q_SIZE;j++)
//            	{
//            		for(int k=0;k<numberActions;k++)
//            		{
//            			Qt.Q[i][j][k] = 0;
//            		}
//            	}
//            }
//        }
//	    run(move);
//	    FileOutputStream f_out = new FileOutputStream(arq);
//	    ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
//	    obj_out.writeObject (Qt);
//	    Button.waitForAnyPress();
	}

}
