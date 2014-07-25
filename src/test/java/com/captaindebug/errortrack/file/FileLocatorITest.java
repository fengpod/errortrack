package com.captaindebug.errortrack.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.captaindebug.errortrack.report.Results;
import com.captaindebug.errortrack.report.Results.ErrorResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:error-track-context.xml" })
public class FileLocatorITest {

	Logger logger = LoggerFactory.getLogger(FileLocatorITest.class);

	private String logFileDir;

	@Autowired
	private FileLocator instance;

	@Autowired
	private Results report;

	Map<String, List<Results.ErrorResult>> results;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		logFileDir = System.getProperty("user.dir") + "/src/test/resources/";
		logger.info("Using directory: " + logFileDir);
		report.clear();

		results = (Map<String, List<ErrorResult>>) ReflectionTestUtils.getField(report, "results");
	}

	@Test
	public void testErrorFreeLog() {

		String testFile = logFileDir + "error-free.log";
		testFileAvailable(testFile);

		ReflectionTestUtils.setField(instance, "scanIn", testFile);

		instance.findFile();

		assertEquals(1, results.size());
		List<Results.ErrorResult> errorList = results.get(testFile);
		assertNotNull(errorList);
		assertEquals(0, errorList.size());
	}

	private void testFileAvailable(String testFile) {
		File file = new File(testFile);
		assertTrue(file.exists());

		file.setLastModified(System.currentTimeMillis());
	}

	@Test
	public void testContainsErrorsLog() {

		String testFile = logFileDir + "contains-errors.log";
		testFileAvailable(testFile);
		ReflectionTestUtils.setField(instance, "scanIn", testFile);

		instance.findFile();

		assertEquals(1, results.size());
		List<Results.ErrorResult> errorList = results.get(testFile);
		assertNotNull(errorList);
		assertEquals(1, errorList.size());

		Results.ErrorResult errorResult = errorList.get(0);
		assertEquals(4, errorResult.getLineNumber());
	}

	@Test
	public void testExclude() {

		String testFile = logFileDir + "exclude.log";
		testFileAvailable(testFile);
		ReflectionTestUtils.setField(instance, "scanIn", testFile);

		instance.findFile();

		assertEquals(1, results.size());
		List<Results.ErrorResult> errorList = results.get(testFile);
		assertNotNull(errorList);
		assertEquals(0, errorList.size());
	}

}
