package net.ninjacat.objmatcher.matcher.reflect;

import io.vavr.control.Try;
import lombok.Value;
import net.jcip.annotations.Immutable;
import net.ninjacat.objmatcher.matcher.TypeConverter;
import net.ninjacat.objmatcher.matcher.errors.MatcherException;
import net.ninjacat.objmatcher.matcher.matchers.Matcher;
import net.ninjacat.objmatcher.matcher.matchers.TypedMatcher;

import java.lang.reflect.Method;

@Value
@Immutable
public class PropertyMatcher<O, T> implements Matcher<O> {
    Property property;
    TypedMatcher<T> matcher;
    TypeConverter typeConverter;

    public PropertyMatcher(final Property property, final TypedMatcher<T> matcher) {
        this(property, matcher, new DefaultTypeConverter());
    }

    public PropertyMatcher(final Property property, final TypedMatcher<T> matcher, final TypeConverter typeConverter) {
        this.property = property;
        this.matcher = matcher;
        this.typeConverter = typeConverter;
    }

    @Override
    public boolean matches(final O object) {
        final Method method = property.getGetterMethod();
        final Object propertyValue = Try
                .of(() -> method.invoke(object))
                .getOrElseThrow((ex) -> new MatcherException(ex, "Failed to get value of property '%s", property.getPropertyName()));

        final Object valueToMatch = ensureValueType(propertyValue, matcher.getExpectedType());
        return getMatcher().matches((T) valueToMatch);
    }

    private Object ensureValueType(final Object value, final Class fieldType) {
        final Class valueType = value.getClass();
        final Class widenedFieldType = TypeWidener.widen(fieldType);
        if (valueType.equals(fieldType)) {
            return value;
        } else {
            return typeConverter.convert(value).to(widenedFieldType);
        }
    }


    @Override
    public String toString() {
        return property.getPropertyName() + " " + matcher;
    }
}
