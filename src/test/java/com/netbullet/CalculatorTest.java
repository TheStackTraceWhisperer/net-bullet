package com.netbullet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Calculator class.
 */
@DisplayName("Calculator Unit Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Addition of two positive numbers")
    void testAddPositiveNumbers() {
        int result = calculator.add(5, 3);
        assertEquals(8, result, "5 + 3 should equal 8");
    }

    @Test
    @DisplayName("Addition of positive and negative numbers")
    void testAddMixedNumbers() {
        int result = calculator.add(10, -5);
        assertEquals(5, result, "10 + (-5) should equal 5");
    }

    @Test
    @DisplayName("Subtraction of two numbers")
    void testSubtract() {
        int result = calculator.subtract(10, 4);
        assertEquals(6, result, "10 - 4 should equal 6");
    }

    @Test
    @DisplayName("Multiplication of two numbers")
    void testMultiply() {
        int result = calculator.multiply(6, 7);
        assertEquals(42, result, "6 * 7 should equal 42");
    }

    @Test
    @DisplayName("Division of two numbers")
    void testDivide() {
        int result = calculator.divide(15, 3);
        assertEquals(5, result, "15 / 3 should equal 5");
    }

    @Test
    @DisplayName("Division by zero throws ArithmeticException")
    void testDivideByZero() {
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    @DisplayName("Multiplication by zero")
    void testMultiplyByZero() {
        int result = calculator.multiply(5, 0);
        assertEquals(0, result, "5 * 0 should equal 0");
    }
}
