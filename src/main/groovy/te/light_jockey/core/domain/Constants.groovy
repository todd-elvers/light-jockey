package te.light_jockey.core.domain

import groovy.transform.CompileStatic

@CompileStatic
class Constants {
    public static final String CONFIG_FILE_NAME = "light-jockey.properties"
    public static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir"
	public static final String TEMP_DIR_SYSTEM_PROPERTY_INVALID_MSG = """\
        Could not determine your temp directory's location.
        This should never really happen... but setting the JVM arg 'java.io.tmpdir' will fix this problem.
    """.stripIndent()
}
