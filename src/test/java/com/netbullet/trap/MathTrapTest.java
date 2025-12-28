package com.netbullet.trap;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

class MathTrapTest {

    @Property
    boolean additionCommutative(@ForAll int a, @ForAll int b) {
        return new MathTrap().add(a, b) == new MathTrap().add(b, a);
    }

    @Property
    boolean additionIdentity(@ForAll int a) {
        return new MathTrap().add(a, 0) == a;
    }
}
