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
		ICON_LOCATION = getBasePath().resolve(getIconRelativeLocation());
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
	private static Path getIconRelativeLocation() {
		return Path.of("resources", "images", "icon", "ef-icon.png");
	}
	
	private static Path getBasePath() {
		return App.isDevelopment() 
				? normalize(App.getAppRootPath().resolve(Path.of("src", "main")))
				: normalize(App.getAppRootPath());
	}
	
	private static Path normalize(Path p) {
		return p.normalize().toAbsolutePath();
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
