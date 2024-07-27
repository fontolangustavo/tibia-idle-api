package com.fontolan.tibiaidle.utils;

import java.util.List;
import java.util.Objects;

public class ArrayUtils {

    /**
     * Encontra o índice de um objeto em uma lista com base no valor do campo `id`.
     *
     * @param list O List contendo os objetos.
     * @param id   O valor do `id` a ser procurado.
     * @param <T>  O tipo do objeto na lista.
     * @return O índice do objeto na lista ou -1 se não encontrado.
     */
    public static <T> int indexOfById(List<T> list, Object id, IdGetter<T> idGetter) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(idGetter.getId(list.get(i)), id)) {
                return i;
            }
        }
        return -1; // Retorna -1 se o objeto não for encontrado
    }

    /**
     * Interface funcional para obter o `id` do objeto.
     */
    @FunctionalInterface
    public interface IdGetter<T> {
        Object getId(T item);
    }

    public static <T> T findById(List<T> list, Object id, IdGetter<T> idGetter) {
        return list.stream()
                .filter(t -> Objects.equals(idGetter.getId(t), id))
                .findFirst()
                .orElse(null);
    }
 }

