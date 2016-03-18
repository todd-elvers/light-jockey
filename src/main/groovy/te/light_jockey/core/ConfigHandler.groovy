package te.light_jockey.core

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import wslite.rest.RESTClient

import static te.light_jockey.core.domain.Constants.*
import static te.light_jockey.misc.PropertiesFileReader.loadPropertiesFromInputStream

@Slf4j
@Singleton(strict = false)
class ConfigHandler {
    public static final String IP_ADDRESS_PROP = 'ip_address'
    public static final String USERNAME_PROP = 'username'
    private final File temporaryDirectory
    private final File configurationFile

    private ConfigHandler() {
        String tempDirPath = System.getProperty(TEMP_DIR_SYSTEM_PROPERTY)
        if(!tempDirPath) throw new FileNotFoundException(TEMP_DIR_SYSTEM_PROPERTY_INVALID_MSG)

        temporaryDirectory = new File(tempDirPath)
        configurationFile = new File(temporaryDirectory, CONFIG_FILE_NAME)
    }

    void updateConfigFile(Map propsToAdd) {
        Properties configFileProps = readConfigProperties()
        configFileProps.putAll(propsToAdd)

        log.debug("Updating config. file to:\n\t{}", configFileProps.collect{"$it.key: $it.value"}.join("\n\t"))
        configFileProps.store(new FileOutputStream(configurationFile), "LightJockey configuration file")
    }

    void createConfigFile() {
        FileUtils.writeStringToFile(configurationFile, "", false)
    }

    Properties readConfigProperties() {
        loadPropertiesFromInputStream(FileUtils.openInputStream(configurationFile))
    }

    boolean configFileExists() {
        configurationFile.exists() && configurationFile.canRead() && configurationFile.canWrite()
    }

    boolean configFileIsStillValid() {
        Properties props = readConfigProperties()
        credentialsExist(props) && credentialsStillValid(props)
    }

    private static boolean credentialsExist(Properties props) {
        props.get(IP_ADDRESS_PROP) && props.get(USERNAME_PROP)
    }

    private static boolean credentialsStillValid(Properties props) {
        try {
            String username = props.get(USERNAME_PROP)
            String ipAddress = props.get(IP_ADDRESS_PROP)
            return !(new RESTClient("http://$ipAddress/api/$username").get().json.error)
        } catch (Throwable ignored) {
            return false
        }
    }
}
