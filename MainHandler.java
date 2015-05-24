package com.uenpjava1;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;


@SuppressWarnings("serial")
public class MainHandler extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		  //1. Carregar variáveis necessárias para o benchmark
		  int operationcount = Integer.parseInt(req.getParameter("operationcount"));
		  int schema = Integer.parseInt(req.getParameter("schema"));
		  String workload = req.getParameter("workload");
		  int writes = Integer.parseInt(req.getParameter("writes"));
		  int reads = Integer.parseInt(req.getParameter("reads"));
		  
		  //2. Uma instância do objeto DatastoreService é necessária para a inserção com a API 
		  //de baixo nível
		  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		  
		  //3. Armazena tempo atual
		  long start = System.currentTimeMillis();
		  
		  //4. Realiza as operações de inserção e leitura, de acordo com o esquema escolhido
		  /*
		   * No JPA, os grupos de entidades são definidos por anotações (ler as classes de entidades)
		   * O JPA não é totalmente compatível com o arquitetura do datastore e FORÇA transações
		   * A transação realiza um commit quando se chama EntityManager.close()
		   * Cada transação tem um limite de 25 grupos de entidade diferentes
		   * Para realizar este benchmark, foi necessário dividir as inserções de entidades
		   * em pequenas transações, o que NÃO TORNA OS RESULTADOS INVÁLIDOS, pois isto simula
		   * uma ambiente real. Por exemplo: quando um usuário insere um artigo, a inserção ocorrerá
		   * numa única transação. Em um ambiente real, um usuário dificilmente inserirá múltiplas
		   * entidades Usuario e Artigo ao mesmo tempo
		   */
		  int sucessWrites = 0;
		  
		 
		  
		  /*No caso do esquema 1, */
		  if(schema==1)
		  {
			  Entity user = new Entity("User");
			  datastore.put(user);
			  Entity article = new Entity("Article", user.getKey());
			  datastore.put(article);
			  
			  for(int i = 0; i < writes; i++){
				  Entity comment = new Entity("Comment", article.getKey());
				  datastore.put(comment);
			  }
			  
		  }
		  
		  /*  No caso do esquema 2, a cada interação são inseridas 2 entidades: User e Article, sendo
		  	que article é filha de User, simulando o que aconteceria com um aplicativo que utiliza
		  	o esquema 2*/
		  
		  if(schema==2)
		  {
			  Entity user = new Entity("User");
			  datastore.put(user);
			  
			  for(int i = 0; i < writes; i++){
				  Entity article = new Entity("Article", user.getKey());
				  datastore.put(article);
				  Entity comment = new Entity("Comment", user.getKey());
				  datastore.put(article);
			  }
		  }
		  
		  //5. Exibe os resultados
		  long elapsedTimeMillis = System.currentTimeMillis() - start;
		  
		  resp.getWriter().println(elapsedTimeMillis/1000F);
		
	}
}
