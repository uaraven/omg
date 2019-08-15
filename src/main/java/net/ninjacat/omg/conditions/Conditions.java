package net.ninjacat.omg.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Conditions {

    private Conditions() {
    }

    /**
     * Starts building a condition
     *
     * @return {@link ConditionBuilder}
     */
    public static LogicalConditionBuilder matcher() {
        return new AndConditionBuilder(null);
    }

    @FunctionalInterface
    public interface ConditionBuilder {
        Condition build();
    }

    public abstract static class LogicalConditionBuilder implements ConditionBuilder {
        private final List<ConditionBuilder> conditions;

        private LogicalConditionBuilder() {
            this.conditions = new ArrayList<>();
        }

        public PropertyBuilder property(final String propertyName) {
            return new PropertyBuilder(propertyName, this);
        }

        void addCondition(final ConditionBuilder builder) {
            conditions.add(builder);
        }

        public LogicalConditionBuilder and(final Consumer<LogicalConditionBuilder> nested) {
            final LogicalConditionBuilder builder = new AndConditionBuilder(this);
            nested.accept(builder);
            addCondition(builder);
            return this;
        }

        public LogicalConditionBuilder or(final Consumer<LogicalConditionBuilder> nested) {
            final LogicalConditionBuilder builder = new OrConditionBuilder(this);
            nested.accept(builder);
            addCondition(builder);
            return this;
        }

        public NotConditionBuilder not() {

        }

        public List<ConditionBuilder> getConditions() {
            return io.vavr.collection.List.ofAll(conditions).asJava();
        }
    }

    public static class PropertyBuilder {
        private final String propertyName;
        private final LogicalConditionBuilder parentBuilder;

        PropertyBuilder(final String propertyName, final LogicalConditionBuilder parentBuilder) {
            this.propertyName = propertyName;
            this.parentBuilder = parentBuilder;
        }

        public <T> LogicalConditionBuilder eq(final T value) {
            parentBuilder.addCondition(() -> new EqCondition<>(propertyName, value));
            return parentBuilder;
        }

        public <T> LogicalConditionBuilder neq(final T value) {
            parentBuilder.addCondition(() -> new NeqCondition<>(propertyName, value));
            return parentBuilder;
        }

        public <T> LogicalConditionBuilder gt(final T value) {
            parentBuilder.addCondition(() -> new GtCondition<>(propertyName, value));
            return parentBuilder;
        }

        public <T> LogicalConditionBuilder lt(final T value) {
            parentBuilder.addCondition(() -> new LtCondition<>(propertyName, value));
            return parentBuilder;
        }

        public <T> LogicalConditionBuilder in(final List<T> value) {
            parentBuilder.addCondition(() -> new InCondition<>(propertyName, value));
            return parentBuilder;
        }

        public LogicalConditionBuilder regex(final String value) {
            parentBuilder.addCondition(() -> new RegexCondition(propertyName, value));
            return parentBuilder;
        }

        public LogicalConditionBuilder match(final Consumer<LogicalConditionBuilder> nested) {
            final ObjectConditionBuilder builder = new ObjectConditionBuilder(propertyName);
            nested.accept(builder);
            parentBuilder.addCondition(builder);
            return parentBuilder;
        }

        public LogicalConditionBuilder match(final Condition nested) {
            final ObjectConditionBuilder builder = new ObjectConditionBuilder(propertyName);
            builder.addCondition(() -> nested);
            parentBuilder.addCondition(builder);
            return parentBuilder;
        }
    }

    public static class AndConditionBuilder extends LogicalConditionBuilder {
        AndConditionBuilder(final LogicalConditionBuilder parentBuilder) {
            super();
        }

        @Override
        public Condition build() {
            if (getConditions().size() == 1) {
                return getConditions().get(0).build();
            } else {
                return ImmutableAndCondition.builder()
                        .children((getConditions().stream().map(ConditionBuilder::build).collect(Collectors.toList())))
                        .build();
            }
        }
    }

    public static class ObjectConditionBuilder extends LogicalConditionBuilder {
        private final String propertyName;

        ObjectConditionBuilder(final String propertyName) {
            super();
            this.propertyName = propertyName;
        }

        @Override
        public Condition build() {
            final Condition condition = getConditions().size() == 1
                    ? getConditions().get(0).build()
                    : ImmutableAndCondition.builder()
                    .children(getConditions().stream().map(ConditionBuilder::build).collect(Collectors.toList()))
                    .build();

            return new ObjectCondition(propertyName, condition);
        }
    }

    public static final class OrConditionBuilder extends LogicalConditionBuilder {
        OrConditionBuilder(final LogicalConditionBuilder parentBuilder) {
            super();
        }

        @Override
        public Condition build() {
            if (getConditions().size() == 1) {
                return getConditions().get(0).build();
            } else {
                return ImmutableOrCondition.builder()
                        .children(getConditions().stream().map(ConditionBuilder::build).collect(Collectors.toList()))
                        .build();
            }
        }
    }
}
