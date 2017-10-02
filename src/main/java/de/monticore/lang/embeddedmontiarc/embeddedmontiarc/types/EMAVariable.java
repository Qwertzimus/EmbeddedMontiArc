package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types;

import de.monticore.lang.monticar.common2._ast.ASTParameter;
import de.monticore.lang.monticar.types2._ast.ASTType;

/**
 * @author Sascha Schneiders
 */
public class EMAVariable {
    protected ASTType type;
    protected String name;

    public EMAVariable(){

    }
    public EMAVariable(ASTType type, String name) {
        this.type = type;
        this.name = name;
    }

    public ASTType getType() {
        return type;
    }

    public void setType(ASTType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
