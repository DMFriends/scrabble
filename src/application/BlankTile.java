package application;

public class BlankTile extends Tile
{
	private char assignedLetter;

	public BlankTile()
	{
		super('_', 0, true);
		this.assignedLetter = '\0';
	}

	public void setAssignedLetter(char c)
	{
		this.assignedLetter = Character.toLowerCase(c);
	}

	public char getAssignedLetter()
	{
		return assignedLetter;
	}

	public boolean isAssigned()
	{
		return assignedLetter != '\0';
	}

	@Override
	public char getLetter()
	{
		return isAssigned() ? assignedLetter : '_';
	}

	@Override
	public String toString()
	{
		return String.valueOf(getLetter());
	}
}
