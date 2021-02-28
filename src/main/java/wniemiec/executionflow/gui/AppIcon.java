package wniemiec.executionflow.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class AppIcon {
	
	private static final Image ICON;
	private static final Path ICON_LOCATION;
	
	static {
		ICON_LOCATION = Path.of(".", "src", "main", "resources", "images", "icon", "ef-icon.png");
		ICON = loadIcon();
	}
	
	private static Image loadIcon() {
		try {
			return ImageIO.read(ICON_LOCATION.toFile());
		} 
		catch (IOException e) {
			return null;
		}
	}

	public static Image getIcon() {
		return ICON;
	}
}
