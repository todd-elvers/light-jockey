package te.light_jockey.misc

import org.apache.commons.io.FileUtils

class PropertiesFileReader {
    static final PROPERTIES_FILE_NAME = "gradle.properties"

    static String readAppProperty(String propName) {
        try {
            return loadPropertiesFromInputStream(resolveAppPropFileInputStream())[propName]
        } catch (ignored) {
            throw new RuntimeException("Unable to read '${propName}' from LightJockey's properties file.")
        }
    }

    private static InputStream resolveAppPropFileInputStream() {
        def propFileInputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(PROPERTIES_FILE_NAME)
        if (!propFileInputStream) {
            propFileInputStream = FileUtils.openInputStream(PROPERTIES_FILE_NAME as File)
        }
        return propFileInputStream
    }

    private static Properties loadPropertiesFromInputStream(InputStream inputStream) {
        try {
            def properties = new Properties()
            properties.load(inputStream)
            return properties
        } finally {
            inputStream.close()
        }
    }
}
