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

import com.google.gson.Gson;
import com.raviraj.simplemoneytransfer.database.model.Account;

public class AccountServiceTest {

	static Server server = new Server(1000);
	
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
	@DisplayName("Test Get Account Gives 404 when account doesnt exist")
	public void testGetAccountW() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 HttpGet getRequest = new HttpGet("http://localhost:1000/moneytransfertest/accounts/1");
		 HttpResponse response = httpClient.execute(getRequest);
		 assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	@DisplayName("Test Add Account by post and Succesfully Get it  by Get")
	public void testAddAccount() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/accounts");
		 Account account = new Account();
		 account.setAccountId(1L);
		 account.setUserId("raviraj");
		 account.setBalance(100);
		 
		 String jsonString = new Gson().toJson(account);
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
         assertEquals(1.0, acc.get("accountId"));
         assertEquals("raviraj", acc.get("userId"));
         assertEquals(100.0, acc.get("balance"));
         
         HttpGet request = new HttpGet("http://localhost:1000/moneytransfertest/accounts/1");
         HttpResponse res2 = httpClient.execute(request);
         assertEquals(200, res2.getStatusLine().getStatusCode());
         String response2 = EntityUtils.toString(res2.getEntity());
         HashMap<String, String> map2 = new Gson().fromJson(response2, HashMap.class);
         assertEquals(1.0, map2.get("accountId"));
         assertEquals("raviraj", map2.get("userId"));
         assertEquals(100.0, map2.get("balance"));
    }
	
	@Test
	@DisplayName("Adding already exisitng account fails with 400")
	public void testAddAlreadyExistingAccount() throws ClientProtocolException, IOException {
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/accounts");
		 Account account = new Account();
		 account.setAccountId(2L);
		 account.setUserId("raviraj");
		 account.setBalance(100);
		 
		 
		 String jsonString = new Gson().toJson(account);
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
	@DisplayName("Adding invalid account gives a Invalid request response")
	public void testAddInvalidAccount() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 final HttpPost httpPost = new HttpPost("http://localhost:1000/moneytransfertest/accounts");
		 Account account = new Account();
		 account.setAccountId(0);
		 account.setUserId("raviraj");
		 account.setBalance(100);
		 
		 
		 String jsonString = new Gson().toJson(account);
		 final StringEntity stringEntity = new StringEntity(jsonString);
		 httpPost.setEntity(stringEntity);
		 httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        
        HttpResponse response = httpClient.execute(httpPost);
        assertEquals(400, response.getStatusLine().getStatusCode());
        response.getEntity().consumeContent();
	}
	
}
