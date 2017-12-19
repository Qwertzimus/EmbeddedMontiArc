/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Ensures that parameters in the component's head are defined in the right order.
 * It is not allowed to define a normal parameter after a declaration of a default parameter.
 * E.g.: Wrong: A[int x = 5, int y]
 * Right: B[int x, int y = 5]
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
 */
public class DefaultParametersHaveCorrectOrder
    implements EmbeddedMontiArcASTComponentCoCo {


  @Override
  public void check(ASTComponent node ) {
    List<ASTParameter> params = node.getParameters();
    boolean foundDefaultParameter = false;
    for (ASTParameter param : params) {

      if (!foundDefaultParameter) {
        foundDefaultParameter = param.getDefaultValue().isPresent();
      }
      else {
        if (foundDefaultParameter && !param.getDefaultValue().isPresent()) {
          Log.error("0xAC005 There are non default parameters after a default parameter",
              node.get_SourcePositionStart());
        }
      }
    }

  }

}
