package com.deepoove.swagger.diff.output;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.del;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.html;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import com.deepoove.swagger.diff.SwaggerDiff;
import com.deepoove.swagger.diff.model.ChangedEndpoint;
import com.deepoove.swagger.diff.model.ChangedOperation;
import com.deepoove.swagger.diff.model.ChangedParameter;
import com.deepoove.swagger.diff.model.ElProperty;
import com.deepoove.swagger.diff.model.Endpoint;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import j2html.tags.ContainerTag;

public class HtmlRender implements Render {

    private final String title;
    private final String linkCss;

    public HtmlRender() {
		this("Api Change Log", "http://deepoove.com/swagger-diff/stylesheets/demo.css");
    }

    public HtmlRender(final String title, final String linkCss) {
        this.title = title;
        this.linkCss = linkCss;
    }


	@Override
	public String render(final SwaggerDiff diff) {
		return render(diff, null);
    }
	public String render(final SwaggerDiff diff, final Model model) {
        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        ContainerTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        ContainerTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

        List<ChangedEndpoint> changedEndpoints = diff.getChangedEndpoints();
        ContainerTag ol_changed = ol_changed(changedEndpoints);

        ContainerTag p_versions = p_versions(diff.getOldVersion(), diff.getNewVersion());
		if (model != null) {
			model.addAttribute("newC", ol_newEndpoint.toString());
			model.addAttribute("missing", ol_missingEndpoint.toString());
			model.addAttribute("changed", ol_changed.toString());
			model.addAttribute("versions", p_versions.toString());
			return null;
		} else {
			return renderHtml(ol_newEndpoint, ol_missingEndpoint, ol_changed, p_versions);
		}
    }

    public String renderHtml(final ContainerTag ol_new, final ContainerTag ol_miss, final ContainerTag ol_changed, final ContainerTag p_versions) {
        ContainerTag html = html().attr("lang", "en").with(
            head().with(
                meta().withCharset("utf-8"),
                title(title),
                script(rawHtml("function showHide(id){if(document.getElementById(id).style.display==\'none\'){document.getElementById(id).style.display=\'block\';document.getElementById(\'btn_\'+id).innerHTML=\'&uArr;\';}else{document.getElementById(id).style.display=\'none\';document.getElementById(\'btn_\'+id).innerHTML=\'&dArr;\';}return true;}")).withType("text/javascript"),
                link().withRel("stylesheet").withHref(linkCss)
            ),
            body().with(
                header().with(h1(title)),
                div().withClass("article").with(
                    div_headArticle("Versions", "versions", p_versions),
                    div_headArticle("What's New", "new", ol_new),
                    div_headArticle("What's Deprecated", "deprecated", ol_miss),
                    div_headArticle("What's Changed", "changed", ol_changed)
                )
            )
        );

        return document().render() + html.render();
    }

    private ContainerTag div_headArticle(final String title, final String type, final ContainerTag ol) {
		return div().with(h2(title).with(a(rawHtml("&uArr;")).withId("btn_" + type).withClass("showhide").withHref("#").attr("onClick",
				"javascript:showHide('" + type + "');return false")), hr(), ol);
    }

    private ContainerTag p_versions(final String oldVersion, final String newVersion) {
        ContainerTag p = p().withId("versions");
        p.withText("Changes from " + oldVersion + " to " + newVersion + ".");
        return p;
    }

    private ContainerTag ol_newEndpoint(final List<Endpoint> endpoints) {
        if (null == endpoints) {
			return ol().withId("new");
		}
        ContainerTag ol = ol().withId("new");
        for (Endpoint endpoint : endpoints) {
            ol.with(li_newEndpoint(endpoint.getMethod().toString(),
					endpoint.getPathUrl(), endpoint.getSummary(), endpoint.getOperation()));
        }
        return ol;
    }

    private ContainerTag li_newEndpoint(final String method, final String path,
			final String desc, final Operation operation) {
		return li().with(
				addlinkToDoc(operation, path).with(span(method).withClass(method)).withText(path + " ")
						.with(span(null == desc ? "" : desc)));
    }

    private ContainerTag ol_missingEndpoint(final List<Endpoint> endpoints) {
        if (null == endpoints) {
			return ol().withId("deprecated");
		}
        ContainerTag ol = ol().withId("deprecated");
        for (Endpoint endpoint : endpoints) {
            ol.with(li_missingEndpoint(endpoint.getMethod().toString(),
                endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return ol;
    }

    private ContainerTag li_missingEndpoint(final String method, final String path,
                                            final String desc) {
        return li().with(span(method).withClass(method),
            del().withText(path)).with(span(null == desc ? "" : " " + desc));
    }

	private ContainerTag div_expandable(final String title, final String type) {
		return span().with(a(rawHtml("&dArr;")).withId("btn_" + type).withClass("showhide").withHref("#").attr("onClick",
				"javascript:showHide('" + type + "');return false"));
	}
    private ContainerTag ol_changed(final List<ChangedEndpoint> changedEndpoints) {
        if (null == changedEndpoints) {
			return ol().withId("changed");
		}
		ContainerTag ol = ol().withId("changed");
        for (ChangedEndpoint changedEndpoint : changedEndpoints) {
            String pathUrl = changedEndpoint.getPathUrl();
            Map<HttpMethod, ChangedOperation> changedOperations = changedEndpoint.getChangedOperations();
            for (Entry<HttpMethod, ChangedOperation> entry : changedOperations.entrySet()) {
                String method = entry.getKey().toString();
                ChangedOperation changedOperation = entry.getValue();
                String desc = changedOperation.getSummary();

				ContainerTag ul_detail = ul().withClass("detail");
                if (changedOperation.isDiffParam()) {
					ul_detail.with(li().with(h3("Parameter")).with(ul_param(changedOperation)));
                }
                if (changedOperation.isDiffProp()) {
                    ul_detail.with(li().with(h3("Return Type")).with(ul_response(changedOperation)));
                }
				ol.with(li().with(span(method).withClass(method)).with(div_expandable("a", pathUrl)).with(
						addlinkToDoc(changedOperation.getOperation(), pathUrl).withText(pathUrl + " ")
								.with(span(null == desc ? "" : desc)))

						.with(ul_detail.withId(pathUrl).withStyle("display: none;")));

            }
        }
        return ol;
    }

	private ContainerTag addlinkToDoc(final Operation operation, final String path) {
		return a().withHref("#" + operation.getTags().get(0) + "$" + path).withTarget("_blank");
	}

    private ContainerTag ul_response(final ChangedOperation changedOperation) {
        List<ElProperty> addProps = changedOperation.getAddProps();
        List<ElProperty> delProps = changedOperation.getMissingProps();
        ContainerTag ul = ul().withClass("change response");
        for (ElProperty prop : addProps) {
			ul.with(li_addProp(prop, null));
        }
        for (ElProperty prop : delProps) {
			ul.with(li_missingProp(prop, null));
        }
		addChangedProperty(changedOperation.getChangedProps(), ul);
        return ul;
    }

	private ContainerTag li_missingProp(final ElProperty prop, final String root) {
		if (root != null && prop.getEl().startsWith(root)) {
			prop.setEl(prop.getEl().replaceFirst(root + "\\.", ""));
		}
        Property property = prop.getProperty();
        return li().withClass("missing").withText("Delete").with(del(prop.getEl())).with(span(null == property.getDescription() ? "" : ("//" + property.getDescription())).withClass("comment"));
    }

	private ContainerTag li_addProp(final ElProperty prop, final String root) {
		if (root != null && prop.getEl().startsWith(root)) {
			prop.setEl(prop.getEl().replaceFirst(root + "\\.", ""));
		}

        Property property = prop.getProperty();
		return li().withText("Add ")
				.with(span().withText((prop.getUProperty().getRequired() ? "required" : "not required"))
						.withClass(prop.getUProperty().getRequired() ? "missing" : ""))
				.withText(" " + prop.getEl())
				.with(span(null == property.getDescription() ? "" : (" //" + property.getDescription())).withClass("comment"));
    }

	private ContainerTag ul_param(final ChangedOperation changedOperation) {
        List<Parameter> addParameters = changedOperation.getAddParameters();
        List<Parameter> delParameters = changedOperation.getMissingParameters();
        List<ChangedParameter> changedParameters = changedOperation.getChangedParameter();
		ContainerTag ul = ul().withClass("change param");
        for (Parameter param : addParameters) {
			ul.with(li_addParam(param));
        }
        for (ChangedParameter param : changedParameters) {
            List<ElProperty> increased = param.getIncreased();
            for (ElProperty prop : increased) {
				ul.with(li_addProp(prop, param.getLeftParameter().getName()));
            }
        }
        for (ChangedParameter param : changedParameters) {
            boolean changeRequired = param.isChangeRequired();
            boolean changeDescription = param.isChangeDescription();
            if (changeRequired || changeDescription) {
				ul.with(li_changedParam(param));
			}
			addChangedProperty(changedOperation.getChangedProps(), ul);
			addChangedProperty(param.getChanged(), ul);
        }
        for (ChangedParameter param : changedParameters) {
            List<ElProperty> missing = param.getMissing();
            for (ElProperty prop : missing) {
				ul.with(li_missingProp(prop, param.getLeftParameter().getName()));
            }
        }
        for (Parameter param : delParameters) {
            ul.with(li_missingParam(param));
        }
        return ul;
    }

    private ContainerTag li_addParam(final Parameter param) {
		return li().withText("Add " + param.getName())
				.with(span(null == param.getDescription() ? "" : (" //" + param.getDescription())).withClass("comment"));
    }

	private ContainerTag li_missingParam(final Parameter param) {
		return li().withClass("missing").with(span("Delete")).with(del(param.getName()))
				.with(span(null == param.getDescription() ? "" : (" //" + param.getDescription())).withClass("comment"));
    }

	private ContainerTag li_changedParam(final ChangedParameter changeParam) {
        boolean changeRequired = changeParam.isChangeRequired();
        boolean changeDescription = changeParam.isChangeDescription();
        Parameter rightParam = changeParam.getRightParameter();
        Parameter leftParam = changeParam.getLeftParameter();
		ContainerTag li = li().withText(rightParam.getName());
        if (changeRequired) {
            li.withText(" change into " + (rightParam.getRequired() ? "required" : "not required"));
        }
        if (changeDescription) {
            li.withText(" Notes ").with(del(leftParam.getDescription()).withClass("comment")).withText(" change into ").with(span(span(null == rightParam.getDescription() ? "" : rightParam.getDescription()).withClass("comment")));
        }
        return li;
    }

	private void addChangedProperty(final List<ElProperty> changedProps, final ContainerTag ul) {
		for (ElProperty elProperty : changedProps) {
			boolean changeRequiredI = elProperty.getProperty().getRequired() != elProperty.getRightProperty().getRequired();
			boolean changeDescriptionI =
					!StringUtils.equals(elProperty.getProperty().getDescription(), elProperty.getRightProperty().getDescription());
			boolean changeSizesI = false;
			if (elProperty.getProperty() instanceof StringProperty) {
				changeSizesI = maxChanged(elProperty);
			}
			if (changeRequiredI || changeDescriptionI || changeSizesI) {
				ContainerTag nestedUl = ul().withClass("nested");
				String text = elProperty.getEl();
				if (text.startsWith("body.")) {
					text = text.substring(5);
				}
				ContainerTag li = li().withText(text).with(nestedUl);
				ul.with(li);

				nestedUl.with(
						li().withText(" change into ")
								.with(span().withClass(elProperty.getUProperty().getRequired() ? "missing" : "")
										.withText((elProperty.getUProperty().getRequired() ? "required" : "not required")
						)));

				if (changeDescriptionI) {
					nestedUl.with(li().withText("notes ").with(del(elProperty.getProperty().getDescription()))
							.withText(" change into ")
							.with(span(span((null == elProperty.getRightProperty().getDescription() ? ""
									: elProperty.getRightProperty().getDescription()))).withClass("comment"))
					)
					;
				}
				if (changeSizesI) {
					Integer maxLength = ((StringProperty) elProperty.getProperty()).getMaxLength();
					nestedUl.with(li().withText("maxLength ").with(del(maxLength + ""))
							.withText(" change into ")
							.with(span().withText(((StringProperty) elProperty.getRightProperty()).getMaxLength() + "")
									.withClass("comment")));
				}
			}
		}
	}

	private boolean maxChanged(final ElProperty elProperty) {
		Integer maximum = ((StringProperty) elProperty.getProperty()).getMaxLength();
		Integer maximum2 = ((StringProperty) elProperty.getUProperty()).getMaxLength();
		if (maximum == null && maximum2 != null) {
			return true;
		}
		if (maximum == null && maximum2 == null) {
			return false;
		}
		return !maximum.equals(maximum2);
	}



}
