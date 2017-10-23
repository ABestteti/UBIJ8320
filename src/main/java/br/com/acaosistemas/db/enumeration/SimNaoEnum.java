package br.com.acaosistemas.db.enumeration;

public enum SimNaoEnum {
    /**
     * Enumera��o para SIM.
     */
    SIM("S", "Sim"),
    /**
     * Enumera��o para N�o.
     */
    NAO("N", "N�o");
    private String id;
    private String descricao;

    /**
     * Construtor padr�o para a Enum.
     * @param id com o c�digo.
     * @param descricao com descritivo da Enum.
     */
    SimNaoEnum(final String id, final String descricao) {
        setId(id);
        setDescricao(descricao);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    /**
     * Recupera a enumera��o com base no identificador.
     *
     * @param id
     *            Valor para o identificador.
     * @return A enumera��o alcan�ada.
     */
    public static SimNaoEnum getById(final String id) {
        SimNaoEnum result = null;
        for (final SimNaoEnum someEnum : values()) {
            if (someEnum.getId().equals(id)) {
                result = someEnum;
            }
        }
        return result;
    }

}
