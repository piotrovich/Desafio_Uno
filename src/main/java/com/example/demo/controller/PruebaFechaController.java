package com.example.demo.controller;

import com.example.demo.dto.FechaDTO;
import com.example.demo.service.impl.PruebaFechaImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fec")
public class PruebaFechaController {

	@Autowired
	private PruebaFechaImpl service;

	@GetMapping("fechas/faltantes")
	public FechaDTO fechaRestante() {
		return service.fechasRestantes();
	}
}
