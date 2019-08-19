package org.sriki.gpeek;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;

public abstract class AbstractBaseTestcase {
    private static final String TEMP_TEST_DIR;

    private static final File projectRootDirectory = projectRootDirectory();

    protected static File tempDir;

    static {
        TEMP_TEST_DIR = projectRootDirectory + "/tmp";
    }

    private static File projectRootDirectory() {
        String testClassesRoot = AbstractBaseTestcase.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile();
        File rootDir = new File(testClassesRoot + "../..");
        try {
            return rootDir.getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find project root directory", e);
        }
    }

    @BeforeClass
    @BeforeAll
    public static void initTestTempDirectory() throws Exception {
        tempDir = new File(TEMP_TEST_DIR);
        if (tempDir.exists()) {
            FileUtils.forceDelete(tempDir);
        }
        tempDir.mkdirs();
    }

    @AfterClass
    @AfterAll
    public static void removeTempDirectory() throws Exception {
        FileUtils.forceDelete(tempDir);
    }
}
