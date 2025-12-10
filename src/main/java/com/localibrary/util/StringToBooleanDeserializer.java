package com.localibrary.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializador customizado para converter Strings em boolean
 * Permite: "true"/"false", "1"/"0", true/false
 */
public class StringToBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        
        String value = jsonParser.getValueAsString();
        
        if (value == null || value.isBlank()) {
            return false;
        }
        
        return value.equalsIgnoreCase("true") 
            || value.equals("1")
            || value.equalsIgnoreCase("yes")
            || value.equalsIgnoreCase("sim");
    }
}
