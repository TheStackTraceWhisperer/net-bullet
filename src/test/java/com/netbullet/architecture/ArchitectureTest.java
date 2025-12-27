package com.netbullet.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
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
        ArchRule rule = noClasses()
                .should().dependOnClassesThat().resideInAPackage("java.util.logging..");

        rule.check(importedClasses);
    }

    @Test
    void testClassesShouldResideInTestPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Test")
                .and().resideOutsideOfPackages("..architecture..")
                .should().resideInAnyPackage("..test..", "com.netbullet");

        rule.check(importedClasses);
    }

    @Test
    void publicClassesShouldHaveJavadoc() {
        // This is a placeholder for future enforcement
        // Currently just validates that classes can be analyzed
        ArchRule rule = classes()
                .that().arePublic()
                .should().haveSimpleNameNotEndingWith("Test")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }
}
