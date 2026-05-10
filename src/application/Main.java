package application;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.Objects;
import java.util.Set;

public class Main extends Application
{
	public static final String APP_VERSION = "v1.1";
	public static Set<String> dictionary = LoadTextFile.loadWords("/resources/dictionary.txt");
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{	        
	        primaryStage.setTitle("Scrabble " + APP_VERSION);
	        
	        primaryStage.getIcons().add(
			    new Image(
			        Objects.requireNonNull(
			            getClass().getResourceAsStream("/resources/scrabble.png")
			        )
			    )
			);
	        
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
