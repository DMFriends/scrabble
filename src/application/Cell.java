package application;

public class Cell
{
	private Tile currentTile;
	private PremiumType premiumType;
	
	public Cell(PremiumType premiumType)
	{
	    this.premiumType = premiumType;
	    this.currentTile = null;
	}
	
	public void placeTile(Tile t)
	{
		if(currentTile != null) currentTile = t;
	}
	
	public Tile getTile()
	{
		return currentTile;
	}
	
	public PremiumType getPremiumType()
	{
		return premiumType;
	}
	
	public boolean isOccupied()
	{
		return currentTile == null;
	}
}
