/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.lang.embeddedmontiarc.cocos;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._cocos.EmbeddedMontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Sining on 2017/3/6.
 */
public class UniquePortsTest extends AbstractCoCoTest {
    @BeforeClass
    public static void setUp() {
        Log.enableFailQuick(false);
    }
    @Ignore
    @Test
    public void testValid() {
        checkValid("", "testing.UniqueName");
    }

    @Ignore
    @Test
    public void testInvalid() {
        checkInvalid(new EmbeddedMontiArcCoCoChecker().addCoCo(new SourceTargetNumberMatch()),
                getAstNode("", "testing.DuplicatePort"),
                new ExpectedErrorInfo());
    }
}
