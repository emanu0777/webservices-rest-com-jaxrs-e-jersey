package br.com.alura.loja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ProjetoTest {

	
	private HttpServer httpServer;
	
	private Client client;
	private WebTarget target;
	
	@Before
	public void startaServidor() {
		httpServer = Servidor.startarServidor();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter());
		client  = ClientBuilder.newClient(clientConfig);
		target = client.target("http://localhost:8080");
	}
	
	@After
	public void stopServidor() {
		httpServer.stop();
	}
	
	@Test
	public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/");
        Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);
        Assert.assertEquals("Minha loja", projeto.getNome());
	}
	
	
	@Test
	public void testaAdicionarNovosProjetos() {
		Projeto projeto = new Projeto(3, "Projeto de Agendamento Alura", 2021);
		Entity<Projeto> entity = Entity.entity(projeto, MediaType.APPLICATION_XML);
		Response response = target.path("/projetos/").request().post(entity);
		assertEquals(201, response.getStatus());
		
		String location = response.getHeaderString("Location");
		Projeto projetoRetornado = client.target(location).request().get(Projeto.class);
		
		assertTrue(projetoRetornado.getNome().contains("Alura"));
	}
}
