package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

abstract class AbstractSerializator<T> {
	protected final Mirror mirror;
	protected final Logger logger;

	public AbstractSerializator(final Mirror mirror, final Logger logger) {
		this.mirror = mirror;
		this.logger = logger;
	}

	public Field getField(final Class<?> type, final String field) {
		final ClassController<?> controller = mirror.on(type);
		return controller.reflect().field(field);
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

	public Class<T> getClass(java.lang.reflect.Type type) {
		final String name = type.toString().replaceAll("class ", "");
		logger.debug("Deserializando tipo {}", name);
		try {
			@SuppressWarnings("unchecked")
			final Class<T> t = (Class<T>) Class.forName(name);
			return t;
		} catch (final Exception e) {
			logger.error("Erro ao instanciar tipo {}, detalhe: {}", type, e.getMessage());
			logger.debug("Erro ao instanciar", e);
			throw new JsonParseException(e);
		}
	}

	public abstract Boolean isToMany(final Field field);

	public abstract Boolean isToOne(final Field field);
}