package edu.illinois.library.cantaloupe.resource.iiif.v3;

import edu.illinois.library.cantaloupe.image.Identifier;
import edu.illinois.library.cantaloupe.operation.Crop;
import edu.illinois.library.cantaloupe.operation.Encode;
import edu.illinois.library.cantaloupe.operation.Operation;
import edu.illinois.library.cantaloupe.operation.OperationList;
import edu.illinois.library.cantaloupe.operation.Rotate;
import edu.illinois.library.cantaloupe.operation.Scale;
import edu.illinois.library.cantaloupe.resource.IllegalClientArgumentException;
import edu.illinois.library.cantaloupe.resource.iiif.FormatException;
import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ParametersTest extends BaseTest {

    private static final double DELTA = 0.00000001;

    private Parameters instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = new Parameters(
                new Identifier("identifier"),
                "0,0,200,200",
                "pct:50",
                "5",
                "default",
                "jpg");
    }

    @Test
    void testFromURI() {
        instance = Parameters.fromURI("bla/20,20,50,50/pct:90/15/bitonal.jpg");
        assertEquals("bla", instance.getIdentifier().toString());
        assertEquals("20,20,50,50", instance.getRegion().toString());
        assertEquals(90, instance.getSize().getPercent(), DELTA);
        assertEquals(15, instance.getRotation().getDegrees(), DELTA);
        assertEquals(Quality.BITONAL, instance.getQuality());
        assertEquals(OutputFormat.JPG, instance.getOutputFormat());
    }

    @Test
    void testFromURIWithInvalidURI1() {
        assertThrows(IllegalClientArgumentException.class,
                () -> Parameters.fromURI("bla/20,20,50,50/15/bitonal.jpg"));
    }

    @Test
    void testFromURIWithInvalidURI2() {
        assertThrows(IllegalClientArgumentException.class,
                () -> Parameters.fromURI("bla/20,20,50,50/pct:90/15/bitonal"));
    }

    @Test
    void testCopyConstructor() {
        Parameters copy = new Parameters(instance);
        assertEquals(copy.getIdentifier(), instance.getIdentifier());
        assertEquals(copy.getRegion(), instance.getRegion());
        assertEquals(copy.getSize(), instance.getSize());
        assertEquals(copy.getRotation(), instance.getRotation());
        assertEquals(copy.getQuality(), instance.getQuality());
        assertEquals(copy.getOutputFormat(), instance.getOutputFormat());
        assertEquals(copy.getQuery(), instance.getQuery());
    }

    @Test
    void testConstructor3WithUnsupportedQuality() {
        assertThrows(IllegalClientArgumentException.class,
                () -> new Parameters(
                        new Identifier("identifier"),
                        "0,0,200,200",
                        "pct:50",
                        "5",
                        "bogus",
                        "jpg"));
    }

    @Test
    void testConstructor3WithUnsupportedOutputFormat() {
        assertThrows(FormatException.class, () ->
                new Parameters(
                        new Identifier("identifier"),
                        "0,0,200,200",
                        "pct:50",
                        "5",
                        "default",
                        "bogus"));
    }

    @Test
    void testToOperationList() {
        final OperationList opList = instance.toOperationList(1);
        Iterator<Operation> it = opList.iterator();
        assertTrue(it.next() instanceof Crop);
        assertTrue(it.next() instanceof Scale);
        assertTrue(it.next() instanceof Rotate);
        assertTrue(it.next() instanceof Encode);
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testToString() {
        assertEquals("identifier/0,0,200,200/pct:50/5/default.jpg",
                instance.toString());
    }

}
