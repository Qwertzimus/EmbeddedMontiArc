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
package de.monticore.lang.embeddedmontiarc

import de.monticore.ast.ASTNode

object StartSourcePositionComparator : Comparator<ASTNode> {
    override fun compare(o1: ASTNode, o2: ASTNode): Int {
        val l1 = o1._SourcePositionStart.line
        val l2 = o2._SourcePositionStart.line
        val lResult = l1.compareTo(l2)
        if (lResult != 0) {
            return lResult
        }
        val c1 = o1._SourcePositionStart.column
        val c2 = o2._SourcePositionStart.column
        return c1.compareTo(c2)
    }
}
