package com.deepoove.swagger.diff.boot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@Autowired
	VersionRepo repo;

	@GetMapping("/")
	public String showSignUpForm() {
		return "home";
	}

	@GetMapping("/versions")
	@ResponseBody
	public List<Version> getVersions() {
		return repo.findAll();
	}
}
