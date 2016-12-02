import java.util.Random;
import lejos.nxt.Motor;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;

public class Qlearning {
    final static double alpha = 0.1;
    final static double gamma = 0.9;
	

	public static final int Q_SIZE = 6; // Matriz 6x6
	public static final int numberActions = 4;
	

	public static State goal = new State(4, 5); // goal
	public static State initial = new State(0, 0); // initial state 

	private static double Q[][][] = new double[Q_SIZE][Q_SIZE][numberActions]; // Q-table

	public enum Actions {
		FORWARD, LEFT, RIGHT, BACKWARD;
	}

	private static void run()
    {
    	Random rand = new Random();
    	for(int i=0;i<10000;i++)//train episodes
    	{
    		State iniState = initial;
    		Button.waitForAnyPress(); // new episode
    		while(iniState != goal)
    		{
    			int randIndex = rand.nextInt(4);
    			Actions act = Actions.values()[randIndex];
    			
    			if(valid(iniState, randIndex))
    			{
    				State nextState = next(iniState, randIndex);   			
    				double q = Qtable(iniState, randIndex);
    				double max = maxQ(nextState);
    				double r = reward(iniState, randIndex);   			
    				double value = q + alpha * (r + gamma * max - q);    			
    				setQ(iniState.x, iniState.y, randIndex, value);  			
    				//Doaction(act);	
    				iniState = nextState;
    			}
    		}
    	}
    	//doBest();
    }
	
	private static void setQ(int x, int y, int act, double value)
	{
		Q[x][y][act] = value;
	}
	
	private static double Qtable (State iniState, int randIndex)
	{
		return Q[iniState.x][iniState.y][randIndex];
	}
	private static boolean valid(State state, int act)
	{
			if((act==0 && state.y == 5) || (act==3 && state.y == 0) || (act==2 && state.x == 5) || (act==1 && state.x == 0))
				return false;
			else
				return true;
	}
	private static State next (State iniState, int act)
	{
		State nextState = iniState;
		if(act==0)
			nextState.y += 1;
		else
		{
			if(act==1)
				nextState.x -= 1;
			else
			{
				if (act==2)
					nextState.x += 1;
				else
				{
					if(act==3)
						nextState.y -= 1;
				}
			}	
		}
		return nextState;
	}
	private static double maxQ(State state)
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
	
	public static double reward(State iniState, int act)
	{
		if(iniState==goal)
			return 1000;
//		if(iniState.x==goal.x-1 && iniState.y==goal.y && act==2)
//			return 200;
//		else
//			if(iniState.x==goal.x+1 && iniState.y==goal.y && act==1)
//				return 200;
//			else
//				if(iniState.x==goal.x && iniState.y==goal.y-1 && act==0)
//					return 200;
//				else
//					if(iniState.x==goal.x && iniState.y==goal.y+1 && act==3)
//						return 200;
		else
			return -0.1;
	}
	// /////////////////////// Mover ////////////////////////////////////

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
		Motor.A.forward();
		Motor.B.forward();
		Delay.msDelay(1000);
		Motor.A.stop();
		Motor.B.stop();
		CorrectionFactorA();
	}

	private static void GoBackward() // NXT will go backward for 1 seconds
	{
		Motor.A.backward();
		Motor.B.backward();
		Delay.msDelay(1000);
		Motor.A.stop();
		Motor.B.stop();
		CorrectionFactorB();
	}

	private static void TurnLeft() // Turn the NXT to left
	{
		// Motor.A.rotate(4*360);
		Motor.A.rotateTo(Motor.A.getTachoCount() + 4 * 360);
		// Correction factor backwards
		Motor.A.backward();
		Delay.msDelay(250);
		Motor.A.stop();
	}

	private static void TurnRight() // Turn the NXT to right
	{
		// Motor.A.rotate(-4*360);
		Motor.A.rotateTo(Motor.A.getTachoCount() - 4 * 360);
		Motor.A.forward();
		Delay.msDelay(250);
		Motor.A.stop();
	}

	private static void GoLeft() // NXT will turn left and go forward for 1
									// seconds
	{
		TurnLeft();
		Delay.msDelay(1000);
		GoForward();
	}

	private static void GoRight() // NXT will go right and go forward for 1
									// seconds
	{
		TurnRight();
		Delay.msDelay(1000);
		GoForward();
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

	public static void main(String[] args) {
		LCD.drawString("Running", 0, 0);
		run();
	}

}
