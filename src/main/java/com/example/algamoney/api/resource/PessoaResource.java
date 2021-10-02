package com.example.algamoney.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Pessoa> listar(){
		
		return pessoaRepository.findAll();
		
	}
	
	@PostMapping
	//@ResponseStatus(HttpStatus.CREATED) é dispensável já que na resposta já estou dizendo o status.
	//@Valid -> validação do Modelo pelo Spring; @RequestBody -> No corpo da requisição teremos um objeto Pessoa
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa Pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = pessoaRepository.save(Pessoa);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
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
