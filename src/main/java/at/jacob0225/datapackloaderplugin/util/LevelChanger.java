package at.jacob0225.datapackloaderplugin.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LevelChanger {

    private final Logger logger;
    private final LanguageManager languageManager;

    public void changeLevelName() throws IOException {
        logger.info(languageManager.getMessage("developer.changing-level"));
        logger.info(languageManager.getMessage("developer.new-worlds"));
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("server.properties"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IOException("IOException: Could not read 'server.properties'.\n" + e.getMessage());
        }

        for (String line : lines) {
            if (line.startsWith("level-name=")) {
                if (line.equals("level-name=world")) {
                    lines.set(lines.indexOf(line), "level-name=wor1d");
                } else {
                    lines.set(lines.indexOf(line), "level-name=world");
                }

                break;
            }
        }

        try (FileWriter fileWriter = new FileWriter("server.properties")) {
            for (String string : lines) {
                fileWriter.write(string + "\n");
            }
        } catch (IOException e) {
            throw new IOException("IOException: Could not write to 'server.properties'.\n" + e.getMessage());
        }
    }
}
