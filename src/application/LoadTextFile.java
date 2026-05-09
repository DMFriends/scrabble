package application;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Scanner;

public class LoadTextFile
{
	/**
	 * Loads words from the specified text file into a HashSet. Each word is trimmed
	 * of whitespace and converted to lowercase for case-insensitive lookups.
	 *
	 * @return A Set of lowercase words from the file, or an empty set if an error
	 *         occurs.
	 */
	public static Set<String> loadWords(String fileName)
	{
	    try (InputStream input =
	             LoadTextFile.class.getResourceAsStream(fileName);
	         BufferedReader reader =
	             new BufferedReader(new InputStreamReader(input)))
	    {
	        Set<String> words = reader.lines()
	                .map(String::trim)
	                .filter(s -> !s.isEmpty())
	                .map(String::toLowerCase)
	                .collect(Collectors.toCollection(HashSet::new));
	        
	        System.out.println("Successfully loaded " + words.size() + " words from " + fileName);
	        return words;
	    }
	    catch (Exception e)
	    {
	    	System.err.println("Error reading file: " + e.getMessage());
	        e.printStackTrace();
	        return Collections.emptySet();
	    }
	}

	public static ArrayList<String[]> loadCSV(String fileName)
	{
	    try (InputStream input =
	             LoadTextFile.class.getResourceAsStream(fileName);
	         Scanner file = new Scanner(input))
	    {
	        ArrayList<String[]> letters = new ArrayList<>();

	        while(file.hasNextLine())
	        {
	            String[] line = file.nextLine().split(",");
	            letters.add(line);
	        }
	        
	        System.out.println("Successfully loaded " + fileName);
	        return letters;
	    }
	    catch (Exception e)
	    {
	    	System.err.println("Error reading file: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
}