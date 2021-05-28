package com.ursa.tools.amazon.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ursa.tools.amazon.model.LineNotify;
import com.ursa.tools.amazon.payload.LineStatusResponse;
import com.ursa.tools.amazon.payload.LineTokenRequest;
import com.ursa.tools.amazon.payload.LineTokenResponse;
import com.ursa.tools.amazon.repository.LineNotifyRepository;
import com.ursa.tools.amazon.util.ParameterStringBuilder;

@Service
public class LineNotifyServiceImpl implements LineNotifyService{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	LineNotifyRepository lineNotifyRepository;

	@Override
	public Optional<LineNotify> findByCreatedBy(Integer createdBy) {
		return lineNotifyRepository.findByCreatedBy(createdBy);
	}

	@Override
	@Transactional
	public LineNotify save(LineNotify lineNotify) {
		return lineNotifyRepository.save(lineNotify);
	}

	@Override
	public void sendNotify(String message) {
		List<LineNotify> list = lineNotifyRepository.findAll();
		if(list.size() > 0) {			
			try {
				LineNotify line = list.get(0);
			    String token = line.getToken();
				String api = "https://notify-api.line.me/api/notify";
				URL url = new URL(api);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("Authorization", "Bearer " + token);
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				con.setDoOutput(true);
				
				OutputStream os = con.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");

                writer.write( "message=" + message );
                writer.flush();
                writer.close();
                os.close();
				
				int status = con.getResponseCode();
				if(status == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					in.close();
				}
				con.disconnect();
				logger.info("Line notify success");
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error("Line notify error: " + e.getMessage());
			}
		}
	}

	@Override
	public LineTokenResponse getAccessToken(LineTokenRequest request) {
		LineTokenResponse response = null;
		try {
			String api = "https://notify-bot.line.me/oauth/token";
			URL url = new URL(api);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setDoOutput(true);
			
			OutputStream os = con.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");

            Map<String, String> parameters = new HashMap<>();
			parameters.put("code", request.getCode());
			parameters.put("client_id", request.getClientId());
			parameters.put("client_secret", request.getClientSecret());
			parameters.put("grant_type", request.getGrantType());
			parameters.put("redirect_uri", request.getRedirectUri());
			String data = ParameterStringBuilder.getParamsString(parameters);
			
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();
			
			int status = con.getResponseCode();
			if(status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content.toString(), LineTokenResponse.class);
			}
			con.disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Line notify error: " + e.getMessage());			
		}
		return response;
	}

	@Override
	public LineStatusResponse getStatus(String token) {
		LineStatusResponse response = null;
		try {
			String api = "https://notify-api.line.me/api/status";
			URL url = new URL(api);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", "Bearer " + token);
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setDoOutput(true);
			
			int status = con.getResponseCode();
			if(status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content.toString(), LineStatusResponse.class);
			}
			con.disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Get line status error: " + e.getMessage());			
		}
		return response;
	}

}
