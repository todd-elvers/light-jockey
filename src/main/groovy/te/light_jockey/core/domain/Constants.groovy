package te.light_jockey.core.domain

import groovy.transform.CompileStatic

@CompileStatic
class Constants {
    public static final String CONFIG_FILE_NAME = "light-jockey.properties"
    public static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir"
	public static final String TEMP_DIR_SYSTEM_PROPERTY_INVALID_MSG = """\
        Could not determine your temp directory's location.  This shouldn't really happen.
        Setting the JVM arg 'java.io.tmpdir' can fix this problem.
    """.stripIndent()
}
