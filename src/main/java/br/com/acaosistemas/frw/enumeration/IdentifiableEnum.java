package br.com.acaosistemas.frw.enumeration;

/**
 * Interface que define se uma enumeracao e identificavel por ID.
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * @author Estevao Cavinato
 * @param <T> Tipo da chave da enumeracao.
 */
public interface IdentifiableEnum<T extends Object> {

    /**
     * ID da enumeracao, a ser tipada na definicao.
     *
     * @return ID da enumeracao.
     */
    T getId();

    /**
     *
     * Método que retorna a descricao da enumeracao.
     *
     * @return a descricao da enumeracao.
     */
    String getDescricao();

}
