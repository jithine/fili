package com.yahoo.wiki.webservice.data.config.dimension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * Deserialize dimension fields
 */
public class DimensionFieldDeserializer extends JsonDeserializer<WikiDimensionFieldConfigTemplate> {

    @Override
    public WikiDimensionFieldConfigTemplate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        WikiDimensionFieldConfigTemplate wikiDimensionField = new WikiDimensionFieldConfigTemplate();

        if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
            wikiDimensionField.setFieldName(jp.getText());
        } else {
            ObjectCodec oc = jp.getCodec();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode node = oc.readTree(jp);
            LinkedHashSet<WikiDimensionFieldSetsTemplate> list = objectMapper.convertValue(node, new TypeReference<LinkedHashSet<WikiDimensionFieldSetsTemplate>>() {
            });
            wikiDimensionField.setFieldList(list);
        }
        return wikiDimensionField;
    }

}
