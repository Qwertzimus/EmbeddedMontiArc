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
package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.unit.constant.EMAConstantValue;
import de.monticore.lang.embeddedmontiarc.tagging.MiddlewareSymbol;
import de.monticore.lang.monticar.ts.MCASTTypeSymbol;
import de.monticore.lang.monticar.ts.references.MCTypeReference;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;


public class EMAPortBuilder {
    protected Optional<Boolean> incoming = Optional.empty();
    protected Optional<String> name = Optional.empty();
    protected Optional<MCTypeReference> typeReference = Optional.empty();
    protected Optional<EMAConstantValue> constantValue = Optional.empty();
    protected Optional<ASTNode> astNode = Optional.empty();
    protected Optional<MiddlewareSymbol> middlewareSymbol = Optional.empty();

    public static PortSymbol clone(PortSymbol port) {
        if (port.isConstant())
            return new EMAPortBuilder().setName(port.getName()).setDirection(port.isIncoming()).
                    setTypeReference(port.getTypeReference()).setConstantValue(((ConstantPortSymbol) port).getConstantValue()).setASTNode(port.getAstNode())
                    .buildConstantPort();
        else {
            if(port.getNameWithoutArrayBracketPart().equals("degree")){
                System.out.println("info:"+((MCASTTypeSymbol)port.getTypeReference().getReferencedSymbol()).getAstType().toString());
            }
            return new EMAPortBuilder().setName(port.getName()).setDirection(port.isIncoming())
                    .setTypeReference(port.getTypeReference()).setASTNode(port.getAstNode()).setMiddlewareSymbol(port.getMiddlewareSymbol()).build();
        }
    }

    public EMAPortBuilder setDirection(boolean incoming) {
        this.incoming = Optional.of(Boolean.valueOf(incoming));
        return this;
    }

    public EMAPortBuilder setConstantValue(EMAConstantValue constantValue) {
        this.constantValue = Optional.of(constantValue);
        return this;
    }

    public EMAPortBuilder setName(String name) {
        this.name = Optional.of(name);
        return this;
    }

    public EMAPortBuilder setASTNode(Optional<ASTNode> astNode) {
        this.astNode = astNode;
        return this;
    }
    public EMAPortBuilder setMiddlewareSymbol(Optional<MiddlewareSymbol> middlewareSymbol){
        this.middlewareSymbol = middlewareSymbol;
        return this;
    }


    public EMAPortBuilder setTypeReference(MCTypeReference typeReference) {
        this.typeReference = Optional.of(typeReference);
        return this;
    }

    public PortSymbol build() {
        if (name.isPresent() && incoming.isPresent() && typeReference.isPresent()) {
            PortSymbol p = new PortSymbol(this.name.get());
            p.setDirection(this.incoming.get());
            p.setTypeReference(this.typeReference.get());
            if (astNode.isPresent())
                p.setAstNode(astNode.get());
            if(middlewareSymbol.isPresent())
                p.setMiddlewareSymbol(middlewareSymbol.get());
            return p;
        }
        Log.error("not all parameters have been set before to build the port symbol");
        throw new Error("not all parameters have been set before to build the port symbol");
    }

    public ConstantPortSymbol buildConstantPort() {
        if (typeReference == null) {
            Log.error("not all parameters have been set before to build the port symbol");
            throw new Error("not all parameters have been set before to build the port symbol");
        }
        ConstantPortSymbol p = new ConstantPortSymbol(name.get());
        p.setDirection(this.incoming.get());
        p.setTypeReference(typeReference.get());
        p.setConstantValue(constantValue.get());
        if (astNode.isPresent())
            p.setAstNode(astNode.get());
        if(middlewareSymbol.isPresent())
            p.setMiddlewareSymbol(middlewareSymbol.get());
        return p;
    }
}
