package com.pivovarit.collectors;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaModifier.FINAL;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_JARS;
import static com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ArchitectureTest {

    private static final JavaClasses classes = new ClassFileImporter()
      .withImportOption(DO_NOT_INCLUDE_TESTS)
      .withImportOption(DO_NOT_INCLUDE_JARS)
      .withImportOption(DO_NOT_INCLUDE_ARCHIVES)
      .importPackages("com.pivovarit");

    @Test
    void shouldHaveSingleFacade() {
        classes()
          .that().arePublic()
          .should().haveSimpleName("ParallelCollectors").orShould().haveSimpleName("Batching")
          .andShould().haveOnlyFinalFields()
          .andShould().haveOnlyPrivateConstructors()
          .andShould().haveModifier(FINAL)
          .as("all public factory methods should be accessible from the ParallelCollectors and ParallelCollectors.Batching classes")
          .because("users of ParallelCollectors should have a single entry point")
          .check(classes);
    }

    @Test
    void shouldHaveBatchingClassesInsideParallelCollectors() {
        classes()
          .that().arePublic().and().haveSimpleName("Batching")
          .should().beNestedClasses()
          .as("all Batching classes are sub namespaces of ParallelCollectors")
          .check(classes);
    }

    @Test
    void shouldHaveZeroDependencies() {
        classes()
          .that().resideInAPackage("com.pivovarit.collectors")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage("com.pivovarit.collectors", "java..")
          .as("the library should depend only on core Java classes")
          .because("users appreciate not experiencing a dependency hell")
          .check(classes);
    }

    @Test
    void shouldHaveSinglePackage() {
        classes()
          .should().resideInAPackage("com.pivovarit.collectors")
          .check(classes);
    }

    @Test
    void shouldHaveTwoPublicClasses() {
        classes()
          .that().haveSimpleName("ParallelCollectors").or().haveSimpleName("Batching")
          .should().bePublic().andShould().haveModifier(FINAL)
          .check(classes);
    }
}
