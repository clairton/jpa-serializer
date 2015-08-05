package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;

abstract class AbstractSerializator<T> extends Tagable<T> {
	protected final Mirror mirror = new Mirror();
	private final Logger logger = LogManager.getLogger(AbstractSerializator.class);
	private final Nodes nodes = new Nodes() {
		private static final long serialVersionUID = 1L;

		{
			put("serialVersionUID", Mode.IGNORE);
			put("MIRROR", Mode.IGNORE);
			put("logger", Mode.IGNORE);
			put("STYLE", Mode.IGNORE);
		}
	};

	public Field getField(final Class<?> type, final String field) {
		final ClassController<?> controller = mirror.on(type);
		return controller.reflect().field(field);
	}

	public Object getId(final Object target) {
		return getValue(target, "id");
	}

	public Object getValue(final Object target, final String field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	public Object getValue(final Object target, final Field field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	public void setValue(final Object target, final Field field, final Object value) {
		final AccessorsController accessor = mirror.on(target);
		accessor.set().field(field).withValue(value);
	}

	public Class<T> getClass(final java.lang.reflect.Type type) {
		final String name = type.toString().replaceAll("class ", "");
		try {
			@SuppressWarnings("unchecked")
			final Class<T> t = (Class<T>) Class.forName(name);
			logger.debug("Deserializando tipo {}", t.getSimpleName());
			return t;
		} catch (final Exception e) {
			logger.error("Erro ao instanciar tipo {}, detalhe: {}", type, e.getMessage());
			logger.debug("Erro ao instanciar", e);
			throw new JsonParseException(e);
		}
	}

	public Nodes nodes() {
		return nodes;
	}

	protected abstract Boolean isToMany(final Field field);

	protected abstract Boolean isToOne(final Field field);
}