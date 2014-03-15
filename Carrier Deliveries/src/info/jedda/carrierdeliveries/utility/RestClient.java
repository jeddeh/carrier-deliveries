package info.jedda.carrierdeliveries.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

/**
 * REST Client for Apache HTTPClient API. Adapted from
 * {@link dyutiman.wordpress.com/2011/04/13/rest-template-using-apache-httpclient/}.
 * The HttpClient used in this class is not thread-safe.
 */
public class RestClient {
	private static final String SERVER_URL = "http://www.jedda.info";
	private static final HttpClient httpClient = new DefaultHttpClient();

	public static String doGet(final String url) throws HttpException, IOException,
			URISyntaxException {

		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
		HttpGet httpget = new HttpGet(SERVER_URL + url);
		HttpResponse response = httpClient.execute(httpget);

		switch (response.getStatusLine().getStatusCode()) {
		case 200:
			InputStream instream = response.getEntity().getContent();
			return read(instream);
		default:
			throw new HttpException(response.getStatusLine().toString());
		}
	}

	public static String doPatch(final String url, final HttpEntity entity)
			throws URISyntaxException, HttpException, IOException {

		final HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);

		HttpPatch httpPatch = new HttpPatch(SERVER_URL + url);
		httpPatch.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPatch);
		
		switch (response.getStatusLine().getStatusCode()) {
		case 204:
			return response.getStatusLine().toString();
		default:
			throw new HttpException(response.getStatusLine().toString());
		}
	}

	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
}