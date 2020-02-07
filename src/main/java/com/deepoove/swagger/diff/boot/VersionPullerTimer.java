
package com.deepoove.swagger.diff.boot;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;

@Component
public class VersionPullerTimer {

	@Autowired
	VersionRepo repo;

	@Scheduled(cron = "0 0 * * * *")
	public void run() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("mirek", "mirek"));
		//to disable ssl hostname verifier
		restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
		   @Override
		    protected void prepareConnection(final HttpURLConnection connection, final String httpMethod) throws IOException {
		        if (connection instanceof HttpsURLConnection) {
		            ((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
		        }
		        super.prepareConnection(connection, httpMethod);
		    }
		});

		ResponseEntity<Map> forEntity =
				restTemplate.getForEntity("https://test.verto.streamsoft.pl/next-app/services/rest/swagger.json", Map.class);
		Map body = forEntity.getBody();

		String object = (String) ((Map) body.get("info")).get("version");
		String[] split = object.split(" ");
		List<String> verisons = new LinkedList<>(Arrays.asList(split));
		Collections.sort(verisons);
		String name = Joiner.on(",").join(verisons);
		Version findByName = repo.findByName(name);
		if (findByName == null) {
			String hash = save(body.toString());
			Version version = new Version();
			version.setName(name);
			version.setHash(hash);
			repo.save(version);
		}
	}

	public static String save(final String content) throws Exception {
		String encryptedString = DigestUtils.md5Hex(content);
		FileUtils.writeStringToFile(
				new File(new File("").getAbsolutePath() + File.separator + "versions" + File.separator + encryptedString + "-swagger.json"),
				content);
		return encryptedString;
	}
}
