package com.catchthing.catchthing.connect;

import android.os.AsyncTask;
import android.util.Log;

import com.catchthing.catchthing.status.StateMain;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpRequestTask extends AsyncTask<Void, Void, StateMain> {

	// запрос на сервер
	private String url;

	// передача запроса на сервер
	@Override
	protected StateMain doInBackground(Void... params) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			return restTemplate.getForObject(url, StateMain.class);
		} catch (Exception e) {
			Log.e("HttpRequestTask", "Нет подключения к серверу", e);
            return StateMain.builder()
                    .status(HttpStatus.GATEWAY_TIMEOUT)
                    .build();
        }
	}
}
