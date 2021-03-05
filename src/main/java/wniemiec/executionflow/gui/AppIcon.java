package wniemiec.executionflow.gui;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import wniemiec.executionflow.App;

/**
 * Responsible for managing application icon.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class AppIcon {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Image ICON;
	private static final Path ICON_LOCATION;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		ICON_LOCATION = getBasePath().resolve(Path.of("resources", "images", 
													  "icon", "ef-icon.png"));
		ICON = loadIcon();
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private AppIcon() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static Path getBasePath() {
		return App.isDevelopment() 
				? Path.of(".", "src", "main") 
				: Path.of(".");
	}
	
	private static Image loadIcon() {
		try {
			return ImageIO.read(ICON_LOCATION.toFile());
		} 
		catch (IOException e) {
			return null;
		}
	}

	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public static Image getIcon() {
		return ICON;
	}
	
	public static Path getIconLocation() {
		return ICON_LOCATION;
	}
}
