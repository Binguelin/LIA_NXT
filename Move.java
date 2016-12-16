import lejos.robotics.navigation.DifferentialPilot;

public class Move
{
		DifferentialPilot pilot;
		public void GoForward() // NXT will go forward for 1 seconds
		{
			pilot.travel(18.5);
		}
		public void GoBackward() // NXT will go backward for 1 seconds
		{
			pilot.travel(-18.5);
		}
		public void TurnLeft() // Turn the NXT to left
		{
			pilot.rotate(-118);
		}
		public void TurnRight() // Turn the NXT to right
		{
			pilot.rotate(118);
		}
		public void GoLeft() // NXT will turn left and go forward
		{
			TurnLeft();
			GoForward();
			TurnRight();
		}
		public void GoRight() // NXT will go right and go forward
		{
			TurnRight();
			GoForward();
			//TurnLeft();
		}
		public void Doaction(Qlearning.Actions act) {
			switch (act) {
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
}