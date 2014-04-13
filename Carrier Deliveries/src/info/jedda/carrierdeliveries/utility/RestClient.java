package info.jedda.carrierdeliveries.utility;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;

public interface RestClient {
	String doGet(final String url, final Header[] headers) throws HttpException, IOException,
			URISyntaxException;

	String doPatch(final String url, final HttpEntity entity, Header[] headers)
			throws URISyntaxException, HttpException, IOException;

	Header getAuthorizationHeader(String carrierRun, String distributorId);
}
