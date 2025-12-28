package com.netbullet.trap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathTrapTest {

    @Test
    void testAdd() {
        // Weak test: 0 + 0 = 0.
        // A mutant that returns "0" always would survive this.
        assertEquals(0, new MathTrap().add(0, 0));
    }
}
