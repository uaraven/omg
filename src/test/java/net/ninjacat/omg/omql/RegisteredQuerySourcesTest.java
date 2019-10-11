package net.ninjacat.omg.omql;

import io.vavr.collection.List;
import net.ninjacat.omg.errors.OmqlSecurityException;
import org.hamcrest.MatcherAssert;
import org.immutables.value.Value;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class RegisteredQuerySourcesTest {

    @Test
    public void shouldRegisterDependentClasses() {
        final java.util.List<Class<?>> classes = List.<Class<?>>of(RegisteredClass.class).asJava();
        final RegisteredQuerySources sources = new RegisteredQuerySources(classes, true);

        MatcherAssert.assertThat(sources.getSource("FieldClass"), is(notNullValue()));
    }

    @Test(expected = OmqlSecurityException.class)
    public void shouldNotRegisterDependentClasses() {
        final java.util.List<Class<?>> classes = List.<Class<?>>of(RegisteredClass.class).asJava();
        final RegisteredQuerySources sources = new RegisteredQuerySources(classes, false);

        sources.getSource("FieldClass");
    }

    @Test(expected = OmqlSecurityException.class)
    public void shouldNotFindUnregisteredClasses() {
        final java.util.List<Class<?>> classes = List.<Class<?>>of(RegisteredClass.class).asJava();
        final RegisteredQuerySources sources = new RegisteredQuerySources(classes, false);

        sources.getSource(UnregisteredClass.class.getName());
    }

    @Value.Immutable
    public interface FieldClass {
        String name();
    }

    @Value.Immutable
    public interface RegisteredClass {
        FieldClass field();
    }

    @Value.Immutable
    public interface UnregisteredClass {
        FieldClass field();
    }
}