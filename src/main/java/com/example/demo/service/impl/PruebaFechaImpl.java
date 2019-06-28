package com.example.demo.service.impl;

import com.example.demo.constantes.Constantes;
import com.example.demo.dto.FechaDTO;
import com.example.demo.exception.ErrorException;
import com.example.demo.service.PruebaFechaService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class PruebaFechaImpl implements PruebaFechaService {

	RestTemplate restTemplate = new RestTemplate();

	static final Random random = new Random();

	@Value("${url}")
	private String url;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public FechaDTO fechasRestantes() {
		FechaDTO fechas = new FechaDTO();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, entity);

			fechas = objectMapper.readValue(response.getBody(), FechaDTO.class);

			List<LocalDate> responseList = setFechasFaltantes(fechas);

			fechas.setFechasFaltantes(responseList);

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new ErrorException(Constantes.CODERR, Constantes.INVALIDO);
			}
			throw new ErrorException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), Constantes.ERROR_500);
		} catch (JsonParseException e) {
			throw new ErrorException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), Constantes.ERROR_500);
		} catch (JsonMappingException e) {
			throw new ErrorException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), Constantes.ERROR_500);
		} catch (IOException e) {
			throw new ErrorException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), Constantes.ERROR_500);
		}
		return fechas;
	}

	private List<LocalDate> setFechasFaltantes(FechaDTO request) {
		Set<LocalDate> response = new HashSet<>();

		ChronoLocalDate from = ChronoLocalDate.from(request.getFechaCreacion());
		ChronoLocalDate to = ChronoLocalDate.from(request.getFechaFin());
		ChronoPeriod period = ChronoPeriod.between(from, to);

		long meses = period.get(ChronoUnit.MONTHS) + (period.get(ChronoUnit.YEARS) * 12);

		long cantidadRegistrosFaltantes = meses - request.getFechas().size();

		while (response.size() <= cantidadRegistrosFaltantes) {

			int minDay = (int) request.getFechaCreacion().toEpochDay();
			int maxDay = (int) request.getFechaFin().toEpochDay();
			long randomDay = minDay + random.nextInt(maxDay - minDay);

			LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
			randomDate = randomDate.withDayOfMonth(1);

			if (!request.getFechas().contains(randomDate)) {response.add(randomDate);}

			if ((response.size() >= cantidadRegistrosFaltantes)) {break;}
		}
		return response.stream().sorted().collect(Collectors.toList());
	}
}
