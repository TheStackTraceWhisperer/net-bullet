package com.netbullet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Calculator class. Tests complex scenarios and
 * workflows.
 */
@DisplayName("Calculator Integration Tests")
class CalculatorIT {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Complex calculation workflow: (10 + 5) * 3 - 8 / 2")
    void testComplexCalculationWorkflow() {
        // (10 + 5) * 3 - 8 / 2 = 15 * 3 - 4 = 45 - 4 = 41
        int step1 = calculator.add(10, 5); // 15
        int step2 = calculator.multiply(step1, 3); // 45
        int step3 = calculator.divide(8, 2); // 4
        int result = calculator.subtract(step2, step3); // 41

        assertEquals(41, result, "Complex calculation should equal 41");
    }

    @Test
    @DisplayName("Chain of operations with negative numbers")
    void testChainWithNegativeNumbers() {
        // Start with 100, subtract 25, multiply by 2, add 50
        int result = calculator.subtract(100, 25); // 75
        result = calculator.multiply(result, 2); // 150
        result = calculator.add(result, 50); // 200

        assertEquals(200, result, "Chain calculation should equal 200");
    }

    @Test
    @DisplayName("Multiple division operations")
    void testMultipleDivisions() {
        // 100 / 5 / 2 = 10
        int result = calculator.divide(100, 5); // 20
        result = calculator.divide(result, 2); // 10

        assertEquals(10, result, "100 / 5 / 2 should equal 10");
    }

    @Test
    @DisplayName("Calculation with all operations")
    void testAllOperations() {
        // Test using all operations: ((20 + 10) * 2) / 6 - 5 = 5
        int sum = calculator.add(20, 10); // 30
        int product = calculator.multiply(sum, 2); // 60
        int quotient = calculator.divide(product, 6); // 10
        int result = calculator.subtract(quotient, 5); // 5

        assertEquals(5, result, "All operations should equal 5");
    }

    @Test
    @DisplayName("Error handling in workflow with division by zero")
    void testErrorHandlingInWorkflow() {
        // Perform calculations and then attempt division by zero
        int result = calculator.add(10, 5);
        result = calculator.multiply(result, 2);

        final int finalResult = result;
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(finalResult, 0);
        }, "Division by zero should throw ArithmeticException even in workflow");
    }
}
