package te.light_jockey.misc

import org.apache.commons.io.FileUtils

class PropertiesFileReader {
    static final APP_PROP_FILENAME = "gradle.properties"

    static String readAppProperty(String propName) {
        try {
            return loadPropertiesFromInputStream(resolveInputStream(APP_PROP_FILENAME))[propName]
        } catch (ignored) {
            throw new RuntimeException("Unable to read '${propName}' from LightJockey's properties file.")
        }
    }

    private static InputStream resolveInputStream(String filename) {
        def propFileInputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
        if (!propFileInputStream) {
            propFileInputStream = FileUtils.openInputStream(filename as File)
        }
        return propFileInputStream
    }

    static Properties readFile(String propFilename) {
        try {
            return loadPropertiesFromInputStream(resolveInputStream(propFilename))
        } catch (ignored) {
            throw new RuntimeException("Unable to read from properties file.")
        }
    }

    static Properties loadPropertiesFromInputStream(InputStream inputStream) {
        try {
            def properties = new Properties()
            properties.load(inputStream)
            return properties
        } finally {
            inputStream.close()
        }
    }
}
