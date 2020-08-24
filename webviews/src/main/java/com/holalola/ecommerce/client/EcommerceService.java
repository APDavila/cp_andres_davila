package com.holalola.ecommerce.client;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holalola.ecommerce.client.vo.GetByMailRequest;
import com.holalola.ecommerce.client.vo.GetByMailResponse;

@Stateless
public class EcommerceService {
	private static final String ECOMMERCE_DOMAIN = "http://10.122.5.148:8080/WS_NUO_ECOMMERCE/";
	private static final String PAYPHONE_DOMAIN = "https://pay.payphonetodoesposible.com/api/";
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//	
//	public String findDeferred() {
//		DeferredRequest json = new DeferredRequest(email);
//		String xmlStr = consultawwwformurlencodedJSON("clientes/consultar/mail",
//				new String(new Gson().toJson(json)), "");
//
//		if (xmlStr == null || xmlStr.trim().length() <= 0)
//			return new GetByMailResponse();
//		return GSON.fromJson(xmlStr, new TypeToken<GetByMailResponse>() {
//		}.getType());
//	}
	
	public GetByMailResponse findByEmail(String email) {
		GetByMailRequest json = new GetByMailRequest(email);
		String xmlStr = consultawwwformurlencodedJSON("clientes/consultar/mail",
				new String(new Gson().toJson(json)), "");

		if (xmlStr == null || xmlStr.trim().length() <= 0)
			return new GetByMailResponse();
		return GSON.fromJson(xmlStr, new TypeToken<GetByMailResponse>() {
		}.getType());
	}
	
	public String consultawwwformurlencodedJSON(String as_metodo, String Json, String token) {
		try {

			StringBuilder postData = new StringBuilder();
			URL url = new URL(String.format(ECOMMERCE_DOMAIN + "%s", as_metodo));
			url = new URL(url.toString() + postData.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("connectapitoken", token);
			conn.setDoOutput(true);	
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = Json.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			String ls_reusltado = IOUtils.toString(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			return ls_reusltado;
		} catch (Exception err) {
			System.out.println("error --------------------------consultawwwformurlencodedJSON---------------------\n"
					+ err.getMessage());
			return "";
		}

	}

}
