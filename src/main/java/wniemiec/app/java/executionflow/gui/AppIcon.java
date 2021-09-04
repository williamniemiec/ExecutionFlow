package wniemiec.app.java.executionflow.gui;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import wniemiec.app.java.ExecutionFlow;

/**
 * Responsible for managing application icon.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
		return ExecutionFlow.isDevelopment() 
				? normalize(ExecutionFlow.getAppRootPath().resolve(Path.of("src", "main")))
				: normalize(ExecutionFlow.getAppRootPath());
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
