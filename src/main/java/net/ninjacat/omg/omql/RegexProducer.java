package net.ninjacat.omg.omql;

import net.ninjacat.omg.conditions.Conditions;
import net.ninjacat.omg.errors.OmqlParsingException;
import net.ninjacat.omg.omql.parser.OmqlParser;

public class RegexProducer implements OmqlConditionProducer<OmqlParser.ConditionContext> {
    @Override
    public void create(final Conditions.LogicalConditionBuilder builder,
                       final String property,
                       final TypeValidator validator,
                       final OmqlParser.ConditionContext value) {
        final Object converted = validator.validate(property, value.literal_value().getText());
        if (converted instanceof String) {
            builder.property(property).regex((String) converted);
        } else {
            throw new OmqlParsingException("Regex operation is only supported for strings");
        }
    }
}
