package com.allterco.jjv;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * Hello world!
 *
 */

public class App 
{
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	static PublicKey GetAlltercoPubKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException  
	{
		StringReader src=new StringReader(
			"-----BEGIN PUBLIC KEY-----\n"+
			"MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE3Kx+6C/0ZbnelYUgucUo4/X4xt1NCmEL\n"+
			"coyLpgkuLHume4VLZnQjtXeYgzr2FUdsO/ip8SzssSu3CEU9ArvB+yGIlW7l1yLt\n"+
			"wHVs/2zXrL0riL++7jdoQCpTGanFVzpM\n"+
			"-----END PUBLIC KEY-----\n"
		);
		PemReader pr=new PemReader(src);
		PemObject o = pr.readPemObject();
		byte[] content = o.getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		KeyFactory factory = KeyFactory.getInstance("ECDSA");
		return factory.generatePublic(pubKeySpec);

	}

	static JWTVerifier getES384Verifier(ECPublicKey pubkey) {
		return JWT.require(Algorithm.ECDSA384(pubkey,null))
			.acceptLeeway(5)   //sec for nbf and iat
			.acceptExpiresAt(30) //30 secs for exp
			.build();

	}
	public static void main( String[] args ) 
	{
		if (args.length==0) {
			System.out.println("a JWT to check must be provided as parameter");
			System.exit(-1);
		} 

		String toCheck = args[0];
		System.out.println("Must check "+toCheck);

		PublicKey pubkey=null;
		try {
			pubkey = GetAlltercoPubKey();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			System.out.println("Failed to transfigure Allterco public key!");
			System.exit(-1);
		}
		if (! (pubkey instanceof ECPublicKey)) {
			System.out.println("Obtained Allterco public key is not a ECPublicKey?!");
			System.exit(-1);
		}

		JWTVerifier verify = getES384Verifier((ECPublicKey)pubkey);
		DecodedJWT decoded=null;
		try {
			decoded = verify.verify(toCheck);
		} catch (Throwable e) {
			System.out.println("failed to verify! e:"+e);
			System.exit(-1);
		}

		System.out.println("Verified: "+decoded.getClaims());

	}
}
