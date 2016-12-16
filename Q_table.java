class Q_Table
{
	int Q_SIZE;
	int numberActions;
	double Q[][][];
	public Q_Table(int Q_SIZE, int numberActions)
	{
		Q = new double[Q_SIZE][Q_SIZE][numberActions];
		this.Q_SIZE = Q_SIZE;
		this.numberActions = numberActions;
	}
}