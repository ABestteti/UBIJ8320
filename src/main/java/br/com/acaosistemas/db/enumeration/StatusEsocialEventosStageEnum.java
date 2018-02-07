package br.com.acaosistemas.db.enumeration;

import br.com.acaosistemas.frw.enumeration.BaseEnum;

public enum StatusEsocialEventosStageEnum implements BaseEnum<Integer> {
	/**
     * Indica que o evento da stage esta pronto para ser assinado (101).
     * {@value 101}
     */
    A_ASSINAR(101, "A assinar"),
 
    /**
     * Indica que o evento da stage foi assinado com sucesso pelo web service
     * de assinatura de evento.
     * {@value 198}
     */
    ASSINADO_COM_SUCESSO(198, "Evento da stage foi assinado com sucesso"),
    
    /**
     * Indica que houve um erro na assinatura do evento da stage pelo web service
     * de assinatura de evento.
     * {@value 199}
     */
    ERRO_ASSINATURA_IRRECUPERAVEL(199, "Erro na assinatura do evento da stage - irrecuperavel"),
    
    /**
     * Indica que o XML do evento esta preparado para ser validado contra o seu 
     * respectivo XSD.
     * {@value 201}
     */
    A_VALIDAR(201, "A validar"),
    
    /**
     * Indica que o XML do evento nao passou pela validacao do seu XSD, e que
     * foram geradas mensagens de erro.
     * {@value 295}
     */
    ERRO_VALIDACAO(295, "Erro de validacao do XML"),
    
    /**
     * Indica que o XML do evento foi validado com sucesso contra o seu respectivo
     * XSD.
     * {@value 298}
     */
    VALIDADO_COM_SUCESSO(298, "Validado com sucesso"),
    
    /**
     * Significa que houve um erro na validacao do XML do evento e que ele e irrecuperavel.
     * {@value 299}
     */
    ERRO_VALIDACAO_IRRECUPERAVEL(299, "Erro na validacao - irrecuperavel");

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
