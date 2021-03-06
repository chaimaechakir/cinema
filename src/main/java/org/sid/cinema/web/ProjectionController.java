package org.sid.cinema.web;


import java.util.List;

import javax.validation.Valid;

import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.ProjectionRepository;
import org.sid.cinema.dao.SalleRepository;
import org.sid.cinema.dao.SeanceRepository;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Projection;
import org.sid.cinema.entities.Salle;
import org.sid.cinema.entities.Seance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectionController {
	@Autowired
	ProjectionRepository projectionRepository;
	@Autowired
	FilmRepository filmRepository;
	@Autowired
	SalleRepository salleRepository;
	@Autowired
	SeanceRepository seanceRepository;

	@GetMapping(path = "/projection")
	public String projection(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Page<Projection> pageprojections = projectionRepository.findByFilmTitreContains(keyword,
				PageRequest.of(page, size));
		model.addAttribute("pageprojections", pageprojections);
		model.addAttribute("currentpage", page);
		model.addAttribute("keyword", keyword);
		model.addAttribute("size", size);
		model.addAttribute("pages", new int[pageprojections.getTotalPages()]);
		return "projection";
	}

	@GetMapping(path = "/supprimerProjection")
	public String supprimerProjection(Long id, String keyword, int page, int size, Model model) {
		Projection p = projectionRepository.findById(id).get();
		model.addAttribute("id_courant", id);
		if (p.getTickets().isEmpty()) {
			projectionRepository.deleteById(id);
			model.addAttribute("modeSup", "Autorise");
		} else {
			model.addAttribute("modeSup", "nonAutorise");
		}
		return projection(model, page, size, keyword);
	}

	@GetMapping(path = "/formProjection")
	public String formProjection(Model model) {
		List<Film> films = filmRepository.findAll();
		List<Salle> salles = salleRepository.findAll();
		List<Seance> seances = seanceRepository.findAll();
		model.addAttribute("films", films);
		model.addAttribute("seances", seances);
		model.addAttribute("salles", salles);
		model.addAttribute("projection", new Projection());
		model.addAttribute("mode", "nouveau");
		return "formProjection";
	}

	@PostMapping(path = "/enregistrerProjection")
	public String enregistrerCinema(@Valid Projection p, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors())
			return "FormProjection";
		projectionRepository.save(p);
		model.addAttribute("projection", p);
		return "validationProjection";
	}

	@GetMapping(path = "/modifierProjection")
	public String modifierCinema(Model model, Long id) {
		List<Film> films = filmRepository.findAll();
		List<Salle> salles = salleRepository.findAll();
		List<Seance> seances = seanceRepository.findAll();
		model.addAttribute("films", films);
		model.addAttribute("seances", seances);
		model.addAttribute("salles", salles);
		Projection p = projectionRepository.findById(id).get();
		model.addAttribute("projection", p);
		model.addAttribute("mode", "modifier");
		return "formProjection";
	}
}

