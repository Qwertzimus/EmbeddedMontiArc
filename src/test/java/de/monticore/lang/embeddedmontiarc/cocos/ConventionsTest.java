/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

/**
 *
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Haber
 * @date 08.02.2010
 */
public class ConventionsTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Ignore("implement coco")
  @Test
  public void testComponentConventions()
      throws RecognitionException, IOException {
    // runChecker("arc/coco/conventions/conv/violatesComponentNaming.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
    // runChecker("arc/coco/conventions/conv/InnerViolatesComponentNaming.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testReferenceConventions() {
    runCheckerWithSymTab("orgarc/arc/coco/conventions", "conv.ReferencesViolateNamingConventions");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testPortConvention() {
    // runChecker("arc/coco/conventions/conv/PortViolatesNamingConventions.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testImportConvention() {
    // runChecker("arc/coco/conventions/conv/UnuniqueImports.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testWrongConnector() {
    // runChecker("arc/coco/conventions/conv/WrongConnector.arc");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testConnectorSourceAndTargetDifferentComponent() {
    // runChecker("arc/coco/conventions/conv/ConnectorSourceAndTargetSameComponent.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testMissingSourceAndTargetDefinition() {
    runCheckerWithSymTab("orgarc/arc/coco/conventions", "conv.MissingSourceTargetDefinition");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore
  @Test
  public void testUnusedPorts() {
    runCheckerWithSymTab("orgarc/arc/coco/conventions", "conv.UnusedPorts");
    Collection<String> findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC006") || s.contains("xAC007"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());

    findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC008") || s.contains("xAC009"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());
  }

  @Ignore("implement coco")
  @Test
  public void testOuterComponentWithInstanceName() {
    // runChecker("arc/coco/conventions/conv/OuterComponentWithInstanceName.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

}
