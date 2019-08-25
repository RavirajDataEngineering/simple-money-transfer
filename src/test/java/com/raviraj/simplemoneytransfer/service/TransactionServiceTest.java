package com.raviraj.simplemoneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.google.gson.Gson;
import com.raviraj.simplemoneytransfer.database.model.Transaction;

@TestMethodOrder(OrderAnnotation.class)
public class TransactionServiceTest {
	static Server server = new Server(1001);
	
	@BeforeAll
	public static void init() {
		
		System.out.println("starting server");
        ServletContextHandler ctx = 
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
                
        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/moneytransfertest/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages", 
                "com.raviraj.simplemoneytransfer.service");
        try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterAll
	public static void close() {
		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Test Get Transaction Gives 404 when account doesnt exist")
	public void testGetAccountW() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 HttpGet getRequest = new HttpGet("http://localhost:1000/moneytransfertest/transactions/1");
		 HttpResponse response = httpClient.execute(getRequest);
		 assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	@DisplayName("Test Add Transaction by post and Succesfully Get it  by Get")
	public void testAddAccount() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/transactions");
		 Transaction tr = new Transaction();
		 tr.setAmount(10);
		 tr.setCreditAccountId(1);
		 tr.setDebitAccountId(2);
		 tr.setTransactionId(6);
		 
		 String jsonString = new Gson().toJson(tr);
		 final StringEntity stringEntity = new StringEntity(jsonString);
		 httpPost.setEntity(stringEntity);
		 httpPost.setHeader("Accept", "application/json");
         httpPost.setHeader("Content-type", "application/json");
         
         HttpResponse response = httpClient.execute(httpPost);
         assertEquals(200, response.getStatusLine().getStatusCode());
         
         final HttpEntity entity = response.getEntity();
         assertNotNull(entity);
         assertEquals("application/json", entity.getContentType().getValue());
         String responseStr = EntityUtils.toString(entity);
         System.out.println(responseStr);
         HashMap<String, String> acc = new Gson().fromJson(responseStr, HashMap.class);
         assertEquals(1.0, acc.get("creditAccountId"));
         assertEquals(2.0, acc.get("debitAccountId"));
         assertEquals(10.0, acc.get("amount"));
         
         HttpGet request = new HttpGet("http://localhost:1000/moneytransfertest/transactions/6");
         HttpResponse res2 = httpClient.execute(request);
         assertEquals(200, res2.getStatusLine().getStatusCode());
         String response2 = EntityUtils.toString(res2.getEntity());
         HashMap<String, String> map2 = new Gson().fromJson(response2, HashMap.class);
         assertEquals(1.0, acc.get("creditAccountId"));
         assertEquals(2.0, acc.get("debitAccountId"));
         assertEquals(10.0, acc.get("amount"));
    }
	
	@Test
	@DisplayName("Adding already exisitng transaction fails with 400")
	public void testAddAlreadyExistingAccount() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/transactions");
		 Transaction tr = new Transaction();
		 tr.setAmount(10);
		 tr.setCreditAccountId(1);
		 tr.setDebitAccountId(2);
		 tr.setTransactionId(7);
		 
		 
		 String jsonString = new Gson().toJson(tr);
		 final StringEntity stringEntity = new StringEntity(jsonString);
		 httpPost.setEntity(stringEntity);
		 httpPost.setHeader("Accept", "application/json");
         httpPost.setHeader("Content-type", "application/json");
         
         HttpResponse response = httpClient.execute(httpPost);
         assertEquals(200, response.getStatusLine().getStatusCode());
         response.getEntity().consumeContent();
         
         response = httpClient.execute(httpPost);
         assertEquals(400, response.getStatusLine().getStatusCode());
         
	}
	
	@Test
	@DisplayName("Adding invalid Transaction gives a Invalid request response")
	public void testAddInvalidAccount() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/transactions");
		 Transaction tr = new Transaction();
		 tr.setAmount(10);
		 tr.setCreditAccountId(1);
		 tr.setDebitAccountId(2);
		 tr.setTransactionId(0);
		 
		 
		 String jsonString = new Gson().toJson(tr);
		 final StringEntity stringEntity = new StringEntity(jsonString);
		 httpPost.setEntity(stringEntity);
		 httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        
        HttpResponse response = httpClient.execute(httpPost);
        assertEquals(400, response.getStatusLine().getStatusCode());
        response.getEntity().consumeContent();
	}
	
	
}
