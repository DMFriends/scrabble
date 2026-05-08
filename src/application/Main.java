package application;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.util.Set;

public class Main extends Application
{
	public static Set<String> dictionary = LoadTextFile.loadWords("dictionary.txt");
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Image icon = new Image("assets/scrabble.png");
	        primaryStage.getIcons().add(icon);
	        primaryStage.setTitle("Scrabble");
	        primaryStage.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth());
	        primaryStage.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
	        primaryStage.setResizable(false);

	        StartScreen startScreen = new StartScreen(primaryStage);
	        primaryStage.setScene(startScreen.getScene());
	        primaryStage.show();     
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
