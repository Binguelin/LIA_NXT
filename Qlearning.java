import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import lejos.robotics.navigation.DifferentialPilot;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.util.Delay;

public class Qlearning {
	static DifferentialPilot pilot;
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

	public enum Actions {
		FORWARD, LEFT, RIGHT, BACKWARD;
	}

	private static void run()
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
    				Doaction(act);	
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
		State s1 = new State(3,5);
		State s2 = new State(5,5);
		State s3 = new State(4,4);
		if(iniState==goal)
			return 1000;
		return -0.1;
	}
	///////////////////////// Mover ////////////////////////////////////

	private static void CorrectionFactorA() // Correction Factor motor A
	{
		Motor.A.forward();
		Delay.msDelay(250);
		Motor.A.stop();
	}

	private static void CorrectionFactorB() // Correction Factor motor B
	{
		Motor.B.forward();
		Delay.msDelay(250);
		Motor.B.stop();
	}

	private static void GoForward() // NXT will go forward for 1 seconds
	{
		pilot.travel(1); 
//		Motor.A.forward();
//		Motor.B.forward();
//		Delay.msDelay(1000);
//		Motor.A.stop();
//		Motor.B.stop();
//		CorrectionFactorA();
	}

	private static void GoBackward() // NXT will go backward for 1 seconds
	{
		pilot.travel(-1);
//		Motor.A.backward();
//		Motor.B.backward();
//		Delay.msDelay(1000);
//		Motor.A.stop();
//		Motor.B.stop();
//		CorrectionFactorB();
	}

	private static void TurnLeft() // Turn the NXT to left
	{
		pilot.rotate(90);
//		Motor.A.rotate(4*360);
//		Motor.A.rotateTo(Motor.A.getTachoCount() + 4 * 360);
//		Motor.A.backward(); // Correction factor backwards
//		Delay.msDelay(250);
//		Motor.A.stop();
	}

	private static void TurnRight() // Turn the NXT to right
	{
		pilot.rotate(-90);
//		Motor.A.rotate(-1290);
//		Motor.A.rotateTo(Motor.A.getTachoCount() - 4 * 360);
//		Motor.A.forward();
//		Delay.msDelay(250);
//		Motor.A.stop();
	}

	private static void GoLeft() // NXT will turn left and go forward for 1
									// seconds
	{
		TurnLeft();
		Delay.msDelay(1000);
		GoForward();
		TurnRight();
	}

	private static void GoRight() // NXT will go right and go forward for 1
									// seconds
	{
		TurnRight();
		Delay.msDelay(1000);
		GoForward();
		TurnLeft();
	}

	public static void Doaction(Actions action) {
		switch (action) {
		case FORWARD:
			GoForward();
		case LEFT:
			GoLeft();
		case RIGHT:
			GoRight();
		case BACKWARD:
			GoBackward();
		}
	}
	// /////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws IOException{
	    Motor.A.setSpeed(4*360); //motor A speed
	    Motor.B.setSpeed(4*360); //motor B speed
	    File arq = new File("Entrada");
        try
        {
            FileInputStream arquivoLeitura = new FileInputStream(arq);
            ObjectInputStream objLeitura = new ObjectInputStream(arquivoLeitura);
            Q_Table Qt = (Q_Table) objLeitura.readObject();
            objLeitura.close();
            arquivoLeitura.close();
        }
        catch( Exception e ) // Set Q_table in the first exemple
        {
            for(int i=0;i<Q_SIZE;i++)
            {
            	for(int j=0;j<Q_SIZE;j++)
            	{
            		for(int k=0;k<numberActions;k++)
            		{
            			Qt.Q[i][j][k] = 0;
            		}
            	}
            }
        }
	    run();
	    FileOutputStream f_out = new FileOutputStream(arq);
	    ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
	    obj_out.writeObject (Qt);
	    Button.waitForAnyPress();
	}

}
