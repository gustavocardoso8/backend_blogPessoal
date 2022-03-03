package br.org.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.org.generation.blogpessoal.model.Usuario;
import br.org.generation.blogpessoal.repository.UsuarioRepository;
import br.org.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L, 
				"Jacinto Leite", "ensoqdm@email.com.br", "13465278", " "));
		
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		
	}
	
	@Test
	@Order(2)
	@DisplayName("Não deve permitir a duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Xisto Mendes", "qualquer.coisa@email.com.br", "13465278", " "));
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L, 
				"Robersvaldo Juveno", "mais.qualquer-coisa@email.com.br", "13465278", " "));
		
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		
	}
	
	
	
	@Test
	@Order(3)
	@DisplayName("Atualizar Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCreate = usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Memeiro News", "meme_zin@email.com.br", "issoai123", " "));
		
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(), 
				"Anderson Silva", "sem-ideia@email.com.br", "seilaoquepor123", " ");
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/atualizar", HttpMethod.PUT,requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		
	}
	
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os Usuário")
	public void deveListarTodosOsUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Frederico Rodolfo ", "saia.fred@email.com.br", "kiko123", " "));
			
			usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Tavinho 'O Tall ", "noiqta.brux@email.com.br", "Tatavi123", " "));
		
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
				
	}
	
	@Test
	@Order(5)
	@DisplayName("Procurar usuário por ID")
	public void procurarUsuariosPorId() {
		
		Usuario usuario = usuarioRepository.save(new Usuario(0L, 
				"Francisquinha", "dona.neves@email.com.br", "velha1234", " "));
	
		ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("root", "root")
			.exchange("/usuarios/"+usuario.getId(), HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
}
