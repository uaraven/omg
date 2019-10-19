/*
 * omg: ClassValidator.java
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

import net.ninjacat.omg.errors.TypeConversionException;
import net.ninjacat.omg.utils.Reflect;

import java.lang.reflect.Method;

import static net.ninjacat.omg.omql.OmqlTypeConversion.toJavaTypeStrict;

public class ClassValidator implements TypeValidator {

    private final Class<?> clazz;

    ClassValidator(final Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object validate(final String fieldName, final String value) throws TypeConversionException {
        final Class<?> propertyType = getReturnType(fieldName);

        return toJavaTypeStrict(propertyType, value);
    }

    @Override
    public boolean isObjectProperty(final String fieldName) {
        return !getReturnType(fieldName).isPrimitive();
    }

    @Override
    public Class<?> getReturnType(final String fieldName) {
        final Method callable = Reflect.getCallable(fieldName, clazz);
        return callable.getReturnType();
    }

}
