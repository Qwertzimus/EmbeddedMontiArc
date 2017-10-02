/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcASTConnectorCoCo;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.*;

/**
 * Bundle of CoCos for the MontiArc language.
 *
 * @author Robert Heim
 */
public class EmbeddedMontiArcCoCos {
  public static EmbeddedMontiArcCoCoChecker createChecker() {
    return new EmbeddedMontiArcCoCoChecker()
        //.addCoCo(new UniqueConstraint())
        .addCoCo(new UniquePorts())
        .addCoCo(new ComponentInstanceNamesUnique())
        .addCoCo(new PortUsage())
        .addCoCo(new SubComponentsConnected())
        .addCoCo(new PackageLowerCase())
        .addCoCo(new ComponentCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new ParameterNamesUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((EmbeddedMontiArcASTConnectorCoCo) new ConnectorEndPointCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        .addCoCo(new ReferencedSubComponentExists())
        .addCoCo(new PortTypeOnlyBooleanOrSIUnit());
  }
}
