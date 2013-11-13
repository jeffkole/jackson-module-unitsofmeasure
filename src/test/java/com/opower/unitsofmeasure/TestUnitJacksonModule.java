package com.opower.unitsofmeasure;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jscience.physics.unit.SI;
import org.jscience.physics.unit.UCUM;
import org.unitsofmeasurement.unit.Unit;

import static org.jscience.physics.unit.SIPrefix.KILO;
import static org.junit.Assert.*;

/**
 * Unit tests for UnitJacksonModule
 */
public class TestUnitJacksonModule {
    // can't directly unit test the Jackson Module classes; need to go through JsonFactory
    private JsonFactory jsonFactory;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new UnitJacksonModule());

        this.jsonFactory = new JsonFactory(mapper);
    }

    @Test
    public void testSerializeArea() throws Exception {
        assertEquals("Expected JSON with a UCUM representation of the area unit", "\"m2\"", serialize(SI.SQUARE_METRE));
    }

    @Test
    public void testSerializeTemperature() throws Exception {
        assertEquals("Expected JSON with a UCUM representation of the temperature unit",
                     "\"[degF]\"", serialize(UCUM.FAHRENHEIT));
        assertEquals("Expected JSON with a UCUM representation of the temperature unit",
                     "\"Cel\"", serialize(SI.CELSIUS));
    }
    @Test
    public void testSerializeLength() throws Exception {
        assertEquals("Expected JSON with a UCUM representation of the length unit",
                     "\"[mi_i]\"", serialize(UCUM.MILE_INTERNATIONAL));

        assertEquals("Expected JSON with a UCUM representation of the length unit", "\"km\"", serialize(KILO(SI.METRE)));
    }

    @Test
    public void testParseArea() throws Exception {
        Unit parsedAmount = parse("\"[sft_i]\"", Unit.class);

        assertEquals("The Unit<Area> in the parsed JSON doesn't match", UCUM.SQUARE_FOOT_INTERNATIONAL, parsedAmount);
    }

    @Test
    public void testParseTemperature() throws Exception {
        Unit parsedAmount = parse("\"Cel\"", Unit.class);
        assertEquals("The Unit<Temperature> in the parsed JSON doesn't match", SI.CELSIUS, parsedAmount);
    }

    @Test
    public void testParseLength() throws Exception {
        Unit parsedAmount = parse("\"km\"", Unit.class);

        assertEquals("The Unit<Length> in the parsed JSON doesn't match", KILO(SI.METRE), parsedAmount);
    }

    @Test(expected = JsonParseException.class)
    public void testParseWithUnrecognizedField() throws Exception {
        parse("foobar", Unit.class);
    }

    protected String serialize(Object objectToSerialize) throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = this.jsonFactory.createJsonGenerator(writer);

        generator.writeObject(objectToSerialize);
        generator.close();
        return writer.toString();
    }

    protected <T> T parse(String json, Class<T> aClass) throws IOException {
        JsonParser parser = this.jsonFactory.createJsonParser(json);
        T object = parser.readValueAs(aClass);

        parser.close();
        return object;
    }
}
