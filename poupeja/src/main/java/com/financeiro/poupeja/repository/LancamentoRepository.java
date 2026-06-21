package com.financeiro.poupeja.repository;

import com.financeiro.poupeja.dto.CategoriaGastoDTO;
import com.financeiro.poupeja.dto.FormaPagamentoRelatorioDTO;
import com.financeiro.poupeja.entity.Lancamento;
import com.financeiro.poupeja.entity.Usuario;
import com.financeiro.poupeja.enumeration.TipoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    Page<Lancamento> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao, Pageable pageable);

    Page<Lancamento> findByUsuario(Usuario usuario, Pageable pageable);

    List<Lancamento> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao);

    List<Lancamento> findByUsuario(Usuario usuario);

    @Query("SELECT SUM(l.valorTotal) FROM Lancamento l WHERE l.usuario = :usuario AND l.tipo = :tipo AND l.data BETWEEN :startDate AND :endDate")
    Double sumValorTotalByUsuarioAndTipoAndDataBetween(
        @Param("usuario") Usuario usuario,
        @Param("tipo") TipoLancamento tipo,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new com.financeiro.poupeja.dto.CategoriaGastoDTO(l.categoria.id, SUM(l.valorTotal)) FROM Lancamento l WHERE l.usuario = :usuario AND l.tipo = :tipo AND l.data BETWEEN :startDate AND :endDate GROUP BY l.categoria.id")
    List<CategoriaGastoDTO> sumValorTotalByUsuarioAndTipoAndDataBetweenGroupedByCategoria(
        @Param("usuario") Usuario usuario,
        @Param("tipo") TipoLancamento tipo,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT new com.financeiro.poupeja.dto.FormaPagamentoRelatorioDTO(fp.descricao, SUM(l.valorTotal), COUNT(l.id)) " +
           "FROM Lancamento l " +
           "JOIN l.formaPagamento fp " +
           "WHERE l.usuario = :usuario " +
           "GROUP BY fp.id, fp.descricao")
    List<FormaPagamentoRelatorioDTO> findTotaisByFormaPagamento(@Param("usuario") Usuario usuario);
}
