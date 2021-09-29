package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController // controlador REST -> O retorno será convertido para JSON por exemplo; facilitador;
@RequestMapping("/categorias") // mapeamento de requisição; em "/categorias" esta classe será acionada
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public List<Categoria> listar(){
		
		return categoriaRepository.findAll();
		
	}
	
	@PostMapping
	//@ResponseStatus(HttpStatus.CREATED) é dispensável já que na resposta já estou dizendo o status.
	public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		/*{codigo} pode ser usado livremente e o Spring o detecta como atributo de Categoria, pois 
		 * ao injetarmos categoriaRepository a mesma conhece a impl de JpaRepository<Categoria,Long> ?*/
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}") // {codigo} é variável (Expression Language)
		.buildAndExpand(categoriaSalva.getCodigo()).toUri();
		
		response.setHeader("Locations", uri.toASCIIString());
		
		return ResponseEntity.created(uri).body(categoriaSalva);
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
