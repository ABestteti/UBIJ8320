package br.com.acaosistemas.db.enumeration;

/**
 * Enumeracao para representar os valores SIM e NAO.
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Alterações:
 * <p>
 * 2018.03.08 - ABS - Adiconado JavaDOc.
 * 
 * @author Anderson Bestteti Santos
 *
 */
public enum SimNaoEnum {
    /**
     * Enumeracao para SIM.
     */
    SIM("S", "Sim"),
    /**
     * Enumeracao para Nao.
     */
    NAO("N", "Nao");
    private String id;
    private String descricao;

    /**
     * Construtor padrao para a Enum.
     * @param id com o codigo.
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
     * Recupera a enumeracao com base no identificador.
     *
     * @param id
     *            Valor para o identificador.
     * @return A enumeracao alcancada.
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