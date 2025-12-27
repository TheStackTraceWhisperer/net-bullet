package com.netbullet.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture tests to enforce structural rules and best practices.
 * These tests prevent architectural violations at build time.
 */
class ArchitectureTest {

    private static final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.netbullet");

    @Test
    void classesShouldNotUseJavaUtilLogging() {
        // Enforce use of a proper logging framework (e.g., SLF4J) instead of java.util.logging
        ArchRule rule = noClasses()
                .should().dependOnClassesThat().resideInAPackage("java.util.logging..");

        rule.check(importedClasses);
    }

    @Test
    void classesShouldNotDependOnTestPackages() {
        // Production code should not depend on test packages
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackages("..test..", "..architecture..")
                .should().dependOnClassesThat().resideInAPackage("..test..")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    @Test
    void testClassesShouldNotBeInProductionCode() {
        // Ensure test classes are properly separated from production code
        // This rule allows flexibility in the current simple structure
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("IT")
                .should().resideInAPackage("..main..")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }
}
