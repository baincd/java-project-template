import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class JavaProjectSetup {

	static String defaultMavenGroupId = "com.example";
	static String defaultMavenArtifactId = "java-project-template";
	static String defaultJavaPackage = buildJavaPackage(defaultMavenGroupId,defaultMavenArtifactId);
	static String defaultJavaClassName = buildJavaClassName(defaultMavenArtifactId);
	static String defaultJavaPackagePath = buildJavaPackagePath(defaultJavaPackage);

	
	public static void main(String argv[]) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("*********************************");
		System.out.println("** Java Project Template Setup **");
		System.out.println("*********************************");
		System.out.println("");

		String newMavenGroupId = input(in, "maven group id", defaultMavenGroupId);
		String newMavenArtifactId = input(in, "maven artifact id", defaultMavenArtifactId);
		String newJavaPackage = input(in, "java package", buildJavaPackage(newMavenGroupId, newMavenArtifactId));
		String newJavaClassName = input(in, "java class name", buildJavaClassName(newMavenArtifactId));

		String newJavaPackagePath = buildJavaPackagePath(newJavaPackage);
		
		replaceInFile("pom.xml", 
				defaultMavenGroupId, newMavenGroupId,
				defaultMavenArtifactId, newMavenArtifactId,
				buildMavenDesc(defaultMavenArtifactId), buildMavenDesc(newMavenArtifactId));

		try {
			replaceInFile(".project", 
					defaultMavenArtifactId, newMavenArtifactId);
		} catch (FileNotFoundException fnfe) {
			// ignore if not a Eclipse project
		}

		moveAndReplaceInFile(
				format("src/main/java/{0}/{1}.java",defaultJavaPackagePath, defaultJavaClassName),
				format("src/main/java/{0}/{1}.java", newJavaPackagePath, newJavaClassName),
				defaultJavaClassName, newJavaClassName,
				defaultJavaPackage, newJavaPackage);

		moveAndReplaceInFile(
				format("src/test/java/{0}/{1}Tests.java",defaultJavaPackagePath, defaultJavaClassName),
				format("src/test/java/{0}/{1}Tests.java", newJavaPackagePath, newJavaClassName),
				defaultJavaClassName, newJavaClassName,
				defaultJavaPackage, newJavaPackage);
		
		moveAndReplaceInFile(
				format("src/test/java/{0}/TestFileUtils.java",defaultJavaPackagePath),
				format("src/test/java/{0}/TestFileUtils.java", newJavaPackagePath),
				defaultJavaClassName, newJavaClassName,
				defaultJavaPackage, newJavaPackage);

		removeEmptyDirs(new File("src/main/java"));
		removeEmptyDirs(new File("src/test/java"));
		
		new File(format("src/main/java/{0}.java",JavaProjectSetup.class.getName())).delete(); 
		new File("setup").delete();

		System.out.println("");
		System.out.println("Done!  Refresh your IDE to see changes.");

	}
	
	private static void removeEmptyDirs(File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				removeEmptyDirs(f);
			}
		}
		if (dir.list().length == 0) {
			dir.delete();
		}
	}

	private static void moveAndReplaceInFile(String origFilePath, String newFilePath, String...findAndReplaceParms) throws IOException {
		replaceInFile(origFilePath, findAndReplaceParms);
		moveFile(origFilePath, newFilePath);
	}

	private static void moveFile(String origFilePath, String newFilePath) {
		File origFile = new File(origFilePath);
		File newFile = new File(newFilePath);
		newFile.getParentFile().mkdirs();
		origFile.renameTo(newFile);
	}
	
	private static void replaceInFile(String filePath, String...findAndReplaceParms) throws IOException {
		File file = new File(filePath);
		String fileContents = readFileToString(file);
		Iterator<String> findAndReplaceIter = asList(findAndReplaceParms).iterator();
		while (findAndReplaceIter.hasNext()) {
			String pattern = findAndReplaceIter.next();
			String replace = findAndReplaceIter.next();
			fileContents = fileContents.replace(pattern, replace);
		}
		writeToFile(file, fileContents);
	}

	public static String readFileToString(File file) throws IOException {
		try ( FileReader reader = new FileReader(file) ) {
			StringBuilder sb = new StringBuilder();
			char[] inBuf = new char[1024 * 4];
			int inBufCount;
			while ( (inBufCount = reader.read(inBuf)) != -1) {
				sb.append(inBuf,0,inBufCount);
			}
			return sb.toString();
		}
	}
	
	public static void writeToFile(File file, String fileContents) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(fileContents);
		}
	}

	
	public static String input(BufferedReader in, String fieldName, String defaultValue) throws IOException {
		System.out.print(format("{0} (default={1}): ",fieldName, defaultValue));
		return defaultIfBlank(in.readLine(),defaultValue);
	}
	
	public static String buildJavaClassName(String artifactId) {
		return hyphenToCamelCase(artifactId,"") + "Application";
	}
	
	public static String buildMavenDesc(String artifactId) {
		return hyphenToCamelCase(artifactId, " ").trim();
	}
	
	public static String hyphenToCamelCase(String artifactId, String seperator) {
		StringBuilder sb = new StringBuilder();
		boolean capNextChar = true;
		for (char c : artifactId.toCharArray()) {
			if (c == '-' || c == '.') {
				capNextChar = true;
				continue;
			}
			if (capNextChar) {
				c = Character.toUpperCase(c);
				sb.append(seperator);
				capNextChar = false;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String buildJavaPackage(String groupId, String artifactId) {
		String pkg = groupId.replaceAll("-", "");
		String suffix = "." + artifactId.replaceAll("-", "");
		if (!pkg.endsWith(suffix)) {
			pkg += suffix;
		}
		return pkg;
	}
	
	public static String buildJavaPackagePath(String javaPackage) {
		return javaPackage.replace(".", "/");
	}
}
