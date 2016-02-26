package te.light_jockey.core

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField
import static org.apache.commons.lang3.reflect.FieldUtils.writeField

class TestUtils {

    /**
     * Reflectively sets the value of a final field on an object. <br/>
     * Although this method was created to handle final fields, it also works
     * with non-final fields.
     *
     * @param instance the instance with the field to set
     * @param fieldName the name of the field to set
     * @param value the new value to set the field to
     */
    public static void setFinalField(Object instance, String fieldName, value) {
        writeField(
                getDeclaredField(instance.class, fieldName, true),
                instance,
                value
        )
    }

}
