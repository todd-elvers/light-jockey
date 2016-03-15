package te.light_jockey.core

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import wslite.rest.RESTClient

import static te.light_jockey.misc.PropertiesFileReader.*

@Slf4j
class ConfigHandler {

    private static ConfigHandler instance = null
    public static final String IP_ADDRESS_PROP_NAME = 'ip_address'
    public static final String USERNAME_PROP_NAME = 'username'
    private static final String CONFIG_FILE_NAME = "light-jockey.properties"
    private final File temporaryDirectory
    private final File configurationFile

    static ConfigHandler getInstance() {
        if(!instance) instance = new ConfigHandler()
        return instance
    }

    private ConfigHandler() {
        String tempDirPath = System.getProperty("java.io.tmpdir")
        if(!tempDirPath) {
            throw new FileNotFoundException("Could not determine temporary directory location. Setting the VM arg -Djava.io.tmpdir can fix this problem.")
        }
        temporaryDirectory = new File(tempDirPath)
        configurationFile = new File(temporaryDirectory, CONFIG_FILE_NAME)
    }

    //TODO: RESUME HERE!!
    void updateConfigFile(Map propsToAdd) {
        Properties configFileProps = readConfigProperties().putAll(propsToAdd)
        log.debug("Updating the configuration file to: $propsToAdd")
        // This line is causing: (42) Cannot invoke method store() on null object
        configFileProps.store(new FileOutputStream(configurationFile), "LightJockey configuration file :: auto-generated on ${new Date()}")
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
        props.get(IP_ADDRESS_PROP_NAME) && props.get(USERNAME_PROP_NAME)
    }

    private static boolean credentialsStillValid(Properties props) {
        try {
            String username = props.get(USERNAME_PROP_NAME)
            String ipAddress = props.get(IP_ADDRESS_PROP_NAME)
            return new RESTClient("http://$ipAddress/api/$username").get().json.error
        } catch (Throwable ignored) {
            return false
        }
    }
}
