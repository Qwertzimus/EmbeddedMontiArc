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
package de.monticore.lang.embeddedmontiarc.tagging;

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;

import java.util.Optional;

public class RosConnectionSymbol extends MiddlewareSymbol {
    public static final RosConnectionKind KIND = RosConnectionKind.INSTANCE;

    public RosConnectionSymbol(String topicName, String topicType) {
        this(KIND, topicName, topicType);
    }

    public RosConnectionSymbol(RosConnectionKind kind, String topicName, String topicType) {
        super(kind, topicName, topicType, Optional.empty());
    }

    public RosConnectionSymbol(String topicName, String topicType, String msgField) {
        this(KIND, topicName, topicType, msgField);
    }

    protected RosConnectionSymbol(RosConnectionKind kind, String topicName, String topicType, String msgField) {
        super(kind, topicName, topicType, Optional.of(msgField));
    }

    @Override
    public String toString() {
        return String.format("RosConnection = (%s, %s), %s",
                getTopicName(), getTopicType(), getMsgField());
    }

    public String getTopicName() {
        return getValue(0);
    }

    public String getTopicType() {
        return getValue(1);
    }

    public Optional<String> getMsgField() {
        return getValue(2);
    }

    public static class RosConnectionKind extends TagKind {
        public static final RosConnectionKind INSTANCE = new RosConnectionKind();

        protected RosConnectionKind() {
        }
    }
}