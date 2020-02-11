package com.deepoove.swagger.diff.boot;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.output.HtmlRender;

@Controller
public class HomeController {

	@Autowired
	VersionRepo repo;

	@PostMapping("/")
	public String homePost(@RequestBody final MultiValueMap<String, String> formData, final Model model) {
		Long app1 = Long.valueOf(formData.get("api1").get(0));
		Long app2 = Long.valueOf(formData.get("api2").get(0));
		SwaggerDiff diff = SwaggerDiff.compareV2(path(repo.getOne(app1).getHash()),
				path(repo.getOne(app2).getHash()));
		String html = new HtmlRender("Changelog", "demo.css").render(diff, model);
		return "home";
	}

	public static String path(final String name){
		return new File("").getAbsolutePath() + File.separator + "versions" + File.separator + name + "-swagger.json";

	}
	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/versions")
	@ResponseBody
	public List<Version> getVersions() {
		return repo.findAll();
	}
}
