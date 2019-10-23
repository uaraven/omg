/*
 * omg: ConditionsJsonTest.java
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

package net.ninjacat.omg.conditions;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConditionsJsonTest {

    @Test
    public void shouldSerializeAndDeserializeSimpleEqCondition() throws IOException {
        final Condition condition = Conditions.matcher().property("name").eq("Bruce").build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleNeqCondition() throws IOException {
        final Condition condition = Conditions.matcher().property("name").neq("Bruce").build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleGtCondition() throws IOException {
        final Condition condition = Conditions.matcher().property("value").gt(10).build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleLtCondition() throws IOException {
        final Condition condition = Conditions.matcher().property("value").lt(10).build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleInCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .property("value")
                .in(io.vavr.collection.HashSet.of(1, 2, 3, 4).toJavaSet())
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond.repr(), is(condition.repr()));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleRegexCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .property("name")
                .regex(".*")
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond.repr(), is(condition.repr()));
    }

    @Test
    public void shouldSerializeAndDeserializeSimpleMatchCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .property("name")
                .match(Conditions.matcher().property("a").eq("b").build())
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond.repr(), is(condition.repr()));
    }

    @Test
    public void shouldSerializeAndDeserializeAndCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .property("name").neq("Bruce")
                .property("value").gt(200)
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

    @Test
    public void shouldSerializeAndDeserializeOrCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .or(o -> o
                        .property("name").neq("Bruce")
                        .property("value").gt(200)
                )
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }


    @Test
    public void shouldSerializeAndDeserializeNotCondition() throws IOException {
        final Condition condition = Conditions.matcher()
                .not(o -> o.property("value").gt(200))
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }


    @Test
    public void shouldSerializeMultipleLevelNested() throws IOException {
        final Condition condition = Conditions.matcher()
                .or(or -> or
                        .property("name").regex("A.*")
                        .property("name").regex(".*z"))
                .and(and -> and
                        .property("value").in(io.vavr.collection.List.of(1, 2, 3).asJava())
                        .not(o -> o.property("value").gt(200)))
                .build();
        final String jsonCondition = Conditions.asJson(condition);
        final Condition newCond = Conditions.fromJson(jsonCondition);

        assertThat(newCond, is(condition));
    }

}
