package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Mode.ID;
import static br.eti.clairton.jpa.serializer.Mode.ID_POLYMORPHIC;
import static br.eti.clairton.jpa.serializer.Mode.IGNORE;
import static br.eti.clairton.jpa.serializer.Mode.RECORD;
import static br.eti.clairton.jpa.serializer.Mode.RELOAD;
import static org.apache.logging.log4j.LogManager.getLogger;

import java.lang.reflect.Field;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;

public abstract class JpaSerializer<T> extends Tagable<T> {
	private static final long serialVersionUID = 1L;
	private final Logger logger = getLogger(JpaSerializer.class);
	private final Nodes nodes = new Nodes() {
		private static final long serialVersionUID = 1L;

		{
			put("serialVersionUID", IGNORE);
			put("MIRROR", IGNORE);
			put("logger", IGNORE);
			put("STYLE", IGNORE);
		}
	};
	protected final Mirror mirror = new Mirror();

	protected void record(final String field) {
		config(field, RECORD);
	}

	protected void record(final String field, final Operation operation) {
		config(field, RECORD, operation);
	}

	protected void id(final String field) {
		config(field, ID);
	}

	protected void idPolymorphic(final String field) {
		config(field, ID_POLYMORPHIC);
	}

	protected void id(final String field, final Operation operation) {
		config(field, ID, operation);
	}

	protected void idPolymorphic(final String field, final Operation operation) {
		config(field, ID_POLYMORPHIC, operation);
	}

	protected void reload(final String field) {
		config(field, RELOAD);
	}

	protected void reload(final String field, final Operation operation) {
		config(field, RELOAD, operation);
	}

	protected void ignore(final String field) {
		config(field, IGNORE);
	}

	protected void ignore(final String field, final Operation operation) {
		config(field, IGNORE, operation);
	}

	protected void config(final String field, final Mode mode) {
		nodes().put(field, mode);
	}

	protected void config(final String field, final Mode mode, final Operation operation) {
		nodes().put(field, mode, operation);
	}

	protected Field getField(final Class<?> type, final String field) {
		final ClassController<?> controller = mirror.on(type);
		return controller.reflect().field(field);
	}

	protected Object getId(final Object target) {
		return getValue(target, "id");
	}

	protected Object getValue(final Object target, final String field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	protected Object getValue(final Object target, final Field field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	protected void setValue(final Object target, final Field field, final Object value) {
		final AccessorsController accessor = mirror.on(target);
		accessor.set().field(field).withValue(value);
	}

	protected Class<T> getClass(final java.lang.reflect.Type type) {
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

	protected Boolean isToMany(final Field field, final Operation operation) {
		return (field.isAnnotationPresent(ManyToOne.class) ||
					field.isAnnotationPresent(OneToOne.class)) &&
						!isRecord(field.getName(), operation) && 
							!isReload(field.getName(), operation);
	}

	protected Boolean isToOne(final Field field, final Operation operation) {
		return (field.isAnnotationPresent(OneToMany.class) || 
					field.isAnnotationPresent(ManyToMany.class)) &&
						!isRecord(field.getName(), operation) && 
							!isReload(field.getName(), operation);
	}

	protected Boolean isIgnore(final String key, final Operation operation) {
		return nodes().isIgnore(key, operation);
	}

	protected Boolean isIgnore(final String key) {
		return nodes().isIgnore(key);
	}

	protected Boolean isId(final String key, final Operation operation) {
		return nodes().isId(key, operation);
	}

	protected Boolean isId(final String key) {
		return nodes().isId(key);
	}

	protected Boolean isIdPolymorphic(final String key) {
		return nodes().isIdPolymorphic(key);
	}

	protected Boolean isIdPolymorphic(final String key, final Operation operation) {
		return nodes().isIdPolymorphic(key, operation);
	}

	protected Boolean isReload(final String key, final Operation operation) {
		return nodes().isReload(key, operation);
	}

	protected Boolean isReload(final String key) {
		return nodes().isReload(key);
	}

	protected Boolean isRecord(final String key, final Operation operation) {
		return nodes().isRecord(key, operation);
	}

	protected Boolean isRecord(final String key) {
		return nodes().isRecord(key);
	}
}