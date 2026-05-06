package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileBag
{
	private List<Tile> allTiles = new ArrayList<>();
	
	public TileBag()
	{
		List<String[]> letters = LoadTextFile.loadCSV("letters.csv");
		
		for(String[] line : letters)
		{
			for(int i = 0; i < Integer.parseInt(line[2]); i++)
			{
				allTiles.add(new Tile(line[0].charAt(0), 
						Integer.parseInt(line[1]), line[0].charAt(0) != '0'));
			}
		}
	}
	
	public void shuffle()
	{
		Collections.shuffle(allTiles);
	}
	
	public Tile drawTile()
	{
		return allTiles.remove(allTiles.size() - 1); // returns null if bag is empty
	}
	
	public List<Tile> drawTiles(int n)
	{
		List<Tile> tiles = new ArrayList<>();
		
		for(int i = 0; i < n; i++)
		{
			tiles.add(drawTile());
		}
		
		return tiles;
	}
	
	public void returnTile(Tile t)
	{
		allTiles.add(t);
		shuffle();
	}
	
	public void returnTiles(ArrayList<Tile> tiles)
	{
		for(Tile t : tiles)
		{
			allTiles.add(t);
		}
		
		shuffle();
	}
	
	public boolean isEmpty()
	{
		return allTiles.isEmpty();
	}
	
	public int tilesRemaining()
	{
		return allTiles.size();
	}
}
