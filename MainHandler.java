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
		
		
		  //1. Carregar vari�veis necess�rias para o benchmark
		  int operationcount = Integer.parseInt(req.getParameter("operationcount"));
		  int schema = Integer.parseInt(req.getParameter("schema"));
		  String workload = req.getParameter("workload");
		  int writes = Integer.parseInt(req.getParameter("writes"));
		  int reads = Integer.parseInt(req.getParameter("reads"));
		  
		  //2. Uma inst�ncia do objeto DatastoreService � necess�ria para a inser��o com a API 
		  //de baixo n�vel
		  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		  
		  //3. Armazena tempo atual
		  long start = System.currentTimeMillis();
		  
		  //4. Realiza as opera��es de inser��o e leitura, de acordo com o esquema escolhido
		  /*
		   * No JPA, os grupos de entidades s�o definidos por anota��es (ler as classes de entidades)
		   * O JPA n�o � totalmente compat�vel com o arquitetura do datastore e FOR�A transa��es
		   * A transa��o realiza um commit quando se chama EntityManager.close()
		   * Cada transa��o tem um limite de 25 grupos de entidade diferentes
		   * Para realizar este benchmark, foi necess�rio dividir as inser��es de entidades
		   * em pequenas transa��es, o que N�O TORNA OS RESULTADOS INV�LIDOS, pois isto simula
		   * uma ambiente real. Por exemplo: quando um usu�rio insere um artigo, a inser��o ocorrer�
		   * numa �nica transa��o. Em um ambiente real, um usu�rio dificilmente inserir� m�ltiplas
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
		  
		  /*  No caso do esquema 2, a cada intera��o s�o inseridas 2 entidades: User e Article, sendo
		  	que article � filha de User, simulando o que aconteceria com um aplicativo que utiliza
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
