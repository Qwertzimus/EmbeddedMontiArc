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
package de.monticore.lang.embeddedmontiarc;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc.StreamScanner;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ComponentSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.EmbeddedMontiArcLanguage;
import de.monticore.lang.monticar.streamunits._symboltable.ComponentStreamUnitsSymbol;
import de.monticore.lang.monticar.streamunits._symboltable.StreamUnitsLanguage;
import de.monticore.lang.monticar.struct._symboltable.StructLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StreamScannerTest {

    private static final Path BASE_PATH = Paths.get("src/test/resources");

    @Test
    public void testMySuperNiceComponent() {
        Scope symTab = createSymTab(BASE_PATH.toString());
        StreamScanner scanner = new StreamScanner(BASE_PATH, symTab);
        Map<ComponentSymbol, Set<ComponentStreamUnitsSymbol>> result = scanner.scan();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        ComponentSymbol mySuperNiceComponent = symTab.<ComponentSymbol>resolve("testing.MySuperNiceComponent", ComponentSymbol.KIND).orElse(null);
        Assert.assertNotNull(mySuperNiceComponent);
        Set<ComponentStreamUnitsSymbol> mySuperNiceStreams = result.get(mySuperNiceComponent);
        Assert.assertNotNull(mySuperNiceStreams);
        Assert.assertFalse(mySuperNiceStreams.isEmpty());
        Assert.assertEquals(2, mySuperNiceStreams.size());
        Iterator<ComponentStreamUnitsSymbol> it = mySuperNiceStreams.iterator();
        ComponentStreamUnitsSymbol mySuperNiceStream1 = it.next();
        ComponentStreamUnitsSymbol mySuperNiceStream2 = it.next();
        if (!"MySuperNiceStream1".equals(mySuperNiceStream1.getName())) {
            ComponentStreamUnitsSymbol swap = mySuperNiceStream1;
            mySuperNiceStream1 = mySuperNiceStream2;
            mySuperNiceStream2 = swap;
        }
        Assert.assertEquals("testing.MySuperNiceStream1", mySuperNiceStream1.getFullName());
        Assert.assertEquals("testing.MySuperNiceStream2", mySuperNiceStream2.getFullName());
        Assert.assertEquals(6, mySuperNiceStream1.getNamedStreams().size());
        Assert.assertEquals(6, mySuperNiceStream2.getNamedStreams().size());
    }

    private static Scope createSymTab(String... modelPath) {
        ModelingLanguageFamily fam = new ModelingLanguageFamily();
        fam.addModelingLanguage(new EmbeddedMontiArcLanguage());
        fam.addModelingLanguage(new StreamUnitsLanguage());
        fam.addModelingLanguage(new StructLanguage());
        final ModelPath mp = new ModelPath();
        for (String m : modelPath) {
            mp.addEntry(Paths.get(m));
        }
        GlobalScope scope = new GlobalScope(mp, fam);
        de.monticore.lang.monticar.Utils.addBuiltInTypes(scope);
        LogConfig.init();
        return scope;
    }
}
