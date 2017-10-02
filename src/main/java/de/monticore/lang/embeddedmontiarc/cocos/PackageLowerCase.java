/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTEMACompilationUnit;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTEMACompilationUnitCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcModelNameCalculator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures, that packages of components are lower-case. This is required for inner components, see
 * {@link EmbeddedMontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class PackageLowerCase implements EmbeddedMontiArcASTEMACompilationUnitCoCo {

  /**
   * @see EmbeddedMontiArcASTEMACompilationUnitCoCo#check(ASTEMACompilationUnit)
   */
  @Override
  public void check(ASTEMACompilationUnit node) {
    String pack = Names.getQualifiedName(node.getPackage());
    if (pack.toUpperCase().equals(pack)) {
      Log.error("0xAC003 The package must be lower case", node.get_SourcePositionStart());
    }
  }

}
