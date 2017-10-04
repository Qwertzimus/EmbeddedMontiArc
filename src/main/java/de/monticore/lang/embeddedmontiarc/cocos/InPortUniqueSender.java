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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.*;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTComponentCoCo;
import de.monticore.lang.monticar.common2._ast.ASTQualifiedNameWithArray;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements R1 and R2
 * 
 * @author Crispin Kirchner
 */
public class InPortUniqueSender implements EmbeddedMontiArcASTComponentCoCo {
  
  /**
   * @see EmbeddedMontiArcASTComponentCoCo#check(ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    InPortUniqueSenderCheck check = new InPortUniqueSenderCheck(node);
    check.check();
  }
  
  private class InPortUniqueSenderCheck {
    private List<String> connectorTargets = new ArrayList<>();
    
    private ASTComponent node;
    
    public InPortUniqueSenderCheck(ASTComponent node) {
      this.node = node;
    }
    
    public void check() {
      checkConnectors();
      //checkSimpleConnectors();
    }
    
    private void checkTarget(ASTQualifiedNameWithArray target) {
      String targetString = target.toString();
      
      if (connectorTargets.contains(targetString)) {
        Log.error(String.format("0x2BD7E target port \"%s\" already in use.", target.toString()),
            target.get_SourcePositionStart());
      }
      else {
        connectorTargets.add(targetString);
      }
    }
    
    private void checkConnectors() {
      for (ASTConnector connector : node.getConnectors()) {
        for (ASTQualifiedNameWithArray target : connector.getTargets()) {
          checkTarget(target);
        }
      }
    }
    
    /*private void checkSimpleConnectors() {
      for (ASTSubComponent subComponent : node.getSubComponents()) {
        for (ASTSubComponentInstance instance : subComponent.getInstances()) {
          for (ASTSimpleConnector connector : instance.getConnectors()) {
            for (ASTQualifiedName target : connector.getTargets()) {
              checkTarget(target);
            }
          }
        }
      }
    }*/
  }
  
}
