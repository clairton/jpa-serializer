package br.eti.clairton.jpa.serializer;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

public class AplicacaoDeserializer extends JpaDeserializer<Aplicacao> {

	public AplicacaoDeserializer(final @NotNull EntityManager entityManager) {
		super(entityManager);
	}

}
