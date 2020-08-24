package com.holalola.webhook.acciones;

import java.security.Key;
import java.util.Formatter;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebookGeneral;
import com.holalola.webhook.facebook.MensajeParaFacebookGeneralV2;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.InformacionUsuarioFacebook;
import com.holalola.webhook.facebook.response.UserLocation;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.RichMessage;

public class ConsultarFacebook {

	final static Logger log = LoggerFactory.getLogger(ConsultarFacebook.class);

	private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create(); // GsonBuilder().create();
																						// //Anterior

	// private static final String facebookToken =
	// System.getProperty("lola.facebook.token");
	private static final String facebookUri = "https://graph.facebook.com/v4.0/";
	private static final String calveToken = "probandoWebhook";
	// private static final String paramAccessToken = "?access_token="+
	// facebookToken;
	private static final String paramFieldsUsuario = "&fields=first_name,last_name,profile_pic,birthday";
	private static final String paramFieldsUbicacionUsuario = "&fields=location";
	private static final String mesagesUri = "me/messages";
	private static final String attachmentsUri = "me/message_attachments";
	private static final String whitelistUri = "/me/messenger_profile";

	private static String getParamAccesToken(String facebookToken) {
		return "?access_token=" + facebookToken;
	}

	private static String getSHA256Hash(String message, String secret) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
			//System.out.println(hash);
			return hash;
		} catch (Exception e) {
			System.out.println("Error");
		}
		return "";
	}

	private static String getSHA256Hash1(String message, String secret) {
		String result = "";

		try {
			String data = message;
			String key = secret;
			// Get an hmac_sha1 key from the raw key bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// Convert raw bytes to Hex
			byte[] hexBytes = new Hex().encode(rawHmac);

			// Covert array of Hex bytes to a String
			result = new String(hexBytes, "ISO-8859-1");

		} catch (Exception e) {

		}
		return result;
	}

	private static String HMAC_SHA256(String secret, String message) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
		} catch (Exception e) {

		}
		return hash.trim();
	}

	public static String hashMac(String text, String secretKey) {

		try {
			Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
			Mac mac = Mac.getInstance(sk.getAlgorithm());
			mac.init(sk);
			final byte[] hmac = mac.doFinal(text.getBytes());
			return toHexString(hmac);
		} catch (Exception e1) {// throw an exception or pick a different encryption method
			try {
				throw e1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	private static final String HASH_ALGORITHM = "HmacSHA256";

	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		Formatter formatter = new Formatter(sb);
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		return sb.toString();
	}

	private static String obtenerInformacion(String facebookUserId, String facebookToken) {

		/*StringBuffer urlConsultaUsuario = new StringBuffer(facebookUri);

		String hash = getSHA256Hash1(getParamAccesToken(facebookToken), calveToken);

		String hash1 = hashMac(calveToken, getParamAccesToken(facebookToken));

		String hash3 = hashMac(getParamAccesToken(facebookToken), calveToken);

		System.out.println("URL 1 ----  " + hash1);

		System.out.println("URL 3 ----  " + hash3);

		urlConsultaUsuario.append(facebookUserId).append("/ids_for_pages").append(getParamAccesToken(facebookToken))
				.append("&appsecret_proof=" + hash);

		System.out.println("URL " + urlConsultaUsuario);

		try {
			// Client client = ClientBuilder.newClient(new
			// ClientConfig().register(LoggingFilter.class));
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultaUsuario.toString());

			Response response = webTarget.request().get(Response.class);
			if (response.getStatus() == 200) {
				String facebookResponse = response.readEntity(String.class);

				System.out.println(facebookResponse);
				return facebookResponse;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    */
		return "";
	}

	public static InformacionUsuarioFacebook obtenerInformacionDeUsuario(String facebookUserId, String facebookToken) {
		
		InformacionUsuarioFacebook usuarioFacebook = new InformacionUsuarioFacebook();
		StringBuffer urlConsultaUsuario = new StringBuffer(facebookUri);
		urlConsultaUsuario.append(facebookUserId).append(getParamAccesToken(facebookToken)).append(paramFieldsUsuario);

		try {
			// Client client = ClientBuilder.newClient(new
			// ClientConfig().register(LoggingFilter.class));
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultaUsuario.toString());

			Response response = webTarget.request().get(Response.class);
			
			if (response.getStatus() == 200) {
				String facebookResponse = response.readEntity(String.class);
				usuarioFacebook = GSON.fromJson(facebookResponse, InformacionUsuarioFacebook.class);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return usuarioFacebook;
	}
	
	public static UserLocation obtenerUbicacionUsuario(String facebookUserId, String facebookToken) {
		
		UserLocation usuarioFacebook = new UserLocation();
		StringBuffer urlConsultaUsuario = new StringBuffer(facebookUri);
		urlConsultaUsuario.append(facebookUserId).append(getParamAccesToken(facebookToken)).append(paramFieldsUbicacionUsuario);

		try {
			// Client client = ClientBuilder.newClient(new
			// ClientConfig().register(LoggingFilter.class));
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultaUsuario.toString());

			Response response = webTarget.request().get(Response.class);
			
			if (response.getStatus() == 200) {
				String facebookResponse = response.readEntity(String.class);
				usuarioFacebook = GSON.fromJson(facebookResponse, UserLocation.class);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return usuarioFacebook;
	}

	public static int postToFacebook(MensajeParaFacebookGeneral mensajeParaFacebook, String facebookToken) {

		StringBuffer urlEnvioMensajes = new StringBuffer(facebookUri).append(mesagesUri)
				.append(getParamAccesToken(facebookToken));

		Response response = ejecutarLlamadoFacebook(urlEnvioMensajes.toString(), mensajeParaFacebook);

		System.out.println(response.getStringHeaders());
		System.out.println(response.readEntity(String.class));

		return response.getStatus();
	}

	
	public static int postToFacebookString(String jsonParaFacebook, String facebookToken) {

		StringBuffer urlEnvioMensajes = new StringBuffer(facebookUri).append(mesagesUri)
				.append(getParamAccesToken(facebookToken));

		Response response = ejecutarLlamadoFacebook(urlEnvioMensajes.toString(), jsonParaFacebook);

		System.out.println(response.getStringHeaders());
		System.out.println(response.readEntity(String.class));

		return response.getStatus();
	}
	public static void whitelistDomains(List<String> domains, String facebookToken) {
		System.out.println("facebook ui = "+facebookUri);
		System.out.println("fb tk = "+facebookToken);
		StringBuffer urlWhiteList = new StringBuffer(facebookUri).append(whitelistUri)
				.append(getParamAccesToken(facebookToken));
		Response response = ejecutarLlamadoFacebook(urlWhiteList.toString(), new WhitelistedDomains(domains));

		System.out.println(response.readEntity(String.class));
	}

	public static String uploadToFacebook(String tipoAttachment, String url, String facebookToken) {

		StringBuffer urlUpload = new StringBuffer(facebookUri).append(attachmentsUri)
				.append(getParamAccesToken(facebookToken));

		MensajeParaFacebook message = new MensajeParaFacebook(null,
				new RichMessage(new Attachment(tipoAttachment, new MediaPayload(url))));
		Response response = ejecutarLlamadoFacebook(urlUpload.toString(), message);

		return GSON.fromJson(response.readEntity(String.class), AttachmentResponse.class).getAttachment_id();
	}

	private static Response ejecutarLlamadoFacebook(String urlFacebook, Object mensajeParaFacebook) {

		// Client client = ClientBuilder.newClient(new
		// ClientConfig().register(LoggingFilter.class));
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(urlFacebook);

		/*
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); String
		 * jsonParaFacebook = gson.toJson(mensajeParaFacebook);
		 */

		String jsonParaFacebook = GSON.toJson(mensajeParaFacebook); // ANterior

		if (log.isDebugEnabled()) {
			log.debug("\n-------------\nMensaje para facebook: \n{}\n------------\n", jsonParaFacebook);

		}

		System.out.println(jsonParaFacebook);

		return webTarget.request().post(Entity.entity(jsonParaFacebook, MediaType.APPLICATION_JSON));
	}
	
	private static Response ejecutarLlamadoFacebook(String urlFacebook, String jsonParaFacebook) {

		// Client client = ClientBuilder.newClient(new
		// ClientConfig().register(LoggingFilter.class));
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(urlFacebook);
 
		if (log.isDebugEnabled()) {
			log.debug("\n-------------\nMensaje para facebook: \n{}\n------------\n", jsonParaFacebook);

		}

		System.out.println(jsonParaFacebook);

		return webTarget.request().post(Entity.entity(jsonParaFacebook, MediaType.APPLICATION_JSON));
	}

	class AttachmentResponse {

		String attachment_id;

		public String getAttachment_id() {
			return attachment_id;
		}

		public void setAttachment_id(String attachment_id) {
			this.attachment_id = attachment_id;
		}
	}

	static class WhitelistedDomains {
		List<String> whitelisted_domains;

		public WhitelistedDomains(List<String> whitelisted_domains) {
			this.whitelisted_domains = whitelisted_domains;
		}

		public List<String> getWhitelisted_domains() {
			return whitelisted_domains;
		}
	}
	
	//************************************************DIALOG V2************************************************
	
	public static int postToFacebookV2(MensajeParaFacebookGeneralV2 mensajeParaFacebookV2, String facebookToken) {

		StringBuffer urlEnvioMensajes = new StringBuffer(facebookUri).append(mesagesUri)
				.append(getParamAccesToken(facebookToken));

		Response response = ejecutarLlamadoFacebook(urlEnvioMensajes.toString(), mensajeParaFacebookV2);

		System.out.println(response.getStringHeaders());
		System.out.println(response.readEntity(String.class));

		return response.getStatus();
	}

}
