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
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController // controlador REST -> O retorno será convertido para JSON por exemplo; facilitador;
@RequestMapping("/categorias") // mapeamento de requisição; em "/categorias" esta classe será acionada
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Categoria> listar(){
		
		return categoriaRepository.findAll();
		
	}
	
	@PostMapping
	//@ResponseStatus(HttpStatus.CREATED) é dispensável já que na resposta já estou dizendo o status.
	//@Valid -> validação do Modelo pelo Spring; @RequestBody -> No corpo da requisição teremos um objeto Categoria
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> categoriaBuscada = categoriaRepository.findById(codigo);
		
		/*
		if(categoriaBuscada.isPresent()) { // avaliando o objeto Optional<Categoria>
		
		return ResponseEntity.ok(categoriaBuscada.get());
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		*/
		
		return categoriaBuscada.isPresent() == true ? ResponseEntity.ok(categoriaBuscada.get()) : ResponseEntity.notFound().build();
	}

}
