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

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTConnector;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTConnectorCoCo;
import de.monticore.lang.monticar.common2._ast.ASTQualifiedNameWithArray;
import de.se_rwth.commons.logging.Log;

import java.util.function.Predicate;

/**
 * @author Crispin Kirchner
 * 
 * Implementation of CO1 and CO2
 */
public class ConnectorEndPointCorrectlyQualified
    implements EmbeddedMontiArcASTConnectorCoCo {
  
  private void checkEndpointCorrectlyQualified(ASTQualifiedNameWithArray name,
      Predicate<Integer> predicate, String errorMessage) {
    int i = 0;
    if (name.getCompName() != null)
      ++i;
    if (name.getPortName() != null)
      ++i;
    //if (!predicate.test(name.getParts().size())) {
    if (!predicate.test(i)){
      Log.error(String.format(errorMessage, name.toString()), name.get_SourcePositionStart());
    }
  }
  
  /**
   * Ensure that the connector endpoint is of the form `rootComponentPort' or `subComponent.port'
   */
  private void checkEndPointMaximallyTwiceQualified(ASTQualifiedNameWithArray name) {
    checkEndpointCorrectlyQualified(name, i -> i <= 2 && i > 0,
        "0xDB61C Connector endVal point \"%s\" must only consist of an optional component name and a port name");
  }
  
  /**
   * @see EmbeddedMontiArcASTConnectorCoCo#check(ASTConnector)
   */
  @Override
  public void check(ASTConnector node) {
    if(node.getSource().isPresent()) {
      checkEndPointMaximallyTwiceQualified(node.getSource().get());
    }else{
      Log.error("Error Connector has no valid source or constant");
    }

    for (ASTQualifiedNameWithArray name : node.getTargets()) {
      checkEndPointMaximallyTwiceQualified(name);
    }
  }
  
}
