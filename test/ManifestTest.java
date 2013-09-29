import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ManifestTest {

    @Test
    public void shouldHaveTheSameJarsInManifestAndAntBuild() throws IOException {
        ArrayList<String> jarsFromManifest = allLinesIn("src//MANIFEST.MF");
        ArrayList<String> jarsFromAntBuild = getJarNamesFromAntBuild("src//build.xml");

        for (String jarInAntBuild : jarsFromAntBuild) {
            assertTrue("Manifest is missing jar from ant build: " + jarInAntBuild, jarsFromManifest.contains(jarInAntBuild));
        }
    }

    private ArrayList<String> exclusions() {
        ArrayList<String> exclusions = new ArrayList<String>();
        exclusions.add("junit-4.10.jar");
        exclusions.add("mockito-all-1.9.5.jar");
        return exclusions;
    }

    private ArrayList<String> getJarNamesFromAntBuild(String filename) throws FileNotFoundException {
        return excludeUnwanted(jarNames(allLinesIn(filename)));
    }

    private ArrayList<String> excludeUnwanted(ArrayList<String> linesWithJars) {
        linesWithJars.removeAll(exclusions());
        return linesWithJars;
    }

    private ArrayList<String> jarNames(ArrayList<String> allLines) {
        ArrayList<String> jarNames = new ArrayList<String>();
        for (String antLine : allLines) {
            Pattern jarNameInAntPathElement = Pattern.compile("\\s*<pathelement\\s+location=\".*/(.*\\.jar)\".*");
            Matcher matcher = jarNameInAntPathElement.matcher(antLine);
            if (matcher.matches()) {
                jarNames.add(matcher.group(1));
            }
        }
        return jarNames;
    }

    private ArrayList<String> allLinesIn(String filename) throws FileNotFoundException {
        ArrayList<String> jarList = new ArrayList<String>();
        File file = new File(filename);

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            jarList.add(line.trim());
        }
        return jarList;
    }
}
