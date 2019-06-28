package com.example.javaprojecttemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.net.URI;

import org.junit.Test;

import lombok.SneakyThrows;

public class TestFileUtils {
	
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	@SneakyThrows
	public static File classpathFile(String pathRelativeToClasspath) {
		URI filePathUri = TestFileUtils.class.getClassLoader().getResource(pathRelativeToClasspath).toURI();
		return new File(filePathUri);
	}
	
	@SneakyThrows
	public static String readFileToString(File file) {
		try ( FileReader reader = new FileReader(file) ) {
			StringBuilder sb = new StringBuilder();
			char[] inBuf = new char[DEFAULT_BUFFER_SIZE];
			int inBufCount;
			while ( (inBufCount = reader.read(inBuf)) != -1) {
				sb.append(inBuf,0,inBufCount);
			}
			return sb.toString();
		}
	}
	
	@Test
	public void readingClasspathFileShouldNotThrowException() {
		File file = classpathFile("application.properties");
		String fileContents = readFileToString(file);
		
		assertThat(fileContents).isNotNull();
	}
}
