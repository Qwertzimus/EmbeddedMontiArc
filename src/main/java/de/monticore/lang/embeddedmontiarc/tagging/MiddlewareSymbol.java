package de.monticore.lang.embeddedmontiarc.tagging;

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;

public abstract class MiddlewareSymbol extends TagSymbol {
    public MiddlewareSymbol(TagKind kind, Object... values) {
        super(kind, values);
    }
}
