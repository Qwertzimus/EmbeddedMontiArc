package de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable;

import de.monticore.lang.monticar.resolution._ast.ASTTypeArgument;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * @author Sascha Schneiders
 */
public class ActualTypeArgumentASTElement extends ActualTypeArgument {

    ASTTypeArgument astTypeArguments;

    public ActualTypeArgumentASTElement(boolean isLowerBound, boolean isUpperBound, TypeReference<? extends TypeSymbol> type) {
        super(isLowerBound, isUpperBound, type);
    }

    public ActualTypeArgumentASTElement(TypeReference<? extends TypeSymbol> type) {
        super(type);
    }

    public ASTTypeArgument getAstTypeArguments() {
        return astTypeArguments;
    }

    public ActualTypeArgumentASTElement setAstTypeArguments(ASTTypeArgument astTypeArguments) {
        this.astTypeArguments = astTypeArguments;
        return this;
    }
}
