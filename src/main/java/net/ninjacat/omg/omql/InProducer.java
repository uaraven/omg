/*
 * omg: InProducer.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.omql.parser.OmqlParser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Producer for SQL condition IN (list)
 */
public class InProducer implements OmqlConditionProducer<OmqlParser.InExprContext> {

    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final QueryContext context,
                       final OmqlParser.InExprContext value) {
        final List<Object> values = value.list().literal_value().stream()
                .map(it -> context.validator().validate(property, it.getText()))
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            builder.property(property).in(Collections.emptyList());
        } else {
            final Class<?> firstClass = values.get(0).getClass();
            if (!values.stream().allMatch(it -> firstClass.isAssignableFrom(it.getClass()))) {
                throw new OmqlParsingException("All elements of IN operation should be of same type");
            }
            final List<Object> items = values.stream().map(firstClass::cast).collect(Collectors.toList());
            builder.property(property).in(items);
        }
    }
}
