/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponentHead;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentHeadCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Crispin Kirchner
 */
public class ParameterNamesUnique implements EmbeddedMontiArcASTComponentHeadCoCo {

  /**
   * @see EmbeddedMontiArcASTComponentHeadCoCo#check(ASTComponentHead)
   */
  @Override
  public void check(ASTComponentHead node) {
    List<ASTParameter> parameters = node.getParameters();

    List<String> parameterNames = new ArrayList<>();
    for (ASTParameter parameter : parameters) {

      if (parameterNames.contains(parameter.getName())) {
        Log.error(String.format("0xC4A61 Parameter name \"%s\" not unique", parameter.getName()),
            parameter.get_SourcePositionStart());
      }

      else {
        parameterNames.add(parameter.getName());
      }
    }
  }

}
