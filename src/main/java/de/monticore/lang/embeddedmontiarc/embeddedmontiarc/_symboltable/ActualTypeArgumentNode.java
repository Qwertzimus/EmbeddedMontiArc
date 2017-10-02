package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * @author Sascha Schneiders
 */
public class ActualTypeArgumentNode extends ActualTypeArgument {
    ASTNode node;

    public ActualTypeArgumentNode(boolean isLowerBound, boolean isUpperBound, TypeReference<? extends TypeSymbol> type) {
        super(isLowerBound, isUpperBound, type);
    }

    public ActualTypeArgumentNode(TypeReference<? extends TypeSymbol> type, ASTNode node) {
        super(type);
    }

    public ASTNode getNode() {
        return node;
    }

    public void setNode(ASTNode node) {
        this.node = node;
    }
}
