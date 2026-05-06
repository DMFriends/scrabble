package application;

public class Player
{
	private int playerID;
	private Rack rack;
	private int score;
	private boolean isTurn;
	
	public Player(int playerID)
	{
		this.playerID = playerID;
		this.rack = new Rack();
		this.score = 0;
		this.isTurn = false;
	}
	
	public void addScore(int score)
	{
		this.score += score;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getPlayerID()
	{
		return playerID;
	}
	
	public Rack getRack()
	{
		return rack;
	}
	
	public boolean isTurn()
	{
		return isTurn;
	}
	
	public void setTurn(boolean isTurn)
	{
		this.isTurn = isTurn;
	}
	
	public void drawTiles(TileBag bag)
	{
		rack.drawFromBag(bag);
	}
	
	public boolean hasEmptyRack()
	{
		return rack.isEmpty();
	}
}
