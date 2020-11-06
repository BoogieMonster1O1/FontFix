package io.github.boogiemonster1o1.fontfix;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigLoader {
	private static final String[] LOGICAL_FONTS = {"Serif", "SansSerif", "Dialog", "DialogInput", "Monospaced"};
	private final Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	private final Properties props = new Properties();

	public void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("fontfix.properties");
		try {
			if (Files.exists(path)) {
				this.props.load(Files.newInputStream(path));
			} else {
				Files.copy(this.getClass().getResourceAsStream("/fontfix-default-config.properties"), path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFontName(String defaultValue) {
		String fontName = this.getProps().getProperty("font.name");

		if (Objects.isNull(fontName)) {
			return defaultValue;
		}

		String searchName = fontName.replaceAll("[- ]", "").toLowerCase();

		for (String logicalFont : LOGICAL_FONTS) {
			if (logicalFont.compareToIgnoreCase(searchName) == 0) {
				return logicalFont;
			}
		}

		String altSearchName = searchName + " medium";
		String partialMatch = null;

		for (Font font : this.allFonts) {
			String name = font.getName().replaceAll("[- ]", "");
			if (name.compareToIgnoreCase(searchName) == 0 || name.compareToIgnoreCase(altSearchName) == 0) {
				return font.getName();
			}

			if ((name + font.getFamily()).replaceAll("[- ]", "").toLowerCase().contains(searchName)) {
				if (partialMatch == null || partialMatch.length() > font.getName().length()) {
					partialMatch = font.getName();
				}
			}
		}

		if(partialMatch != null) {
			return partialMatch;
		}

		FontFix.LOGGER.error("FontFix cannot find font.name \"{}\"", fontName);
		return defaultValue;
	}

	public int getFontSize(int defaultValue)
	{
		String value = this.getProps().getProperty("font.size");

		if(value == null) {
			return defaultValue;
		}

		try {
			int i = Integer.parseInt(value);

			if(i <= 0) {
				throw new NumberFormatException();
			}

			defaultValue = i;
		} catch(NumberFormatException e) {
			FontFix.LOGGER.error("FontFix font.size must be an integer greater than zero");
		}

		return defaultValue;
	}

	public boolean getBoolean(String propertyName, boolean defaultValue) {
		String value = this.getProps().getProperty(propertyName);
		if(value == null) {
			return defaultValue;
		} else if(value.compareToIgnoreCase("true") == 0) {
			return true;
		} else if(value.compareToIgnoreCase("false") == 0) {
			return false;
		} else {
			FontFix.LOGGER.error("FontFix " + propertyName + " must be either \"true\" or \"false\"");
			return defaultValue;
		}
	}

	public Properties getProps() {
		return this.props;
	}
}
