package com.opower.unitsofmeasure;

import java.io.IOException;
import java.text.ParsePosition;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;
import org.jscience.physics.unit.formats.UCUMFormat;
import org.unitsofmeasurement.unit.Unit;

/**
 * Configures Jackson to (de)serialize JScience Unit objects using their UCUM representation, since the actual objects don't
 * translate well into JSON.
 */
public class UnitJacksonModule extends SimpleModule {
    public UnitJacksonModule() {
        super("UnitJsonSerializationModule", new Version(1, 0, 0, null));

        addSerializer(Unit.class, new UnitJsonSerializer());
        addDeserializer(Unit.class, new UnitJsonDeserializer());
    }

    private class UnitJsonSerializer extends ScalarSerializerBase<Unit> {
        protected UnitJsonSerializer() {
            super(Unit.class);
        }

        @Override
        public void serialize(Unit unit, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (unit == null) {
                jgen.writeNull();
            }
            else {
                // Format the unit using the standard UCUM representation.
                // The string produced for a given unit is always the same; it is not affected by the locale.
                // It can be used as a canonical string representation for exchanging units.
                String ucumFormattedUnit = UCUMFormat.getCaseSensitiveInstance().format(unit, new StringBuilder()).toString();

                jgen.writeString(ucumFormattedUnit);
            }
        }
    }

    private class UnitJsonDeserializer extends StdScalarDeserializer<Unit> {
        protected UnitJsonDeserializer() {
            super(Unit.class);
        }

        @Override
        public Unit deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();

            if (currentToken == JsonToken.VALUE_STRING) {
                return UCUMFormat.getCaseInsensitiveInstance().parse(jsonParser.getText(), new ParsePosition(0));
            }
            throw deserializationContext.wrongTokenException(jsonParser,
                                                             JsonToken.VALUE_STRING,
                                                             "Expected unit value in String format");
        }
    }
}
