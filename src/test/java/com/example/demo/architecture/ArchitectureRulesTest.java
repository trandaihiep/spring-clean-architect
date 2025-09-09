package com.example.demo.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class ArchitectureRulesTest {

    @Test
    void domainDoesNotDependOnSpring() {
        var classes = new ClassFileImporter().importPackages("com.example.demo.domain");
        ArchRuleDefinition.noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .check(classes);
    }
}

