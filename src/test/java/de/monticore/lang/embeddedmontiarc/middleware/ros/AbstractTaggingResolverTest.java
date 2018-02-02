package de.monticore.lang.embeddedmontiarc.middleware.ros;

import de.monticore.lang.embeddedmontiarc.AbstractSymtabTest;
import de.monticore.lang.embeddedmontiarc.tagging.RosToEmamTagSchema;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;

import java.util.Arrays;

public class AbstractTaggingResolverTest extends AbstractSymtabTest{

    protected static TaggingResolver createSymTabAndTaggingResolver(String... modelPath) {
        Scope scope = createSymTab(modelPath);
        TaggingResolver tagging = new TaggingResolver(scope, Arrays.asList(modelPath));
        RosToEmamTagSchema.registerTagTypes(tagging);
        return tagging;
    }

}
