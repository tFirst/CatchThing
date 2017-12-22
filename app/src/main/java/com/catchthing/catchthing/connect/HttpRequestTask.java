package com.catchthing.catchthing.connect;

import android.os.AsyncTask;
import android.util.Log;

import com.catchthing.catchthing.status.StateMain;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpRequestTask extends AsyncTask<Void, Void, StateMain> {

	private String url;

	public HttpRequestTask(String url) {
		this.url = url;
		System.out.println(this.url);
	}

	@Override
	protected StateMain doInBackground(Void... params) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			return restTemplate.getForObject(url, StateMain.class);
		} catch (Exception e) {
			Log.e("MainActivity", e.getMessage(), e);
		}

		return null;
	}
}
