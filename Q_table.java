import java.io.Serializable;
class Q_Table{
	static int Q_SIZE;
	static int numberActions;
	public static double Q[][][] = new double[Q_SIZE][Q_SIZE][numberActions];
	public Q_Table(int Q_SIZE, int numberActions)
	{
		this.Q_SIZE = Q_SIZE;
		this.numberActions = numberActions;
	}
}
