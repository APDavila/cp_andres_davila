package com.holalola.cine.ejb.servicio;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.batch.runtime.BatchRuntime;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chilkatsoft.*;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.holalola.util.CodigoQR;

/**
 * @author Administrador
 *
 */
@ManagedBean
public final class ChilkatServicio {

	@EJB
	private static PropiedadesLola propiedadesLola;
	
	 
	private static final String PEM_LOGUIN = "login_and_register_public_key.pem";
	private static final String PEM_CONSULTA = "invocation_public_key.pem";
	private static final String PEM_PRIVADO = "confirmation_private_key.pem";
	private static final String PEM_PUBLICAPP = "public_for_app_movil.pem";
	private static final String ID_ACCESO = System.getProperty("lola.tokenAccCk");

	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	
	 
	
	public static String getIdAcceso() {
		return ID_ACCESO;
	}

	public static String getPemLoguin() {
		return PEM_LOGUIN;
	}
	
	public static String getPemPublicAPP() {
		return PEM_PUBLICAPP;
	}

	public static String getPemConsulta() {
		return PEM_CONSULTA;
	}

	public static String getPemPrivado() {
		return PEM_PRIVADO;
	}
	
	static {
		try {			
			System.loadLibrary("chilkat");
		} catch (Exception e) {
			logger.info("\n\n\n-------------------\n Error: \n-----------------------");
			System.err.println("Native code library failed to load.\n" + e);
		}
	}
	
	
	//public static void main(String argv[])
	public static void inicia()
	  {
	    CkCrypt2 crypt = new CkCrypt2();

	    boolean success = crypt.UnlockComponent("Anything for 30-day trial");
	    if (success != true) {
	        System.out.println(crypt.lastErrorText());
	        return;
	    }

	    //  Set properties for PBES1 encryption:
	    
	    String  password = "mySecretPassword";

	    crypt.put_CryptAlgorithm("pbes1");
	    crypt.put_PbesPassword(password);

	    //  Set the underlying PBE algorithm (and key length):
	    //  For PBES1, the underlying algorithm must be either
	    //  56-bit DES or 64-bit RC2
	    //  (this is according to the PKCS#5 specifications at
	    //  http://www.rsa.com/rsalabs/node.asp?id=2127   )
	    crypt.put_PbesAlgorithm("rc2");
	    crypt.put_KeyLength(64);

	    //  The salt for PBKDF1 is always 8 bytes:
	    crypt.SetEncodedSalt("0102030405060708","hex");

	    //  A higher iteration count makes the algorithm more
	    //  computationally expensive and therefore exhaustive
	    //  searches (for breaking the encryption) is more difficult:
	    crypt.put_IterationCount(1024);

	    //  A hash algorithm needs to be set for PBES1:
	    crypt.put_HashAlgorithm("sha1");

	    //  Indicate that the encrypted bytes should be returned
	    //  as a hex string:
	    crypt.put_EncodingMode("hex");

	    String plainText = "To be encrypted.";

	    String encryptedText = crypt.encryptStringENC(plainText);

	    System.out.println(encryptedText);

	    password = "mySecretPassword";
	    crypt.put_PbesPassword(password);
	    
	    //  Now decrypt:
	    String decryptedText = crypt.decryptStringENC(encryptedText);

	    System.out.println(decryptedText);

	  }
	
	public static String Encriptar(String texto) {
		 
        String secretKey = CodigoQR.getValidadorQR(); //llave para encriptar datos
        String base64EncryptedString = "";
 
        try {
 
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
 
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);
 
            byte[] plainTextBytes = texto.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            base64EncryptedString = new String(base64Bytes);
 
        } catch (Exception ex) {
        }
        return base64EncryptedString;
     }
	
	public static String Desencriptar(String textoEncriptado) throws Exception {
		 
        String secretKey = CodigoQR.getValidadorQR(); //llave para desenciptar datos
        String base64EncryptedString = "";
 
        try {
            byte[] message = Base64.decodeBase64(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
 
            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);
 
            byte[] plainText = decipher.doFinal(message);
 
            base64EncryptedString = new String(plainText, "UTF-8");
 
        } catch (Exception ex) {
        }
        return base64EncryptedString;
    }
	
	
	public static String Encriptar(String as_cadena, String as_keyPem) {
		try {
			
			CkPublicKey piblicKey = new CkPublicKey();

			boolean success = piblicKey.LoadOpenSslPemFile(propiedadesLola.certificados + "\\" + as_keyPem);// "\\login_and_register_public_key.pem");;

			if (success != true) {
				System.out.println(piblicKey.lastErrorText());
				return "";
			}
			
			CkRsa rsa = new CkRsa();

			success = rsa.UnlockComponent(ChilkatServicio.getIdAcceso());
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			String publicKeyXml = piblicKey.getXml();
			success = rsa.ImportPrivateKey(publicKeyXml);
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			
			rsa.put_EncodingMode("hex");

			boolean usePrivateKey = false;
			String encryptedStr = rsa.encryptStringENC(as_cadena, usePrivateKey);
			System.out.println(encryptedStr);

					
			return encryptedStr;

		} catch (Exception e) {
			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");
		}
		return "";
	}

	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (Exception e) {
			logger.info("\n--------\n " + e.getMessage() + " \n-----------------\n");
		}
		return "";
	}

	public static String Desencriptar(String as_cadena, String as_keyPem) {
		try {
			
			CkRsa rsaDecryptor = new CkRsa();

			CkPrivateKey privkey = new CkPrivateKey();

			boolean success = privkey.LoadEncryptedPem(propiedadesLola.certificados + "\\" + as_keyPem, "PAR_CHATHL");

			if (success != true) {
				return "";
			}

			String privateKey = privkey.getXml();

			rsaDecryptor.put_EncodingMode("hex");

			success = rsaDecryptor.ImportPrivateKey(privateKey);

			String decryptedStr = rsaDecryptor.decryptStringENC(as_cadena, true);

			System.out.println(decryptedStr);

			return decryptedStr;

		} catch (Exception e) {
			logger.info("\n--------\n " + e.getMessage() + " \n-----------------\n");
		}
		return "";
	}
}
