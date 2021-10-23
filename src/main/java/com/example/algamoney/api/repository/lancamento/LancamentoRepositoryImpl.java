package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.example.algamoney.api.model.Categoria_;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.model.Pessoa_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery{

	@PersistenceContext
	private EntityManager entitymanager;
	
	/*Utilizar-se-á a Criteria do JPA*/
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		/*CriteriaBuilder -> CriteriaQuery -> TypedQuery -> .getResultList()*/
		
		//obtendo o objeto CriteriaBuilder, a partir de um EntityManager
		CriteriaBuilder builder = entitymanager.getCriteriaBuilder();
		
		//CriteriaQuery define por sua vez a entidade principal a ser utilizada 
		//na query – aqui no caso a classe Lancamento
		//passa-se o retorno esperado da consulta
		CriteriaQuery<Lancamento> criteriaQuery = builder.createQuery(Lancamento.class);
		
		/*O próximo passo é estabelecer a raiz da consulta, ou seja, a classe principal da cláusula FROM. 
		 * Para isso utilizamos o método from() a partir da interface CriteriaQuery. 
		 * Isso equivale à declaração de uma variável de identificação, e formará a base para expressões 
		 * que será utilizada como o caminho para o resto da consulta.
		 * Equivalente ao alias l em from Lancamento l - Podemos usá-lo como ponto de partida para acessar atributos da entidade*/
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class); 
		
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteriaQuery.where(predicates);
		
		TypedQuery<Lancamento> typedQuery = entitymanager.createQuery(criteriaQuery);
		adicionarRestricoesDePaginacao(typedQuery, pageable);
		
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(lancamentoFilter)) ;
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = entitymanager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(ResumoLancamento.class
				, root.get(Lancamento_.codigo), root.get(Lancamento_.descricao)
				, root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento)
				, root.get(Lancamento_.valor), root.get(Lancamento_.tipo)
				, root.get(Lancamento_.categoria).get(Categoria_.nome)
				, root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = entitymanager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}


	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,Root<Lancamento> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.DESCRICAO)) , "%" + lancamentoFilter.getDescricao() + "%"));
		}
		
		if(!ObjectUtils.isEmpty(lancamentoFilter.getDataVencimentoDe())) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Lancamento_.DATA_VENCIMENTO), lancamentoFilter.getDataVencimentoDe()));
			
		}
		
		
		if(!ObjectUtils.isEmpty(lancamentoFilter.getDataVencimentoAte())) {
			predicates.add(
					builder.lessThanOrEqualTo(root.get(Lancamento_.DATA_VENCIMENTO), lancamentoFilter.getDataVencimentoAte()));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
		//Predicate[] predicate = new Predicate[predicates.size()];
		//return predicates.toArray(predicate);
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> typedQuery, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize(); // pageable vem setado do Cliente;
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		typedQuery.setFirstResult(primeiroRegistroDaPagina); // o primeiro resultado inicia em 0;
		typedQuery.setMaxResults(totalRegistrosPorPagina);
		
	}
	private Long total(LancamentoFilter lancamentoFilter) {
		
		CriteriaBuilder builder = entitymanager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class); // tipo de retorno
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class); // entidade na qual será realizada a consulta
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteriaQuery.where(predicates);
		
		criteriaQuery.select(builder.count(root));
		return entitymanager.createQuery(criteriaQuery).getSingleResult();
		
	}



}
