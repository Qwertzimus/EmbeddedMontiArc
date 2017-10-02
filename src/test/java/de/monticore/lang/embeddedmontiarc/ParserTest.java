/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._parser.EmbeddedMontiArcParser;
import de.monticore.lang.monticar.ranges._ast.ASTRange;
import de.monticore.lang.monticar.ranges._ast.ASTRanges;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sascha Schneiders, Sining Wang, Yu Qiao
 */
public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = false;
  private static List<String> expectedParseErrorModels = Arrays.asList(
      "")
      .stream().map(s -> Paths.get(s).toString())
      .collect(Collectors.toList());

  @Before
  public void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }

//  @Test
//  public void testArc() throws Exception {
//    test("arcn");
//    if (Log.getErrorCount() > 0) {
//      throw new Exception("Test Failed, found errors");
//    }
//  }

  @Test
  public void testEmbeddedMontiArc() throws Exception {
    test("ema");
    if (Log.getErrorCount() > 0) {
      throw new Exception("Test Failed, found errors");
    }
  }

  @Test
  public void testParsing1() throws IOException{
    EmbeddedMontiArcParser parser = new EmbeddedMontiArcParser();
    ASTRanges ast = parser.parseString_Ranges("[(0 km : 0.05 m : 10 km) (10 km : 0.1 m : 45 km)]").orElse(null);
    assertNotNull(ast);
  }

  //Works not but should as this works in the common project and comes from another grammar
  //see SIParser tests as comparison
  //TODO investigate this bug further
  //@Ignore
  @Test
  public void testParsing2() throws IOException{
    EmbeddedMontiArcParser parser = new EmbeddedMontiArcParser();
    ASTRanges ast = parser.parseString_Ranges("[(0 : 0.05 : 10) (10 : 0.1 : 45 km)]").orElse(null);
    assertNotNull(ast);
  }

  private void test(String fileEnding) throws IOException {
    ParseTest parserTest = new ParseTest("." + fileEnding);
    Files.walkFileTree(Paths.get("src/test/resources"), parserTest);

    if (!parserTest.getModelsInError().isEmpty()) {
      Log.debug("Models in error", "ParserTest");
      for (String model : parserTest.getModelsInError()) {
        Log.debug("  " + model, "ParserTest");
      }
    }
    Log.info("Count of tested models: " + parserTest.getTestCount(), "ParserTest");
    Log.info("Count of correctly parsed models: "
        + (parserTest.getTestCount() - parserTest.getModelsInError().size()), "ParserTest");

    assertTrue("There were models that could not be parsed", parserTest.getModelsInError()
        .isEmpty());
  }

  /**
   * Visits files of the given file ending and checks whether they are parsable.
   *
   * @author Robert Heim
   * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
   */
  private static class ParseTest extends SimpleFileVisitor<Path> {

    private String fileEnding;

    private List<String> modelsInError = new ArrayList<>();

    private int testCount = 0;

    public ParseTest(String fileEnding) {
      super();
      this.fileEnding = fileEnding;
    }

    /**
     * @return testCount
     */
    public int getTestCount() {
      return this.testCount;
    }

    /**
     * @return modelsInError
     */
    public List<String> getModelsInError() {
      return this.modelsInError;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      if (file.toFile().isFile()
          && (file.toString().toLowerCase().endsWith(fileEnding))) {

        Log.debug("Parsing file " + file.toString(), "ParserTest");
        testCount++;
        Optional<ASTEMACompilationUnit> maModel = Optional.empty();
        boolean expectingError = ParserTest.expectedParseErrorModels.contains(file.toString());

        EmbeddedMontiArcParser parser = new EmbeddedMontiArcParser();
        try {
          if (expectingError) {
            Log.enableFailQuick(false);
          }
          maModel = parser.parse(file.toString());
        }
        catch (Exception e) {
          if (!expectingError) {
            Log.error("Exception during test", e);
          }
        }
        if (!expectingError && (parser.hasErrors() || !maModel.isPresent())) {
          modelsInError.add(file.toString());
          Log.error("There were unexpected parser errors");
        }
        else {
          Log.getFindings().clear();
        }
        Log.enableFailQuick(ParserTest.ENABLE_FAIL_QUICK);
      }
      return FileVisitResult.CONTINUE;
    }
  }
}
