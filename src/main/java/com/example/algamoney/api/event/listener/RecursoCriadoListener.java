package com.example.algamoney.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;

@Component // irá "escutar" o RecursoCriadoEvent; quando o mesmo for lançado esta classe será acionada;
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent>{

	@Override
	public void onApplicationEvent(RecursoCriadoEvent recursoCriadoEvent) {
		
		HttpServletResponse response = recursoCriadoEvent.getResponse();
		Long codigo = recursoCriadoEvent.getCodigo();
		
		adicionarHeaderLocation(response, codigo);
		
	}

	/**/
	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		
		/*O argumento de buildAndExpand será injetado em {codigo}*/
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}") // o param {xxxx} pode ser qualquer coisa;
		.buildAndExpand(codigo).toUri();
		
		response.setHeader("Locations", uri.toASCIIString());
	}

}
