package com.interview;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class APITest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "XX:YY", 401 }, { "AA:BB", 401 }, { "VALIDUSER:VALIDPWD", 401 } });
  }

  @Parameter // first data value (0) is default
  public String creds;

  @Parameter(1)
  public int expectedRespCode;

  @Test
  public void testLogin() throws Exception {
    String[] credValues = creds.split(":");
    String userName = credValues[0];
    String password = credValues[1];

    String serviceUrl = "https://api.shipt.com/auth/v2/oauth/token?bucket_number=0";
    System.out.println("URL " + serviceUrl);

    Client client = ClientBuilder.newClient(new ClientConfig());
    WebTarget webTarget = client.target(serviceUrl);

    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
    formData.add("username", userName);
    formData.add("password", password);
    formData.add("grant_type", "password");

    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br").header(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_TYPE)
        .header("Origin", "https://shop.shipt.com").header("Referer", "https://shop.shipt.com/login")
        .header("X-User-Type", "Customer");

    Response response = invocationBuilder.post(Entity.form(formData));
    String responseText = response.readEntity(String.class);

    assertEquals(response.getStatus(), expectedRespCode);
    if(response.getStatus() == 401) {
      assert(responseText.contains("Invalid Username or Password"));
    }

  }
}
