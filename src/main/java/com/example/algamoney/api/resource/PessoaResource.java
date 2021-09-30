package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	@GetMapping
	public List<Pessoa> listar(){
		
		return pessoaRepository.findAll();
		
	}
	
	@PostMapping
	//@ResponseStatus(HttpStatus.CREATED) é dispensável já que na resposta já estou dizendo o status.
	//@Valid -> validação do Modelo pelo Spring; @RequestBody -> No corpo da requisição teremos um objeto Pessoa
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa Pessoa, HttpServletResponse response) {
		Pessoa PessoaSalva = pessoaRepository.save(Pessoa);
		
		/*{codigo} pode ser usado livremente e o Spring o detecta como atributo de Pessoa, pois 
		 * ao injetarmos pessoaRepository a mesma conhece a impl de JpaRepository<Pessoa,Long> ?*/
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}") // {codigo} é variável (Expression Language)
		.buildAndExpand(PessoaSalva.getCodigo()).toUri();
		
		response.setHeader("Locations", uri.toASCIIString());
		
		return ResponseEntity.created(uri).body(PessoaSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Pessoa> PessoaBuscada = pessoaRepository.findById(codigo);
		
		/*
		if(PessoaBuscada.isPresent()) { // avaliando o objeto Optional<Pessoa>
		
		return ResponseEntity.ok(PessoaBuscada.get());
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		*/
		
		return PessoaBuscada.isPresent() == true ? ResponseEntity.ok(PessoaBuscada.get()) : ResponseEntity.notFound().build();
	}


}
