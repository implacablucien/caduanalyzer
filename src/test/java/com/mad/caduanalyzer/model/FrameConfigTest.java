package com.mad.caduanalyzer.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FrameConfigTest {

    @Test
    void testFrameConfigUniqueness() {
        var config1 = new FrameConfig(42, 1, 1024, "AA", true, 23, 10, 2, true, true);
        var config2 = new FrameConfig(42, 2, 1115, "BB", false, 0, 8, 0, false, true);
        var config3 = new FrameConfig(42, 1, 996, "CC", true, 0, 12, 1, false, false);

        var set = new HashSet<FrameConfig>();
        set.add(config1);
        set.add(config2);

        // config3 has same SC/VC as config1, should be considered equal if equals/hashCode
        assertFalse(set.add(config3));
        assertEquals(2, set.size());
    }
}
