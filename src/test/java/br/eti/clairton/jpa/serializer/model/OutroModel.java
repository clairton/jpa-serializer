package br.eti.clairton.jpa.serializer.model;


public class OutroModel extends Aplicacao {
	private static final long serialVersionUID = 6016230217349046379L;
	private String outroValor = "PSADGKSADGLDSLÇ";

	public OutroModel(final String nome) {
		super(nome);
	}

	public String getOutroValor() {
		return outroValor;
	}
}