package com.raviraj.simplemoneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.service.utils.TransactionPayload;


public class TransferServiceTest {

	
	static Server server = new Server(1002);
	
	@BeforeAll
	public static void init() throws ClientProtocolException, IOException {
		
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
        addAccounts();
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
	
	public static void addAccounts() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost("http://localhost:1002/moneytransfertest/accounts");
		Account account = new Account();
		account.setAccountId(3);
		account.setUserId("Ram");
		account.setBalance(100);

		String jsonString = new Gson().toJson(account);
		StringEntity stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		HttpResponse response = httpClient.execute(httpPost);
		//assertEquals(200, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
		
		account.setAccountId(4);
		account.setUserId("Arjun");
		account.setBalance(100);
		
		jsonString = new Gson().toJson(account);
		stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		response = httpClient.execute(httpPost);
		response.getEntity().consumeContent();
		//assertEquals(200, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
		
		account.setAccountId(5);
		account.setUserId("Ryzen");
		account.setBalance(200);
		
		jsonString = new Gson().toJson(account);
		stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		
		response = httpClient.execute(httpPost);
		assertEquals(200, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
	}
	
	@Test
	@DisplayName(" Test a succesfull transaction with correct balance updates and corresponding transaction updated")
	@Order(4)
	public  void testSuccesfulTransfer() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost("http://localhost:1002/moneytransfertest/transfer");
		TransactionPayload payload = new TransactionPayload();
		payload.setAmount(10);
		payload.setCreditAccountId(3);
		payload.setDebitAccountId(4);
		
		String jsonString = new Gson().toJson(payload);
		StringEntity stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(httpPost);
		assertEquals(200, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
		
		HttpGet request = new HttpGet("http://localhost:1002/moneytransfertest/accounts/3");
        HttpResponse res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        String response2 = EntityUtils.toString(res2.getEntity());
        HashMap<String, String> map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(3.0, map2.get("accountId"));
        assertEquals("Ram", map2.get("userId"));
        assertEquals(132.0, map2.get("balance"));
        
        request = new HttpGet("http://localhost:1002/moneytransfertest/accounts/4");
        res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        response2 = EntityUtils.toString(res2.getEntity());
        map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(4.0, map2.get("accountId"));
        assertEquals("Arjun", map2.get("userId"));
        assertEquals(80.0, map2.get("balance"));
        
        request = new HttpGet("http://localhost:1002/moneytransfertest/transactions/3");
        res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        response2 = EntityUtils.toString(res2.getEntity());
        map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(3.0, map2.get("creditAccountId"));
        assertEquals(4.0, map2.get("debitAccountId"));
        assertEquals(10.0, map2.get("amount"));
	}
	
	
	@Test
	@DisplayName(" Test a unsuccesful transfer when the funds are insufficient")
	@Order(3)
	public  void testInsufficentFundTransfer() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost("http://localhost:1002/moneytransfertest/transfer");
		TransactionPayload payload = new TransactionPayload();
		payload.setAmount(1000);
		payload.setCreditAccountId(1);
		payload.setDebitAccountId(2);
		
		String jsonString = new Gson().toJson(payload);
		StringEntity stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(httpPost);
		assertEquals(400, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
	}
	
	@Test
	@DisplayName(" Test a unsuccesful transfer when the account doesnt exist in db")
	@Order(1)
	public  void testNonExistentAccountTransfer() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost("http://localhost:1002/moneytransfertest/transfer");
		TransactionPayload payload = new TransactionPayload();
		payload.setAmount(10);
		payload.setCreditAccountId(1);
		payload.setDebitAccountId(10);
		
		String jsonString = new Gson().toJson(payload);
		StringEntity stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(httpPost);
		assertEquals(400, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
	}
	
	@Test
	@DisplayName(" Test multiple succesful concurrent transactions with correct balance updates and corresponding transactions updated")
	@Order(2)
	public  void testSuccesfulConcurrentTransfers() throws ClientProtocolException, IOException, InterruptedException {
		ExecutorService tpool = Executors.newFixedThreadPool(2);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		DefaultHttpClient httpClient2 = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost("http://localhost:1002/moneytransfertest/transfer");
		TransactionPayload payload = new TransactionPayload();
		payload.setAmount(10);
		payload.setCreditAccountId(3);
		payload.setDebitAccountId(4);
		
		String jsonString = new Gson().toJson(payload);
		StringEntity stringEntity = new StringEntity(jsonString);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		
		TransactionPayload payload2 = new TransactionPayload();
		payload2.setAmount(12);
		payload2.setCreditAccountId(3);
		payload2.setDebitAccountId(5);
		
		final HttpPost httpPost2 = new HttpPost("http://localhost:1002/moneytransfertest/transfer");
		String jsonString2 = new Gson().toJson(payload2);
		StringEntity stringEntity2 = new StringEntity(jsonString2);
		httpPost2.setEntity(stringEntity2);
		httpPost2.setHeader("Accept", "application/json");
		httpPost2.setHeader("Content-type", "application/json");
		
		tpool.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpResponse response = httpClient.execute(httpPost);
					assertEquals(200, response.getStatusLine().getStatusCode());
					response.getEntity().consumeContent();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		tpool.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpResponse response = httpClient2.execute(httpPost2);
					assertEquals(200, response.getStatusLine().getStatusCode());
					response.getEntity().consumeContent();
					}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
        
		Thread.sleep(10000);
        HttpGet request = new HttpGet("http://localhost:1002/moneytransfertest/accounts/4");
        HttpResponse res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        String response2 = EntityUtils.toString(res2.getEntity());
        HashMap map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(4.0, map2.get("accountId"));
        assertEquals("Arjun", map2.get("userId"));
        assertEquals(90.0, map2.get("balance"));
        res2.getEntity().consumeContent();
        
        request = new HttpGet("http://localhost:1002/moneytransfertest/accounts/5");
        res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        response2 = EntityUtils.toString(res2.getEntity());
         map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(5.0, map2.get("accountId"));
        assertEquals("Ryzen", map2.get("userId"));
        assertEquals(188.0, map2.get("balance"));
        res2.getEntity().consumeContent();
        
        request = new HttpGet("http://localhost:1002/moneytransfertest/accounts/3");
        res2 = httpClient.execute(request);
        assertEquals(200, res2.getStatusLine().getStatusCode());
        response2 = EntityUtils.toString(res2.getEntity());
         map2 = new Gson().fromJson(response2, HashMap.class);
        assertEquals(3.0, map2.get("accountId"));
        assertEquals("Ram", map2.get("userId"));
        assertEquals(122.0, map2.get("balance"));
        res2.getEntity().consumeContent();
        
       
        
        tpool.isShutdown();
    }
	
}
