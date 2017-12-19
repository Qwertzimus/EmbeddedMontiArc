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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast;

//import de.monticore.common.common._ast.ASTStereotype;
//import de.monticore.types.types._ast.ASTTypeArguments;

import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.monticar.types2._ast.ASTReferenceType;
import de.monticore.lang.monticar.types2._ast.ASTTypeParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Write me!
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class ASTComponent extends ASTComponentTOP {
  /**
   * Constructor for de.monticore.lang.embeddedmontiarc.embeddedmontiarc._ast.ASTComponent
   */
  public ASTComponent() {
    super();
  }

  protected ASTComponent(
          ASTStereotype stereotype
          ,
          String name
          ,
          ASTTypeParameters genericTypeParameters
          ,
          List<ASTParameter> parameters
          ,
          ASTReferenceType superComponent
          ,
          ASTComponentBody body

  )  {
    super(stereotype, name, genericTypeParameters, parameters, superComponent, body);
  }

  // do not use symbol table, since symbol table must not be created
  public List<ASTPort> getPorts() {
    List<ASTPort> ret = new ArrayList<>();
    for (ASTElement element : this.getBody().getElements()) {
      if (element instanceof ASTInterface) {
        ret.addAll(((ASTInterface) element).getPorts());
      }
    }
    return ret;
  }

  // do not use symbol table, since symbol table must not be created
  public List<ASTConnector> getConnectors() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTConnector).
        map(a -> (ASTConnector) a).collect(Collectors.toList());
  }

  // do not use symbol table, since symbol table must not be created
  public List<ASTSubComponent> getSubComponents() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTSubComponent).
        map(a -> (ASTSubComponent) a).collect(Collectors.toList());
  }

  // do not use symbol table, since symbol table must not be created
  public List<ASTComponent> getInnerComponents() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTComponent).
        map(a -> (ASTComponent) a).collect(Collectors.toList());
  }
}
