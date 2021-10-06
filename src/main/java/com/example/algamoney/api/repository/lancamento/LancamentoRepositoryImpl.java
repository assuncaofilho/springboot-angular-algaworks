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

import org.springframework.util.ObjectUtils;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery{

	@PersistenceContext
	private EntityManager Entitymanager;
	
	/*Utilizar-se-á a Criteria do JPA*/
	@Override
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
		
		/*CriteriaBuilder -> CriteriaQuery -> TypedQuery -> .getResultList()*/
		
		//obtendo o objeto CriteriaBuilder, a partir de um EntityManager
		CriteriaBuilder builder = Entitymanager.getCriteriaBuilder();
		
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
		
		
		TypedQuery<Lancamento> typedQuery = Entitymanager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
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

}
