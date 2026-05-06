package application;

public class Tile
{
	private final char letter;
	private final int pointValue;
	private final boolean isBlank;

	public Tile(char letter, int pointValue, boolean isBlank)
	{
		this.letter = letter;
		this.pointValue = pointValue;
		this.isBlank = isBlank;
	}

	public char getLetter()
	{
		return letter;
	}

	public int getPointValue()
	{
		return pointValue;
	}

	public boolean isBlank()
	{
		return isBlank;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(letter);
	}
}
