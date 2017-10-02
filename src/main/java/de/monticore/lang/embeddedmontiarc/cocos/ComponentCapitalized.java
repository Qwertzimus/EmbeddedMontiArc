/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcModelNameCalculator;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures, that component names startVal in upper-case. This is required for inner components, see
 * {@link EmbeddedMontiArcModelNameCalculator}.
 *
 * @author Robert Heim
 */
public class ComponentCapitalized implements EmbeddedMontiArcASTComponentCoCo {

  /**
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAC004 Component names must be startVal in upper-case",
          node.get_SourcePositionStart());
    }
  }
}
