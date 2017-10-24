package br.com.acaosistemas.db.enumeration;

import br.com.acaosistemas.frw.enumeration.BaseEnum;

public enum StatusEsocialEventosStageEnum implements BaseEnum<Integer> {
	/**
     * Indica que o evento da stage esta pronto para ser assinado.
     */
    A_ASSINAR(201, "A assinar"),
 
    /**
     * Indica que o evento da stage foi assinado com sucesso pelo web service
     * de assinatura de evento.
     */
    ASSINADO_COM_SUCESSO(298, "Evento da stage foi assinado com sucesso"),
    
    /**
     * Indica que houve um erro na assinatura do evento da stage pelo web service
     * de assinatura de evento.
     */
    ERRO_ASSINATURA_IRRECUPERAVEL(299, "Erro na assinatura do evento da stage - irrecuperável");

    private Integer id;
    private String descricao;

    /**
     * Construtor.
     *
     * @param id Identificador da enumeracao.
     * @param descricao Descricao da enumeracao.
     */
    StatusEsocialEventosStageEnum(final Integer id, final String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    /**
     * Recupera a enumeracao atraves do ID informado.
     *
     * @param id Identificador da enumeracao.
     * @return Enumeracao alcancada.
     */
    public static StatusEsocialEventosStageEnum getById(final Integer id) {
    	StatusEsocialEventosStageEnum statusEventoStageEnum = null;
        for (final StatusEsocialEventosStageEnum someEnum : values()) {
            if (someEnum.getId().equals(id)) {
                statusEventoStageEnum =  someEnum;
            }
        }
        return statusEventoStageEnum;
    }
}
