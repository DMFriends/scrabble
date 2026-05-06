package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rack
{
	private List<Tile> tiles = new ArrayList<>();
	public static final int MAX_TILES = 9;
	
	public Rack()
	{
		
	}
	
	public void drawFromBag(TileBag bag)
	{
		int numTilesNeeded = 7 - tiles.size();
		List<Tile> tilesToAdd = bag.drawTiles(numTilesNeeded);
		
		for(int i = 0; i < tilesToAdd.size(); i++)
		{
			if(tiles.get(i) != null)
			{
				tiles.add(tilesToAdd.get(i));
			}
			else
			{
				break;
			}
		}
	}
	
	public boolean removeTile(Tile t)
	{
		return tiles.remove(t);
	}
	
	public void returnTile(Tile t)
	{
		tiles.add(t);
	}
	
	public List<Tile> getTiles()
	{
	    return Collections.unmodifiableList(tiles);
	}
	
	public boolean isEmpty()
	{
		return tiles.isEmpty();
	}
	
	public int size()
	{
		return tiles.size();
	}
	
	public boolean contains(Tile t)
	{
		return tiles.contains(t);
	}
}
