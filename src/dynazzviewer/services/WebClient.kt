package dynazzviewer.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

interface WebClient {
	fun requestData(uri : String) : String
}
