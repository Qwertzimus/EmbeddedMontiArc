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

/**
 * Created by Sining on 2017/3/5.
 */
public class SourceTargetNumberMatch implements EmbeddedMontiArcASTConnectorCoCo {
    @Override
    public void check(ASTConnector node){
        int sourceNum = 0, targetNum = 0;

        sourceNum = getSourceNum(node);

        for (ASTQualifiedNameWithArray target : node.getTargets()) {

            targetNum = getTargetNum(target);

            if (sourceNum != targetNum){
                Log.error("0xJK901 source port number "+ sourceNum +" and target port number "+ targetNum + " don't match");
            }
        }
    }

    private int getSourceNum(ASTConnector node){
        int sourceNum = 0, sourceComp = 0, sourcePort = 0;
        if (node.getSource().isPresent()) {
            if (node.getSource().get().getCompArray().isPresent()){
                if (node.getSource().get().getCompArray().get().getLowerbound().isPresent())
                    sourceComp = node.getSource().get().getCompArray().get().getUpperbound().get().getNumber().get().intValue() - node.getSource().get().getCompArray().get().getLowerbound().get().getNumber().get().intValue() + 1;
            }else sourceComp = 1;
            if (node.getSource().get().getPortArray().isPresent()){
                if (node.getSource().get().getPortArray().get().getLowerbound().isPresent())
                    sourcePort = node.getSource().get().getPortArray().get().getUpperbound().get().getNumber().get().intValue() - node.getSource().get().getPortArray().get().getLowerbound().get().getNumber().get().intValue() + 1;
            }else sourcePort = 1;
            sourceNum = sourceComp * sourcePort;
        }
        return sourceNum;
    }

    private int getTargetNum(ASTQualifiedNameWithArray target){
        int targetNum = 0, targetComp = 0, targetPort = 0;
        if (target.getCompArray().isPresent()){
            if (target.getCompArray().get().getLowerbound().isPresent())
                targetComp = target.getCompArray().get().getUpperbound().get().getNumber().get().intValue() - target.getCompArray().get().getLowerbound().get().getNumber().get().intValue() + 1;
        }else targetComp = 1;
        if (target.getPortArray().isPresent()){
            if (target.getPortArray().get().getLowerbound().isPresent())
                targetPort = target.getPortArray().get().getUpperbound().get().getNumber().get().intValue() - target.getPortArray().get().getLowerbound().get().getNumber().get().intValue() + 1;
        }else targetPort = 1;
        targetNum = targetComp * targetPort;
        return targetNum;
    }
}
