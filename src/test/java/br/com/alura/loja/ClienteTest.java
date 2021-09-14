package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

public class ClienteTest {

	
	private HttpServer httpServer;
	
	private WebTarget target;
	
	private Client client;
	
	@Before
	public void startaServidor()  {
		httpServer =  Servidor.startarServidor();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(clientConfig);
		this.target = client.target("http://localhost:8080");
	}
	
	@After
	public void stopServidor() {
		httpServer.stop();
	}
	
    @Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
        Client client = ClientBuilder.newClient();
        target = client.target("http://localhost:8080");
        String conteudo = target.path("/carrinhos/1").request().get(String.class);
        Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }
    
//    @Test
//    public void testaQueAdicionarUmCarrinhoTrazMensagemSucesso() {
//    	client = ClientBuilder.newClient();
//        target = client.target("http://localhost:8080");
//        Carrinho carrinho = new Carrinho();
//        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
//        carrinho.setRua("Rua Vergueiro");
//        carrinho.setCidade("Sao Paulo");
//        String xml = carrinho.toXml();
//
//        Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
//        Response response = target.path("/carrinhos").request().post(entity);
//        Assert.assertEquals("<status>sucesso</status>", response.readEntity(String.class));
//    }
//    
    @Test
    public void testaQueSuportaNovosCarrinhos() {
    	Carrinho carrinho = new Carrinho();
    	carrinho.adiciona(new Produto(314, "Microfone", 37, 1));
    	carrinho.setRua("Afonso Pena 337");
    	carrinho.setCidade("São Paulo");
    	String xml = carrinho.toXml();
    	Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
    	Response response = target.path("/carrinhos/").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        String conteudo = this.client.target(location).request().get(String.class);
        
        Assert.assertTrue(conteudo.contains("Microfone"));
        
    }

}