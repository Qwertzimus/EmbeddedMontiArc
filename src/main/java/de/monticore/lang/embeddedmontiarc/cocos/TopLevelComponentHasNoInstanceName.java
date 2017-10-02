/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * @author Crispin Kirchner
 */
public class TopLevelComponentHasNoInstanceName
    implements EmbeddedMontiArcASTComponentCoCo {

  /**
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.symbolIsPresent()) {
      Log.error(String.format(
          "0xE51E8 Symbol of component \"%s\" is missing. " +
              "The context condition \"%s\" can't be checked that way.",
          node.getName(), TopLevelComponentHasNoInstanceName.class.getName()));
    }

    ComponentSymbol symbol = (ComponentSymbol) node.getSymbol().get();
    if (!symbol.isInnerComponent() && node.instanceNameIsPresent()) {
      Log.error(
          String.format("0x3F207 Top level component \"%s\" has an instance name", node.getName()),
          node.get_SourcePositionStart());
    }
  }

}
